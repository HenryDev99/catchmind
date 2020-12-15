package catchmind_c;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class result extends Frame{
	private Label lb = new Label();
	String a;
	public result(String a) {
		setSize(500, 500);
		setVisible(true);
		this.a = a;
		
		lb.setText(a);
		this.add(lb, BorderLayout.CENTER);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
	}
}
