package conceptLatticeAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class ScoreConcept {
	private HashMap<Integer,Pair<Double, Double>> node;
	private HashMap<Integer, ArrayList<String>> object,attribute;
	private ArrayList<Pair<Integer, Integer>> edge;
	private int conceptMax;
	private HashMap<Integer, Double> score;
	
	public ScoreConcept(HashMap<Integer, Pair<Double, Double>> node,
			ArrayList<Pair<Integer, Integer>> edge,
			HashMap<Integer, ArrayList<String>> object,
			HashMap<Integer, ArrayList<String>> attribute, int conceptMax) {
		this.node = node;
		this.edge = edge;
		this.object = object;
		this.attribute = attribute;
		this.conceptMax = conceptMax;
		this.score = new HashMap<Integer, Double>();
	}
	
	public HashMap<Integer, Double> calcScore(int featureNum, int featureMax) {
		init(featureNum);
		for(int i=0; i<=featureMax; i++) {
			if(i == featureNum)
				continue;
			edit(i);
		}
		return score;
	}
	
	private void init(int featureNum) {
		for(int i=0; i<=conceptMax; i++)
			if(node.containsKey(i))
				score.put(i, 0D);
		int now = ConceptTools.getFeatureConcept(featureNum, attribute, conceptMax);
		ConcurrentSkipListSet<Integer> allSub = new ConcurrentSkipListSet<Integer>();
		ConcurrentSkipListSet<Integer> allSuper = new ConcurrentSkipListSet<Integer>();
		ConceptTools.getAllSubConcept(now, edge, allSub);
		allSub.add(now);
		while(!allSub.isEmpty()) {
			int con = allSub.pollFirst();
			if(!object.containsKey(con))
				continue;
			ConceptTools.getAllSuperConcept(con, edge, allSuper);
			while(!allSuper.isEmpty())
				score.put(allSuper.pollFirst(), 1D);
			score.put(con, 1D);
		}
	}
	
	private void edit(int featureNum) {
		int now = ConceptTools.getFeatureConcept(featureNum, attribute, conceptMax);
		if(now == -1)
			return;
		ConcurrentSkipListSet<Integer> allSub = new ConcurrentSkipListSet<Integer>();
		ConcurrentSkipListSet<Integer> allSuper = new ConcurrentSkipListSet<Integer>();
		ConcurrentSkipListSet<Integer> editConcept = new ConcurrentSkipListSet<Integer>();
		ConceptTools.getAllSubConcept(now, edge, allSub);
		allSub.add(now);
		while(!allSub.isEmpty()) {
			int con = allSub.pollFirst();
			if(!object.containsKey(con))
				continue;
			ConceptTools.getAllSuperConcept(con, edge, allSuper);
			allSuper.add(con);
			while(!allSuper.isEmpty())
				editConcept.add(allSuper.pollFirst());
		}
		while(!editConcept.isEmpty()){
			double temp = score.get(editConcept.first()); 
			score.put(editConcept.first(), temp/(1+temp));
			editConcept.pollFirst();
		}
	}
}
