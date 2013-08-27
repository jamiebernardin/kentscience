package org.kentscience.statmech

import org.kentscience.util.*

class ThermalAverage {

	double T
	Closure potential
	Closure density
	double minX = -5
	double maxX = 5
	int N = 200000
	
	ThermalAverage(Closure potential) {
		density = { x -> Math.exp(-potential(x)/T) }
	}
	
	double calculate(Closure property, double T) {
		this.T = T
		double z = Simpson.integrate(density, minX, maxX, N)
		Simpson.integrate({x -> property(x)*density(x)}, minX, maxX, N)/z	
	}

	static void main(String[] args) {
		def temps = [0.05, 0.2, 0.4]
		Closure potential = { x ->
			double x2 = x*x
			(x2-1)*(x2-1)
		}
		ThermalAverage average = new ThermalAverage(potential) 
		Closure location = { x-> x}
		temps.each { t ->
			println "T:$t, ${average.calculate(potential, t)}"
		}
	}
	
	
}


