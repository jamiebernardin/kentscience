package org.kentscience.ins

import groovy.lang.Closure;

import java.util.List;

class INS {
	
	static main(args) {
		Closure potential = { x ->
			double x2 = x*x
			(x2-1)*(x2-1)
		}
		Closure location = { x -> x }
		def thermalProperties = [potential]
		INS ins = new INS(5E5, [0.05, 0.1, 0.15, 0.2, 0.4], potential, thermalProperties) 
		long startTime = System.currentTimeMillis()
		ins.run()
		println ins.averages()
		println "time: ${System.currentTimeMillis() - startTime}"
	}
	
	List idxs
	List<Closure> thermalProperties = []
	int warmUpCycles = 10000
	Map<Integer, List<Double>> runningPropertySums = [:]
	
	int N
	int mvIndex = 0
	Closure potential
	List<INSConfig> configs = []
	List<Double> temps	
	List<INSTempEnv> tempEnvs = []
	double[] rhos
	
	public INS(BigDecimal N, List<Double> temps, Closure potential, def thermalProperties) {
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
		}
		idxs = Util.generateIndices(temps.size())
		rhos = new double[idxs.size()]
	}
	
	public void run() {
		init()
		calcRho()
		(0..N).each { 
			mvIndex++
			int n = selectIndex()
			configs.eachWithIndex{ config, i ->
				config.tempEnv = tempEnvs[idxs[n].getIdx(i)]
				config.move()
			}
			calcRho()
			if (mvIndex > warmUpCycles) {
				updatePropertySums()
			}
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
				l << runningPropertySums[k][i]/(mvIndex-warmUpCycles)
			}
			averages << l
		}
		return averages
	}
	
	private void init() {
		def sums = []
		(0..temps.size()-1).each { i->
			runningPropertySums[i] = []
			(1..thermalProperties.size()).each{
				runningPropertySums[i]  << 0.0
			}
		}	
	} 
	
	
	private void calcRho() {
		double[] rs = new double[idxs.size()]
		double total = 0
		idxs.eachWithIndex { tuple, k ->
			double texp = 0
			(0..temps.size()-1).each { i->
				texp += configs[i].potentialValueCurrent/tempEnvs[tuple.getIdx(i)].T
			}
			rs[k] = Math.exp(-texp)
			total += rs[k]
		}
		(0..rhos.length-1).each { i ->
			rhos[i] = rs[i]/total
		}
	}
	
	private int selectIndex() {
		 double select = Math.random() 
		 int i = 0
		 double total = rhos[0]
		 while (total < select) {
			 total += rhos[++i]
		 }
		 return i
	}
	
	private void updatePropertySums() {
		thermalProperties.eachWithIndex { f,i ->
			double[] vals = new double[temps.size()]
			(0..temps.size()-1).each { k->
				if (f == potential) {
					vals[k] = configs[k].potentialValueCurrent
				} else {
					vals[k] = f(configs[k].xCurrent)
				}
			}
			(0..temps.size()-1).each { k ->
				idxs.eachWithIndex{ tuple, j ->
					runningPropertySums[tuple.getIdx(k)][i] += rhos[j]*vals[k]
				}	
			}		
		}
	}

	

}
