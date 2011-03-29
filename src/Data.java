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

			String name = read.readLine();
			String type = read.readLine();
			String meta = read.readLine();
			AttrMeta.init(name, type, meta);
			
			ArrayList<String[]> file =  new ArrayList<String[]>();
			while (read.ready()) {
				String line = read.readLine();
				if (line.length() > 0) {
					file.add(line.split("\t"));
				}
			}
			
			parseEntities(file);
			
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
	
	private TreeMap<String, Clas> parseClas(ArrayList<String[]> file) {
		TreeMap<String, Clas> cl = new TreeMap<String, Clas>();
		
		for (String[] s : file) {
			String name = s[AttrMeta.clasIdx];
			if (!cl.containsKey(name)) {
				cl.put(name, new Clas(name));
			}
		}
		
		return cl;
	}
	
	private void parseEntities(ArrayList<String[]> file) {
		TreeMap<String, Clas> cl = parseClas(file);
		clas = new Clas[cl.size()];
		cl.values().toArray(clas);
		
		AttrMeta.setClas(clas);
		
		ArrayList<Entity> ents = new ArrayList<Entity>();
		for (String[] sents : file) {
			ArrayList<String> ats = new ArrayList<String>();
			String name = "";
			for (int i = 0; i < sents.length; i++) {
				if (i != AttrMeta.clasIdx && i != AttrMeta.nameIdx) {
					ats.add(sents[i]);
				} else if (i == AttrMeta.nameIdx) {
					name = sents[i];
				}
			}
			String[] atsa = new String[ats.size()];
			ats.toArray(atsa);
			ents.add(new Entity(cl.get(sents[AttrMeta.clasIdx]), atsa, name));
		}
		
		entity = new Entity[ents.size()];
		ents.toArray(entity);
	}
}
