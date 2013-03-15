package clustering.incremental;

import java.util.ArrayList;

import measures.EuclideanDistance;

import datasets.DatasetPattern;


public class Centroid {
	private int ID;
	private DatasetPattern pattern;	
	private double [] updateRate;
	private double [] SD;
	private double [] pHat;
	private double [] p;
	private double alpha;
	private double beta;
	private double c;
	private double k;
	private double taw;
	private double sumOfFeatures =0;
	ArrayList<Integer> pointsIDS;
	
	public Centroid(int id, DatasetPattern p) {
		this.ID = id;
		this.pattern = new DatasetPattern(id);
		ArrayList<Double> features = p.getFeatureVector();
		this.updateRate = new double[features.size()];
		this.SD = new double[features.size()];
		this.pHat = new double[features.size()];
		this.p = new double[features.size()];
		for (int i = 0; i < features.size(); i++) {
			this.pattern.addFeature(features.get(i));
			this.sumOfFeatures += features.get(i);
			this.updateRate[i] = this.SD[i] = this.pHat[i] = this.p[i] = 0  ;
		}
		this.alpha = 0.5;
		this.beta = 0.5;
		this.c = 0.5;
		this.k = 0.5;
		this.taw = 0;
		this.pointsIDS = new ArrayList<Integer>();		
	}
	
	public void updateCentroid(DatasetPattern p){
		this.pointsIDS.add(p.getID());
		double lambda = 0.5*(1- (calculateDotPoroduct(this.pattern.getFeatureVector(), p.getFeatureVector())/calculateMag(this.pattern.getFeatureVector(), p.getFeatureVector())));
		
		
		this.taw = Math.exp(-this.alpha*(1-lambda));
		this.taw = this.k*this.taw + (1-this.k) * this.taw;
		
		double pe = calculatePE();
		for (int i = 0; i < this.pattern.getFeatureVectorLength(); i++) {
			double feature = this.pattern.getFeatureVector().get(i);
			double modifiedFeature = feature - (feature - p.getFeatureVector().get(i)) * this.taw * this.p[i] * pe;
			this.pattern.getFeatureVector().set(i, modifiedFeature);
		}
		double [] diffs = new double[this.pattern.getFeatureVector().size()];
		for (int i = 0; i < diffs.length; i++) {
			diffs[i] = Math.abs(p.getFeatureVector().get(i)-this.pattern.getFeatureVector().get(i));
		}
		updateTheUpdatingRate(diffs);
		updateSD(diffs);
		updatePHat();
		updateP();
	}
	
	
	public int getID() {
		return ID;
	}
	
	private double calculateDotPoroduct(ArrayList<Double> list1, ArrayList<Double> list2){
		double result=0;
		for (int i = 0; i < list1.size(); i++) {
			result+= list1.get(i)*list2.get(i);
		}
		return result;
	}
	
	private double calculateMag(ArrayList<Double> list1, ArrayList<Double> list2){
		double result = 0;
		for (int i = 0; i < list1.size(); i++) {
			result+= Math.pow(list1.get(i) - list2.get(i), 2); 
		}
		return Math.sqrt(result);		
	}
	
	public double calculateDistance(DatasetPattern p){
		return EuclideanDistance.calculateDistance(this.pattern, p);
	}
	
	public double calculatePE(){
		double pe = 0;
		for (int i = 0; i < this.pattern.getFeatureVector().size(); i++) {
			double norm = normalizeFeature(this.pattern.getFeatureVector().get(i), this.sumOfFeatures);
			pe += norm * Math.log10(norm); 
		}
		return (1- (pe/Math.log10(0.5)));		
	}
	
	private void updateTheUpdatingRate(double [] distance){
		for (int i = 0; i < distance.length; i++) {
			this.updateRate[i] = (this.alpha*this.updateRate[i]) + ((1-this.alpha)*distance[i]);
		}		
	}
	
	private void updateSD(double [] distance){
		for (int i = 0; i < distance.length; i++) {
			this.SD[i] = (this.beta*this.SD[i]) + ((1-this.beta) * Math.abs(this.updateRate[i]- distance[i]) );
		}
	}
	
	private void updatePHat(){
		for (int i = 0; i < this.pHat.length; i++) {
			double e =  Math.exp(-0.5*(this.SD[i]/(1+this.updateRate[i])));
			this.pHat[i] = (2/(1+ e ))-1;
		}
	}
	
	private void updateP(){
		for (int i = 0; i < this.p.length; i++) {
			this.p[i] = c*this.p[i] + (1-c)*this.pHat[i];
		}
	}
	
	
	private double normalizeFeature(double feature, double sum){
		return feature/sum;
	}
	
	public static void main(String[] args) {
		System.out.println(Math.log10(0.5));
		int x,y;
		x=y=5;
		System.out.println(x);
		System.out.println(y);
	}
	
}
