#!/bin/bash
#!/usr/bin/expect -f 
#
# Please provie the following details as per production and 
# backup server environment before running the script.
. ./scripts/db_trim.conf
number=$1
trim_duration=$2

if [[ $number  -ge 0 && $trim_duration == "" ]];then
   echo -ne "Usage: trim_db.sh <limit> <db-retention-duration>\"\n"
   echo -ne "\nPlease specify value as below:\n   limit: <1..n> \n   db-retention-duration: <Day|Week|Month|Year>\n"
   exit 0
fi
if [[ "$trim_duration" != "Min" && "$trim_duration" != "Hour" && "$trim_duration" != "Day" && "$trim_duration" != "Week" && "$trim_duration" != "Month" && "$trim_duration" != "Year" ]];then 
   echo -ne "Invalid duration value!, please specify <Week|Month|Year> to retain db data\n"
   exit 0
fi
echo "Duration: $number $trim_duration"
DATE=`date "+%Y-%m-%d %H:%M:%S" -d "-$number $trim_duration"`
/usr/bin/expect -<<EOD
set timeout -1
if { "$MARIADB_TRIM_ENABLE" == "enabled" } {
spawn bash SMARTWebApp.sh demo
while (1) {
      expect {
       "Please enter a number to select the mode: " {
        send -- 8\r
        }
       "DB trim Date: " {
        send -- "$DATE\r"
        }
       "^Trimming is successfull" {
        break
       }
    }
  }
}
EOD
/usr/bin/expect -<<EOD
set timeout -1
if { "$MONGO_TRIM_ENABLE" == "enabled" } {
   spawn bash SMARTWebApp.sh demo
   while (1) {
    expect {
     "Please enter a number to select the mode: " {
      send -- 9\r
     }
     "no)? " {
      send -- "yes\r"
     }
     "DB trim Date: " {
      send -- "$DATE\r"
     }
     "password for user: " {
      send -- "$MONGODB_PSWD\r"
     }
     " password: " {
         send -- "$BKP_SEVER_PWD\r"
     }
     "^Trimming is successfull" {
      break
     }
   }
 }
}
EOD

/usr/bin/expect -<<EOD
spawn bash SMARTWebApp.sh demo
set timeout -1
while (1) {
   expect {
     "Please enter a number to select the mode: " {
      send -- 10\r
     }
     "no)? " {
      send -- "yes\r"
     }
     "password for user: " {
      send -- "$BKP_SEVER_PWD\r"
     }
     " password: " {
       send -- "$BKP_SEVER_PWD\r"
     }
     "^Completed merging " {
       break
     }
   }
}
EOD
