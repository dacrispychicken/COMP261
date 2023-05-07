import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

//=============================================================================
//   TODO   Finding Components
//   Finds all the strongly connected subgraphs in the graph
//   Labels each stop with the number of the subgraph it is in and
//   sets the subGraphCount of the graph to the number of subgraphs.
//   Uses Kosaraju's_algorithm   (see lecture slides, based on
//   https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm)
//=============================================================================

public class Components{

    // Use Kosaraju's algorithm.
    // In the forward search, record which nodes are visited with a visited set.
    // In the backward search, use the setSubGraphId and getSubGraphID methods
    // on the stop to record the component (and whether the node has been visited
    // during the backward search).
    // Alternatively, during the backward pass, you could use a Map<Stop,Stop>
    // to record the "root" node of each component, following the original version
    // of Kosaraju's algorithm, but this is unnecessarily complex.


    /**
     * Uses Kosaraju's Algorithm to find all strongly connected components in the directed graph
     * labels each stop with the id of its component
     * sets total number of components in the graph
     * includes a forward and backwards depth first search using helper methods
     *
     * wikipedia:
     * "In the mathematical theory of directed graphs, a graph is said to be strongly connected if every vertex is reachable from every other vertex. The strongly connected components of an arbitrary directed graph form a partition into subgraphs that are themselves strongly connected"
     */
    public static void findComponents(Graph graph) {
        // reset subGraphIds of 'graph'
        System.out.println("calling findComponents");
        graph.resetSubGraphIds();

        Set<Stop> visited = new HashSet<Stop>();       //keep track of visited
        List<Stop> postOrder = new ArrayList<Stop>();  //list to store stops in post-order, important for backwards DFS

        //forward search - for each stop, if the stop not visited perform forward dfs, marks stops as visiting and add to post-order list
        for(Stop stop : graph.getStops()){
            if(!visited.contains(stop)){
                dfsForward(stop, visited, postOrder);
            }
        }

        //Reset visited set for backward search
        visited.clear();
        int subGraphId = 0;                           //used to assign unique IDs to the SCCs

        //Backward search - iterate through stops in reverse order, perform back dfs, marks stops as visited and assigning to current subGraphId
        for(int i = postOrder.size() - 1; i >= 0; i--){
            Stop stop = postOrder.get(i);
            if(!visited.contains(stop)) {
                dfsBackward(stop, visited, graph, subGraphId);
                subGraphId++;
            }
        }

        graph.setSubGraphCount(subGraphId);          //updates total number of strongly connected components in graph
    }

    /**
     * Forwards Depth First Search for Kosaaraju's algorithm
     * traverses graph in a depth-first order
     * marks stops as visited and adding them to the post-orderlist
     */
    public static void dfsForward(Stop stop, Set<Stop> visited, List<Stop> postOrder){
        visited.add(stop);
        for(Edge edge : stop.getForwardEdges()) {
            Stop neighbour = edge.toStop();
            if(!visited.contains(neighbour)){
                dfsForward(neighbour, visited, postOrder);
            }
        }
        postOrder.add(stop);
    }

    /**
     *Backwards Depth first search for Kosaraju's algorithm
     * traverses graph using reversed edges and assigns the subGraphID -
     * - to each visited stop to identify the strongly connected components
     */
    private static void dfsBackward(Stop stop, Set<Stop> visited, Graph graph, int subGraphId) {
        visited.add(stop);
        stop.setSubGraphId(subGraphId);
        for (Edge edge : stop.getBackwardEdges()) {
            Stop neighbor = edge.fromStop();
            if (!visited.contains(neighbor)) {
                dfsBackward(neighbor, visited, graph, subGraphId);
            }
        }
    }


}
