package com.cooksys.fileserverfail3.server.userinterfaces;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.cooksys.fileserverfail3.server.Server;
import com.cooksys.fileserverfail3.server.client.Client;
import com.cooksys.fileserverfail3.server.client.ClientConfigReader;

public class FileShareUserInterFace{
	
	private final String ClIENT_CONFIG_FILE_PATH = "/CooksysShare/";
	private final File CLIENT_CONFIG = new File("clientConfig.xml");
	private int PORT; 
	private static Server server;
	private String downloadPath = "downloads";
	private String sharedPath = "share";
	final DefaultListModel<String> remoteFileList = new DefaultListModel<>();
	private int openFile;
	JAXBContext jax;
	String fileSelected;
	private String[] IPAdresses;
	private JFrame frame;
	private JButton btnDisconnect;
	private JButton btnConnect;
	private JList listRemote;
	private JList listDownload;
	private JList listLocal;
	private JScrollPane scrollPaneRemote;
	private JScrollPane scrollPaneLocalFile;
	private JScrollPane scrollPaneDownload;
	private JButton btnDownloadFile;
	private JComboBox comboBoxIP = new JComboBox();
	private JLabel lblStatus;
	

	/**
	 * Launch the application.
	 * @throws IOException 
	 */
	public static void main(String[] args)  {
		
		EventQueue.invokeLater(new Runnable() {
			
			public void run() {
			
				try {
					
					FileShareUserInterFace window = new FileShareUserInterFace();

					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FileShareUserInterFace() {
		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		
		try{
			jax = JAXBContext.newInstance(ClientConfigReader.class);
			Unmarshaller clientUM = jax.createUnmarshaller();
			clientUM.unmarshal(CLIENT_CONFIG);
			ClientConfigReader clientReader = (ClientConfigReader) clientUM.unmarshal(new FileReader(CLIENT_CONFIG));
			
			PORT = clientReader.getPort();
			IPAdresses = clientReader.getIpAddresses();
			//downloadPath = clientReader.getDOWNLOAD_DIRECTORY_LOCATION();
			//sharedPath = clientReader.getSharedPath();
			
		}catch (JAXBException | FileNotFoundException e){
			e.printStackTrace();
		}
		
		final Client client = new Client();
		
		
		
		frame = new JFrame();
		frame.setTitle("Cooksys FileShare");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
				client.disconnect();
				
				
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		JPanel panelMain = new JPanel();
		frame.getContentPane().add(panelMain, BorderLayout.CENTER);
		panelMain.setLayout(new BorderLayout(0, 0));
		
		lblStatus = new JLabel("Status");
		panelMain.add(lblStatus, BorderLayout.SOUTH);		
		
		JPanel panelTop = new JPanel();
		panelMain.add(panelTop, BorderLayout.NORTH);
		panelTop.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		comboBoxIP = new JComboBox(IPAdresses);
		panelTop.add(comboBoxIP);
		
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				ArrayList<String>remoteFiles;
				try{
				if(comboBoxIP.getSelectedIndex() > 0){
					String devName = "";
					String selectedIP = (String) comboBoxIP.getSelectedItem();
					client.connect(selectedIP);
					devName = client.getDeveloperName();
					lblStatus.setText("Conencted to server for: " + devName );
					JOptionPane.showMessageDialog(null, "selectedIP: "+ selectedIP);
					remoteFiles = client.clientGetListOfFiles();
					for (String remoteFile : remoteFiles)
						remoteFileList.addElement(remoteFile.toString());
					btnConnect.setEnabled(false);
					btnDisconnect.setEnabled(true);;
				}else{
					JOptionPane.showMessageDialog(null,  "Select a valid IP ");
				}
				}catch(IOException fsu){
					JOptionPane.showMessageDialog(null, "Unable to connect");;
					fsu.printStackTrace();
				}
				
				

			}
		});
				
			
	
		panelTop.add(btnConnect);
		
		btnDisconnect = new JButton("Disconnect");
		btnDisconnect.setEnabled(false);
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.disconnect();
				lblStatus.setText("Disconnected");
				remoteFileList.clear();
				btnConnect.setEnabled(true);
				btnDisconnect.setEnabled(false);
			}
		});
		panelTop.add(btnDisconnect);
		
		JPanel panelFileListAction = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelFileListAction.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		
		panelTop.add(panelFileListAction);
		
