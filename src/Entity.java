import java.awt.Color;


public class Entity {
	public double x;
	public double y;
	
	public double speedX;
	public double speedY;
	
	public double[] attr;
	
	public String name;
	
	public Clas clas;
	public Clas predClas;
	public boolean clasHidden;
	
	public double force = 0;
	
	public Entity(Clas cl, String[] at, String n) {
		clas = cl;
		name = n;
		clasHidden = false;
		attr = AttrMeta.translateArr(at);
		
		clas.add(this);
	}
	
	public Color color(boolean pred) {
		if (clasHidden) {
			if (pred && predClas != null) {
				return clas.color().darker();
			} else {
				return Color.getHSBColor(0f, 0f, 0f);
			}
		} else {
			Color col = clas.color();
			
			for (int i = 0; i < force - 1; i++) 
				col = col.darker();
			//System.out.println(force);
			force = 0;
			
			return col;
		}
	}
	
	public void move() {
		if (speedX > 10) speedX = 10; else if (speedX < -10) speedX = -10;
		if (speedY > 10) speedY = 10; else if (speedY < -10) speedY = -10;
		
		x += speedX;
		y += speedY;
	}
	
	public void moveBack() {
		x -= speedX;
		y -= speedY;
	}
	
}
