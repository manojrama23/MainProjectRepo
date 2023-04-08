#!/bin/bash
. ./scripts/commonFunctions.lib
if [ ! -f "./scripts/db_trim.conf" ]; then
    logMessage "Config file db_trim.conf not found inside scripts directory."
    exit 1
fi
. ./scripts/db_trim.conf

executedfor=""
unoconvsubstring="/usr/bin/unoconv"

checkOSArchitchture(){
  archValue=$(arch)
  archValueFromUname=$(uname -m)
  archValueFromGetConf=$(getconf LONG_BIT)
  if [ "$archValue" == "x86_64" ] && [ "$archValueFromUname" == "x86_64" ] && [ "$archValueFromGetConf" == "64" ]; then
    logMessage "Operating system architecture : "$archValue
  else
    logMessage "System OS architechture is 32-bit. So can not proceed with installation."
  fi
}



installCPAN(){
  logMessage "***************************************  CPAN  ****************************************" "newline-1"
  installcpan
  logMessage "Successfully installed CPAN"
}

installSSH(){
  logMessage "***************************************  SSH   ***************************************" "newline-1"
  installssh
  logMessage "Successfully installed SSH"
}

installSAR(){
  logMessage "***************************************  SAR   ***************************************" "newline-1"
  installsar
  logMessage "Successfully installed SAR"
}

installPERLPACKAGES(){
  logMessage "***************************************  PERL PACKAGES  ****************************************" "newline-1"
  installperlpackage
  logMessage "Successfully installed PERL PACKAGES"
}

exportENVForTomcat(){
  grep -q "export CATALINA_HOME=\$HOME/tomcat" "$HOME_DIR/.bashrc"
  if [ $? -eq "0" ]; then
    logMessage "Deleting old Tomcat settings in bashrc file at path: "$HOME_DIR
    sed -i "/CATALINA_HOME=\$HOME\/tomcat/d" $HOME_DIR/.bashrc
    sed -i "/PATH=\$CATALINA_HOME\/bin:\$PATH/d" $HOME_DIR/.bashrc
  fi
  logMessage "Updating Tomcat settings in bashrc file at path: "$HOME_DIR
  echo "export CATALINA_HOME=\$HOME/tomcat/apache-tomcat-8.5.35" >> $HOME_DIR/.bashrc
  echo "export PATH=\$CATALINA_HOME/bin:\$PATH" >> $HOME_DIR/.bashrc

  logMessage "HomeDir : "$HOME_DIR
  source $HOME_DIR/.bashrc
  if [ $? -ne "0" ]; then
    logMessage ".bashrc could not be executed. Execute it manually."
  else
    logMessage ".bashrc executed."
  fi

  if [ -h "$HOME_DIR/tomcat" ]; then
    rm "$HOME_DIR/tomcat"
    logMessage "Old softlink deleted."
  fi

  ln -s $APACHE_HOME_DIR "$HOME_DIR/tomcat"
  if [ $? -ne "0" ]; then
    logMessage "New softlink creation failed!"
    exit
  else
    logMessage "New softlink created successfully."
  fi
}


installSEVEN(){
  logMessage "***************************************  7ZIP  ****************************************" "newline-1"
  installseven
  logMessage "Successfully installed 7ZIP"
}

installTomcat(){
  logMessage "************************* Apache Tomcat Application server 8.5.35 *************************" "newline-1"
  logMessage "Installing Apache Tomcat application server. Please wait..."
  mkdir -p $APACHE_HOME_DIR
  invoked_by_user=$(id -urn)
  path="$SETUP_INSTALLABLES/tomcat*.tar.gz"
  tar -xzf $path -C $APACHE_HOME_DIR
  if [ $? -ne 0 ]; then
    logMessage "Failed to install apache tomcat. Existing installation..."
    exit
  else
    logMessage "Successfully installed Apache Tomcat at path : $APACHE_HOME_DIR"
    if [ "$invoked_by_user" == "root" ]; then
      chmod -R 777 $APACHE_HOME_DIR
      chown  $SUDO_USER:$SUDO_USER -R $APACHE_HOME_DIR
    else
      chmod -R 777 $APACHE_HOME_DIR
      #chown  $invoked_by_user: $invoked_by_user $WILDFLY_HOME_DIR
    fi
  fi
  exportENVForTomcat

}
javaCheck(){
  javastring=$(which java)
  if [[ -z "$javastring" ]]; then
    echo "NotInstalled"
  else
    echo "Installed"
  fi
}

javaVersionCheck(){
  javaversion=`javac -version 2>&1`
  javamajorver=$(echo $javaversion | cut -f2 -d " " | cut -f1 -d "_")
  if [[ "$javamajorver" < "1.8.0" ]]; then
    echo "NotValidVersion"
  else
    echo "ValidVersion"
  fi
}

exportENVForJava(){
  grep -q "export JAVA_HOME=\$HOME/jdk" "$HOME_DIR/.bashrc"
  if [ $? -eq "0" ]; then
    logMessage "Deleting old Java settings in bashrc file at path: "$HOME_DIR
    sed -i "/JAVA_HOME=\$HOME\/jdk/d" $HOME_DIR/.bashrc
    sed -i "/PATH=\$JAVA_HOME\/bin:\$PATH/d" $HOME_DIR/.bashrc
  fi
  logMessage "Updating Java settings in bashrc file at path: "$HOME_DIR
  echo "export JAVA_HOME=\$HOME/jdk" >> $HOME_DIR/.bashrc
  echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> $HOME_DIR/.bashrc

  logMessage "HomeDir : "$HOME_DIR
  source $HOME_DIR/.bashrc
  if [ $? -ne "0" ]; then
    logMessage ".bashrc could not be executed. Execute it manually."
  else
    logMessage ".bashrc executed."
  fi

  if [ -h "$HOME_DIR/jdk" ]; then
    rm "$HOME_DIR/jdk"
    logMessage "Old softlink deleted."
  fi

  ln -s $JAVAHOME_PATH "$HOME_DIR/jdk"
  if [ $? -ne "0" ]; then
    logMessage "New softlink creation failed!"
    exit
  else
    logMessage "New softlink created successfully."
  fi
}

