/**
 * AStar search (and Dijkstra search) uses a priority queue of partial paths
 * that the search is building.
 * Each partial path needs several pieces of information, to specify
 * the path to that point, its cost so far, and its estimated total cost
 */

public class PathItem implements Comparable<PathItem> {

    private Stop node;             // current node
    private Edge edge;             // edge that leads from the previous node to current
    private double lengthToNode;   // total distance/time travelled to reach current node
    private double totalEstimate;  // estimated total distance/time travel

    /**
     * Constructs a PathItem
     */
    public PathItem(Stop node, Edge edge, double lengthToNode, double estimateTotalPath){
        this.node = node;
        this.edge = edge;
        this.lengthToNode = lengthToNode;
        this.totalEstimate = estimateTotalPath;
    }

    /**
     * Returns stop node of this pathItem
     */
    public Stop getNode() {
        return node;
    }

    /**
     * Returns the edge of this PathItem
     */
    public Edge getEdge() {
        return edge;
    }

    /**
     * Returns lengthToNode of this PathItem, which is the total distance/time travelled
     * to reach the current node
     */
    public double getLengthToNode() {
        return lengthToNode;
    }

    /**
     * Returns totalEstimate of this PathItem, which is the total distance/time
     * to reach goal node from start node via the current node
     */
    public double getTotalEstimate() {
        return totalEstimate;
    }

    /**
     * Compares this PathItem to anoother PathItem based on their totalEstimate
     * this makes it possible for a PathItem to be sorted correctly in the PriorityQueue of AStar
     */
    @Override
    public int compareTo(PathItem other) {
        return Double.compare(this.totalEstimate, other.totalEstimate);
    }
}
