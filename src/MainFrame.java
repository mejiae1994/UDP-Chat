import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTextField ipField;
	private JTextField portField;
	private String specifiedIp;
	private int portNumber;
	private int myPortNum = 64000;
	private InetAddress myAddress;
	private DatagramSocket socket = null;
	private String mainMessage;
	private HashMap<String, ChatFrame> addresses = new HashMap<String, ChatFrame>();
	private String myName = "Edison";
	private String sendersName;
	
	public MainFrame() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		try {
			setTitle(InetAddress.getLocalHost().toString());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		createPanels();
		establishConnection();
		
	}
	
	public void getInputs() {
		specifiedIp = ipField.getText();
	}
		
	public void createPanels() {
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 444, 265);
		contentPane.add(panel);
		panel.setLayout(null);
		
		ipField = new JTextField(); 
		ipField.setBounds(156, 54, 116, 22);
		panel.add(ipField);
		ipField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("name");
		lblNewLabel.setBounds(188, 34, 84, 16);
		panel.add(lblNewLabel);
		
		JButton broadCastButton = new JButton("broadcast");
		broadCastButton.setBounds(175, 160, 97, 25);
		panel.add(broadCastButton);
		broadCastButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getInputs();
				sendBCM();
				
			}
		});
		
	}
	
	public void connectTo(){
		if(!addresses.containsKey(specifiedIp+"&"+portNumber)) {
			addresses.put(specifiedIp+"&"+portNumber, new ChatFrame(specifiedIp, portNumber, this, sendersName ));
		}
	}
	
	public byte[] byteArray(String ip) {
		String[] ips = ip.split("\\.");
		byte[] ipNums = new byte[4];
		for(int i = 0; i < ips.length; i++) {
			ipNums[i] = (byte) Integer.parseInt(ips[i]);
		}
		return ipNums;
	}
	
	public void receiveMethod() {
		byte[] inBuffer = new byte[100];
		DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
		
		do {
			for(int i = 0; i < inBuffer.length; i++) {
				inBuffer[i] = ' ';
			}
			
			try{
				//this thread will block in the receive call until a message is received.
				socket.receive(inPacket);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
			if(inPacket.getLength() != 0) {
				String message = new String(inPacket.getData());
				if(message.contains("#####")) {
					int port = inPacket.getPort();
					String ip = inPacket.getAddress().getHostAddress();
					String senderName = getSenderName(message);
					sendersName = senderName;
					searchMap(ip+"&"+port, port, ip, message);
					System.out.println("Receive message = " + message);
				} else {
					int port = inPacket.getPort();
					String ip = inPacket.getAddress().getHostAddress();
					searchMap(ip+"&"+port, port, ip, message);
					System.out.println("Receive message = " + message);
				}
			}
		} while(true);
	}
	
	private String getSenderName(String message) {
		String[] ips = message.split("\\s+");
		System.out.println(ips[1] + " is the name");
		return ips[1];
	}

	public void searchMap(String key, int port, String ip, String message) {
		if(!addresses.containsKey(key)){
			addresses.put(key, new ChatFrame(ip, port, this, sendersName));
		}
			addresses.get(key).appendMessage(message);
	}
	
	public void establishConnection() {
		byte[] otherHost = new byte[4];
		otherHost[0] = (byte) 148;
		otherHost[1] = (byte) 84;
		otherHost[2] = (byte) 129;
		otherHost[3] = (byte) 59;
		try {
			myAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
			System.out.println("address is: " + myAddress.getHostAddress());
		
		try {
			socket = new DatagramSocket(myPortNum);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Thread receiveThread = new Thread(new Runnable () {
			public void run() {
				receiveMethod();
			}
		});
		
		receiveThread.setName("My Datagram Receive Thread");
		receiveThread.start();
		
	}
	
	public void sendMessage(String ip, int port, String message) {
		byte[] buffer = new byte[100];
		buffer = message.getBytes();
		InetAddress otherAddress;
		try {	
			otherAddress = InetAddress.getByAddress(byteArray(ip));
			
			System.out.println("otheraddress: " + otherAddress);
			System.out.println("portNumber: " + portNumber);
			
			DatagramPacket myPacket = new DatagramPacket(buffer, buffer.length, otherAddress, port);
			
			System.out.println("Sending message = " + message);
			socket.send(myPacket);
			
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
	}
	
	public String formatedMessage() {
		String formatedString = "";
		formatedString = "????? " + specifiedIp + " ##### " + myName;
		return formatedString;
		
	}
	
	public void sendBCM() {
		byte[] bufferBCM = new byte[100];
		bufferBCM = formatedMessage().getBytes();
		InetAddress BCMAddress;
		String broadCastIp = "255.255.255.255";
		
		try {
			BCMAddress = InetAddress.getByAddress(byteArray(broadCastIp));
			System.out.println("broadcast message being sent");
			System.out.println(formatedMessage() + " this is the message");
			
			DatagramPacket BCM = new DatagramPacket(bufferBCM, bufferBCM.length, BCMAddress, myPortNum);
			
			socket.send(BCM);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setSize(460,300);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void closeConnection() {
		socket.disconnect();
		
	}
}
