package tankWar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;


public class Missile {
	/*
	 * �ڵ�����֮�ڵ�λ��
	 */
	int x;
	int y;
	Direction dir;
	static  int WITH = 10;
	static  int HEIGHT = 10;
	TankClient tc = null;
	private boolean live = true;
	boolean good = true;
	static int ID = 1;
	int id;
	int tankID;
	public boolean isGood() {
		return good;
	}
	/*
	 * �ڵ�����֮�ٶ�
	 */
	private static int SPEED = 7;
	private static int SYSPEED = (int) (Math.sqrt(SPEED*SPEED*2)/2);
	
	public Missile(int tankId,int x, int y,Direction dir,TankClient tc,boolean good) {
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.tc = tc;
		this.good = good;
		this.id = ID++;
		this.tankID = tankId;
	}
	/*
	 * �ڵ�����֮���ڵ�
	 */
	public void draw(Graphics g){
		Color c = g.getColor();
		if(isGood()){
//			 SPEED = 7;
//			 SYSPEED = (int) (Math.sqrt(SPEED*SPEED*2)/2);
			 g.setColor(Color.RED);
		}
		if(!isGood()){
//			 SPEED = 5;
//			 SYSPEED = (int) (Math.sqrt(SPEED*SPEED*2)/2);
			 g.setColor(Color.black);
		}
		g.fillRect(x, y, WITH, HEIGHT);
		g.setColor(c);
		
		move(dir);
	}

	/*
	 * �ڵ�����֮�ƶ�����
	 */
	public void move(Direction dir){
		switch(dir) {
		case L:
			x -= SPEED;
			break;
		case LU:
			x -= SYSPEED;
			y -= SYSPEED;
			break;
		case LD:
			x -= SYSPEED;
			y += SYSPEED;
			break;
		case R:
			x += SPEED;
			break;
		case RU:
			x += SYSPEED;
			y -= SYSPEED;
			break;
		case RD:
			x += SYSPEED;
			y += SYSPEED;
			break;
		case U:
			y -= SPEED;
			break;
		case D:
			y += SPEED;
			break;
		default:
			break;
		}
		if(this.x < 0 || this.y < 0 || this.x > TankClient.WIDE || this.y > TankClient.HIGH || (!isLive()) )
			tc.missiles.remove(this);
	}
	/*
	 * �ڵ�����֮����λ��
	 */
	public Rectangle getrect(){
		return new Rectangle(x, y, WITH, HEIGHT);
	}
	/*
	 * �ڵ�����֮���е���
	 */
	public boolean hit(Tank t){
		if(this.getrect().intersects(t.getrect()) && t.isLive() && (t.isGood() != this.isGood()) ){
			t.setLive(false);
			this.live = false;
			Explode explode = new Explode(t.x, t.y, tc);
			tc.explodes.add(explode);
			tc.missiles.remove(this);
			tc.tanks.remove(t);
			return true;
		}
		return false;		
	}
	public boolean isLive() {
		return live;
	}
	public void setLive(boolean live) {
		this.live = live;
	}
	/*
	 * �ڵ�����֮���ж������
	 */
	public boolean hits(ArrayList<Tank> tanks) {
		for (int i = 0; i < tanks.size(); i++) {
			if( hit(tanks.get(i)) ){
				return true;
			}
		}
		return false;
	}
}
