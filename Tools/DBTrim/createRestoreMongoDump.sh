#!/bin/bash

. ./scripts/commonFunctions.lib
. ./scripts/db_trim.conf
cpCmd=`which cp`
trim_date=$2
getMysqlCommand(){
  if [ -z "$1" ]; then
    mysqlclientdir=$(getMysqlInstalledDir)
  else
    mysqlclientdir=$(getMysqlDumpInstalledDir)
  fi

  if [ ! -z "$mysqlclientdir" ]; then
    propfile=$INSTALLATION_PATH$CONFIG_PATH
    if [ -f "$propfile" ]; then
      while read line; do
        IFS='=' read -a myarray <<< "$line"
        key=${myarray[0]}
        value=${myarray[1]}
        if [ "$key" == "SQL_DB_IP" ]; then
          mysqlIP=$value
        elif [ "$key" == "PASSWORD" ]; then
          mysqlPwd=$value
        fi
      done < $propfile
    fi

    if [ -z "${mysqlIP}" ]; then
      mysqlIP="$MYSQL_SERVER_IP"
    fi

    if [ -n "$mysqlPwd" ]; then
      MYSQL_COMMAND="$mysqlclientdir -p$mysqlPwd"
    else
      MYSQL_COMMAND="$mysqlclientdir"
    fi

    MYSQL_COMMAND="$MYSQL_COMMAND -h $mysqlIP"

  else
    MYSQL_COMMAND=""
  fi
  echo $MYSQL_COMMAND
}

createMongoDump(){
  #mysqlCmdString=$(getMysqlCommand "dump")
  logMessage "createMongoDump : MONGODB command : " "onlytologfile"
  sudo mongod --dbpath /data/db --logpath /var/log/mongodb/mongod.log --fork
  mongodump -d SMART_Config -o $INSTALLABLES_DIR

    if [ $? -ne 0 ]; then
      logMessage "Failed to create the dump."
    else
      logMessage "Successfully created the Mongo dumps. Dump files located at : $INSTALLABLES_DIR"
    fi

}

exportMongoDBData() {
    db=$1
    out_dir=$2
    if [ ! $out_dir ]; then
        mkdir -p $out_dir
    fi
    mongo $db --quiet --eval "db.getCollectionNames()" | sed 's/[][]//g' | sed 's/,//g'| sed 's/"//g'| sed -r '/^\s*$/d' > collections
    input="collections"
    while IFS= read -r line
    do
       col=`echo $line`
       echo "Exporting Collection: $col"    
       mongoexport -d $db -c "$col" -o "$out_dir/${col}.json"
    done < "$input"
}
createMongoDumpTrim(){
  #mysqlCmdString=$(getMysqlCommand "dump")
  logMessage "createMongoDump : MONGODB command : " "onlytologfile"
  dumpfilename="$PRD_DUMP_DIR/latestBackup/"
  dumpfilename_bkp="$PRD_DUMP_DIR/`date +%m%d%Y`/"
  if [[ ! -d "$dumpfilename" ]];then
        logMessage "Directory $dumpfilename doesn't exist!-"
        logMessage "Creating directory: $dumpfilename"
        mkdir $dumpfilename
  fi
  if [[ ! -d "$dumpfilename_bkp" ]];then
        logMessage "Directory $dumpfilename_bkp doesn't exist!-"
        logMessage "Creating directory: $dumpfilename_bkp"
        mkdir $dumpfilename_bkp
  fi
  logMessage "Taking backup of the existing dump file in $dumpfilename directory.."
  logMessage "Deleting previous dump files.."
  rm -r $dumpfilename/SMART_Config/ >/dev/null 2>&1
  sudo mongod --dbpath /data/db --logpath /var/log/mongodb/mongod.log --fork
  mongodump -d SMART_Config -o $dumpfilename
  #exportMongoDBData SMART_Config $dumpfilename_bkp 
  #exportMongoDBData SMART_Config $dumpfilename
  mongodump -d SMART_Config -o $dumpfilename_bkp
  if [ $? -ne 0 ]; then
      logMessage "Failed to create the dump."
      return 1
  else
      if [[ `find $dumpfilename/SMART_Config -maxdepth 0 -empty -exec echo 1 \;` -eq 1 ]];then
         return 1
      fi
      actual_col_count=`mongo --quiet < scripts/mongoscript_count.js | grep -v "switched to db SMART_Config"`
      dump_col_count=`ls $dumpfilename/SMART_Config | wc -l`
      actual_col_count=$((actual_col_count+actual_col_count))
      if [[ $actual_col_count != $dump_col_count ]];then
         logMessage "Actual Collection Count: $actual_col_count, Dumped Collection Count: $dump_col_count"
         logMessage "DB collection count in dump file doesn't match with actual db collection count, can not proceed for trimming.."
	 exit 1
      fi
      logMessage "Successfully created the Mongo dumps. Dump files located at : $PRD_DUMP_DIR"
      return 0
  fi

}
restoreMongoDump(){

  logMessage "MONGO Command: mongorestore -d database folder" "onlytologfile"

  if [ "$1" == "" ]; then
    logMessage "Restoring Mongo dump $dumpFileName, please wait... " "newline-1"
    dumpFileName="$INSTALLABLES_DIR/$MYSQL_DB_NAME2"
    sudo mongod --dbpath /data/db --logpath /var/log/mongodb/mongod.log --fork
    mongo < scripts/mongoscripts.js
    mongorestore -d SMART_Config $INSTALLABLES_DIR/$MYSQL_DB_NAME2
    #dumpFileName1="$INSTALLABLES_DIR/$MYSQL_DB_NAME1.sql"
  elif [ "$1" == "demo" ]; then
    logMessage "Restoring Mongo dump $dumpFileName, please wait... " "newline-1"
    dumpFileName="$DEMO_PKG_FOLDER/$INSTALLABLES_DIR/$MYSQL_DB_NAME2"
    sudo mongod --dbpath /data/db --logpath /var/log/mongodb/mongod.log --fork
    mongo < scripts/mongoscripts.js
    mongorestore -d SMART_Config $dumpFileName
  fi
  if [ $? -ne 0 ]; then
    logMessage "Failed to restore dump $dumpFileName. Incorrect dump."
  else
    logMessage "Successfully restored the MongoDB dump ($dumpFileName)."

   
  fi
}

cleanMongoDataBase(){
   sudo mongod --dbpath /data/db --logpath /var/log/mongodb/mongod.log --fork
   mongo < scripts/mongoscripts.js
}

inputParam=$1;
if [ -z "$inputParam" ]; then
  logMessage "MariaDB dump script: Nothing to do"
else
  logMessage "MariaDB dump : Action : $inputParam" "newline-1"
  if [ "$inputParam" == "createdump" ]; then
    createMongoDump
  elif [ "$inputParam" == "createdump_trim" ]; then
    createMongoDumpTrim
  elif [ "$inputParam" == "cleandb" ]; then
    cleanMongoDataBase
  elif [ "$inputParam" == "demorestoredump" ]; then
    restoreMongoDump "demo"
  elif [ "$inputParam" == "restorebackupdump" ]; then
    restoreMysqlDump "backup"
  else 
    if [ "$inputParam" == "restoredump" ]; then
      restoreMongoDump
    fi
  fi	
fi

