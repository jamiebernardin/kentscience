package org.kentscience.ins

public class Util {
	public static main(args) {
	//	println generateIndices(1, 3)
	//	println generateIndices(6)
	//	println generateChain(2,5)
	//	println generateChain(2,3)
		println generateChain(2,5)
	//	println generateIndices(4)
		int 
	}
	static List generateIndices(Integer num) {
		if (num > 6) throw new Exception("this needs to be fixed for num > 6")
		Set idxs = []
		int total = factorial(1, num)
		int count = 0
		while (idxs.size() != total) {
			count++
			Tuple t = new Tuple(num)		
			(1..num).each {
				// mega hack... but works for less than 7
				t.idx[it -1] = (int) (Math.random()*num+1)
			}
			if (t.test()) idxs << t
		}
		idxs as List
	}
	static List generateChain(Integer partial, Integer total) {
		if (total%partial != partial/2) throw new Exception("total%partial must equal partial/2")
		List chain = []
		int counter = partial/2
		chain << generateIndices((int) partial/2)
		while (counter < total) {
			chain << generateIndices(counter, partial)
			counter += partial
		}
		chain << generateIndices(partial)
		counter = partial
		while (counter < total-partial/2) {
			chain << generateIndices(counter, partial)
			counter += partial
		}
		chain << generateIndices((int) (total-partial/2), (int) partial/2)
		chain
	}
	private static List generateIndices(Integer offset, Integer num) {
		List baseTuples = generateIndices(num)
		baseTuples.each{ t ->
			(0..num-1).each { i ->
				t.idx[i] += offset
			}
		}
		baseTuples
	}
	static private int factorial(int carry, int i) {
		return (i!=0) ? carry*i*factorial(carry, i-1) : carry
	}
}

public class Tuple {
	Tuple(int num) {
		idx = new Integer[num]
		this.num = num
	}
	public int getIdx(int i) {
		idx[i]-1
	}
	public int minIdx() {
		if (idx.length == 1) return idx[0]-1
		int val = idx[0]-1
		(1..idx.length-1).each {
			val = (int) Math.min(val, idx[it]-1)
		}
		val
	}
	public int size() { idx.length }
	public String toString() { Arrays.asList(idx).toString() }
	Integer[] idx
	int num
	public boolean equals(Object that) {
		boolean retVal = true
		(0..num-1).each { i ->
			if (that.idx[i] != this.idx[i]) return false
		}
		retVal
	}
	public int hashCode() {
		int value = 0
		(0..num-1).each { i -> 
			value += idx[i]*Math.pow(10, i)
		}
		//println "HASH: $value"
		value
	}
	private boolean test() {
		Set ids = []
		(0..num-1).each {
			ids << idx[it]
		}
		(ids.size() == num)
	}
}
