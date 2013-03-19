package measures;

import java.util.ArrayList;

import datasets.DatasetPattern;

public class EuclideanDistance {

	
	public static double calculateDistance(DatasetPattern p1, DatasetPattern p2) {
		ArrayList<Double> vector1 = p1.getFeatureVector();
		ArrayList<Double> vector2 = p2.getFeatureVector();
		int length = vector1.size();
		double distance = 0;
		for (int i = 0; i < length; i++) {
			distance += Math.pow(vector1.get(i) - vector2.get(i), 2);
		}
//		System.out.println("distance = " + Math.sqrt(distance));
		return Math.sqrt(distance);
	}

}
