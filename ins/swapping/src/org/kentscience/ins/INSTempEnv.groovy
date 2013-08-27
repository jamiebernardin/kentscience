package org.kentscience.ins

import groovy.lang.Closure;

import java.util.List;

class INSTempEnv {
	double dxMin = 0.001
	double dxMax = 2.00
	double acceptCurrent = 0
	double acceptPrevious = 0
	double T
	double dxCurrent = 0.1
	double dxPrevious = 0

	void adjustDX(int adjustInterval) {
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
	
	void init() {
		acceptPrevious = acceptCurrent
		dxPrevious = dxCurrent
		dxCurrent *= 1.1
		acceptCurrent = 0
	}

}
