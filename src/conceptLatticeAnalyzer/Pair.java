package conceptLatticeAnalyzer;

public class Pair<T1, T2> {
	private T1 x;
	private T2 y;
	
	public Pair(T1 x, T2 y) {
		this.x = x;
		this.y = y;
	}
	
	public T1 first() {
		return x;
	}
	
	public T2 second() {
		return y;
	}
	
	public void setF(T1 x) {
		this.x = x;
	}
	
	public void setS(T2 y){
		this.y = y;
	}
	
	public String toString(){
		return x+", "+y;
	}
	
}
