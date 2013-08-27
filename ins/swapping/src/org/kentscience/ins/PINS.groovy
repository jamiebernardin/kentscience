package org.kentscience.ins


import java.util.List;

import org.kentscience.util.MersenneTwister;
import org.kentscience.util.MinimalDynamicChart

class PINS {

	static main(args) {
		long startTime = System.currentTimeMillis()
		Closure potential = { x ->
			double x2 = x*x
			(x2-1)*(x2-1)
		}
		Closure location = { x -> x }
		def chart = new MinimalDynamicChart(1)
		def thermalProperties = [ potential]
		int blockSize = 2
		def moves = 1E6
		def temperatures = [0.05, 0.1, 0.2, 0.3, 0.4]
		//(1..6).each { temperatures << 0.05*it }
		//println temperatures
		PINS pins = new PINS(moves, temperatures, blockSize, potential, thermalProperties)
	//	pins.log._rhos = Boolean.TRUE; pins.log._swap = Boolean.TRUE
		new Thread({pins.run()}).start()
		while (true) {
			Thread.sleep(1000)
			if (pins.mvIndex > pins.warmUpCycles) {
				def allPoints = pins.averages()[0]
				chart.addPoints(pins.mvIndex, [-(allPoints[0]-0.0255249)/0.0255249])
			}
		}
//		println "time: ${System.currentTimeMillis() - startTime}"
	}

	List logChannels = ["_rhos","_sums","_swap","_block"]
	
	MersenneTwister random = new MersenneTwister()
	Map<String, Boolean> log = [:]
	List chain
	List<Closure> thermalProperties = []
	int warmUpCycles = 50000
	Map<Integer, List<Double>> runningPropertySums = [:]

	int N
	int mvIndex = 0
	Closure potential
	List<INSConfig> configs =  []
	List<Double> temps
	List<INSTempEnv> tempEnvs = []

	public PINS(BigDecimal N, List<Double> temps, int blockSize, Closure potential, def thermalProperties) {
		this.temps = temps
		this.potential = potential
		this.thermalProperties = thermalProperties
		this.N = (int) N
		temps.each { t ->
			def c = new INSConfig(potential:potential)
			c.init()
			configs << c
			def te = new INSTempEnv(T:t)
			te.init()
			tempEnvs << te
			c.tempEnv = te
		}
		chain = Util.generateChain(blockSize, temps.size())
		logChannels.each { log.put(it,false) }
		log._block = true
	}

	public void run() {
		init()
		(0..N).each {
			mvIndex++
			processOuterBlock(chain[0])
			(1..chain.size()-2).each { i->
				processRegularBlock(chain[i])
			}
			processOuterBlock(chain[chain.size()-1])
			if ((mvIndex)%100000==0) {
				println averages()
			}
		}
	}

	List averages() {
		def averages = []
		thermalProperties.eachWithIndex { f,i ->
			def l = []
			(0..temps.size()-1).each { k->
				l << runningPropertySums[k][i]/(2*(mvIndex-warmUpCycles))
			}
			averages << l
		}
		return averages
	}

	private void init() {
		(0..temps.size()-1).each { i->
			runningPropertySums[i] = []
			(1..thermalProperties.size()).each{ runningPropertySums[i]  << 0.0 }
		}
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
			def t = new Thread({
				if (log._block) println "X=${tuple.minIdx()+i+1} move with T=${tuple.getIdx(i)+1}"
				INSConfig config = configs[tuple.minIdx()+i]
				config.tempEnv = tempEnvs[tuple.getIdx(i)]
				config.move()
			})
			t.start()
			threads << t
		}
		threads.each { t -> t.join() }
		rhos = calcRhos(block)
		updatePropertySums(block, rhos)
		if (swap) {
			def swapTuple = block[selectIndex(rhos)]
			swapConfigs(swapTuple)
		}
	}

	private void processOuterBlock(List block) {
		if (block.size()==1) {
			int idx = block[0].getIdx(0)
			configs[idx].tempEnv = tempEnvs[idx]
			configs[idx].move()
			updatePropertySums(block, (double[]) [1.0].toArray())
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
				texp += configs[k+offset].potentialValueCurrent/tempEnvs[tuple.getIdx(k)].T
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

	private void updatePropertySums(List block, double[] rhos) {
		if (mvIndex < warmUpCycles) return
			int offset = block[0].minIdx()
		thermalProperties.eachWithIndex { f,i ->
			block.eachWithIndex { tuple, m ->
				if (log._sums) println "________"
				if (log._sums) println "tuple: $tuple"
				(0..tuple.size()-1).each { k ->
					if (log._sums) println "Sum(T=${x$k+offset+1}) += rho${m+1}*V({tuple.getIdx(k)+1})"
					double val
					if (f == potential) {
						val = configs[k+offset].potentialValueCurrent
					} else {
						val = f(configs[k+offset].xCurrent)
					}
					runningPropertySums[tuple.getIdx(k)][i] += rhos[m]*val
				}
			}
		}
	}

}
 // scrapts
/*
  		def pinsGroup = []
		def threads = []
		(1..numThreads).each {
			def pins = new PINS(movesPerThread, temperatures, blockSize, potential, thermalProperties)
			def thread = new Thread({pins.run()})
			pinsGroup << pins; threads << thread
			thread.start()
		}
		threads.each { t -> t.join() }
		println total(pinsGroup)
*/

/*
  	public static def total(def pinsGroup) {
		Map<Integer, List<Double>> totals = [:]
		(0..pinsGroup[0].temps.size()-1).each { i->
			totals[i] = []
			(1..pinsGroup[0].thermalProperties.size()).each{ totals[i]  << 0.0 }
		}
		pinsGroup.each { p ->
			p.thermalProperties.eachWithIndex { f,i ->
				(0..p.temps.size()-1).each { k->
					totals[k][i] << p.runningPropertySums[k][i]/(2*pinsGroup.size()*(p.mvIndex-p.warmUpCycles))
				}
			}
		}
		return totals
	}
 */

