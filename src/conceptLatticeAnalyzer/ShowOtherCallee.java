package conceptLatticeAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class ShowOtherCallee {
	private CalleeCaller cc;
	private HashMap<Integer, ArrayList<String>> attribute;
	private ArrayList<Pair<Integer, Integer>> edge;
	private int conceptMax;
	public ShowOtherCallee(CalleeCaller cc,
			HashMap<Integer, ArrayList<String>> attribute,
			ArrayList<Pair<Integer, Integer>> edge, int conceptMax){
		this.cc =cc;
		this.attribute = attribute;
		this.edge = edge;
		this.conceptMax = conceptMax;
	}
	public HashMap<Integer, ArrayList<String>> change() {
		for(int nextConcept=0; nextConcept<=conceptMax; nextConcept++){
			if(!attribute.containsKey(nextConcept))
				continue;
			ConcurrentSkipListSet<Integer> subConcept = new ConcurrentSkipListSet<Integer>();
			ConceptTools.getAllSubConcept(nextConcept, edge, subConcept);
			ConcurrentSkipListSet<Integer> superConcept = new ConcurrentSkipListSet<Integer>();
			ConceptTools.getAllSuperConcept(nextConcept, edge, superConcept);
			ConcurrentSkipListSet<Integer> otherConcept = new ConcurrentSkipListSet<Integer>();
			for(int i=0; i<=conceptMax; i++)
				if(!subConcept.contains(i) && !superConcept.contains(i) && i!=nextConcept && attribute.containsKey(i))
					otherConcept.add(i);
			ArrayList<String> ats = attribute.get(nextConcept);
			while(!otherConcept.isEmpty()){
				int con = otherConcept.pollFirst();
				ArrayList<String> conats = attribute.get(con);
				for(int i=0, order=1; i<ats.size(); i++){
					String at = ats.get(i);
					if(at.substring(0,1).equals("f") && at.indexOf(": ") != -1)
						continue;
					if(at.indexOf("==>")!=-1)
						at = at.substring(at.lastIndexOf("==>")+3);
					if(at.indexOf("-->")!=-1)
						at = at.substring(at.lastIndexOf("-->")+3);
					for(int j=0; j<conats.size(); j++) {
						String conat = conats.get(j);
						if(conat.indexOf("==>")!=-1)
							conat = conat.substring(conat.lastIndexOf("==>")+3);
						if(cc.isCallerCallee(at, conat)) {
							conats.set(j, "other"+nextConcept+"--"+order+"==>"+conats.get(j));
						}
					}
					order++;
				}
			}
		}
		return attribute;
	}	
}
