package com.smart.rct.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


public class SSHExecution {

	public static void main(String[] args) {
	    String host="10.20.120.199";
	    String user="user";
	    String password="root123";
	    Session session = null;
	    Channel channel= null;
	    try{
	    	BufferedReader reader = new BufferedReader(new FileReader("my_script.sh"));
	    	String line = reader.readLine();
			while (line != null) {
				System.out.println("Executing command : "+line);
				session = doConnect(host, user, password);
				channel=session.openChannel("exec");
				((ChannelExec)channel).setCommand(line);
		        channel.setInputStream(null);
		        ((ChannelExec)channel).setErrStream(System.err);
		        InputStream in=channel.getInputStream();
		        channel.connect();
		        byte[] tmp=new byte[1024];
		        while(true){
		          while(in.available()>0){
		            int i=in.read(tmp, 0, 1024);
		            if(i<0)break;
		            System.out.print(new String(tmp, 0, i));
		          }
		          if(channel.isClosed()){
		            System.out.println("exit-status: "+channel.getExitStatus());
		            break;
		          }
		          try{Thread.sleep(1000);}catch(Exception ee){}
		        }
		        line = reader.readLine();
		        channel.disconnect();
		        session.disconnect();
			}
			reader.close();
	        System.out.println("DONE");
	    }catch(Exception e){
	    	e.printStackTrace();
	    }

	}
	
	private static Session doConnect(String host, String user,  String password){
		Session session = null;
		try{
	    	java.util.Properties config = new java.util.Properties(); 
	    	config.put("StrictHostKeyChecking", "no");
	    	JSch jsch = new JSch();
	    	session=jsch.getSession(user, host, 22);
	    	session.setPassword(password);
	    	session.setConfig(config);
	    	session.connect();
	    	System.out.println("Connected to host: "+host);
		}catch(Exception e){
	    	e.printStackTrace();
	    }
		return session;
	}
	

}
