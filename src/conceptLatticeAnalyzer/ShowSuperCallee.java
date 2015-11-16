package conceptLatticeAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class ShowSuperCallee {
	private CalleeCaller cc;
	private HashMap<Integer, ArrayList<String>> attribute;
	private ArrayList<Pair<Integer, Integer>> edge;
	private int conceptMax;
	private ArrayList<Boolean> flag;
	public ShowSuperCallee(CalleeCaller cc,
			HashMap<Integer, ArrayList<String>> attribute,
			ArrayList<Pair<Integer, Integer>> edge, int conceptMax){
		this.cc =cc;
		this.attribute = attribute;
		this.edge = edge;
		this.conceptMax = conceptMax;
		flag = new ArrayList<Boolean>();
		for(int i=0; i<=conceptMax; i++)
			flag.add(false);
	}
	
	public HashMap<Integer, ArrayList<String>> change() {
		Integer nextConcept;
		while((nextConcept = nextConcept()) != null) {
			if(attribute.containsKey(nextConcept)){
				ConcurrentSkipListSet<Integer> superConcept = new ConcurrentSkipListSet<Integer>();
				ConceptTools.getAllSuperConcept(nextConcept, edge, superConcept);
				ArrayList<String> ats = attribute.get(nextConcept);
				while(!superConcept.isEmpty()){
					int con = superConcept.pollFirst();
					if(!attribute.containsKey(con))
						continue;
					ArrayList<String> conats = attribute.get(con);
					for(int i=0; i<ats.size(); i++){
						String at = ats.get(i);
						if(at.indexOf("-->")!=-1)
							at = at.substring(at.lastIndexOf("-->")+3);
						for(int j=0; j<conats.size(); j++) {
							String conat = conats.get(j);
							if(conat.indexOf("-->")!=-1)
								conat = conat.substring(conat.lastIndexOf("-->")+3);							
							if(cc.isCallerCallee(at, conat)) {
								conats.set(j, "sub"+nextConcept+"--"+(i+1)+"==>"+conats.get(j));
							}
						}
					}
				}
			}
			flag.set(nextConcept, true);
		}
		return attribute;
	}
	
	private Integer nextConcept() {
		ArrayList<Integer> superConcepts;
		boolean temp;
		for(int i=0; i<=conceptMax; i++) {
			if(flag.get(i))
				continue;
			if(!attribute.containsKey(i)) {
				flag.set(i, true);
				i = 0;
				continue;
			}
			superConcepts = ConceptTools.getSuperConcept(i, edge);
			temp = true;
			if(!superConcepts.isEmpty()){
				for(Integer superConcept : superConcepts){
					temp &= flag.get(superConcept);
				}
			}
			if(temp)
				return i;
		}
		return null;
	}
}
