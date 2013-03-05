package datasets;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DigitsLoader implements DatasetLoaderIF{

	@Override
	public ArrayList<DatasetPattern> loadDataset(String datasetPath)throws IOException {
		ArrayList<DatasetPattern> list = new ArrayList<DatasetPattern>();
		FileInputStream fstream = new FileInputStream(datasetPath);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		int id=0;
		while ((strLine = br.readLine()) != null) {
			String [] tokens = strLine.split(",");
			DatasetPattern pattern = new DatasetPattern(id);
			for (int i = 0; i < tokens.length; i++) {
				pattern.addFeature(Double.parseDouble(tokens[i].trim()));
			}
			System.out.println(id);
			id++;
			list.add(pattern);
		}
		return list;
	}
	
	public static void main(String[] args) throws IOException {
		DigitsLoader loader = new DigitsLoader();
		ArrayList<DatasetPattern> list = loader.loadDataset("/media/4B27441968D9A496/master/Enhanced Incremental DBSCAN/datasets/pendigit/all_data.txt");
		DatasetPattern p = list.get(0);
		System.out.println("number of patterns = " + list.size());
		System.out.println("each pattern has "+ p.getFeatureVectorLength() + " features");
	}

}
