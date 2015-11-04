package tarble;

public class Pair<S, T> {
	private S first;
	private T second;
	
	public Pair(S first, T second) {
		this.first = first;
		this.second = second;
	}
	
	public S getFirst() {
		return first;
	}
	
	public T getSecond() {
		return second;
	}
	
	public void setFirst(S value) {
		first = value;
	}
	
	public void setSecond(T value) {
		second = value;
	}
	
	
	// returns true if either the first element of both pairs are equal and the second
	// element of both pairs are equal, or the first of one is equal to the second of
	// the other for both pairs.
	@Override
	public boolean equals(Object otherObj) {
		if (otherObj instanceof Pair) {
			Pair other = (Pair) otherObj;
			return other.getFirst().equals(first) && other.getSecond().equals(second);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return first.hashCode() - second.hashCode();
	}
	
	@Override
	public String toString() {
		return "Pair(" + first.toString() + "," + second.toString() + ")";
	}
}