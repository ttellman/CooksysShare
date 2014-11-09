package com.cooksys.fileserverfail3.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import com.cooksys.fileserverfail3.server.interfaces.Commands;

/**
 * @author Tim
 *
 */

public class Server implements Commands {

	private ServerSocket serverSocket;
	private ArrayList<String> connectedClient = new ArrayList<>();
	private File shareDirectory = new File(shareDirectoryLocation);
	private String userName;
	static String shareDirectoryLocation = "share";
	private String developer = "Timothy";
	private final File SERVER_CONFIG_FILE = new File("serverConfig.xml");
	private boolean go = true;

	// not method constructor with run method
	public Server() {
		serverRun();
	};

	public String getShareDirectoryLocation() {

		return shareDirectoryLocation;
	}

	public String getDeveloper() {
		return developer;
	}

	public void setDeveloper(String developer) {
		this.developer = developer;
	}

	// method to create array containing files to be shared
	public File[] getListOfFIles() {

		File[] filesToShare = shareDirectory.listFiles();

		return filesToShare;
	}
	//method to start server 
	public void serverRun() {
		int port = 0;

		try {
			// read port number form server xml file needs to be pulled out and placed in own method 
			JAXBContext jaxb = JAXBContext
					.newInstance(ServerConfigReader.class);
			Unmarshaller unm = jaxb.createUnmarshaller();
			ServerConfigReader serverXMLReader = (ServerConfigReader) unm.unmarshal(new FileReader(SERVER_CONFIG_FILE));
			port = serverXMLReader.getPORT();
		} catch (JAXBException | FileNotFoundException sxr) {
			sxr.printStackTrace();
		}

		try {
			//create new server
			ServerSocket server = new ServerSocket(port);
			while (go) {

				System.out.println("Server is starting waiting for connection on port " + port);
				Socket socket = server.accept();
				//add clients as they connect to array list
				connectedClient.add(socket.getInetAddress().getHostAddress());
				ClientHandler clientHandler = new ClientHandler(socket);

				Thread thread = new Thread(clientHandler);
				thread.start();

			}
		} catch (IOException se1) {
			se1.printStackTrace();
		}

		try {
			serverSocket.close();
		} catch (IOException se2) {
			System.out.println("Error stopping server");
			se2.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Server();
	}

}