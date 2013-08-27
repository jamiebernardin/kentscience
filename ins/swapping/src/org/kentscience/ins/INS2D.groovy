package org.kentscience.ins

import groovy.lang.Closure;

import java.util.List;

class INS2D {
	
	static main(args) {
		Closure potential = { x ->
			double x2 = x*x
			(x2-1)*(x2-1)
		}
		INS2D ins = new INS2D(N:6E5, potential:potential, thermalProperties:[{x->x}, potential ])
		ins.tempEnv1 = new INSTempEnv(T:0.05)
		ins.tempEnv2 = new INSTempEnv(T:0.2)
		ins.config1 = new INSConfig(potential:potential, xCurrent:1.2)
		ins.config2 = new INSConfig(potential:potential, xCurrent:1.2)
		long startTime = System.currentTimeMillis()
		ins.run()
		println ins.averages()
		println "time: ${System.currentTimeMillis() - startTime}"
	}
	
	List<Closure> thermalProperties = []
	int warmUpCycles = 1000
	List<Double> runningPropertySums1 = []
	List<Double> runningPropertySums2 = []
	
	int N
	
	Closure potential
	Closure density = { V, T -> Math.exp(-V/T)}
	
	INSConfig config1
	INSConfig config2
	
	INSTempEnv tempEnv1
	INSTempEnv tempEnv2
	
	double rho1
	double rho2
	
	void init() {
		(1..thermalProperties.size()).each{ 
			runningPropertySums1 << 0 
			runningPropertySums2 << 0
		}
		config1.init()
		config2.init()	
	} 
	
	List averages() {
		List averages = []
		runningPropertySums1.each {
			averages << it/(N-warmUpCycles)
		}
		runningPropertySums2.each {
			averages << it/(N-warmUpCycles)
		}
		averages
	}
	
	void calcRho() {
//		double r1 = density(config1.potentialValueCurrent, tempEnv1.T) * density(config2.potentialValueCurrent, tempEnv2.T)
//		double r2 = density(config1.potentialValueCurrent, tempEnv2.T) * density(config2.potentialValueCurrent, tempEnv1.T)
		double r1 = Math.exp(-(config1.potentialValueCurrent/tempEnv1.T+config2.potentialValueCurrent/tempEnv2.T))
		double r2 = Math.exp(-(config1.potentialValueCurrent/tempEnv2.T+config2.potentialValueCurrent/tempEnv1.T))
		rho1 = r1/(r1+r2)
		rho2 = r2/(r1+r2)
	}
	
	int selectIndex() {
		 (Math.random() < rho1) ? 1 : 2
	}
	
	void updatePropertySums() {
		double val1, val2
		thermalProperties.eachWithIndex { f,i ->
			if (f == potential) {
				val1 = config1.potentialValueCurrent
				val2 = config2.potentialValueCurrent
			} else {
				val1 = f(config1.xCurrent)
				val2 = f(config2.xCurrent)
			}		
			runningPropertySums1[i] += rho1*val1 + rho2*val2
			runningPropertySums2[i] += rho2*val1 + rho1*val2
		}
	}
	
	void run() {
		init()
		calcRho()
		(0..N).each { mvIndex ->
			if (selectIndex() == 1) {
				config1.tempEnv = tempEnv1
				config2.tempEnv = tempEnv2
			} else {
				config1.tempEnv = tempEnv2
				config2.tempEnv = tempEnv1
			}
			config1.move()
			config2.move()
			calcRho()
			if (mvIndex > warmUpCycles) {
				updatePropertySums()
			}
			if ((mvIndex==1E4) || (mvIndex==1E5) || (mvIndex==2E4) || (mvIndex==3E5)) {
				println "rho1: $rho1, rho2: $rho2"
			}
		}
	}
	

}
