package com.cooksys.fileserverfail3.server;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name ="serverConfig")
public class ServerConfigReader{

	
	@XmlElement(name = "developer")
	public String getDeveloper() {
		return developer;
	}
	public void setDeveloper(String developer) {
		this.developer = developer;
	}
	@XmlElement(name = "port")
	public int getPORT() {
		return PORT;
	}
	public void setPORT(int pORT) {
		PORT = pORT;
	}
	@XmlElement(name = "SharedPath")
	public String getShareDirectory() {
		return shareDirectory;
	}
	public void setShareDirectory(String shareDirectory) {
		this.shareDirectory = shareDirectory;
	}
	
	String developer = "";
	int PORT;
	String shareDirectory = "";
}
