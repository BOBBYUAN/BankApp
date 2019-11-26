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

/**
 * The most important class for your application.
 * DO NOT CHANGE ITS SIGNATURE.
 */
public class App implements Testable
{
	private OracleConnection _connection;                   // Example connection object to your DB.

	/**
	 * Default constructor.
	 * DO NOT REMOVE.
	 */
	App()
	{
		// TODO: Any actions you need.
		int choice = 0;
		String order;

		while (choice != 10) {

			Scanner sc = new Scanner(System.in);
			System.out.println("Bank Teller");
			System.out.println("1. Enter Check Transaction");
			System.out.println("2. Generate Monthly Statement");
			System.out.println("3. Listed Clost Accounts");
			System.out.println("4. Generate Government Drug and Tax Evasion Report (DTER)");
			System.out.println("5. Customer Report");
			System.out.println("6. Add Interest");
			System.out.println("7. Create Account");
			System.out.println("8. Delete Closed Accounts and Customers");
			System.out.println("9. Delete Transactions");
			System.out.println("10. Exit");

			System.out.println("Enter choice(1-10): ");

			choice = sc.nextInt();

			switch (choice) {
				case 1:
					order = "You just choose 1";
					break;
				case 2:
					order = "You just choose 2";
					break;
				case 3:
					order = "You just choose 3";
					break;
				case 4:
					order = "You just choose 3";
					break;
				case 5:
					order = "You just choose 3";
					break;
				case 6:
					order = "You just choose 3";
					break;
				case 7:
					order = "You just choose 3";
					break;
				case 8:
					order = "You just choose 3";
					break;
				case 9:
					order = "You just choose 3";
					break;
				case 10:
					order = "To Exit";
					break;

				default:
					order = "You didn't choose a option";


			}
			System.out.println("Here is your choice: " + order);
			System.out.print( "\n\n\n" );

		}




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
		return "0 it works!";
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
				preparedStatement.setString(4, "17127");
				preparedStatement.executeQuery();

				String sql2 = "insert into interestcheckingaccount (aid,cid,branch_name,balance,interest) values (?,?,?,?,?)";
				preparedStatement = _connection.prepareStatement(sql2);
				preparedStatement.setInt(1,Integer.parseInt(id));
				preparedStatement.setString(2, tin);
				preparedStatement.setString(3, "CHASE");
				preparedStatement.setDouble(4, initialBalance);
				preparedStatement.setFloat(5, 0.15f);
				preparedStatement.executeQuery();


				result =  "0 " + id + " INTEREST_CHECKING " + initialBalance + " " + tin;

			} else if (accountType == AccountType.STUDENT_CHECKING) {
				String sql1 = "insert into customer (cid,cname,address,pin) values (?,?,?,?)";
				PreparedStatement preparedStatement = _connection.prepareStatement(sql1);
				preparedStatement.setString(1,tin);
				preparedStatement.setString(2, name);
				preparedStatement.setString(3, address);
				preparedStatement.setString(4, "17127");
				preparedStatement.executeQuery();

				String sql2 = "insert into studentcheckingaccount (aid,cid,branch_name,balance,interest) values (?,?,?,?,?)";
				preparedStatement = _connection.prepareStatement(sql2);
				preparedStatement.setInt(1,Integer.parseInt(id));
				preparedStatement.setString(2, tin);
				preparedStatement.setString(3, "CHASE");
				preparedStatement.setDouble(4, initialBalance);
				preparedStatement.setFloat(5, 0.15f);
				preparedStatement.executeQuery();


				result =  "0 " + id + " STUDENT_CHECKING " + initialBalance + " " + tin;

			} else if (accountType == AccountType.SAVINGS) {
				String sql1 = "insert into customer (cid,cname,address,pin) values (?,?,?,?)";
				PreparedStatement preparedStatement = _connection.prepareStatement(sql1);
				preparedStatement.setString(1,tin);
				preparedStatement.setString(2, name);
				preparedStatement.setString(3, address);
				preparedStatement.setString(4, "17127");
				preparedStatement.executeQuery();

				String sql2 = "insert into savingaccount (aid,cid,branch_name,balance,interest) values (?,?,?,?,?)";
				preparedStatement = _connection.prepareStatement(sql2);
				preparedStatement.setInt(1,Integer.parseInt(id));
				preparedStatement.setString(2, tin);
				preparedStatement.setString(3, "CHASE");
				preparedStatement.setDouble(4, initialBalance);
				preparedStatement.setFloat(5, 0.15f);
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


			String s1 = "create table Customer(cid varchar(20),cname varchar(20),address varchar(20),pin varchar(20),primary key(cid))";
			String s2 = "create table Account(aid integer,cid varchar(20),branch_name varchar(20),balance decimal(13,2),interest float,primary key(aid),foreign key(cid) references Customer(cid))";
			String s3 = "create table StudentCheckingAccount(aid integer,cid varchar(20),branch_name varchar(20),balance decimal(13,2),interest float,primary key(aid),foreign key(cid) references Customer(cid))";
			String s4 = "create table InterestCheckingAccount(aid integer,cid varchar(20),branch_name varchar(20),balance decimal(13,2),interest float,primary key(aid),foreign key(cid) references Customer(cid))";
			String s5 = "create table SavingAccount(aid integer,cid varchar(20),branch_name varchar(20),balance decimal(13,2),interest float,primary key(aid),foreign key(cid) references Customer(cid))";
			String s6 = "create table Transaction(tid integer,cid varchar(20),aid integer,testId date,info varchar(20),type varchar(20),primary key(tid),foreign key(cid) references Customer(cid),foreign key(aid) references Account(aid))";

			statement.addBatch(s1);
			statement.addBatch(s2);
			statement.addBatch(s3);
			statement.addBatch(s4);
			statement.addBatch(s5);
			statement.addBatch(s6);
			statement.executeBatch();

			//ResultSet resultSet = statement.executeQuery("create table Customer(cid integer,cname varchar(20),address varchar(20),pin varchar(20),primary key(cid))" );
			//resultSet = statement.executeQuery("create table Account(aid integer,cid integer,branch_name varchar(20),balance decimal(13,2),interest float,primary key(aid),foreign key(cid) references Customer(cid))" );
			//resultSet = statement.executeQuery("create table CheckingAccount(aid integer,primary key(aid),foreign key(aid) references Account(aid) on delete cascade)" );
			//resultSet = statement.executeQuery("create table Transaction(tid integer,cid integer,aid integer,testId date,info varchar(20),type varchar(20),primary key(tid),foreign key(cid) references Customer(cid),foreign key(aid) references Account(aid))" );
			return "successfully creates the table!";
//			try( statement.executeBatch() )
//			{
//				return "successfully creates the table!";
////
//			}
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "not successful!";
		}
	}

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
			ResultSet resultSet = statement.executeQuery("drop table Transaction");
			resultSet = statement.executeQuery("drop table SavingAccount");
			resultSet = statement.executeQuery("drop table StudentCheckingAccount");
			resultSet = statement.executeQuery("drop table InterestCheckingAccount");
			resultSet = statement.executeQuery("drop table Account");
			resultSet = statement.executeQuery("drop table Customer");

			return "successfully drops the table!";

		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "not successful!";
		}
	}

}
