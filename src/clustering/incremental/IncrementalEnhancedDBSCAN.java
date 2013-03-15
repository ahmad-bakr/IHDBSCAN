package clustering.incremental;

import java.io.IOException;
import java.util.ArrayList;

import measures.EuclideanDistance;

import datasets.ChameleonLoader;
import datasets.DatasetPattern;
import evaluation.DaviesBouldin;
import evaluation.DunnIndex;


public class IncrementalEnhancedDBSCAN {
	private Centroid [] partitions;
	private ArrayList<Cluster> clusters;
	private ArrayList<DatasetPattern> dataset;
	private IncrementalPartitioning partitioner;
	private ArrayList<DenseRegion> denseRegions;
	private IncrementalDBSCANPartitioner [] incrementalDBSCANs;
	private int minPts;
	private double eps;
	private int clustersCount;
	private double alpha;

	
	public IncrementalEnhancedDBSCAN(ArrayList<DatasetPattern> dataset, int numberOfPartitions, int minpts, double eps, double alpha) {
		this.dataset = dataset;
		this.minPts = minpts;
		this.eps = eps;
		this.clusters = new ArrayList<Cluster>();
		this.denseRegions = new ArrayList<DenseRegion>();
		this.partitioner = new IncrementalPartitioning(dataset, numberOfPartitions);
		this.partitions = partitioner.getCentroids();
		this.incrementalDBSCANs = new IncrementalDBSCANPartitioner[numberOfPartitions];
		for (int i = 0; i < numberOfPartitions; i++) {
			this.incrementalDBSCANs[i] = new IncrementalDBSCANPartitioner(i, this.minPts, this.eps, this.dataset);
		}
		this.clustersCount =0;
		this.alpha = alpha;	
	}

	public void run(){
		for (int i = 0; i < this.dataset.size(); i++) {
			DatasetPattern point = this.dataset.get(i);
			int partitionIndex = this.partitioner.partitionPoint(point);
			IncrementalDBSCANPartitioner incrementalDBSCAN = this.incrementalDBSCANs[partitionIndex];
			incrementalDBSCAN.addPointToPartition(point);
		}
		
		for (int i = 0; i < this.incrementalDBSCANs.length; i++) {
			IncrementalDBSCANPartitioner p = incrementalDBSCANs[i];
			ArrayList<DenseRegion> regions = p.getDenseRegions();
			for (int j = 0; j < regions.size(); j++) {
				DenseRegion d = regions.get(j);
				if(d.getActive()) this.denseRegions.add(d);
			}
		}
		
		removeNoiseAndLabelOutliers();
		mergeRegions(alpha);

	}
	
	private void removeNoiseAndLabelOutliers(){
		for (int i = 0; i < this.dataset.size(); i++) {
			DatasetPattern p = this.dataset.get(i);
			if (p.getAssignedCluster().equalsIgnoreCase("")){
				DenseRegion d = getNearestRegion(p);
				
				if(d!=null)d.addPoint(p.getID());
			}
		}
	}


	private DenseRegion getNearestRegion(DatasetPattern point){
		DenseRegion denseRegion = null;
		Double minDistance = Double.MAX_VALUE;
		for (int i = 0; i < this.denseRegions.size(); i++) {
			DenseRegion d = this.denseRegions.get(i);
			ArrayList<Integer> points = d.getPoints();
			for (int j = 0; j < points.size(); j++) {
				DatasetPattern p2 = this.dataset.get(points.get(j));
				if (p2.isNoise()) continue;
				//double distance = calculateDistanceBtwTwoPoints(point, p2);
				double distance =  EuclideanDistance.calculateDistance(point, p2);
				if(distance < minDistance){
					denseRegion = d;
					minDistance = distance;
				}
			}
		}
		
		if(minDistance > this.eps) return null;
		
		return denseRegion;
	}

	public ArrayList<DenseRegion> getDenseRegions() {
		return denseRegions;
	}

	
	/**
	 * Merge regions
	 * @param alpha alpha merge value
	 */
	private void mergeRegions(double alpha){
		for (int i = 0; i < this.denseRegions.size(); i++) {
			DenseRegion r1 = this.denseRegions.get(i);
			for (int j = 0; j < i; j++) {
				
				DenseRegion r2 = this.denseRegions.get(j);
				if(r1.getIsInCluster() && r2.getIsInCluster() && (r1.getClusterID() == r2.getClusterID())) continue;
				double connectivity = calculateConnectivityBetweenTwoRegions(r1, r2);
				if(connectivity >= alpha) mergeTwoRegions(r1, r2);
			}
		}
		
		for (int i = 0; i < this.denseRegions.size(); i++) {
			DenseRegion r = denseRegions.get(i);
			if (r.getIsInCluster()) continue;
			Cluster c = new Cluster(this.clustersCount);
			r.setClusterID(c.getID());
			c.addDenseRegion(r);
			this.clusters.add(c);
			clustersCount++;
		}
		
	}
	
	private double calculateConnectivityBetweenTwoRegions(DenseRegion r1, DenseRegion r2){
		int r1EdgesCount = calculateNumberOfEdgesInRegion(r1);
		int r2EdgesCount = calculateNumberOfEdgesInRegion(r2);
		int connectingEdgesCount = calculateNumOfConnectingEdgesBetTwoRegions(r1, r2);
		return (connectingEdgesCount*1.0)/((r1EdgesCount+r2EdgesCount)/2);
	}

