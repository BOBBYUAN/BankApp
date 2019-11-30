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
				preparedStatement.setString(4, "17127");
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


			String s1 = "create table Customer(" +
					"cid varchar(20)," +
					"cname varchar(20)," +
					"address varchar(20)," +
					"pin varchar(20)," +
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
					"tate date, " +
					"ttype varchar(20)," +
					"tid integer, " +
					"amount decimal(13, 2), " +
					"ttype varchar(20), " +
					"fee decimal(13,2), " +
					"check_number integer, " +
					"balance decimal(13, 2), " +
					"primary key(tid))";
			String s4 = "create table Owners(" +
					"	cid varchar(20), " +
					"aid integer, " +
					"primary integer default 0," +
					"primary key(cid, aid, primary), " +
					"foreign key(cid) references Customer(cid) on delete cascade," +
					"foreign key(aid) references Account(aid) on delete cascade)";

			statement.addBatch(s1);
			statement.addBatch(s2);
			statement.addBatch(s3);
			statement.addBatch(s4);
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
			preparedStatement.executeQuery();
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

			if (resultSet.next())
			{
				String createCustomer = "insert into Customer(cid, cname, address, pin) values(?, ?, ?, 1717)";
				PreparedStatement preparedUpdateStatement = _connection.prepareStatement(createCustomer);
				preparedUpdateStatement.setString(1, tin);
				preparedUpdateStatement.setString(2, name);
				preparedUpdateStatement.setString(3, address);
//				preparedUpdateStatement.setString(4, "1717"); ///come back to encrypt
				preparedUpdateStatement.executeUpdate();
			}
			else
			{
				System.out.println("Unable to create customer because account specified is closed");
				return "1";
			}
			return "0" + accountId + "" + name;
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

				System.out.println("PARENTID: " + parentId);

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
			insert.executeQuery();

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
			insert.executeQuery();

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
			}

		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			return "1";
		}
		return "0";
	}

	public String collect(String pocketId, String parentAccountId, double amount)
	{
		try(Statement statement = _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) )
		{
			double fee = 0.03 * amount;

			String sql = "select balance, status from account where aid = ? and pocket_linked_to = ? and type = 'POCKET'";
			PreparedStatement p = _connection.prepareStatement(sql);
			p.setString(1, pocketId);
			p.setString(2, parentAccountId);
			ResultSet resultSet = p.executeQuery();
			double parentBal;
			double pocketBal;

			if (resultSet.next())
			{
				int status = Integer.parseInt(resultSet.getString(2));
				if (status == 1)
				{
					System.out.println("Pocket account is closed");
					return "1";
				}

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
			String sql = "select aid from account where ttype = 'INTEREST_CHECKING' or account.type = 'STUDENT_CHECKING' or account.type = 'SAVINGS')";
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
			String sql = "select aid from account where aid = ? and type = 'INTEREST_CHECKING' or account.type = 'STUDENT_CHECKING'";
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
				checkNumber = Integer.parseInt(resultSet.getString(1)) + 1;
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
						// generated check number to store in transaction
						System.out.println(checkNumber);
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
}
