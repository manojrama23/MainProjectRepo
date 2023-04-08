#!/usr/bin/python
#!/usr/bin/env python
# This script is written to trim the database tables untill the user given datetime 
# in an active server so that it reduces the workload and speed up the server.
# All the trimmed data will be appended to a backup server where all the data will be available
#
#
import sys
import mysql.connector
import datetime

if len(sys.argv) < 6:
   print("Please enter the date to trim the data till the date: \n")
   print("Usage:" + sys.argv[0] +  " <user> <password> <host> <db_name> <trim_datetime>((format: %Y-%m-%d %H:%M:%S))")
   sys.exit(0)
db_user = sys.argv[1]
db_password = sys.argv[2]
db_host = sys.argv[3]
db_name = sys.argv[4]
backup_date = sys.argv[5]
date_format = '%Y-%m-%d %H:%M:%S'
try:
  date_obj = datetime.datetime.strptime(backup_date, date_format)
except ValueError:
  print("Incorrect date format, format should be %Y-%m-%d %H:%M:%S")
  sys.exit(1)

#conn = mysql.connector.connect(user='root', password='root123', host='127.0.0.1', database='RctUserMgmt')
conn = mysql.connector.connect(user=db_user, password=db_password, host=db_host, database=db_name)
#conn = mysql.connector.connect(user=db_user, database=db_name)
cursor = conn.cursor()
q = "SELECT TABLE_NAME AS `Table` FROM information_schema.TABLES WHERE TABLE_SCHEMA = '"+str(db_name)+"'  ORDER BY(DATA_LENGTH + INDEX_LENGTH) DESC;"
cursor.execute(str(q))
rawdata = cursor.fetchall()
cursor.execute("SET SQL_SAFE_UPDATES=0;")
print("Trimming mysql DB..")
query="DELETE child \
FROM MIG_RUN_TEST_RESULT AS child \
INNER JOIN MIG_RUN_TEST AS parent ON child.RUN_TEST_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child \
FROM AUDIT_5G_DSS_SUMMARY AS child \
INNER JOIN MIG_RUN_TEST AS parent ON child.RUN_TEST_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child \
FROM AUDIT_5G_DSS_ISSUE AS child \
INNER JOIN MIG_RUN_TEST AS parent ON child.RUN_TEST_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child \
FROM AUDIT_5G_CBAND_SUMMARY AS child \
INNER JOIN MIG_RUN_TEST AS parent ON child.RUN_TEST_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child \
FROM AUDIT_4GFSU_SUMMARY AS child \
INNER JOIN MIG_RUN_TEST AS parent ON child.RUN_TEST_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child \
FROM AUDIT_4GFSU_ISSUE AS child \
INNER JOIN MIG_RUN_TEST AS parent ON child.RUN_TEST_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child \
FROM AUDIT_4G_ISSUE AS child \
INNER JOIN MIG_RUN_TEST AS parent ON child.RUN_TEST_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child \
FROM  AUDIT_4G_SUMMARY AS child \
INNER JOIN MIG_RUN_TEST AS parent ON child.RUN_TEST_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child \
FROM AUDIT_5G_CBAND_ISSUE AS child \
INNER JOIN MIG_RUN_TEST AS parent ON child.RUN_TEST_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child \
FROM OV_SCHEDULED_DETAILS AS child \
INNER JOIN WORK_FLOW_MANAGE_DETAILS AS parent ON child.WFM_RUN_TEST_ID = parent.ID \
INNER JOIN MIG_RUN_TEST AS grandparent ON parent.MIG_RUN_TEST_ID = grandparent.ID \
OR parent.NEGROW_RUN_TEST_ID = grandparent.ID \
OR parent.NE_STATUS_RUN_TEST_ID = grandparent.ID \
OR parent.POST_MIG_RUN_TEST_ID = grandparent.ID \
OR parent.PRE_AUDIT_RUN_TEST_ID = grandparent.ID \
OR parent.RAN_ATP_RUN_TEST_ID = grandparent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child \
FROM WORK_FLOW_MANAGE_DETAILS AS child \
INNER JOIN MIG_RUN_TEST AS parent ON child.MIG_RUN_TEST_ID = parent.ID \
OR child.NEGROW_RUN_TEST_ID = parent.ID \
OR child.POST_MIG_RUN_TEST_ID = parent.ID \
OR child.NE_STATUS_RUN_TEST_ID = parent.ID \
OR child.PRE_AUDIT_RUN_TEST_ID = parent.ID \
OR child.RAN_ATP_RUN_TEST_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child \
FROM MIG_RUN_TEST_INPUT AS child \
INNER JOIN MIG_RUN_TEST AS parent ON child.RUN_TEST_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE FROM AUDIT_4GFSU_PASSFAIL WHERE CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE FROM AUDIT_4G_PASSFAIL WHERE CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE FROM AUDIT_5GCBAND_PASSFAIL WHERE CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE FROM AUDIT_5GDSS_PASSFAIL WHERE CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child FROM AUDIT_CRITICAL_PARAMS_INDEX1 AS child \
INNER JOIN AUDIT_CRITICAL_PARAMS_SUMMARY AS parent ON child.INDEX_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child FROM AUDIT_CRITICAL_PARAMS_INDEX2 AS child \
INNER JOIN AUDIT_CRITICAL_PARAMS_SUMMARY AS parent ON child.INDEX_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child FROM AUDIT_CRITICAL_PARAMS_INDEX3 AS child \
INNER JOIN AUDIT_CRITICAL_PARAMS_SUMMARY AS parent ON child.INDEX_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child FROM AUDIT_CRITICAL_PARAMS_INDEX4 AS child \
INNER JOIN AUDIT_CRITICAL_PARAMS_SUMMARY AS parent ON child.INDEX_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child FROM AUDIT_CRITICAL_PARAMS_INDEX5 AS child \
INNER JOIN AUDIT_CRITICAL_PARAMS_SUMMARY AS parent ON child.INDEX_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child FROM AUDIT_CRITICAL_PARAMS_INDEX6 AS child \
INNER JOIN AUDIT_CRITICAL_PARAMS_SUMMARY AS parent ON child.INDEX_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE FROM AUDIT_CRITICAL_PARAMS_SUMMARY WHERE CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE FROM OV_UPDATE_RUN_TEST_RESULT WHERE CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE parent \
FROM MIG_RUN_TEST AS parent \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"';\
\
DELETE child \
FROM OV_UPDATE_RUN_TEST_RESULT AS child \
INNER JOIN MIG_RUN_TEST AS parent ON child.RUN_TEST_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE child \
FROM MIG_USE_CASE_XML_RULE AS child \
INNER JOIN MIG_USE_CASE_BUILDER_SCRIPTS AS parent ON child.SCRIPTS_ID = parent.SCRIPTS_ID \
INNER JOIN MIG_UPLOADED_SCRIPT_DETAILS AS grandparent ON parent.SCRIPT_DETAILS_ID = grandparent.ID \
WHERE grandparent.CREATION_DATE < '"+str(backup_date)+"' AND (grandparent.MIGRATION_TYPE != 'PostMigration' \
AND grandparent.SUB_TYPE!='PREAUDIT' AND grandparent.SUB_TYPE!='NESTATUS' ); \
\
DELETE child \
FROM MIG_USE_CASE_COMMAND_RULE AS child \
INNER JOIN MIG_USE_CASE_BUILDER_SCRIPTS AS parent ON child.SCRIPTS_ID = parent.SCRIPTS_ID \
INNER JOIN MIG_UPLOADED_SCRIPT_DETAILS AS grandparent ON parent.SCRIPT_DETAILS_ID = grandparent.ID \
WHERE grandparent.CREATION_DATE < '"+str(backup_date)+"' AND (grandparent.MIGRATION_TYPE != 'PostMigration' \
AND grandparent.SUB_TYPE!='PREAUDIT' AND grandparent.SUB_TYPE!='NESTATUS' ); \
\
DELETE child \
FROM MIG_USE_CASE_BUILDER_SCRIPTS AS child \
INNER JOIN MIG_UPLOADED_SCRIPT_DETAILS AS parent ON child.SCRIPT_DETAILS_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"' AND (parent.MIGRATION_TYPE != 'PostMigration' \
AND parent.SUB_TYPE!='PREAUDIT' AND parent.SUB_TYPE!='NESTATUS' ); \
\
DELETE child \
FROM MIG_RUN_TEST_RESULT AS child \
INNER JOIN MIG_UPLOADED_SCRIPT_DETAILS AS parent ON child.SCRIPT_ID = parent.ID \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"' AND (parent.MIGRATION_TYPE != 'PostMigration' \
AND parent.SUB_TYPE!='PREAUDIT' AND parent.SUB_TYPE!='NESTATUS' ); \
\
DELETE parent \
FROM MIG_UPLOADED_SCRIPT_DETAILS AS parent \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"' AND (parent.MIGRATION_TYPE != 'PostMigration' \
AND parent.SUB_TYPE!='PREAUDIT' AND parent.SUB_TYPE!='NESTATUS' ); \
\
DELETE parent \
FROM PREMIGRATION_OV_UPDATE_RUN_TEST_RESULT AS parent \
WHERE parent.CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE parent \
FROM GENERATE_INFO_AUDIT AS parent \
WHERE parent.GENERATION_DATE < '"+str(backup_date)+"'; \
\
DELETE parent \
FROM AUDIT_TRAIL AS parent \
WHERE parent.ACTION_PERFORMED_DATE < '"+str(backup_date)+"'; \
\
DELETE parent \
FROM MIG_USE_CASE_BUILDER AS parent \
WHERE parent.USE_CASE_CREATION_TIME < '"+str(backup_date)+"' AND (parent.MIGRATION_TYPE != 'PostMigration' \
AND parent.SUB_TYPE!='PREAUDIT' AND parent.SUB_TYPE!='NESTATUS' ); \
\
DELETE FROM SITEREPORT_OV_UPDATE_RUN_TEST_RESULT WHERE CREATION_DATE < '"+str(backup_date)+"'; \
\
DELETE FROM SITE_DATA_LIST WHERE PACKED_DATE < '"+str(backup_date)+"'; \
\
DELETE FROM PARTIAL_SITE_DATA_LIST WHERE PACKED_DATE < '"+str(backup_date)+"'; \
\
DELETE FROM REPORTS_DETAILS WHERE REPORTS_GENERATION_DATE < '"+str(backup_date)+"'; \
\
DELETE FROM OV_SCHEDULED_DETAILS WHERE FETCH_DATE <'"+str(backup_date)+"'; \
\
DELETE FROM WORK_FLOW_MANAGE_DETAILS WHERE CREATION_DATE <'"+str(backup_date)+"'; \
\
DELETE FROM 4G_RET_TEST WHERE Time_Stamp <'"+str(backup_date)+"'; \
\
TRUNCATE PRE_POST_MAPPING;"
for q in query.split(";"):
   if q:
      print("EXECUTING QUERY: "+ q)
      cursor.execute(q)
      conn.commit()
      print('Number of rows deleted', cursor.rowcount)
cursor.execute("SET SQL_SAFE_UPDATES=1;")
print("Trimming is successfull")
cursor.close()
conn.close()
