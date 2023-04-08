#!/usr/bin/python
# This script is written to trim the database tables untill the user given datetime 
# in an active server so that it reduces the workload and speed up the server.
# All the trimmed data will be appended to a backup server where all the data will be available
#
#
import sys
if sys.version_info < (3, 8):
    print('Please upgrade your Python version to 3.8.0 or higher')
    sys.exit(1)

import mysql.connector
import datetime
import pymongo

if len(sys.argv) < 7:
   print("Please enter the date to trim the data till the date:\n")
   print("Usage:" + sys.argv[0] +  " <user> <password> <host> <mysql_db_name> <mongo_db_name> <trim_datetime>((format: %Y-%m-%d %H:%M:%S))")
   sys.exit(1)
db_user = sys.argv[1]
db_password = sys.argv[2]
db_host = sys.argv[3]
db_name = sys.argv[4]
db_name_mongo = sys.argv[5]
backup_date = sys.argv[6]
date_format = '%Y-%m-%d %H:%M:%S'
try:
  date_obj = datetime.datetime.strptime(backup_date, date_format)
except ValueError:
  print("Incorrect date format, format should be %Y-%m-%d %H:%M:%S")
  sys.exit(1)
#conn = mysql.connector.connect(user='root', password='root123', host='127.0.0.1', database='RctUserMgmt')
delete_collection_list_rw = ""
conn = mysql.connector.connect(user=db_user, password=db_password, host=db_host, database=db_name)
cursor = conn.cursor()
query = "SELECT PROGRAME_NAME_ID, CIQ_FILE_NAME FROM "+str(db_name)+ " .CIQ_UPLOAD_AUDITTRAIL where CREATION_DATE < '"+str(backup_date)+"';"
cursor.execute(query)
rawdata = cursor.fetchall()
for i in range(len(rawdata)):
       if i == 0 and not delete_collection_list_rw:
            delete_collection_list_rw = str(rawdata[i][0])+"_"+str(rawdata[i][1])
       else:
            delete_collection_list_rw = str(delete_collection_list_rw) +","+str(rawdata[i][0])+"_"+str(rawdata[i][1])

query = "SELECT PROGRAME_NAME_ID, CIQ_FILE_NAME, CHECK_LIST_FILE_NAME FROM "+str(db_name)+ " .CIQ_UPLOAD_AUDITTRAIL where CREATION_DATE < '"+str(backup_date)+"';"
cursor.execute(query)
rawdata = cursor.fetchall()
for i in range(len(rawdata)):
       if str(rawdata[i][2]):
            delete_collection_list_rw = str(delete_collection_list_rw) +","+str(rawdata[i][0])+"_"+str(rawdata[i][2])+"_"+str(rawdata[i][1])
       else :
            delete_collection_list_rw = str(delete_collection_list_rw) +","+str(rawdata[i][0])+"_"+str(rawdata[i][1])
            
client = pymongo.MongoClient("mongodb://"+ str(db_host))
#db = client["SMART_Config"]
db = client[str(db_name_mongo)]
#print(db.list_collection_names())
delete_collection_list=delete_collection_list_rw.split(",")
if not db.list_collection_names():
	print("No collections found to trim")
	exit(0)
count = 0
for col in db.list_collection_names():
  col = db[col]
  count = count + int(col.count_documents({}))
  if col.name in delete_collection_list:
     col.drop()
     print("Deleted collection: "+ str(col.name))
#  else:
#      print("Collection not found: "+ str(col))
print("Total documents count:", count)
print("Trimming mysql DB..")
cursor.execute("SET SQL_SAFE_UPDATES=0;")
query = "DELETE FROM "+ str(db_name)+ ".CIQ_UPLOAD_AUDITTRAIL where CREATION_DATE <'"+str(backup_date)+"'"
print("EXECUTING: "+ query)
cursor.execute(str(query))
conn.commit()
print('Number of rows deleted', cursor.rowcount)
cursor.execute("SET SQL_SAFE_UPDATES=1;")
cursor.close()
conn.close()
print("Trimming is successfull")
