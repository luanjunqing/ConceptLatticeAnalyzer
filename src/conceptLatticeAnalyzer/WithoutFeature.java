package conceptLatticeAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class WithoutFeature {
	private HashMap<Integer, Pair<Double, Double>> node;
	private ArrayList<Pair<Integer, Integer>> edge;
	private HashMap<Integer, ArrayList<String>> object,attribute;
	private int conceptMax;
	private ConcurrentSkipListSet<Integer> reachConcept;
	
	public WithoutFeature(HashMap<Integer, Pair<Double, Double>> node,
			ArrayList<Pair<Integer, Integer>> edge,
			HashMap<Integer, ArrayList<String>> object,
			HashMap<Integer, ArrayList<String>> attribute, int conceptMax){
		this.node = node;
		this.edge = edge;
		this.object = object;
		this.attribute = attribute;
		this.conceptMax = conceptMax;
		this.reachConcept = new ConcurrentSkipListSet<Integer>();
	}
	
	public void change(int featureNum){
		getAllReachConcept(featureNum);
		for(int i=0; i<=conceptMax; i++){
			if(!node.containsKey(i))
				continue;
			if(!reachConcept.contains(i))
				node.remove(i);
		}
		for(int i=0; i<edge.size(); i++){
			if(!reachConcept.contains(edge.get(i).first()) || !reachConcept.contains(edge.get(i).second())){
				edge.remove(i);
				i--;
			}
		}
		for(int i=0; i<=conceptMax; i++){
			if(!object.containsKey(i))
				continue;
			if(!reachConcept.contains(i))
				object.remove(i);
		}
		for(int i=0; i<=conceptMax; i++){
			if(!attribute.containsKey(i))
				continue;
			if(!reachConcept.contains(i))
				attribute.remove(i);
		}
	}
	
	private void getAllReachConcept(int featureNum){
		int featureConceptNum = ConceptTools.getFeatureConcept(featureNum, attribute, conceptMax);
		reachConcept.add(featureConceptNum);
		//superConcept
		ConcurrentSkipListSet<Integer> allSuper = new ConcurrentSkipListSet<Integer>();
		ConceptTools.getAllSuperConcept(featureConceptNum, edge, allSuper);
		while(!allSuper.isEmpty())
			reachConcept.add(allSuper.pollFirst());
		//subConcept
		ConcurrentSkipListSet<Integer> allSub = new ConcurrentSkipListSet<Integer>();
		ConceptTools.getAllSubConcept(featureConceptNum, edge, allSub);
		while(!allSub.isEmpty()){
			if(object.containsKey(allSub.first())) {
				ConceptTools.getAllSuperConcept(allSub.first(), edge, allSuper);
				while(!allSuper.isEmpty())
					reachConcept.add(allSuper.pollFirst());
			}
			reachConcept.add(allSub.pollFirst());
		}
	}
	
	
}
