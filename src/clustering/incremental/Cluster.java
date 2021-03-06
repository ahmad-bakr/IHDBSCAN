package clustering.incremental;

import java.util.ArrayList;
import java.util.Hashtable;


public class Cluster {
	private int ID;
	private ArrayList<DenseRegion> regions;
	private boolean isActive;
	private ArrayList<Integer> pointsIDs;
	private Hashtable<Integer, Boolean> pointsSeen;
	
	
	public Cluster(int id) {
		this.regions = new ArrayList<DenseRegion>();
		this.ID = id;
		this.isActive = true;
		this.pointsIDs = new ArrayList<Integer>();
		this.pointsSeen = new Hashtable<Integer, Boolean>();
	}
	
	
	public void addPoint(int index){
		if(!this.pointsSeen.containsKey(index)){
			this.pointsIDs.add(index);
			this.pointsSeen.put(index, true);
		}
	}
	
	public ArrayList<Integer> getPointsIDs() {
		return pointsIDs;
	}
	
	public void AddListOfPoints(ArrayList<Integer> pointsIds){
		this.pointsIDs.addAll(pointsIds);
	}
	
	
	public int getID() {
		return ID;
	}
	
	public ArrayList<DenseRegion> getRegions() {
		return this.regions;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public boolean getIsActive(){
		return this.isActive;
	}
	
	public void addDenseRegion(DenseRegion region){
		this.regions.add(region);
	}
	
	public void addPointsList(ArrayList<Integer> points){
		this.pointsIDs.addAll(points);
	}

}
