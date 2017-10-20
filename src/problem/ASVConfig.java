package problem;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.awt.geom.Point2D;

/**
 * Represents a configuration of the ASVs. This class doesn't do any validity
 * checking - see the code in tester.Tester for this.
 *
 * @author lackofcheese
 */
public class ASVConfig {
	/** The position of each ASV */
	private List<Point2D> asvPositions = new ArrayList<Point2D>();
	private int turnDirection = 0;

	/**
	 * Constructor. Takes an array of 2n x and y coordinates, where n is the
	 * number of ASVs
	 *
	 * @param coords
	 *            the x- and y-coordinates of the ASVs.
	 */
	public ASVConfig(double[] coords) {
		for (int i = 0; i < coords.length / 2; i++) {
			asvPositions.add(new Point2D.Double(coords[i * 2],
					coords[i * 2 + 1]));
		}
		setUpOrientation();
	}

	/**
	 * Constructs an ASVConfig from a space-separated string of x- and y-
	 * coordinates
	 *
	 * @param asvCount
	 *            the number of ASVs to read.
	 * @param str
	 *            the String containing the coordinates.
	 */
	public ASVConfig(int asvCount, String str) throws InputMismatchException {
		Scanner s = new Scanner(str);
		for (int i = 0; i < asvCount; i++) {
			asvPositions
					.add(new Point2D.Double(s.nextDouble(), s.nextDouble()));
		}
		s.close();
		setUpOrientation();
	}
	
	public ASVConfig(ASVConfig cfg, ASVConfig goal) {
		turnDirection = cfg.getOrientation();
		Point2D lastGoalASV = goal.getPosition(goal.getASVCount()-1);
		Point2D originGoalASV = goal.getPosition(goal.getASVCount()-2);
		double lastAngle = normaliseAngle(Math.atan2(lastGoalASV.getY() - originGoalASV.getY(), lastGoalASV.getX() - originGoalASV.getX()));
		int j=0;
		for (j = 0; j < cfg.getASVCount(); j++) {
			if (cfg.getPosition(j).distance(goal.getPosition(j)) > 0.0005) {
				break;
			}
		}
		if (j == cfg.getASVCount()) {
			asvPositions = cfg.getASVPositions();
		} else if (j > 0) {
			int turningDirection = turnDirection;
			Point2D p1 = cfg.getPosition(j);
			Point2D origin = cfg.getPosition(j-1);
			double angle = 0;
			if (j==cfg.getASVCount()-1) {
				double currentAngle = normaliseAngle(Math.atan2(p1.getY() - origin.getY(), p1.getX() - origin.getX()));
				if (currentAngle < lastAngle && turnDirection == -1) {
					turningDirection = 1;
				} else if (currentAngle > lastAngle && turnDirection == 1) {
					turningDirection = -1;
				}
				angle = currentAngle + 0.0009/origin.distance(cfg.getPosition(cfg.getASVCount()-1))*turningDirection;
			} else {
				angle = normaliseAngle(Math.atan2(p1.getY() - origin.getY(), p1.getX() - origin.getX()) + 0.0009/origin.distance(cfg.getPosition(cfg.getASVCount()-1))*turningDirection);
			}
			System.out.println(turningDirection);
			System.out.println(angle);
			System.out.println(normaliseAngle(Math.atan2(p1.getY() - origin.getY(), p1.getX() - origin.getX())));
			System.out.println(cfg.getEnclosedArea());
			
			double xOff = Math.cos(angle)*0.05;
			double yOff = Math.sin(angle)*0.05;
			double previousAngle = angle;
			for (int m=0; m < cfg.getASVCount(); m++) {
				if (m<j) {
					asvPositions.add(cfg.getPosition(m));
				} else if (m==j) {
					Point2D asvn = new Point2D.Double(cfg.getPosition(m-1).getX()+xOff, cfg.getPosition(m-1).getY()+yOff);
					asvPositions.add(asvn);
					System.out.println("randomising?");
					System.out.println(j);
				} else {
					angle = previousAngle + Math.random()*Math.PI*turningDirection;
					double xOffs = Math.cos(angle)*0.05;
					double yOffs = Math.sin(angle)*0.05;
					Point2D asvn = new Point2D.Double(asvPositions.get(m-1).getX()+xOffs, asvPositions.get(m-1).getY()+yOffs);
					while(cfg.getPosition(m).distance(asvn) > 0.001) {
						//System.out.println(cfg.getPosition(i).distance(asvn));
						angle = previousAngle + Math.random()*Math.PI*turningDirection;
						xOffs = Math.cos(angle)*0.05;
						yOffs = Math.sin(angle)*0.05;
						asvn = new Point2D.Double(asvPositions.get(m-1).getX()+xOffs, asvPositions.get(m-1).getY()+yOffs);
					}
					asvPositions.add(asvn);
					previousAngle = angle;
				}
			}
			
		} else {
			Point2D p1 = cfg.getPosition(0);
			Point2D p2 = goal.getPosition(0);
			double directionOfGoal = Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
			double moveDistance = Math.random()*0.001;
			double moveAngle = (Math.random()-0.5)*Math.PI+directionOfGoal;
			double xOff = Math.cos(moveAngle)*moveDistance;
			double yOff = Math.sin(moveAngle)*moveDistance;
			asvPositions.add(new Point2D.Double(p1.getX()+xOff, p1.getY()+yOff));
			double angle = 0;
			double previousAngle = 0;
			for (int i = 1; i < cfg.getASVCount(); i++) {
				if (i<3) {
					if (i==2) {
						angle = previousAngle + Math.random()*Math.PI*turnDirection;
					} else {
						angle = Math.random()*2*Math.PI;
					}
					double xOffs = Math.cos(angle)*0.05;
					double yOffs = Math.sin(angle)*0.05;
					Point2D asvn = new Point2D.Double(asvPositions.get(i-1).getX()+xOffs, asvPositions.get(i-1).getY()+yOffs);
					while(cfg.getPosition(i).distance(asvn) > 0.001) {
						if (i==2) {
							angle = previousAngle + Math.random()*Math.PI*turnDirection;
							System.out.println(cfg.getPosition(i).distance(asvn));
						} else {
							angle = Math.random()*2*Math.PI;
						}
						xOffs = Math.cos(angle)*0.05;
						yOffs = Math.sin(angle)*0.05;
						asvn = new Point2D.Double(asvPositions.get(i-1).getX()+xOffs, asvPositions.get(i-1).getY()+yOffs);
					}
					asvPositions.add(asvn);
					previousAngle = angle;
				} else {
					double angleTurned = Math.abs(normaliseAngle(Math.PI-(angle - previousAngle)));
					previousAngle = angle;
					if (angleTurned > Math.PI/2) {
						angle += Math.random()*Math.PI*turnDirection;
						double xOffs = Math.cos(angle)*0.05;
						double yOffs = Math.sin(angle)*0.05;
						Point2D asvn = new Point2D.Double(asvPositions.get(i-1).getX()+xOffs, asvPositions.get(i-1).getY()+yOffs);
						while(cfg.getPosition(i).distance(asvn) > 0.001) {
							angle += Math.random()*Math.PI*turnDirection;
							xOffs = Math.cos(angle)*0.05;
							yOffs = Math.sin(angle)*0.05;
							asvn = new Point2D.Double(asvPositions.get(i-1).getX()+xOffs, asvPositions.get(i-1).getY()+yOffs);
						}
						asvPositions.add(asvn);
					} else {
						angle += generateObtuseAngle();
						double xOffs = Math.cos(angle)*0.05;
						double yOffs = Math.sin(angle)*0.05;
						Point2D asvn = new Point2D.Double(asvPositions.get(i-1).getX()+xOffs, asvPositions.get(i-1).getY()+yOffs);
						while(cfg.getPosition(i).distance(asvn) > 0.001) {
							angle += generateObtuseAngle();
							xOffs = Math.cos(angle)*0.05;
							yOffs = Math.sin(angle)*0.05;
							asvn = new Point2D.Double(asvPositions.get(i-1).getX()+xOffs, asvPositions.get(i-1).getY()+yOffs);
						}
						asvPositions.add(asvn);
					}
				}
			}
		}
	}
	
