import java.awt.Color;


public class Entity {
	public double x;
	public double y;
	
	public double speedX;
	public double speedY;
	
	public double[] attr;
	
	public String name;
	
	public Clas clas;
	public boolean clasHidden;
	
	public Entity(Clas cl, String[] at, String n) {
		clas = cl;
		name = n;
		clasHidden = false;
		attr = AttrMeta.translateArr(at);
		
		clas.add(this);
	}
	
	public Color color() {
		if (clasHidden) {
			return Color.getHSBColor(0f, 0f, 0f);
		} else {
			return clas.color();
		}
	}
	
	public void move() {
		x += speedX;
		y += speedY;
	}
	
	public void moveBack() {
		x -= speedX;
		y -= speedY;
	}
	
}
