package algorithms.search;

import java.io.Serializable;
import java.util.Objects;

public abstract class AState implements Serializable {

    private AState discoveredFrom;
    private double cost=0;

    public AState(AState discoveredFrom,double cost) {
        this.discoveredFrom=discoveredFrom;
        this.cost=cost;
    }

    public AState getDiscoveredFrom() {
        return discoveredFrom;
    }

    public void setDiscoveredFrom(AState discoveredFrom) {
        this.discoveredFrom = discoveredFrom;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public abstract int hashCode();
}