	/**
	 * calculate the number of edges in a dense region
	 * @param r dense region
	 * @return number of edges in the dense region
	 */
	private int calculateNumberOfEdgesInRegion(DenseRegion r){
		ArrayList<Integer> regionPoints = r.getPoints();
		int numberOfEdges =0;
		for (int i = 0; i < regionPoints.size(); i++) {
			DatasetPattern p1 = this.dataset.get(regionPoints.get(i));
			if(p1.isCore(this.minPts)) continue;
			for (int j = 0; j < i; j++) {
				DatasetPattern p2 = this.dataset.get(regionPoints.get(j));
				//double distance = calculateDistanceBtwTwoPoints(p1, p2);
				double distance = EuclideanDistance.calculateDistance(p1, p2);
				if (distance <=eps) numberOfEdges++;
			}
		}
		return numberOfEdges;
	}

	/**
	 * Number of connectivity edges between two regions
	 * @param r1 region 1
	 * @param r2 region 2
	 * @return number of connectivity edges between r1 and r2
	 */
	private int calculateNumOfConnectingEdgesBetTwoRegions(DenseRegion r1, DenseRegion r2){
		ArrayList<Integer> r1BoarderPoints = r1.getPoints();
		ArrayList<Integer> r2BoarderPoints = r2.getPoints();
		int numberOfConnectivityEdges =0;
		for (int i = 0; i < r1BoarderPoints.size(); i++) {
			DatasetPattern p1 = this.dataset.get(r1BoarderPoints.get(i));
			if(p1.isCore(this.minPts)) continue;
			for (int j = 0; j < r2BoarderPoints.size(); j++) {
				DatasetPattern p2 = this.dataset.get(r2BoarderPoints.get(j));
				if(p2.isCore(this.minPts)) continue;
			//	double distance = calculateDistanceBtwTwoPoints(p1, p2);
				double distance = EuclideanDistance.calculateDistance(p1, p2);
				if(distance<=this.eps) numberOfConnectivityEdges++;
			}
		}
		return numberOfConnectivityEdges;
	}



	/**
	 * Merge to regions in clusters
	 * @param r1 region 1
	 * @param r2 region 2
	 */
	private void mergeTwoRegions(DenseRegion r1, DenseRegion r2){
		Cluster c = null;
		int clusterID =0;
		if(!r1.getIsInCluster() && !r2.getIsInCluster()){
			c = new Cluster(this.clustersCount);
			c.addDenseRegion(r1);
			c.addDenseRegion(r2);
			r1.setClusterID(c.getID());
			r2.setClusterID(c.getID());
			this.clusters.add(c);
			this.clustersCount++;
			
		}
		else if(r1.getIsInCluster() && r2.getIsInCluster()){
			mergeCluster(this.clusters.get(r1.getClusterID()), this.clusters.get(r2.getClusterID()));
		}
		else if(r1.getIsInCluster()){
			clusterID= r1.getClusterID();
			c = this.clusters.get(clusterID);
			r2.setClusterID(clusterID);	
			c.addDenseRegion(r2);
		}
		else if (r2.getIsInCluster()){
			clusterID = r2.getClusterID();
			c = this.clusters.get(clusterID);
			r1.setClusterID(clusterID);
			c.addDenseRegion(r1);
		}
				
	}
	
	/**
	 * Merge two clusters
	 * @param c1 cluster 1
	 * @param c2 cluster 2
	 */
	private void mergeCluster(Cluster c1, Cluster c2){
		c2.setActive(false);
		ArrayList<DenseRegion> c2Regions = c2.getRegions();
		for (int i = 0; i < c2Regions.size(); i++) {
			DenseRegion r = c2Regions.get(i);
			r.setClusterID(c1.getID());
			c1.addDenseRegion(r);
		}
	}
	
	public ArrayList<Cluster> getClusters() {
		return clusters;
	}


	public static void main(String[] args) throws IOException {
		int numPartitions =50;
		double eps = 10;
		int minPts= 20;
		double alpha = 0.01;
		ChameleonLoader loader = new ChameleonLoader();
		ArrayList<DatasetPattern> dataset = loader.loadDataset("/media/4B27441968D9A496/master/Enhanced Incremental DBSCAN/datasets/chameleon-data/t4.8k.dat");
		long startTime = System.currentTimeMillis();
		IncrementalEnhancedDBSCAN algorithm = new IncrementalEnhancedDBSCAN(dataset, numPartitions, minPts, eps, alpha);
		algorithm.run();
		long endTime = System.currentTimeMillis();
		System.out.println("Runtime = " + (endTime-startTime));
		ArrayList<Cluster> clusters = algorithm.getClusters();
		ArrayList<DenseRegion> regions = new ArrayList<DenseRegion>();
		

		DunnIndex dunn = new DunnIndex(clusters, regions ,dataset);
		System.out.println("Dunn Index = " + dunn.calculateDunnIndex());

		DaviesBouldin davies = new DaviesBouldin(clusters, dataset);
		System.out.println("Davies Measure = " + davies.calculateDaviesMeasure());
		
		
		
		
//		PlotEhancedDBSCAN plotter = new PlotEhancedDBSCAN("Clusters");
//		plotter.plot(dataset, clusters);
//		plotter.pack();
//		RefineryUtilities.centerFrameOnScreen(plotter);
//		plotter.setVisible(true); 

		
		
	}
	
	
}
