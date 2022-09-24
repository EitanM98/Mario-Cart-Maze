package algorithms.search;

import java.util.*;

public class BreadthFirstSearch extends ASearchingAlgorithm{
    protected Queue<AState> open;
    protected HashSet<AState> visitedSet;

    public BreadthFirstSearch() {
        super();
        open=new LinkedList<>();
        visitedSet = new HashSet<>();
    }

    /**
     * Method will search  a path using Breadth First Search algorithm and will return null if not found, or the goal state if found
     * @param s Searchable Problem
     * @return AState final state in the path
     */
    @Override
    public AState search(ISearchable s) {
        AState start = s.getStartState();
        AState goal = s.getGoalState();
        open.add(start);
        //visitedSet.add(start);
        while(!open.isEmpty()){
            AState cur = open.poll();
            if(visitedSet.contains(cur))
                continue;
            if (cur.equals(goal)){
                //Updating num of evaluated nodes
                setVisitedCounter(visitedSet.size());
                return cur;
            }
            visitedSet.add(cur);
            ArrayList<AState> neighbors = s.getAllPossibleStates(cur);
            for(AState neighbor:neighbors){
                if(!visitedSet.contains(neighbor))
                    open.add(neighbor);
            }
        }

        return null;
    }

    @Override
    public String getName() {
        return "BreadthFirstSearch";
    }

}
