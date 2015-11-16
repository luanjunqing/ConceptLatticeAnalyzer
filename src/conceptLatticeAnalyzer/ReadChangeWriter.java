package conceptLatticeAnalyzer;

import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static conceptLatticeAnalyzer.ConceptTools.*;

public class ReadChangeWriter {
	private String readPath, writePath;
	private File readFile, writeFile;
	private CalleeCaller cc;
	private BufferedReader br;
	private PrintWriter pw;
	private HashMap<Integer, Pair<Double, Double>> node;
	private ArrayList<Pair<Integer, Integer>> edge;
	private HashMap<Integer, ArrayList<String>> object,attribute;
	private int conceptMax = 0;
	private HashMap<Integer, Double> score;
	public ReadChangeWriter(String readePath, String writePath, String ccpath) {
		this.readPath = readePath;
		this.writePath = writePath;
		cc = new CalleeCaller(ccpath);
		node = new HashMap<Integer, Pair<Double,Double>>();
		edge = new ArrayList<Pair<Integer,Integer>>();
		object = new HashMap<Integer, ArrayList<String>>();
		attribute = new HashMap<Integer, ArrayList<String>>();
	}
	
	public void init() throws IOException, FontFormatException {
		readFile = new File(readPath);
		br = new BufferedReader(new FileReader(readFile));
		writeFile = new File(writePath);
		pw = new PrintWriter(new BufferedWriter(new FileWriter(writeFile)));
		cc.init();
		cc.makeTable();
	}
	
	public void read() throws IOException, FontFormatException {
		String s;
		int conceptNum;
		ArrayList<String> temp;
		while((s = br.readLine()) != null) {
			conceptNum = makeConceptNum(s);
			switch(checkLineType(s)){
			case Node:
				if(conceptMax < conceptNum)
					conceptMax = conceptNum;
				node.put(conceptNum, makeConceptNode(s));
				break;
			case Edge:
				edge.add(makeConceptEdge(s));
				break;
			case Object:
				if(!object.containsKey(conceptNum))
					object.put(conceptNum, new ArrayList<String>());
				temp = object.get(conceptNum);
				temp.add(makeConceptObject(s));
				object.put(conceptNum, temp);
				break;
			case Attribute:
				if(!attribute.containsKey(conceptNum))
					attribute.put(conceptNum, new ArrayList<String>());
				temp = attribute.get(conceptNum);
				temp.add(makeConceptAttribute(s));
				attribute.put(conceptNum, temp);
				break;
			case EOF:
				return;
			case Other:
				System.out.println("Format Error in concept-lattice");
				throw new FontFormatException(s);
			}
		}
	}
	
	public void fina() throws IOException {
		br.close();
		pw.close();
		cc.fina();
	}
	
	public void write() throws IOException {
		for(int i=1; node.containsKey(i); i++)
			pw.println("Node: "+i+", "+node.get(i));
		for(Pair<Integer, Integer> pairII : edge)
			pw.println("Edge: "+pairII);
		for(int i=1; i<=conceptMax; i++){
			if(!object.containsKey(i))
				continue;
			for(String s : object.get(i))
				pw.println("Object: "+i+", "+s);
		}
		for(int i=1; i<=conceptMax; i++){
			if(!attribute.containsKey(i))
				continue;
			for(String s : attribute.get(i))
				pw.println("Attribute: "+i+", "+s);
		}
		pw.println("EOF");
	}
	
	public void change(){
		score = new ScoreConcept(node, edge, object, attribute, conceptMax).calcScore(8, 10);
		new WithoutFeature(node, edge, object, attribute, conceptMax).change(8);
		attribute = new WithoutCallee(cc,attribute,conceptMax).change();
	}
	
	public void writeJson() throws IOException {
		pw.println("var $lattice = {");
		pw.println("  \"concepts\": {");
		for(int i=0; i<=conceptMax; i++){
			if(!node.containsKey(i))
				continue;
			pw.println("    \""+i+"\": {");
			pw.println("      \"left\": "+node.get(i).first() + ",");
			pw.println("      \"top\": "+node.get(i).second() + ",");
			if(score != null)
				if(score.containsKey(i))
					pw.println("      \"score\": "+String.format("%.2f",score.get(i))+",");
			pw.println("      \"children\": [");
			for(Pair<Integer,Integer> pairII : edge){
				if(pairII.second() == i)
					pw.println("        "+pairII.first()+",");
			}
			pw.println("      ],");
			pw.println("      \"parents\": [");
			for(Pair<Integer,Integer> pairII : edge){
				if(pairII.first() == i)
					pw.println("        "+pairII.second()+",");
			}
			pw.println("      ],");
			pw.println("      \"intent\": [");
				if(attribute.containsKey(i))
					for(String s : attribute.get(i))
						pw.println("        \""+s+"\",");
			pw.println("      ],");
			pw.println("      \"extent\": [");
				if(object.containsKey(i))
					for(String s : object.get(i))
						pw.println("        \""+s+"\",");
			pw.println("      ],");
			pw.println("    },");
		}
		pw.println("  },");
		pw.println("  \"objects\": {");
		for(int i=1; i<=conceptMax; i++){
			if(object.containsKey(i))
				for(String s : object.get(i))
					pw.println("    \""+s+"\": "+i+",");
		}
		pw.println("  },");
		pw.println("  \"attributes\": {");
		for(int i=1; i<=conceptMax; i++){
			if(attribute.containsKey(i))
				for(String s : attribute.get(i))
					pw.println("    \""+s+"\": "+i+",");
		}
		pw.println("  },");
		pw.println("  \"relations\": [");
		for(Pair<Integer, Integer> pairII : edge){
			pw.println("    [");
			pw.println("      " + pairII.first()+",");
			pw.println("      " + pairII.second());
			pw.println("    ],");
		}
		pw.println("  ]");
		pw.println("}");
	}
	
	public void equalFeature(int featureNum){
		for(int i=0; i<=featureNum; i++){
			int iCon = getFeatureConcept(i, attribute, conceptMax);
			if(iCon == -1)
				continue;
			ConcurrentSkipListSet<Integer> iSuper = new ConcurrentSkipListSet<Integer>();
			ConcurrentSkipListSet<Integer> iSub = new ConcurrentSkipListSet<Integer>();
			getAllSuperConcept(iCon, edge, iSuper);
			getAllSubConcept(iCon, edge, iSub);
			for(int j=i+1; j<=featureNum; j++){
				int jCon = getFeatureConcept(j, attribute, conceptMax);
				if(jCon == -1)
					continue;
				ConcurrentSkipListSet<Integer> jSuper = new ConcurrentSkipListSet<Integer>();
				ConcurrentSkipListSet<Integer> jSub = new ConcurrentSkipListSet<Integer>();
				getAllSuperConcept(jCon, edge, jSuper);
				getAllSubConcept(jCon, edge, jSub);
				if(iSuper.equals(jSuper) && iSub.equals(jSub))
					System.out.println("feature:"+i+"---feature:"+j);
			}
		}
	}
	
	public static void main(String[] args) {
		ReadChangeWriter wocc = new ReadChangeWriter("resource/concept-lattice-edit.txt", "resource/lattice-scoreF8.json", "resource/dependencies_in_source.csv");
		try {
			wocc.init();
			wocc.read();
			wocc.change();
			wocc.writeJson();
//			wocc.equalFeature(10);
			wocc.fina();
		} catch(IOException e) {
			System.out.println(e);
		} catch(FontFormatException e) {
			System.out.println(e);
		}

	}

}
