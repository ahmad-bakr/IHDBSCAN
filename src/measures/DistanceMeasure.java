package measures;

import datasets.DatasetPattern;

public interface DistanceMeasure {
	
	public double calculateDistance(DatasetPattern p1, DatasetPattern p2);

}
