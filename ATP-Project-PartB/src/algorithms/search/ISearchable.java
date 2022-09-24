package algorithms.search;

import java.util.ArrayList;
import java.util.HashSet;

public interface ISearchable {
    AState getStartState();
    AState getGoalState();
    ArrayList<AState> getAllPossibleStates(AState astate);
}
