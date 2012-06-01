package com.laxser.tentaclex.registry.tomcat;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import com.laxser.tentaclex.registry.TentacleServiceDescriptor;
import com.laxser.tentaclex.registry.TentacleServiceDescriptorBase;
import com.laxser.tentaclex.registry.impl.zookeeper.RegistryHelper;
import com.laxser.tentaclex.registry.impl.zookeeper.ZookeeperBasedRegistry;



/**
 * 命令处理器
 * 
 * @author laxser  Date 2012-6-1 上午8:46:44
@contact [duqifan@gmail.com]
@CommandHandler.java

 */
public class CommandHandler {
    
    private static final String XOA_CONSOLE = "xoa-console>";
    
    private static final String CMD_QUIT = "quit";
    
    private static final String CMD_HELP = "help";
    
    private static final String SERVER_XML_PATH = "/conf/server.xml";
    
    private static String HELP = "Commands: \r\n" + 
        "    server list this \t#List services running on this node \r\n" +
        "    server list all \t#List all services running on XOA-Grid associated with this node \r\n" +
        "    server disable this \t#Disable all services on this node \r\n" + 
        "    server enable this \t#Enable all services ont this node \r\n" +
        "    server disable [serviceId] [ip:port] \t#Disable the given service node specified by ip and port \r\n" + 
        "    server enable [serviceId] [ip:port] \t#Enable the given service node specified by ip and port \r\n" +
        "    help \t#Show help \r\n" +
        "    quit \t#Quit";
    
    private ServerXmlResolver serverXmlResolver = new ServerXmlResolver();
    
    private ZookeeperBasedRegistry registry = new ZookeeperBasedRegistry();
    
    private RegistryHelper helper = new RegistryHelper();
    
    public CommandHandler() {
        registry.init();
    }
    
    /**
     * 处理命令
     * @param command
     */
    public void handle(String command) {
        int code = handle0(command);
        if (code == -1) {
            badCommand(command);
        }
        printConsoleMark();
    }
    
