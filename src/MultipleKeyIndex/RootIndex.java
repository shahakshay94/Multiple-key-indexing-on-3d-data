package MultipleKeyIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RootIndex {	
	private Map<String, XIndex> hashMap = null;
	
	public RootIndex() {
		this.hashMap = new HashMap<>();
	}
	
	public void addNewItem(double lowerBound, double upperBound, XIndex value) {
		this.hashMap.put(String.format("%f-%f", lowerBound, upperBound), value);
	}
	
	public List<XIndex> getIndexesOfRange(double lowerBound, double upperBound) {
		List<XIndex> result = new ArrayList<>();
		for (Entry<String, XIndex> entry : hashMap.entrySet()) {
			String entryRange = entry.getKey();
			double entryLowerBound = Double.valueOf(entryRange.split("-")[0]);
			double entryUpperBound = Double.valueOf(entryRange.split("-")[1]);
			if ((entryLowerBound <= lowerBound && entryUpperBound >= upperBound) ||
					(entryLowerBound >= lowerBound && entryUpperBound <= upperBound) ||
					(entryLowerBound <= upperBound && entryUpperBound >= lowerBound)) {
				result.add(entry.getValue());
			}
		}
		return result;
	}
}
