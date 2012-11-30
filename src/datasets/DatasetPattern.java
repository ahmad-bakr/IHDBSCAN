package datasets;

import java.util.ArrayList;

public class DatasetPattern {	
	private ArrayList<Double> features;
	private boolean isVisited;
	private boolean isNoise;
	private boolean isBoarder;
	private int ID;
	
	/**
	 * Constructor
	 */
	public DatasetPattern() {
		this.features = new ArrayList<Double>();
		this.isBoarder = false;
		this.isNoise = false;
		this.isVisited = false;
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

}
