import java.util.TreeMap;

public class AttrScore {
	private Entity[] entity;
	private Clas[] clas;
	
	public AttrScore(Entity[] en, Clas[] c) {
		entity = en;
		clas = c;
		
	}
	
	public TreeMap<Integer, TreeMap<Double, Double>> valueScore() {
		TreeMap<Integer, TreeMap<Double, Double>> vs = new TreeMap<Integer, TreeMap<Double, Double>>();
		
		for (int i = 0; i < AttrMeta.size; i++) {
			vs.put(i, valueScoreForAttr(i));
		}
		
		return vs;
	}
	
	public TreeMap<Double, Double> valueScoreForAttr(int idx) {
		TreeMap<Double, Double> score = new TreeMap<Double, Double>();
		
		for (Entity en : entity) {
			double val = AttrMeta.discreteValue(idx, en.attr[idx]);
			double n = 0;
			if (score.containsKey(val)) n += score.get(val);
			score.put(val, n + 1);
		}
		double avg = (double)entity.length / score.size();
		for (double key : score.keySet()) {
			double val = score.get(key);
			val = avg / val;
			score.put(key, Math.sqrt(val));
		}
		
		return score;
	}
	
	public double[] score() {
		double scores[] = new double[AttrMeta.size];
		
		int entLength = 0;
		for (Entity en : entity) entLength += en.clasHidden ? 0 : 1;
		
		double clasEnt = entropyWithProp(Double.NaN, -1, entLength);
		for (int i = 0; i < AttrMeta.size; i++) {
			double entSum = 0;
			for (double value : AttrMeta.getValuesFor(i).values()) {
				entSum += entropyWithProp(value, i, entLength);
			}
			//System.out.println("ent for " + i + ": " + (clasEnt - entSum));
			scores[i] = clasEnt - entSum;
		}
		return scores;
	}
	
	public double entropyWithProp(double value, int attr, int entLength) {
		int i = 0;
		double ent = 0;
		double size[] = new double[clas.length];
		double sizeAll = 0;
		for (Clas cl : clas) {
			double p = (double)cl.stEl(value, attr);
			size[i++] = p;
			sizeAll += p;
		}
		
		if (sizeAll == 0) return 0;
		
		for (double p : size) {
			p /= sizeAll;
			if (p != 0) {
				ent += -p * (Math.log(p) / Math.log(2));
			}
		}
		double prop = sizeAll / entLength;
		
		return ent * prop;
	}
	
}
