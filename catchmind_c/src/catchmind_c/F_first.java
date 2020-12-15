package catchmind_c;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class F_first extends JFrame{
	
	JButton host, client;
	JLabel title;
	
	public F_first() {
		setSize(500, 300);
		setLayout(null);
		setResizable(false);
		host = new JButton("host");
		client = new JButton("client");
		title = new JLabel("Catch Mind");
		
		host.setSize(100, 100);
		host.setLocation(10, 100);
		
		client.setSize(100, 100);
		client.setLocation(150, 100);
		
		host.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new f_catchmind();
				setVisible(false);
			}
		});
		
		client.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setVisible(false);
				new insertIpPort();
			}
		});
		title.setSize(100, 50);
		title.setLocation(10, 20);
		
		add(host);
		add(client);
		add(title);
		setVisible(true);
	}
	public static void main(String[] args) {		
		new F_first();		
    }
}
