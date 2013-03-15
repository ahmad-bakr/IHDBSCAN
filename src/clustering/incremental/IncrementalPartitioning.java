package clustering.incremental;

import java.io.IOException;
import java.util.ArrayList;

import org.jfree.ui.RefineryUtilities;

import ploting.PlotPartitioning;

import measures.EuclideanDistance;
import datasets.ChameleonLoader;
import datasets.DatasetPattern;

public class IncrementalPartitioning {
 	private ArrayList<DatasetPattern> dataset;
	private Centroid[] centroids;

	public IncrementalPartitioning(ArrayList<DatasetPattern> list, int k) {
		this.dataset= list;
		this.centroids = new Centroid[k];
		for (int i = 0; i < k; i++) {
			int randomIndex = 0 + (int)(Math.random() * (((200-1) - 0) + 1));
			DatasetPattern p = this.dataset.get(randomIndex);
			this.centroids[i] = new Centroid(i, p);
		}
	}
	
	/**
	 * return the centroids
	 * @return centroids
	 */
	public Centroid[] getCentroids() {
		return centroids;
	}
	
	
	/**
	 * Insert point into partitions
	 * @param point data point
	 * @return the partition id
	 */
	public int partitionPoint(DatasetPattern point){
		double distance = Double.MAX_VALUE;
		Centroid cen = null;
		for (int j = 0; j < this.centroids.length; j++) {
			//double d = calculateDistanceBtwTwoPoints(this.centroids[j].getX(), this.centroids[j].getY(), point);
			double d = EuclideanDistance.calculateDistance(centroids[j].getPattern(), point);
			if(d<distance){
				cen = this.centroids[j];
				distance = d;
			}
		}
		point.setAssignedCentroidID(cen.getID());
		cen.updateCentroid(point);
		return cen.getID();
	}
	
	/**
	 * run the partitioning algorithm to al data
	 */
	public void run(){
		for (int i = 0; i < this.dataset.size(); i++) {
			DatasetPattern point = this.dataset.get(i);
			partitionPoint(point);
		}
	}

	public static void main(String[] args) throws IOException {
		int k=4;
		ChameleonLoader loader = new ChameleonLoader();
		ArrayList<DatasetPattern> list = loader.loadDataset("/media/4B27441968D9A496/master/Enhanced Incremental DBSCAN/datasets/chameleon-data/t4.8k.dat");
		IncrementalPartitioning p = new IncrementalPartitioning(list, k);
		p.run();
		PlotPartitioning plotter = new PlotPartitioning("partitions");
		plotter.plot(list, p.getCentroids());
		plotter.pack();
		RefineryUtilities.centerFrameOnScreen(plotter);
		plotter.setVisible(true); 

	}
	
	
}
