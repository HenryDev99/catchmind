package catchmind_c;

import java.awt.Button;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class f_catchmind extends Frame implements MouseListener,MouseMotionListener{
	
	private MenuBar mb = new MenuBar();
	
	private Menu ocolor = new Menu("COLOR");
	private CheckboxMenuItem ocred = new CheckboxMenuItem("RED", true);
	private CheckboxMenuItem ocblue = new CheckboxMenuItem("BLUE");
	private CheckboxMenuItem ocgreen = new CheckboxMenuItem("GREEN");
	
	private Button jb, c_jb;
	private Label lb;
	private Panel pl;
	// x,y : 그래픽 객체의 시작 좌표값 
	// x1,y1 : 그래픽 객체의 종료 좌표값
	private int x,y,x1,y1;
	
	private Color color;
	
	private ArrayList<DrawInfo> vc = new ArrayList<>();
	
	private Socket socket;
	private OutputStream output;
	
	private boolean f_start = false;
	private boolean f_first = true;
	private boolean f_clear = false;
	
	Thread th_title;
	
	public f_catchmind() {
		super("catch mind");
		init();
		start();
		setLayout(null);
		setSize(500, 500);
		this.add(jb);
		this.add(lb);
		this.add(c_jb);
		this.add(pl);
		setVisible(true);
		setResizable(false);
	}
	
	void clear() 
	{
		f_clear = true;
		
		String total = "clear" + "/" + x + "/" + y;
		
		byte[] data = total.getBytes();
		
		try {
			output.write(data);
			output.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		vc.clear();
		this.repaint();
	}
	
	void init() {
		jb = new Button("start");
		jb.setSize(50, 50);
		jb.setLocation(430,75);
		jb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				f_start = true;
				try {
					socket = new Socket("127.0.0.1", 9000);
					output = socket.getOutputStream();
					String a = "host";
					byte[] data = a.getBytes("UTF-8");
					output.write(data);
					output.flush();
					
					th_title = new Thread( new Runnable(){
						public void run(){
							int maxBufferSize = 1024;
					        byte[] recvBuffer = new byte[maxBufferSize];
					        InputStream is = null;
					        int nReadSize = 0;
					        
					        while(true)
					        {
					        	try {
									is = socket.getInputStream();
									nReadSize = is.read(recvBuffer);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

						        //받아온 값이 0보다 클때
						        if (nReadSize > 0) {
						            //받아온 byte를 Object로 변환 
						            //확인을 위해 출력
					        		String str = new String(recvBuffer);
					        		String[] strary = str.split("/");
						        	if(f_first)
						        	{
						        		if(strary[0].equals("no"))
						        		{
						        			new result("Already a host");
							        		setVisible(false);
						        		}
						        		else
						        		{
						        			lb.setText(str);
								        	f_first = false;
						        		}
						        	}
						        	else
						        	{
						        		if(strary[0].equals("end"))
						        		{
						        			String total = "end";
						        			
						        			byte[] data = total.getBytes();
						        			
						        			try {
						        				output.write(data);
						        				output.flush();
						        			} catch (IOException e1) {
						        				// TODO Auto-generated catch block
						        				e1.printStackTrace();
						        			}
						        			
						        			System.out.println("aaa");
							        		new result("Game End");
							        		setVisible(false);
						        		}
						        		else
						        		{
						        			System.out.println(str);
							        		lb.setText(str);
						        		}
						        	}

						        }
									        	
					        }
					    }
					});
					
					th_title.start();
				} catch (IOException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		});
		
		c_jb = new Button("clear");
		c_jb.setSize(50, 50);
		c_jb.setLocation(430, 440);
		c_jb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				clear();
			}
		});
		
		lb = new Label();
		lb.setSize(500, 50);
		lb.setLocation(0, 20);
		lb.setAlignment(Label.CENTER);
		lb.setText("시작해주세요!");
		
		pl = new Panel();
		pl.setBackground(Color.black);
		pl.setSize(500, 3);
		pl.setLocation(0, lb.getY() + lb.getHeight());
		
		this.setMenuBar(mb);
		mb.add(ocolor);
		ocolor.add(ocred);
		ocolor.add(ocblue);
		ocolor.add(ocgreen);
	}
	
	void start() {
		this.addWindowListener(new WindowAdapter() {
			@SuppressWarnings("deprecation")
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
	}
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		if(f_clear)
		{
			f_clear = false;
		}
		else
		{
			Color c = new Color(ocred.getState()?255:0, ocgreen.getState()?255:0, ocblue.getState()?255:0);
			g.setColor(c);
			
			g.drawLine(x, y, x1, y1);
			
			for(int i = 0; i<vc.size(); i++){
				
				// 배열에서 1개의 그림정보를 imsi에 담는다.
				DrawInfo imsi = vc.get(i);
				
				// imsi의 색상정보를 가져와서 실제 그림을 그릴 그래픽 객체 g에 담는다.
				g.setColor(imsi.getColor());
				
				g.drawLine(imsi.getX(), imsi.getY(), imsi.getX1(), imsi.getY1());
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		x1 = e.getX();
		y1 = e.getY();
		
		Color c = new Color(ocred.getState()?255:0, ocgreen.getState()?255:0 , ocblue.getState()?255:0);
		
		DrawInfo di = new DrawInfo(x, y, x1, y1, c);
		
		vc.add(di);
		// 현재의 마우스 위치를 시작 위치로 재설정한다.
		x = x1;
		y = y1;
		
		if(f_start)
		{
			String s_x = Integer.toString(e.getX());
			String s_y = Integer.toString(e.getY());
			String s_c = di.getColor().toString();
			String total = s_x + "/" + s_y + "/" + s_c;
			
			byte[] data = total.getBytes();
			
			try {
				output.write(data);
				output.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		x = e.getX();
		y = e.getY();
		
		String s_x = Integer.toString(e.getX());
		String s_y = Integer.toString(e.getY());
		String total = "ack" + "/" + s_x + "/" + s_y;
		
		byte[] data = total.getBytes();
		
		if(f_start)
		{
			try {
				output.write(data);
				output.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		x1 = e.getX();
		y1 = e.getY();
		
		Color c = new Color(ocred.getState()?255:0, ocgreen.getState()?255:0, ocblue.getState()?255:0);
		
		DrawInfo di = new DrawInfo(x, y, x1, y1, c);
		
		// 도형의 시작, 종료위치와 색상, 채움여부를 그래픽배열에 저장한다.  
		vc.add(di);
		
		this.repaint();		
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}

