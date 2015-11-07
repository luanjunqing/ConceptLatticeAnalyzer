package conceptLatticeAnalyzer;

public class ConceptTools {
	public static LineType checkLineType(String s) {
		if(s.length() > 4 && "Node:".equals(s.substring(0, 5)))
			return LineType.Node;
		else if(s.length() > 4 && "Edge:".equals(s.substring(0, 5)))
			return LineType.Edge;
		else if(s.length() > 6 && "Object:".equals(s.substring(0, 7)))
			return LineType.Object;
		else if(s.length()>9 && "Attribute:".equals(s.substring(0, 10)))
			return LineType.Attribute;
		else if("EOF".equals(s))
			return LineType.EOF;
		return LineType.Other;
}
	public static int makeConceptNum(String s) {
		String temp;
		if(s.indexOf(',') == -1)
			return -1;
		temp = s.substring(0, s.indexOf(','));		
		switch(checkLineType(s)){
		case Node:
			return Integer.parseInt(temp.substring(6));
		case Object:
			return Integer.parseInt(temp.substring(8));
		case Attribute:
			return Integer.parseInt(temp.substring(11));
		default:
			return -1;
		}
	}
	
	public static String makeConceptAttribute(String s) {
		if(checkLineType(s) != LineType.Attribute)
			return null;
		return s.substring(s.indexOf(',')+2);
	}
	
	public static String makeConceptObject(String s) {
		if(checkLineType(s) != LineType.Object)
			return null;
		return s.substring(s.indexOf(',')+2);
	}
	
	public static Pair<Double, Double> makeConceptNode(String s) {
		if(checkLineType(s) != LineType.Node)
			return null;
		String[] sList = s.split(",");
		return new Pair<Double, Double>(Double.parseDouble(sList[1]), Double.parseDouble(sList[2]));
	}
	
	public static Pair<Integer, Integer> makeConceptEdge(String s) {
		if(checkLineType(s) != LineType.Edge)
			return null;
		int first = Integer.parseInt(s.substring(6, s.indexOf(',')));
		return new Pair<Integer, Integer>(first, Integer.parseInt(s.substring(s.indexOf(',')+2)));
	}
}