    /**
     * @param command
     * @return -1表示bad command，0表示执行成功
     */
    private int handle0(String command) {
        if (command.length() == 0) {
            return 0;
        } else if (command.startsWith("server ")) {
            
            String[] args = command.split(" ");
            
            if (args.length < 2) {
                return -1;
            }
            //args[0] == "server"
            if (args[1].equals("list")) {
                
                if (args.length < 3) {
                    return -1;
                }
                
                if (args[2].equals("all")) {    //server list all
                    
                    List<TentacleServiceDescriptor> services = serverListThis();
                    println("");
                    println("Services running on XOA-Grid associated with this node: ");
                    for (TentacleServiceDescriptor service : services) {
                        String serviceId = service.getServiceId();
                        
                        println("Nodes for " + serviceId + ":");
                        List<TentacleServiceDescriptor> nodes = registry.getServiceNodes(serviceId);
                        if (nodes != null && nodes.size() > 0) {
                            for (TentacleServiceDescriptor node : nodes) {
                                println(node);
                            }
                        } else {
                            println("none");
                        }
                    }
                } else if (args[2].equals("this")) {    //server list this
                    serverListThis();
                } else {
                    return -1;
                }
                
            } else if (args[1].equals("enable") || args[1].equals("disable")) {
                if (args.length == 3 && args[2].equals("this")) {   //server enable|disable this
                    
                    List<TentacleServiceDescriptor> services = serverXmlResolver.resolve(locateServerXml());
                    for (TentacleServiceDescriptor service : services) {
                        if(helper.exists(service)) {
                            
                            TentacleServiceDescriptorBase targetDesc = new TentacleServiceDescriptorBase();
                            targetDesc.setServiceId(service.getServiceId());
                            targetDesc.setIpAddress(service.getIpAddress());
                            targetDesc.setPort(service.getPort());
                            if (args[1].equals("disable")) {
                                targetDesc.setDisabled(true);
                            } 
                            boolean succ = helper.updateServiceNode(targetDesc);
                            println((succ ? "Success" : "Fail") + " to " + args[1] + " "
                                    + targetDesc);
                        } else {
                           println("Not exists in registry: " + service); 
                        }
                    }
                } else if(args.length == 4){    //server enable|disable [serviceId] [ip:port] 
                    
                    String serviceId = args[2];
                    String ipport = args[3];
                    String[] ss = ipport.split(":");
                    if (ss.length != 2) {
                        println("bad 'ip:port' :" + command);
                        return 0;
                    }
                    String ip = ss[0];
                    int port = -1;
                    try {
                        port = Integer.parseInt(ss[1]);
                    } catch (NumberFormatException e) {
                        println("bad 'ip:port' :" + command);
                        return 0;
                    }
                    
                    TentacleServiceDescriptor service = new TentacleServiceDescriptorBase().setServiceId(
                            serviceId).setIpAddress(ip).setPort(port);

                    if(helper.exists(service)) {
                        
                        TentacleServiceDescriptorBase targetDesc = new TentacleServiceDescriptorBase();
                        targetDesc.setServiceId(service.getServiceId());
                        targetDesc.setIpAddress(service.getIpAddress());
                        targetDesc.setPort(service.getPort());
                        if (args[1].equals("disable")) {
                            targetDesc.setDisabled(true);
                        } 
                        boolean succ = helper.updateServiceNode(targetDesc);
                        println((succ ? "Success" : "Fail") + " to " + args[1] + " "
                                + targetDesc);
                    } else {
                       println("Not exists in registry: " + service); 
                    }
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } else if (command.equals(CMD_HELP)) {
            printHelp();
        } else if (command.equals(CMD_QUIT)) {
            exit();
        } else {
            return -1;
        }
        return 0;
    }
    
    
    /**
     * 打印帮助
     */
    private void printHelp() {
        println(HELP);
    }
    
    /**
     * 错误的命令
     * @param command
     */
    private void badCommand(String command) {
        println("Bad command: " + command);
        println(HELP);
    }
    
    /**
     * 退出控制台
     */
    private void exit() {
        this.destroy();
        println("Bye~~");
        System.exit(0);
    }
    
    /**
     * @return 当前节点上运行的服务
     */
    private List<TentacleServiceDescriptor> serverListThis() {
        println("Services running on this node:");
        List<TentacleServiceDescriptor> services = serverXmlResolver.resolve(locateServerXml());
        for (TentacleServiceDescriptor service : services) {
            
            TentacleServiceDescriptor descInRegistry = helper.getNodeConfig(service.getServiceId(),
                    service.getIpAddress(), service.getPort());

            StringBuilder msg = new StringBuilder();
            msg.append(service.getServiceId());
            msg.append(",");
            msg.append(service.getIpAddress());
            msg.append(":");
            msg.append(service.getPort());
            msg.append(" (");
            if (descInRegistry != null) {
                
                if (descInRegistry.isDisabled()) {
                    msg.append("Disabled");
                } else {
                    msg.append("Enabled");
                }
                msg.append(" in Registry.");
            } else {
                msg.append("This node is not configured in Registry.");
            }
            msg.append(")");
            println(msg.toString());
        }
        return services;
    }
    
    /**
     * 输出命令提示符
     */
    public void printConsoleMark() {
        println("");
        print(XOA_CONSOLE);
    }
    
    private void print(Object o) {
        System.out.print(o);
    }
    
    private void println(Object o) {
        System.out.println(o);
    }
    
    /**
     * 定位tomcat中的server.xml文件
     * @return server.xml文件的InputStream
     */
    private InputStream locateServerXml() {
        try {
            
            String catalinaHome = System.getProperty("CATALINA_HOME");
            String rootPath;
            if (catalinaHome != null && catalinaHome.length() > 0) {
                rootPath = catalinaHome;
            } else {
                System.err.println("WARN: System environment CATALINA_HOME should be set.");
                rootPath = "..";
            }
            String path = rootPath + SERVER_XML_PATH;
            return new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 销毁相关资源
     */
    public void destroy() {
        registry.destroy();
        helper.destroy();
    }
    
}