	public ASVConfig(int asvCount, double[] asv1coord) {
		asvPositions.add(new Point2D.Double(asv1coord[0], asv1coord[1]));
		double angle = 0;
		double previousAngle = 0;
		double turningDirection = 0;
		for (int i = 1; i < asvCount; i++) {
			if (i<3) {
				angle = Math.random()*2*Math.PI;
				double xOff = Math.cos(angle)*0.05;
				double yOff = Math.sin(angle)*0.05;
				asvPositions.add(new Point2D.Double(asvPositions.get(i-1).getX()+xOff, asvPositions.get(i-1).getY()+yOff));
				if (i==2) {
					if ((angle-previousAngle)>0) {
						turningDirection = 1;
					} else {
						turningDirection = -1;
					}
				}
				previousAngle = angle;
			} else {
				double angleTurned = Math.abs(normaliseAngle(Math.PI-(angle - previousAngle)));
				previousAngle = angle;
				if (angleTurned > Math.PI/2) {
					angle += Math.random()*Math.PI*turningDirection;
				} else {
					angle += generateObtuseAngle();
				}
				double xOff = Math.cos(angle)*0.05;
				double yOff = Math.sin(angle)*0.05;
				asvPositions.add(new Point2D.Double(asvPositions.get(i-1).getX()+xOff, asvPositions.get(i-1).getY()+yOff));
			}
		}
		setUpOrientation();
	}

	/**
	 * Copy constructor.
	 *
	 * @param cfg
	 *            the configuration to copy.
	 */
	public ASVConfig(ASVConfig cfg) {
		asvPositions = cfg.getASVPositions();
		turnDirection = cfg.getOrientation();
	}
	
