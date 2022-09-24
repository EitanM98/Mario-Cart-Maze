package algorithms.search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public abstract class ASearchingAlgorithm implements  ISearchingAlgorithm{

    protected int numOfVisitedNodes;

    /**
     * Method will search a path and will return null if not found, or the goal state if found
     * @param s Searchable Problem
     * @return AState final state in the path
     */
    public abstract AState search(ISearchable s);

    public int getNumberOfNodesEvaluated(){ return  numOfVisitedNodes;}

    protected void setVisitedCounter(int count) {
        numOfVisitedNodes=count;
    }

    public ASearchingAlgorithm() {
        this.numOfVisitedNodes = 0;
    }

    /**
     * Method creates the solution from start point to goal point
     * @param searchable a problem to be searched
     * @return Solution path if found, null otherwise
     */
    @Override
    public Solution solve(ISearchable searchable) {
        AState curState=search(searchable);
        if(curState==null)
            return null;
        LinkedList<AState> solution_path=new LinkedList<>();
        solution_path.add(curState);
        AState startState=searchable.getStartState();
        while(!curState.equals(startState)){
            curState=curState.getDiscoveredFrom();
            solution_path.addFirst(curState);
        }
        Solution sol=new Solution();
        sol.setSolPath(new ArrayList(solution_path));
        return sol;
    }
}
