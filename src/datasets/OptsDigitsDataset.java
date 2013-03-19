package datasets;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class OptsDigitsDataset implements DatasetLoaderIF{

	private Hashtable<String, Integer> classesInfo;
	private ArrayList<DatasetPattern> dataset;
	private int numberOfPatterns;
	
	public OptsDigitsDataset() {
		this.classesInfo = new Hashtable<String, Integer>();
		this.dataset = new ArrayList<DatasetPattern>();
		this.numberOfPatterns =0;
	}
	
	@Override
	public ArrayList<DatasetPattern> loadDataset(String datasetPath)throws IOException {
	
		FileInputStream fstream = new FileInputStream(datasetPath);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		int id=0;
		while ((strLine = br.readLine()) != null) {
			this.numberOfPatterns++;
			String [] tokens = strLine.split(",");
			DatasetPattern pattern = new DatasetPattern(id);
			for (int i = 0; i < tokens.length-1; i++) {
				pattern.addFeature(Double.parseDouble(tokens[i].trim()));
			}
			pattern.originalCluster(String.valueOf(tokens[tokens.length-1]));
			
			int count = this.classesInfo.get(String.valueOf(tokens[tokens.length-1]));
			this.classesInfo.put(String.valueOf(tokens[tokens.length-1]), count + 1);
		
			id++;
			this.dataset.add(pattern);
		}
		return this.dataset;
	}
	
	@Override
	public int getNumberofPatternsInClass(String className) {
		return this.classesInfo.get(className);
	}

	@Override
	public ArrayList<DatasetPattern> getDataset() {
		return this.dataset;
	}
	
	@Override
	public ArrayList<String> getClassesNames() {
		ArrayList<String> classes = new ArrayList<String>();
		Enumeration e = this.classesInfo.keys();
		while (e.hasMoreElements()) {
			String className= (String) e.nextElement();
			classes.add(className);
		}
		return classes;
	}
	
	@Override
	public int getNumberOfAllPatterns() {
		return this.numberOfPatterns;
	}

	
	public static void main(String[] args) throws IOException {
		OptsDigitsDataset loader = new OptsDigitsDataset();
		ArrayList<DatasetPattern> list = loader.loadDataset("/media/4B27441968D9A496/master/Enhanced Incremental DBSCAN/datasets/pendigit/all_data.txt");
		DatasetPattern p = list.get(0);
		System.out.println("number of patterns = " + list.size());
		System.out.println("each pattern has "+ p.getFeatureVectorLength() + " features");
	}





}
