package org.kentscience.ins

import org.kentscience.util.MersenneTwister;


class INSConfig {

	INSTempEnv tempEnv
	double xCurrent
	double xPrevious
	int adjustInterval = 1000
	Closure potential
	int mvIndex = 0
	double potentialValueCurrent = 0
	double potentialValuePrevious = 0
	MersenneTwister random = new MersenneTwister()
	
	void init() {
		xCurrent = random.nextDouble()
		potentialValuePrevious = potential(xCurrent)
		potentialValueCurrent = potentialValuePrevious
		xPrevious = xCurrent
	}
	
	String toString() {
		"xCurrent:$xCurrent, potentialValueCurrent:$potentialValueCurrent, temp:$tempEnv.T"
	}
	
	void move() {
		mvIndex++
		if (mvIndex==adjustInterval) {
			tempEnv.init()
		}
		else if ((mvIndex%adjustInterval) == 0) {
			tempEnv.adjustDX(adjustInterval)
		}
		xCurrent += (random.nextDouble()-0.5)*tempEnv.dxCurrent
		if (tryAccept()	) {
			xPrevious = xCurrent
			potentialValuePrevious = potentialValueCurrent
			tempEnv.acceptCurrent += 1.0
		} else {
			xCurrent = xPrevious
			potentialValueCurrent = potentialValuePrevious
		}
	}
	
	private boolean tryAccept() {
		boolean accept = false
		potentialValueCurrent = potential(xCurrent)
		if (potentialValueCurrent < potentialValuePrevious) {
			accept = true
		} else {
			double densityValue = Math.exp((potentialValuePrevious-potentialValueCurrent)/tempEnv.T)
			accept =  (random.nextDouble() < densityValue)
	//		println "xCurrent: $xCurrent, accept $accept, densityValue:$densityValue, dxCurrent: $dxCurrent"
		}
		accept
	}
	
	static main(args) {
	
	}

}
