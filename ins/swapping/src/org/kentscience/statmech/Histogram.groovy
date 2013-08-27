package org.kentscience.statmech

class Histogram {
	int buckets
	int minVal
	int maxVal
	double dx
	ArrayList<Double> values = []
	Histogram(int buckets, double minVal, double maxVal) {
		this.buckets = buckets
		this.minVal = minVal
		this.maxVal = maxVal
		dx = (maxVal-minVal)/buckets
		(0..buckets).each { values << 0 }
		println dx
	}
	void add(double val) {
		int i = Math.max(0,Math.min(values.size()-1, (val-minVal)/dx))
		values[i] = values[i] + 1
	}
	void printValues() {
		(0..values.size()-1).each { 
			println "x: ${dx*(it-buckets/2.0)}, h: ${values[it]}"
		}
	}
	static main(args) {
		def h = new Histogram(100, -1, 1)
		h.add(0.40001)
		h.add(-0.2)
		h.printValues()
	}

}
