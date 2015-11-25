package conceptLatticeAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;

//if concept has method whoes callees also belong to the concept, remove callees
public class WithoutCallee {
	private CalleeCaller cc;
	private HashMap<Integer, ArrayList<String>> attribute;
	int conceptMax;
	
	public WithoutCallee(CalleeCaller cc, HashMap<Integer, ArrayList<String>> attribute, int conceptMax){
		this.cc =cc;
		this.attribute = attribute;
		this.conceptMax = conceptMax;
	}
	
	public HashMap<Integer, ArrayList<String>> change(){
		for(int i=0; i<=conceptMax; i++) {
			if(!attribute.containsKey(i))
				continue;
			attribute.put(i, callerCalleeChange(attribute.get(i)));
		}
		return attribute;
	}
	
	private ArrayList<String> callerCalleeChange(ArrayList<String> modules) {
		ArrayList<String> afterChange = new ArrayList<String>();
		HashMap<String, ArrayList<Integer>> callerCalleeTable = makeCCTable(modules);
		for(int i=0; i<modules.size(); i++){
			if(callerCalleeTable.get(modules.get(i)).get(0) != i)
				continue;
			changeCallee(afterChange, modules, callerCalleeTable, i, 0);
		}
		return afterChange;
	}
	
	private void changeCallee(ArrayList<String> afterChange,
			ArrayList<String> modules,
			HashMap<String, ArrayList<Integer>> callerCalleeTable,
			int now, int num){
		String s = "";
		for(int i=0; i<num; i++)
			s += "-->";
		afterChange.add(s + modules.get(now));
		ArrayList<Integer> temp = callerCalleeTable.get(modules.get(now));
		if(temp.size() > 1)
			for(int i=1; i<temp.size(); i++)
				changeCallee(afterChange, modules, callerCalleeTable, temp.get(i), num+1);
	}
	
	private HashMap<String, ArrayList<Integer>> makeCCTable(ArrayList<String> modules){
		HashMap<String, ArrayList<Integer>> callerCalleeTable = new HashMap<>();
		ArrayList<Integer> temp;
		for(int i=0; i<modules.size(); i++) {
			temp = new ArrayList<Integer>();
			temp.add(i);
			callerCalleeTable.put(modules.get(i), temp);
		}
		for(int i=0; i<modules.size(); i++) {
			for(int j=0; j<modules.size(); j++) {
				if(i == j)
					continue;
				if(cc.isCallerCallee(modules.get(i),modules.get(j))) {
					temp = callerCalleeTable.get(modules.get(i));
					temp.add(j);
					callerCalleeTable.put(modules.get(i), temp);
					temp = callerCalleeTable.get(modules.get(j));
					temp.set(0, i);
					callerCalleeTable.put(modules.get(j), temp);
				}
			}
		}
		return callerCalleeTable;
	}
	
}
