package com.cooksys.fileserverfail3.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.cooksys.fileserverfail3.server.interfaces.Commands;

public class ClientHandler implements Commands, Runnable {

	private static Socket socket;
	private OutputStream outputStream;
	private InputStream inputStream;
	private boolean go = true;
	private Server server;
	File[] sharedFiles;
	private File shareDirectory = new File(shareDirectoryLocation);
	static String shareDirectoryLocation = "share";

	private static final File SERVER_CONFIG_FILE = new File(
			"serverConfig.xml");

	public ClientHandler(Socket socket) throws IOException {

		this.socket = socket;
		// Instantiate streams to correct sockets.
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
		// write to server console that client connected and from what address
		// and computer name.
		System.out.println("New Client connected to server on Address "
				+ socket.getInetAddress().getHostAddress()
				+ " with the hostname of  "
				+ socket.getInetAddress().getHostName());

		Thread thread = new Thread();
		thread.start();
	}
	//method to unmarshall xml getting name of developer to send to connected client
	public void sendDevName() {
		try {
			String developerName = "";
			JAXBContext jaxb;
			jaxb = JAXBContext.newInstance(ServerConfigReader.class);
			Unmarshaller unmarsh = jaxb.createUnmarshaller();
			ServerConfigReader xmlServerConfig = (ServerConfigReader) unmarsh
					.unmarshal(SERVER_CONFIG_FILE);
			developerName = xmlServerConfig.getDeveloper();
			System.out.println(developerName);
			outputStream.write(developerName.getBytes());
			outputStream.write(END);

		} catch (IOException | JAXBException che1) {
			che1.printStackTrace();
			return;
		}
	}
	//method to deterime what the client needs IE list of files or to send file
	public void clientReply() throws IOException {
		 int command;
		while (go) {
			
			command = inputStream.read();
			try {
				System.out.println("Waiting on client response");
				// read byte command sent from client
				// command = inputStream.read();
				System.out.println("Command from client = " + command);

				// if(command == -1)

				switch (command) {
				// if command is to send file list send list
				case Commands.LIST_FILES:
					System.out.println("sending file list");
					sendFileList();
					break;
				// send file requested from list
				case Commands.SEND_FILE:
					sendFile();
					break;
				case Commands.END:
					System.out.println("End command received bye bye");
				default:
					break;
				}

			} catch (IOException che2) {
				System.out.println("che2");
				che2.printStackTrace();
			}
		
		
		}
		
	}

	@Override
	public void run() {
		sendDevName();


		try {
			clientReply();
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
	//method to send file list across socket to client that is connected 
	private void sendFileList() throws IOException {
		
		sharedFiles = shareDirectory.listFiles();
		System.out.println(sharedFiles.length);
		// send each file name to client returning new list after each file name
		for (File file : sharedFiles) {
			//send the file names in  bytes to client 
			outputStream.write((file.getName()).getBytes());
			outputStream.write(Commands.END);
			System.out.println(file.getName() + Commands.END);
		}
		//send of list command so client knows that this is the complete list of available files 
		outputStream.write(Commands.END_OF_LIST);
		System.out.println("End of list sent" + Commands.END_OF_LIST);
		System.out.println("File list sent to client");

	}
	//method to send file to client 
	private void sendFile() throws IOException {
		
		String fileRequested = "";
		int character;
		//read in the file name that is being requested 
		while ((character = inputStream.read()) != END)
			fileRequested += (char) character;
		System.out.println("Sending requested file " + fileRequested);
		File file = new File(shareDirectoryLocation, fileRequested);
		//check if file exits and let client know if file is available
		if (file.exists() && file.isFile()) {
			outputStream.write(Commands.FILE_EXISTS);
			System.out.println(fileRequested + "Does exist");
			//read in the file 			
			InputStream fileInput = new BufferedInputStream(new FileInputStream(file));
			//asssign the file size to the variable fileSize 
			long fileSize = file.length();
			System.out.println("Size of the file being sent is " + fileSize);
			//send information about the file size to connected client
			outputStream.write(new Long(fileSize).toString().getBytes());
			outputStream.write(END);
			// create array of bytes for reading file
			byte[] buffer = new byte[1024 * 8];
			int bytesRead;
			while ((bytesRead = fileInput.read(buffer)) > 0)
				outputStream.write(buffer, 0, bytesRead);
			fileInput.close();
			//outputStream.close();
			System.out.println("Done sending file");

		}else {
			outputStream.write(Commands.FILE_DOES_NOT_EXIST);
			
		}

	}

	public void stop() {
		go = false;
	}
}