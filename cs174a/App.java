package cs174a;// You may have as many imports as you need.
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
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


/**
 * The most important class for your application.
 * DO NOT CHANGE ITS SIGNATURE.
 */
public class App implements Testable
{
	private OracleConnection _connection;                   // Example connection object to your DB.
	static String salt = "glennie-rousseva"; 				// for password encryption


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
		final String DB_USER = "c##grousseva";
		final String DB_PASSWORD = "8611311";

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

			uiMenu();

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
				preparedStatement.setString(3, "CHASE");
				preparedStatement.setDouble(4, initialBalance);
				preparedStatement.setString(5, "INTEREST_CHECKING");
				preparedStatement.setFloat(6, 0.15f);
				preparedStatement.setInt(7, 0);
				preparedStatement.executeQuery();


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
				preparedStatement.setString(3, "CHASE");
				preparedStatement.setDouble(4, initialBalance);
				preparedStatement.setString(5, "STUDENT_CHECKING");
				preparedStatement.setFloat(6, 0.15f);
				preparedStatement.executeQuery();


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
				preparedStatement.setString(3, "CHASE");
				preparedStatement.setDouble(4, initialBalance);
				preparedStatement.setString(5, "SAVINGS");
				preparedStatement.setFloat(6, 0.15f);
				preparedStatement.executeQuery();

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
			String s5 = "create sequence tid start with 1 increment by 1";

//			statement.addBatch(s1);
//			statement.addBatch(s2);
//			statement.addBatch(s3);
//			statement.addBatch(s4);
			statement.addBatch(s5);
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
			ResultSet resultSet = statement.executeQuery("drop table Owners");
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
			addTransaction("glen", "deposit", amount, accountId, null, null);

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
	public String createPocketAccount( String id, String linkedId, double initialTopUp, String tin)
	{
		String accountID = "";
		try( Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String sql = "select account.aid from account where account.cid = ? and " +
					"(account.type = 'INTEREST_CHECKING' or account.type = 'STUDENT_CHECKING' or account.type = 'SAVINGS') and " +
					"account.balance > 0.01";
			PreparedStatement preparedStatement = _connection.prepareStatement(sql);
			preparedStatement.setString(1, tin);
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
			preparedStatement.setString(3, "CHASE");
			preparedStatement.setDouble(4, initialTopUp);
			preparedStatement.setString(5, "POCKET");
			preparedStatement.setFloat(6, 0.00f);
			preparedStatement.setInt(7, 0);
			preparedStatement.setInt(8, Integer.parseInt(linkedId));
			preparedStatement.executeUpdate();
			System.out.println("SUCESS");

			return "0";
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	@Override
	public String setDate( int year, int month, int day)
	{
		// java months are 0 based, ex. if you want month date to be november, put 10 instead of 11
		Calendar cal = Calendar.getInstance();
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			cal.set(year, month, day);
			String d = Integer.toString(year) + "-" + Integer.toString(month)  + "-" + Integer.toString(day);
			cal.getTime();
			return "date is: " + d + "\n" + cal.getTime();
		}
		catch( SQLException e)
		{
			System.err.println( e.getMessage() );
			return "1";
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
			return "0 " + accountId + " " + name;
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
					double parentNewBal = parentBal - amount;

					if (parentNewBal <= 0.01)
					{
						System.out.println("Not enough funds");
						return "1";
					}
					else
					{
						double pocketNewBal = pocketBal + amount;

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

						addTransaction("glen", "tops up", amount, parentId, accountId, null);

					}

				}

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

	@Override
	public String payFriend(String from, String to, double amount ) // come back to fix cid check ownership
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			if (amount < 0)
			{
				System.out.println("Amount must be positive");
				return "1";
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
				System.out.println("Invalid source pocket account id");
				return "1";
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

				addTransaction("glen", "pays friend", amount, from, to, null);

			}
			else
			{
				System.out.println("Invalid destination pocket account id");
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
				System.out.println(newBal);

				if (newBal <= 0.01)
				{
					System.out.println("Account closed"); /// come back to fix
					return "1";
				}

				String update = "update account set balance = ? where aid = ?";
				PreparedStatement preparedUpdateStatement = _connection.prepareStatement(update);
				preparedUpdateStatement.setDouble(1, newBal);
				preparedUpdateStatement.setString(2, accountId);
				preparedUpdateStatement.executeUpdate();

				addTransaction("glen", "withdrawal", amount, accountId, null, null);
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

				if (newBal <= 0.01)
				{
					System.out.println("Account closed"); /// come back to fix
					return "1";
				}

				String update = "update account set balance = ? where aid = ?";
				PreparedStatement preparedUpdateStatement = _connection.prepareStatement(update);
				preparedUpdateStatement.setDouble(1, newBal);
				preparedUpdateStatement.setString(2, accountId);
				preparedUpdateStatement.executeUpdate();

				addTransaction("glen", "purchases", amount, accountId, null, null);

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
			if (Integer.parseInt(resultSet.getString(2)) == 1)
			{
				System.out.println("Customer must be a primary account owner on source account to do transfer");
				return "1";
			}


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
			if (Integer.parseInt(resultSet.getString(2)) == 0)
			{
				System.out.println("Customer must be a co owner on destination account to do transfer");
				return "1";
			}


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

				addTransaction("glen", "trasnfers", amount, from, to, null);

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

				addTransaction("glen", "collects", amount, pocketId, parentAccountId, null);

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

						addTransaction("glen", "wires", amount, from, to, null);
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
			System.out.println("HELLOE");

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

						addTransaction("glen", "writes a check", amount, accountId, null, checkN);

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
				System.out.println(dbPin.equals(checkEncryption));
				System.out.println(dbPin);
				System.out.println(checkEncryption);
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

	public String addTransaction(String customerName, String trans_type, double amount, String from, String to, String checkNumber)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{

			String sql = "insert into transaction(tid, cname, tdate, ttype, amount, from_aid, to_aid, check_number) " +
					"values(tid.nextval, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement p = _connection.prepareStatement(sql);
			p.setString(1, customerName);
			p.setString(2, "21-NOV-19");
			p.setString(3, trans_type);
			p.setDouble(4, amount);
			p.setString(5, from);
			p.setString(6, to);
			p.setString(7, checkNumber);
			p.executeUpdate();
			return "0";
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	public String deleteTransactions()
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			// check if its end of the month?
			String sql = "truncate table transaction";
			PreparedStatement p = _connection.prepareStatement(sql);
			p.executeUpdate();
			return "0";
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
//
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
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			String s = "select cid from owners where cid = ? and primary = 0";
			PreparedStatement ps = _connection.prepareStatement(s);
			ps.setString(1, customerId);
			ResultSet resultSet = ps.executeQuery();

			ArrayList<String> aids = new ArrayList<String>();

			if(!resultSet.next())
			{
				System.out.println("Sorry you do not have this privilege. You must be a primary owner of some account to generate a monthly report.");
				return "1";
			}
			else
			{
				s = "select aid from owners where cid = ?";
				ps = _connection.prepareStatement(s);
				ps.setString(1, customerId);
				resultSet = ps.executeQuery();

				while (resultSet.next())
				{
					aids.add(resultSet.getString(1));
				}

				for (String aid: aids)
				{
					printReport(aid);
				}
			}
		return "0";
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
	}

	public String printReport(String accountId)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			System.out.println("-----------------------------");
			System.out.println("Report for account: " + accountId);
			String s = "select * from transaction where from_aid = ? or to_aid = ? order by tdate, ttype";
			PreparedStatement ps = _connection.prepareStatement(s);
			ps.setString(1, accountId);
			ps.setString(2, accountId);
			ResultSet resultSet = ps.executeQuery();

			String d;
			String name;
			String type;
			double amount;
			String from = "";
			String to = "";
			int check_numer;

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
							break;
						case "tops up":
							System.out.println(d + " " + name + " top-ups " + amount + " to account " + to + " from account " + from);
							break;
						case "withdrawal":
							System.out.println(d + " " + name + " withdraws " + amount + " from account " + from);
							break;
						case "purchases":
							System.out.println(d + " " + name + " purchases " + amount + " from account " + from);
							break;
						case "writes a check":
							System.out.println(d + " " + name + " writes a check in amount of " + amount + " from account " + from);
							break;
						case "collects":
							System.out.println(d + " " + name + " collects " + amount + " from account " + from + " to account " + to);
							break;
						case "trasnfers":
							System.out.println(d + " " + name + " transfers " + amount + " from account " + from + " to account " + to);
							break;
						case "pays friend":
							System.out.println(d + " " + name + " pay-friends " + amount + " from account " + from + " to account " + to);
							break;
						case "wires":
							System.out.println(d + " " + name + " wires " + amount + " from account " + from + " to account " + to);
							break;
						default:
							System.out.println("UNKNOWN TRANSACTION TYPE!!");
							break;
					}
				}
			}
			System.out.println("-----------------------------");
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

