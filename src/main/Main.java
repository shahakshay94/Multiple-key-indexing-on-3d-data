package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import MultipleKeyIndex.RootIndex;
import MultipleKeyIndex.XIndex;
import MultipleKeyIndex.YIndex;

public class Main {
	private static String inputFilePath = "C:\\Users\\Quoc Minh Vu\\EclipseWorkspace\\COMP6521_LAB2\\src\\input\\LA2.txt";
	private static int noOfBuckets = 1000;
	
	public static void main(String[] args) {
		long startTime1 = System.nanoTime();
		RootIndex root = buildIndex();
		System.out.printf("Build Index time: %d(s) %n", ((System.nanoTime() - startTime1) / 1000000000));
		
		// Query 1
		Scanner reader = new Scanner(System.in);
		System.out.print("Enter x1: "); double x1 = reader.nextDouble();
		System.out.print("Enter x2: "); double x2 = reader.nextDouble();
		System.out.print("Enter y1: "); double y1 = reader.nextDouble();
		System.out.print("Enter y2: "); double y2 = reader.nextDouble();
		System.out.print("Enter z1: "); double z1 = reader.nextDouble();
		System.out.print("Enter z2: "); double z2 = reader.nextDouble();
		reader.close();
		
		long startTime2 = System.nanoTime();
		List<Point> query1Result = doRangeQuery(root, x1, x2, y1, y2, z1, z2);
		System.out.printf("Query 1 time: %d(s) %n", ((System.nanoTime() - startTime2) / 1000000000));
		System.out.println("Result size = " + query1Result.size());
		query1Result.forEach(point -> System.out.println(point));
	}
	
	private static List<Point> doRangeQuery(RootIndex root, double x1, double x2, double y1, double y2, double z1, double z2) {
		List<Point> result = new ArrayList<>();
		List<XIndex> xIndexList = root.getValueOfRange(x1, x2);
		for (XIndex xIndex : xIndexList) {
			List<YIndex> yIndexList = xIndex.getValueOfRange(y1, y2);
			for (YIndex yIndex : yIndexList) {
				List<PointList> zIndex = yIndex.getValueOfRange(z1, z2);
				for (PointList pointList : zIndex) {
					for (Point point : pointList) {
						if (point.isInRange(x1, x2, y1, y2, z1, z2)) {
							result.add(point);
						}
					}
				}
			}
		}
		return result;
	}	
	
	private static RootIndex buildIndex() {
		PointList xPointList = readFile();
		Map<String, PointList> xBucket = hash(xPointList, "X");

		RootIndex rootIndex = new RootIndex();
		for (Entry<String, PointList> xEntry : xBucket.entrySet()) {
			String xBucketRange = xEntry.getKey();
			double xLowerBound = Double.valueOf(xBucketRange.split("-")[0]);
			double xUpperBound = Double.valueOf(xBucketRange.split("-")[1]);
			PointList yPointList = xEntry.getValue();
			Map<String, PointList> yBucket = hash(yPointList, "Y");
			
			XIndex xIndex = new XIndex();
			for (Entry<String, PointList> yEntry : yBucket.entrySet()) {
				String yBucketRange = yEntry.getKey();
				double yLowerBound = Double.valueOf(yBucketRange.split("-")[0]);
				double yUpperBound = Double.valueOf(yBucketRange.split("-")[1]);
				PointList zPointList = yEntry.getValue();
				Map<String, PointList> zBucket = hash(zPointList, "Z");
				
				YIndex yIndex = new YIndex();
				for (Entry<String, PointList> zEntry : zBucket.entrySet()) {
					String zBucketRange = zEntry.getKey();
					double zLowerBound = Double.valueOf(zBucketRange.split("-")[0]);
					double zUpperBound = Double.valueOf(zBucketRange.split("-")[1]);
					PointList pointList = zEntry.getValue();
					yIndex.addNewItem(zLowerBound, zUpperBound, pointList);
				}
				xIndex.addNewItem(yLowerBound, yUpperBound, yIndex);
			}
			rootIndex.addNewItem(xLowerBound, xUpperBound, xIndex);
		}
		return rootIndex;
	}
	
	private static Map<String, PointList> hash(PointList pointList, String dimension) {
		pointList.sort(pointList, 0, pointList.size() - 1, dimension);
		return pointList.divideIntoBuckets(noOfBuckets, dimension);
	}
	
	private static PointList readFile() {
		PointList result = new PointList();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(inputFilePath));
			String line;
			while ((line = reader.readLine()) != null) {
				List<String> data = Arrays.asList(line.substring(1,  line.length() - 1).replaceAll(" ", "").split(","));
				double x = Double.valueOf(data.get(0));
				double y = Double.valueOf(data.get(1));
				double z = Double.valueOf(data.get(2));
				Point point = new Point(x, y, z);
				result.add(point);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	private static void writeFile(String filePath, PointList pointList) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(filePath));
			for (Point point : pointList) {
				bw.write(point.toString());
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
