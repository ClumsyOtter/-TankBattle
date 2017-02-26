package tankWar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Tank {
	public int ID;
	public int x, y; // ̹������λ��
	static int WITH = 30;
	static int HEIGHT = 30;
	TankClient tc = null;
	private boolean bL = false, bR = false, bU = false, bD = false; // ̹�˷����ж�
	Direction barrel = Direction.R;
	boolean good = true;

	public boolean isGood() {
		return good;
	}

	private boolean live = true;
	Direction dir = null;
	Random r = new Random();
	Direction[] dirs = Direction.values();

	// private int step = r.nextInt(40)+10;
	/*
	 * ��ʼ��̹��
	 */
	public Tank(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/*
	 * ��ȡTankClient����
	 */
	public Tank(int x, int y, TankClient tc, boolean good, Direction dir) {
		this.x = x;
		this.y = y;
		this.tc = tc;
		this.good = good;
		this.dir = dir;
	}

	/*
	 * ̹������̹֮���ٶ�
	 */

	private int SPEED = 3;
	private int XYSPEED = (int) (Math.sqrt(SPEED * SPEED * 2) / 2);

	/*
	 * ̹������֮���̹��
	 */
	public void addtank(int number) {
		int xt, yt;
		int dirss;
		for (int i = 0; i < number; i++) {
			xt = (r.nextInt(770 / 30 - 2) + 2) * 30;
			yt = (r.nextInt(540 / 30 - 2) + 2) * 30 + 30;
			dirss = r.nextInt(dirs.length - 1);
			Tank t = new Tank(xt, yt, tc, false, dirs[dirss]);
			tc.tanks.add(t);
		}
	}

	/*
	 * ̹������֮��̹���Լ�̹����Ͳ
	 */
	public void draw(Graphics g) {
		if (!live)
			return;
		// if(!good){
		// SPEED = 1;
		// XYSPEED = (int) (Math.sqrt(SPEED * SPEED * 2) / 2 +1);
		// }
		Color c = g.getColor();
		if (this.good == true)
			g.setColor(Color.BLUE);
		else
			g.setColor(Color.yellow);
		g.fillOval(x, y, WITH, HEIGHT);
		g.drawString(String.valueOf(ID), x, y);
		g.setColor(Color.MAGENTA);
		move();
		switch (barrel) {
		case L:
			g.drawLine(x + Tank.WITH / 2, y + Tank.HEIGHT / 2, x, y + Tank.HEIGHT / 2);
			break;
		case LU:
			g.drawLine(x + Tank.WITH / 2, y + Tank.HEIGHT / 2, x, y);
			break;
		case LD:
			g.drawLine(x + Tank.WITH / 2, y + Tank.HEIGHT / 2, x, y + Tank.HEIGHT);
			break;
		case R:
			g.drawLine(x + Tank.WITH / 2, y + Tank.HEIGHT / 2, x + Tank.WITH, y + Tank.HEIGHT / 2);
			break;
		case RU:
			g.drawLine(x + Tank.WITH / 2, y + Tank.HEIGHT / 2, x + Tank.WITH, y);
			break;
		case RD:
			g.drawLine(x + Tank.WITH / 2, y + Tank.HEIGHT / 2, x + Tank.WITH, y + Tank.HEIGHT);
			break;
		case U:
			g.drawLine(x + Tank.WITH / 2, y + Tank.HEIGHT / 2, x + Tank.WITH / 2, y);
			break;
		case D:
			g.drawLine(x + Tank.WITH / 2, y + Tank.HEIGHT / 2, x + Tank.WITH / 2, y + Tank.HEIGHT);
			break;
		default:
			break;
		}
		g.setColor(c);
	}

	/*
	 * ̹������֮��ȡ�����¼���������Ӧ
	 */
	public void keyevent(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_LEFT:
			bL = true;
			break;
		case KeyEvent.VK_RIGHT:
			bR = true;
			break;
		case KeyEvent.VK_UP:
			bU = true;
			break;
		case KeyEvent.VK_DOWN:
			bD = true;
			break;
		}
		direction();
	}

	/*
	 * ̹������֮������ж�
	 */
	void direction() {
		Direction oldDir = dir;
		if (bL && !bU && !bR && !bD)
			dir = Direction.L;
		else if (bL && bU && !bR && !bD)
			dir = Direction.LU;
		else if (!bL && bU && !bR && !bD)
			dir = Direction.U;
		else if (!bL && bU && bR && !bD)
			dir = Direction.RU;
		else if (!bL && !bU && bR && !bD)
			dir = Direction.R;
		else if (!bL && !bU && bR && bD)
			dir = Direction.RD;
		else if (!bL && !bU && !bR && bD)
			dir = Direction.D;
		else if (bL && !bU && !bR && bD)
			dir = Direction.LD;
		else if (!bL && !bU && !bR && !bD)
			dir = Direction.STOP;
		if (oldDir != dir) {
			TankMoveMessage tmm = new TankMoveMessage(this.barrel,this.dir, this.ID, this.x, this.y);
			tc.nc.send(tmm);
		}
	}

	/*
	 * ̹������֮�ƶ�����
	 */
	public void move() {
		switch (dir) {
		case L:
			x -= SPEED;
			break;
		case LU:
			x -= XYSPEED;
			y -= XYSPEED;
			break;
		case LD:
			x -= XYSPEED;
			y += XYSPEED;
			break;
		case R:
			x += SPEED;
			break;
		case RU:
			x += XYSPEED;
			y -= XYSPEED;
			break;
		case RD:
			x += XYSPEED;
			y += XYSPEED;
			break;
		case U:
			y -= SPEED;
			break;
		case D:
			y += SPEED;
			break;
		case STOP:
			break;
		}
		if (this.dir != Direction.STOP)
			this.barrel = this.dir;
		// if(!good){
		// if(step == 0){
		// if(r.nextInt(90) > 60){
		// fire();
		// }
		// int n = r.nextInt(dirs.length - 1);
		// dir = dirs[n];
		// step = r.nextInt(40)+30;
		// }
		// if(outOfGrangel()){
		// int n = r.nextInt(dirs.length - 1);
		// dir = dirs[n];
		// step = r.nextInt(40)+30;
		// }
		// if(r.nextInt(120) > 118)
		// fire();
		// step--;
		// }
		// �жϳ���
		outOfGrangel();
	}

	/*
	 * ̹������֮�жϳ���
	 */
	public boolean outOfGrangel() {
		if (this.x < 0) {
			this.x = 0;
			return true;
		}
		if (this.y < 30) {
			this.y = 30;
			return true;
		}
		if (this.x > TankClient.WIDE - Tank.WITH) {
			this.x = TankClient.WIDE - Tank.WITH;
			return true;
		}
		if (this.y > TankClient.HIGH - Tank.HEIGHT) {
			this.y = TankClient.HIGH - Tank.HEIGHT;
			return true;
		}
		return false;
	}

	/*
	 * ���̵���ʱ���¼�
	 */
	public void keyRelease(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (isLive())
			switch (keyCode) {
			case KeyEvent.VK_A:
				fire();
				break;
			case KeyEvent.VK_LEFT:
				bL = false;
				break;
			case KeyEvent.VK_RIGHT:
				bR = false;
				break;
			case KeyEvent.VK_UP:
				bU = false;
				break;
			case KeyEvent.VK_DOWN:
				bD = false;
				break;
			// case KeyEvent.VK_S:
			// if(dir == Direction.STOP)
			// for(int i =0; i<dirs.length-1;i++){
			// Missile m = new Missile(x, y, dirs[i],this.tc,this.good);
			// tc.missiles.add(m);
			// }
			// break;
			}
		direction();
	}

	/*
	 * ̹������֮�����ڵ�
	 */
	public Missile fire() {
		if (!isLive()) {
			return null;
		}
		int x = this.x + Tank.WITH / 2 - Missile.WITH / 2;
		int y = this.y + Tank.HEIGHT / 2 - Missile.HEIGHT / 2;
		Missile m = new Missile(this.ID, x, y, barrel, this.tc, this.good);
		MissileNewMsg mnm = new MissileNewMsg(this.ID, m, tc);
		tc.nc.send(mnm);
		tc.missiles.add(m);
		return m;

	}

	/*
	 * ̹��������̹��λ��
	 */
	public Rectangle getrect() {
		return new Rectangle(x, y, WITH, HEIGHT);
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}
}
