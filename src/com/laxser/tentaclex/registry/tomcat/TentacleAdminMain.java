package com.laxser.tentaclex.registry.tomcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * xoa admin console主程序
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2011-4-12 下午06:59:51
 */
public class TentacleAdminMain {

	public static void main(String[] args) {
	    
	    if (args.length == 0) {    //进入控制台
	        System.out.println("Welcome to TentacleX console. Input 'help' for helps.");
	        
	        CommandHandler handler = new CommandHandler();
	        handler.printConsoleMark();
	        
	        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	        while(true) {
	            try {
	                String command = in.readLine();
	                command = command.trim();
	                handler.handle(command);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    } else {   //直接执行单条命令，不进入控制台
	        StringBuilder command = new StringBuilder();
	        for (String arg : args) {
	            command.append(arg);
	            command.append(" ");
	        }
	        CommandHandler handler = new CommandHandler();
	        
	        handler.handle(command.toString());
	        
	        handler.destroy();
	    }
	}
}
