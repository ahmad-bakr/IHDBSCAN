package datasets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public interface DatasetLoaderIF {
	public ArrayList<DatasetPattern> loadDataset(String datasetPath) throws IOException;
	public int getNumberofPatternsInClass(String className);
	public ArrayList<DatasetPattern> getDataset();
	public ArrayList<String> getClassesNames();
	public int getNumberOfAllPatterns();
}
