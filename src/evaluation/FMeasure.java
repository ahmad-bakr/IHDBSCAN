package evaluation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;

import clustering.incremental.Cluster;
import datasets.DatasetLoaderIF;
import datasets.DatasetPattern;

public class FMeasure {
	
	private double precision;
	private double recall;
	private double fmeasure;
	private Hashtable<String, ArrayList<Integer>> confusionMatrix;
	
	
	public FMeasure() {
		this.precision = 0;
		this.recall = 0;
		this.fmeasure = 0;
		this.confusionMatrix = new Hashtable<String, ArrayList<Integer>>();		
	}
	
	
	public double getFmeasure() {
		return (2.0*this.precision*this.recall)/(this.precision+this.recall);
	}

	public double getPrecision() {
		return precision;
	}

	public double getRecall() {
		return recall;
	}
	
	

	public void calculate(ArrayList<Cluster> clusters , DatasetLoaderIF datasetManager) throws Exception{
		ArrayList<String> classes = datasetManager.getClassesNames();
		initializeConfusionMartix(classes, clusters.size());
		
		for (int i = 0; i < clusters.size(); i++) {
			processCluster(clusters.get(i), datasetManager.getDataset());
		}

		for (int i = 0; i < classes.size(); i++) {
			String className = classes.get(i);
			double values [] = calculateFMeasureForClass(className, datasetManager, clusters);
			this.fmeasure += values[2] * datasetManager.getNumberofPatternsInClass(className);
			this.recall += values[1] * datasetManager.getNumberofPatternsInClass(className) ;
			this.precision += values[0] * datasetManager.getNumberofPatternsInClass(className);
		}

		this.fmeasure = (this.fmeasure*1.0)/datasetManager.getNumberOfAllPatterns();
		this.precision = (this.precision*1.0)/datasetManager.getNumberOfAllPatterns();
		this.recall = (this.recall*1.0)/datasetManager.getNumberOfAllPatterns();

	}
	
	
	private double[] calculateFMeasureForClass(String className, DatasetLoaderIF datasetManager, ArrayList<Cluster> clusters){
		double values [] = new double[3] ;
		double maxFMeasure = 0;
		double maxPrecision = 0;
		double maxRecall = 0;
		ArrayList<Integer> list = this.confusionMatrix.get(className);
		int classSize = datasetManager.getNumberofPatternsInClass(className);
		for (int i = 0; i < list.size(); i++) {
			int clusterID = i;
			int clustersize = clusters.get(clusterID).getPointsIDs().size();
			double precision = (list.get(i)*1.0)/clustersize;
			double recall = (list.get(i)*1.0)/classSize;
			double fmeasure = (2*precision*recall)/(precision+recall);
			if(fmeasure>maxFMeasure){
				maxFMeasure = fmeasure;
				maxPrecision = precision;
				maxRecall = recall;
			}
		}

		values[0] = maxPrecision;
		values[1] = maxRecall;
		values[2] = maxFMeasure;
		return values;
	}	
	
	
	
	
	private void initializeConfusionMartix(ArrayList<String> originalClasses, int numberOfClusters){
		for (int i = 0; i < originalClasses.size(); i++) {
			ArrayList<Integer> list = new ArrayList<Integer>(numberOfClusters);
			for (int j = 0; j < numberOfClusters; j++) {
				list.add(0);
			}
			confusionMatrix.put(originalClasses.get(i),list);
		}
	}

	
	private void processCluster(Cluster cluster, ArrayList<DatasetPattern> dataset) {
		ArrayList<Integer> pointsIDs = cluster.getPointsIDs();
		int clusterIDIndex = cluster.getID(); 
		for (int i = 0; i < pointsIDs.size(); i++) {
			DatasetPattern pattern = dataset.get(i);
			String originalClass = pattern.getOriginalCluster();
			int count = this.confusionMatrix.get(originalClass).get(clusterIDIndex);
			this.confusionMatrix.get(originalClass).set(clusterIDIndex, count +1);
		}	
	}


}
