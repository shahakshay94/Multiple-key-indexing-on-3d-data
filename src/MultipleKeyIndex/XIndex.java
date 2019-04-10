package MultipleKeyIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class XIndex {	
	private Map<String, YIndex> hashMap = null;
	
	public XIndex() {
		this.hashMap = new HashMap<>();
	}
	
	public void addNewItem(double lowerBound, double upperBound, YIndex value) {
		this.hashMap.put(String.format("%f-%f", lowerBound, upperBound), value);
	}
	
	public List<YIndex> getIndexesOfRange(double lowerBound, double upperBound) {
		List<YIndex> result = new ArrayList<>();
		for (Entry<String, YIndex> entry : hashMap.entrySet()) {
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
