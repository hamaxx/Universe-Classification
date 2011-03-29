import java.util.ArrayList;
import java.util.TreeMap;


public class AttrMeta {
	private static final int DISC_N = 10;
	
	private static TreeMap<Integer, TreeMap<String, Double>> trans;
	private static double current[];
	
	public static String[] type;
	public static String[] name;
	
	public static int nameIdx;
	public static int clasIdx;
	
	public static double[] min;
	public static double[] max;
	public static double[] scores;
	public static TreeMap<Integer, TreeMap<Double, Double>> valueScore;
	
	private static Clas[] clas;
	
	public static int size;
	
	public static void reset() {
		scores = null;
		valueScore = null;
		clas = null;
	}
	
	public static void init(String na, String ty, String meta) {
		nameIdx = -1;
		clasIdx = -1;
		
		type = parseType(ty, meta);
		size = type.length;
		name = parseName(na);
		
		current = new double[size];
		
		min = new double[size];
		max = new double[size];
		
		trans = new TreeMap<Integer, TreeMap<String, Double>>();
		for (int i = 0; i < size; i++) {
			trans.put(i, new TreeMap<String, Double>());
		}
	}
	
	private static String[] parseType(String ty, String meta) {
		String[] t = meta.split("\t");
		for (int i = 0; i < t.length; i++) {
			if (t[i].equals("meta")) {
				nameIdx = i;
			} else if (t[i].equals("class")) {
				clasIdx = i;
			}
		}
		
		ArrayList<String> types = new ArrayList<String>();
		t = ty.split("\t");
		for (int i = 0; i < t.length; i++) {
			if (i != nameIdx && i != clasIdx) {
				String st = t[i];
				if (st.equals("c")) st = "c";
				else st = "d";
				types.add(st);
			}
		}
		
		t = new String[types.size()];
		types.toArray(t);

		return t;
	}
	
	private static String[] parseName(String na) {
		if (na.length() > 0) {
			return na.split("\t");
		} else {
			String[] n = new String[size];
			for (int i = 0; i < size; i++) n[i] = "Attr " + (i + 1);
			return n;
		}
	}
	
	public static void setClas(Clas[] c) {
		clas = c;
	}
	
	public static double discreteValue(int idx, double value) {
		if (type[idx].equals("d")) {
			return value;
		}
		
		double range = max[idx] - min[idx];
		double d = range / DISC_N;
		double dval = (int)((value - min[idx]) / d);
		
		return dval;
	}
	
	public static TreeMap<String, Double> getValuesFor(int idx) {
		if (type[idx].equals("d")) {
			return trans.get(idx);
		}
		
		TreeMap<String, Double> tm = new TreeMap<String, Double>();
		for (int i = 0; i <= DISC_N; i++) {
			double range = max[idx] - min[idx];
			double d = range / DISC_N;
			
			String key = (min[idx] + i * d) + " - " + (min[idx] + (i + 1) * d);
			tm.put(key, (double)i);
		}
		
		return tm;
	}
	
	public static double getMin(int idx) {
		return min[idx];
	}
	
	public static double getMax(int idx) {
		return max[idx];
	}
	
	public static void parseStats(Entity[] entity) {
		minamx(entity);
		
		AttrScore sc = new AttrScore(entity, clas);
		scores = sc.score();
		valueScore = sc.valueScore();
	}
	
	public static double valueScore(int idx, double val) {
		val = discreteValue(idx, val);
		double score = 0;
			
		try {
			score = valueScore.get(idx).get(val);
		} catch(Exception e) {
			System.out.println(idx + " " + val);
			for (String d : getValuesFor(idx).keySet()) System.out.println(d);
			System.out.println();
			for (double d : valueScore.get(idx).keySet()) System.out.println(d);
			//System.exit(0);
			System.out.println();
			System.out.println();
		}
		return score;
	}
	
	private static void minamx(Entity[] entity) {
		for (int i = 0; i < size; i++) {
			min[i] = 1E9;
			max[i] = -1;
		}
		
		for (Entity en : entity) {
			for (int i = 0; i < size; i++) {
				double val = en.attr[i];
				if (val < min[i]) min[i] = val;
				if (val > max[i]) max[i] = val;
			}
		}
	}
	
	public static double translate(String satr, int idx) {
		if (type[idx].equals("c")) {
			if (satr.equals("?")) return -1;
			return Double.parseDouble(satr);
		}
			
		if (!trans.get(idx).containsKey(satr)) {
			trans.get(idx).put(satr, current[idx]);
			current[idx]++;
		}
		
		return trans.get(idx).get(satr);
	}
	
	public static double[] translateArr(String[] satr) {
		double[] datr = new double[satr.length];
		
		for (int i = 0; i < datr.length; i++) {
			datr[i] = translate(satr[i], i);
		}
		
		return datr;
	}
}