			Scanner sc = new Scanner(System.in);
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
					System.out.print("Enter amount: ");
					double a = sc.nextDouble();
					writeCheck(accId, a);
					break;
				case 2:
					System.out.print("Enter account id: ");
					accId = sc.next();
					generateMonthlyStatement(accId);  // to do
					break;
				case 3:
					listClosedAccounts();
					break;
				case 4:
					System.out.print("Enter account id: ");
					accId = sc.next();
					System.out.println("Generate Government Drug and Tax Evasion Report (DTER)"); // to do
					break;
				case 5:
					System.out.print("Enter tax identification number: ");
					String tid = sc.next();
					getCustomerReport(tid);
					break;
				case 6:
					System.out.print("Enter account id: ");
					accId = sc.next();
					System.out.println("Add interest"); // to do
					break;
				case 7:
					System.out.println("Select what type of account you would like to create: ");
					System.out.println("1. Interest Checking");
					System.out.println("2. Student Checking");
					System.out.println("3. Savings");
					System.out.println("4. Pocket");

					int choice2 = sc.nextInt();


					System.out.print("What is your tax identification number? ");
					tid = sc.next();

					System.out.print("Please write a 5 digit account id number to be associated with your account: ");
					String aid = sc.next();


					switch (choice2)
					{
						case 1:
							System.out.print("What is your name? ");
							String n = sc.next();
							System.out.print("What is your address? ");
							String add = sc.next();

							createCheckingSavingsAccount(AccountType.INTEREST_CHECKING, aid, 1000, tid, n, add);
							break;
						case 2:
							System.out.print("What is your name? ");
							n = sc.next();
							System.out.print("What is your address? ");
							add = sc.next();

							createCheckingSavingsAccount(AccountType.STUDENT_CHECKING, aid, 1000, tid, n, add);
							break;
						case 3:
							System.out.print("What is your name? ");
							n = sc.next();
							System.out.print("What is your address? ");
							add = sc.next();

							createCheckingSavingsAccount(AccountType.SAVINGS, aid, 1000, tid, n, add);
							break;
						case 4:
							System.out.print("What checking/savings account id do you wish to link your pocket account to? ");
							String linked = sc.next();
							createPocketAccount(aid, linked, 50, tid);
							break;
					}
					break;
				case 8:
					System.out.println("Deleting closed accounts and customers.");
					// deleteClosedAccountsAndCustomers()
					break;
				case 9:
					System.out.println("Deleting transactions.");
					deleteTransactions();
					break;
				case 10:
					selected = "Exiting BankTeller!";
					break;
				default:
					selected = "You didn't choose a option";
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
		String tid = sc.next();
		//check if user exists
		System.out.println("Please enter pin: ");
		String pin = sc.next();
		if (verifyPin(tid, pin))
		{
			System.out.println("YES");
			System.out.println("Choose one of the following options:");
			System.out.println("1: Deposit");
			System.out.println("2: Top-Up");
			System.out.println("3: Withdrawal");
			System.out.println("4: Purchase");
			System.out.println("5: Transfer");
			System.out.println("6: Collect");
			System.out.println("7: Pay-Friend");
			System.out.println("8: Wire");
			System.out.println("9: Exit ATM");

			choice = sc.nextInt();
			switch(choice)
			{
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
					transfer(tid, from, to, a);
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
					wire(tid, from, to, a);
					break;

				default:
					System.out.println("Please choose an option");
					break;
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
				System.out.println("Set date");
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
