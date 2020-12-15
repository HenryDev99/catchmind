package catchmind_c;

import java.awt.Button;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class insertIpPort extends JFrame{
	private JTextField ip = new JTextField();
	private JTextField port = new JTextField();
	private JButton send = new JButton("참가하기");
	private JLabel l_ip = new JLabel("IP: ");
	private JLabel l_port = new JLabel("PORT: ");
	
	
	public insertIpPort() {
		setLayout(null);
		setSize(500, 500);
		setVisible(true);
		setResizable(false);
		
		
		l_ip.setSize(100, 30);
		l_ip.setLocation(0,100);
		
		ip.setSize(200, 30);
		ip.setLocation(150,100);
		ip.setBorder(new LineBorder(Color.black));
		
		l_port.setSize(100, 30);
		l_port.setLocation(0,200);
		
		port.setSize(200, 30);
		port.setLocation(150,200);
		port.setBorder(new LineBorder(Color.black));
		
		send.setSize(100, 50);
		send.setLocation(300, 300);
		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				new c_catchmind(ip.getText(), Integer.parseInt(port.getText()));
			}
		});
		
		this.add(ip);
		this.add(port);
		this.add(send);
		this.add(l_ip);
		this.add(l_port);
	}
}
