package com.cooksys.fileserverfail3.server.client;

import java.awt.Desktop;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.cooksys.fileserverfail3.server.interfaces.Commands;

public class Client implements Commands {

	private int PORT;
	Socket socket;
	InputStream inputStream = null;
	OutputStream outputStream = null;
	String downloadDirectoryLocation;
	private String fileSelected;
	private String downloadPath = "downloads";
	private String localPath = "share";
	//private final String Client_CONFIG_LOCATION = "//CooksysShare/src/com/cooksys/fileserverfail3/server/client/";
	private final File CLIENT_CONFIG_FILE = new File("clientConfig.xml");
	private String developerName;
	public String getDeveloperName() {
		return developerName;
	}

	public Client() {
		readClientXML();

	}

	public String connect(String ipAddress) throws UnknownHostException,
			IOException {
		socket = new Socket(ipAddress, PORT);
		System.out.println(ipAddress + " " + PORT);

		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();

		System.out.println("Connected, waiting for name...");

		int character;

		developerName = "";

		while ((character = inputStream.read()) != Commands.END) {
			developerName += (char) character;

			System.out.println("Recieved: " + character);
		}

		System.out.println("Name = " + developerName);

		return developerName;
	}

	public ArrayList<String> clientGetListOfFiles() throws IOException {
		// send request for files on server that are available for download.
		ArrayList<String> listOfFiles = new ArrayList<>();

		int character;

		outputStream.write(Commands.LIST_FILES);
		System.out.println("sendiong command for list of files. ");

		while ((character = inputStream.read()) != Commands.END_OF_LIST) {
			String fileName = "" + (char) character;

			while ((character = inputStream.read()) != Commands.END)
				fileName += (char) character;
			System.out.println("Got file name: " + fileName);

			listOfFiles.add(fileName);
		}
		return listOfFiles;

	}

	public void getFile(String fileSelected)  {
		try{
			
		
		outputStream.write(Commands.SEND_FILE);
		System.out.println(Commands.SEND_FILE);
		System.out.println("I WANT THIS FILE NOM NOM " + fileSelected);
		outputStream.write(fileSelected.getBytes());
		outputStream.write(END);

		int character;
		character = inputStream.read();
		System.out.println("Server sent back response" + character);
		if (character == FILE_EXISTS) {
			String incomingFileSize = "";
			while ((character = inputStream.read()) != END)
				incomingFileSize += (char) character;

			long lengthOfFile = 0;

			try {
				lengthOfFile = Long.parseLong(incomingFileSize);
			} catch (NumberFormatException clgfe1) {
				clgfe1.printStackTrace();
			}
			File file = new File(downloadPath+ "\\" + fileSelected);
			file.createNewFile();
			OutputStream fileOutputStream = new BufferedOutputStream(
					new FileOutputStream(file));
			byte[] buffer = new byte[1024 * 8];

			int bytesRead;
			long totalBytesRead = 0;
			while (totalBytesRead < lengthOfFile
					&& (bytesRead = inputStream.read(buffer, 0, Math.min(
							(int) (lengthOfFile - totalBytesRead),
							buffer.length))) > 0) {
				fileOutputStream.write(buffer, 0, bytesRead);

				totalBytesRead += bytesRead;
			}
			fileOutputStream.close();
		} else if (character == FILE_DOES_NOT_EXIST)
			throw new FileNotFoundException(fileSelected);
		else
			System.out.println("Unknown Command" + character);
	}
		catch(UnknownHostException e1){
			e1.printStackTrace();
		}catch (IOException e2){
			e2.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("Error disconnecting....");
			e.printStackTrace();
		}
	}

	private void readClientXML() {
		try {
			JAXBContext jaxb = JAXBContext
					.newInstance(ClientConfigReader.class);
			Unmarshaller clientUnMAR = jaxb.createUnmarshaller();
			clientUnMAR.unmarshal(CLIENT_CONFIG_FILE);
			ClientConfigReader clientCReader = (ClientConfigReader) clientUnMAR
					.unmarshal(new FileReader(CLIENT_CONFIG_FILE));

			PORT = clientCReader.getPort();
			downloadDirectoryLocation = clientCReader
					.getDOWNLOAD_DIRECTORY_LOCATION();

		} catch (JAXBException | FileNotFoundException rcx) {
			rcx.printStackTrace();
		}

	}
	
	public void viewLocalFile(String fileSelected) {
		File fileToOpen = new File(localPath, fileSelected);
		try {
			Desktop.getDesktop().open(fileToOpen);//open file to desktop
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void viewDownloadedFile(String fileSelected) {
		File fileToOpen = new File(downloadPath, fileSelected);
		try {
			Desktop.getDesktop().open(fileToOpen);//open file to desktop
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new Client();
	}

}
