import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;

public class ChatFrame extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextArea textArea;
	/**
	 * Create the frame.
	 */
	private String ipAddress;
	private int portNum = 0;
	private MainFrame mf;
	private String personName;
	
	public ChatFrame(String ip, int port, MainFrame mf, String personName) {
		super();
		this.mf = mf;
		ipAddress = ip;
		portNum = port;
		this.personName = personName;
		setTitle("communicating with IP= " + ipAddress + ", name "+  personName);
		setResizable(false);
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 444, 265);
		contentPane.add(panel);
		panel.setLayout(null);
		
		textArea = new JTextArea();
		textArea.setFont(new Font("MS Reference Sans Serif", Font.PLAIN, 13));
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setBounds(12, 13, 420, 176);
		panel.add(textArea);
		
		textField = new JTextField();
		textField.setBounds(12, 202, 294, 39);
		panel.add(textField);
		textField.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.setBounds(306, 209, 61, 25);
		panel.add(btnSend);
		
		JButton btnExit = new JButton("Exit");
		btnExit.setBounds(371, 209, 61, 25);
		panel.add(btnExit);
		
		exitFrame(btnExit);
		sendMessage(btnSend);
	}
	
	private String grabMessage() {
		return textField.getText();
	}
	public void appendMessage(String message) {
		textArea.append(message + "\n");
	}
	
	private void sendMessage(JButton btnSend) {
		btnSend.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				appendMessage(grabMessage());
				mf.sendMessage(ipAddress, portNum, grabMessage());
				textField.setText("");
				
			}
		});
		
	}

	public void exitFrame(JButton button) {
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();			
			}
		});
	}
}
