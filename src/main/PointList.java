package main;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class PointList extends ArrayList<Point> {
	public Map<String, PointList> divideIntoBuckets(double bucketSize, String dimension) {
		double originalSize = this.size();
		int noOfBuckets = (originalSize <= bucketSize) ? 1 : (int) Math.ceil(originalSize / bucketSize);
		Map<String, PointList> result = new HashMap<>(noOfBuckets);
		int pointCount = 0;
		double minValue = Double.MIN_VALUE, maxValue = Double.MAX_VALUE;
		PointList currentPointList = new PointList();
		for (int i = 0; i < this.size(); i++) {
			Point point = this.get(i);
			currentPointList.add(point);
			pointCount++;
			if (pointCount >= bucketSize && i < this.size() - 1) {
				Point nextPoint = this.get(i + 1);
				if (nextPoint.getDimension(dimension) == point.getDimension(dimension))
					pointCount--;
			}
			if (pointCount >= bucketSize || i == this.size() - 1) {
				pointCount = 0;
				maxValue = point.getDimension(dimension);
				result.put(String.format("%f-%f", minValue, maxValue), currentPointList);
				currentPointList = new PointList();
				minValue = maxValue + 0.000001;
			}
		}
		return result;
	}
	
	public void sort(PointList pointList, int low, int high, String dimension) {
		int i = low, j = high;
		Point pivot = pointList.get(low + (high - low) / 2);
		while (i <= j) {
			while (pointList.get(i).getDimension(dimension) < pivot.getDimension(dimension)) {
				i++;
			}
			while (pointList.get(j).getDimension(dimension) > pivot.getDimension(dimension)) {
				j--;
			}

			if (i <= j) {
				exchange(this, i, j);
				i++;
				j--;
			}

		}
		if (low < j) {
			sort(pointList, low, j, dimension);
		}
		if (i < high) {
			sort(pointList, i, high, dimension);
		}
	}
	
	private void exchange(PointList pointList, int i, int j) {
		Point temp = (Point) pointList.get(i);
		pointList.set(i, pointList.get(j));
		pointList.set(j, temp);
	}
}
