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

createMysqlDump(){
  mysqlCmdString=$(getMysqlCommand "dump")
  logMessage "createMysqlDump : MYSQL command : $mysqlCmdString" "onlytologfile"

  if [ -z "$mysqlCmdString" ]; then
    logMessage "Mysql is not installed. Can not create mysql dump."
  else
    mysqlCmdString="$mysqlCmdString --databases $MYSQL_DB_NAME"
    dumpfilename="$INSTALLABLES_DIR/$MYSQL_DB_NAME.sql"
    logMessage "Creating the dumpfilename : $dumpfilename" "onlytologfile"
    `$mysqlCmdString -p$MYSQL_PASSWORD > $dumpfilename`
   
  

    if [ $? -ne 0 ]; then
      logMessage "Failed to create the dump."
    else
      logMessage "Successfully created the mysql dumps. Dump files located at : $INSTALLABLES_DIR"
    fi
  fi
}
createMysqlDumpBeforeTrim(){
  mysqlCmdString=$(getMysqlCommand "dump")
  mysqlCmd=$(getMysqlCommand)
  MYSQL_PASSWORD=$(getSqlPassword | xargs)
  logMessage "createMysqlDump : MYSQL command : $mysqlCmdString" "onlytologfile"
  if [ -z "$mysqlCmdString" ]; then
    logMessage "Mysql is not installed. Can not create mysql dump."
  else
    if [[ ! -d $PRD_DUMP_DIR/latestBackup ]];then
        logMessage "Directory $PRD_DUMP_DIR/latestBackup doesn't exist!-"
        logMessage "Creating directory: $PRD_DUMP_DIR/latestBackup"
        mkdir $PRD_DUMP_DIR/latestBackup/
    fi
    if [[ ! -d "$PRD_DUMP_DIR/`date +%m%d%Y`/" ]];then
        logMessage "Directory $PRD_DUMP_DIR/`date +%m%d%Y` doesn't exist!-"
        logMessage "Creating directory: $PRD_DUMP_DIR/`date +%m%d%Y`"
        mkdir $PRD_DUMP_DIR/`date +%m%d%Y`
    fi
    mysqlCmdString="$mysqlCmdString --databases $MYSQL_DB_NAME"
    dumpfilename="$PRD_DUMP_DIR/latestBackup/$MYSQL_DB_NAME".sql
    dumpfilename_bkp="$PRD_DUMP_DIR/`date +%m%d%Y`/$MYSQL_DB_NAME".sql
    logMessage "Taking backup of the existing dump file in $PRD_DUMP_DIR directory.."
    logMessage "Creating the dumpfilename : $dumpfilename" "onlytologfile"
    #`$mysqlCmdString -p$MYSQL_PASSWORD --skip-add-drop-table --skip-triggers --skip-add-locks --skip-disable-keys --skip-set-charset --insert-ignore --skip-lock-tables --single-transaction > $dumpfilename_bkp`
    `$mysqlCmdString -p$MYSQL_PASSWORD --skip-add-drop-table --skip-triggers --skip-add-locks --skip-disable-keys --skip-set-charset --replace --skip-lock-tables --single-transaction > $dumpfilename`
    `$mysqlCmdString -p$MYSQL_PASSWORD --skip-add-drop-table --skip-triggers --skip-add-locks --skip-disable-keys --skip-set-charset --replace --skip-lock-tables --single-transaction > $dumpfilename_bkp`
    if [ $? -ne 0 ]; then
      logMessage "Failed to create the dump."
      return 1
    else
      if [ -f "$dumpfilename" ] && ! [ -s "$dumpfilename" ];then
          return 1
      fi
      db_table_count=`$mysqlCmd -p${MYSQL_PASSWORD} -Bse"SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '$MYSQL_DB_NAME'"`
      dumped_db_count=`grep -c "CREATE TABLE"  $dumpfilename`
      if [[ $db_table_count != $dumped_db_count ]];then
          echo "Actual table count: $db_table_count, Dump File table count: $dumped_db_count"
          logMessage "DB table count in dump file doesn't match with actual db table count, can not proceed for trimming.."
          exit 1
      fi
      logMessage "Successfully created the mysql dumps. Dump files located at : $PRD_DUMP_DIR"
      return 0
    fi
  fi
}
restoreMysqlDump(){
  mysqlCmdString=$(getMysqlCommand)
  logMessage "MARIADB Command: $mysqlCmdString" "onlytologfile"

  if [ "$1" == "" ]; then
    dumpFileName="$INSTALLABLES_DIR/$MYSQL_DB_NAME.sql"
    dumpFileName1="$INSTALLABLES_DIR/$MYSQL_DB_NAME1.sql"
  elif [ "$1" == "demo" ]; then
    dumpFileName="$DEMO_PKG_FOLDER/$INSTALLABLES_DIR/$MYSQL_DB_NAME.sql"
   
  fi

  logMessage "Restoring Mariadb dump $dumpFileName, please wait... " "newline-1"
  `$mysqlCmdString -p$MYSQL_PASSWORD < $dumpFileName`
  if [ $? -ne 0 ]; then
    logMessage "Failed to restore dump $dumpFileName. Incorrect dump."
  else
    logMessage "Successfully restored the Mariadb dump ($dumpFileName)."

   
  fi
}

cleanMySqlDataBase(){
  if [ -z "$1" ]; then
    mysqlclientdir=$(getMysqlInstalledDir)
  else
    mysqlclientdir=$(getMysqlDumpInstalledDir)
  fi

  if [ ! -z "$mysqlclientdir" ]; then
    logMessage "Cleaning DB......" "newline-1"
    mysqlCmdString=$(getMysqlCommand)
    `$mysqlclientdir -p$MYSQL_PASSWORD -D$MYSQL_DB_NAME -e "DROP DATABASE $MYSQL_DB_NAME;"`
    #`$mysqlCmdString -D$MYSQL_DB_NAME -e "DROP DATABASE $MYSQL_DB_NAME;"`
    logMessage "Deleted Existing Databases."
  else
    logMessage "Failed to clean the database. Incorrect DB."
  fi
}

inputParam=$1;
if [ -z "$inputParam" ]; then
  logMessage "MariaDB dump script: Nothing to do"
else
  logMessage "MariaDB dump : Action : $inputParam" "newline-1"
  if [ "$inputParam" == "createdump" ]; then
    createMysqlDump
  elif [ "$inputParam" == "createdump_trim" ]; then
    if [ -z '$trim_date' ]; then
	logMessage "Please provide trim_date."
	exit 0
    fi
    createMysqlDumpBeforeTrim 
  elif [ "$inputParam" == "cleandb" ]; then
    cleanMySqlDataBase
  elif [ "$inputParam" == "demorestoredump" ]; then
    restoreMysqlDump "demo"
  elif [ "$inputParam" == "restorebackupdump" ]; then
    restoreMysqlDump "backup"
  else 
    if [ "$inputParam" == "restoredump" ]; then
      restoreMysqlDump
    fi
  fi	
fi

