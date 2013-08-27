package org.kentscience.ec3

class SA {
	double temp1
	double rate
	int iters
	EC3Config bitConfig
	SA(EC3Config ec3, int iterations, double T1, double rate) {
		bitConfig = ec3
		this.iters = iterations
		this.temp1 = T1
		this.rate = rate
	}
	void run() {
		long start = System.currentTimeMillis()
		double scale = rate/iters
		(1..iters).each { iter -> 
			double temp = temp1*Math.exp(-scale*iter)
			bitConfig.move(temp)
			if (iter%10000 == 0) println "temp: $temp, error: ${bitConfig.val()} time: ${(System.currentTimeMillis()-start)/1000}"
		}
	}
	int val() { bitConfig.val() }
	static void main(args) {
		int total = 0
		[0.7].each { frac->
			(3..3).each { i ->
				String fname = './clauses/'+frac+'/'+i+'.txt'
				EC3Config e = EC3Config.read(fname)
				SA sa = new SA(e.clone(), (int) 1E5, 1.0, 3)	
				sa.run()
				println sa.val()
				total += sa.val()
			}
		}
		println "total: $total"
	}
}
