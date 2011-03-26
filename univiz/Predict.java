import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;

public class Predict {
	ParseData data;
	public double CA;
	public Predict(ParseData d) {
		data = d;
	}
	
	public void predict() {
		int hit = 0, all = 0;
		for (int i = 0; i < data.ent.length; i++) {
			if (data.cat[i] == -1) {
				if (predictFor(i)) {
					hit++;
				} 
				all++;;
			}
		}
		CA = (double)(hit * 1000 / all) / 10;
		System.out.println(hit + " / " + all + " = " + CA + "%");
	}
	
	public boolean predictFor(int idx) {
		TreeMap<Double, Double> cats = new TreeMap<Double, Double>();
		ArrayList<double[]> na = new ArrayList<double[]>();
		
		double x = data.ent[idx][0];
		double y = data.ent[idx][1];
		
		for (int i = 0; i < data.ent.length; i++) {
			if (i != idx && data.cat[i] != -1) {
				double cat = data.cat[i];
				
				double x1 = data.ent[i][0];
				double y1 = data.ent[i][1];
				double d = Math.sqrt(Math.pow(x1 - x, 2) + Math.pow(y1 - y, 2));
				d = Math.max(d, 5);
				na.add(new double[] {cat, 1.0 / d});
			}
		}
		
		Collections.sort(na, new Comparator<double[]>() {
			public int compare(double[] a0, double[] a1) {
				return a0[1] > a1[1] ? -1 : 1;
			}
		});
		
		double sum = 0;
		for (int i = 0; i < na.size() / 2; i++) {
			double[] n = na.get(i);
			
			double d = 1 / n[1];

			if (d > 20) {
				double avg = sum / i;
				if (i > 2 && d > avg * 4 || i > 0 && d > avg * 10) {
					System.out.println(i);
					break;
				}
			}
			
			if (cats.containsKey(n[0])) n[1] += cats.get(n[0]);
			cats.put(n[0], n[1]);
			
			sum += d;
		}
		
		double maxKey = -1, maxValue = -1;
		for (Double key : cats.keySet()) {
			double value = cats.get(key);
			System.out.println(key + ": " + value);
			if (value > maxValue) {
				maxValue = value;
				maxKey = key;
			}
		}
		double trueW = data.realCat[idx];
		
		System.out.println("Winner: " + maxKey);
		System.out.println("True winner: " + trueW);
		
		System.out.println();
		
		return trueW == maxKey;
	}
	
}
