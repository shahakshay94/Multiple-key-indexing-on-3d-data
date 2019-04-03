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
	private static String outputPath = "C:\\Users\\Quoc Minh Vu\\EclipseWorkspace\\COMP6521_LAB2\\src\\output\\";
	private static int noOfBuckets = 1000;
	private static Scanner scanner;
	
	public static void main(String[] args) {
		String userChoice = "";
		RootIndex root = null;
		while (true) {
			scanner = new Scanner(System.in);
			System.out.println("-----------MENU-----------");
			System.out.println("1. Build Index");
			System.out.println("2. Range Query");
			System.out.println("3. Nearest-neighbor Query");
			System.out.println("4. Exit");
			System.out.print("Your choice: ");
			userChoice = scanner.next();
			System.out.println("--------------------------");
			switch (userChoice) {
				case "1":
					root = buildIndex();
					break;
				case "2":
					query1(root);
					break;
				case "3":
					query2(root);
					break;
				case "4":
					return;
				default:
					break;
			}
		}
	}
	
	private static void query1(RootIndex root) {
		if (root == null) {
			System.out.println("There is no Index");
			return;
		}
		double x1, x2, y1, y2, z1, z2;
		while (true) {
			System.out.println("-------------------");
			System.out.print("Enter x1: "); x1 = scanner.nextDouble();
			System.out.print("Enter x2: "); x2 = scanner.nextDouble();
			System.out.print("Enter y1: "); y1 = scanner.nextDouble();
			System.out.print("Enter y2: "); y2 = scanner.nextDouble();
			System.out.print("Enter z1: "); z1 = scanner.nextDouble();
			System.out.print("Enter z2: "); z2 = scanner.nextDouble();
			System.out.print("-------------------");
			if (x1 < x2 && y1 < y2 && z1 < z2)
				break;
			else
				System.out.println("Invalid range");
		}
		
		long startTime2 = System.nanoTime();
		List<Point> query1Result = doRangeQuery(root, x1, x2, y1, y2, z1, z2);
		long endTime2 = System.nanoTime();
		System.out.println("Result size = " + query1Result.size());
		System.out.printf("Query 1 time: %d(s) %n", ((endTime2 - startTime2) / 1000000000));
		System.out.println();
		while (true) {
			System.out.print("Export result to file? (Y/N): "); String userChoice = scanner.next();
			switch (userChoice) {
				case "Y":
				case "y":
					dumpResultToFile(query1Result);
					return;
				case "N":
				case "n":
					return;
				default:
					break;
			}
		}
		
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
	
	private static void query2(RootIndex root) {
		if (root == null) {
			System.out.println("There is no Index");
			return;
		}
	}
	
	private static RootIndex buildIndex() {
		System.out.println("Building Index...");
		long startTime1 = System.nanoTime();
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
		System.out.printf("Build Index time: %d(s) %n", ((System.nanoTime() - startTime1) / 1000000000));
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
	
	private static void dumpResultToFile(List<Point> points) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(outputPath + "range_query_result.txt"));
			for (Point point : points) {
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
