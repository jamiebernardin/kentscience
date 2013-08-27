package org.kentscience.ec3

import org.kentscience.util.Bit;
import org.kentscience.util.Clause;

class EC3Config {
	String toString() {
		StringBuffer buf = new StringBuffer()
		buf << N
		clauses.each { buf << it }
		buf.toString()
	}
    void write(String fname) {
		def file = new FileOutputStream(fname)
		file << N
		clauses.each{
			file << "\n"
			file << it
		}
	}
	EC3Config() {}
	EC3Config clone() {
		EC3Config that = new EC3Config()
		that.clauses = this.clauses
		that.N = N
		(1..N).each {
			that.bits << new Bit(false)
		}
		that.currentVal = that.val()
		that
	}
	static EC3Config read(String fname) {
		EC3Config e = new EC3Config()
		File file = new File(fname)
		def list = file.readLines()
		e.N = Integer.parseInt(list[0])
		(1..list.size()-1).each { idx ->
			def c = list[idx]
			StringTokenizer st = new StringTokenizer(c, ",")
			e.clauses << new Clause(Integer.parseInt(st.nextToken()), 
								Integer.parseInt(st.nextToken()), 
								Integer.parseInt(st.nextToken())
								)
		}
		(1..e.N).each {
			e.bits << new Bit(false)
		}
		e.currentVal = e.val()
		return e
	}
	EC3Config(int N, int M) {
		this.N = N
		(1..N).each {
			bits << new Bit(false)
		}
		(1..M).each {
			clauses << new Clause(N)
		}
		currentVal = val()
	}
	EC3Config(int N, List<Clause> clauses) {
		this.N = N
		(1..N).each { bits << new Bit() }
		currentVal = val()
	}
	int val() {
		int value = 0
		clauses.each { c ->
			value += c.val(bits)
		}
		value
	}
	void move(double T) {
		boolean accept = false
		flip()
		int futureVal = val()
		if (futureVal < currentVal) {
			accept = true
		} else {
			accept =  (Math.random() < Math.exp((currentVal-futureVal)/T))
		}
		if (!accept)  {
			unflip()
		} else {
			currentVal = futureVal
		}
	}
	void flip() {
		flipBit = bits[select(N)].flip()
	}
	void unflip() {
		flipBit?.flip()
	}
	int select(int n) { (Math.random() * n)  }
	int N
	List<Bit> bits = []
	List<Clause> clauses = []
	Bit flipBit
	int currentVal = 0
	static void main(args) {
		int bits = 1000
		String baseDir = './clauses/'
		String fname
		int clauses
		[0.5, 0.6, 0.7].each { frac->
			def file = new File(baseDir+frac)
			if (!file.exists()) {
				file.mkdir()
			}
			(1..10).each { i ->
				fname = baseDir+frac+'/'+i+'.txt'
				clauses = frac*bits
				EC3Config ec1 = new EC3Config(bits, clauses)
				ec1.write(fname)
			}
		}
	}
}
