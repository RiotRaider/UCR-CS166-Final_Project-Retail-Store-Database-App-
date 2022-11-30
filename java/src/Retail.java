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
import java.util.Scanner;

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
   String userID;
   String userType;

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
              esql.userID=authorisedUserData.get(0).trim();
              esql.userType = authorisedUserData.get(1).trim();
              boolean usermenu = true;

              while(usermenu) {
               if(esql.userType.equals("admin")){
                  adminMenu();
                  switch (readChoice()){
                     case 1: viewStores(esql);printWait(); break;
                     case 2: viewProducts(esql); break;
                     case 3: placeOrder(esql); break;
                     case 4: viewRecentOrders(esql); break;
                     case 5: adminViewUsers(esql);break;
                     case 6: adminViewProducts(esql);break;
                     case 7: adminUpdateUser(esql);break;
                     case 8: adminUpdateProduct(esql);break;
                     case 9: viewRecentUpdates(esql); break;
                     case 10: viewPopularProducts(esql); break;
                     case 11: viewPopularCustomers(esql); break;
                     case 12: placeProductSupplyRequests(esql); break;
                     case 13: viewStoreOrders(esql);break;
                     case 20: usermenu = false; break;
                     default : System.out.println("Unrecognized choice!"); break;
                    }
               }else if(esql.userType.equals("manager")){
                  manageMenu();
                  switch (readChoice()){
                     case 1: viewStores(esql);printWait(); break;
                     case 2: viewProducts(esql); break;
                     case 3: placeOrder(esql); break;
                     case 4: viewRecentOrders(esql); break;
                     case 5: updateProduct(esql); break;
                     case 6: viewRecentUpdates(esql); break;
                     case 7: viewPopularProducts(esql); break;
                     case 8: viewPopularCustomers(esql); break;
                     case 9: placeProductSupplyRequests(esql); break;
                     case 10: viewStoreOrders(esql);break;
                     case 20: usermenu = false; break;
                     default : System.out.println("Unrecognized choice!"); break;
                    }
               }else{
                  custMenu();
               switch (readChoice()){
                  case 1: viewStores(esql);printWait(); break;
                  case 2: viewProducts(esql); break;
                  case 3: placeOrder(esql); break;
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
         int latitude; // latitude must be between [0, 100]
         do {
            System.out.print("\tEnter latitude: ");
            try { 
               latitude = Integer.parseInt(in.readLine());
               if (!(latitude<0 || latitude>100))
                  break;
               System.out.println("\tLatitude must be a value between 0 and 100!");
            }catch (Exception e) {
               System.out.println("\tYour input is invalid!");
               continue;
            }
         }while (true);
         int longitude;// longitude must be between [0, 100]
         do {
            System.out.print("\tEnter longitude: ");
            try { 
               longitude = Integer.parseInt(in.readLine());
               if (!(longitude<0 || longitude>100))
                  break;
               System.out.println("\tLongitude must be a value between 0 and 100!");
            }catch (Exception e) {
               System.out.println("\tYour input is invalid!");
               continue;
            }//end try
         }while (true);
         
         String type="customer";

			String query = String.format("INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %d, %s,'%s')", name, password, latitude, longitude, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser

   //Function to call to wait for user to prompt to continue
   //Use to let user view data sets before new prompts or menus print
   public static void printWait(){
      System.out.print("\n\nPress <ENTER>...");
      Scanner s = new Scanner(System.in);
      s.nextLine();
   }
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
      System.out.println("10.View Orders at Store");
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
      System.out.println("\n***ADMIN OPTIONS***");
      System.out.println("5. View all Users");
      System.out.println("6. View all Products");
      System.out.println("7. Update a User");
      System.out.println("8. Update a Product");
      System.out.println("9. View 5 recent Product Updates Info");
      System.out.println("10. View 5 Popular Items");
      System.out.println("11. View 5 Popular Customers");
      System.out.println("12. Place Product Supply Request to Warehouse");
      System.out.println("13.View Orders at Store");
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
      System.out.println("Invalid Login...");
      return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewStores(Retail esql) {//View Stores 30 miles or less from logged in Customer
      try{
         String query = String.format("SELECT S.storeID, S.name, calculate_distance(U.latitude, U.longitude, S.latitude, S.longitude) AS distance FROM Store S, Users U WHERE U.userID = '%s' AND calculate_distance(U.latitude, U.longitude, S.latitude, S.longitude)<= 30;", esql.userID);
         System.out.println();
         esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void viewProducts(Retail esql) {//View Products available at any store
      try{
         int store=0;
         String query = String.format("SELECT storeID, name, dateestablished FROM Store;");
         System.out.println();
         esql.executeQueryAndPrintResult(query);
         do {
            System.out.print("\tEnter Store ID: ");
            try { // read the integer, parse it and break.
               store = Integer.parseInt(in.readLine());
               query = String.format("SELECT * FROM Store WHERE storeID = %d;", store);
               if(esql.executeQuery(query)==0){
                  System.out.println("The store does not exist");
               }else{break;}
            }catch (Exception e) {
               System.out.println("Your input is invalid!");
               continue;
            }//end try
         }while (true);
      query = String.format("SELECT P.productName as Name, P.numberOfUnits as Qty, P.pricePerUnit as Unit_Price FROM Product P WHERE P.storeID = '%d';",store);
      System.out.println();
      esql.executeQueryAndPrintResult(query);
      printWait();
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void placeOrder(Retail esql) {//Place an order from a store within range
      try{
         int store=0;
         String productName;
         int units;
         String query;
         do { // StoreID input
            viewStores(esql);
            System.out.print("\tEnter Store ID: ");
            try { // read the integer, parse it and break.
               store = Integer.parseInt(in.readLine());
               query = String.format("SELECT S.storeID FROM Store S, Users U WHERE S.storeID = '%d' AND U.userID = '%s' AND calculate_distance(U.latitude, U.longitude, S.latitude, S.longitude)<= 30;", store, esql.userID);
               if(esql.executeQuery(query)==0){
                  System.out.println("The store does not exist or is too far!");
                  continue;
               }
               break;
            }catch (Exception e) {
               System.out.println("Your input is invalid!");
               continue;
            }//end try
         }while (true);
         do{ //Product name input
            query = String.format("SELECT ProductName, numberofunits as Qty_Available FROM Product WHERE storeID = %d;", store);
            System.out.println();
            esql.executeQueryAndPrintResult(query);
            while(true){
               System.out.print("\tEnter Product Name: ");
               productName = in.readLine().trim();
               query = String.format("SELECT * FROM Product WHERE storeID = %d AND productName = '%s';",store,productName);
               if(esql.executeQuery(query)==0){
                  System.out.println("Invalid Product...");
               }else{break;}
            }
            System.out.print("\tEnter Number of units: ");
            try { // read the integer, parse it and break.
               units = Integer.parseInt(in.readLine());
            }catch (Exception e) {
               System.out.println("Your input is invalid!");
               continue;
            }//end try
            query = String.format("SELECT storeID FROM Product WHERE storeID = '%d' AND productName = '%s' AND numberOfUnits>=%d;", store, productName, units);
            if(esql.executeQuery(query)==0){
               System.out.println("The product does not exists or there is not enough stock!");
               continue;
            }
            break;
         }while(true);
         
         //Insert into Orders table
         String q3 = String.format("INSERT INTO Orders (customerID, storeID, productName, unitsOrdered, orderTime) VALUES (%s, %d, '%s', %d, DATE_TRUNC('second', CURRENT_TIMESTAMP::timestamp))", esql.userID, store, productName, units);
         esql.executeUpdate(q3);
         //Update Product table
         String q4 = String.format("UPDATE Product SET numberOfUnits = numberOfUnits - %d WHERE storeID = '%d' AND productName = '%s'", units, store, productName);
         esql.executeUpdate(q4);
         //Feedback to user
         System.out.println("\nOrder Submitted...");
         query = String.format("SELECT * FROM Orders WHERE customerID=%s ORDER BY orderNumber DESC LIMIT 1;",esql.userID);
         esql.executeQueryAndPrintResult(query);
         printWait();
      }
      catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void viewRecentOrders(Retail esql) {//View your recent orders
      try{
         String query = String.format("SELECT S.storeID, S.name, O.productName, O.unitsOrdered, O.orderTime FROM Store S, Orders O WHERE '%s' = O.customerID AND O.storeID = S.storeID ORDER BY O.orderTime desc LIMIT 5;", esql.userID);
         System.out.println();
         esql.executeQueryAndPrintResult(query);
         printWait();
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void updateProduct(Retail esql) {//Managers and Admins can update Product Qty Issues
      try{
         String[] values = {null,null,null,null};
         List<String> validProduct=null;
         String query = String.format("SELECT * FROM Product P WHERE P.storeID IN (SELECT S.storeID FROM Store S WHERE S.managerID = %s) ORDER BY P.storeID;", esql.userID);
         System.out.println();
         esql.executeQueryAndPrintResult(query);
         do{
            try{
               System.out.print("\tEnter Store ID: ");
               values[0] = in.readLine().trim();
               query = String.format("Select * FROM Store WHERE storeID= %s AND managerID = %s;", values[0],esql.userID);
               if(esql.executeQuery(query)== 0)
                  System.out.format("Invalid Store Choice! Please select a store where you manage\n", esql.userID);
               else{break;}
            }catch (Exception e) {
               System.out.println("Your input is invalid!");
               continue;
            }
         }while(true);
         do{
            try{
               System.out.print("\tEnter Product Name: ");
               values[1] = in.readLine().trim();
               query = String.format("Select * FROM Product WHERE productName = '%s' AND storeID = %s;", values[1], values[0]);
               if(esql.executeQuery(query)== 0){
                  System.out.format("Product '%s' does not exist at Store %s! Please select valid product\n", values[1], values[0]);
               }else{break;}
            }catch (Exception e) {
               System.out.println("Your input is invalid!");
               continue;
            }
         }while(true);

         validProduct=esql.executeQueryAndReturnResult(query).get(0);
         System.out.format("Current Product : %s at Store %s\n",validProduct.get(1).trim(),validProduct.get(0).trim());
         System.out.format("Current Quantity: %s\nNew Quantity (Press Enter to keep current): ", validProduct.get(2));
         values[2] = in.readLine();
         if(values[2].equals("")){
            values[2] = validProduct.get(2);
         }
         System.out.format("Current Unit Price: %s\nNew Unit Price (Press Enter to keep current): ", validProduct.get(3));
         values[3] = in.readLine();
         if(values[3].equals("")){
            values[3] = validProduct.get(3);
         }
         System.out.println("\nOriginal Product Info:");
         esql.executeQueryAndPrintResult(query);
         String update = String.format("UPDATE Product SET numberOfUnits = %s , pricePerUnit = %s WHERE storeID = %s AND productName = '%s';", values[2],values[3],values[0],values[1]);
         esql.executeUpdate(update);
         System.out.println("\nUpdated Product Info:");
         esql.executeQueryAndPrintResult(query);
         query = String.format("INSERT INTO ProductUpdates (managerID,storeID,productName,updatedOn) VALUES ( %s, %s, '%s', DATE_TRUNC('second', CURRENT_TIMESTAMP::timestamp));", esql.userID, values[0], values[1]);
         esql.executeUpdate(query);
         printWait();
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void viewRecentUpdates(Retail esql) {//View Recent Product Updates at store you manage
      try{
         String store;
         String query;
         if(esql.userType.equals("admin")){
            query = String.format("SELECT storeID, name, dateestablished FROM Store;");
            System.out.println();
            esql.executeQueryAndPrintResult(query);
            do{
               try{
                  System.out.print("\tEnter Store ID: ");
                  store = in.readLine().trim();
                  query = String.format("Select * FROM Store WHERE storeID= %s;",store,esql.userID);
                  if(esql.executeQuery(query) == 0){
                     System.out.format("Invalid Store Choice! Please select a valid store\n",esql.userID);
                  }else{break;}
               }catch (Exception e) {
                  System.out.println("Your input is invalid!");
                  continue;
               }
            }while(true);
         }else{
            query = String.format("SELECT storeID, name, dateestablished FROM Store WHERE managerID = %s;", esql.userID);
            System.out.println();
            esql.executeQueryAndPrintResult(query);
            do{
               try{
                  System.out.print("\tEnter Store ID: ");
                  store = in.readLine().trim();
                  query = String.format("Select * FROM Store WHERE storeID= %s AND managerID = %s;", store,esql.userID);
                  if(esql.executeQuery(query)== 0)
                     System.out.format("Invalid Store Choice! Please select a store where you manage\n", esql.userID);
                  else{break;}
               }catch (Exception e) {
                  System.out.println("Your input is invalid!");
                  continue;
               }
            }while(true);
         }
         query = String.format("SELECT * FROM ProductUpdates WHERE storeID = %s ORDER BY updateNumber DESC LIMIT 5;",store);
         System.out.println();
         esql.executeQueryAndPrintResult(query);
         printWait();
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
      
   }
   public static void viewPopularProducts(Retail esql) {//View the most popular products in a store
      try{
         String store;
         String query;
         String q1;
         if(esql.userType.equals("admin")){
            query = String.format("SELECT storeID, name, dateestablished FROM Store;");
            System.out.println();
            esql.executeQueryAndPrintResult(query);
            do{
               try{
                  System.out.print("\tEnter Store ID: ");
                  store = in.readLine().trim();
                  query = String.format("Select * FROM Store WHERE storeID= %s;",store,esql.userID);
                  if(esql.executeQuery(query) == 0){
                     System.out.format("Invalid Store Choice! Please select a valid store\n",esql.userID);
                  }else{break;}
               }catch (Exception e) {
                  System.out.println("Your input is invalid!");
                  continue;
               }
            }while(true);
         }else{
            query = String.format("SELECT storeID, name, dateestablished FROM Store WHERE managerID = %s;", esql.userID);
            System.out.println();
            esql.executeQueryAndPrintResult(query);
            do{
               try{
                  System.out.print("\tEnter Store ID: ");
                  store = in.readLine().trim();
                  query = String.format("Select * FROM Store WHERE storeID= %s AND managerID = %s;", store,esql.userID);
                  if(esql.executeQuery(query)== 0)
                     System.out.format("Invalid Store Choice! Please select a store where you manage\n", esql.userID);
                  else{break;}
               }catch (Exception e) {
                  System.out.println("Your input is invalid!");
                  continue;
               }
            }while(true);
         }
         query = String.format("SELECT O.productName, COUNT(*) as NumOfOrders FROM Orders O WHERE O.storeID ='%s' GROUP BY O.productName ORDER BY COUNT(*) DESC LIMIT 5;", store);
         System.out.println();
         esql.executeQueryAndPrintResult(query);
         printWait();
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void viewPopularCustomers(Retail esql) {//View the most popular customers across all the manager's stores
      try{
         int valid = 0;
         String store;
         String query;
         String q1;
         if(esql.userType.equals("admin")){
            query = String.format("SELECT storeID, name, dateestablished FROM Store;");
            System.out.println();
            esql.executeQueryAndPrintResult(query);
            do{
               try{
                  System.out.print("\tEnter Store ID: ");
                  store = in.readLine().trim();
                  query = String.format("Select * FROM Store WHERE storeID= %s;",store,esql.userID);
                  if(esql.executeQuery(query) == 0){
                     System.out.format("Invalid Store Choice! Please select a valid store\n",esql.userID);
                  }else{break;}
               }catch (Exception e) {
                  System.out.println("Your input is invalid!");
                  continue;
               }
            }while(true);
         }else{
            query = String.format("SELECT storeID, name, dateestablished FROM Store WHERE managerID = %s;", esql.userID);
            System.out.println();
            esql.executeQueryAndPrintResult(query);
            do{
               try{
                  System.out.print("\tEnter Store ID: ");
                  store = in.readLine().trim();
                  query = String.format("Select * FROM Store WHERE storeID= %s AND managerID = %s;", store,esql.userID);
                  if(esql.executeQuery(query)== 0)
                     System.out.format("Invalid Store Choice! Please select a store where you manage\n", esql.userID);
                  else{break;}
               }catch (Exception e) {
                  System.out.println("Your input is invalid!");
                  continue;
               }
            }while(true);
         }
         query = String.format("SELECT O.customerID, U.name, COUNT(*) as NumOfOrders FROM Orders O, Users U WHERE O.storeID='%s' AND O.customerID=U.userID GROUP BY O.customerID, U.name ORDER BY COUNT(*) DESC LIMIT 5;", store);
         System.out.println();
         esql.executeQueryAndPrintResult(query);
         printWait();
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void placeProductSupplyRequests(Retail esql) {//Place Supply Requests for a store you manage
      try{
         int valid = 0;
         int qty = 0;
         String query;
         String[] values = {null,null,null};
         query = String.format("SELECT * FROM Warehouse ORDER BY warehouseID;");
         System.out.println();
         esql.executeQueryAndPrintResult(query);
         do{
            System.out.print("\tEnter Warehouse ID: ");
            values[0] = in.readLine().trim();
            query = String.format("SELECT * FROM Warehouse WHERE warehouseID= %s;",values[0]);
            valid = esql.executeQuery(query);
            if(valid == 0)
               System.out.format("Invalid Choice! Please select a warehouse\n",esql.userID);
         }while(valid<=0);
         valid=0;
         if(esql.userType.equals("admin")){
            query = String.format("SELECT storeID, name, dateestablished FROM Store;");
            System.out.println();
            esql.executeQueryAndPrintResult(query);
            do{
               System.out.print("\tEnter Store ID: ");
               values[1] = in.readLine().trim();
               query = String.format("SELECT * FROM Store WHERE storeID= %s;",values[1]);
               valid = esql.executeQuery(query);
               if(valid == 0)
                  System.out.format("Invalid Store Choice! Please select a valid store\n");
            }while(valid<=0);
         }else{
            query = String.format("SELECT storeID, name, dateestablished FROM Store WHERE managerID=%s;", esql.userID);
            System.out.println();
            esql.executeQueryAndPrintResult(query);
            do{
               System.out.print("\tEnter Store ID: ");
               values[1] = in.readLine().trim();
               query = String.format("SELECT * FROM Store WHERE storeID= %s AND managerID = %s;",values[1],esql.userID);
               valid = esql.executeQuery(query);
               if(valid == 0)
                  System.out.format("Invalid Store Choice! Please select a store you manage\n",esql.userID);
            }while(valid<=0);
         }
         valid=0;
         query = String.format("SELECT productName, numberOfUnits FROM Product WHERE storeID=%s ORDER BY productName;", values[1]);
         System.out.println();
         esql.executeQueryAndPrintResult(query);
         do{
            System.out.print("\tEnter Product Name: ");
            values[2] = in.readLine().trim();
            query = String.format("Select * FROM Product WHERE storeID= %s AND productName = '%s';",values[1],values[2]);
            valid = esql.executeQuery(query);
            if(valid == 0)
               System.out.format("Invalid Choice! Please select a valid product\n",esql.userID);}while(valid<=0);
         valid=0;
         do{ 
            System.out.print("\tEnter Quantity: ");
            try { 
               qty = Integer.parseInt(in.readLine());
               valid=1;
               break;
            }catch (Exception e) {
               System.out.println("Enter a valid amount!");
               continue;
            }
         }while(valid<=0);
         int curQty = Integer.parseInt(esql.executeQueryAndReturnResult(String.format("SELECT numberOfUnits FROM Product WHERE storeID =%s AND productName = '%s'", values[1],values[2])).get(0).get(0));
         System.out.println("Update Product");
         query = String.format("UPDATE Product SET numberOfUnits = %s WHERE storeID = %s AND productName = '%s';", (qty+curQty),values[1],values[2]);
         esql.executeUpdate(query);
         System.out.println("Insert Product Request");
         query = String.format("INSERT INTO ProductSupplyRequests(managerID,warehouseID,storeID,productName,unitsRequested) VALUES (%s, %s, %s, '%s', %d); ",esql.userID, values[0],values[1],values[2],qty);
         esql.executeUpdate(query);
         System.out.println();
         System.out.println("Order Submitted...");
         query = String.format("SELECT * FROM ProductSupplyRequests ORDER BY requestNumber DESC LIMIT 1;");
         esql.executeQueryAndPrintResult(query);
         printWait();
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void viewStoreOrders(Retail esql){//View orders at a store you manage
      try{
         String query;
         String store;
         int valid=0;
         if(esql.userType.equals("admin")){
            query = String.format("Select storeID, name, dateestablished FROM Store;");
            System.out.println();
            esql.executeQueryAndPrintResult(query);
            do{
               System.out.println("\tSelect Store ID:");
               store = in.readLine().trim();
               query = String.format("Select * FROM Store WHERE storeID= %s;", store);
               valid = esql.executeQuery(query);
               if(valid == 0)
                  System.out.format("Invalid Store Choice! Please select a valid store\n");
            }while(valid <=0);
         }else{
            query = String.format("Select storeID, name, dateestablished FROM Store WHERE managerID = %s;",esql.userID);
            System.out.println();
            esql.executeQueryAndPrintResult(query);
            do{
               System.out.println("\tSelect Store ID:");
               store = in.readLine().trim();
               query = String.format("Select * FROM Store WHERE storeID= %s AND managerID = %s;", store,esql.userID);
               valid = esql.executeQuery(query);
               if(valid == 0)
                  System.out.format("Invalid Store Choice! Please select a store where User %s is Manager\n", esql.userID);
            }while(valid <=0);
         }
         query = String.format("SELECT * FROM Orders WHERE storeID = %s ORDER BY ordertime DESC;", store);
         System.out.println();
         esql.executeQueryAndPrintResult(query);
         printWait();
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void adminViewUsers(Retail esql) {//Admins can view all registered users
       try{
         String query = String.format("SELECT * FROM Users ORDER BY type,name;");
         System.out.println();
         esql.executeQueryAndPrintResult(query);
         printWait();
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void adminViewProducts(Retail esql) {//Admins can view all products at all stores
      try{
         String query = String.format("SELECT * FROM Product ORDER BY storeID,productName;");
         System.out.println();
         esql.executeQueryAndPrintResult(query);
         printWait();
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void adminUpdateUser(Retail esql) {//Admins can make changes to user information
      try{
         int user = 0;
         String[] values = {null,null,null,null,null};
         List<List<String>> result = null;
         List<String> validUser=null;
         System.out.print("\tEnter User ID: ");
         user = Integer.parseInt(in.readLine());
         String query = String.format("Select * FROM Users WHERE userID = '%d';", user);
         result = esql.executeQueryAndReturnResult(query);
         validUser = result.get(0);
         System.out.format("\tUser ID : %s\n",validUser.get(0));
         System.out.format("\tCurrent Name: %s\n\tNew User Name (Press Enter to keep current): ", validUser.get(1));
         values[0] = in.readLine();
         if(values[0].equals("")){
            values[0] = validUser.get(1);
         }
         System.out.format("\tCurrent Password: %s\n\tNew Password (Press Enter to keep current): ", validUser.get(2));
         values[1] = in.readLine();
         if(values[1].equals("")){
            values[1] = validUser.get(2);
         }
         System.out.format("\tCurrent Latitude: %s\n\tNew Latitude (Press Enter to keep current): ", validUser.get(3));
         values[2] = in.readLine();
         if(values[2].equals("")){
            values[2] = validUser.get(3);
         }
         System.out.format("\tCurrent Longitude: %s\n\tNew Longitude (Press Enter to keep current): ", validUser.get(4));
         values[3] = in.readLine();
         if(values[3].equals("")){
            values[3] = validUser.get(4);
         }
         System.out.format("Current Account Type: %s\n", validUser.get(5));
         System.out.println("Select New Type:");
         System.out.println("\t1:Keep Current Value");
         System.out.println("\t2:Set as CUSTOMER");
         System.out.println("\t3:Set as MANAGER");
         System.out.println("\t4:Set as ADMIN");
         while(values[4]==null){
            switch(readChoice()){
               case 1:values[4]=validUser.get(5).trim();break;
               case 2:values[4] = "customer";break;
               case 3:values[4] = "manager";break;
               case 4:values[4] = "admin";break;
               default:System.out.println("Unrecognized choice!"); break;
            }
         }
         System.out.println("\nOriginal User Info:");
         esql.executeQueryAndPrintResult(query);
         String update = String.format("UPDATE Users SET name = '%s', password = %s , latitude = %s , longitude = %s , type = '%s' WHERE userID = %d;", values[0],values[1],values[2],values[3],values[4],user);
         esql.executeUpdate(update);
         System.out.println();
         System.out.println("\nUpdated User Info:");
         esql.executeQueryAndPrintResult(query);
         printWait();
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void adminUpdateProduct(Retail esql) {//Admins can edit Products
      try{
         int valid = 0;
         String[] values = {null,null,null,null};
         List<String> validProduct=null;
         String query = null;
         do{
         System.out.print("\tEnter Product Name: ");
         values[1] = in.readLine().trim();
         query = String.format("Select * FROM Product WHERE productName = '%s';", values[1]);
         valid = esql.executeQuery(query);
         if(valid==0)
            System.out.format("Product '%s' does not exist! Please select valid product", values[1]);
         }while(valid<=0);
         valid = 0;
         do{
         System.out.print("\tEnter Store ID: ");
         values[0] = in.readLine();
         query = String.format("Select * FROM Product WHERE storeID = %s AND productName = '%s' ;", values[0],values[1]);
         valid = esql.executeQuery(query);
         if(valid==0)
            System.out.format("Either Store '%s' does not exist or does not stock Product '%s'! Please select different store", values[0],values[1]);
         }while(valid<=0);

         validProduct=esql.executeQueryAndReturnResult(query).get(0);

         System.out.format("Current Product : %s at Store %s\n",validProduct.get(1).trim(),validProduct.get(0).trim());
         System.out.format("Current Quantity: %s\nNew Quantity (Press Enter to keep current): ", validProduct.get(2));
         values[2] = in.readLine();
         if(values[2].equals("")){
            values[2] = validProduct.get(2);
         }
         System.out.format("Current Unit Price: %s\nNew Unit Price (Press Enter to keep current): ", validProduct.get(3));
         values[3] = in.readLine();
         if(values[3].equals("")){
            values[3] = validProduct.get(3);
         }
         
         System.out.println("\nOriginal Product Info:");
         esql.executeQueryAndPrintResult(query);
         String update = String.format("UPDATE Product SET numberOfUnits = %s , pricePerUnit = %s WHERE storeID = %s AND productName = '%s';", values[2],values[3],values[0],values[1]);
         esql.executeUpdate(update);
         System.out.println();
         System.out.println("\nUpdated Product Info:");
         esql.executeQueryAndPrintResult(query);
         query = String.format("INSERT INTO ProductUpdates (managerID,storeID,productName,updatedOn) VALUES ( %s, %s, '%s', DATE_TRUNC('second', CURRENT_TIMESTAMP::timestamp));", esql.userID, values[0], values[1]);
         esql.executeUpdate(query);
         printWait();
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   
}//end Retail

