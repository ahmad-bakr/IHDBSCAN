package ploting;

import java.util.ArrayList;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import clustering.incremental.Centroid;
import datasets.DatasetPattern;

public class PlotPartitioning extends ApplicationFrame{
	
	public PlotPartitioning(String title) {
		super(title);
	}
	
	public void plot(ArrayList<DatasetPattern> dataset, Centroid[] centroids){
		XYSeriesCollection datasetCollection = new XYSeriesCollection();
		XYSeries [] series = new XYSeries[centroids.length];
		for (int i = 0; i < centroids.length; i++) {
			XYSeries seriesMed = new XYSeries("m"+String.valueOf(i));
			double x = centroids[i].getPattern().getFeatureVector().get(0);
			double y = centroids[i].getPattern().getFeatureVector().get(1);
			seriesMed.add(x, y);
			datasetCollection.addSeries(seriesMed);
			series[i] = new XYSeries(i);
		}
		
		for (int i = 0; i < dataset.size(); i++) {
			DatasetPattern p = dataset.get(i);
			series[p.getAssignedCentroidID()].add(p.getFeatureVector().get(0), p.getFeatureVector().get(1));
		}
		for (int i = 0; i < series.length; i++) {
			datasetCollection.addSeries(series[i]);
		}

		JFreeChart chart = ChartFactory.createScatterPlot("Clusters", "X", "Y", datasetCollection, PlotOrientation.VERTICAL, true, true, false);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel); 

		

	}

}
