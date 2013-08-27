package com.kentscience.ising

import org.kentscience.util.MinimalDynamicChart


class Lattice2D {
	int n, m
	Spin[][] s
	Coupling coupling
	double totalEnergy = 0
	double magnetization = 0
	Lattice2D(int n, int m, Coupling coupling) {
		this.coupling = coupling
		this.n = n
		this.m = m
		this.s = new Spin[n][m]
		(0..s.size()-1).each { i ->
			(0..this.s[i].size()-1).each { j ->
				s[i][j] = new Spin(i,j)
			}
		}
		(0..s.size()-1).each { i ->
			(0..this.s[i].size()-1).each { j ->
				if (i!=j) {
					totalEnergy += calcEnergy(s[i][j])
					magnetization += s[i][j].value()
				}
			}
		}
	}
	double calcEnergy(Spin s) {
		double total = 0
		eachNeighbor(s, { neighbor ->
			total += coupling.eval(s,neighbor)*(s*neighbor)
		})
		total
	}
	void eachNeighbor(Spin spin, Closure c) {
		int i = spin.idx[0]
		int j = spin.idx[1]
		if (i == 0) {
			c(s[n-1][j])
		} else {
			c(s[i-1][j])
		}
		c(s[(i+1)%n][j])
		if (j == 0) {
			c(s[i][m-1])
		} else {
			c(s[i][j-1])
		}
		c(s[i][(j+1)%m])
	}
	Spin selectRandom() {
		s[(int) (n*Math.random())][(int) (m*Math.random())]
	}
	void eachSpin(Closure c) {
		(0..n-1).each{ i ->
			(0..m-1).each { j ->
				c(s[i][j])
			}
		}
	}
	static void main(def args) {
		def chart = new MinimalDynamicChart(2)
		int n = 32, m = 32
		int numSpins = n*m
		double T = 1
		int steps = 6000*numSpins
		def constant = { -1.0 }
		def random = { -1 + Math.random() }
		Coupling coupling = new Coupling(n,m,random)
		def lattice = new Lattice2D(n, m, coupling)
		println "initial energy: ${lattice.totalEnergy/numSpins}"
		int totalFlips = 0
		int positiveFlips = 0
		(1..steps).each { i ->
			Spin s = lattice.selectRandom()
			double delta = -2*lattice.calcEnergy(s)
			boolean flip = true
			if (delta > 0) {
				flip = Math.random() < Math.exp(-delta/T)
				if (flip) positiveFlips++
			}
			if (flip) {
				totalFlips++
				s.flip()
				lattice.totalEnergy += delta
				lattice.magnetization += 2*s.value()
			}	
			if (i%numSpins == 0) {
				chart.addPoints(i, [ lattice.totalEnergy/numSpins, lattice.magnetization/numSpins])
			}
			if (i%numSpins == 0) {
				println "SPS=${i/numSpins} E=${lattice.totalEnergy/numSpins} M=${lattice.magnetization/numSpins}"
			}
		}	
		println "positiveFlips: $positiveFlips, totalFlips: $totalFlips"
		println "Total energy per spin: ${lattice.totalEnergy/numSpins}"
		println "magnetization per spin = ${lattice.magnetization/numSpins}"
	}
}

