package bitflow4j.misc;

public class Pair<A, B>{

	private A value1;
	private B value2;
	
	public Pair(A value1, B value2) {
		this.value1 = value1;
		this.value2 = value2;
	}

	public A getValue1() {
		return value1;
	}

	public void setValue1(A value1) {
		this.value1 = value1;
	}

	public B getValue2() {
		return value2;
	}

	public void setValue2(B value2) {
		this.value2 = value2;
	}
}
