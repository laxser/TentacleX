package com.laxser.tentaclex.registry.tomcat;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.laxser.tentaclex.registry.TentacleServiceDescriptor;
import com.laxser.tentaclex.registry.TentacleServiceDescriptorBase;

/**
 * 用于分析tomcat中的server.xml配置文件
 * 
 *@author laxser  Date 2012-6-1 上午8:46:49
@contact [duqifan@gmail.com]
@ServerXmlResolver.java

 */
public class ServerXmlResolver {

	protected Log logger = LogFactory.getLog(ServerXmlResolver.class);
	
	/**
	 * 解析server.xml，从中获取当前tomcat实例上运行了哪些服务节点。
	 * 
	 * @param in
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TentacleServiceDescriptor> resolve(InputStream in) {
		
		SAXReader reader = new SAXReader();
		
		int port = -1;
		List<String> hostNames = new ArrayList<String>();
		try {
			Document document = reader.read(in);
			Element root = document.getRootElement().element("Service");
			List<Element> connectorNodes = root.elements("Connector");
			for (Element connectorNode : connectorNodes) {
				String protocol = connectorNode.attributeValue("protocol");
				if (isTxConnector(protocol)) {
					port = Integer.parseInt(connectorNode.attributeValue("port"));
					if (logger.isInfoEnabled()) {
						logger.info("Found TX connector on port: " + port);
					}
				}
			}
			
			if (port == -1) {
				logger.error("No XOA Connector found.");
				return null;
			}
			
			List<Element> hostNodes = root.element("Engine").elements("Host");
			if (hostNodes.size() == 0) {
				logger.error("No TetacleX Host found.");
				return null;
			}
			
			for (Element hostNode : hostNodes) {
				String hostName = hostNode.attributeValue("name");
				hostNames.add(hostName);
			}
			
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		String localAddress;
		try {
			localAddress = getLocalAddress();
		} catch (UnknownHostException e) {
			logger.error("Can not get local ip address.", e);
			return null;
		}
		
		List<TentacleServiceDescriptor> services = new ArrayList<TentacleServiceDescriptor>();
		for (String hostName : hostNames) {
			
			TentacleServiceDescriptorBase desc = new TentacleServiceDescriptorBase();
			desc.setServiceId(hostName);
			desc.setIpAddress(localAddress);
			desc.setPort(port);
			
			services.add(desc);
		}
		
		return services;
	}
	
	private String getLocalAddress() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}
	
	
	public boolean isTxConnector(String protocol) {
		return "com.laxser.tentaclex.server.coyote.NettyHttpProtocolHandler".equals(protocol);
	}
	
}
