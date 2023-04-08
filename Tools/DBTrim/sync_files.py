#!/usr/bin/python
import subprocess
import argparse
import os
import datetime
import sys
RSYNC_USER = "user"
USER_PSWD = "root123"
date_format = '%Y-%m-%d %H:%M:%S'
def parse_arguments():
    parser = argparse.ArgumentParser()
    parser.add_argument("-n","--hosts", dest="hosts", help="Enter remote host/hosts name, comma seperated", metavar="HOSTS")
    parser.add_argument("-s","--src", dest="source", help="Source directory from where files to synchronized", metavar="SOURCE")
    parser.add_argument("-t","--sync-date", dest="sync_date", help="Sync files/directories till the given datetime", metavar="SYNC_DATE")
    parser.add_argument("-d","--dest", dest="dest", help="Sync/Copy files/directories to the given destination directory in remote host", metavar="DEST_DIR")
    args = parser.parse_args()

    if args.source is None or args.hosts is None or args.dest is None:
        parser.print_help()
        parser.exit()
    if args.sync_date is not None:
        try:
            date_obj = datetime.datetime.strptime(args.sync_date, date_format)
        except ValueError:
            print("Incorrect sync_date date format, format should be %Y-%m-%d %H:%M:%S")
            sys.exit(1)
    if args.source is not None and not os.path.exists(args.source):
        print("Source path doesn't exist: " + str(args.source))
        sys.exit(1)
    #args.exclude = " --exclude " + " --exclude".join(args.exclude.split(','))
    args.hosts = args.hosts.split(',')
    return args.hosts, args.source, args.sync_date, args.dest

def sync(hosts, src, sync_date, dest_dir):
    from_date = "1902-01-01 00:00:00"
    to_date = sync_date
    if sync_date is None:
        from_date = datetime.datetime.today() - datetime.timedelta(days=7)
        to_date = datetime.datetime.today()
    for host in hosts:
        host = host.strip()
        #cmd = "(find "+str(src)+"  -mmin -$(echo $(date +%s) - $(date +%s -d\""+str(from_date)+"\") | bc -l | awk \'{print $1 / 60}\' ) -mmin +$(echo $(date +%s) - $(date +%s -d\""+str(to_date)+"\") | bc -l | awk \'{print $1 / 60}\' ) -exec realpath --relative-to="+str(src)+" \'{}\' \\;)  | sed -n \'1!p\' | rsync -ahrvz --update --files-from=- "+str(src) +" "+ host+":"+str(dest_dir)
        #cmd = "(find "+str(src)+"  -mmin -$(echo $(date +%s) - $(date +%s -d\""+str(from_date)+"\") | bc -l | awk \'{print $1 / 60}\' ) -mmin +$(echo $(date +%s) - $(date +%s -d\""+str(to_date)+"\") | bc -l | awk \'{print $1 / 60}\' ) -exec realpath --relative-to="+str(src)+" \'{}\' \\;)  | sed -n \'1!p\' | rsync -ahrvz --remove-source-files --update --files-from=- "+str(src) +" "+ host+":"+str(dest_dir) +" && find "+str(src) +" -depth -type d  -empty -exec rmdir \'{}\' \\;"
        cmd = "(find "+str(src)+"  -mmin -$(echo $(date +%s) - $(date +%s -d\""+str(from_date)+"\") | bc -l | awk \'{print $1 / 60}\' ) -mmin +$(echo $(date +%s) - $(date +%s -d\""+str(to_date)+"\") | bc -l | awk \'{print $1 / 60}\' ) -exec realpath --relative-to="+str(src)+" \'{}\' \\;)  | sed -n \'1!p\' | rsync -ahrvz --update --files-from=- "+str(src) +" "+ host+":"+str(dest_dir)
        #print("cmd",cmd)
        p = subprocess.Popen(cmd, shell=True, executable='/bin/bash')
        p.wait()
        print("Rsync process completed.")

if __name__ == '__main__':
    hosts, source, sync_date, dest_dir = parse_arguments()
    sync(hosts, source, sync_date, dest_dir)
