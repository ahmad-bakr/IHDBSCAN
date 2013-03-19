package clustering.incremental;

import java.util.ArrayList;
import java.util.Hashtable;

import measures.EuclideanDistance;
import datasets.DatasetPattern;

public class IncrementalDBSCANPartitioner {

	private ArrayList<DatasetPattern> partitionPoints;
	private ArrayList<DenseRegion> denseRegions;
	private ArrayList<DatasetPattern> dataset;
	private int ID;
	private int minPts;
	private double eps;
	private int pointsCount;
	private int denseRegionCount;

	public IncrementalDBSCANPartitioner(int id, int minpts, double eps, ArrayList<DatasetPattern> dataset) {
		this.ID = id;
		this.partitionPoints = new ArrayList<DatasetPattern>();
		this.denseRegions = new ArrayList<DenseRegion>();
		this.minPts = minpts;
		this.eps = eps;
		this.pointsCount = 0;
		this.denseRegionCount = 0;
		this.dataset = dataset;
	}

	/**
	 * Add point to the partition
	 * 
	 * @param p the point to be added to the partition
	 */
	public void addPointToPartition(DatasetPattern p) {
		p.setIndexInPartition(this.pointsCount);
		clusterPoint(p);
		this.partitionPoints.add(p);
		this.pointsCount++;
		if(!p.isCore(minPts) && !p.isNoise()) System.out.println("border");
	}

	/**
	 * Cluster a new point
	 * @param point new point
	 */
	private void clusterPoint(DatasetPattern point) {
		ArrayList<Integer> updSeedPointIndexs = getUpdSeedSet(point);
		if (updSeedPointIndexs.size() == 0) {
			markAsNoise(point);
		} else if (updSeedContainsCorePointsWithNoCluster(updSeedPointIndexs)) {
			createDenseRegion(point, updSeedPointIndexs);
		} else if (updSeedContainsCorePointsFromOneCluster(updSeedPointIndexs)) {
			joinDenseRegion(point, updSeedPointIndexs);
		} else {
			mergeDenseRegions(point, updSeedPointIndexs);
		}
		point.isVisited(true);
	}
	
	
	/**
	 * Merge the clusters into the first cluster
	 * @param point point
	 * @param indexs updSeed points
	 */
	private void mergeDenseRegions(DatasetPattern point,ArrayList<Integer> indexs){
		ArrayList<DenseRegion> regions = getDenseRegionsOfPoints(indexs);
		DenseRegion masterRegion = regions.get(0);
		point.assignedCluster(masterRegion.getID());
		masterRegion.addPoint(point.getID());
		for (int i = 1; i < regions.size(); i++) {
			DenseRegion d = regions.get(i);
			d.setActive(false);
			ArrayList<Integer> dPoints = d.getPoints();
			for (int j = 0; j < dPoints.size(); j++) {
				DatasetPattern p = this.partitionPoints.get(this.dataset.get(dPoints.get(j)).getIndexInPartition());
				p.assignedCluster(masterRegion.getID());
				masterRegion.addPoint(p.getID());
			}
		}
	}
	

	/**
	 * Collect the clusters ids of a updSeed
	 * @param pointsIDs points of updSeed
	 * @return list of clusters ids
	 */
	private ArrayList<DenseRegion> getDenseRegionsOfPoints(ArrayList<Integer> pointsIDs){
		ArrayList<DenseRegion> regions = new ArrayList<DenseRegion>();
		Hashtable<String, Boolean> idsSeen = new Hashtable<String, Boolean>();
		for (int i = 0; i < pointsIDs.size(); i++) {
			DatasetPattern p = this.partitionPoints.get(pointsIDs.get(i));
			if(p.getAssignedCluster().equalsIgnoreCase("")) continue;
			if(!idsSeen.containsKey(p.getAssignedCluster())){
				regions.add(this.denseRegions.get(Integer.parseInt(p.getAssignedCluster().split("_")[1])));
				idsSeen.put(p.getAssignedCluster(), true);
			}
		}
		return regions;
	}



