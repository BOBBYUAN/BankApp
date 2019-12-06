package cs174a;// You may have as many imports as you need.
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.sql.Time;
import java.util.Properties;
import java.util.Scanner;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.util.Calendar;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
import java.util.Date;
import java.io.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.LocalDate;

/**
 * The most important class for your application.
 * DO NOT CHANGE ITS SIGNATURE.
 */
public class App implements Testable
{
	private OracleConnection _connection;                   // Example connection object to your DB.
	static String salt = "glennie-rousseva";            // for password encryption
	static String customerTaxID = "";


	/**
	 * Default constructor.
	 * DO NOT REMOVE.
	 */
	App()
	{
		// TODO: Any actions you need.
	}

	/**
	 * This is an example access operation to the DB.
	 */
	void exampleAccessToDB()
	{
		// Statement and ResultSet are AutoCloseable and closed automatically.
		try( Statement statement = _connection.createStatement() )
		{
			try( ResultSet resultSet = statement.executeQuery( "select owner, table_name from all_tables" ) )
			{
				while( resultSet.next() )
					System.out.println( resultSet.getString( 1 ) + " " + resultSet.getString( 2 ) + " " );
			}
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
		}
	}

	////////////////////////////// Implement all of the methods given in the interface /////////////////////////////////
	// Check the Testable.java interface for the function signatures and descriptions.

