package datasets;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ChameleonLoader implements DatasetLoaderIF{

	@Override
	public ArrayList<DatasetPattern> loadDataset(String datasetPath) throws IOException {
		ArrayList<DatasetPattern> list = new ArrayList<DatasetPattern>();
		FileInputStream fstream = new FileInputStream(datasetPath);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		int id=0;
		while ((strLine = br.readLine()) != null) {
			String [] tokens = strLine.split(" ");
			DatasetPattern pattern = new DatasetPattern(id);
			for (int i = 0; i < tokens.length; i++) {
				System.out.println(tokens[i]);
				pattern.addFeature(Double.parseDouble(tokens[i]));
			}
			id++;
			list.add(pattern);
		}
		return list;
	}
	
	@Override
	public int getNumberofPatternsInClass(String className) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public ArrayList<DatasetPattern> getDataset() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getClassesNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfAllPatterns() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void main(String[] args) throws IOException {
		ChameleonLoader loader = new ChameleonLoader();
		ArrayList<DatasetPattern> list = loader.loadDataset("/media/4B27441968D9A496/master/Enhanced Incremental DBSCAN/datasets/chameleon-data/t4.8k.dat");
		System.out.println(list.size());
		System.out.println(list.get(0).getFeatureVector().get(0));
		System.out.println(list.get(0).getFeatureVector().get(1));

	}



}
