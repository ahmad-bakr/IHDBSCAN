package datasets;

import java.util.ArrayList;

public class DatasetPattern {	
	private ArrayList<Double> features;
	private boolean isVisited;
	private boolean isNoise;
	private boolean isBoarder;
	private int ID;
	private String originalCluster;
	private String assignedCluster;
	private int indexInPartition;
	private int pointCausedToBeCore;
	private ArrayList<Integer> pointsAtEpsIndexs;
	private int assignedCentroidID;

	
	
	/**
	 * Constructor
	 */
	public DatasetPattern(int id) {
		this.features = new ArrayList<Double>();
		this.isBoarder = false;
		this.isNoise = false;
		this.isVisited = false;
		this.ID = id;
		this.pointCausedToBeCore =-1;
		this.originalCluster = "";
		this.isVisited = false;
		this.isNoise = false;
		this.isBoarder = false;
		this.assignedCluster="";
		this.pointsAtEpsIndexs = new ArrayList<Integer>(); 
	}
	
	/**
	 * add feature to feature vector
	 * @param d feature
	 */
	public void addFeature(Double d){
		this.features.add(d);
	}
	
	/**
	 * get the length of the feature vector
	 * @return length of the feature vector
	 */
	public int getFeatureVectorLength(){
		return this.features.size();
	}
	
	/**
	 * Get the feature vector
	 * @return feature vector
	 */
	public ArrayList<Double> getFeatureVector(){
		return this.features;
	}
	
	/**
	 * Get the id of the dataset pattern
	 * @return id
	 */
	public int getID() {
		return ID;
	}
	
	/**
	 * true if the pattern is visited otherwise false
	 * @return true or false
	 */
	public boolean isVisited(){
		return this.isVisited;
	}
	
	/**
	 * true if the pattern is noise otherwise false
	 * @return true or false
	 */
	public boolean isNoise(){
		return this.isNoise;
	}
	
	/**
	 * true if the pattern is boarder otherwise false
	 * @return true or false
	 */
	public boolean isBoarder(){
		return this.isBoarder;
	}
	
	/**
	 * Set true if the pattern is noise otherwise false
	 * @param noise true or false
	 */
	public void isNoise(boolean noise){
		this.isNoise = noise;
	}
	
	/**
	 * Set true if the pattern is boader otherwise false
	 * @param boarder true or false
	 */
	public void isBoarder(boolean boarder){
		this.isBoarder = boarder;
	}
	
	/**
	 * Set true if the pattern is visited otherwise false
	 * @param visited true or false
	 */
	public void isVisited(boolean visited){
		this.isVisited = visited;
	}
	
	
	public int getPointCausedToBeCore() {
		return pointCausedToBeCore;
	}
	
	public void setAssignedCentroidID(int assignedCentroidID) {
		this.assignedCentroidID = assignedCentroidID;
	}
	
	public int getAssignedCentroidID() {
		return assignedCentroidID;
	}
	
	
	public void pointCausedToBeCore(int pointCausedToBeCore) {
		this.pointCausedToBeCore = pointCausedToBeCore;
	}
	
	public ArrayList<Integer> getPointsAtEpsIndexs() {
		return pointsAtEpsIndexs;
	}
	
	public void addToNeighborhoodPoints(int i){
		this.pointsAtEpsIndexs.add(i);
	}

	
	public boolean isCore(int minPts){
		if(this.pointsAtEpsIndexs.size()>= minPts) return true;
		return false;
	}
	
	public void assignedCluster(String assignedCluster) {
		this.assignedCluster = assignedCluster;
	}

	public void originalCluster(String originalCluster) {
		this.originalCluster = originalCluster;
	}
	
	
	public String getOriginalCluster() {
		return originalCluster;
	}
	
	public String getAssignedCluster() {
		return assignedCluster;
	}
	
	public void setIndexInPartition(int indexInPartition) {
		this.indexInPartition = indexInPartition;
	}
	
	public int getIndexInPartition() {
		return indexInPartition;
	}

	
	

	
	

}
