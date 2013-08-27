package org.kentscience.qa
import org.kentscience.util.Bit

class QAConfig {
	Bit[][] spins
	Bit flipBit
	int N, M
	void flip() {
		flipBit = spins[(Math.random() * M) as Integer][(Math.random() * N) as Integer].flip()
	}
	void unflip() {
		flipBit?.flip()
	}
	QAConfig(int M, int N) {
		this.M = M
		this.N = N
		spins = new Bit[M][N]
		(0..N-1).each { i ->
			spins[0][i] = new Bit(true)			
		}
		(1..M-1).each { j ->
			(0..N-1).each{ i ->
				spins[j][i] = new Bit(spins[0][i].x)
			}
		}
	}
	static main(args) {
		QAConfig config = new QAConfig(3,2)
		println config.spins
		config.flip()
		println config.spins
		config.unflip()
		config.flip()
		println config.spins
	}

}
