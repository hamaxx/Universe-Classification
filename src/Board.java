import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;


public class Board extends JPanel {
	private static final long serialVersionUID = 1L;
	
	Data data;
	Menu menu;
	
	double border;

	long startTime;
	long fpsTime;
	
	public Board(Menu m) {
		this.setBackground(Color.white);
		this.setPreferredSize(new Dimension(800, 800));
		menu = m;
		init();
	}
	
	public void init() {
        AttrMeta.reset();
        Clas.num = 0;
        
		data = new Data("datasets/" + Main.filename);
		
		border = Math.sqrt(data.entity.length) * 60;
		data.randomPosition(border);
		menu.mass = startSpeed();
		menu.setMenu(data);
						
		startTime = System.nanoTime() / (int)1E9;		
		fpsTime = System.nanoTime();
	}
	
	private double startSpeed() {
		double str = 0;
		for (Conn con : data.conn) {
			str += Math.abs(Math.pow(con.strength - data.avgConn, 3));
		}
		return str;
	}
	
	private void step() {
		for (Conn con : data.conn) {
			double d = con.dist();
			if (d < 8 && d > 0) {
				crash(con);
			}
			force(con);
		}
		
		move();
		shrinkUniverse();
	}
	
	private void move() {
		for (Entity ent : data.entity) {
			double x = ent.x, y = ent.y;
			double d1 = Math.sqrt(Math.pow(x - border, 2) + Math.pow(y - border, 2));
			if (d1 > border) {
				x += ent.speedX;
				y += ent.speedY;
				double d2 = Math.sqrt(Math.pow(x - border, 2) + Math.pow(y - border, 2));
				if (d2 > d1) {
					double nx = border - x;
					double ny = border - y;
					double n = Math.sqrt(Math.pow(nx, 2) + Math.pow(ny, 2));
					nx /= n; ny /= n;
					
					double dot = -ent.speedX * nx - ent.speedY * ny;

					ent.speedX = (2 * dot * nx + ent.speedX) * 0.5;
					ent.speedY = (2 * dot * ny + ent.speedY) * 0.5;
				}
			}
			ent.move();
		}
	}
	
	private void shrinkUniverse() {
		for (Entity en : data.entity) {
			double k = menu.shrinkSpeed;
			en.x = (en.x - border) * k + border;
			en.y = (en.y - border) * k + border;
			
			en.speedX *= 0.99;
			en.speedY *= 0.99;
		}
	}
	
	private void crash(Conn con) {
		double g = 1;

		if (con.strength > menu.avgConn) {
			g *= 1 - ((con.strength - menu.avgConn) / (con.strength + menu.avgConn) + 1) / 2;
		} else {
			g *= 1;//((menu.avgConn - con.strength) / (con.strength + menu.avgConn)) / 4 + 0.8;
		}

		double sx = con.e1.speedX * g;
		double sy = con.e1.speedY * g;
		con.e1.speedX = con.e2.speedX * g;
		con.e1.speedY = con.e2.speedY * g;
		con.e2.speedX = sx;
		con.e2.speedY = sy;
	
		con.moveFor(-(10 - con.dist()));	
	}

	private void force(Conn con) {
		double m = Math.pow(con.strength - menu.avgConn, 3);
		double d = con.dist();
		
		double force = 0;

		if (m < 0) {
			if (d < border / 3) {
				force = m / (d * d) * 1E5;
			}
		} else {
			force = m * d * d / 1E4;
		}

		force /= menu.mass;

		if (force > 20) force = 20;
		if (Double.isNaN(force) || Double.isInfinite(force)) force = 0;
		
		con.changeSpeed(force);
	}

	private void reMove() {
		if (!Main.play) return;
		
		try {
			Thread.sleep(1);
			step();
			repaint();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (System.nanoTime() / (int)1E9 - startTime > 5) {
			menu.newDistPredict();
			startTime = System.nanoTime() / (int)1E9;
		}
	}
	
	public void paint(Graphics g) {
		super.paintComponent(g);
		paintFps(g);
		
		double zoom = ((double)getHeight() / 2) / (border + 50);		
		g.setColor(new Color(100, 100, 100));
		double rc = border * zoom * 2;
		int xc = (int)((double)getWidth() / 2 - rc / 2);
		int yc = (int)((double)getHeight() / 2 - rc / 2);
		g.drawOval(xc, yc, (int)rc, (int)rc);
		
		int moveX = (int)((border - getWidth() / 2) * zoom);
		int moveY = (int)((border - getHeight() / 2) * zoom);
		
		for (Entity ent : data.entity) {
			int x = (int)Math.round(ent.x * zoom + ((1 - zoom) * getWidth() / 2));
			int y = (int)Math.round(ent.y * zoom + ((1 - zoom) * getHeight() / 2));
							
			int r = (int)Math.ceil(5 * zoom);
			g.setColor(ent.color(menu.showPre));
			g.fillOval(x - r - moveX, y - r - moveY, 2 * r, 2 * r);
		}

		//printColors(g);
		reMove();
	}
	
	private void paintFps(Graphics g) {
		long t = (long)((System.nanoTime() - fpsTime) / 1E3);
		long fps = Math.round(1E6 / t);
		
		g.drawString("fps: " + fps, 10, 20);
		
		fpsTime = System.nanoTime();
	}
	
}
