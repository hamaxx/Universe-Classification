import java.awt.Color;
import java.util.ArrayList;

public class Clas implements Comparable<Clas>{
	public String name;
	
	public static int num = 0;
	public int id;
	
	public ArrayList<Entity> entity;
	
	public Clas(String n) {
		name = n;
		entity = new ArrayList<Entity>();
		id = num;
		num++;
	}
	
	public void add(Entity e) {
		entity.add(e);
	}
	
	public int stEl(double value, int attr) {
		if (Double.isNaN(value)) {
			int entLength = 0;
			for (Entity en : entity) entLength += en.clasHidden ? 0 : 1;
			return entLength;
		} else {
			int count = 0;
			
			for (Entity e : entity) {
				if (!e.clasHidden) {
					if (AttrMeta.discreteValue(attr, e.attr[attr]) == value) {
						count++;
					}
				}
			}
			
			return count;
		}
	}
	
	public Color color() {
		float h = (float)id / num;
		float s = 0.8f;
		float b = 0.8f;
		
		return Color.getHSBColor(h, s, b);
	}

	@Override
	public int compareTo(Clas o) {
		return id - o.id;
	}
}