	/**
	 * Add point to cluster (Given that all points at indexs come from only one
	 * cluster)
	 * 
	 * @param point
	 *          the point
	 * @param indexs
	 *          indexes point
	 */
	private void joinDenseRegion(DatasetPattern point, ArrayList<Integer> indexs) {
		String denseRegionID = this.partitionPoints.get(indexs.get(0)).getAssignedCluster().split("_")[1];
		DenseRegion d = this.denseRegions.get(Integer.parseInt(denseRegionID));
		d.addPoint(point.getID());
		point.assignedCluster(d.getID());
	}

	/**
	 * Check if all points has the same cluster
	 * 
	 * @param indexs
	 *          updSeed
	 * @return true if all points have the same clusters
	 */
	private boolean updSeedContainsCorePointsFromOneCluster(ArrayList<Integer> indexs) {
		String clusterID = this.partitionPoints.get(indexs.get(0)).getAssignedCluster();
		for (int i = 1; i < indexs.size(); i++) {
			DatasetPattern p = this.partitionPoints.get(indexs.get(i));
			if (!clusterID.equalsIgnoreCase(p.getAssignedCluster()))
				return false;
		}
		return true;
	}

	/**
	 * create new cluster with the points
	 * 
	 * @param point
	 *          datapoint
	 * @param seedPointsIDs
	 *          updSeed points
	 */
	private void createDenseRegion(DatasetPattern point, ArrayList<Integer> seedPointsIDs) {
		DenseRegion d = new DenseRegion(this.ID + "_" + this.denseRegionCount);
		this.denseRegionCount++;
		point.assignedCluster(d.getID());
		d.addPoint(point.getID());
		for (int i = 0; i < seedPointsIDs.size(); i++) {
			DatasetPattern p = this.partitionPoints.get(seedPointsIDs.get(i));
			p.assignedCluster(d.getID());
			d.addPoint(p.getID());
		}
		this.denseRegions.add(d);
	}

	/**
	 * Check if all the core points has no assigned cluster
	 * 
	 * @param indexs
	 *          updSeet set
	 * @return true if no cluster is assigned to all points
	 */
	private boolean updSeedContainsCorePointsWithNoCluster(
			ArrayList<Integer> indexs) {
		for (int i = 0; i < indexs.size(); i++) {
			DatasetPattern p = this.partitionPoints.get(indexs.get(i));
			if (!p.getAssignedCluster().equalsIgnoreCase(""))
				return false;
		}
		return true;
	}

	/**
	 * Get the updSeed set of a datapoint
	 * 
	 * @param point
	 *          point
	 * @return updSeed set
	 */
	private ArrayList<Integer> getUpdSeedSet(DatasetPattern point) {
		ArrayList<Integer> updSeedIndex = new ArrayList<Integer>();
		for (int i = 0; i < this.partitionPoints.size(); i++) {
			DatasetPattern p = this.partitionPoints.get(i);
			//double distance = calculateDistanceBtwTwoPoints(point, p);
			double distance = EuclideanDistance.calculateDistance(point, p);
			if (distance > this.eps)
				continue;
			point.addToNeighborhoodPoints(p.getID());
			p.addToNeighborhoodPoints(point.getID());
			if (p.getPointsAtEpsIndexs().size() == this.minPts) {
				p.pointCausedToBeCore(point.getID());
				updSeedIndex.add(p.getIndexInPartition());
				continue;
			}
			if (p.isCore(this.minPts))
				updSeedIndex.add(p.getIndexInPartition());
		}
		return updSeedIndex;
	}

	/**
	 * Get id of the incremental DBSCAN partitioner
	 * 
	 * @return id
	 */
	public int getID() {
		return this.ID;
	}

	/**
	 * Get the points of the partition
	 * 
	 * @return points of the partition
	 */
	public ArrayList<DatasetPattern> getPartitionPoints() {
		return this.partitionPoints;
	}


	/**
	 * get the dense regions of this partition
	 * 
	 * @return dense regions
	 */
	public ArrayList<DenseRegion> getDenseRegions() {
		return this.denseRegions;
	}

	/**
	 * Mark point as noise
	 * 
	 * @param point
	 *          point
	 */
	private void markAsNoise(DatasetPattern point) {
		point.isNoise(true);
	}
	
	public static void main(String[] args) {
		
	}

}
