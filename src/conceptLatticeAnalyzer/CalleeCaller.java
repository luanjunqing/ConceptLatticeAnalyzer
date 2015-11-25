package conceptLatticeAnalyzer;

import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CalleeCaller {
	private String path;
	private File file;
	private BufferedReader br;
	private HashMap<String, ArrayList<String>> calleeCallerTable;
	private HashMap<String, ArrayList<String>> callerCalleeTable;
	
	public CalleeCaller(String path) {
		this.path = path;
		calleeCallerTable = new HashMap<String, ArrayList<String>>();
		callerCalleeTable = new HashMap<String, ArrayList<String>>();
	}
	
	public void init() throws FileNotFoundException {
		file = new File(path);
		br = new BufferedReader(new FileReader(file));
	}
	
	public void fina() throws IOException {
		br.close();
	}
	
	public void makeTable() throws IOException, FontFormatException{
		String s;
		while((s = br.readLine()) != null) {
			String[] sList = s.split("\",\"",0);
			if(sList.length != 5) {
				System.out.println("Format Error in CalleeCaller");
				throw new FontFormatException(s);
			}
			sList[0] = sList[0].substring(1);
			//initialize
			if(!calleeCallerTable.containsKey(sList[0]))
				calleeCallerTable.put(sList[0], new ArrayList<String>());
			ArrayList<String> temp = calleeCallerTable.get(sList[0]);
			temp.add(sList[2]);
			calleeCallerTable.put(sList[0], temp);

			if(!callerCalleeTable.containsKey(sList[2]))
				callerCalleeTable.put(sList[2], new ArrayList<String>());
			temp = callerCalleeTable.get(sList[2]);
			temp.add(sList[0]);
			callerCalleeTable.put(sList[2], temp);
		}
	}
	
	public Boolean isCalleeCaller(String callee, String caller) {
		if(!calleeCallerTable.containsKey(callee))
			return false;
		ArrayList<String> temp = calleeCallerTable.get(callee);
		for(String s : temp)
			if(caller.equals(s))
				return true;
		return false;
	}

	public Boolean isCallerCallee(String caller, String callee) {
		if(!callerCalleeTable.containsKey(caller))
			return false;
		ArrayList<String> temp = callerCalleeTable.get(caller);
		for(String s : temp)
			if(callee.equals(s))
				return true;
		return false;
	}

}
