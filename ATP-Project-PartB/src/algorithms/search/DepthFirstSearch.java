package algorithms.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class DepthFirstSearch extends ASearchingAlgorithm{

    private Stack<AState> open = new Stack<>();


    /**
     * Method will search  a path using Depth First Search algorithm and will return null if not found, or the goal state if found
     * @param s Searchable Problem
     * @return AState final state in the path
     */
    @Override
    public AState search(ISearchable s) {
        HashSet<AState> visitedSet = new HashSet<>();
        AState start = s.getStartState();
        AState goal = s.getGoalState();
        open.push(start);
        while(!open.isEmpty()) {
            AState currState = open.pop();
            if(visitedSet.contains(currState))
                continue;
            if ( currState.equals(goal)){
                //Updating num of evaluated nodes
                setVisitedCounter(visitedSet.size());
                return currState;
            }
            visitedSet.add(currState);
            ArrayList<AState> neighbors = s.getAllPossibleStates(currState);
            for(AState neighbor:neighbors){
                if(!visitedSet.contains(neighbor))
                    open.push(neighbor);
            }

        }
        return null;
    }

    @Override
    public String getName() {
        return "DepthFirstSearch";
    }
}
