import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Transparency;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel implements KeyListener {
	private static final long serialVersionUID = 1L;
	
	public static ParseData data;
	public static Predict predict;
	public static boolean grad = false;
	public static boolean run = false;
	public static long startTime;
	public static double zoom;
	public static int speed = 10;
	public static double border;
	
	public static double inout;
	public static double mass = 1;
	public static double shrinkSpeed = 0.999;
	
	public static TreeMap<Double, Color> colors;
	
	public static void main(String[] args) {
		String filename = args.length > 0 ? args[0] : "datasets/heart.data";
		data = new ParseData(filename, null, 0.2);
		predict = new Predict(data);
		inout = data.avgCon;
		border = data.border;
		startTime = System.nanoTime() / (int)1E9;
		
		zoom = 500 / data.border;
		
		setColors();
		
		if (args != null)
			new Main();
	}
	
	public void restart() {
		run = false;
		main(null);
		center();
		run = true;
	}
	
	public void predict() {
		predict.predict();
	}
	
	public void move() {
		for (int i = 0; i < data.ent.length; i++) {
			double x1 = data.ent[i][0];
			double y1 = data.ent[i][1];
			
			for (int j = 0; j < i; j++) {
				double x2 = data.ent[j][0];
				double y2 = data.ent[j][1];
				
				double dsq = Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
				double d = Math.sqrt(dsq);
				
				if (d < 8 && d > 0) {
					crash(i, j, x1, y1, x2, y2, d);
				}
				if (data.stAtr != data.conn[i][j] || d > 20) {
					gravity(i, j, x1, y1, x2, y2, d);
				} else {
					glue(i, j);
				}
				 
			}
		}
		for (int i = 0; i < data.ent.length; i++) {
			double x = data.ent[i][0], y = data.ent[i][1];
			double d1 = Math.sqrt(Math.pow(x - getWidth() / 2, 2) + Math.pow(y - getHeight() / 2, 2));
			
			if (d1 > border) {
				x += data.ent[i][2];
				y += data.ent[i][3];
				double d2 = Math.sqrt(Math.pow(x - getWidth() / 2, 2) + Math.pow(y - getHeight() / 2, 2));
				if (d2 > d1) {
					data.ent[i][2] *= 0;
					data.ent[i][3] *= 0;
	
				}
			}
			addSpeed(i);

		}
		shrinkUniverse();
	}
	
	public void shrinkUniverse() {
		for (int i = 0; i < data.ent.length; i++) {
			double k = shrinkSpeed;
			data.ent[i][0] = (data.ent[i][0] - getWidth() / 2) * k + getWidth() / 2;
			data.ent[i][1] = (data.ent[i][1] - getHeight() / 2) * k + getHeight() / 2;
		}
	}
	
	public void crash(int i, int j, double x1, double y1, double x2, double y2, double d) {
		double g = (data.avgCon / data.conn[i][j]);
		if (g > 0.1) {
			//g = Math.max(g, 0.1);
			g = Math.min(g, 0.9);
			double sx = data.ent[i][2];
			double sy = data.ent[i][3];
			data.ent[i][2] = data.ent[j][2] * g;
			data.ent[i][3] = data.ent[j][3] * g;
			data.ent[j][2] = sx * g;
			data.ent[j][3] = sy * g;
			
			double dx = (x2 - x1) / d * (10 - d);
			double dy = (y2 - y1) / d * (10 - d);
			data.ent[i][0] -= dx;
			data.ent[i][1] -= dy;
			data.ent[j][0] += dx;
			data.ent[j][1] += dy;	
		} else {
			glue(i, j);
		}
	}
	
	public void gravity(int i, int j, double x1, double y1, double x2, double y2, double d) {
		double m = Math.pow(data.conn[i][j] - inout, 5);
		d = Math.max(d, 30);
		if (m > 0) {
		//	d = Math.min(d, 600);
		} else {
			m *= 1 + 1E5 / (d * d);
		}
		double force = (m / Math.sqrt(d)) * 1E-2;			
		force /= mass;
		if (force > 20) force = 20;
		if (Double.isNaN(force) || Double.isInfinite(force)) force = 0;
		
		double dx = ((x2 - x1) / d * force);
		double dy = ((y2 - y1) / d * force);

		data.ent[i][2] += dx;
		data.ent[i][3] += dy;
		data.ent[j][2] -= dx;
		data.ent[j][3] -= dy;
	}
	
	public void glue(int i, int j) {
		double sx = (data.ent[i][2] + data.ent[j][2]) / 2;
		double sy = (data.ent[i][3] + data.ent[j][3]) / 2;
		
		data.ent[i][2] = sx;
		data.ent[i][3] = sy;
		data.ent[j][2] = sx;
		data.ent[j][3] = sy;	
	}
	
	public void addSpeed(int i) {
		data.ent[i][0] += data.ent[i][2];
		data.ent[i][1] += data.ent[i][3];
		double k = 1 - 0.01 / mass;
		data.ent[i][2] *= k;
		data.ent[i][3] *= k;
	}
	
	public void center() {
		double avgX = 0, avgY = 0;
		for (int i = 0; i < data.ent.length; i++) {
			double x = data.ent[i][0];
			double y = data.ent[i][1];
			avgX += x;
			avgY += y;
		}
		avgX /= data.ent.length;
		avgY /= data.ent.length;
		avgX -= getWidth() / 2;
		avgY -= getHeight() / 2;

		for (int i = 0; i < data.ent.length; i++) {
			data.ent[i][0] -= avgX;
			data.ent[i][1] -= avgY;
		}
	}

	public double minmax(double min, double max, double n) {
		return Math.min(Math.max(n, min), max);
	}
	
	public void reMove() {
		if (!run) return;
		try {
			Thread.sleep(speed);
			if (grad)
				move();
			move();
			repaint();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (System.nanoTime() / (int)1E9 - startTime > 10) {
			predict.predict();
			startTime = System.nanoTime() / (int)1E9;
		}
	}
	
	Image[] imgcache;
	
	public void paint(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(new Color(100, 100, 100));
		double rc = border * zoom * 2;
		int xc = (int)((double)getWidth() / 2 - rc / 2);
		int yc = (int)((double)getHeight() / 2 - rc / 2);
		g.drawOval(xc, yc, (int)rc, (int)rc);
		
		if (grad) {
			imgcache = new Image[(int)(data.maxCat + 1)];
			for (int i = 0; i < data.ent.length; i++) {
				int x = (int)Math.round(data.ent[i][0] * zoom + ((1 - zoom) * getWidth() / 2));
				int y = (int)Math.round(data.ent[i][1] * zoom + ((1 - zoom) * getHeight() / 2));
				int r = 100;
				
				if (imgcache[(int)data.cat[i]] == null) {
					imgcache[(int)data.cat[i]] = getGraphicsConfiguration().createCompatibleImage(2*r, 2*r, Transparency.TRANSLUCENT);
		            Graphics2D gImg = (Graphics2D)imgcache[(int)data.cat[i]].getGraphics();
		            gImg.setComposite(AlphaComposite.Src);
		            gImg.setColor(new Color(0, 0, 0, 0));
		            gImg.fillRect(0, 0, 2*r, 2*r);
		            
		            Graphics2D g2 = (Graphics2D) gImg.create();
					Paint p = new RadialGradientPaint(new Point2D.Double(r, r), r, 
							new float[] {0f, 1f}, 
							new Color[] {getColor(i, 0.5f), getColor(i, 0f)});
					g2.setPaint(p);
					g2.fillOval(0, 0, 2 * r, 2 * r);
					g2.dispose();
					
					gImg.dispose();
				}
				g.drawImage(imgcache[(int)data.cat[i]], x - r, y - r, null);
				
				r = (int)Math.ceil(6 * zoom);
				g.setColor(new Color(0f, 0f, 0f, 1f));
				g.fillOval(x - r, y - r, 2 * r, 2 * r);
			}
		}
		
		for (int i = 0; i < data.ent.length; i++) {
			int x = (int)Math.round(data.ent[i][0] * zoom + ((1 - zoom) * getWidth() / 2));
			int y = (int)Math.round(data.ent[i][1] * zoom + ((1 - zoom) * getHeight() / 2));
							
			int r = (int)Math.ceil(5 * zoom);
			g.setColor(getColor(i, 1f));
			g.fillOval(x - r, y - r, 2 * r, 2 * r);
		}
		printMenu(g);
		printColors(g);
		reMove();
	}
	
	
	public static void setColors() {
		colors = new TreeMap<Double, Color>();
		float hg = 0f; 
		float d = 1.0f / (data.catList.size() - 1);
		for (double c : data.catList) {
			float h = 0;
			float s = 1f;
			float b = 0f;
			
			if (c!= -1) {
				h = hg;
				s = 0.8f;
				b = 0.9f;
				hg += d;
			} 
			
			Color col = Color.getHSBColor(h, s, b);
			colors.put(c, col);
		}
	}
	
	public void printColors(Graphics g) {
		int d = 15, x = 10, y = (int)Math.round(getHeight() - data.catList.size() * d);
		g.setFont(new Font("Helvetica", Font.BOLD,  12));
		
		g.setColor(new Color(0, 0, 0, 150));
		g.fillRect(0, y - 20, 100 , data.cat.length * d);
		
		for (double i : colors.keySet()) {
			g.setColor(colors.get(i));
			g.drawString(data.catName(i), x, y); y += d;
		}
	}
	
	public Color getColor(int i, float alpha) {
		Color c = colors.get(data.cat[i]);
		float[] cols = new float[3];
		cols = c.getRGBColorComponents(cols);
		
		return new Color(cols[0], cols[1], cols[2], alpha);
	}

    public Main() {    	
        JFrame frame = new JFrame("dataviz");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.add(this);
        frame.addKeyListener(this);
        this.setBackground(Color.white);
        frame.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener() {
			public void ancestorMoved(HierarchyEvent e) {}
			public void ancestorResized(HierarchyEvent e) {center();}			
		});
        center();
    }
    
    public void printMenu(Graphics g) {
		int x = 10, y = 20, d = 15;
		
		g.setColor(new Color(200, 200, 255, 150));
		g.fillRect(0, 0, 230 , 14 * d);
		
		g.setColor(new Color(0, 0, 0, 255));
		g.setFont(new Font("Helvetica", Font.BOLD,  12));
		
		g.drawString("play / pause: enter", x, y); y += d;
		g.drawString("restart: backspace", x, y); y += d;
		y += d;
		g.drawString("zoom: 1 / 2 = " + zoom, x, y); y += d;
		g.drawString("step: 9 / 0 = " + (float)Math.round(speed * 10) / 10, x, y); y += d;
		g.drawString("show gradient: g ", x, y); y += d;
		y += d;
		g.drawString("inertia: left / right = " + (float)Math.round(mass * 10) / 10, x, y); y += d;
		g.drawString("threshold: up / down = " + (double)Math.round(inout * 100) / 100, x, y); y += d;
		g.drawString("shrink: pg up / down = " + (double)Math.round(shrinkSpeed * 100000) / 100000, x, y); y += d;
		y += d;
		g.drawString("Run prediction: p", x, y); y += d;
		g.drawString("     CA = " + predict.CA, x, y); y += d;
    }

	@Override
	public void keyPressed(KeyEvent e) {
    	switch (e.getKeyCode()) {
			case KeyEvent.VK_1 : zoom *= 2; break;
			case KeyEvent.VK_2 : zoom /= 2; break;
			case KeyEvent.VK_G : grad ^= true; break;
		
			case KeyEvent.VK_LEFT : if (mass > 0.11) mass /= 2; break;
			case KeyEvent.VK_RIGHT : mass *= 2; break;
		
			case KeyEvent.VK_UP : inout+=0.1; break;
			case KeyEvent.VK_DOWN : inout-=0.1; break;

			case KeyEvent.VK_9 : speed *= 2; break;
			case KeyEvent.VK_0 : speed /= 2; break;
			
			case KeyEvent.VK_PAGE_UP : if (shrinkSpeed < 0.999991) shrinkSpeed = (shrinkSpeed + 9) / 10; break;
			case KeyEvent.VK_PAGE_DOWN : if (shrinkSpeed > 0.91)shrinkSpeed = (shrinkSpeed * 10) - 9; break;
			
			case KeyEvent.VK_ENTER : run ^= true; break;
			case KeyEvent.VK_BACK_SPACE : restart(); break;
			
			case KeyEvent.VK_P : predict(); break;
    	}
    	
    	//System.out.printf("z:%.2f m:%.2f io:%.0f\n", zoom, mass, inout);
    	repaint();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
