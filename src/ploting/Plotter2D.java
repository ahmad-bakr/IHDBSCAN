package ploting;

import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import clustering.incremental.Cluster;

import datasets.DatasetPattern;

public class Plotter2D extends ApplicationFrame{

	public Plotter2D(String title) {
		super(title);
	}
	
	public void plot(ArrayList<DatasetPattern> dataset,ArrayList<Cluster> clusters){
		XYSeriesCollection datasetCollection = new XYSeriesCollection();

		for (int i = 0; i < clusters.size(); i++) {
			Cluster c = clusters.get(i);
			if(!c.getIsActive()) continue;
			if(c.getPointsIDs().size() < 30) continue;
			XYSeries series = new XYSeries(i);
			ArrayList<Integer> points = c.getPointsIDs();
			for (int j = 0; j < points.size(); j++) {
				DatasetPattern p = dataset.get(points.get(j));
				double x = p.getFeatureVector().get(0);
				double y = p.getFeatureVector().get(1);
				series.add(x, y);
			}
			datasetCollection.addSeries(series);
		}
		
		JFreeChart chart = ChartFactory.createScatterPlot("Clusters", "X", "Y", datasetCollection, PlotOrientation.VERTICAL, true, true, false);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel); 

	}

}
