package main;
public class Point {
	private double x;
	private double y;
	private double z;
	
	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getDimension(String dimension) {
		switch (dimension) {
			case "X":
			case "x":
				return x;
			case "Y":
			case "y":
				return y;
			case "Z":
			case "z":
				return z;
			default:
				throw new IllegalArgumentException("Dimension must be X, Y or Z: " + dimension);
		}
		
	}
	
	public boolean isInRange(double x1, double x2, double y1, double y2, double z1, double z2) {
		return (x1 <= x && x <= x2 && y1 <= y && y <= y2 && z1 <= z && z <= z2);
	}

	@Override
	public String toString() {
		return String.format("(%f, %f, %f)", x, y, z);
	}
}
