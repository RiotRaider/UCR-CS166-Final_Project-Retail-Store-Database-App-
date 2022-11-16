#! /bin/bash
echo "initializing Retail Store database"
source ./db_scripts/startPostgreSQL.sh
sleep 1

source ./db_scripts/createPostgreDB.sh
sleep 1

source ./sql/scripts/create_db.sh
sleep 1

source ./java/scripts/compile.sh