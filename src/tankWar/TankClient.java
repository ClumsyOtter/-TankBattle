package tankWar;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


public class TankClient extends Frame {

	/**
	 * @author ����
	 * @version
	 */
	private static final long serialVersionUID = -6758343604444170663L;
	static int WIDE = 800; // ��Ϸ��Ļ��
	static int HIGH = 600; // ��Ϸ��Ļ��
	ArrayList<Missile> missiles = new ArrayList<Missile>(); // �ڵ�����
	ArrayList<Explode> explodes = new ArrayList<Explode>(); // ��ը����
	ArrayList<Tank> tanks = new ArrayList<Tank>(); // �з�̹������
	Tank mytank = new Tank(30, 60, this,true,Direction.STOP); // ����̹��
	Random r = new Random(new Date().getTime());
	Image offscream = null;

	NetClient nc = new NetClient(this); //����ͻ���
	connectSet cs = new connectSet();
	/*
	 * �����߳̽��ж�ʱ
	 */
	private class threadPaint implements Runnable {

		@Override
		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * ����paint���� ÿ���ػ��ǻ��Զ����� 
	 * Color c = g.getColor();
	 * g.setColor(Color.BLUE);
	 * g.drawString("missiles"+missiles.size(), 100, 100);
	 */
	@Override
	public void paint(Graphics g) {
		 Color c = g.getColor();
		 g.setColor(Color.MAGENTA);
		 g.setColor(c);
		mytank.draw(g);
		for (int i = 0; i < tanks.size(); i++) {
			Tank m = tanks.get(i);
			m.draw(g);
		}
		for (int i = 0; i < missiles.size(); i++) {
			Missile m = missiles.get(i);
			if(m.hit(mytank)){
				TankDeathMsg tdm = new TankDeathMsg(mytank.ID,this);
				this.nc.send(tdm);
				MissileDeathMsg mdm = new MissileDeathMsg(m.id, m.tankID);
				this.nc.send(mdm);
			}
			m.draw(g);
		}
		for (int i = 0; i < explodes.size(); i++) {
			Explode m = explodes.get(i);
			m.draw(g);
		}
		//crashExolode();
	}

	/*
	 * ˫�ػ��壬������˸
	 */
	public void update(Graphics g) {
		if (offscream == null) {
			offscream = this.createImage(WIDE, HIGH);
		}
		Graphics Offimg = offscream.getGraphics();
		Color c = Offimg.getColor();
		Offimg.setColor(Color.gray);
		Offimg.fillRect(0, 0, WIDE, HIGH);
		paint(Offimg);
		g.drawImage(offscream, 0, 0, null);
		g.setColor(c);
	}

	/*
	 * ��������
	 */
	public void launchFream() {
		this.setLocation(500, 200);
		this.setSize(WIDE, HIGH);

		// �رհ�ť�¼��趨
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		// �ı������������
		this.setTitle("̹�˴�ս");
		this.setResizable(false);
		this.setVisible(true);
		this.setBackground(Color.GRAY);
		this.addKeyListener(new keylisten());
		//mytank.addtank(5);
		new Thread(new threadPaint()).start();
	}

	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.launchFream();
	}

	/*
	 * ������̼���
	 */
	private class keylisten extends KeyAdapter {
		/*
		 * ����ѹ�µ��¼�
		 */
		public void keyPressed(KeyEvent e) {
			mytank.keyevent(e);
			
		}
		/*
		 * ���̵�����¼�
		 */
		public void keyReleased(KeyEvent e) {
			int keycount = e.getKeyCode();
			if( keycount == KeyEvent.VK_Q){
				cs.setVisible(true);
			}
			mytank.keyRelease(e);
		}
	}
	/*
	 * �Ի���
	 */
	@SuppressWarnings("serial")
	private class connectSet extends Dialog {
		TextField ipNumber = new TextField("127.0.0.1",12);
		TextField udpNumber = new TextField("2222",5);
		TextField serverNumber = new TextField(""+TankWarServer.TANK_PORT,5);
		public connectSet() {
			super(TankClient.this,true);
			Button b = new Button("ȷ��");
			this.setLayout(new FlowLayout());
			this.add(new Label("IP"));
			this.add(ipNumber);
			this.add(new Label("Port"));
			this.add(serverNumber);
			this.add(new Label("My Port"));
			this.add(udpNumber);
			this.setLocation(600, 400);
			this.add(b);
			this.pack();
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String ip = ipNumber.getText().trim();
					int servernumber = Integer.parseInt(serverNumber.getText().trim());
					int udpNumbers = Integer.parseInt(udpNumber.getText().trim());
					nc.setUDP_NUMBER(udpNumbers);	
					nc.connect(ip, servernumber);
					setVisible(false);
				}
			});
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}	
			});
		}	
	}
	/*
	 * ̹����ײ��ᱬը
	 */
	public void crashExolode(){
		for (int i = 0; i < tanks.size(); i++) {
			Tank m = tanks.get(i);
			if( mytank.getrect().intersects(m.getrect()) && m.isLive() && mytank.isLive() ){
				Explode explode = new Explode(m.x, m.y, this);
				Explode explode1 = new Explode(mytank.x, mytank.y, this);
				this.explodes.add(explode1);
				this.explodes.add(explode);
				mytank.setLive(false);
				m.setLive(false);
			}
		}	
	}
}
