package clustering.incremental;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import org.jfree.ui.RefineryUtilities;
import ploting.Plotter2D;
import measures.EuclideanDistance;
import datasets.ChameleonLoader;
import datasets.DatasetLoaderIF;
import datasets.DatasetPattern;
import datasets.OptsDigitsDataset;
import evaluation.DaviesBouldin;
import evaluation.DunnIndex;
import evaluation.FMeasure;

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
	
	
	private void clusterPattern(DatasetPattern pattern){
		ArrayList<Integer> updSeedPointIndexs = getUpdSeedSet(pattern);
		if(updSeedPointIndexs.size()==0){
			 markAsNoise(pattern);
		}
		else if(updSeedContainsCorePatternsWithNoCluster(updSeedPointIndexs)){
			createCluster(pattern, updSeedPointIndexs);
		}
		else if(updSeedContainsCorePatternsFromOneCluster(updSeedPointIndexs)){
			joinCluster(pattern, updSeedPointIndexs);
		}
		else{
			mergeClusters(pattern, updSeedPointIndexs);
		}
		pattern.isVisited(true);


	}
	
	/**
	 * Merge the clusters into the first cluster
	 * @param point point
	 * @param indexs updSeed points
	 */
	private void mergeClusters(DatasetPattern point,ArrayList<Integer> indexs){
		ArrayList<Cluster> clusters = getClusterOfPoints(indexs);
		Cluster masterCluster = clusters.get(0);
		String masterClusterID = String.valueOf(masterCluster.getID());
		point.assignedCluster(masterClusterID);
		masterCluster.addPoint(point.getID());
		for (int i = 1; i < clusters.size(); i++) {
			Cluster c = clusters.get(i);
			c.setActive(false);
			ArrayList<Integer> cPoints = c.getPointsIDs();
			for (int j = 0; j < cPoints.size(); j++) {
				DatasetPattern p = this.dataset.get(cPoints.get(j));
				p.assignedCluster(masterClusterID);
				masterCluster.addPoint(p.getID());
			}
		}
		//handleInsertionEffect(point, indexs);
	}

	/**
	 * Collect the clusters ids of a updSeed
	 * @param pointsIDs points of updSeed
	 * @return list of clusters ids
	 */
	private ArrayList<Cluster> getClusterOfPoints(ArrayList<Integer> pointsIDs){
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		Hashtable<String, Boolean> idsSeen = new Hashtable<String, Boolean>();
		for (int i = 0; i < pointsIDs.size(); i++) {
			DatasetPattern p = this.dataset.get(pointsIDs.get(i));
			if(p.getAssignedCluster().equalsIgnoreCase("")) continue;
			if(!idsSeen.containsKey(p.getAssignedCluster())){
				clusters.add(this.clustersList.get(Integer.parseInt(p.getAssignedCluster())));
				idsSeen.put(p.getAssignedCluster(), true);
			}
		}
		return clusters;
	}

	
	
	/**
	 * Add point to cluster (Given that all points at indexs come from only one cluster)
	 * @param point the point
	 * @param indexs indexes point
	 */
	private void joinCluster(DatasetPattern point, ArrayList<Integer> indexs){
		String clusterID = this.dataset.get(indexs.get(0)).getAssignedCluster();
		Cluster c = this.clustersList.get(Integer.parseInt(clusterID));
		c.addPoint(point.getID());
		point.assignedCluster(clusterID);
	//	handleInsertionEffect(point, indexs);
	}

	
	/**
	 * Check if all points has the same cluster
	 * @param indexs updSeed 
	 * @return true if all points have the same clusters
	 */
	private boolean updSeedContainsCorePatternsFromOneCluster(ArrayList<Integer> indexs){
		String clusterID = this.dataset.get(indexs.get(0)).getAssignedCluster();
		for (int i = 1; i < indexs.size(); i++) {
			DatasetPattern p = this.dataset.get(indexs.get(i));
			if(!clusterID.equalsIgnoreCase(p.getAssignedCluster())) return false;
		}
		return true;
	}

	
	
	/**
	 * create new cluster with the points
	 * @param point datapoint
	 * @param seedPointsIDs updSeed points
	 */
	private void createCluster(DatasetPattern point, ArrayList<Integer> seedPointsIDs){
		Cluster c = new Cluster(this.clustersCount);
		String clusterID = String.valueOf(c.getID());
		this.clustersCount++;
		point.assignedCluster(clusterID);
		c.addPoint(point.getID());
		for (int i = 0; i < seedPointsIDs.size(); i++) {
			DatasetPattern p = this.dataset.get(seedPointsIDs.get(i));
			p.assignedCluster(clusterID);
			c.addPoint(p.getID());
		}
		this.clustersList.add(c);
	//	handleInsertionEffect(point, seedPointsIDs);
	}

	
	
	/**
	 * Check if all the core points has no assigned cluster
	 * @param indexs updSeet set
	 * @return true if no cluster is assigned to all points
	 */
	private boolean updSeedContainsCorePatternsWithNoCluster(ArrayList<Integer> indexs){
		for (int i = 0; i < indexs.size(); i++) {
			DatasetPattern p = this.dataset.get(indexs.get(i));
			if(!p.getAssignedCluster().equalsIgnoreCase("")) return false;
		}
		return true;
	}


	
	/**
	 * Mark point as noise
	 * @param point point
	 */
	private void markAsNoise(DatasetPattern p){
		p.isNoise(true);
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
			if(pattern.getID() == p.getID()) continue;
			if(!p.isVisited()) break;
			double distance = EuclideanDistance.calculateDistance(pattern, p);
		//	System.out.println("distance = " + distance);
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
	
	public void run(){
		for (int i = 0; i < this.dataset.size(); i++) {
			System.out.println(i);
			DatasetPattern p = this.dataset.get(i);
			clusterPattern(p);
		}
		noiseLabel();
	}
	
	
	private void noiseLabel(){
		for (int i = 0; i < this.dataset.size(); i++) {
			DatasetPattern p = this.dataset.get(i);
			if(!p.isNoise()) continue;
			ArrayList<Integer> neighbors = p.getPointsAtEpsIndexs();
			for (int j = 0; j < neighbors.size(); j++) {
				DatasetPattern neighbor = this.dataset.get(neighbors.get(j));
				if(neighbor.getAssignedCluster().equalsIgnoreCase("") || !neighbor.isCore(this.minPts)) continue;
		//		if(neighbor.getIsNoise()) continue;	
				p.assignedCluster(neighbor.getAssignedCluster());
				Cluster c = this.clustersList.get(Integer.parseInt(p.getAssignedCluster()));
				c.addPoint(p.getID());
				break;
			}
		}
	}

	public ArrayList<Cluster> getClustersList() {
		return clustersList;
	}
	
	public void printClustersInformation(){
		int cluster_num = 1;
		System.out.println("Dataset has " + this.dataset.size() + " Points");
		for (int i = 0; i < this.clustersList.size(); i++) {
			Cluster c = this.clustersList.get(i);
			if(!c.getIsActive()) continue;
			if(c.getPointsIDs().size() < 30) continue;
			System.out.println("Cluster " + cluster_num + " has " + c.getPointsIDs().size() + " Points");
			cluster_num++;
		}
	}


	
	public static void main(String[] args) throws Exception {
		double eps = 17;
		int minpts= 2;
		DatasetLoaderIF loader = new OptsDigitsDataset();
		ArrayList<DatasetPattern> dataset = loader.loadDataset("/media/4B27441968D9A496/master/Enhanced Incremental DBSCAN/datasets/optdigits/all_data.txt");
		long startTime = System.currentTimeMillis();
		IncrementalDBSCAN incDBSCAN = new IncrementalDBSCAN(dataset, minpts, eps);
		incDBSCAN.run();
		ArrayList<Cluster> clustersList = incDBSCAN.getClustersList();
		long endTime = System.currentTimeMillis();
		System.out.println("Incremental DBSCAN Results");
		System.out.println("==================================");
		incDBSCAN.printClustersInformation();
		FMeasure fmeasure = new FMeasure();
		fmeasure.calculate(clustersList, loader);
		System.out.println("Runtime = " + (endTime-startTime));
		System.out.println("EPS = " + eps);
		System.out.println("Minpts = " + minpts);
		System.out.println("Fmeasure = " + fmeasure.getFmeasure());
		System.out.println("Precision = "+ fmeasure.getPrecision());
		System.out.println("Recall = "+ fmeasure.getRecall());
		DunnIndex dunn = new DunnIndex(clustersList, dataset);
		System.out.println("Dunn Index = " + dunn.calculateDunnIndex());
		DaviesBouldin davies = new DaviesBouldin(clustersList, dataset);
		System.out.println("Davies Measure = " + davies.calculateDaviesMeasure());

		
	}
	


}
