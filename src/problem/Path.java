package problem;

import java.util.*;

public class Path {
	
	private List<ASVConfig> pathing = new ArrayList<ASVConfig>();
	private double distance;
	
	public Path (ASVConfig initialState) {
		pathing.add(initialState);
		distance = 0;
	}
	
	public Path (Path treadedPath) {
		pathing.addAll(treadedPath.getPath());
		distance = treadedPath.getTraversedDistance();
	}
	
	public double getTraversedDistance() {
		return distance;
	}
	
	public List<ASVConfig> getPath() {
		return pathing;
	}
	
	public void addStep (ASVConfig nextStep) {
		distance += pathing.get((pathing.size()-1)).totalDistance(nextStep);
		pathing.add(nextStep);
	}
	
	public int numOfSteps () {
		return pathing.size()-1;
	}

}