		btnDownloadFile = new JButton("Download");
		btnDownloadFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				client.getFile(fileSelected);
				
			}
		});
		panelFileListAction.add(btnDownloadFile);
		
		
		
		JPanel panelContainer = new JPanel();
		panelMain.add(panelContainer, BorderLayout.CENTER);
		panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));
		
		JPanel panelTopContainer = new JPanel();
		panelContainer.add(panelTopContainer);
		panelTopContainer.setLayout(new BoxLayout(panelTopContainer, BoxLayout.X_AXIS));
		
		JPanel panelRemote = new JPanel();
		panelTopContainer.add(panelRemote);
		panelRemote.setLayout(new BorderLayout(0, 0));
		
		scrollPaneRemote = new JScrollPane();
		panelRemote.add(scrollPaneRemote);
		
		listRemote = new JList(remoteFileList);
		listRemote.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				fileSelected = remoteFileList
						.getElementAt(listRemote
								.getSelectedIndex());
			}
		});
					
		listRemote.addMouseListener(new MouseAdapter() {
		
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && !e.isConsumed()) {
					
						System.out.println(fileSelected);
						client.getFile(fileSelected);
						e.consume();
					
				}
			}
		});
		scrollPaneRemote.setViewportView(listRemote);
		
		JPanel panelDownload = new JPanel();
		panelTopContainer.add(panelDownload);
		panelDownload.setLayout(new BorderLayout(0, 0));
		
		scrollPaneDownload = new JScrollPane();
		panelDownload.add(scrollPaneDownload);
		final DefaultListModel<String>downloadedFilesList = new DefaultListModel<>();
		File downloadedFiles = new File(downloadPath);
		File[] downloadList = downloadedFiles.listFiles();
		for(File files : downloadList)
			downloadedFilesList.addElement(files.getName().toString());
		
		listDownload = new JList<>(downloadedFilesList);
		listDownload.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				fileSelected = downloadedFilesList.getElementAt(listDownload.getSelectedIndex());
				
			}
		});
		listDownload.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount() == 2 && !e.isConsumed()){
					client.viewDownloadedFile(fileSelected);
					e.consume();
				}
			}
			
		});
		
		
		scrollPaneDownload.setViewportView(listDownload);
		
		
		//setting the local files panel
		JPanel panelLocal = new JPanel();
		panelContainer.add(panelLocal);
		panelLocal.setLayout(new BorderLayout(0, 0));
		
		scrollPaneLocalFile = new JScrollPane();
		panelLocal.add(scrollPaneLocalFile);
		
		//add local files to array to be displayed in list
		final DefaultListModel<String>localList= new DefaultListModel<>();
		File localFile = new File(sharedPath);
		File[] localList2 = localFile.listFiles(); 
		for (File files : localList2)
			localList.addElement(files.getName().toString());
		listLocal = new JList(localList);
		listLocal.addListSelectionListener(new ListSelectionListener() {
			
			//select file in the local file list 
			@Override
			public void valueChanged(ListSelectionEvent e) {
			fileSelected = localList.getElementAt(listLocal.getSelectedIndex());
				
			}
		});
		//capture event to open file that is selected
		listLocal.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				if (e.getClickCount() == 2 && !e.isConsumed()){
					client.viewLocalFile(fileSelected);
				}
			}
		});
		
	
		scrollPaneLocalFile.setViewportView(listLocal);
		
		
		
		
		JPanel panel = new JPanel();
		scrollPaneLocalFile.setColumnHeaderView(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JButton btnRefreshSharedList = new JButton("Refresh Shared List");
		panel.add(btnRefreshSharedList);
		btnRefreshSharedList.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final DefaultListModel<String> remoteFileList = new DefaultListModel<>();
				ArrayList<String>remoteFiles;
				try{
				remoteFiles = client.clientGetListOfFiles();
				for (String remoteFile : remoteFiles)
				remoteFileList.addElement(remoteFile.toString());
				listRemote = new JList(remoteFileList);
				listRemote.addListSelectionListener(new ListSelectionListener() {
					
					@Override
					public void valueChanged(ListSelectionEvent e) {
						fileSelected = remoteFileList
								.getElementAt(listRemote
										.getSelectedIndex());
					}
				});
							
				listRemote.addMouseListener(new MouseAdapter() {
				
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() == 2 && !e.isConsumed()) {
							
								System.out.println(fileSelected);
								client.getFile(fileSelected);
								e.consume();
							
						}
					}
				});
				scrollPaneRemote.setViewportView(listRemote);
				}catch(IOException e2){
					e2.printStackTrace();
				}
				
				
			}
		});
		
		JButton btnRefreshLocalList = new JButton("Refresh Local File List");
		panel.add(btnRefreshLocalList, BorderLayout.SOUTH);
		btnRefreshLocalList.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final DefaultListModel<String>localList= new DefaultListModel<>();
				File localFile = new File(sharedPath);
				File[] localList2 = localFile.listFiles(); 
				for (File files : localList2)
					localList.addElement(files.getName().toString());
				listLocal = new JList(localList);
				listLocal.addListSelectionListener(new ListSelectionListener() {
					
					//select file in the local file list 
					@Override
					public void valueChanged(ListSelectionEvent e) {
					fileSelected = localList.getElementAt(listLocal.getSelectedIndex());
						
					}
				});
				//capture event to open file that is selected
				listLocal.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e){
						if (e.getClickCount() == 2 && !e.isConsumed()){
							client.viewLocalFile(fileSelected);
						}
					}
				});
				
			
				scrollPaneLocalFile.setViewportView(listLocal);
				
			}
		});
		
		JButton btnRefreshDownLoad = new JButton("Refresh Download List");
		panel.add(btnRefreshDownLoad, BorderLayout.EAST);
		btnRefreshDownLoad.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final DefaultListModel<String>downloadedFilesList = new DefaultListModel<>();
				File downloadedFiles = new File(downloadPath);
				File[] downloadList = downloadedFiles.listFiles();
				for(File files : downloadList)
					downloadedFilesList.addElement(files.getName().toString());
				
				listDownload = new JList<>(downloadedFilesList);
				listDownload.addListSelectionListener(new ListSelectionListener() {
					
					@Override
					public void valueChanged(ListSelectionEvent e) {
						fileSelected = downloadedFilesList.getElementAt(listDownload.getSelectedIndex());
						
					}
				});
				listDownload.addMouseListener(new MouseAdapter() {
					
					public void mouseClicked(MouseEvent e){
						if(e.getClickCount() == 2 && !e.isConsumed()){
							client.viewDownloadedFile(fileSelected);
							e.consume();
						}
					}
					
				});
				
				
				scrollPaneDownload.setViewportView(listDownload);
				
			}
		});
	}

	public JList getListRemote() {
		return listRemote;
	}
	public JList getListDownload() {
		return listDownload;
	}
	public JList getListLocal() {
		return listLocal;
	}

	

	
}
