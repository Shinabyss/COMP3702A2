package problem;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * This class represents one of the rectangular obstacles in Assignment 1.
 * 
 * @author lackofcheese
 */
public class Obstacle {
	/** Stores the obstacle as a Rectangle2D */
	private Rectangle2D rect;
	private List<Point2D> cornersInBoundary = new ArrayList<Point2D>();
	private double xMin;
	private double xMax;
	private double yMin;
	private double yMax;

	/**
	 * Constructs an obstacle with the given (x,y) coordinates of the
	 * bottom-left corner, as well as the width and height.
	 * 
	 * @param x
	 *            the minimum x-value.
	 * @param y
	 *            the minimum y-value.
	 * @param w
	 *            the width of the obstacle.
	 * @param h
	 *            the height of the obstacle.
	 */
	public Obstacle(double x, double y, double w, double h) {
		this.rect = new Rectangle2D.Double(x, y, w, h);
	}

	/**
	 * Constructs an obstacle from the representation used in the input file:
	 * that is, the x- and y- coordinates of all of the corners of the
	 * rectangle.
	 * 
	 * @param str
	 */
	public Obstacle(String str) {
		Scanner s = new Scanner(str);
		List<Double> xs = new ArrayList<Double>();
		List<Double> ys = new ArrayList<Double>();
		for (int i = 0; i < 4; i++) {
			double x = s.nextDouble();
			double y = s.nextDouble();
			xs.add(x);
			ys.add(y);
			if ((x > 0.02 && x < 0.98) && (y > 0.02 && y < 0.98)) {
				cornersInBoundary.add(new Point2D.Double(x, y));
			}
		}
		xMin = Collections.min(xs);
		xMax = Collections.max(xs);
		yMin = Collections.min(ys);
		yMax = Collections.max(ys);
		this.rect = new Rectangle2D.Double(xMin, yMin, xMax - xMin, yMax - yMin);
		s.close();
	}

	/**
	 * Returns a copy of the Rectangle2D representing this obstacle.
	 * 
	 * @return a copy of the Rectangle2D representing this obstacle.
	 */
	public Rectangle2D getRect() {
		return (Rectangle2D) rect.clone();
	}
	
	public char getMoveDirection (Point2D origin) {
		double disToxMin = Math.abs(origin.getX()-xMin);
		double disToxMax = Math.abs(origin.getX()-xMax);
		double disToyMin = Math.abs(origin.getY()-yMin);
		double disToyMax = Math.abs(origin.getY()-xMax);
		List<Double> distToEdges = new ArrayList<Double>();
		if (xMin > 0.02) {
			distToEdges.add(disToxMin);
		}
		if (xMax < 0.98) {
			distToEdges.add(disToxMax);
		}
		if (yMin > 0.02) {
			distToEdges.add(disToyMin);
		}
		if (yMax < 0.98) {
			distToEdges.add(disToyMax);
		}
		if(distToEdges.isEmpty()) {
			return 'e';
		}
		double minDistToObs = Collections.min(distToEdges);
		if (minDistToObs == disToxMin || minDistToObs == disToxMax ) {
			if (yMin > 0.02 && disToyMin <= disToyMax || yMax > 0.98) {
				System.out.println("going down!");
				return 'd';
			} else if (yMax < 0.98 && disToyMax < disToyMin || yMin < 0.02) {
				System.out.println("going up!");
				return 'u';
			} else {
				System.out.println("correction direction unknown!");
				return 'e';
			}
		} else {
			if (xMin > 0.02 && disToxMin <= disToxMax || xMax > 0.98) {
				return 'l';
			} else if (xMax < 0.98 && disToxMax < disToxMin || xMin > 0.02) {
				return 'r';
			} else {
				return 'e';
			}
		}
	}
	
	public List<Point2D> getCornerInBound (Point2D origin) {
		double disToxMin = Math.abs(origin.getX()-xMin);
		double disToxMax = Math.abs(origin.getX()-xMax);
		double disToyMin = Math.abs(origin.getY()-yMin);
		double disToyMax = Math.abs(origin.getY()-xMax);
		List<Double> distToEdges = new ArrayList<Double>();
		if (xMin > 0.02) {
			distToEdges.add(disToxMin);
		}
		if (xMax < 0.98) {
			distToEdges.add(disToxMax);
		}
		if (yMin > 0.02) {
			distToEdges.add(disToyMin);
		}
		if (yMax < 0.98) {
			distToEdges.add(disToyMax);
		}
		if(distToEdges.isEmpty()) {
			return null;
		}
		double minDistToObs = Collections.min(distToEdges);
		List<Point2D> retVal = new ArrayList<Point2D>();
		if (minDistToObs == disToxMin) {
			if (yMin > 0.02) {
				retVal.add(new Point2D.Double(xMin, yMin));
			}
			if (yMax < 0.98) {
				retVal.add(new Point2D.Double(xMin, yMax));
			}
		} else if (minDistToObs == disToxMax) {
			if (yMin > 0.02) {
				retVal.add(new Point2D.Double(xMax, yMin));
			}
			if (yMax < 0.98) {
				retVal.add(new Point2D.Double(xMax, yMax));
			}
		} else if (minDistToObs == disToyMin) {
			if (xMin > 0.02) {
				retVal.add(new Point2D.Double(xMin, yMin));
			}
			if (xMax < 0.98) {
				retVal.add(new Point2D.Double(xMax, yMin));
			}
		} else {
			if (xMin > 0.02) {
				retVal.add(new Point2D.Double(xMin, yMax));
			}
			if (xMax < 0.98) {
				retVal.add(new Point2D.Double(xMax, yMax));
			}
		}
		return retVal;
	}

	/**
	 * Returns a String representation of this obstacle.
	 * 
	 * @return a String representation of this obstacle.
	 */
	public String toString() {
		return rect.toString();
	}
}
