package cs174a;// DO NOT REMOVE THIS IMPORT.
import cs174a.Testable.*;
import java.util.Scanner;
/**
 * This is the class that launches your application.
 * DO NOT CHANGE ITS NAME.
 * DO NOT MOVE TO ANY OTHER (SUB)PACKAGE.
 * There's only one "main" method, it should be defined within this Main class, and its signature should not be changed.
 */
public class Main
{
	/**
	 * Program entry point.
	 * DO NOT CHANGE ITS NAME.
	 * DON'T CHANGE THE //!### TAGS EITHER.  If you delete them your program won't run our tests.
	 * No other function should be enclosed by the //!### tags.
	 */
	//!### COMENZAMOS
	public static void main( String[] args )
	{

		App app = new App();                        // We need the default constructor of your App implementation.  Make sure such
													// constructor exists.
		String r = app.initializeSystem();          // We'll always call this function before testing your system.


		app.uiMenu();

		if( r.equals( "0" ) )
		{
			//app.exampleAccessToDB();                // Example on how to connect to the DB.
//			r = app.createTables();
//			System.out.println( r );

//			r = app.dropTables();
//			System.out.println( r );


//			// 2011-3-11 initialize data
//			r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "17431", 1200, "344151573", "Joe Pepsi", "3210 State St" );               ///done
//			System.out.println( r );
//			r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "54321", 21000, "212431965", "Hurryson Ford", "678 State St" );               ///done
//			System.out.println( r );
//			r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "12121", 1200, "207843218", "David Copperfill", "1357 State St" );               ///done
//			System.out.println( r );
//			r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "41725", 15000, "201674933", "George Brush", "5346 Foothill Av" );               ///done
//			System.out.println( r );
//			r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "93156", 2000000, "209378521", "Kelvin Costner", "Santa Cruz #3579" );               ///done
//			System.out.println( r );
//			r = app.createPocketAccount("53027", "12121", 50, "207843218");  /// done
//			System.out.println(r);
//			r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "43942", 1289, "361721022", "Alfred Hitchcock", "6667 El Colegio #40" );               ///done
//			System.out.println( r );
//			r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "29107", 34000, "209378521", "Kelvin Costner", "Santa Cruz #3579" );               ///done
//			System.out.println( r );
//			r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "19023", 2300, "412231856", "Cindy Laugher", "7000 Hollister" );               ///done
//			System.out.println( r );

			// For Pit wilson
//			r = app.createCustomer("43942", "400651982", "Pit Wilson", "911 State St");  /// done
//			System.out.println(r);
			// manually add the coowner for pit wilson
//			r = app.createPocketAccount("60413", "43942", 20, "400651982");  /// done
//			System.out.println(r);

//			r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "32156", 1000, "188212217", "Magic Jordon", "3852 Court Rd" );               ///done
//			System.out.println( r );
//			r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "76543", 8456, "212116070", "Li Kung", "2 People's Rd Beijin" );               ///done
//			System.out.println( r );
//			r = app.createPocketAccount("43947", "29107", 30, "207843218");  /// done
//			System.out.println(r);

			// For Fatal Castro
//			r = app.createCustomer("41725", "401605312", "Fatal Castro", "3756 La Cumbre Plaza");  /// done
//			System.out.println(r);
//			r = app.createPocketAccount("88888", "11111", 100, "666");  /// done
//			System.out.println(r);

			//------NOW SET NEW DATE START TRANSACTION

//			r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "11111", 2345, "666", "new", "3address" );               ///done
//			System.out.println( r );

//			r = app.topUp( "88888", 100 );
//			System.out.println( r );



//			java.sql.Date dt = app.getDate();
//			System.out.println(dt);

//			r = app.setDate(2011, 3, 1);     // done and tested correct output
//			System.out.println(r);

//			System.out.println(app.checkEndOfMonth(app.getDate()));
		}
	}
	//!### FINALIZAMOS
}
