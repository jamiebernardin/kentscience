package org.kentscience.util

import java.util.List;

class Bit {
	boolean x
	Bit(boolean bool) { x = bool }
	Bit() { x = (Math.random() < 0.5) }
	int val() {
		x ? 1 : 0
	}
	double multiply(Bit that) {
		(this.x == that.x) ? 1 : 0
	}
	Bit flip() {
		x = (!x)
		return this
	}
	String toString() {
		x
	}
	static FALSE = new Bit(false)
	static TRUE = new Bit(true)
	static void main(args) {
		Bit bit = new Bit(true)
		println bit
		println bit.val()
		bit.flip()
		println bit
		println bit.val()
	}
}
