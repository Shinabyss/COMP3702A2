package problem;

import java.util.Comparator;

public class PrioEntryComparator implements Comparator<PriorityEntry> {

	@Override
	public int compare(PriorityEntry arg0, PriorityEntry arg1) {
		// TODO Auto-generated method stub
		if (arg0.getCost() < arg1.getCost())
        {
            return -1;
        }
        if (arg0.getCost() > arg1.getCost())
        {
            return 1;
        }
		return 0;
	}

}