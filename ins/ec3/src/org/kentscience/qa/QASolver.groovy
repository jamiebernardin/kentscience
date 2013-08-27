package org.kentscience.qa

import org.kentscience.functions.SubsetSum
import org.kentscience.util.*

class QASolver {
	GeneratingFunction generator
	QAConfig config
	int N, M
	int iterations
	QASolver(GeneratingFunction generator, int M, int N) {
		this.generator = generator
		this.M = M
		this.N = N
		config = new QAConfig(M, N)
	}
	void solve(int iterations) {
	//	double val = driver()
	//	double val = target()
		(0..iterations).each { i ->		
			double b = Math.pow(i/iterations, 2); 
			double a = 1-b
		    double val = b*target() + a*driver()
			config.flip()
			double newVal = b*target() + a*driver()
			//	double newVal = target()
			if (i % 1000 == 0) {
				println "a:$a, b:$b"
				println "driver: ${driver()}"
				println "target: ${target()}"
				println "oldVal: $val, newVal: $newVal"
			}
			if (newVal < val) { 
				val = newVal
			} else {
				config.unflip()
			}
		}
		
	}
	double target() {
		double val = 0
		(0..M-1).each { m ->
			val += generator.calculate(config.spins[m])
		}	
		val/(M*N)	
	}
	
	double driver() {
		double val = 0
		(0..N-1).each { i ->
			val += config.spins[0][i]*config.spins[M-1][i]
			(0..M-2).each { m -> 
				val += config.spins[m][i]*config.spins[m+1][i]
			}
		}
		val 
	}
	
	void test() {
		println "driver: ${driver()}"
		println "target: ${target()}"
		config.flip()
		println "driver: ${driver()}"
		println "target: ${target()}"
	}
	
	static main(args) {
		
		long start = System.currentTimeMillis()
//		def problem = new SubsetSum([-7, -3, -2, 5, 8], 0)
		def problem = new SubsetSum([267, 493, 869, 961, 1000, 1153, 1246, 1598, 1766, 1922], 5842)
		def solver = new QASolver(problem, 20, 10)
		def s = solver.config.spins
		(0..s.length-1).each { i ->
			println "${s[i]} = ${problem.calculate(s[i])}"
		}
	//	solver.test()
		solver.solve(1000000)
		
		(0..s.length-1).each { i ->
			println "${s[i]} = ${problem.calculate(s[i])}"
		}
		println "time: " + (System.currentTimeMillis() -start)/1000
	}

}
