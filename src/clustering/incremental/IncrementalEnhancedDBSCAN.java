package clustering.incremental;

import java.util.ArrayList;

import datasets.DatasetPattern;


public class IncrementalEnhancedDBSCAN {
	private Centroid [] partitions;
	private ArrayList<Cluster> clusters;
	private ArrayList<DatasetPattern> dataset;
	private IncrementalPartitioning partitioner;
	private ArrayList<DenseRegion> denseRegions;
	//private IncrementalDBSCANPartitioner [] incrementalDBSCANs;
	private int minPts;
	private double eps;
	private int clustersCount;
	private double alpha;

}