	@Override
	public String initializeSystem()
	{
		// Some constants to connect to your DB.
		final String DB_URL = "jdbc:oracle:thin:@cs174a.cs.ucsb.edu:1521/orcl";
		final String DB_USER = "c##wangcheng";
		final String DB_PASSWORD = "7429699";

		// Initialize your system.  Probably setting up the DB connection.
		Properties info = new Properties();
		info.put( OracleConnection.CONNECTION_PROPERTY_USER_NAME, DB_USER );
		info.put( OracleConnection.CONNECTION_PROPERTY_PASSWORD, DB_PASSWORD );
		info.put( OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "20" );

		try
		{
			OracleDataSource ods = new OracleDataSource();
			ods.setURL( DB_URL );
			ods.setConnectionProperties( info );
			_connection = (OracleConnection) ods.getConnection();

			// Get the JDBC driver name and version.
			DatabaseMetaData dbmd = _connection.getMetaData();
			System.out.println( "Driver Name: " + dbmd.getDriverName() );
			System.out.println( "Driver Version: " + dbmd.getDriverVersion() );

			// Print some connection properties.
			System.out.println( "Default Row Prefetch Value is: " + _connection.getDefaultRowPrefetch() );
			System.out.println( "Database Username is: " + _connection.getUserName() );
			System.out.println();


			return "0";
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

	}

	/**
	 * Example of one of the testable functions.
	 */
	@Override
	public String listClosedAccounts()
	{

		StringBuilder sb = new StringBuilder();
		try( Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) ) {


			try( ResultSet resultSet = statement.executeQuery( "select account.aid from account where account.status = 1" ) )
			{
				while( resultSet.next() ) {
					sb.append(resultSet.getInt("aid"));
					sb.append(" ");
				}
			}

			return "0 " + sb.toString();

		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	/**
	 * Another example.
	 */
	@Override
	public String createCheckingSavingsAccount( AccountType accountType, String id, double initialBalance, String tin, String name, String address )
	{

		String result;
		try( Statement statement = _connection.createStatement() )
		{

			if (accountType == AccountType.INTEREST_CHECKING) {
//				String sql1 = "insert into customer (cid,cname,address,pin) values (?,?,?,?)";
//				PreparedStatement preparedStatement = _connection.prepareStatement(sql1);
//				preparedStatement.setString(1,tin);
//				preparedStatement.setString(2, name);
//				preparedStatement.setString(3, address);
//				preparedStatement.setString(4, encryptPin("1717"));
//				preparedStatement.executeQuery();


				String sql2 = "insert into account (aid,cid,branch_name,balance,type,interest,status) values (?,?,?,?,?,?,?)";
				PreparedStatement preparedStatement = _connection.prepareStatement(sql2);
				preparedStatement.setInt(1,Integer.parseInt(id));
				preparedStatement.setString(2, tin);
				preparedStatement.setString(3, "BOA");
				preparedStatement.setDouble(4, initialBalance);
				preparedStatement.setString(5, "INTEREST_CHECKING");
				preparedStatement.setFloat(6, 0.03f);
				preparedStatement.setInt(7, 0);
				preparedStatement.executeQuery();

				addPrimary(tin, id);


				result =  "0 " + id + " INTEREST_CHECKING " + initialBalance + " " + tin;

			} else if (accountType == AccountType.STUDENT_CHECKING) {
//				String sql1 = "insert into customer (cid,cname,address,pin) values (?,?,?,?)";
//				PreparedStatement preparedStatement = _connection.prepareStatement(sql1);
//				preparedStatement.setString(1,tin);
//				preparedStatement.setString(2, name);
//				preparedStatement.setString(3, address);
//				preparedStatement.setString(4, encryptPin("1717"));
//				preparedStatement.executeQuery();

				String sql2 = "insert into account (aid, cid, branch_name, balance, type, interest) values (?,?,?,?,?,?)";
				PreparedStatement preparedStatement = _connection.prepareStatement(sql2);
				preparedStatement.setInt(1,Integer.parseInt(id));
				preparedStatement.setString(2, tin);
				preparedStatement.setString(3, "BOA");
				preparedStatement.setDouble(4, initialBalance);
				preparedStatement.setString(5, "STUDENT_CHECKING");
				preparedStatement.setFloat(6, 0.00f);
				preparedStatement.executeQuery();

				addPrimary(tin, id);

				result =  "0 " + id + " STUDENT_CHECKING " + initialBalance + " " + tin;

			} else if (accountType == AccountType.SAVINGS) {
//				String sql1 = "insert into customer (cid,cname,address,pin) values (?,?,?,?)";
//				PreparedStatement preparedStatement = _connection.prepareStatement(sql1);
//				preparedStatement.setString(1,tin);
//				preparedStatement.setString(2, name);
//				preparedStatement.setString(3, address);
//				preparedStatement.setString(4, encryptPin("1717"));
//				preparedStatement.executeQuery();

				String sql2 = "insert into account (aid, cid, branch_name, balance, type, interest) values (?,?,?,?,?,?)";
				PreparedStatement preparedStatement = _connection.prepareStatement(sql2);
				preparedStatement.setInt(1,Integer.parseInt(id));
				preparedStatement.setString(2, tin);
				preparedStatement.setString(3, "BOA");
				preparedStatement.setDouble(4, initialBalance);
				preparedStatement.setString(5, "SAVINGS");
				preparedStatement.setFloat(6, 0.048f);
				preparedStatement.executeQuery();

				addPrimary(tin, id);

				result =  "0 " + id + " SAVINGS " + initialBalance + " " + tin;
				//result = "successful";

			} else {
				result = "1";
			}
		}
		catch( SQLException e )
		{
			result = "1";
			System.err.println( e.getMessage() );
		}

		return result;
		//return "0 " + id + " " + accountType + " " + initialBalance + " " + tin;
	}

	public String createCheckingSavingsAccount2( AccountType accountType, String id, double initialBalance, String tin, String name, String address )
	{

		String result;
		try( Statement statement = _connection.createStatement() )
		{

			if (accountType == AccountType.INTEREST_CHECKING) {
				String sql1 = "insert into customer (cid,cname,address,pin) values (?,?,?,?)";
				PreparedStatement preparedStatement = _connection.prepareStatement(sql1);
				preparedStatement.setString(1,tin);
				preparedStatement.setString(2, name);
				preparedStatement.setString(3, address);
				preparedStatement.setString(4, encryptPin("1717"));
				preparedStatement.executeQuery();


				String sql2 = "insert into account (aid,cid,branch_name,balance,type,interest,status) values (?,?,?,?,?,?,?)";
				preparedStatement = _connection.prepareStatement(sql2);
				preparedStatement.setInt(1,Integer.parseInt(id));
				preparedStatement.setString(2, tin);
				preparedStatement.setString(3, "BOA");
				preparedStatement.setDouble(4, initialBalance);
				preparedStatement.setString(5, "INTEREST_CHECKING");
				preparedStatement.setFloat(6, 0.03f);
				preparedStatement.setInt(7, 0);
				preparedStatement.executeQuery();

				addPrimary(tin, id);


				result =  "0 " + id + " INTEREST_CHECKING " + initialBalance + " " + tin;

			} else if (accountType == AccountType.STUDENT_CHECKING) {
				String sql1 = "insert into customer (cid,cname,address,pin) values (?,?,?,?)";
				PreparedStatement preparedStatement = _connection.prepareStatement(sql1);
				preparedStatement.setString(1,tin);
				preparedStatement.setString(2, name);
				preparedStatement.setString(3, address);
				preparedStatement.setString(4, encryptPin("1717"));
				preparedStatement.executeQuery();

				String sql2 = "insert into account (aid, cid, branch_name, balance, type, interest) values (?,?,?,?,?,?)";
				preparedStatement = _connection.prepareStatement(sql2);
				preparedStatement.setInt(1,Integer.parseInt(id));
				preparedStatement.setString(2, tin);
				preparedStatement.setString(3, "BOA");
				preparedStatement.setDouble(4, initialBalance);
				preparedStatement.setString(5, "STUDENT_CHECKING");
				preparedStatement.setFloat(6, 0.00f);
				preparedStatement.executeQuery();

				addPrimary(tin, id);

				result =  "0 " + id + " STUDENT_CHECKING " + initialBalance + " " + tin;

			} else if (accountType == AccountType.SAVINGS) {
				String sql1 = "insert into customer (cid,cname,address,pin) values (?,?,?,?)";
				PreparedStatement preparedStatement = _connection.prepareStatement(sql1);
				preparedStatement.setString(1,tin);
				preparedStatement.setString(2, name);
				preparedStatement.setString(3, address);
				preparedStatement.setString(4, encryptPin("1717"));
				preparedStatement.executeQuery();

				String sql2 = "insert into account (aid, cid, branch_name, balance, type, interest) values (?,?,?,?,?,?)";
				preparedStatement = _connection.prepareStatement(sql2);
				preparedStatement.setInt(1,Integer.parseInt(id));
				preparedStatement.setString(2, tin);
				preparedStatement.setString(3, "BOA");
				preparedStatement.setDouble(4, initialBalance);
				preparedStatement.setString(5, "SAVINGS");
				preparedStatement.setFloat(6, 0.048f);
				preparedStatement.executeQuery();

				addPrimary(tin, id);

				result =  "0 " + id + " SAVINGS " + initialBalance + " " + tin;
				//result = "successful";

			} else {
				result = "1";
			}
		}
		catch( SQLException e )
		{
			result = "1";
			System.err.println( e.getMessage() );
		}


		return result;
		//return "0 " + id + " " + accountType + " " + initialBalance + " " + tin;
	}


	public String createCheckingSavingsAccount3( AccountType accountType, String id, double initialBalance, String tin, String name, String address )
	{

		String result;
		try( Statement statement = _connection.createStatement() )
		{



				String sql3 = "select * from Account A where A.cid = ?";
				PreparedStatement preparedStatement = _connection.prepareStatement(sql3);
				preparedStatement.setString(1,tin);
				ResultSet resultset = preparedStatement.executeQuery();

				if (resultset.next()) {   // return customer

					if (accountType == AccountType.INTEREST_CHECKING) {

						String sql2 = "insert into account (aid,cid,branch_name,balance,type,interest,status) values (?,?,?,?,?,?,?)";
						preparedStatement = _connection.prepareStatement(sql2);
						preparedStatement.setInt(1,Integer.parseInt(id));
						preparedStatement.setString(2, tin);
						preparedStatement.setString(3, "BOA");
						preparedStatement.setDouble(4, initialBalance);
						preparedStatement.setString(5, "INTEREST_CHECKING");
						preparedStatement.setFloat(6, 0.03f);
						preparedStatement.setInt(7, 0);
						preparedStatement.executeQuery();

						addPrimary(tin, id);


						result =  "0 " + id + " INTEREST_CHECKING " + initialBalance + " " + tin;

					} else if (accountType == AccountType.STUDENT_CHECKING) {

						String sql2 = "insert into account (aid, cid, branch_name, balance, type, interest) values (?,?,?,?,?,?)";
						preparedStatement = _connection.prepareStatement(sql2);
						preparedStatement.setInt(1,Integer.parseInt(id));
						preparedStatement.setString(2, tin);
						preparedStatement.setString(3, "BOA");
						preparedStatement.setDouble(4, initialBalance);
						preparedStatement.setString(5, "STUDENT_CHECKING");
						preparedStatement.setFloat(6, 0.00f);
						preparedStatement.executeQuery();

						addPrimary(tin, id);

						result =  "0 " + id + " STUDENT_CHECKING " + initialBalance + " " + tin;

					} else if (accountType == AccountType.SAVINGS) {

						String sql2 = "insert into account (aid, cid, branch_name, balance, type, interest) values (?,?,?,?,?,?)";
						preparedStatement = _connection.prepareStatement(sql2);
						preparedStatement.setInt(1,Integer.parseInt(id));
						preparedStatement.setString(2, tin);
						preparedStatement.setString(3, "BOA");
						preparedStatement.setDouble(4, initialBalance);
						preparedStatement.setString(5, "SAVINGS");
						preparedStatement.setFloat(6, 0.048f);
						preparedStatement.executeQuery();

						addPrimary(tin, id);

						result =  "0 " + id + " SAVINGS " + initialBalance + " " + tin;

					} else {
						result = "1";
					}

				} else {     // new customer

					if (accountType == AccountType.INTEREST_CHECKING) {
						String sql1 = "insert into customer (cid,cname,address,pin) values (?,?,?,?)";
						preparedStatement = _connection.prepareStatement(sql1);
						preparedStatement.setString(1,tin);
						preparedStatement.setString(2, name);
						preparedStatement.setString(3, address);
						preparedStatement.setString(4, encryptPin("1717"));
						preparedStatement.executeQuery();


						String sql2 = "insert into account (aid,cid,branch_name,balance,type,interest,status) values (?,?,?,?,?,?,?)";
						preparedStatement = _connection.prepareStatement(sql2);
						preparedStatement.setInt(1,Integer.parseInt(id));
						preparedStatement.setString(2, tin);
						preparedStatement.setString(3, "BOA");
						preparedStatement.setDouble(4, initialBalance);
						preparedStatement.setString(5, "INTEREST_CHECKING");
						preparedStatement.setFloat(6, 0.03f);
						preparedStatement.setInt(7, 0);
						preparedStatement.executeQuery();

						addPrimary(tin, id);


						result =  "0 " + id + " INTEREST_CHECKING " + initialBalance + " " + tin;

					} else if (accountType == AccountType.STUDENT_CHECKING) {
						String sql1 = "insert into customer (cid,cname,address,pin) values (?,?,?,?)";
						preparedStatement = _connection.prepareStatement(sql1);
						preparedStatement.setString(1,tin);
						preparedStatement.setString(2, name);
						preparedStatement.setString(3, address);
						preparedStatement.setString(4, encryptPin("1717"));
						preparedStatement.executeQuery();

						String sql2 = "insert into account (aid, cid, branch_name, balance, type, interest) values (?,?,?,?,?,?)";
						preparedStatement = _connection.prepareStatement(sql2);
						preparedStatement.setInt(1,Integer.parseInt(id));
						preparedStatement.setString(2, tin);
						preparedStatement.setString(3, "BOA");
						preparedStatement.setDouble(4, initialBalance);
						preparedStatement.setString(5, "STUDENT_CHECKING");
						preparedStatement.setFloat(6, 0.00f);
						preparedStatement.executeQuery();

						addPrimary(tin, id);

						result =  "0 " + id + " STUDENT_CHECKING " + initialBalance + " " + tin;

					} else if (accountType == AccountType.SAVINGS) {
						String sql1 = "insert into customer (cid,cname,address,pin) values (?,?,?,?)";
						preparedStatement = _connection.prepareStatement(sql1);
						preparedStatement.setString(1,tin);
						preparedStatement.setString(2, name);
						preparedStatement.setString(3, address);
						preparedStatement.setString(4, encryptPin("1717"));
						preparedStatement.executeQuery();

						String sql2 = "insert into account (aid, cid, branch_name, balance, type, interest) values (?,?,?,?,?,?)";
						preparedStatement = _connection.prepareStatement(sql2);
						preparedStatement.setInt(1,Integer.parseInt(id));
						preparedStatement.setString(2, tin);
						preparedStatement.setString(3, "BOA");
						preparedStatement.setDouble(4, initialBalance);
						preparedStatement.setString(5, "SAVINGS");
						preparedStatement.setFloat(6, 0.048f);
						preparedStatement.executeQuery();

						addPrimary(tin, id);

						result =  "0 " + id + " SAVINGS " + initialBalance + " " + tin;

					}
					else {
						result = "1";
					}
				}


		}
		catch( SQLException e )
		{
			result = "1";
			System.err.println( e.getMessage() );
		}


		return result;
		//return "0 " + id + " " + accountType + " " + initialBalance + " " + tin;
	}

	@Override
	public String createTables() {
		try( Statement statement = _connection.createStatement() )
		{


			String s1 = "create table Customer(" +
					"cid varchar(20)," +
					"cname varchar(20)," +
					"address varchar(20)," +
					"pin varchar(2056)," +   // changed to 2056 for pin hashing :D
					"primary key(cid))";
			String s2 =  "create table Account(" +
					"aid integer," +
					"cid varchar(20)," +
					"branch_name varchar(20)," +
					"balance decimal(13,2)," +
					"type varchar(20), " +
					"interest float, " +
					"status integer default 0, " +
					"pocket_linked_to integer," +
					"primary key(aid)," +
					"foreign key(cid) references Customer(cid))";
			String s3 = "create table Transaction(" +
					"tid integer, " +
					"tdate date, " +
					"cname varchar(20)," +
					"ttype varchar(20)," +
					"amount decimal(13, 2), " +
					"from_aid integer," +
					"to_aid integer," +
					"check_number integer, " +
					"primary key(tid))";
			String s4 = "create table Owners(" +
					"	cid varchar(20), " +
					"aid integer, " +
					"primary integer default 0," +
					"primary key(cid, aid, primary), " +
					"foreign key(cid) references Customer(cid) on delete cascade," +
					"foreign key(aid) references Account(aid) on delete cascade)";
			//String s5 = "create sequence tid start with 1 increment by 1";
			//String s6 = "create table Settings(current_date date)";

			// not quite sure the syntax we create table like that
			String s7 = "create table SETTINGTIMES ( id int, settime date, primary key(id))";

//			String s8 = "create sequence  SETTINGTIMES_seq";
//			String s9 = "create or replace trigger SETTINGTIMES_trg";
//			String s10 = "before insert on SETTINGTIMES for each row begin select SETTINGTIMES_seq.nextval into :new.id from dual; end";

			statement.addBatch(s1);
			statement.addBatch(s2);
			statement.addBatch(s3);
			statement.addBatch(s4);
			//statement.addBatch(s5);
			statement.addBatch(s7);
//			statement.addBatch(s8);
//			statement.addBatch(s9);
//			statement.addBatch(s10);
			statement.executeBatch();

			//ResultSet resultSet = statement.executeQuery("create table Customer(cid integer,cname varchar(20),address varchar(20),pin varchar(20),primary key(cid))" );
			//resultSet = statement.executeQuery("create table Account(aid integer,cid integer,branch_name varchar(20),balance decimal(13,2),interest float,primary key(aid),foreign key(cid) references Customer(cid))" );
			//resultSet = statement.executeQuery("create table CheckingAccount(aid integer,primary key(aid),foreign key(aid) references Account(aid) on delete cascade)" );
			//resultSet = statement.executeQuery("create table Transaction(tid integer,cid integer,aid integer,testId date,info varchar(20),type varchar(20),primary key(tid),foreign key(cid) references Customer(cid),foreign key(aid) references Account(aid))" );
			return "0";
//			try( statement.executeBatch() )
//			{
//				return "successfully creates the table!";
////
//			}
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	@Override
	public String dropTables() {
		try( Statement statement = _connection.createStatement() )
		{

//			String s1 = "drop table Transaction";
//			String s2 = "drop table CheckingAccount";
//			String s3 = "drop table Account";
//			String s4 = "drop table Customer";
//
//			statement.addBatch(s1);
//			statement.addBatch(s2);
//			statement.addBatch(s3);
//			statement.addBatch(s4);
//
//			statement.executeBatch();
			//ResultSet resultSet = statement.executeQuery("drop table Transaction");
//			resultSet = statement.executeQuery("drop table SavingAccount");
//			resultSet = statement.executeQuery("drop table StudentCheckingAccount");
//			resultSet = statement.executeQuery("drop table InterestCheckingAccount");
			ResultSet resultSet = statement.executeQuery("drop table SETTINGTIMES");
			resultSet = statement.executeQuery("drop table Owners");
			resultSet = statement.executeQuery("drop table Account");
			resultSet = statement.executeQuery("drop table Customer");
			resultSet = statement.executeQuery("drop table Transaction");
			return "0";

		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	@Override
	public String deposit( String accountId, double amount )
	{
		double oldBalance = 0.0;
		double newBalance = 0.0;
		String result;
		try( Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) ) {

			String sql = "select account.balance from account where account.aid = ?";
			PreparedStatement preparedStatement = _connection.prepareStatement(sql);
			preparedStatement.setString(1, accountId);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next())
			{
				oldBalance = resultSet.getDouble("balance");
				newBalance = oldBalance + amount;
			}
			else
			{
				return "this aid is invalid";
			}


			String updateBalance = "update account set balance = ? where account.aid = ?";
			PreparedStatement preparedUpdateStatement = _connection.prepareStatement(updateBalance);
			preparedUpdateStatement.setDouble(1, newBalance);
			preparedUpdateStatement.setString(2, accountId);

			preparedUpdateStatement.executeUpdate();
			addTransaction(this.getName(customerTaxID), "deposit", amount, accountId, null, null);

			return "0 " + oldBalance + " " + newBalance;

		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

	}

	@Override
	public String showBalance( String accountId )
	{
		double balance = 0.0;
		try( Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) ) {

			String sql = "select account.balance from account where account.aid = ?";

			PreparedStatement preparedStatement = _connection.prepareStatement(sql);
			preparedStatement.setString(1, accountId);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				balance = resultSet.getDouble("balance");
			}
			return "0 " + balance;

		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	@Override
	public String createPocketAccount( String id, String linkedId, double initialTopUp, String tin )
	{
		String accountID = "";
		try( Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String sql = "select account.aid from account where account.aid = ? and " +
					"(account.type = 'INTEREST_CHECKING' or account.type = 'STUDENT_CHECKING' or account.type = 'SAVINGS') and " +
					"account.balance > 0.01";
			PreparedStatement preparedStatement = _connection.prepareStatement(sql);
			preparedStatement.setString(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
			{
				accountID = resultSet.getString(1);
				if (accountID.equals(linkedId) == false)
				{
					System.out.println("Cannot create pocket account");
					return "1";
				}
			}

		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		try( Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String q = "insert into account (aid,cid,branch_name,balance,type,interest,status, pocket_linked_to) values (?,?,?,?,?,?,?,?)";
			PreparedStatement preparedStatement = _connection.prepareStatement(q);
			preparedStatement.setInt(1,Integer.parseInt(id));
			preparedStatement.setString(2, tin);
			preparedStatement.setString(3, "BOA");
			preparedStatement.setDouble(4, initialTopUp);
			preparedStatement.setString(5, "POCKET");
			preparedStatement.setFloat(6, 0.00f);
			preparedStatement.setInt(7, 0);
			preparedStatement.setInt(8, Integer.parseInt(linkedId));
			preparedStatement.executeUpdate();

			addPrimary(tin, id);

			return("0 " + accountID + "POCKET " + initialTopUp + " " + tin);
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	@Override
	public String setDate( int year, int month, int day )
	{

		String r = "0 ";
		PreparedStatement ps = null;
		String date_str = Integer.toString(year) + '-' + Integer.toString(month) + '-' + Integer.toString(day);
		//SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
		//java.util.Date udate_str = sdf1.parse(date_str);
		java.sql.Date sqdate_str = java.sql.Date.valueOf(date_str);

		//java.util.Date date = parseDate(date_str);


		final String INSERT_INTO_System_Date = "INSERT INTO SETTINGTIMES(ID, SETTIME)"
				+ "VALUES (?,?)";

		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String SQL = "truncate table settingtimes";
			PreparedStatement p = _connection.prepareStatement(SQL);
			p.executeUpdate();


			ps = _connection.prepareStatement(INSERT_INTO_System_Date);
			ps.setInt(1,3); // now here it doesn't matter primary key auto increment
			ps.setDate(2,sqdate_str);
			ps.executeUpdate();



//			Date dt;
//			String s = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day);
//			System.out.println(s);
//			try
//			{
//				dt = new SimpleDateFormat("yyyy-MM-dd").parse(s);
//				System.out.println(dt.toString());
//			}
//			catch (java.text.ParseException e)
//			{
//				System.out.println(e.getMessage());
//			}
//
//			String createPrimary = "insert into Settings(current_date) values(to_date(?, 'YYYY-MM-DD'))";
//			PreparedStatement insert = _connection.prepareStatement(createPrimary);
//			insert.setDate(1, java.sql.Date.valueOf("2013-09-04")); //even hard coding doesnt work...
//			insert.setString(1, s);
//			insert.executeUpdate();
//			return "0";
		}
		catch( SQLException e)
		{
			e.printStackTrace();
			r = "1 ";
//			System.err.println( e.getMessage() );
//			return "1";
		}
		if (month < 10)
		{
			date_str = Integer.toString(year) + "-0" + Integer.toString(month) + '-' + Integer.toString(day);
			return r + date_str;

		}
		else
		{
			return r + date_str;
		}
	}

	public static Date parseDate(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			return null;
		}
	}


	public boolean checkEndOfMonth(java.sql.Date date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(this.getDate());
		int r = c.getActualMaximum(Calendar.DATE);
		return r == c.get(Calendar.DAY_OF_MONTH);
	}

	public java.sql.Date getDate()
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String s = "select SETTIME from SETTINGTIMES ORDER BY id DESC FETCH FIRST 1 ROWS ONLY";
			PreparedStatement preparedStatement = _connection.prepareStatement(s);
			ResultSet resultSet = preparedStatement.executeQuery();


			if(resultSet.next())
			{
				java.sql.Date dbSqlDate = resultSet.getDate("SETTIME");
//				java.util.Date dbSqlDateConverted = new java.util.Date(dbSqlDate.getTime());
//				System.out.println(dbSqlDateConverted);
				return dbSqlDate;
			}
			else
			{
				return null;
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return null;
		}
	}


	@Override
	public String createCustomer( String accountId, String tin, String name, String address )
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String sql = "select aid from account where aid = ? and status = 0";
			PreparedStatement preparedStatement = _connection.prepareStatement(sql);
			preparedStatement.setString(1, accountId);
			ResultSet resultSet = preparedStatement.executeQuery();

			String encryptedPin = encryptPin("1717");

			if (resultSet.next())
			{
				String createCustomer = "insert into Customer(cid, cname, address, pin) values(?, ?, ?, ?)";
				PreparedStatement preparedUpdateStatement = _connection.prepareStatement(createCustomer);
				preparedUpdateStatement.setString(1, tin);
				preparedUpdateStatement.setString(2, name);
				preparedUpdateStatement.setString(3, address);
				preparedUpdateStatement.setString(4, encryptedPin);
				preparedUpdateStatement.executeUpdate();
			}
			else
			{
				System.out.println("Unable to create customer because account specified is closed");
				return "1";
			}
			return "0";
		}
		catch( SQLException e)
		{
			System.err.println( e.getMessage() );
			return "1";
		}

	}

	@Override
	public String topUp( String accountId, double amount )
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			if (amount < 0)
			{
				System.out.println("Amount must be positive");
				return "1";
			}

			double parentNewBal = 0.0;
			double pocketNewBal = 0.0;

			String sql = "select pocket_linked_to, balance, status from account where aid = ? and type = 'POCKET'";
			PreparedStatement p = _connection.prepareStatement(sql);
			p.setString(1, accountId);
			ResultSet resultSet = p.executeQuery();

			if (resultSet.next())
			{
				int status = Integer.parseInt(resultSet.getString(3));
				if (status == 1)
				{
					System.out.println("Pocket account is closed");
					return "1";
				}

				String parentId = resultSet.getString(1);
				double pocketBal = Double.parseDouble(resultSet.getString(2));

				sql = "select balance from account where aid = ?";
				p = _connection.prepareStatement(sql);
				p.setString(1, parentId);
				resultSet = p.executeQuery();

				while (resultSet.next())
				{
					double parentBal = Double.parseDouble(resultSet.getString(1));
					parentNewBal = parentBal - amount;

					if (parentNewBal <= 0.01)
					{
						//System.out.println("Not enough funds");
						//return "1";
					}
					else
					{
						pocketNewBal = pocketBal + amount;

						String updatePocketBalance = "update account set balance = ? where aid = ?";
						PreparedStatement preparedUpdateStatement = _connection.prepareStatement(updatePocketBalance);
						preparedUpdateStatement.setDouble(1, pocketNewBal);
						preparedUpdateStatement.setString(2, accountId);

						preparedUpdateStatement.executeUpdate();

						String updateParentBalance = "update account set balance = ? where aid = ?";
						preparedUpdateStatement = _connection.prepareStatement(updateParentBalance);
						preparedUpdateStatement.setDouble(1, parentNewBal);
						preparedUpdateStatement.setString(2, parentId);

						preparedUpdateStatement.executeUpdate();

						addTransaction(this.getName(customerTaxID), "tops up", amount, parentId, accountId, null);

					}

				}

			}
			else
			{
				System.out.println("Invalid pocket account id");
				return "1";
			}
			return("0 " + parentNewBal + " " + pocketNewBal);
		}

		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	@Override
	public String payFriend( String from, String to, double amount ) // come back to fix cid check ownership
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String result;
			if (amount < 0)
			{
				//System.out.println("Amount must be positive");
				result = "1";
			}

//			if(checkOwnership(this.cid, from) == false)
//			{
//				System.out.println("Customer must be an owner of source account");
//				return "1";
//			}

			String fromPocket = "select balance, status from account where aid = ? and type = 'POCKET'";
			PreparedStatement fromP = _connection.prepareStatement(fromPocket);
			fromP.setString(1, from);
			ResultSet resultSet1 = fromP.executeQuery();
			double fromBalance = 0;
			double toBalance = 0;

			if (resultSet1.next())
			{
				fromBalance = Double.parseDouble(resultSet1.getString(1));
				int status = Integer.parseInt(resultSet1.getString(2));
				if (status == 1)
				{
				//	System.out.println("Source account is closed");
					result = "1";
					//return "1";
				}
				if (fromBalance - amount < 0.00)
				{
				//	System.out.println("Not enough funds");
					result = "1";
					//return "1";
				}
			}
			else
			{
				//System.out.println("Invalid source pocket account id");
				result = "1";
				//return "1";
			}


			String toPocket = "select balance, status from account where aid = ? and type = 'POCKET'";
			PreparedStatement toP = _connection.prepareStatement(toPocket);
			toP.setString(1, to);
			ResultSet resultSet2 = toP.executeQuery();

			if (resultSet2.next())
			{
				int status = Integer.parseInt(resultSet2.getString(2));
				if (status == 1)
				{
					System.out.println("Destination account is closed");
					result = "1";
					//return "1";
				}
				toBalance = Double.parseDouble(resultSet2.getString(1));
				toBalance = toBalance + amount;
				fromBalance = fromBalance - amount;

				if (fromBalance < 0) {
					System.out.println("mmmmm");
					//result = "1";
					return "1";
				} else if (fromBalance == 0.01 || fromBalance == 0.00) {
					String updateFromPocket = "update account set balance = ?, status = ? where aid = ?";
					PreparedStatement preparedUpdateStatement = _connection.prepareStatement(updateFromPocket);
					preparedUpdateStatement.setDouble(1, fromBalance);
					preparedUpdateStatement.setInt(2, 1);
					preparedUpdateStatement.setString(3, from);

					preparedUpdateStatement.executeUpdate();

					preparedUpdateStatement = _connection.prepareStatement(updateFromPocket);
					preparedUpdateStatement.setDouble(1, toBalance);
					preparedUpdateStatement.setInt(2, 0);
					preparedUpdateStatement.setString(3, to);

					preparedUpdateStatement.executeUpdate();

					addTransaction(this.getName(customerTaxID), "pays friend", amount, from, to, null);

					result = "0";
				} else {

					String updateFromPocket = "update account set balance = ? where aid = ?";
					PreparedStatement preparedUpdateStatement = _connection.prepareStatement(updateFromPocket);
					preparedUpdateStatement.setDouble(1, fromBalance);
					preparedUpdateStatement.setString(2, from);

					preparedUpdateStatement.executeUpdate();

					preparedUpdateStatement = _connection.prepareStatement(updateFromPocket);
					preparedUpdateStatement.setDouble(1, toBalance);
					preparedUpdateStatement.setString(2, to);

					preparedUpdateStatement.executeUpdate();


					addTransaction(this.getName(customerTaxID), "pays friend", amount, from, to, null);
					result = "0";

				}
			}
			else
			{
				System.out.println("Invalid destination pocket account id");
				result = "1";
				//return "1";
			}


			return (result + " " + fromBalance + " " + toBalance);
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	public String withdrawal(String accountId, double amount)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			if (amount <= 0)
			{
				System.out.println("Amount must be positive");
				return "1";
			}

			String q = "select balance, status, type from account " +
					"where aid = ?";
			PreparedStatement p = _connection.prepareStatement(q);
			p.setString(1, accountId);
			ResultSet resultSet = p.executeQuery();
			double bal;
			double newBal;

			if(resultSet.next())
			{
				int status = Integer.parseInt(resultSet.getString(2));
				if (status == 1)
				{
					System.out.println("Account is closed");
					return "1";
				}
				String t = resultSet.getString(3);

				if (!t.equals("INTEREST_CHECKING") && !t.equals("STUDENT_CHECKING") && !t.equals("SAVINGS"))

				{
					System.out.println("Cannot withdraw because account must be checking or savings");
					return "1";
				}

				bal = Double.parseDouble(resultSet.getString(1));
				newBal = bal - amount;

				// if the balance is 0.00 or 0.01, then we close the account
				if (newBal == 0.01 || newBal == 0.00)
				{
					String closeAccount = "update account set balance = ?, status = ? where aid = ? ";
					PreparedStatement preparedUpdateStatement = _connection.prepareStatement(closeAccount);
					preparedUpdateStatement.setDouble(1, newBal);
					preparedUpdateStatement.setInt(2, 1);
					preparedUpdateStatement.setString(3, accountId);
					preparedUpdateStatement.executeUpdate();
					addTransaction(this.getName(customerTaxID), "withdrawal", amount, accountId, null, null);

					System.out.println("Account closed"); /// come back to fix
					return "0";
				} else if (newBal < 0) {     // if the new balance is less than 0, then we need to close the transaction
											 // but the account still opens

					return "Transactions fail";

				} else {                    // nothing closed
					String update = "update account set balance = ? where aid = ?";
					PreparedStatement preparedUpdateStatement = _connection.prepareStatement(update);
					preparedUpdateStatement.setDouble(1, newBal);
					preparedUpdateStatement.setString(2, accountId);
					preparedUpdateStatement.executeUpdate();

					addTransaction(this.getName(customerTaxID), "withdrawal", amount, accountId, null, null);

					return ("0 " + bal + " " + newBal);

				}
			}
			else
			{
				System.out.println("Account id is not valid");
				return "1";
			}
			//return ("0 " + bal + " " + newBal);

		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	public String purchase(String accountId, double amount)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			if (amount <= 0)
			{
				System.out.println("Amount must be positive");
				return "1";
			}

			String q = "select balance, status, type from account " +
					"where aid = ?";
			PreparedStatement p = _connection.prepareStatement(q);
			p.setString(1, accountId);
			ResultSet resultSet = p.executeQuery();
			double bal;
			double newBal;

			if(resultSet.next())
			{
				int status = Integer.parseInt(resultSet.getString(2));
				if (status == 1)
				{
					System.out.println("Account is closed");
					return "1";
				}
				String t = resultSet.getString(3);

				if (!t.equals("POCKET"))

				{
					System.out.println("Cannot withdraw because account must be pocket");
					return "1";
				}

				bal = Double.parseDouble(resultSet.getString(1));
				newBal = bal - amount;

				if (newBal == 0.01 || newBal == 0.0)
				{
					String closeAccount = "update account set balance = ?, status = ? where aid = ? ";
					PreparedStatement preparedUpdateStatement = _connection.prepareStatement(closeAccount);
					preparedUpdateStatement.setDouble(1, newBal);
					preparedUpdateStatement.setInt(2, 1);
					preparedUpdateStatement.setString(3, accountId);
					preparedUpdateStatement.executeUpdate();

					addTransaction(this.getName(customerTaxID), "purchase", amount, accountId, null, null);

					System.out.println("Account closed"); /// come back to fix
				} else if (newBal < 0) {

					return "Transactions fail";

				} else {

					String update = "update account set balance = ? where aid = ?";
					PreparedStatement preparedUpdateStatement = _connection.prepareStatement(update);
					preparedUpdateStatement.setDouble(1, newBal);
					preparedUpdateStatement.setString(2, accountId);
					preparedUpdateStatement.executeUpdate();

					addTransaction(this.getName(customerTaxID), "purchases", amount, accountId, null, null);

				}

			}
			else
			{
				System.out.println("Account id is not valid");
				return "1";
			}
			return ("0 " + bal + " " + newBal);

		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}


	// change type void, return type String was just used to test (or keep it, decide later)
	public String addPrimary(String customerId, String accountId) // owner_type: 0 for primary
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{

			String sql = "select aid, primary from owners where aid = ?";
			PreparedStatement p =  _connection.prepareStatement(sql);
			p.setString(1, accountId);
			ResultSet resultSet1 = p.executeQuery();

			int prim;
			if(resultSet1.next())
			{
				prim = Integer.parseInt(resultSet1.getString(2));
				if(prim == 0)
				{
					System.out.println("Account already has a primary owner");
					return "1";
				}
			}

			sql = "select cid from customer where cid = ?";
			p = _connection.prepareStatement(sql);
			p.setString(1, customerId);
			resultSet1 = p.executeQuery();

			String c;
			int a;

			if(!resultSet1.next())
			{
				System.out.println("Invalid tax id number");
				return "1";
			}
			c = resultSet1.getString(1);

			sql = "select aid from account where aid = ?";
			p = _connection.prepareStatement(sql);
			p.setString(1, accountId);
			resultSet1 = p.executeQuery();

			if(!resultSet1.next())
			{
				System.out.println("Invalid account id");
				return "1";
			}
			a = Integer.parseInt(resultSet1.getString(1));

			String createPrimary = "insert into Owners(cid, aid, primary) values(?, ?, ?)";
			PreparedStatement insert = _connection.prepareStatement(createPrimary);
			insert.setString(1, c);
			insert.setInt(2, a);
			insert.setInt(3, 0); //0 is for primary owner
			insert.executeUpdate();

			return "0" + " " + c + " " + a;
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	// change type void, return type String was just used to test (or keep it, decide later)
	public String addCoowner(String customerId, String accountId)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{

			String sql = "select * from owners where aid = ? and cid = ?";
			PreparedStatement p =  _connection.prepareStatement(sql);
			p.setString(1, accountId);
			p.setString(2, customerId);
			ResultSet resultSet1 = p.executeQuery();

			if(resultSet1.next())
			{
				System.out.println("User is already an owner");
				return "1";
			}

			int prim;

			sql = "select cid from customer where cid = ?";
			p = _connection.prepareStatement(sql);
			p.setString(1, customerId);
			resultSet1 = p.executeQuery();

			String c;
			int a;


			if(!resultSet1.next())
			{
				System.out.println("Invalid tax id number");
				return "1";
			}
			c = resultSet1.getString(1);

			sql = "select aid from account where aid = ?";
			p = _connection.prepareStatement(sql);
			p.setString(1, accountId);
			resultSet1 = p.executeQuery();

			if(!resultSet1.next())
			{
				System.out.println("Invalid account id");
				return "1";
			}
			a = Integer.parseInt(resultSet1.getString(1));

			String createPrimary = "insert into Owners(cid, aid, primary) values(?, ?, ?)";
			PreparedStatement insert = _connection.prepareStatement(createPrimary);
			insert.setString(1, c);
			insert.setInt(2, a);
			insert.setInt(3, 1); // 1 is for co-owner
			insert.executeUpdate();

			return "0" + " " + c + " " + a;
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	public String transfer(String customerId, String from, String to, double amount)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			if (amount < 0)
			{
				System.out.println("Amount must be positive");
				return "1";
			}
			if (amount > 2000)
			{
				System.out.println("Amount cannot exceed $2,000");
				return "1";
			}

			String checkIsPrimary = "select cid, primary from owners where cid = ? and aid = ?";
			PreparedStatement p = _connection.prepareStatement(checkIsPrimary);
			p.setString(1, customerId);
			p.setString(2, from);
			ResultSet resultSet = p.executeQuery();

			if (!resultSet.next())
			{
				System.out.println("Invalid customerId or invalid source account id or invalid desination id");
				return "1";
			}
//			if (Integer.parseInt(resultSet.getString(2)) == )
//			{
//				System.out.println("Customer must be a primary account owner on source account to do transfer");
//				return "1";
//			}


			String checkIsCoowner = "select cid, primary from owners where cid = ? and aid = ?";
			p = _connection.prepareStatement(checkIsCoowner);
			p.setString(1, customerId);
			p.setString(2, to);
			resultSet = p.executeQuery();


			if (!resultSet.next())
			{
				System.out.println("Invalid customerId or invalid source account id or invalid desination id");
				return "1";
			}
//			if (Integer.parseInt(resultSet.getString(2)) == 1)
//			{
//				System.out.println("Customer must be a primary on destination account to do transfer");
//				return "1";
//			}


			String fromCheckingsOrSavings = "select balance, status from account where aid = ? and " +
					"(account.type = 'INTEREST_CHECKING' or account.type = 'STUDENT_CHECKING' or account.type = 'SAVINGS')";

			PreparedStatement fromCS = _connection.prepareStatement(fromCheckingsOrSavings);
			fromCS.setString(1, from);
			ResultSet resultSet1 = fromCS.executeQuery();
			double fromBalance = 0;
			double toBalance = 0;

			if (resultSet1.next())
			{
				fromBalance = Double.parseDouble(resultSet1.getString(1));
				int status = Integer.parseInt(resultSet1.getString(2));
				if (status == 1)
				{
					System.out.println("Source account is closed");
					return "1";
				}
				if (fromBalance - amount <= 0.01)
				{
					System.out.println("Not enough funds");
					return "1";
				}
			}
			else
			{
				System.out.println("Invalid checking/savings account id");
				return "1";
			}


			String toCheckingsOrSavings = "select balance, status from account where aid = ? and " +
					"(account.type = 'INTEREST_CHECKING' or account.type = 'STUDENT_CHECKING' or account.type = 'SAVINGS')";

			PreparedStatement toP = _connection.prepareStatement(toCheckingsOrSavings);
			toP.setString(1, to);
			ResultSet resultSet2 = toP.executeQuery();

			if (resultSet2.next())
			{
				int status = Integer.parseInt(resultSet2.getString(2));
				if (status == 1)
				{
					System.out.println("Destination account is closed");
					return "1";
				}
				toBalance = Double.parseDouble(resultSet2.getString(1));
				toBalance = toBalance + amount;
				fromBalance = fromBalance - amount;

				String updateFromPocket = "update account set balance = ? where aid = ?";
				PreparedStatement preparedUpdateStatement = _connection.prepareStatement(updateFromPocket);
				preparedUpdateStatement.setDouble(1, fromBalance);
				preparedUpdateStatement.setString(2, from);

				preparedUpdateStatement.executeUpdate();

				preparedUpdateStatement = _connection.prepareStatement(updateFromPocket);
				preparedUpdateStatement.setDouble(1, toBalance);
				preparedUpdateStatement.setString(2, to);

				preparedUpdateStatement.executeUpdate();

				addTransaction(this.getName(customerTaxID), "trasnfers", amount, from, to, null);

			}

		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
		return "0";
	}

	public String collect(String pocketId,  double amount)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			double fee = 0.03 * amount;

			String sql = "select balance, status, pocket_linked_to from account where aid = ? and type = 'POCKET'";
			PreparedStatement p = _connection.prepareStatement(sql);
			p.setString(1, pocketId);
			ResultSet resultSet = p.executeQuery();
			double parentBal;
			double pocketBal;
			String parentAccountId;

			if (resultSet.next())
			{
				int status = Integer.parseInt(resultSet.getString(2));
				if (status == 1)
				{
					System.out.println("Pocket account is closed");
					return "1";
				}

				parentAccountId = resultSet.getString(3);
				pocketBal = resultSet.getDouble(1);
				pocketBal = pocketBal - amount - fee;

				if (pocketBal <= 0.01) {
					System.out.println("Not enough funds");
					return "1";
				}
			}
			else
			{
				System.out.println("Invalid pocket or checking/savings account id");
				return "1";
			}

			sql = "select balance from account where aid = ?";
			p = _connection.prepareStatement(sql);
			p.setString(1, parentAccountId);
			resultSet = p.executeQuery();

			if (resultSet.next())
			{
				parentBal = resultSet.getDouble(1);
				parentBal = parentBal + amount;

				String updatePocketBalance = "update account set balance = ? where aid = ?";
				PreparedStatement preparedUpdateStatement = _connection.prepareStatement(updatePocketBalance);
				preparedUpdateStatement.setDouble(1, pocketBal);
				preparedUpdateStatement.setString(2, pocketId);

				preparedUpdateStatement.executeUpdate();

				String updateParentBalance = "update account set balance = ? where aid = ?";
				preparedUpdateStatement = _connection.prepareStatement(updateParentBalance);
				preparedUpdateStatement.setDouble(1, parentBal);
				preparedUpdateStatement.setString(2, parentAccountId);

				preparedUpdateStatement.executeUpdate();

				addTransaction(this.getName(customerTaxID), "collects", amount, pocketId, parentAccountId, null);

			}
			else
			{
				System.out.println("Invalid pocket account id");
				return "1";
			}
			return "0";
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	public boolean checkOwnership(String customerId, String accountId)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String sql = "select cid from owners where cid = ? and aid = ?";
			PreparedStatement p = _connection.prepareStatement(sql);
			p.setString(1, customerId);
			p.setString(2, accountId);
			ResultSet resultSet = p.executeQuery();

			String owner;

			if (resultSet.next())
			{
				owner = resultSet.getString(1);
				if (owner.equals(customerId))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			return false;
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return false;
		}
	}


	public boolean checkValidAccount(String accountId)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String sql = "select aid from account where aid = ?";
			PreparedStatement p = _connection.prepareStatement(sql);
			p.setString(1, accountId);
			ResultSet resultSet = p.executeQuery();

			if (resultSet.next())
			{
					return true;
			}
			return false;
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return false;
		}
	}

