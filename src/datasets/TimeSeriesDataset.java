package datasets;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class TimeSeriesDataset implements DatasetLoaderIF{

	private Hashtable<String, Integer> classesInfo;
	private ArrayList<DatasetPattern> dataset;
	private int numberOfPatterns;

	public TimeSeriesDataset() {
		this.classesInfo = new Hashtable<String, Integer>();
		this.dataset = new ArrayList<DatasetPattern>();
		this.numberOfPatterns =0;
	}	
	
	
	@Override
	public ArrayList<DatasetPattern> loadDataset(String datasetPath) throws IOException {
		FileInputStream fstream = new FileInputStream(datasetPath);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		int id=0;
		int lineCount = 0;
		while ((strLine = br.readLine()) != null) {
			this.numberOfPatterns++;
			String [] tokens = strLine.split(" ");
			DatasetPattern pattern = new DatasetPattern(id);
			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i].trim().length() > 0){
					pattern.addFeature(Double.parseDouble(tokens[i].trim()));
				}
			}
			
			String className = String.valueOf(lineCount/100);
			pattern.originalCluster(className);
			
			lineCount++;
			
			if(this.classesInfo.containsKey(className)){
				int count = this.classesInfo.get(className);
				this.classesInfo.put(className, count + 1);
				
			}else{
				this.classesInfo.put(className, 1);
			}
			
			this.dataset.add(pattern);
		
			id++;
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
		TimeSeriesDataset loader = new TimeSeriesDataset();
		ArrayList<DatasetPattern> list = loader.loadDataset("/media/4B27441968D9A496/master/Enhanced Incremental DBSCAN/datasets/time_series_date/synthetic_control_charts_SCC.txt");
		System.out.println(1/100);
		System.out.println(100/100);
		System.out.println(201/100);

	}


}
