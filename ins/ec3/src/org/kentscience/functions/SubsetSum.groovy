package org.kentscience.functions

import java.util.List

import org.kentscience.util.Bit;
import org.kentscience.util.GeneratingFunction;

class SubsetSum implements GeneratingFunction {

	List<Integer> coefficients
	int sum
	double factor
	SubsetSum(List<Integer> coefficients, int sum) {
		this.coefficients = coefficients
		this.sum = sum
		double min = 0, max = 0
		coefficients.each { n ->
			if (n>max) max = n
		}
		factor = max
	}
	@Override 
	public double calculate(Bit[] binVars) {
		double val = 0
		(0..binVars.length-1).each { i ->
			val += binVars[i].val()*coefficients[i]
		}
		(val-sum)*(val-sum) + hasOne(binVars)
	}
	
	private double hasOne(Bit[] binVars) {
		double val = 1
		int i = 0
		while (i < binVars.length && val != 0) {
			if (binVars[i].val() != 0) val = 0;
			i++
		}
		val*factor
	}

	static main(args) {
		SubsetSum ss = new SubsetSum([-7, -3, -2, 5, 8], 0)
		println ss.calculate([Bit.FALSE,Bit.TRUE,Bit.TRUE, Bit.TRUE, Bit.FALSE] as Bit[])
		println ss.calculate([Bit.FALSE,Bit.FALSE,Bit.FALSE, Bit.FALSE, Bit.FALSE] as Bit[])
	}

}
