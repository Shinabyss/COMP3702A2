package problem;

public class PriorityEntry implements Comparable<PriorityEntry>{
	
	private ASVConfig cfg;
	private Path path;
	private double heuristic;
	
	public PriorityEntry (ASVConfig asvcfg, Path path, ASVConfig goal) {
		this.cfg = asvcfg;
		this.path = path;
		heuristic = cfg.maxDistance(goal);
	}
	
	public double getDistance () {
		return path.getTraversedDistance();
	}
	
	public double getHeuristic () {
		return heuristic;
	}
	
	public double getCost () {
		return path.getTraversedDistance();
	}
	
	public Path getPath () {
		return path;
	}
	
	public ASVConfig getASVConfig () {
		return cfg;
	}
	
	@Override
	public int compareTo(PriorityEntry other) {
		if (this.getCost() < other.getCost()) {
			return -1;
		} else if (this.getCost() > other.getCost()) {
			return 1;
		} else {
			return 0;
		}
	}
}