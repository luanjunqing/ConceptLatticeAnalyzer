package conceptLatticeAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class ShowSubCallee {
	private CalleeCaller cc;
	private HashMap<Integer, ArrayList<String>> attribute;
	private ArrayList<Pair<Integer, Integer>> edge;
	private int conceptMax;
	private ArrayList<Boolean> flag;
	public ShowSubCallee(CalleeCaller cc,
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
				ConcurrentSkipListSet<Integer> subConcept = new ConcurrentSkipListSet<Integer>();
				ConceptTools.getAllSubConcept(nextConcept, edge, subConcept);
				ArrayList<String> ats = attribute.get(nextConcept);
				while(!subConcept.isEmpty()){
					int con = subConcept.pollFirst();
					if(!attribute.containsKey(con))
						continue;
					ArrayList<String> conats = attribute.get(con);
					for(int i=0, other=1; i<ats.size(); i++){
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
								conats.set(j, "super"+nextConcept+"--"+other+"==>"+conats.get(j));
							}
						}
						other++;
					}
				}
			}
			flag.set(nextConcept, true);
		}
		return attribute;
	}
	
	private Integer nextConcept() {
		ArrayList<Integer> subConcepts;
		boolean temp;
		for(int i=0; i<=conceptMax; i++) {
			if(flag.get(i))
				continue;
			if(!attribute.containsKey(i)) {
				flag.set(i, true);
				i = 0;
				continue;
			}
			subConcepts = ConceptTools.getSubConcept(i, edge);
			temp = true;
			if(!subConcepts.isEmpty()){
				for(Integer subConcept : subConcepts){
					temp &= flag.get(subConcept);
				}
			}
			if(temp)
				return i;
		}
		return null;
	}
}
