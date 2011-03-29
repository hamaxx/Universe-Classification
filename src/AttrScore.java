import java.util.TreeMap;

public class AttrScore {
	private Entity[] entity;
	private Clas[] clas;
	public TreeMap<Integer, TreeMap<Double, Double>> valueScore;
	
	public AttrScore(Entity[] en, Clas[] c) {
		entity = en;
		clas = c;
		valueScore = new TreeMap<Integer, TreeMap<Double, Double>>();
	}
	
	public double[] score() {
		double scores[] = new double[AttrMeta.size];
		
		int entLength = 0;
		for (Entity en : entity) entLength += en.clasHidden ? 0 : 1;
		
		double clasEnt = entropyWithProp(Double.NaN, -1, entLength);
		for (int i = 0; i < AttrMeta.size; i++) {
			valueScore.put(i, new TreeMap<Double, Double>());
			double entSum = 0;
			for (double value : AttrMeta.getValuesFor(i).values()) {
				entSum += entropyWithProp(value, i, entLength);
			}
			System.out.println("ent for " + i + ": " + (clasEnt - entSum));
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
		
		if (!Double.isNaN(value)) {
			valueScore.get(attr).put(value, prop);
		}
		return ent * prop;
	}
	
}
