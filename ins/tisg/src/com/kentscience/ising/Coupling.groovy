package com.kentscience.ising;

public class Coupling {
	int n, m
	Closure generator
	Map<String,Double> J = new HashMap<String,Double>()
	Coupling(int n, int m, Closure g) {
		this.n = n 
		this.m = m
		this.generator = g
	}
	private double eval(int i, int j) {
		if (i==j) return 0
		String key = createKey(i, j)
		def val = J[key]
		if (!val) {
			val = generator()
			String otherKey = createKey(j,i)
			J[key] = val
			J[otherKey] = val
		}
		val
	}
	double eval(Spin s1, Spin s2) {
		int i = s1.idx[0]*n+s1.idx[1]
		int j = s2.idx[0]*n+s2.idx[1]
		eval(i,j)
	}
	private String createKey(int i, int j) {
		"$i-$j"
	}
	public static void main(String[] args) {
		int n = 128, m = 128;
		Coupling c = new Coupling(n, m,{ Math.random() - 0.5 })
		Spin s1 = new Spin(0, 1)
		Spin s2 = new Spin(0, 2)
		Spin s3 = new Spin(4, 5)
		println c(s1, s2)
		println c(s2, s1)
		println c(s2, s3)
		println c(s3, s2)
		println c(s3,s3)
	}

}
