/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Retail {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Retail shop
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Retail(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Retail

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;


      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      String row = null;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
            if(i==1){
               row = String.format("%-25s",rsmd.getColumnName(i));
            }else{
               row += String.format("%-25s",rsmd.getColumnName(i));
            }
			}
         System.out.println(row);
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            if(i==1){
               row = String.format("%-25s",rs.getString (i).trim());
            }else{
               row += String.format("%-25s",rs.getString (i).trim());
            }
         System.out.println(row);
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Retail.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Retail esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Retail object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Retail (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            List<String> authorisedUserData = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUserData = LogIn(esql);break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUserData != null) {
              String authorisedUser = authorisedUserData.get(0).trim();
              String authorisedUserType = authorisedUserData.get(1).trim();
              boolean usermenu = true;

              while(usermenu) {
               if(authorisedUserType.equals("admin")){
                  adminMenu();
                  switch (readChoice()){
                     case 1: viewStores(esql, authorisedUser); break;
                     case 2: viewProducts(esql); break;
                     case 3: placeOrder(esql, authorisedUser); break;
                     case 4: viewRecentOrders(esql); break;
                     case 5: updateProduct(esql); break;
                     case 6: viewRecentUpdates(esql); break;
                     case 7: viewPopularProducts(esql); break;
                     case 8: viewPopularCustomers(esql); break;
                     case 9: placeProductSupplyRequests(esql); break;
                     case 10:adminViewUsers(esql);break;
                     case 11:adminViewProducts(esql);break;
                     case 12:adminUpdateUser(esql);break;
                     case 13:adminUpdateProduct(esql);break;
                     case 20: usermenu = false; break;
                     default : System.out.println("Unrecognized choice!"); break;
                    }
               }else if(authorisedUserType.equals("manager")){
                  manageMenu();
                  switch (readChoice()){
                     case 1: viewStores(esql, authorisedUser); break;
                     case 2: viewProducts(esql); break;
                     case 3: placeOrder(esql, authorisedUser); break;
                     case 4: viewRecentOrders(esql); break;
                     case 5: updateProduct(esql); break;
                     case 6: viewRecentUpdates(esql); break;
                     case 7: viewPopularProducts(esql); break;
                     case 8: viewPopularCustomers(esql); break;
                     case 9: placeProductSupplyRequests(esql); break;
                     case 20: usermenu = false; break;
                     default : System.out.println("Unrecognized choice!"); break;
                    }
               }else{
                  custMenu();
               switch (readChoice()){
                  case 1: viewStores(esql, authorisedUser); break;
                  case 2: viewProducts(esql); break;
                  case 3: placeOrder(esql, authorisedUser); break;
                  case 4: viewRecentOrders(esql); break;
                  case 20: usermenu = false; break;
                  default : System.out.println("Unrecognized choice!"); break;
                  }
               }
            }
         }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();
         System.out.print("\tEnter latitude: ");   
         String latitude = in.readLine();       //enter lat value between [0.0, 100.0]
         System.out.print("\tEnter longitude: ");  //enter long value between [0.0, 100.0]
         String longitude = in.readLine();
         
         String type="customer";

			String query = String.format("INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name, password, latitude, longitude, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser

   public static void custMenu(){
      System.out.println("\n\nMAIN MENU");
      System.out.println("---------");
      System.out.println("1. View Stores within 30 miles");
      System.out.println("2. View Product List");
      System.out.println("3. Place a Order");
      System.out.println("4. View 5 recent orders");
      System.out.println(".........................");
      System.out.println("20. Log out");
   }
   
   public static void manageMenu(){
      System.out.println("\n\nMAIN MENU");
      System.out.println("---------");
      System.out.println("1. View Stores within 30 miles");
      System.out.println("2. View Product List");
      System.out.println("3. Place a Order");
      System.out.println("4. View 5 recent orders");
      System.out.println("\n***MANAGER OPTIONS***");
      System.out.println("5. Update Product");
      System.out.println("6. View 5 recent Product Updates Info");
      System.out.println("7. View 5 Popular Items");
      System.out.println("8. View 5 Popular Customers");
      System.out.println("9. Place Product Supply Request to Warehouse");
      System.out.println(".........................");
      System.out.println("20. Log out");
   }
   public static void adminMenu(){
      System.out.println("\n\nMAIN MENU");
      System.out.println("---------");
      System.out.println("1. View Stores within 30 miles");
      System.out.println("2. View Product List");
      System.out.println("3. Place a Order");
      System.out.println("4. View 5 recent orders");
      System.out.println("\n***MANAGER OPTIONS***");
      System.out.println("5. Update Product");
      System.out.println("6. View 5 recent Product Updates Info");
      System.out.println("7. View 5 Popular Items");
      System.out.println("8. View 5 Popular Customers");
      System.out.println("9. Place Product Supply Request to Warehouse");
      System.out.println("\n***ADMIN OPTIONS***");
      System.out.println("10. View all Users");
      System.out.println("11. View all Products");
      System.out.println("12. Update a User");
      System.out.println("13. Update a Product");
      System.out.println(".........................");
      System.out.println("20. Log out");
   }


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static List<String> LogIn(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT userID, type FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
         List<List<String>> result = esql.executeQueryAndReturnResult(query);
	 if (result.size() > 0){
         List<String> user = result.get(0);
		   return user;
      }
      return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewStores(Retail esql, String user) {
      try{
         String query = String.format("SELECT S.storeID, S.name, calculate_distance(U.latitude, U.longitude, S.latitude, S.longitude) AS distance FROM Store S, Users U WHERE U.userID = '%s' AND calculate_distance(U.latitude, U.longitude, S.latitude, S.longitude)<= 30;", user);
         esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void viewProducts(Retail esql) {
      try{
         int store;
         do {
         System.out.print("\tEnter Store ID: ");
         try { // read the integer, parse it and break.
            store = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);

      String query = String.format("SELECT P.productName as Name, P.numberOfUnits as Qty, P.pricePerUnit as Unit_Price FROM Product P WHERE P.storeID = '%d';",store);
      esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void placeOrder(Retail esql, String user) {
      try{
         int store;
         String productName;
         int units;
         do { // StoreID input
            System.out.print("\tEnter Store ID: ");
            try { // read the integer, parse it and break.
               store = Integer.parseInt(in.readLine());
               break;
            }catch (Exception e) {
               System.out.println("Your input is invalid!");
               continue;
            }//end try
         }while (true);
         System.out.print("\tEnter Product Name: ");// Product name input
         productName = in.readLine();
         do { // Number of units input
            System.out.print("\tEnter Number of units: ");
            try { // read the integer, parse it and break.
               units = Integer.parseInt(in.readLine());
               break;
            }catch (Exception e) {
               System.out.println("Your input is invalid!");
               continue;
            }//end try
         }while (true);
         String q1 = String.format("SELECT S.storeID FROM Store S, Users U WHERE S.storeID = '%d' AND U.userID = '%s' AND calculate_distance(U.latitude, U.longitude, S.latitude, S.longitude)<= 30;", store, user);
         if(esql.executeQuery(q1)==0){
            System.out.println("The store does not exist or is too far!");
            return;
         }
         String q2 = String.format("SELECT storeID FROM Product WHERE storeID = '%d' AND productName = '%s' AND numberOfUnits>=%d;", store, productName, units);
         if(esql.executeQuery(q2)==0){
            System.out.println("The product does not exists or there is not enough stock!");
            return;
         }
         //Insert into Orders table
         String q3 = String.format("INSERT INTO Orders (customerID, storeID, productName, unitsOrdered, orderTime) VALUES (%s, %d, '%s', %d, now())", user, store, productName, units);
         esql.executeUpdate(q3);
         //Update Product table
         String q4 = String.format("UPDATE Product SET numberOfUnits = numberOfUnits - %d WHERE storeID = '%d' AND productName = '%s'", units, store, productName);
         esql.executeUpdate(q4);


      }
      catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void viewRecentOrders(Retail esql) {}
   public static void updateProduct(Retail esql) {}
   public static void viewRecentUpdates(Retail esql) {}
   public static void viewPopularProducts(Retail esql) {}
   public static void viewPopularCustomers(Retail esql) {}
   public static void placeProductSupplyRequests(Retail esql) {}

   public static void adminViewUsers(Retail esql) {
       try{
         String query = String.format("SELECT * FROM Users ORDER BY type,name;");
         esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void adminViewProducts(Retail esql) {
      try{
         String query = String.format("SELECT * FROM Product ORDER BY storeID,productName;");
         esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void adminUpdateUser(Retail esql) {}
   public static void adminUpdateProduct(Retail esql) {}
   
}//end Retail

