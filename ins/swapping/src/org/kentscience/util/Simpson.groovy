package org.kentscience.util

class Simpson {

	public static double integrate(Closure f, double a, double b, int N) {
		// precision parameter
		double h = (b - a) / (N - 1);     // step size

		// 1/3 terms
		double sum = 1.0 / 3.0 * (f(a) + f(b));

		// 4/3 terms
		for (int i = 1; i < N - 1; i += 2) {
			double x = a + h * i;
			sum += 4.0 / 3.0 * f(x);
		}

		// 2/3 terms
		for (int i = 2; i < N - 1; i += 2) {
			double x = a + h * i;
			sum += 2.0 / 3.0 * f(x);
		}

		return sum * h;
	}



	// sample client program
	public static void main(String[] args) {
		def f = { x -> Math.sin(x) }
		println integrate(f, 0, 2*Math.PI, 10)
		println integrate(f, 0, 2*Math.PI, 100)
		println integrate(f, 0, 2*Math.PI, 1000)
		println integrate(f, 0, 2*Math.PI, 10000)
	}

}



