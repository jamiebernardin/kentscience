package org.kentscience.pt

class ParallelTempering {
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
			T:0.2,
			potential:potential,
			thermalProperties:[{x->x}, potential ]
		)
		Configuration config2 = new Configuration(
			xCurrent:1.2,
			dxCurrent:0.1,
			T:0.05,
			potential:potential,
			thermalProperties:[{x->x}, potential ]
		)
		int N = 1E6
		int swapInterval = 1
		config1.init()
		config2.init()
		long startTime = System.currentTimeMillis()
		(0..N).each {
			config1.move()
			config2.move()
			if ((it%swapInterval) == 0) {
				swap(config1, config2)
			}
		}
		println config1.averages() 
		println config2.averages() 
		println "time: ${System.currentTimeMillis() - startTime}"
	}
	public static void swap(config1, config2) {
		config1.xCurrent = config2.xPrevious
		config2.xCurrent = config1.xPrevious
		if (tryAccept(config1,config2)	) {
			config1.xPrevious = config1.xCurrent
			config1.potentialValuePrevious = config1.potentialValueCurrent
			config2.xPrevious = config2.xCurrent
			config2.potentialValuePrevious = config2.potentialValueCurrent
		} else {
			config1.xCurrent = config1.xPrevious
			config1.potentialValueCurrent = config1.potentialValuePrevious
			config2.xCurrent = config2.xPrevious
			config2.potentialValueCurrent = config2.potentialValuePrevious
		}
	}
	public static boolean tryAccept(Configuration config1, Configuration config2) {
		boolean accept = false
		config1.potentialValueCurrent = config2.potentialValuePrevious
		config2.potentialValueCurrent = config1.potentialValuePrevious
		double dnew = config1.potentialValuePrevious/config2.T + config2.potentialValuePrevious/config1.T
		double dold = config1.potentialValuePrevious/config1.T + config2.potentialValuePrevious/config2.T
		if (dnew < dold) {
			accept = true
		} else {
			double densityValue = Math.exp((dold-dnew))
			accept =  (Math.random() < densityValue)
	//		println "accept $accept, densityValue:$densityValue"
		}
		accept
	}
}
