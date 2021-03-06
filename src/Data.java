import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.TreeMap;

public class Data {
	public Conn[] conn;
	
	public double avgConn;

	public Entity[] entity;
	public Clas[] clas;

	public Data(String filename) {
		parseFile(filename);
		
		//randomTest();
		
		AttrMeta.parseStats(entity, clas.length > 1);
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
	
	public void calculateConnections() {
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
	
	public void export() {
		String out = "";
		out += "name	class	x	y\n";
		out += "string	d	c	c\n";
		out += "meta	class\n";
		
		for (Entity e : entity) {
			out += e.name + "	" + e.clas.name + "	" + e.x + "	" + e.y + "\n";
		}
		try{
			FileWriter fstream = new FileWriter("out.tab");
			BufferedWriter outf = new BufferedWriter(fstream);
			outf.write(out);
			outf.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
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
				if (Math.random() < Main.testSize) {
					if (line.length() > 0) {
						file.add(line.split("\t"));
					}
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
		//System.out.println();
	}
	
	private TreeMap<String, Clas> parseClas(ArrayList<String[]> file) {
		TreeMap<String, Clas> cl = new TreeMap<String, Clas>();
		
		if (AttrMeta.clasIdx < 0) {
			cl.put("none", new Clas("none"));
			return cl;
		}
		
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
			String name = "x";
			for (int i = 0; i < sents.length; i++) {
				if (i != AttrMeta.clasIdx && i != AttrMeta.nameIdx) {
					ats.add(sents[i]);
				} else if (i == AttrMeta.nameIdx) {
					name = sents[i];
				}
			}
			String[] atsa = new String[ats.size()];
			ats.toArray(atsa);
			Clas c = AttrMeta.clasIdx < 0 ? cl.firstEntry().getValue() : cl.get(sents[AttrMeta.clasIdx]);
			ents.add(new Entity(c, atsa, name));
		}
		
		entity = new Entity[ents.size()];
		ents.toArray(entity);
	}
}