	public boolean checkSavingsOrCheckings(String accountId)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String sql = "select aid from account where aid = ? and (type = 'INTEREST_CHECKING' or type = 'STUDENT_CHECKING' or type = 'SAVINGS')";
			PreparedStatement p = _connection.prepareStatement(sql);
			p.setString(1, accountId);
			ResultSet resultSet = p.executeQuery();

			String a;

			if(resultSet.next())
			{
				a = resultSet.getString(1);
				if(a.equals(accountId))
				{
					return true;
				}
				return false;
			}
			return false;
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return false;
		}

	}

	public boolean checkChecking(String accountId)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String sql = "select aid from account where aid = ? and (type = 'INTEREST_CHECKING' or type = 'STUDENT_CHECKING')";
			PreparedStatement p = _connection.prepareStatement(sql);
			p.setString(1, accountId);
			ResultSet resultSet = p.executeQuery();

			String a;

			if(resultSet.next())
			{
				a = resultSet.getString(1);
				if(a.equals(accountId))
				{
					return true;
				}
				return false;
			}
			return false;
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return false;
		}

	}

	public String wire(String customerId, String from, String to, double amount)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			double fee = 0.02 * amount;
			double fromBal;
			double toBal;

			if (checkValidAccount(from) == false)
			{
				System.out.println("Source account is not valid");
				return "1";
			}

			if (checkValidAccount(to) == false)
			{
				System.out.println("Destination account is not valid");
				return "1";
			}

			if (checkSavingsOrCheckings(from) == false)
			{
				System.out.println("Source account is not checking or savings account");
				return "1";
			}

			if (checkSavingsOrCheckings(to) == false)
			{
				System.out.println("Destination account is not checking or savings account");
				return "1";
			}

			if(checkOwnership(customerId, from))
			{
				String sql = "select balance from account where aid = ?";
				PreparedStatement p = _connection.prepareStatement(sql);
				p.setString(1, from);
				ResultSet resultSet = p.executeQuery();

				if(resultSet.next())
				{
					fromBal = Double.parseDouble(resultSet.getString(1));
					fromBal = fromBal - amount - fee;
					if (fromBal <= 0.01)
					{
						System.out.println("Not enough funds");
						return "1";
					}

					sql = "select balance from account where aid = ?";
					p = _connection.prepareStatement(sql);
					p.setString(1, to);
					resultSet = p.executeQuery();

					if(resultSet.next())
					{
						toBal = Double.parseDouble(resultSet.getString(1));
						toBal = toBal + amount;

						String updateFromBal = "update account set balance = ? where aid = ?";
						PreparedStatement preparedUpdateStatement = _connection.prepareStatement(updateFromBal);
						preparedUpdateStatement.setDouble(1, fromBal);
						preparedUpdateStatement.setString(2, from);

						preparedUpdateStatement.executeUpdate();

						String updateToBal = "update account set balance = ? where aid = ?";
						preparedUpdateStatement = _connection.prepareStatement(updateToBal);
						preparedUpdateStatement.setDouble(1, toBal);
						preparedUpdateStatement.setString(2, to);

						preparedUpdateStatement.executeUpdate();

						addTransaction(this.getName(customerTaxID), "wires", amount, from, to, null);
					}

				}
				else
				{
					System.out.println("Invalid customer id or source account id or destination account id");
				}
			}
			else
			{
				System.out.println("Customer is not owner of source account");
				return "1";
			}
			return "0";

		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	// get maximum check number from transaction table and incrememnt by 1 to create new check number
	// if no checks have been made then first check number is 1
	public int generateCheckNo()
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			int checkNumber;
			String sql = "select max(check_number) from transaction";
			PreparedStatement p = _connection.prepareStatement(sql);
			ResultSet resultSet = p.executeQuery();

			if(resultSet.next())
			{
				String maxCheckNum = resultSet.getString(1);
				System.out.println("<" + maxCheckNum + ">");
				if(maxCheckNum == null)
				{
					checkNumber = 1;
				}
				else
				{
					checkNumber = Integer.parseInt(maxCheckNum) + 1;
				}
			}
			else
			{
				checkNumber = 1;
			}
			return checkNumber;
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return 1;
		}
	}

	public String writeCheck(String accountId, double amount)
	{
		System.out.println(accountId);
		System.out.println(amount);
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{

			if (amount < 0)
			{
				System.out.println("Amount must be positive");
				return "1";
			}
			double checkingBal;
			int checkNumber;

			if(checkValidAccount(accountId))
			{
				if (checkChecking(accountId))
				{
					String sql = "select balance from account where aid = ?";
					PreparedStatement p = _connection.prepareStatement(sql);
					p.setString(1, accountId);
					ResultSet resultSet = p.executeQuery();

					while(resultSet.next())
					{
						checkingBal = resultSet.getDouble(1);
						if (checkingBal - amount <= 0.01)
						{
							System.out.println("Not enough funds");
							return "1";
						}
						checkingBal = checkingBal - amount;

						String updateCheckingBal = "update account set balance = ? where aid = ?";
						PreparedStatement preparedUpdateStatement = _connection.prepareStatement(updateCheckingBal);
						preparedUpdateStatement.setDouble(1, checkingBal);
						preparedUpdateStatement.setString(2, accountId);

						preparedUpdateStatement.executeUpdate();

						checkNumber = generateCheckNo();
						String checkN = Integer.toString(checkNumber);

						addTransaction(this.getName(customerTaxID), "writes a check", amount, accountId, null, checkN);

					}

				}
				else
				{
					System.out.println("You can only write a check from a checking account");
					return "1";
				}
			}
			else
			{
				System.out.println("Not a valid account id");
				return "1";
			}
			return "0";
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}


	public String encryptPin(String pin)
	{
		try
		{
//			SecureRandom random = new SecureRandom();
//			byte[] salt = new byte[16];
//			random.nextBytes(salt);
//			String s = new String(salt);
//			s = "glennie";

			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(salt.getBytes());
			String passwordToHash = pin;
			byte[] hashedPassword = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
			String hp = new String(hashedPassword);
			return hp;
		}
		catch (Exception e)
		{
			return "1";
		}
	}

	public boolean verifyPin(String cid, String pin)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String s = "select pin from customer where cid = ?";
			PreparedStatement p = _connection.prepareStatement(s);
			p.setString(1, cid);
			ResultSet rs = p.executeQuery();

			String dbPin = "";
			String checkEncryption = "";

			if(rs.next())
			{
				dbPin = rs.getString(1);
				checkEncryption = encryptPin(pin);
				return(dbPin.equals(checkEncryption));
			}
			else
			{
				System.out.println("No such customer");
				return false;
			}

		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return false;
		}

	}

	public void setPin(String cid, String OldPIN, String NewPIN)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String encryptedOld = encryptPin(OldPIN);
			String encryptedNew = encryptPin(NewPIN);

			String s = "select cid from customer where cid = ? and pin = ?";
			PreparedStatement p = _connection.prepareStatement(s);
			p.setString(1, cid);
			p.setString(2, encryptedOld);
			ResultSet rs = p.executeQuery();

			if (rs.next())
			{
				String updatePin = "update customer set pin = ? where cid = ? and pin = ?";
				PreparedStatement preparedUpdateStatement = _connection.prepareStatement(updatePin);
				preparedUpdateStatement.setString(1, encryptedNew);
				preparedUpdateStatement.setString(2, cid);
				preparedUpdateStatement.setString(3, encryptedOld);
				preparedUpdateStatement.executeUpdate();

				System.out.println("Successfully updated pin");
				System.out.println(encryptedOld);
				System.out.println(encryptedNew);

			}
			else
			{
				System.out.println("Incorrect cid and/or oldPin");
				return;
			}


		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			System.out.println("1");

		}

	}



	// confused about transaction date, should it be the current date? or something else
	public void addTransaction(String customerName, String trans_type, double amount, String from, String to, String checkNumber)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{

			String sql = "insert into transaction(tid, cname, tdate, ttype, amount, from_aid, to_aid, check_number) " +
					"values(tid.nextval, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement p = _connection.prepareStatement(sql);
			p.setString(1, customerName);
			p.setDate(2, this.getDate());
			p.setString(3, trans_type);
			p.setDouble(4, amount);
			p.setString(5, from);
			p.setString(6, to);
			p.setString(7, checkNumber);
			p.executeUpdate();
//			return "0";
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
//			return "1";
		}
	}

	public String deleteTransactions()
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			if (checkEndOfMonth(this.getDate()) == false)
			{
				System.out.println("Sorry it is not the end of the month");
				return "1";
			}
			else
			{
				String sql = "truncate table transaction";
				PreparedStatement p = _connection.prepareStatement(sql);
				p.executeUpdate();
				return "0";
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

//	public String deleteClosedAccountsAndCustomers()
//	{
//		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
//		{
//			// check the date whether is the last day of the month
//			ResultSet resultSet = statement.executeQuery( "select SETTIME from SETTINGTIMES ORDER BY id DESC FETCH FIRST 1 ROWS ONLY" );
//
//			if( resultSet.next() ) {
//
//				java.sql.Date dbSqlDate = resultSet.getDate("SETTIME");
//				java.util.Date dbSqlDateConverted = new java.util.Date(dbSqlDate.getTime());
//				SimpleDateFormat simpleDateformat = new SimpleDateFormat("MM"); // two digit numerical represenation
//				System.out.println(simpleDateformat.format(dbSqlDateConverted));
//
//				//if () {   // for template: the date is the last date of the month
//
//					String sql2 = "select account.aid from account where account.status = 1";
//					PreparedStatement preparedStatement = _connection.prepareStatement(sql2);
//					preparedStatement.executeQuery( "delete from account where account.status = 1" );
//
//					ResultSet resultSet2 = preparedStatement.executeQuery(); // get the result from sql2
//
//					while (resultSet2.next()) {
//
//						String aid = resultSet2.getString("AID");
//
//						Resultset resultset3 = preparedStatementForPocket.executeQuery();
//
//						if (resultset3.next()) {
//							String sql4 = "delete from account where account.id = ?";
//							PreparedStatement preparedStatementForPocket = _connection.prepareStatement(sql4);
//							preparedStatementForPocket.setString(aid);
//							preparedStatementForPocket.executeUpdate();
//						}
//
//					}
//					// if the date is the last day of the month
//				//} else {
//				//	return "1 the date is not the last day of the month";
//				//}
//			}
//			return "1";
//		}
//		catch(SQLException e)
//		{
//			System.err.println(e.getMessage());
//			return "1";
//		}
//	}

	public String getCustomerReport(String cid)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String s = "select cid from customer where cid = ?";
			PreparedStatement ps = _connection.prepareStatement(s);
			ps.setString(1, cid);
			ResultSet resultSet = ps.executeQuery();

			if(!resultSet.next())
			{
				System.out.println("Invalid tax id number");
				return "1";
			}
			else
			{
				String sql = "select A.aid, A.status from owners O, account A where O.cid = ? and A.aid = O.aid";
				PreparedStatement p = _connection.prepareStatement(sql);
				p.setString(1, cid);
				ResultSet rs = p.executeQuery();

				HashMap<String, Integer> accountStatus = new HashMap<>();

				while (rs.next()) {
					accountStatus.put(rs.getString(1), rs.getInt(2));
				}

				for (String i : accountStatus.keySet()) {
					String key = i;
					Integer value = accountStatus.get(i);
					System.out.println(key + " " + value);
				}
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
		return "0";
	}


	// goes and checks if the customer is a primary owner of some account in owners table
	// if they are, generate reports for all the accounts on which they are listed as a primary or co-owner
	// else, they don't have this privilege bc their only role in the bank system is as a co-owner (meaning they arent primary owners of any accounts)

	public String generateMonthlyStatement(String customerId)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) ) {
			if (checkEndOfMonth(this.getDate()) == false) {
				System.out.println("Sorry it is not the end of the month");
				return "1";
			}
			else
			{
				String s = "select cid from owners where cid = ? and primary = 0";
				PreparedStatement ps = _connection.prepareStatement(s);
				ps.setString(1, customerId);
				ResultSet resultSet = ps.executeQuery();

				ArrayList<String> aids = new ArrayList<String>();

				if (!resultSet.next()) {
					System.out.println("Sorry you do not have this privilege. You must be a primary owner of some account to generate a monthly report.");
					return "1";
				} else {
					s = "select aid from owners where cid = ?";
					ps = _connection.prepareStatement(s);
					ps.setString(1, customerId);
					resultSet = ps.executeQuery();

					while (resultSet.next()) {
						aids.add(resultSet.getString(1));
					}

					for (String aid : aids) {
						printOwnersAndAdresses(aid);
						printReport(aid);
					}
				}
				insuranceMonthlyReport(customerId);
				System.out.println("-----------------------------");

				return "0";
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	public void printOwnersAndAdresses(String accountId)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String s = "select cid from owners where aid = ? ";
			PreparedStatement ps = _connection.prepareStatement(s);
			ps.setString(1, accountId);
			ResultSet resultSet = ps.executeQuery();

			ArrayList<String> cids = new ArrayList<>();
			while(resultSet.next())
			{
				cids.add(resultSet.getString(1));
			}

			for(String cid : cids)
			{
				s = "select cname, address from customer where cid = ? ";
				ps = _connection.prepareStatement(s);
				ps.setString(1, cid);
				resultSet = ps.executeQuery();

				while(resultSet.next())
				{
					System.out.println("Customer: " + resultSet.getString(1) + "\t\t\t\t\t" + "Address: " + resultSet.getString(2));
				}
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return;
		}

	}


	public String insuranceMonthlyReport(String cid)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String s = "select aid from owners where cid = ? and primary = 0";
			PreparedStatement ps = _connection.prepareStatement(s);
			ps.setString(1, cid);
			ResultSet resultSet = ps.executeQuery();

			ArrayList<String> aids = new ArrayList<String>();
			while(resultSet.next())
			{
				aids.add(resultSet.getString(1));
			}

			double total = 0.0;
			for(int i = 0; i < aids.size(); i++)
			{
				s = "select balance from account where aid = ?";
				ps = _connection.prepareStatement(s);
				ps.setString(1, aids.get(i));
				resultSet = ps.executeQuery();

				while(resultSet.next())
				{
					total += resultSet.getDouble(1);
				}
			}
			if(total > 100000)
			{
				System.out.println("Warning! You have reached the limit of $100,000");
				return "1";
			}
			return "0";
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

	}

	// need to be updated
	public String printReport(String accountId)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String s = "select balance from account where aid = ?";
			PreparedStatement ps = _connection.prepareStatement(s);
			ps.setString(1, accountId);
			ResultSet resultSet = ps.executeQuery();
			double currentBalance;

			if(resultSet.next())
			{
				currentBalance = resultSet.getDouble(1);
			}
			else
			{
				System.out.println("There is no such account");
				return "1";
			}

			System.out.println("-----------------------------");
			System.out.println("Report for account: " + accountId);
			s = "select * from transaction where from_aid = ? or to_aid = ? order by tdate, ttype";
			ps = _connection.prepareStatement(s);
			ps.setString(1, accountId);
			ps.setString(2, accountId);
			resultSet = ps.executeQuery();

			String d;
			String name;
			String type;
			double amount;
			String from = "";
			String to = "";
			int check_numer;

			double monthAmount = 0;

			if(!resultSet.next())
			{
				System.out.println("No transactions for this account.");
				return "1";
			}
			else
			{

				while(resultSet.next())
				{
					d = resultSet.getString(2);
					name = resultSet.getString(3);
					type = resultSet.getString(4);
					amount = resultSet.getDouble(5);
					from = resultSet.getString(6);
					to = resultSet.getString(7);
					check_numer = resultSet.getInt(8);

					switch (type)
					{
						case "deposit":
							System.out.println(d + " " + name + " deposits " + amount + " to account " + from);
							monthAmount += amount;
							break;
						case "tops up":
							if(from.equals(accountId))
								monthAmount -= amount;
							else
								monthAmount += amount;
							System.out.println(d + " " + name + " top-ups " + amount + " to account " + to + " from account " + from);
							break;
						case "withdrawal":
							System.out.println(d + " " + name + " withdraws " + amount + " from account " + from);
							monthAmount -= amount;
							break;
						case "purchases":
							System.out.println(d + " " + name + " purchases " + amount + " from account " + from);
							monthAmount -= amount;
							break;
						case "writes a check":
							System.out.println(d + " " + name + " writes a check in amount of " + amount + " from account " + from);
							monthAmount -= amount;
							break;
						case "collects":
							if(from.equals(accountId))
								monthAmount -= amount;
							else
								monthAmount += amount;
							System.out.println(d + " " + name + " collects " + amount + " from account " + from + " to account " + to);
							break;
						case "trasnfers":
							if(from.equals(accountId))
								monthAmount -= amount;
							else
								monthAmount += amount;
							System.out.println(d + " " + name + " transfers " + amount + " from account " + from + " to account " + to);
							break;
						case "pays friend":
							if(from.equals(accountId))
								monthAmount -= amount;
							else
								monthAmount += amount;
							System.out.println(d + " " + name + " pay-friends " + amount + " from account " + from + " to account " + to);
							break;
						case "wires":
							if(from.equals(accountId))
								monthAmount -= amount;
							else
								monthAmount += amount;
							System.out.println(d + " " + name + " wires " + amount + " from account " + from + " to account " + to);
							break;
						case "accrues interest":
							System.out.println(d + " " + name + " accrues interest " + amount + " to account " + to);
							monthAmount += amount;
							break;
						default:
							System.out.println("UNKNOWN TRANSACTION TYPE!!");
							break;
					}
				}
			}

			System.out.println("Initial account balance: " + (currentBalance - monthAmount));
			System.out.println("Final account balance: " + currentBalance);
			System.out.println("-----------------------------");
			return "0";
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	public void addInterest()
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) ) {

			if (checkEndOfMonth(this.getDate()) == false)
			{
				System.out.println("Sorry it is not the end of the month");
				return;
			}
			else
			{
				ArrayList<String> openAids = new ArrayList<String>();

				String sql = "select aid from account where status = 0";
				PreparedStatement preparedStatement = _connection.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (!resultSet.next()) {
					System.out.println("No open accounts");
				} else {
					sql = "select aid from account where status = 0";
					preparedStatement = _connection.prepareStatement(sql);
					resultSet = preparedStatement.executeQuery();

					while (resultSet.next()) {
						openAids.add(resultSet.getString(1));
					}

					for (String aid : openAids) {
						accrueInterest(aid);
					}
				}
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
	}

//	public void accrueInterest(String accountId) {
//		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
//		{
//			String sql = "select account.balance from account where account.aid = ? and account.status = 0";
//			PreparedStatement preparedStatement = _connection.prepareStatement(sql);
//			preparedStatement.setString(1, accountId);
//			ResultSet resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
////				String checkDate = "select SETTIME from SETTINGTIMES ORDER BY id DESC FETCH FIRST 1 ROWS ONLY";
////				System.out.println("In here actually");
////				PreparedStatement preparedStatement2 = _connection.prepareStatement(checkDate);
////				ResultSet resultSet2 = preparedStatement2.executeQuery();
//
//
//				ResultSet resultSet2 = statement.executeQuery( "select SETTIME from SETTINGTIMES ORDER BY id DESC FETCH FIRST 1 ROWS ONLY" );
//
//					while( resultSet2.next() ) {
//
//						java.sql.Date dbSqlDate = resultSet2.getDate("SETTIME");
//						java.util.Date dbSqlDateConverted = new java.util.Date(dbSqlDate.getTime());
//						System.out.println(dbSqlDateConverted); // check the current system date
//					}
//					// then if yes we accruate the interest
//
//
//
////				String addInterest = "update account set account.interest = ? where account.aid = ?";
////				PreparedStatement preparedUpdateStatement = _connection.prepareStatement(addInterest);
////				preparedUpdateStatement.setFloat(1, newInterest);
////				preparedUpdateStatement.setString(2, accountId);
////
////				preparedUpdateStatement.executeUpdate();
//			}
//			else
//			{
//				System.out.println("Unable to accrue interest because account specified is closed");
//			}
//		}
//		catch(SQLException e)
//		{
//			System.err.println(e.getMessage());
//		}
//	}

	public void accrueInterest(String accountId)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			if (checkEndOfMonth(this.getDate()) == false)
			{
				System.out.println("Sorry it is not the end of the month");
				return;
			}
			else
			{
				java.sql.Date today = this.getDate();

				String sql = "select balance, interest from account where aid = ?";
				PreparedStatement preparedStatement = _connection.prepareStatement(sql);
				preparedStatement.setString(1, accountId);
				ResultSet resultSet = preparedStatement.executeQuery();

				double a = 0.0;
				float interest_rate = 0.00f;

				if (resultSet.next()) {
					a = resultSet.getDouble(1);
					interest_rate = resultSet.getFloat(2);

				} else {
					System.out.println("error");
				}

				ArrayList<Double> dailyBalances = new ArrayList<Double>(0);

				String s = today.toString();
				String[] splits = s.split("-");
				int l = Integer.parseInt(splits[splits.length - 1]);


				for (int i = 1; i <= l; i++) {
					java.sql.Date day = new java.sql.Date(today.getYear(), today.getMonth(), i);
					double bal = getBalance(day, accountId, a);

					dailyBalances.add(bal);

				}

				Double total = new Double(0.0);
				for (int i = 1; i < dailyBalances.size(); i++) {
					total += dailyBalances.get(i);
				}

				total = total / dailyBalances.size();


				this.addAccrueInterestTransaction(accountId, (interest_rate * total));
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
	}


	//same as deposit but with type interest
	public String addAccrueInterestTransaction( String accountId, double amount )
	{
		double oldBalance = 0.0;
		double newBalance = 0.0;
		String result;
		try( Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) ) {

			String sql = "select account.balance from account where account.aid = ?";
			PreparedStatement preparedStatement = _connection.prepareStatement(sql);
			preparedStatement.setString(1, accountId);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next())
			{
				oldBalance = resultSet.getDouble("balance");
				newBalance = oldBalance + amount;
			}
			else
			{
				return "this aid is invalid";
			}


			String updateBalance = "update account set balance = ? where account.aid = ?";
			PreparedStatement preparedUpdateStatement = _connection.prepareStatement(updateBalance);
			preparedUpdateStatement.setDouble(1, newBalance);
			preparedUpdateStatement.setString(2, accountId);

			preparedUpdateStatement.executeUpdate();
			addTransaction(this.getName(customerTaxID), "add interest", amount, null, accountId, null);

			return "0 " + oldBalance + " " + newBalance;
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

	}


	public double getBalance(java.sql.Date da, String accountId, double accountBal)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String sql = "select * from transaction where (from_aid = ? or to_aid = ?) and tdate >= ? order by tdate";
			PreparedStatement preparedStatement = _connection.prepareStatement(sql);
			preparedStatement.setString(1, accountId);
			preparedStatement.setString(2, accountId);
			preparedStatement.setDate(3, da);
			ResultSet resultSet = preparedStatement.executeQuery();


			String type;
			double amount;
			String from = "";
			String to = "";
			int check_numer;

			double monthAmount = 0;

			while(resultSet.next())
			{
				type = resultSet.getString(4);
				amount = resultSet.getDouble(5);
				from = resultSet.getString(6);
				to = resultSet.getString(7);
				check_numer = resultSet.getInt(8);

				switch (type)
				{
					case "deposit":
						monthAmount += amount;
						break;
					case "tops up":
						if(from.equals(accountId))
							monthAmount -= amount;
						else
							monthAmount += amount;
						break;
					case "withdrawal":
						monthAmount -= amount;
						break;
					case "purchases":
						monthAmount -= amount;
						break;
					case "writes a check":
						monthAmount -= amount;
						break;
					case "collects":
						if(from.equals(accountId))
							monthAmount -= amount;
						else
							monthAmount += amount;
						break;
					case "trasnfers":
						if(from.equals(accountId))
							monthAmount -= amount;
						else
							monthAmount += amount;
						break;
					case "pays friend":
						if(from.equals(accountId))
							monthAmount -= amount;
						else
							monthAmount += amount;
						break;
					case "wires":
						if(from.equals(accountId))
							monthAmount -= amount;
						else
							monthAmount += amount;
						break;
					case "accrues interest":
						if(to.equals(accountId))
							monthAmount += amount;
						break;
					default:
						System.out.println("UNKNOWN TRANSACTION TYPE!!");
						break;
				}
			}
			return (accountBal - monthAmount);

		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return 0.0;
		}
	}

	public String getAddress(String tid)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String sql = "select address from customer where cid = ?";
			PreparedStatement preparedStatement = _connection.prepareStatement(sql);
			preparedStatement.setString(1, tid);
			ResultSet resultSet = preparedStatement.executeQuery();

			String a = "";
			while(resultSet.next())
			{
				a = resultSet.getString(1);
				System.out.println("0");
				return a;
			}
			return "0";
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

	}

	public String getName(String tid)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String sql = "select cname from customer where cid = ?";
			PreparedStatement preparedStatement = _connection.prepareStatement(sql);
			preparedStatement.setString(1, tid);
			ResultSet resultSet = preparedStatement.executeQuery();

			String n = "";
			while(resultSet.next())
			{
				n = resultSet.getString(1);
				System.out.println("0");
				return n;
			}
			return "0";
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}

	}

	public String generateDTER(String customerId)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) ) {
			String s = "select sum(t.amount) as total_amount from owners o, transaction t where o.aid = t.from_aid and o.cid = ? and o.primary = 0 and ttype='deposit'";
			PreparedStatement ps = _connection.prepareStatement(s);
			ps.setString(1, customerId);
			ResultSet resultSet = ps.executeQuery();

			double totalDeposit = 0.0;

			if (resultSet.next())
			{
				totalDeposit = (resultSet.getDouble("total_amount"));
				System.out.println("TOTAL DEPOSIT");
				System.out.println(Double.toString(totalDeposit));
			}

			s = "select sum(t.amount) as total_amount from owners o, transaction t where o.aid = t.to_aid and o.cid = ? and o.primary = 0 and ttype='trasnfers'";
			ps = _connection.prepareStatement(s);
			ps.setString(1, customerId);
			resultSet = ps.executeQuery();

			double totalTransfers = 0.0;

			if (resultSet.next())
			{
				totalTransfers = (resultSet.getDouble("total_amount"));
				System.out.println("TOTAL Transfers");
				System.out.println(Double.toString(totalTransfers));
			}

			s = "select sum(t.amount) as total_amount from owners o, transaction t where o.aid = t.to_aid and o.cid = ? and o.primary = 0 and ttype='wires'";
			ps = _connection.prepareStatement(s);
			ps.setString(1, customerId);
			resultSet = ps.executeQuery();

			double totalWires = 0.0;

			if (resultSet.next())
			{
				totalWires = (resultSet.getDouble("total_amount"));
				System.out.println("TOTAL Wires");
				System.out.println(Double.toString(totalWires));
			}

			if((totalDeposit + totalTransfers + totalWires) > 10000)
			{
				System.out.println(customerId);
			}
			return "0";
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	public void bankTeller()
	{
		int choice = 0;
		String selected = "";


		while (choice != 10) {

			Scanner sc = new Scanner(System.in).useDelimiter("\n");
			System.out.println("Bank Teller");
			System.out.println("1. Enter Check Transaction");
			System.out.println("2. Generate Monthly Statement");
			System.out.println("3. List Closed Accounts");
			System.out.println("4. Generate Government Drug and Tax Evasion Report (DTER)");
			System.out.println("5. Customer Report");
			System.out.println("6. Add Interest");
			System.out.println("7. Create Account");
			System.out.println("8. Delete Closed Accounts and Customers");
			System.out.println("9. Delete Transactions");
			System.out.println("10. Exit");

			System.out.print("Enter choice(1-10): ");

			choice = sc.nextInt();

			switch (choice) {
				case 1:
					System.out.print("Enter account id: ");
					String accId = sc.next();
					customerTaxID = accId;
					System.out.print("Enter amount: ");
					double a = sc.nextDouble();
					writeCheck(accId, a);
					break;
				case 2:
					System.out.print("Enter tax identification number: ");
					accId = sc.next();
					generateMonthlyStatement(accId);  // to do
					break;
				case 3:
					listClosedAccounts();
					break;
				case 4:
					System.out.print("Enter tax id: ");
					customerTaxID = sc.next();
					generateDTER(customerTaxID);
					break;
				case 5:
					System.out.print("Enter tax identification number: ");
					customerTaxID = sc.next();
					getCustomerReport(customerTaxID);
					break;
				case 6:
					addInterest();
					break;
				case 7:
					System.out.println("Select what type of account you would like to create: ");
					System.out.println("1. Interest Checking");
					System.out.println("2. Student Checking");
					System.out.println("3. Savings");
					System.out.println("4. Pocket");

					int choice2 = sc.nextInt();
					int returningnew;


					System.out.print("Type 0 if you are a returning customer or 1 if you are a new customer: ");
					returningnew = sc.nextInt();


					System.out.print("What is your tax identification number? ");
					customerTaxID = sc.next();

					System.out.print("Please write a 5 digit account id number to be associated with your account: ");
					String aid = sc.next();

					if (returningnew == 0)
					{
						switch (choice2)
						{
							case 1:
								createCheckingSavingsAccount(AccountType.INTEREST_CHECKING, aid, 1000, customerTaxID, this.getName(customerTaxID), this.getAddress(customerTaxID));
								break;
							case 2:
								createCheckingSavingsAccount(AccountType.STUDENT_CHECKING, aid, 1000, customerTaxID, this.getName(customerTaxID), this.getAddress(customerTaxID));
								break;
							case 3:
								createCheckingSavingsAccount(AccountType.SAVINGS, aid, 1000, customerTaxID, this.getName(customerTaxID), this.getAddress(customerTaxID));
								break;
							case 4:
								System.out.print("What checking/savings account id do you wish to link your pocket account to? ");
								String linked = sc.next();
								createPocketAccount(aid, linked, 50, customerTaxID);
								System.out.println(aid);
								System.out.println(linked);
								System.out.println(customerTaxID);
								break;
						}
						break;
					}
					else
					{
						switch (choice2)
						{
							case 1:
								System.out.println("What is your name?");
								sc.nextLine();
								String n = sc.nextLine();
								System.out.println("What is your address?");
								String add = sc.next();
								createCheckingSavingsAccount2(AccountType.INTEREST_CHECKING, aid, 1000, customerTaxID, n, add);
								break;
							case 2:
								System.out.println("What is your name?");
								sc.nextLine();
								n = sc.nextLine();
								System.out.println("What is your address?");
								add = sc.next();
								createCheckingSavingsAccount2(AccountType.STUDENT_CHECKING, aid, 1000, customerTaxID, n, add);
								break;
							case 3:
								System.out.println("What is your name? ");
								sc.nextLine();
								n = sc.nextLine();
								System.out.println("What is your address? ");
								add = sc.next();
								createCheckingSavingsAccount2(AccountType.SAVINGS, aid, 1000, customerTaxID, n, add);
								break;
							case 4:
								System.out.print("What checking/savings account id do you wish to link your pocket account to? ");
								String linked = sc.next();
								createPocketAccount(aid, linked, 50, customerTaxID);
								break;
						}
					}
					break;
				case 8:
					System.out.println("Deleting closed accounts and customers.");
					 //deleteClosedAccountsAndCustomers();
					break;
				case 9:
					System.out.println("Deleting transactions.");
					deleteTransactions();
					break;
				case 10:
					System.out.println("Exiting BankTeller!");
					break;
				default:
					System.out.println("Not a valid option.");
					break;
			}

		}
	}

	public void atm()
	{
		Scanner sc = new Scanner(System.in);
		int choice = 0;
		System.out.println("Welcome to ATM");
		System.out.println("Please enter tax identification number: ");
		customerTaxID = sc.next();
		//check if user exists
		System.out.println("Please enter pin: ");
		String pin = sc.next();
		if (verifyPin(customerTaxID, pin))
		{
			while (choice != 10) {
				System.out.println("Choose one of the following options:");
				System.out.println("1: Deposit");
				System.out.println("2: Top-Up");
				System.out.println("3: Withdrawal");
				System.out.println("4: Purchase");
				System.out.println("5: Transfer");
				System.out.println("6: Collect");
				System.out.println("7: Pay-Friend");
				System.out.println("8: Wire");
				System.out.println("9: Accrue-Interest");
				System.out.println("10: Exit ATM");

				choice = sc.nextInt();
				switch(choice) {
					case 1:
						System.out.println("To which account would you like to deposit? "); //fix why it doesnt show wrong errors
						String accId = sc.next();
						System.out.print("How much would you like to deposit? ");
						double a = sc.nextDouble();
						deposit(accId, a);
						break;

					case 2:
						System.out.println("Which pocket account id would you like to top up? "); // works
						accId = sc.next();
						System.out.print("How much would you like to top-up? ");
						a = sc.nextDouble();
						topUp(accId, a);
						break;

					case 3:
						System.out.print("From which account would you like to withdraw? "); // works
						accId = sc.next();
						System.out.print("How much would you like to withdraw? ");
						a = sc.nextDouble();
						withdrawal(accId, a);
						break;

					case 4:
						System.out.print("From which pokcet account id would you like to purchase? "); // works
						accId = sc.next();
						System.out.print("How much would you like to purchase? ");
						a = sc.nextDouble();
						purchase(accId, a);
						break;

					case 5:
						System.out.print("From which checkings/savings account would you like to transfer? ");
						String from = sc.next();
						System.out.print("To which checkings/savings account would you like to transfer? ");
						String to = sc.next();
						System.out.print("How much would you like to transfer? ");
						a = sc.nextDouble();
						transfer(customerTaxID, from, to, a);
						break;

					case 6:
						System.out.print("From which pocket account would you like to collect money from? "); // words
						accId = sc.next();
						System.out.print("How much would you like to collect? ");
						a = sc.nextDouble();
						collect(accId, a);
						break;

					case 7:
						System.out.print("From which pocket account would you like to pay from? "); // works
						from = sc.next();
						System.out.print("To which pocket account would you like to pay? ");
						to = sc.next();
						System.out.print("How much would you like to pay? ");
						a = sc.nextDouble();
						payFriend(from, to, a);
						break;

					case 8:
						System.out.print("From which checking/savings account would you like to wire from? "); // works
						from = sc.next();
						System.out.print("To which checking/savings would you like to wire to? ");
						to = sc.next();
						System.out.print("How much would you like to wire? ");
						a = sc.nextDouble();
						wire(customerTaxID, from, to, a);
						break;

					case 9:
						System.out.print("From which account would you like to accrue interest? "); // works
						accId = sc.next();
						accrueInterest(accId);
						break;
					case 10:
						System.out.println("Exiting");
					default:
						System.out.println("Please choose an option");
						break;
				}
			}
		}
		else
		{
			System.out.println("Sorry wrong pin");
		}
	}

	public void uiMenu()
	{
		Scanner sc = new Scanner(System.in);
		int choice = 0;
		System.out.println("Choose one of the following options:");
		System.out.println("0: Show customer interface");
		System.out.println("1: Show bank teller");
		System.out.println("2: Set system date");
		System.out.println("3: Exit application");

		choice = sc.nextInt();

		switch (choice)
		{
			case 0:
				atm();
				break;
			case 1:
				bankTeller();
				break;
			case 2:
				System.out.println("What year? ");
				int y = sc.nextInt();
				System.out.println("What month? ");
				int m = sc.nextInt();
				System.out.println("What day? ");
				int da = sc.nextInt();
				setDate(y, m, da);
				break;
			case 3:
				System.out.println("Exiting");
				break;
			default:
				System.out.println("Please choose an option");
				break;
		}
	}

}
