package clustering.incremental;

import java.util.ArrayList;

import measures.EuclideanDistance;
import datasets.DatasetPattern;

public class IncrementalDBSCAN {
	
	private ArrayList<DatasetPattern> dataset;
	private ArrayList<Cluster> clustersList;
	private int minPts;
	private double eps;
	private int clustersCount;

	public IncrementalDBSCAN(ArrayList<DatasetPattern> dataset, int minpts, double eps) {
		this.dataset = dataset;
		this.minPts = minpts;
		this.eps = eps;
		this.clustersList = new ArrayList<Cluster>();
		this.clustersCount = 0;
	}
	
	
	private void clusterPoint(DatasetPattern pattern){
		
	}
	
	
	/**
	 * Get the updSeed set of a datapoint
	 * @param point point
	 * @return updSeed set
	 */
	private ArrayList<Integer> getUpdSeedSet(DatasetPattern pattern){
		ArrayList<Integer> updSeedIndex = new ArrayList<Integer>();
		for (int i = 0; i < this.dataset.size(); i++) {
			DatasetPattern p = this.dataset.get(i);
			if(!p.isVisited()) break;
			double distance = EuclideanDistance.calculateDistance(pattern, p);
			if(distance > this.eps) continue;
			pattern.addToNeighborhoodPoints(p.getID());
			p.addToNeighborhoodPoints(pattern.getID());
			if(p.getPointsAtEpsIndexs().size() == this.minPts){
				 p.pointCausedToBeCore(pattern.getID());
				 updSeedIndex.add(p.getID());
				 continue;
			}
			if(p.isCore(this.minPts)) updSeedIndex.add(p.getID());
		}
		return updSeedIndex;
	}

	
	


}
