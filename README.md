# UCR-CS166-Final_Project
Final Project for CS166

From a lab machine or other linux machine with PostgreSQL installed open the main folder for the repository and run the command:
>`source ./initialize.sh`

This will initialize the Postgre environment, create the database, copy the data files over and run all the sql scripts. It will automatically compile and open the Java application.

If your PostgreSQL server is already running you will have to instead use the command:
>`source ./sql/scripts/create_db.sh`

If you exited the Java application at any time but want to reenter it without stopping and restarting the entire PostgreSQL server you can use the following:
>`source ./java/scrips/compile.sh`

Finally, when you are done make sure to stop the PostgreSQL server with the following:
>`source ./db_scripts/stopPostgreDB.sh`

