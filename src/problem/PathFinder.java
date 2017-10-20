package problem;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.*;

public class PathFinder {
	
	private static int asvCount;
	private static ASVConfig initASV;
	private static ASVConfig goalASV;
	private static List<ASVConfig> mapping = new ArrayList<ASVConfig>();
	private static int obstacleCount;
	private static List<Obstacle> obstacles = new ArrayList<Obstacle>();
	public static final double DEFAULT_MAX_ERROR = 1e-5;
	public static final double MAX_STEP = 0.001;
	public static final Rectangle2D BOUNDS = new Rectangle2D.Double(0, 0, 1, 1);
	private static Rectangle2D lenientBounds;
	private static Comparator<PriorityEntry> comparator = new PrioEntryComparator();
	private static HashMap<ASVConfig, Path> visited = new HashMap<ASVConfig, Path>();
	private static PriorityQueue<PriorityEntry> toBeVisited = new PriorityQueue<PriorityEntry>(comparator);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length != 2) {
			System.err.println("Incorrect Number of Arguments to start program.\nUsage: program input_file output_file");
			System.exit(1);
		}
		List<String> rawInput = readFile(args[0]);
		asvCount = Integer.parseInt(rawInput.get(0));
		initASV = new ASVConfig(asvCount, rawInput.get(1));
		goalASV = new ASVConfig(asvCount, rawInput.get(2));
		obstacleCount = Integer.parseInt(rawInput.get(3));
		lenientBounds = grow(BOUNDS, DEFAULT_MAX_ERROR);
		for (int i=4; i < rawInput.size(); i++) {
			obstacles.add(new Obstacle(rawInput.get(i)));
		}
		//test input
		System.out.println(initASV.toString());
		System.out.println(goalASV.toString());
		for (Obstacle i : obstacles) {
			System.out.println(i.toString());
		}
		mappingERT();
		System.out.println(Math.PI);
		System.out.println(Math.atan2(initASV.getPosition(2).getY() - initASV.getPosition(0).getY(), initASV.getPosition(2).getX() - initASV.getPosition(0).getX()));
		//Path calculatedPath = calculateRoute();
		double totalDist = 0;
		try{
		    PrintWriter writer = new PrintWriter(args[1], "UTF-8");
			for (int i=0; i < mapping.size(); i++) {
				if (i>0) {
					totalDist += mapping.get(i).maxDistance(mapping.get(i-1));
				}
			}
			writer.println(mapping.size()-1 + " " + totalDist);
			for (ASVConfig a : mapping) {
				writer.println(a.toString());
			}
		    writer.close();
		} catch (IOException e) {
			
		}
		
	}
	
	private static Path calculateRoute() {
		Path initPath = new Path(initASV);
		visited.put(initASV, initPath);
		for (ASVConfig i : mapping) {
			if (isValidStep(initASV, i)) {
				Path path1 = new Path(initPath);
				path1.addStep(i);
				PriorityEntry entry = new PriorityEntry(i, path1, goalASV);
				toBeVisited.add(entry);
			}
		}
		PriorityEntry toExpand = toBeVisited.remove();
		visited.put(toExpand.getASVConfig(), toExpand.getPath());
		while (!toBeVisited.isEmpty()) {
			if (toExpand.getASVConfig()==goalASV) {
				System.out.println("Path Found!!");
				return toExpand.getPath();
			}
			for (ASVConfig i : mapping) {
				if (isValidStep(toExpand.getASVConfig(), i)&&!visited.containsKey(i)) {
					Path path = new Path(toExpand.getPath());
					path.addStep(i);
					PriorityEntry entry = new PriorityEntry(i, path, goalASV);
					toBeVisited.add(entry);
					//System.out.println(mapping.size());
					//System.out.println(i);
					//System.out.println("Entry Added to queue! B: " + toBeVisited.size());
				}
			}
			toExpand = toBeVisited.remove();
			visited.put(toExpand.getASVConfig(), toExpand.getPath());
			System.out.println("here?? - " + visited.size());
			System.out.println(toExpand.getASVConfig().toString());
		}
		System.out.println("Path not found!!");
		return null;
	}
	
	private static void mappingERT() {
		mapping.add(initASV);
		while (!isValidStep(mapping.get(mapping.size()-1), goalASV)) {
			int j = mapping.size();
			for (int k=0; k<1; k++) {
				ASVConfig tester = new ASVConfig(mapping.get(j-1), goalASV);
				while (!isValidASVConfig(tester)) {
					System.out.println("invalid?");
					System.out.println(mapping.get(j-1));
					System.out.println(tester.toString());
					System.out.println(hasValidBoomLengths(tester));
					System.out.println(isConvex(tester));
					System.out.println(hasEnoughArea(tester));
					tester = new ASVConfig(mapping.get(j-1), goalASV);
				}
				if (hasCollision(tester, obstacles)) {
					for (Obstacle o : obstacles) {
						if (hasCollision(tester, o)) {
							char wallOrientation = o.getMoveDirection(tester.getPosition(0));
							ASVConfig coordShiftedTest = new ASVConfig(mapping.get(j-1), wallOrientation);
									if (!hasCollision(coordShiftedTest, obstacles)) {
										mapping.add(coordShiftedTest);
									}
						}
					}
				} else {
					mapping.add(tester);
				}
			}
			System.out.println("Entry count: " + mapping.size());
			System.out.println(mapping.get(j-1).toString());
		}
		mapping.add(goalASV);
	}
	
	private static void mappingPRM(String outFile) {
		
		mapping.add(initASV);
		while (mapping.size()<1000) {
			double[] testCoord = {Math.random(), Math.random()};
			ASVConfig tester = new ASVConfig(asvCount, testCoord);
			int j = 1;
			while (!isValidASVConfig(tester)) {
				j++;
				testCoord[0] = Math.random();
				testCoord[1] = Math.random();
				tester = new ASVConfig(asvCount, testCoord);
			}
			if (hasCollision(tester, obstacles)) {
				for (int i = 0; i<4; i++) {
					ASVConfig coordShiftedTest = new ASVConfig(tester);
					coordShiftedTest.shiftCoordinates(i);
					if (!hasCollision(coordShiftedTest, obstacles)) {
						mapping.add(coordShiftedTest);
					}
				}
			} else {
				mapping.add(tester);
			}
			System.out.println("Entry count: " + mapping.size());
		}
		mapping.add(goalASV);
	}
	
	private static List<String> readFile(String filename)
	{
	  List<String> records = new ArrayList<String>();
	  try
	  {
	    BufferedReader reader = new BufferedReader(new FileReader(filename));
	    String line;
	    while ((line = reader.readLine()) != null)
	    {
	      records.add(line);
	    }
	    reader.close();
	    return records;
	  }
	  catch (Exception e)
	  {
	    System.err.format("Exception occurred trying to read '%s'.", filename);
	    e.printStackTrace();
	    return null;
	  }
	}
	
	private static Rectangle2D grow(Rectangle2D rect, double delta) {
		return new Rectangle2D.Double(rect.getX() - delta, rect.getY() - delta,
				rect.getWidth() + delta * 2, rect.getHeight() + delta * 2);
	}
	
	private static boolean isValidStep(ASVConfig cfg0, ASVConfig cfg1) {
		return (cfg0.maxDistance(cfg1) <= MAX_STEP + DEFAULT_MAX_ERROR);
	}
	
	private static boolean hasValidBoomLengths(ASVConfig cfg) {
		List<Point2D> points = cfg.getASVPositions();
		for (int i = 1; i < points.size(); i++) {
			Point2D p0 = points.get(i - 1);
			Point2D p1 = points.get(i);
			double boomLength = p0.distance(p1);
			if (boomLength < 0.05 - DEFAULT_MAX_ERROR) {
				return false;
			} else if (boomLength > 0.05 + DEFAULT_MAX_ERROR) {
				return false;
			}
		}
		return true;
	}
	
	private static double normaliseAngle(double angle) {
		while (angle <= -Math.PI) {
			angle += 2 * Math.PI;
		}
		while (angle > Math.PI) {
			angle -= 2 * Math.PI;
		}
		return angle;
	}
	
	private static boolean isConvex(ASVConfig cfg) {
		List<Point2D> points = cfg.getASVPositions();
		points.add(points.get(0));
		points.add(points.get(1));

		double requiredSign = 0;
		double totalTurned = 0;
		Point2D p0 = points.get(0);
		Point2D p1 = points.get(1);
		double angle = Math.atan2(p1.getY() - p0.getY(), p1.getX() - p0.getX());
		for (int i = 2; i < points.size(); i++) {
			Point2D p2 = points.get(i);
			double nextAngle = Math.atan2(p2.getY() - p1.getY(),
					p2.getX() - p1.getX());
			double turningAngle = normaliseAngle(nextAngle - angle);

			if (turningAngle == Math.PI) {
				return false;
			}

			totalTurned += Math.abs(turningAngle);
			if (totalTurned > 3 * Math.PI) {
				return false;
			}

			double turnSign;
			if (turningAngle < -DEFAULT_MAX_ERROR) {
				turnSign = -1;
			} else if (turningAngle > DEFAULT_MAX_ERROR) {
				turnSign = 1;
			} else {
				turnSign = 0;
			}

			if (turnSign * requiredSign < 0) {
				return false;
			} else if (turnSign != 0) {
				requiredSign = turnSign;
			}

			p0 = p1;
			p1 = p2;
			angle = nextAngle;
		}
		return true;
	}
	
	private static final double getMinimumArea(int asvCount) {
		double radius = 0.007 * (asvCount - 1);
		return Math.PI * radius * radius;
	}
	
	private static boolean hasEnoughArea(ASVConfig cfg) {
		double total = 0;
		List<Point2D> points = cfg.getASVPositions();
		points.add(points.get(0));
		points.add(points.get(1));
		for (int i = 1; i < points.size() - 1; i++) {
			total += points.get(i).getX()
					* (points.get(i + 1).getY() - points.get(i - 1).getY());
		}
		double area = Math.abs(total) / 2;
		return (area >= getMinimumArea(cfg.getASVCount()) - DEFAULT_MAX_ERROR);
	}
	
	private static boolean fitsBounds(ASVConfig cfg) {
		for (Point2D p : cfg.getASVPositions()) {
			if (!lenientBounds.contains(p)) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean hasCollision(ASVConfig cfg, List<Obstacle> obstacles) {
		for (Obstacle o : obstacles) {
			if (hasCollision(cfg, o)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean hasCollision(ASVConfig cfg, Obstacle o) {
		Rectangle2D lenientRect = grow(o.getRect(), -DEFAULT_MAX_ERROR);
		List<Point2D> points = cfg.getASVPositions();
		for (int i = 1; i < points.size(); i++) {
			if (new Line2D.Double(points.get(i - 1), points.get(i))
					.intersects(lenientRect)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isValidASVConfig(ASVConfig cfg) {
		return (hasValidBoomLengths(cfg)&&isConvex(cfg)&&hasEnoughArea(cfg)&&fitsBounds(cfg));
	}

}
