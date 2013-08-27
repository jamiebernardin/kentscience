package com.kentscience.ising

class Spin {
	boolean up
	List<Integer> idx
	Spin(boolean b) { up = b }
	Spin() { up = (Math.random() < 0.5) }
	Spin(int i, int j) {
		idx = [i, j]
		up = (Math.random() < 0.5)
	}
	double value() {
		up ? 1.0 : -1.0
	}
	Spin flip() {
		up = (!up)
		return this
	}
	String toString() { 
		String str = up ? "UP":"DOWN" 
		if (idx) str += ", $idx"
		str
 	}	
	double multiply(Spin other) {
		(up == other.up) ? 1.0 : -1.0
	}
	static void main(args) {
		Spin spin1 = new Spin(1,2)
		Spin spin2 = new Spin()
		println spin1
		println spin1*spin2
		println 5%5
	}
}