installJavaPkg(){
  logMessage "Installing $JAVA_VERSION. Please wait..."
  mkdir -p $JAVA_HOME_DIR
  logMessage "Extracting $JAVA_PACKAGE_NAME..."
  tar -xzf "$SETUP_INSTALLABLES/$JAVA_PACKAGE_NAME" -C  $JAVA_HOME_DIR
  if [ "$?" -ne "0" ]; then
    logMessage "Failed to extract $JAVA_PACKAGE_NAME. Exiting installation."
    exit
  else
    exportENVForJava
    logMessage "$JAVA_VERSION installed successfully in path $JAVA_HOME_DIR"
    chmod -R 777 $JAVA_HOME_DIR
  fi
}

installJava(){
  logMessage "*********************************** JAVA $JAVA_VERSION ***********************************" "newline-1"
  logMessage "Checking JAVA installation..."
  javastringvalue=$(javaCheck)
  if [[ "$javastringvalue" == "Installed" ]]; then
    javaversion=$(javaVersionCheck)
    if [[ "$javaversion" == "NotValidVersion" ]]; then
      installJavaPkg
    else
      JAVA_VERSION=$(echo `javac -version 2>&1` | cut -f2 -d " ")
      logMessage "$JAVA_VERSION already installed." "newtab-1"

      grep -q "export JAVA_HOME=\$HOME/jdk" "$HOME_DIR/.bashrc" | grep -v "^ *#.*"
      if [[ $? -ne 0 ]]; then
        javapath=`which java`
        if [[ $javapath = "/usr/bin/java" ]]; then
          JAVAHOME_PATH=$(echo `update-java-alternatives -l` | cut -f3 -d " ")
          JAVA_HOME_DIR=${JAVAHOME_PATH%/*}
          exportENVForJava
        fi
      fi
    fi
  else
    logMessage "No JAVA installation found." "newtab-1"
    installJavaPkg
  fi
}

mvnCheck(){
  mvnstring=$(which mvn)
  if [[ -z "$mvnstring" ]]; then
    echo "NotInstalled"
  else
    echo "Installed"
  fi
}

exportENVForMVN(){
  grep -q "export M2_HOME=\$HOME/mvn" "$HOME_DIR/.bashrc"
  if [ $? -eq 0 ]; then
    logMessage "Deleting old MVN settings in bashrc file at path: "$HOME_DIR
    sed -i "/M2_HOME=\$HOME\/mvn/d" $HOME_DIR/.bashrc
    sed -i "/M2=\$M2_HOME\/bin/d" $HOME_DIR/.bashrc
    sed -i "/MAVEN_OPTS=\"-Xms256m -Xmx512m\"/d" $HOME_DIR/.bashrc
    sed -i "/PATH=\$M2:\$PATH/d" $HOME_DIR/.bashrc
  fi
  logMessage "Updating MVN settings in bashrc file at path: "$HOME_DIR
  echo "export M2_HOME=\$HOME/mvn" >> $HOME_DIR/.bashrc
  echo "export M2=\$M2_HOME/bin" >> $HOME_DIR/.bashrc
  echo "export MAVEN_OPTS=\"-Xms256m -Xmx512m\"" >> $HOME_DIR/.bashrc
  echo "export PATH=\$M2:\$PATH" >> $HOME_DIR/.bashrc

  logMessage "HomeDir : "$HOME_DIR
  source $HOME_DIR/.bashrc
  if [ $? -ne 0 ]; then
    logMessage ".bashrc could not be executed. Execute it manually."
  else
    logMessage ".bashrc executed."
  fi

  if [ -h "$HOME_DIR/mvn" ]; then
    rm "$HOME_DIR/mvn"
    logMessage "Old softlink deleted."
  fi

  ln -s $M2HOME_PATH "$HOME_DIR/mvn"
  if [ $? -ne 0 ]; then
    logMessage "New softlink creation failed!"
  else
    logMessage "Successfully created New softlink."
  fi
}

installMVN(){
  logMessage "*********************************** Apache-$MVN_VERSION ***********************************" "newline-1"
  logMessage "Checking MVN installation..."
  mvnstringvalue=$(mvnCheck)
  if [[ "$mvnstringvalue" == "NotInstalled" ]]; then
    logMessage "Installing $MVN_VERSION. Please wait..."
    mkdir -p $MVN_HOME_DIR
    tar -xzf "$SETUP_INSTALLABLES/$MVN_PACKAGE_NAME" -C $MVN_HOME_DIR
    if [ $? -ne 0 ]; then
      logMessage "Failed to install $MVN_VERSION. Exiting installation..."
      exit
    else
      exportENVForMVN
      logMessage "Successfully installed $MVN_VERSION at path $MVN_HOME_DIR."
      chmod -R +x $MVN_HOME_DIR
    fi
  else
    logMessage "MVN already installed." "newtab-1"
  fi
}

installMARIADB(){
  logMessage "*************************************** MARIADB  ****************************************" "newline-1"
  logMessage "Checking MARIADB installation..."
  mysqlCnfFile="/etc/mysql/my.cnf"
  entryFound=false
  mysqlclient=$(getMysqlInstalledDir)
  if [ -z "$mysqlclient" ]; then
    logMessage "No MARIADB installation found. Installing MARIADB package, please wait..." "onlytomysqllogfile"
    installMariadbPkg
  else
    logMessage "MARIADB is already installed." "onlytomysqllogfile"
  fi
}

installMONGODB(){
logMessage "***************************************  MONGODB   ****************************************" "newline-1"
  logMessage "Checking MONGODB installation..."
  mongoCnfFile="/etc/mongod.conf"
  entryFound=false
  mongoclient=$(getMongoDBInstalledDir)
  if [ -z "$mongoclient" ]; then
    logMessage "No MONGODB installation found. Installing MONGODB package, please wait..." "onlytoMONGODBlogfile"
    installMongoPkg
  else
    logMessage "MONGODB is already installed." "onlytoMONGODBlogfile"
  fi
}

installEXPECT(){
  logMessage "***************************************  EXPECT  ****************************************" "newline-1"
  logMessage "Checking the EXPECT installation..."
  expect=$(getExpectBinaryFile)  
  if [ -z "$expect" ]; then
    logMessage "No EXPECT installation found. Installing EXPECT package, please wait..." "onlytoEXPECTBlogfile"
    installExpectPkg
  else
    logMessage "EXPECT is already installed." "onlytoEXPECTBlogfile"
  fi
}


installCURL(){
  logMessage "***************************************   CURL   ****************************************" "newline-1"
  installcurl
  logMessage "Successfully installed CURL"
}
chgOwnerIns(){
  sudo chown  $INVOKED_BY_USER:$INVOKED_BY_USER_GROUP -R $INSTALLATION_PATH
  chmod -R 777 $INSTALLATION_PATH
  logMessage "Successfully changed ownership at path : $INSTALLATION_PATH" "newline-1"
}

cleanTomcat(){
  logMessage "Cleaning up tomcat previous deployments..." "newline-1"
  rm -rf $APACHE_HOME_DIR/$APACHE_VERSION/webapps/ROOT.war*
  rm -rf $APACHE_HOME_DIR/$APACHE_VERSION/webapps/ROOT
  logMessage "Successfully cleaned previous deployments."
}

cleanDataBase(){
  logMessage "Cleaning database..." "newline-1"
  bash scripts/createRestoreMYSQLDump.sh cleandb
  bash scripts/createRestoreMongoDump.sh cleandb
  logMessage "Successfully cleaned database."
}

cleanDeployment(){
  cleanTomcat
  cleanDataBase
  if [ -d $TO_EXTRACT_SAMSUNG_FOLDER ]; then
    rm -rf $TO_EXTRACT_SAMSUNG_FOLDER
  fi
}

deployWar(){
  logMessage "Tomcat server path : "$APACHE_HOME_DIR/$APACHE_VERSION "newline-1"
  if [ -d "$APACHE_HOME_DIR/$APACHE_VERSION" ]; then
    if [ "$executedfor" == "dev" ]; then
      cleanTomcat
    fi

    logMessage "Copying war into Tomcat server..." "newline-1"
    cp "$INSTALLABLES_DIR/$WAR_FILE_NAME""$WAR_FILE_EXT" "$WAR_FILE_PATH"
    logMessage "Successfully copied war."

    logMessage "Deploying properties file into Tomcat server..." "newline-1"
    deployPropertiesFile
    if [ $? -ne 0 ]; then
      logMessage "Failed to deploy properties file."
      exit
    else
      logMessage "Successfully deployed properties file."
    fi

    logMessage "Updating Base Path..." "newline-1"
    updateBasePath
    if [ $? -ne 0 ]; then
      logMessage "Failed to update base path to $INSTALLATION_PATH$CONFIG_PATH."
      exit
    else
      logMessage "Successfully updated Base Path."
    fi
  fi
}

deployDemoWar(){
  logMessage "Tomcat server path : "$APACHE_HOME_DIR/$APACHE_VERSION "newline-1"
  if [ -d "$APACHE_HOME_DIR/$APACHE_VERSION" ]; then
    if [ "$executedfor" == "demo" ]; then
      cleanTomcat
    fi
    logMessage "Copying war into Tomcat server..." "newline-1"
    cp "$DEMO_PKG_FOLDER/$INSTALLABLES_DIR/$WAR_FILE_NAME1""$WAR_FILE_EXT" "$WAR_FILE_PATH1"
    logMessage "Successfully copied war."
    logMessage "Deploying properties file into wildfly server..." "newline-1"
    deployPropertiesFile
    if [ $? -ne 0 ]; then
      logMessage "Failed to deploy properties file."
      exit
    else
      logMessage "Successfully deployed properties file."
    fi

    logMessage "Updating Base Path..." "newline-1"
    updateBasePath
    if [ $? -ne 0 ]; then
      logMessage "Failed to update base path to $INSTALLATION_PATH$CONFIG_PATH."
      exit
    else
      logMessage "Successfully updated Base Path."
    fi
  fi
}

deploySMART(){
  if [ -f "$BUNDLE_NAME" ]; then
    logMessage "Deployment of SMART started..." "newline-1"
    #1. Extract the Installables tar
    rm -rf $INSTALLABLES_DIR
    tar -xzf $BUNDLE_NAME
    if [ $? -ne 0 ]; then
      logMessage "Could not extract $BUNDLE_NAME. Deployment failed. Exiting deployment..." "newline-1"
      logMessage "Please contact support team to resolve the deployment issue."
      exit
    else
      rm -rf $BUNDLE_NAME
      #2. Stop wildfly
      if [ "$executedfor" == "dev" ]; then
        stopTomcatServer
      fi
      #3. Extract smart
      if [ ! -d $TO_EXTRACT_COMMON_FOLDER ]; then
        mkdir -p $TO_EXTRACT_COMMON_FOLDER
        chmod -R 777 $TO_EXTRACT_COMMON_FOLDER
      fi
      logMessage "Extracting common folder to $TO_EXTRACT_COMMON_FOLDER..." "newline-1"
      tar -xzf $INSTALLABLES_DIR/$SMART_DIR_BUNDLE_FILE_NAME -C $TO_EXTRACT_COMMON_FOLDER
      #tar -xzf $INSTALLABLES_DIR/$SMART_DIR_BUNDLE_FILE_NAME1 -C $TO_EXTRACT_COMMON_FOLDER
      chmod -R 777 $TO_EXTRACT_COMMON_FOLDER
      if [ $? -ne 0 ]; then
        logMessage "Failed to extract common folder to $TO_EXTRACT_COMMON_FOLDER. Deployment failed."
        exit
      else
        logMessage "Successfully extracted common folder to $TO_EXTRACT_COMMON_FOLDER."
        #4. Deploy War
        deployWar
        configMysqlPassword
        bash scripts/createRestoreMYSQLDump.sh restoredump
	bash scripts/createRestoreMongoDump.sh restoredump
        if [ $? -ne 0 ]; then
          logMessage "Failed to deploy mariadb dump. Deployment failed."
          exit
        else
          if [ "$executedfor" == "dev" ]; then
            startTomcatServer
          elif [ "$executedfor" == "customer" ] && [ "$(checkWildflyServerStatus)" == "running" ]; then
            bash $APP_SERVER_PATH/shutdown.sh --connect --commands="deploy $WAR_FILE_PATH/$WAR_FILE_NAME$WAR_FILE_EXT --force,deployment-info --name=$WAR_FILE_NAME$WAR_FILE_EXT, quit"
          fi
        fi
      fi
    fi
    logMessage "Successfully deployed SMART web application." "newline-1"
  else
    logMessage "" "newline-1"
    logMessage "Could not find $BUNDLE_NAME file in the current directory."
    logMessage "Please download the $BUNDLE_NAME and place inside the SMARTSetup folder and then re-run the setup to deploy SMART."
    logMessage "" "newline-1"
  fi
}

deploySMARTDemo(){
  current_dir="$PWD"
  if [ -f "$DEMO_PKG_FOLDER/$BUNDLE_NAME" ]; then
    logMessage "Deployment of SMART started..." "newline-1"
    #1. Extract the SMART Installable tar
    tar -xzf $DEMO_PKG_FOLDER/$BUNDLE_NAME -C $DEMO_PKG_FOLDER/
    if [ $? -ne 0 ]; then
      logMessage "Could not extract $BUNDLE_NAME. Demo deployment failed. Exiting deployment..." "newline-1"
      logMessage "Please contact support team to resolve the deployment issue."
      exit
    else
      #2. Stop wildfly
      if [ "$executedfor" == "demo" ]; then
        stopTomcatServer
      fi
      #3. Extract SMART
      if [ ! -d $TO_EXTRACT_SAMSUNG_FOLDER ]; then
        mkdir -p $TO_EXTRACT_SAMSUNG_FOLDER
      else
        rm -rf $TO_EXTRACT_SAMSUNG_FOLDER/*
      fi
      logMessage "Extracting common folder to $TO_EXTRACT_COMMON_FOLDER..." "newline-1"
      tar -xzf $DEMO_PKG_FOLDER/$INSTALLABLES_DIR/$SMART_DIR_BUNDLE_FILE_NAME -C $TO_EXTRACT_SAMSUNG_FOLDER
      if [ $? -ne 0 ]; then
        logMessage "Failed to extract common folder to $TO_EXTRACT_COMMON_FOLDER. Deployment failed."
        exit
      else
        logMessage "Successfully extracted common folder to $TO_EXTRACT_COMMON_FOLDER."
        #4. Deploy War
        deployDemoWar
        sleep 5
        bash scripts/createRestoreMYSQLDump.sh demorestoredump
        bash scripts/createRestoreMongoDump.sh demorestoredump
        if [ $? -ne 0 ]; then
          logMessage "Failed to deploy mysql dump. Deployment failed."
          exit
        else
          if [ "$executedfor" == "dev" ]; then
            startTomcatServer
          elif [ "$executedfor" == "customer" ] && [ "$(checkWildflyServerStatus)" == "running" ]; then
            bash $APP_SERVER_PATH/jboss-cli.sh --connect --commands="deploy $WAR_FILE_PATH/$WAR_FILE_NAME$WAR_FILE_EXT --force,deployment-info --name=$WAR_FILE_NAME$WAR_FILE_EXT, quit"
          fi
        fi
      fi
    fi
    logMessage "Successfully deployed SMART web application." "newline-1"
  else
    logMessage "" "newline-1"
    logMessage "Could not find $BUNDLE_NAME file in the current directory."
    logMessage "Please download the $BUNDLE_NAME and place inside the SMARTSetup folder and then re-run the setup to deploy SMART."
    logMessage "" "newline-1"
  fi
}

generateSMARTPackage(){
  #1. Build source code- Generate war and common folder tar
  bash scripts/generateSMARTPackage.sh
  #2. Generate Mysql dump
  #bash scripts/createRestoreMYSQLDump.sh createdump
  bash scripts/copyDefaultSMARTDump.sh
  if [ $? -ne 0 ]; then
    logMessage "Failed to create SMART installables."
  else
    #3. Tar the entire content to send it to onsite.
    logMessage "Compressing $INSTALLABLES_DIR folder..." "newline-1"
    tar -czf $BUNDLE_NAME "$INSTALLABLES_DIR/"
    if [ $? -ne 0 ]; then
      logMessage "Failed to compress $INSTALLABLES_DIR."
    else
      logMessage "Successfully compressed SMART Installables ($BUNDLE_NAME) in the current directory."
    fi
  fi
}

generateSMARTDemoPackage(){
  #1. Build source code- Generate war and common folder tar
  bash scripts/generateSMARTPackage.sh "demo"
  #2. Generate Mysql dump
  bash scripts/createRestoreMYSQLDump.sh createdump
  bash scripts/createRestoreMongoDump.sh createdump
  #bash scripts/copyDefaultSMARTDump.sh "demo"
  if [ $? -ne 0 ]; then
    logMessage "Failed to create SMART installables."
  else
    #3. Tar the entire content to send it to onsite.
    logMessage "Compressing $INSTALLABLES_DIR folder..." "newline-1"
    tar -czf $BUNDLE_NAME "$INSTALLABLES_DIR/"
    if [ $? -ne 0 ]; then
      logMessage "Failed to compress $INSTALLABLES_DIR."
    else
      logMessage "Successfully compresed SMART Installables ($BUNDLE_NAME) in the demo directory."
      mv $BUNDLE_NAME $DEMO_PKG_FOLDER/
      rm -rf $INSTALLABLES_DIR/*.*
    fi
  fi
}

softwareInfoDeveloperOption(){
  logMessage "Below software are required for setting up SMART Web Application development environment"
  logMessage "	1. Application Server	: TOMCAT server (apache-tomcat-9.0.12)"
  logMessage "	2. Java			: JDK 1.8.0_152"
  logMessage "	3. Maven		: MAVEN 3.2.3"
  logMessage "	4. Database		: MARIADB "
  logMessage "	5. Database		: MONGODB "
  logMessage "	6. EXPECT		: EXPECT "
  logMessage "	7. CPAN			: CPAN "
  logMessage "	8. SSH			: SSH "
  logMessage "	9. ZIP		        : 7ZIP "
  logMessage "  	10.Database		: CURL "
  logMessage "Please use the following command to execute the script : \"bash SMARTWebApp.sh\"" "newline-1"
}

performDevOperations(){
  logMessage "*************************** SMARTConfig[$(date +"%d-%m-%Y %T")] ****************************" "newline-2"
  if [ "$MODE" = "1" ]  || [ "$MODE" == "SMART-Web-Software-Info" ]; then
    logMessage "SMART software Info"
    softwareInfoDeveloperOption
  elif [ "$MODE" = "2" ] || [ "$MODE" == "SMART-Install" ]; then
    setupInstallationPath
    if [ $? -ne 0 ]; then
      logMessage "Installation failed invalid path."
    else
      logMessage "Selection: Developer Installation." "newline-1"
      logMessage  "This will install the following softwares:"
      logMessage  "     1. TOMCAT server(9.0.0.CR2)"
      logMessage  "     2. JDK 1.8.0_152"
      logMessage  "     3. MAVEN 3.2.3"
      logMessage  "     4. MAVEN 5.5"
      logMessage  "     5. MONGODB"
      logMessage  "     6. EXPECT"
      logMessage  "     7. CPAN"
      logMessage  "     8. SSH"

      checkOSArchitchture
      installTomcat
      installJava
      installMVN
      installMARIADB
      installMONGODB
      installSAR
      installEXPECT
      installCPAN
      installSSH
      installSEVEN
      installCURL
      installPERLPACKAGES
      chgOwnerIns

      exec "$BASH"
    fi
  elif [ "$MODE" = "3" ] || [ "$MODE" == "generate-SMART-package" ]; then
    logMessage "\nSelection: generate SMART package, please wait..."
    generateSMARTPackage	
  elif [ "$MODE" = "4" ] ||  [ "$MODE" == "deploy-SMART" ]; then
    logMessage "Selection: deploy SMART application." "newline-1"
    deploySMART
  elif [ "$MODE" = "5" ] ||  [ "$MODE" == "genearte-war" ]; then
    logMessage "Selection: generate the SMART war file only." "newline-1"
    bash scripts/generateSMARTPackage.sh "war-only"
  elif [ "$MODE" = "6" ] || [ "$MODE" == "clean-deployment" ]; then
    logMessage "Selection: clean deployment." "newline-1"
    cleanDeployment
  elif [ "$MODE" = "7" ] ||  [ "$MODE" == "deploy-war" ]; then
    logMessage "Selection: deploy the SMART war file only." "newline-1"
    deployWar
  elif [ "$MODE" = "8" ]  || [ "$MODE" == "start-Wildfly-server" ]; then
    startTomcatServer
  elif [ "$MODE" = "9" ]  || [ "$MODE" == "stop-Wildfly-server" ]; then
    stopTomcatServer
  elif [ "$MODE" = "10" ] || [ "$MODE" == "create-mysql-dump" ]; then
    logMessage "Selection: Create Mysql dump." "newline-1"
    bash scripts/createRestoreMYSQLDump.sh createdump
  elif [ "$MODE" = "11" ]  || [ "$MODE" == "restore-mysql-dump" ]; then
    logMessage "Selection: Restore the dump" "newline-1"
    bash scripts/createRestoreMYSQLDump.sh restoredump
  elif [ "$MODE" = "12" ]  || [ "$MODE" == "config-mysql" ]; then
    configMysqlPassword
  elif [ "$MODE" = "13" ]  || [ "$MODE" == "exit-setup" ]; then
    logMessage "Exit script" "newline-1"
    exit
  fi
}

performDemoOperations(){
  logMessage "*************************** SMARTConfig[$(date +"%d-%m-%Y %T")] ****************************" "newline-2"
  if [ "$MODE" = "1" ] || [ "$MODE" == "SMART-Install" ]; then
    setupInstallationPath
    if [ $? -ne 0 ]; then
      logMessage "Installation failed invalid path."
    else
      logMessage "Selection: Developer Installation." "newline-1"
      logMessage "This will install the following softwares:"
      logMessage "  1. TOMCAT server(9.0.12)"
      logMessage "  2. JDK 1.8.0_152"
      logMessage "  3. MAVEN 3.2.3"
      logMessage "  4. MARIADB 10.2.14"
      logMessage "  5. MONGODB"
      logMessage "  6. EXPECT"
      logMessage "  7. CPAN"
      logMessage "  8. SSH"

      checkOSArchitchture
      installTomcat
      installJava
      installMVN
      installMARIADB
      installMONGODB
      installSAR
      installEXPECT
      installCPAN
      installSSH
      installSEVEN
      installCURL
      installPERLPACKAGES
      chgOwnerIns

      exec "$BASH"
    fi
  elif [ "$MODE" = "2" ] || [ "$MODE" == "generate-SMART-Demo-package" ]; then
    logMessage "\nSelection: generate SMART package, please wait..."
    generateSMARTDemoPackage	
  elif [ "$MODE" = "3" ] ||  [ "$MODE" == "deploy-SMART-Demo" ]; then
    logMessage "Selection: deploy SMART application." "newline-1"
    deploySMARTDemo
  elif [ "$MODE" = "4" ] || [ "$MODE" == "clean-deployment" ]; then
    logMessage "Selection: clean deployment." "newline-1"
    cleanTomcat
  elif [ "$MODE" = "5" ]  || [ "$MODE" == "start-Tomcat-server" ]; then
    startTomcatServer
  elif [ "$MODE" = "6" ]  || [ "$MODE" == "stop-Tomcat-server" ]; then
    stopTomcatServer
  elif [ "$MODE" = "7" ]  || [ "$MODE" == "exit-setup" ]; then
    logMessage "Exit script" "newline-1"
  elif [ "$MODE" = "8" ]  || [ "$MODE" == "Trim-MariaDB" ]; then
    validateDBTrimConfig
    if [[ $MARIADB_TRIM_ENABLE != "enabled" ]];then
        logMessage "MariaDB Trim is disabled, please enable inside db_trim.config"
        exit 1
    fi
    performMariaDBTrimming
  elif [ "$MODE" = "9" ]  || [ "$MODE" == "Trim-MariaDB" ]; then
    validateDBTrimConfig
    if [[ $MONGO_TRIM_ENABLE != "enabled" ]];then
        logMessage "MongoDB Trim is disabled, please enable inside db_trim.config"
        exit 1
    fi
    performMongoDBTrimming
  elif [ "$MODE" = "10" ]  || [ "$MODE" == "Sync-Backup-DB" ]; then
    validateDBTrimConfig
    performSyncDBToBackupServer
    exit
  fi
}
isDateTimeValid() {
   tm_date=`echo $1 | cut -d " " -f1`
   tm_time=`echo $1 | cut -d " " -f2`
   if [[ $tm_date =~ ^[0-9]{4}-[0-9]{2}-[0-9]{2}$ && $tm_time =~ ^[0-9]{2}:[0-9]{2}:[0-9]{2}$ ]]
   then
      inFormat=0
   else
      inFormat=1
   fi
   date "+%Y%m%d%H%M%S" -d "$db_trim_date" > /dev/null 2>&1
   res=$?
   if [[ $inFormat -ne 0 || $res -ne 0 ]]; then
      return 1
   fi
   return 0
}
validateDBTrimConfig() {
  
  for path in PRD_DUMP_DIR PRD_SERVER_BASE_SRC_PATH PRD_TOMCAT_PATH
  do
      if [ ! -d "${!path}" ]; then
         logMessage "$path ${!path} not exist!"
         exit 1
      fi
  done
}
performMariaDBTrimming() {
   logMessage "\nStarted Trimming MariaDB" 
   logMessage "Started at: `date "+%Y-%m-%d %H:%M:%S"`"
   db_user=`getSqlUserName`
   db_password=`getSqlPassword`
   db_host=$MYSQL_SERVER_IP
   db_name=$MYSQL_DB_NAME
   logMessage "DB trim Date Format: %Y-%m-%d %H:%M:%S, example 2019-01-01 11:01:01"
   read -p "DB trim Date: " db_trim_date
   isDateTimeValid "$db_trim_date"
   res=$?
   if [[ $res -ne 0 ]]; then
      logMessage "\nIncorrect date format, format should be %Y-%m-%d %H:%M:%S"
      exit 1
   fi
   logMessage "Taking mysqldb dump, please wait.."
   bash scripts/createRestoreMYSQLDump.sh createdump_trim "$db_trim_date"
   if [[ $? -ne 0 ]]; then
       logMessage "\n MariaDB dump failed, retrying again"
       bash scripts/createRestoreMYSQLDump.sh createdump_trim "$db_trim_date"
       if [[ $? -ne 0 ]]; then
          logMessage "\n MariaDB dump failed, can not proceed, please dump issue to continue.."
          exit 1
       fi
   fi
   python scripts/mysql_db_trim.py $db_user $db_password $db_host $db_name "$db_trim_date"
   if [[ $? -ne 0 ]]; then
      logMessage "\nMariaDB Trim Failed!"
      exit 1
   fi
   logMessage "Completed Trimming the MariaDB" 
   logMessage "Completed at: `date "+%Y-%m-%d %H:%M:%S"`"
}

performMongoDBTrimming() {
   logMessage "\nStarted Trimming MongoDB"
   logMessage "Started at: `date "+%Y-%m-%d %H:%M:%S"`"
   db_user=`getSqlUserName`
   db_password=`getSqlPassword`
   db_host=$MYSQL_SERVER_IP
   db_name=$MYSQL_DB_NAME
   db_mongo_name=$MYSQL_DB_NAME2
   logMessage "DB trim Date Format: %Y-%m-%d %H:%M:%S, example 2019-01-01 11:01:01"
   read -p "DB trim Date: " db_trim_date
   isDateTimeValid "$db_trim_date"
   res=$?
   if [[ $res -ne 0 ]]; then
      logMessage "\nIncorrect date format, format should be %Y-%m-%d %H:%M:%S"
      exit 1
   fi 
   logMessage "Taking mongodb dump, please wait.."
   bash scripts/createRestoreMongoDump.sh createdump_trim "$db_trim_date"
   if [[ $? -ne 0 ]]; then
      logMessage "\n MongoDB dump failed, retrying again"
      bash scripts/createRestoreMongoDump.sh createdump_trim "$db_trim_date"
      if [[ $? -ne 0 ]]; then
          logMessage "\n MongoDB dump failed, can not proceed, please dump issue to continue.."
          exit 1
      fi
   fi
   python scripts/mongodb_trim.py $db_user $db_password $db_host $db_name $db_mongo_name "$db_trim_date"
   if [[ $? -ne 0 ]]; then
      logMessage "\nMongoDB Trim Failed!"
      exit 1
   fi
   logMessage "Completed Trimming the MongoDB" 
   logMessage "Completed at: `date "+%Y-%m-%d %H:%M:%S"`"
}
performSyncDBToBackupServer() {
   db_user=`getSqlUserName`
   db_password=`getSqlPassword|sed 's/ //g'`
   db_name=$MYSQL_DB_NAME 
   mysqlDumpFilePath=$PRD_DUMP_DIR/latestBackup/$MYSQL_DB_NAME.sql
   mongoDumpFilePath="$PRD_DUMP_DIR/latestBackup/$MYSQL_DB_NAME2"
   mysqlCmdString="mysqldump -u $db_user -p'$db_password' $db_name --skip-add-drop-table --skip-triggers --insert-ignore --skip-lock-tables --single-transaction "
   if [[ $BKP_SERVER_IP =~ ^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
      logMessage "BACKUP SERVER IP: $BKP_SERVER_IP"
   else
      logMessage "Invalid IP address, Please provide valid IP" "newline-1"
   fi
   ping -c 2 $BKP_SERVER_IP &> /dev/null
   if [[ $? -ne 0 ]]; then
      logMessage "Not able to connect to the backup server, please check the IP." "newline-1"
      exit 1
   fi
   if [[ ! -f $mysqlDumpFilePath ]] && [[ ! -f $mongoDumpFilePath ]]; then
      logMessage "MariaDB and MongoDb dump files are not found, please run trim before synchronizing/merging the data." "newline-1"
      exit 1
   fi
   if [[ ! -f $mysqlDumpFilePath ]];then
      logMessage "MariaDB dump files are not found, please run MariaDB trim.."
   fi
   if [[ ! -d $mongoDumpFilePath ]];then
      logMessage "MongoDB dump files are not found, please run MongoDB trim.."
   fi
   cmd="$mysqlCmdString > $BKP_SERVER_DEST_PATH/$MYSQL_DB_NAME"_bkp.sql" ;sudo mongod --dbpath /data/db --logpath /var/log/mongodb/mongod.log --fork ; mongodump -d SMART_Config -o $BKP_SERVER_DEST_PATH/SMART_Config_bkp"
   logMessage "Copying dump files to backup server.."
   scp -r $mysqlDumpFilePath $mongoDumpFilePath $BKP_SERVER_IP:$BKP_SERVER_DEST_PATH/
   if [[ "$?" -ne 0 ]]; then
      logMessage "\nCopying dump files to backup server failed!, please check the destination path."
      exit 1
   fi
   logMessage "Copied dump files to backup server.."
   logMessage "Deploying war file is $DEPLOY_WAR_FILE"
   if [[ $DEPLOY_WAR_FILE = "enabled" ]];then
       logMessage "Copying latest ROOT.war to backup server.."
       scp -r $PRD_TOMCAT_PATH/webapps/ROOT.war $BKP_SERVER_IP:$BKP_TOMCAT_PATH/webapps/
       if [[ "$?" -ne 0 ]]; then
           logMessage "\nCopying ROOT.war to the backup server failed!"
           exit 1
       fi
       logMessage "Copied ROOT.war to the backup server.."
       cmd="${cmd}; source ~/.bashrc ; $BKP_TOMCAT_PATH/bin/shutdown.sh; sleep 10; $BKP_TOMCAT_PATH/bin/startup.sh"
   fi
   #echo "cmd: $cmd"
   logMessage "SRCT base path synchronization is $RSYNC_ENABLE"
   if [[ $RSYNC_ENABLE = "enabled" ]];then
       logMessage "Synchronizing SRCT base data to the backup server.."
       echo "python scripts/sync_files.py --hosts $BKP_SERVER_IP --src $PRD_SERVER_BASE_SRC_PATH --dest $BKP_SERVER_DEST_BASE_PATH"
       python scripts/sync_files.py --hosts $BKP_SERVER_IP --src $PRD_SERVER_BASE_SRC_PATH --dest $BKP_SERVER_DEST_BASE_PATH
       if [[ $? -ne 0 ]]; then
           logMessage "Synchronization failed!"
           exit 1
       fi
       logMessage "Completed synchronizing 1 week old files to the backup server"
   fi
   logMessage "Taking MariaDB and MongoDB backup on backup server.." "newline-1"
   nohup ssh $BKP_SERVER_IP "$cmd" 
   if [[ "$?" -ne 0 ]]; then
        logMessage "\nTaking dump on backup server failed!"
        exit 1
   fi
   logMessage "Taken MariaDB and MongoDB backup on backup server successfully.." "newline-1"
   logMessage "Dump path on backup server: $BKP_SERVER_DEST_PATH" "newline-1"
   logMessage "Merging MariaDB and MongoDB trimmed data to the backup server is $MERGING_DB"
   if [[ $MERGING_DB = "enabled" ]];then
       logMessage "Merging MariaDB and MongoDB trimmed data to the backup server DB, please wait.."
       nohup ssh $BKP_SERVER_IP "mysql -u $db_user -p'$db_password' --force < $BKP_SERVER_DEST_PATH/$MYSQL_DB_NAME.sql ; mongorestore -d $MYSQL_DB_NAME2 $BKP_SERVER_DEST_PATH/$MYSQL_DB_NAME2" 
       #nohup ssh $BKP_SERVER_IP "mysql -u $db_user -p'$db_password' --force < $BKP_SERVER_DEST_PATH/$MYSQL_DB_NAME.sql ; ls $BKP_SERVER_DEST_PATH/$MYSQL_DB_NAME2 > $BKP_SERVER_DEST_PATH/collections; input=\"$BKP_SERVER_DEST_PATH/collections\";while IFS= read -r line; do col=\"\${line%.*}\"; echo \"Importing Collection : \$col\"; mongoimport -d $MYSQL_DB_NAME2 -c \"\$col\" --file $BKP_SERVER_DEST_PATH/SMART_Config/\$line --upsert; done < \"\$input\""
       if [[ "$?" -ne 0 ]]; then
           logMessage "\nMerging data to the backup server failed!"
           exit 1
       fi
      logMessage "Completed merging the latest dump file to backup server"
   fi
}
displayDevMenu(){
  while true; do
    echo -e " Commands:"
    echo -e "          [1]  SMART-Web-Software-Info\t- SMART Web Software Info"
    echo -e "          [2]  SMART-Install\t\t- New Installation"
    echo -e "          [3]  Generate-SMART-package\t- Generates SMART package(war, sql and smart tar)"
    echo -e "          [4]  Deploy-SMART\t\t- Deploy the SMART Package (war, sql and SMARTConfig folder)"
    echo -e "          [5]  Generate-war\t\t- Generates SMART War File"
    echo -e "          [6]  Clean-deployment\t\t- Cleans Tomcat server deployment"
    echo -e "          [7]  Deploy-SMART\t\t- Deploy SMART War File"
    echo -e "          [8]  Start-Tomcat-server\t- Start Tomcat server"
    echo -e "          [9]  Stop-Tomcat-server\t- Stop Tomcat Server"
    echo -e "          [10] Create-mariaDB-dump\t- Generates mariaDB Dump"
    echo -e "          [11] Restore-mariaDB-dump\t- Restore MariaDB Dump"
    echo -e "          [12] Config-mariaDB\t\t- MariaDB Ip and Password Configuration for SMARTConfig"
    echo -e "          [13] Exit-setup\t\t- Exits the setup"

    read -p "Please enter a number to select the mode: " MODE
    if [ ! -z $MODE ]; then
      break
    fi
  done

  performDevOperations
}

displayDemoMenu(){
  while true; do
    echo -e " Commands:"
    echo -e "          [1]  SMART-Install\t\t\t- New Installation"
    echo -e "          [2]  generate-SMART-Demo-package\t- Creates SMART package (tar.gz with war, sql and SMARTConfig folder)"
    echo -e "          [3]  deploy-SMART-Demo\t\t- Deploy the SMART Package (war, sql and SMARTConfig folder)"
    echo -e "          [4]  clean-deployment\t\t\t- Cleans Tomcat server deployment"
    echo -e "          [5]  start-Tomcat-server\t\t- Start Tomcat server"
    echo -e "          [6]  stop-Tomcat-server\t\t- Stop Tomcat Server"
    echo -e "          [7]  exit-setup\t\t\t- Exits the setup"
    echo -e "          [8]  Trim-MariaDB\t\t\t- Trimming MariaDB till the given date"
    echo -e "          [9]  Trim-MongoDB\t\t\t- Trimming MongoDB till the given date"
    echo -e "          [10] Sync-To-Backup-DB-Server\t\t- Sync DB to Backup Server"

    read -p "Please enter a number to select the mode: " MODE
    if [ ! -z $MODE ]; then
      break
    fi
  done

  performDemoOperations
}

installMongodbPkg(){
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv EA312927
echo "deb http://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/3.2 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.2.list
sudo apt-get update
sudo apt-get install -y mongodb-org
sudo systemctl start mongod
sudo systemctl status mongod
sudo systemctl enable mongod
}

if [ "$1" == "dev" ]; then
  executedfor="dev"
  displayDevMenu
elif [ "$1" == "demo" ]; then
  executedfor="demo"
  displayDemoMenu
else
  executedfor="demo"
  displayDemoMenu
fi

