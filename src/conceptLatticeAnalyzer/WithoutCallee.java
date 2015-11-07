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
	
	@SuppressWarnings("unchecked")
	public HashMap<Integer, ArrayList<String>> change(){
		ArrayList<String> temp1, temp2;
		for(int i=0; i<=conceptMax; i++) {
			if(!attribute.containsKey(i))
				continue;
			temp1 = attribute.get(i);
			temp2 = (ArrayList<String>)temp1.clone();
			for(int j=0; j<temp1.size(); j++){
				for(int k=0; k<temp1.size(); k++){
					if(j == k)
						continue;
					if(cc.isCallerCallee(temp1.get(j), temp2.get(k))){
						String tempS = "---->"+temp2.get(k);
						if("---->".equals(temp2.get(j).substring(0, 5)))
							tempS = temp2.get(j).substring(0, temp2.get(j).lastIndexOf("---->")+5) + tempS;
						temp2.set(k, tempS);
					}
				}
			}
			attribute.put(i, temp2);
		}

		return attribute;
	}
	
}
