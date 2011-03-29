import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.TreeMap;

public class Data {
	public Conn[] conn;
	
	public double avgConn;

	public Entity[] entity;
	public Clas[] clas;

	public Data(String filename) {
		parseFile(filename);
		
		randomTest();
		
		AttrMeta.parseStats(entity);
		calculateConnections();

	}
	
	public void randomTest() {
		for (Entity en : entity) {
			if (Math.random() < Main.testSize) {
				en.clasHidden = true;
			}
		}
	}
	
	public void randomPosition(double border) {
		for (Entity ent : entity) {
			double o = Math.random() * Math.PI * 2;
			double d = (1.0 - Math.pow(Math.random(), 2)) * (border - 100);
			
			ent.x = Math.sin(o) * d + border;
			ent.y = Math.cos(o) * d + border;
		}
	}
	
	private void calculateConnections() {
		ArrayList<Conn> cons = new ArrayList<Conn>(); 
		avgConn = 0;
		for (int i = 0; i < entity.length; i++) {
			for (int j = 0; j < i; j++) {
				Conn c = new Conn(entity[i], entity[j]);
				cons.add(c);
				avgConn += c.strength;
			}
		}
		
		conn = new Conn[cons.size()];
		cons.toArray(conn);
		
		avgConn /= conn.length;
		
		System.out.println("connections caltulated: ");
		System.out.println("  " + conn.length + " connections");
		System.out.println("  " + avgConn + " avg strength");
		System.out.println();
	}
		
	private void parseFile(String filename) {
		try {
			BufferedReader read = new BufferedReader(new FileReader(filename));
			
			String[] cn = read.readLine().split(",");
			int clasIdx = Integer.parseInt(cn[0]);
			int nameIdx = Integer.parseInt(cn[1]);
			
			AttrMeta.init(read.readLine().split(","));
			
			ArrayList<String[]> file =  new ArrayList<String[]>();
			while (read.ready()) {
				file.add(read.readLine().split(","));
			}
			
			parseEntities(file, clasIdx, nameIdx);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("\"" + filename + "\" file parsed:");
		System.out.println("  " + entity.length + " entries");
		System.out.println("  " + AttrMeta.size + " attributes");
		System.out.println("  " + clas.length + " classes");
		System.out.println();
	}
	
	private TreeMap<String, Clas> parseClas(ArrayList<String[]> file, int clasIdx) {
		TreeMap<String, Clas> cl = new TreeMap<String, Clas>();
		
		for (String[] s : file) {
			String name = s[clasIdx];
			if (!cl.containsKey(name)) {
				cl.put(name, new Clas(name));
			}
		}
		
		return cl;
	}
	
	private void parseEntities(ArrayList<String[]> file, int clasIdx, int nameIdx) {
		TreeMap<String, Clas> cl = parseClas(file, clasIdx);
		clas = new Clas[cl.size()];
		cl.values().toArray(clas);
		
		AttrMeta.setClas(clas);
		
		ArrayList<Entity> ents = new ArrayList<Entity>();
		for (String[] sents : file) {
			ArrayList<String> ats = new ArrayList<String>();
			for (int i = 0; i < sents.length; i++) {
				if (i != clasIdx && i != nameIdx) {
					ats.add(sents[i]);
				}
			}
			String[] atsa = new String[ats.size()];
			ats.toArray(atsa);
			ents.add(new Entity(cl.get(sents[clasIdx]), atsa));
		}
		
		entity = new Entity[ents.size()];
		ents.toArray(entity);
	}
}
