package catchmind_c;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import static javax.swing.JOptionPane.*;

public class c_catchmind extends Frame{
	static DrawInfo receive;
	private Socket socket;

	private static int x = 0;
	private static int y = 0;
	private static int x1 = 0;
	private static int y1 = 0;
	private ArrayList<DrawInfo> vc = new ArrayList<>();
	
	private Panel pn = new Panel();
	private TextField field = new TextField();
	private Button jb = new Button("send");
	private Label wintxt = new Label("승리: 0");
	private OutputStream output;
	Thread th_recv, th_send,th_draw;
	boolean f_clear = false;
	private final AtomicBoolean running = new AtomicBoolean(false);
	int wincount = 0;
	Color c;
	
	public c_catchmind(String IP, int port) {
		try {
			socket = new Socket(IP, port);
			output = socket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			showMessageDialog(null, "연결하지 못했습니다.");
			setVisible(false);
		}
		this.setLocationRelativeTo(null);
		
		pn.setBackground(Color.white);
		pn.setLayout(new BorderLayout());
		
		field.setBackground(Color.WHITE);
		
		jb.setBackground(Color.LIGHT_GRAY);
		jb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tosend();
			}
		});
		wintxt.setSize(100, 30);
		wintxt.setLocation(0, 100);
		
		pn.add(field, BorderLayout.NORTH);
		pn.add(wintxt, BorderLayout.SOUTH);
		
		this.add(pn, BorderLayout.NORTH);
		this.add(jb, BorderLayout.SOUTH);
		
		setSize(500, 500);
		setVisible(true);
		setResizable(false);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		
		th_draw = new Thread( new Runnable(){
			public void run(){
		    	//스레드가 실행할 코드흐름	
				while(true)
				{
					redraw();
				}
		    }
		});
		
		recvon();
		th_draw.start();
		
	}
	
	void writeWintxt() 
	{
		String txt = "승리: " + Integer.toString(wincount);
		wintxt.setText(txt);
	}
	
	void tosend()
	{
		th_send = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte[] data = field.getText().getBytes();
				
				try {
					output.write(data);
					output.flush();
					field.setText(null);
					field.requestFocus();
				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		th_send.start();
	}
	
	void redraw()
	{
		this.repaint();
	}
	
	void recvon()
	{
		th_recv = new Thread( new Runnable(){
			public void run(){
		    	running.set(true);
				try {
					while(running.get())
					{
						receive(socket);
						
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		});
		
		th_recv.start();
	}
	
	void recvoff()
	{
		running.set(false);
		//while(th_recv.isAlive());
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
			//Color c = new Color(ocred.getState()?255:0, ocgreen.getState()?255:0, ocblue.getState()?255:0);
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

	public void receive(Socket socket) throws IOException {
        //수신 버퍼의 최대 사이즈 지정
        int maxBufferSize = 1024;
        //버퍼 생성
        byte[] recvBuffer = new byte[maxBufferSize];

        //서버로부터 받기 위한 입력 스트림 뚫음
        InputStream is = socket.getInputStream();
        //버퍼(recvBuffer) 인자로 넣어서 받음. 반환 값은 받아온 size
        int nReadSize = is.read(recvBuffer);

        //받아온 값이 0보다 클때
        if (nReadSize > 0) {
            //받아온 byte를 Object로 변환 
            //확인을 위해 출력
        	NumberFormat nf = NumberFormat.getInstance();
        	String str = new String(recvBuffer, "UTF-8");
        	String[] strary = str.split("/");
        	
        	System.out.println(str);
        	
        	if(strary[0].equals("ack"))
        	{
        		try {
        			x = nf.parse(strary[1]).intValue();
					y = nf.parse(strary[2]).intValue();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	else if(strary[0].equals("clear"))
        	{
        		f_clear = true;
        		vc.clear();
        		this.repaint();
        	}
        	else if(strary[0].equals("good"))
        	{
        		++wincount;
        		writeWintxt();
        		
        	}
        	else if(strary[0].equals("end"))
        	{
        		showMessageDialog(null, strary[0]);
        		ByteBuffer buf = java.nio.ByteBuffer.allocate(Integer.SIZE/8);
        		buf.putInt(wincount);
        		buf.order(ByteOrder.BIG_ENDIAN);
     
        		byte[] data = buf.array();
				
				try {
					output.write(data);
					output.flush();
					field.setText(null);
					field.requestFocus();
				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
        	}
        	else if(strary[2].equals("0end"))
        	{
        		showMessageDialog(null, strary[2]);
        		ByteBuffer buf = java.nio.ByteBuffer.allocate(Integer.SIZE/8);
        		buf.putInt(wincount);
        		buf.order(ByteOrder.BIG_ENDIAN);
     
        		byte[] data = buf.array();
				
				try {
					output.write(data);
					output.flush();
					field.setText(null);
					field.requestFocus();
				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
        	}
        	else
        	{
        		try {
    				x1 = nf.parse(strary[0]).intValue();
    				y1 = nf.parse(strary[1]).intValue();
    				
    				Scanner sc = new Scanner(strary[2]);
    			    sc.useDelimiter("\\D+");
    			    c = new Color(sc.nextInt(), sc.nextInt(), sc.nextInt());
   				} catch (ParseException e) {
   					// TODO Auto-generated catch block
   					e.printStackTrace();
   				}            		
            	DrawInfo di = new DrawInfo(x, y, x1, y1, c);
           		
           		vc.add(di);
            		
           		x = x1;
           		y = y1;
        	}
    		
        }
    }
}
