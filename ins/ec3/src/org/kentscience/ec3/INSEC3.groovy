package org.kentscience.ec3

import groovy.lang.Closure;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.kentscience.ins.*;
import org.kentscience.util.*;

class INSEC3 {
	static main(args) {
		long startTime = System.currentTimeMillis()
	//	def chart = new MinimalDynamicChart(1)
		int blockSize = 2
		def moves = 1E6
		def temperatures = [0.05, 0.3, 0.7]
		//(1..6).each { temperatures << 0.05*it }
		//println temperatures
		String fname = '/users/jamie/dev/kent/ec3/clauses/0.6/3.txt'
		EC3Config config = EC3Config.read(fname)
		INSEC3 pins = new INSEC3(config, moves, temperatures, blockSize)
	//	pins.log._rhos = Boolean.TRUE; pins.log._swap = Boolean.TRUE
		new Thread({pins.run()}).start()
		Thread.sleep(5000)
		while (true) {
			Thread.sleep(1000)
	//		chart.addPoints(pins.mvIndex, [pins.val()])
		}
//		println "time: ${System.currentTimeMillis() - startTime}"
	}

	List logChannels = ["_rhos","_sums","_swap","_block"]
	
	MersenneTwister random = new MersenneTwister()
	Map<String, Boolean> log = [:]
	List chain
	int ITERS
	int mvIndex = 0
	Closure potential
	List<EC3Config> configs =  []
	List<Double> temps

	public INSEC3(EC3Config config, BigDecimal ITERS, List<Double> temps, int blockSize) {
		this.temps = temps
		this.ITERS = (int) ITERS
		temps.each { t ->
			def c = config.clone()
			configs << c
		}
		chain = Util.generateChain(blockSize, temps.size())
		logChannels.each { log.put(it,false) }
	}
	

	public void run() {
		long start = System.currentTimeMillis()
		(0..ITERS).each {
			mvIndex++
			processOuterBlock(chain[0])
			(1..chain.size()-2).each { i->
				processRegularBlock(chain[i])
			}
			processOuterBlock(chain[chain.size()-1])
			if ((mvIndex)%1000==0) {
				configs.each { c ->
					println c.currentVal + ":  ${(System.currentTimeMillis()-start)/1000}"
				}
			}
		}
	}
	
	public double val() {
		configs[0].currentVal
	}

	private void processRegularBlock(List block, boolean swap = true) {
		double[] rhos = calcRhos(block)
		def tuple = block[selectIndex(rhos)]
		if (log._block) {
			println "Block: $block"
			println "rhos: $rhos"
			println "tuple selected: $tuple"
		}
		def threads = []
		(0..tuple.size()-1).each { i ->
			if (log._block) println "X=${tuple.minIdx()+i+1} move with T=${tuple.getIdx(i)+1}"
			EC3Config config = configs[tuple.minIdx()+i]
			config.move(temps[tuple.getIdx(i)])
		}
		rhos = calcRhos(block)
		if (swap) {
			def swapTuple = block[selectIndex(rhos)]
			swapConfigs(swapTuple)
		}
	}

	private void processOuterBlock(List block) {
		if (block.size()==1) {
			int idx = block[0].getIdx(0)
			configs[idx].move(temps[idx])
		} else {
			processRegularBlock(block, true)
		}
	}

	double[]  calcRhos(List block) {
		double[] rs = new double[block.size()]
		double[] rhos = new double[block.size()]
		double total = 0
		int offset = block[0].minIdx()
		block.eachWithIndex { tuple, i ->
			double texp = 0
			(0..tuple.size()-1).each { k ->
				texp += configs[k+offset].currentVal/temps[tuple.getIdx(k)]
			}
			rs[i] = Math.exp(-texp)
			total += rs[i]
		}
		(0..rhos.length-1).each { i ->
			rhos[i] = rs[i]/total
		}
		if (log._rhos) rhos.eachWithIndex { rho, i -> println "${block[i]}:$rho" }
		rhos
	}
	void  swapConfigs(Tuple tuple) {
		//	return
		int offset = tuple.minIdx()
		if (log._swap) println "swap tuple: $tuple"
	//	if (log._swap) configs.eachWithIndex { config, i ->  print "(${i+1}:${new Double(config.xCurrent).trunc(3)})" }
		List tempConfigs = []
		(offset..tuple.size()+offset-1).each { tempConfigs << configs[it] }
		(0..tuple.size()-1).each { i ->
		//    configs[offset+i] = tempConfigs[tuple.getIdx(i)-offset]
			configs[tuple.getIdx(i)] = tempConfigs[i]
		}
		if (log._swap) {
	//		println ''
	//		configs.eachWithIndex { config, i ->  print "(${i+1}:${new Double(config.xCurrent).trunc(3)})" }
		}
	}
	private int selectIndex(double[] rhos) {
		double select = random.nextDouble()
		int i = 0
		double total = rhos[0]
		while (total < select) {
			total += rhos[++i]
		}
		return i
	}

}