	public ASVConfig(ASVConfig cfg, char dir) {
		turnDirection = cfg.getOrientation();
		if (dir == 'r') {
			for (Point2D i : cfg.getASVPositions()) {
				asvPositions.add(new Point2D.Double(i.getX()+0.0009, i.getY()));
			}
		} else if (dir == 'l') {
			for (Point2D i : cfg.getASVPositions()) {
				asvPositions.add(new Point2D.Double(i.getX()-0.0009, i.getY()));
			}
		} else if (dir == 'u') {
			for (Point2D i : cfg.getASVPositions()) {
				asvPositions.add(new Point2D.Double(i.getX(), i.getY()+0.0009));
			}
		} else if (dir == 'd'){
			for (Point2D i : cfg.getASVPositions()) {
				asvPositions.add(new Point2D.Double(i.getX(), i.getY()-0.0009));
			}
		} else {
			asvPositions = cfg.getASVPositions();
		}
	}
	
	private void setUpOrientation() {
		Point2D p0 = asvPositions.get(0);
		Point2D p1 = asvPositions.get(1);
		Point2D p2 = asvPositions.get(2);
		double angle1 = Math.atan2(p1.getY() - p0.getY(), p1.getX() - p0.getX());
		if (angle1<0) {
			angle1+=2*Math.PI;
		}
		double angle2 = Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
		if (angle2<0) {
			angle2+=2*Math.PI;
		}
		if (angle2 > angle1) {
			turnDirection = 1;
		} else if (angle2 < angle1) {
			turnDirection = -1;
		}
	}
	
	public int getOrientation() {
		return turnDirection;
	}
	
	private double generateObtuseAngle(){
		return Math.random()*Math.PI/2+Math.PI;
	}
	
	public void shiftCoordinates (int number) {
		if (number == 0) {
			for (Point2D i : asvPositions) {
				i.setLocation(i.getX(), i.getY()+0.001);
			}
		} else if (number == 1) {
			for (Point2D i : asvPositions) {
				i.setLocation(i.getX(), i.getY()-0.001);
			}
		} else if (number == 2) {
			for (Point2D i : asvPositions) {
				i.setLocation(i.getX()-0.001, i.getY());
			}
		} else if (number == 3) {
			for (Point2D i : asvPositions) {
				i.setLocation(i.getX()+0.001, i.getY());
			}
		}	
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

	/**
	 * Returns a space-separated string of the ASV coordinates.
	 *
	 * @return a space-separated string of the ASV coordinates.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Point2D point : asvPositions) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(point.getX());
			sb.append(" ");
			sb.append(point.getY());
		}
		return sb.toString();
	}

	/**
	 * Returns the maximum straight-line distance between the ASVs in this state
	 * vs. the other state, or -1 if the ASV counts don't match.
	 *
	 * @param otherState
	 *            the other state to compare.
	 * @return the maximum straight-line distance for any ASV.
	 */
	public double maxDistance(ASVConfig otherState) {
		if (this.getASVCount() != otherState.getASVCount()) {
			return -1;
		}
		double maxDistance = 0;
		for (int i = 0; i < this.getASVCount(); i++) {
			double distance = this.getPosition(i).distance(
					otherState.getPosition(i));
			if (distance > maxDistance) {
				maxDistance = distance;
			}
		}
		return maxDistance;
	}

	/**
	 * Returns the total straight-line distance over all the ASVs between this
	 * state and the other state, or -1 if the ASV counts don't match.
	 *
	 * @param otherState
	 *            the other state to compare.
	 * @return the total straight-line distance over all ASVs.
	 */
	public double totalDistance(ASVConfig otherState) {
		if (this.getASVCount() != otherState.getASVCount()) {
			return -1;
		}
		double totalDistance = 0;
		for (int i = 0; i < this.getASVCount(); i++) {
			totalDistance += this.getPosition(i).distance(
					otherState.getPosition(i));
		}
		return totalDistance;
	}

	/**
	 * Returns the position of the ASV with the given number.
	 *
	 * @param asvNo
	 *            the number of the ASV.
	 * @return the position of the ASV with the given number.
	 */
	public Point2D getPosition(int asvNo) {
		return asvPositions.get(asvNo);
	}

	/**
	 * Returns the number of ASVs in this configuration.
	 *
	 * @return the number of ASVs in this configuration.
	 */
	public int getASVCount() {
		return asvPositions.size();
	}

	/**
	 * Returns the positions of all the ASVs, in order.
	 *
	 * @return the positions of all the ASVs, in order.
	 */
	public List<Point2D> getASVPositions() {
		return new ArrayList<Point2D>(asvPositions);
	}
	
	public double getEnclosedArea () {
		double total = 0;
		List<Point2D> points = new ArrayList<Point2D>();
		points.addAll(asvPositions);
		points.add(points.get(0));
		points.add(points.get(1));
		for (int i = 1; i < points.size() - 1; i++) {
			total += points.get(i).getX()
					* (points.get(i + 1).getY() - points.get(i - 1).getY());
		}
		double area = Math.abs(total) / 2;
		return area;
	}
}
