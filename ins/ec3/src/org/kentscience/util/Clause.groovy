package org.kentscience.util


class Clause {
	int x1,x2,x3
	int size
	int val(List<Bit> bits) {
		(bits[x1].val()+bits[x2].val()+bits[x3].val()-1)**2
	}
	Clause(x1, x2, x3) {
		this.x1 = x1; this.x2 = x2; this.x3 = x3
	}
	Clause( int N) {
		size = N
		x1 = select()
		x2 = select()
		while (x1 == x2) x2 = select()
		x3 = select()
		while (x3 == x2 || x3 == x1) x3 = select()
	}
	String toString() {
		"$x1,$x2,$x3"
	}
	int select() { (Math.random() * size)  }
	static void main(args) {
		int N = 64
		List<Bit> bits = []
		(1..N).each { bits << new Bit() }
		(1..10).each { 
			def c = new Clause(N)
			println c
			println c.val(bits)
		}	
	}
}


