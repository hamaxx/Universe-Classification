import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class ParseData {
	private ArrayList<ArrayList<Double>> data;
	String[] type; 
	public double[][] conn;
	public double[][] ent;
	public double cat[];
	public double realCat[];
	public TreeSet<Double> catList;
	public double maxCat;
	public double avgCon;
	public int stAtr;
	public double border;
	
	private TreeMap<String, Double> trans;
	private TreeMap<Double, String> transStr;
	private double curTrans = 100;
	private int clasIdx, nameIdx;
	
	private double sample;
	
	public ParseData(String fnTr, String fnTe, double s) {
		data = new ArrayList<ArrayList<Double>>();
		trans = new TreeMap<String, Double>();
		transStr = new TreeMap<Double, String>();
		catList = new TreeSet<Double>();
		sample = s;
		
		readFile(fnTr, false);
		if (fnTe != null)
			readFile(fnTe, true);
		
		parseConn();
		setEnt();
		parseCat();
	}
	
	private double getNum(String str) {
		if (trans.containsKey(str)) {
			return trans.get(str);
		}
		try {
			return Double.parseDouble(str);
		} catch (Exception e) {}
		trans.put(str, curTrans);
		transStr.put(curTrans, str);
		return curTrans++;
	}
	
	public String catName(double idx) {
		if (transStr.containsKey(idx)) {
			return transStr.get(idx);
		}
		return Double.toString(idx);
	}

	private void parseConn() {
		int size = data.size();
		conn = new double[size][size];
		
		avgCon = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < i; j++) {
				double strength = connStrength(i, j);
				conn[i][j] = strength;
				avgCon += strength;
			}
		}
		avgCon /= (size * (size - 1)) / 2;
		System.out.println("connections calculated " + avgCon);
	}
	
	public void parseCat() {
		cat = new double[ent.length];
		realCat = new double[ent.length];
		maxCat = 0;
		for (int i = 0; i < data.size(); i++) {
			double c = data.get(i).get(0);
			
			if (Math.random() < sample) {
				realCat[i] = c;
				c = -1;
			}
			
			if (c > maxCat) maxCat = c;
			cat[i] = c;
		}
	}
	
	public void setEnt() {
		ent = new double[data.size()][4];
		border = Math.max(ent.length * 4, 500);
		for (int i = 0; i < ent.length; i++) {
			double o = Math.random() * Math.PI * 2;
			double d = (1.0 - Math.pow(Math.random(), 2)) * (border - 100);
			
			ent[i][0] = Math.sin(o) * d + border / 2;
			ent[i][1] = Math.cos(o) * d + border / 2;
		}
	}
	
	private double connStrength(int a1, int a2) {
		ArrayList<Double> el1 = data.get(a1);
		ArrayList<Double> el2 = data.get(a2);
		
		double strength = 0;
		for (int i = 1; i < el1.size(); i++) {
			
			double d = Math.abs(el1.get(i) - el2.get(i));
			double s = 0;
			if (d == 0) {
				s = getDConStat(i, el1.get(i));
			} else if (type[i - 1].equals("c")) {
				double[] stat = getCConStat(i);
				double n = (el1.get(i) + el2.get(i)) - 2 * stat[0];
				s = Math.pow(1 - (d / n), 2);
				s = Math.abs(s - stat[1]);
				//System.out.println(s);
			}
			
			strength += s;
		}
		
		return strength;
	}
	
	private double getDConStat(int idx, double val) {
		TreeMap<Double, Integer> dist = new TreeMap<Double, Integer>();
		for (ArrayList<Double> a : data) {
			double m = a.get(idx);
			int st = 0;
			if (dist.containsKey(m)) st = dist.get(m);
			dist.put(m, st + 1);
		}
		double all = data.size();
		double inVal = dist.get(val);
		
		//System.out.println(all + " " + inVal + " " + (1 - inVal / all));
		
		return 1 - inVal / all;
	}
	
	private double[][] getCConStatCache;
	private double[] getCConStat(int idx) {
		if (getCConStatCache == null) getCConStatCache = new double[data.get(0).size()][];
		else if (getCConStatCache[idx] != null) return getCConStatCache[idx];
		
		double min = 1E9;
		for (ArrayList<Double> a : data) {
			double m = a.get(idx);
			if (m < min) min = m;
		}

		double avgCon = 0; int count = 0; double minCon = 1E6;
		for (ArrayList<Double> a1 : data) {
			for (ArrayList<Double> a2 : data) {
				if (a1 != a2) {
					double n = (a1.get(idx) + a2.get(idx)) - 2 * min;
					double d = Math.abs(a1.get(idx) - a2.get(idx));
					double c = d == 0 ? 1 : Math.pow(1 - (d / n), 2);
					
					if (c < minCon) minCon = c;
					avgCon += c;
					count++;
				}
			}
		}
		avgCon /= count;
		
		getCConStatCache[idx] = new double[]{min, avgCon, 0};
		return getCConStatCache[idx];
	}
	
	private void readFile(String fn, boolean test) {
		try {
			BufferedReader read = new BufferedReader(new FileReader(fn));
			
			String[] cn = read.readLine().split(",");
			clasIdx = Integer.parseInt(cn[0]);
			nameIdx = Integer.parseInt(cn[1]);
			
			type = read.readLine().split(",");
			
			while (read.ready()) {		
				ArrayList<Double> ani = new ArrayList<Double>();
				String[] line = read.readLine().split(",");

				String clas = line[clasIdx];
				double d = getNum(clas);
				ani.add(d);
				catList.add(d);

				
				for (int i = 0; i < line.length; i++) {
					if (i == nameIdx || i == clasIdx) continue;
					
					String num = line[i];
					d = getNum(num);
					ani.add(d);
				}
				data.add(ani);
			}
			stAtr = data.get(0).size() - 1;
			System.out.println("file loaded " + stAtr);			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		catList.add(-1d);
	}
	
	public void printConn() {
		for (double[] an : conn) {
			for (double c : an) {
				System.out.print(c + " ");
			}
			System.out.println();
		}
	}
}
