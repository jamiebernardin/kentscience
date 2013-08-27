package org.kentscience.util

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.views.ChartPanel;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

/**
 * Demonstrates minimal effort to create a dynamic chart.
 * 
 * @author Achim Westermann
 * 
 */

public class MinimalDynamicChart {

	Chart2D chart = new Chart2D()
	List<ITrace2D> traces = []
	
	public MinimalDynamicChart(int numSeries) {
		init(numSeries)
	}
	
	public void addPoints(def x, def points) {
		points.eachWithIndex { p, i ->
			traces[i].addPoint((double) x, (double) p)
		}
	}
	
	static def colors = [Color.RED, Color.GREEN, Color.ORANGE, Color.YELLOW, Color.BLUE]
	private init(numSeries) {
		(1..numSeries).each { i ->
			def trace = new Trace2DLtd(200);
			trace.setColor(Color.BLACK)
			traces << trace
			chart.addTrace(trace);
		}
		// Make it visible:
		// Create a frame.
		JFrame frame = new JFrame("MinimalDynamicChart");
		// add the chart to the frame:
		frame.getContentPane().add(chart);
		frame.setSize(400,300);
		// Enable the termination button [cross on the upper right edge]:
		frame.addWindowListener(
				new WindowAdapter(){
					public void windowClosing(WindowEvent e){
						System.exit(0);
					}
				}
				);
		frame.setVisible(true);
	}
	public static void main(args) {
		def chart = new MinimalDynamicChart(2)
		(1..1000).each{ i ->
			chart.addPoints(i, [Math.random(), 2+Math.random()])
			Thread.sleep(100)
		}
	}

	

}

/*
 	Timer timer = new Timer(true);
		TimerTask task = new TimerTask(){

					private double m_y = 0;
					private long m_starttime = System.currentTimeMillis();
					
					public void run() {
						// This is just computation of some nice looking value.
						double rand = Math.random();
						boolean add = (rand >= 0.5) ? true : false;
						this.m_y = (add) ? this.m_y + Math.random() : this.m_y - Math.random();
						// This is the important thing: Point is added from separate Thread.
						trace.addPoint(((double) System.currentTimeMillis() - this.m_starttime), this.m_y);
					}

				};

		timer.schedule(task, 1000, 20);
	}
*/
