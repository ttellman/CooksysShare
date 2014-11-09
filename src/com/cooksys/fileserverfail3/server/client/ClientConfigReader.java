package com.cooksys.fileserverfail3.server.client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "clientConfig")
public class ClientConfigReader {
	
	
	
	
	
	
	public ClientConfigReader(){};

	
	@XmlElement(name = "port")
	public int getPort() {
		return PORT;
	}

	public void setPort(int Port) {
		this.PORT = Port;
	}

	@XmlElement(name = "Developer")
	public String getName() {
		return developer;
	}

	public void setName(String name) {
		this.developer = name;
	}

	@XmlElement(name = "DownloadPath")
	public String getDOWNLOAD_DIRECTORY_LOCATION() {
		return DOWNLOAD_DIRECTORY_LOCATION;
	}

	public void setDOWNLOAD_DIRECTORY_LOCATION(
			String dOWNLOAD_DIRECTORY_LOCATION) {
		DOWNLOAD_DIRECTORY_LOCATION = dOWNLOAD_DIRECTORY_LOCATION;
	}

	@XmlElement(name = "LocalPath")
	public String getSharedPath() {
		return sharedPath;
	}

	public void setSharedPath(String sharedPath) {
		this.sharedPath = sharedPath;
	}

	public String[] getIpAddresses() {
		return ipAddresses;
	}

	public void setIpAddresses(String[] ipAddresses) {
		this.ipAddresses = ipAddresses;
	}

	int PORT = 2663;
	private String sharedPath;
	String developer = "Timothy Tellman";
	
	private String DOWNLOAD_DIRECTORY_LOCATION = "C:/Users/Tim/Documents/workspace-sts-3.4.0.RELEASE/040814/CooksysShare/share";
	String[] ipAddresses = { "192.168.20.101", "192.168.20.102", "192.168.20.103",
			"192.168.20.104", "192.168.20.108", "192.168.20.111",
			"192.168.20.113", "192.168.20.116", "192.168.20.121",
			"192.168.20.124", "localhost" };

		

	
}
