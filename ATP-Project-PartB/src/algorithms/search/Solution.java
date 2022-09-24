package algorithms.search;

import java.io.Serializable;
import java.util.ArrayList;

public class Solution implements Serializable {
    private ArrayList<AState> solPath;

    public ArrayList<AState> getSolutionPath() {
        return solPath;
    }

    public void setSolPath(ArrayList<AState> solPath) {
        this.solPath = solPath;
    }
}
