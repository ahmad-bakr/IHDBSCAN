package datasets;

import java.io.IOException;
import java.util.ArrayList;

public interface DatasetLoaderIF {
	public ArrayList<DatasetPattern> loadDataset(String datasetPath) throws IOException;
}
