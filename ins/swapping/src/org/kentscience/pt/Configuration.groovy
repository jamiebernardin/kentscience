package org.kentscience.pt

import org.kentscience.statmech.Histogram

class Configuration {
	public static void main(String[] args) {
		File outfile = new File("mc.out")
		outfile.delete()
		Closure potential = { x ->
			double x2 = x*x
			(x2-1)*(x2-1)
		}
		Configuration config1 = new Configuration(
			xCurrent:1.2,
			dxCurrent:0.1,
			T:0.4,
			potential:potential,
			thermalProperties:[{x->x}, potential ]
		)
		Histogram h = new Histogram(100,-2,2)
	//	config1.printPotential()
		int N = 1E7
		config1.init()
		(0..N).each {
			config1.move()
			h.add(config1.xCurrent)
	//		outfile << config1.toString() << "\n"
		}
		def averages = config1.averages()
		println averages
	//	h.printValues()
	}
	double dxMin = 0.001
	double dxMax = 2.00
	double xCurrent
	double xPrevious
	double acceptCurrent = 0
	double acceptPrevious = 0
	int adjustInterval = 1000
	double T
	double dxCurrent
	double dxPrevious = 0
	Closure potential
	List<Closure> thermalProperties = []
	int warmUpCycles = 1000
	List<Double> runningPropertySums = []
	int mvIndex = 0
	double potentialValueCurrent = 0
	double potentialValuePrevious = 0
	String toString() {
		"xCurrent: $xCurrent, xPrevious $xPrevious, potentialValue:$potentialValueCurrent, lastPotentialValue: $potentialValuePrevious"
	}
	void printPotential() {
		int N = 100
		(-N..N).each {
			double x = 4.0*it/N
			println "x:$x , V: ${potential(x)}"
		}
	}
	void init() {
		potentialValuePrevious = potential(xCurrent)
		xPrevious = xCurrent
		(1..thermalProperties.size()).each{ runningPropertySums << 0 }	
	}
	void move() {
		mvIndex++
		if (mvIndex==adjustInterval) {
			acceptPrevious = acceptCurrent
			dxPrevious = dxCurrent
			dxCurrent *= 1.1
			acceptCurrent = 0
		}
		else if ((mvIndex%adjustInterval) == 0) {
			adjustDX()
		}
		xCurrent += (Math.random()-0.5)*dxCurrent
		if (tryAccept()	) {
			xPrevious = xCurrent
			potentialValuePrevious = potentialValueCurrent
			acceptCurrent += 1.0
		} else {
			xCurrent = xPrevious
			potentialValueCurrent = potentialValuePrevious
		}
		addValueToSums()
	}
	List averages() {
		List averages = []
		runningPropertySums.each {
			averages << it/(mvIndex-warmUpCycles)
		}
		averages
	}
	private boolean tryAccept() {
		boolean accept = false
		potentialValueCurrent = potential(xCurrent)
		if (potentialValueCurrent < potentialValuePrevious) {
			accept = true
		} else {
			double densityValue = Math.exp((potentialValuePrevious-potentialValueCurrent)/T)
			accept =  (Math.random() < densityValue) 
	//		println "xCurrent: $xCurrent, accept $accept, densityValue:$densityValue, dxCurrent: $dxCurrent"
		}
		accept
	}
	private void addValueToSums() {
		if (mvIndex>warmUpCycles) {
			thermalProperties.eachWithIndex { f,i ->
				runningPropertySums[i] += f(xCurrent)
			}
		}
	}
	private void adjustDX() {
	//	println "dxCurrent=$dxCurrent, dxPrevious;$dxPrevious, acceptCurrent: $acceptCurrent, acceptPrevious:$acceptPrevious"
		double temp = dxCurrent
		if (acceptCurrent == acceptPrevious) {
			dxCurrent *= (1.1)
		} else {
			double slope = (dxCurrent - dxPrevious)*adjustInterval/(acceptCurrent-acceptPrevious)
			double dx = dxCurrent+(0.5-acceptPrevious/adjustInterval)*slope/2.0
			dxCurrent = Math.max(dxMin, Math.min(dxMax, dx))
		}
		dxPrevious = temp
		acceptPrevious = acceptCurrent
		acceptCurrent = 0
	}


}
