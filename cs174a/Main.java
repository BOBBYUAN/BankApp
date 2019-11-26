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



		if( r.equals( "0" ) )
		{
			//app.exampleAccessToDB();                // Example on how to connect to the DB.
//			r = app.createTables();
//			System.out.println( r );

//			r = app.dropTables();
//			System.out.println( r );

			// Example tests.  We'll overwrite your Main.main() function with our final tests.
			r = app.listClosedAccounts();
			System.out.println( r );

			// Another example test.
//			r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "7", 1234.56, "12", "adfasdf", "Known" );
//			System.out.println( r );
//			r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "1221", 1234.56, "54", "adfasdf", "Known" );
//			System.out.println( r );
//			r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "78", 1234.56, "90", "adfasdf", "Known" );
//			System.out.println( r );

			r = app.deposit("34",10.00);
			System.out.println(r);
		}
	}
	//!### FINALIZAMOS
}
