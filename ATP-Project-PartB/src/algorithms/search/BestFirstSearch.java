package algorithms.search;

import java.util.*;

public class BestFirstSearch extends BreadthFirstSearch{


    public BestFirstSearch() {
        super();
        visitedSet = new HashSet<>();
        open = new PriorityQueue<>(new AstateComperator());
    }

    /**
     * Astate Comperator
     * Returns 1 if the cost of a object is greater than the cost of b object and the opposite
     */
    class AstateComperator implements Comparator<AState> {
        public int compare(AState s1, AState s2) {
            if (s1.getCost() < s2.getCost())
                return -1;
            else if (s1.getCost() > s2.getCost())
                return 1;
            return 0;
        }
    }


    @Override
    public String getName() {
        return "BestFirstSearch";
    }
}
