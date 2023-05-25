
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;
import javafx.util.Pair;
import java.util.List;
import java.util.*;

/** Edmond karp algorithm to find augmentation paths and network flow.
 * 
 * This would include building the supporting data structures:
 * 
 * a) Building the residual graph(that includes original and backward (reverse) edges.)
 *     - maintain a map of Edges where for every edge in the original graph we add a reverse edge in the residual graph.
 *     - The map of edges are set to include original edges at even indices and reverse edges at odd indices (this helps accessing the corresponding backward edge easily)
 *     
 *     
 * b) Using this residual graph, for each city maintain a list of edges out of the city (this helps accessing the neighbours of a node (both original and reverse))

 * The class finds : augmentation paths, their corresponing flows and the total flow
 * 
 * 
 */

public class EdmondKarp {
    // class members

    //data structure to maintain a list of forward and reverse edges - forward edges stored at even indices and reverse edges stored at odd indices
    private static Map<String,Edge> edges; 

    // Augmentation path and the corresponding flow
    private static ArrayList<Pair<ArrayList<String>, Integer>> augmentationPaths =null;

    
    /**
     *  Create redisual graph structure in which every regular edge has a backedge with 0 capactity:
     *  - Does so by adding to the edges map:
     *  - Manually created an int i, which is initially 0, then adds forward edge at String.valueOf(i) in map, and reverse at String.valueOf(i+1)
     *    at end of each iteration increase int i by 2 e.g. 0 -> 2 -> 4 -> ... k(where k % 2 == 0)
     */
    public static void computeResidualGraph(Graph graph){
        // Initialise edges map
        edges = new HashMap<>();

        int i = 0; // int for edge index
        for(Edge edge : graph.getOriginalEdges()){
            // Forward edge, add to index 0 first will be put 0,2,4,...
            Edge forward = new Edge(edge.fromCity(),edge.toCity(),edge.transpType(),edge.capacity(),edge.flow());
            edges.put(String.valueOf(i),forward);

            // Reverse edge, add to index 1,3,5,7,...
            Edge backward = new Edge(edge.toCity(),edge.fromCity(),edge.transpType(),0, 0);
            edges.put(String.valueOf(i+1),backward);

            // Add forward and reverse edge ids to corresponding city
            edge.fromCity().addEdgeId(String.valueOf(i));
            edge.toCity().addEdgeId(String.valueOf(i + 1));

            //increments index by 2 for the next pair of edges
            i += 2;
        }

        printResidualGraphData(graph);  //may help in debugging
    }

    // Method to print Residual Graph 
    public static void printResidualGraphData(Graph graph){
        System.out.println("\nResidual Graph");
        System.out.println("\n=============================\nCities:");
        for (City city : graph.getCities().values()){
            System.out.print(city.toString());

            // for each city display the out edges 
            for(String eId: city.getEdgeIds()){
                System.out.print("["+eId+"] ");
            }
            System.out.println();
        }
        System.out.println("\n=============================\nEdges(Original(with even Id) and Reverse(with odd Id):");
        edges.forEach((eId, edge)->
                System.out.println("["+eId+"] " +edge.toString()));

        System.out.println("===============");
    }

    //=============================================================================
    //  Methods to access data from the graph. 
    //=============================================================================

    /**
     * Return the corresonding edge for a given String ID
     * - added in null test case:
     *   - if null, throw IllegalArgumentException : return edge
     */
    public static Edge getEdge(String id){
        Edge edge = edges.get(id);

        if(edge == null){
            throw new IllegalArgumentException("No edge found with ID: " + id);
        }

        return edge;
    }

    /**
     * Finds augmentation paths and their corresponding flows.
     *
     * @param graph - the graph used that will be made into a residual graph
     * @param from - from city
     * @param to - to city
     * @return - pair of Augmentation path and int representatin of the Maximum Flow through the path
     */
    public static ArrayList<Pair<ArrayList<String>, Integer>> calcMaxflows(Graph graph, City from, City to) {

        // This is where we store the augmentation paths
        augmentationPaths = new ArrayList<>();
        // Compute the residual graph first, Stored in the edges map
        computeResidualGraph(graph);

        while (true) {
            // Run the bfs to find an augmenting path and bottleneck
            Pair<ArrayList<String>, Integer> bfsResult = bfs(graph, from, to);
            // If the bfsResult's key (i.e., the path) is null, then we didn't find an augmenting path and we can break the loop
            if (bfsResult.getKey() == null) {
                break;
            }

            // Otherwise, we did find an augmenting path, so we'll add it to the list of augmenting paths
            augmentationPaths.add(bfsResult);
            // Get the bottleneck capacity of this path
            int bottleneck = bfsResult.getValue();

            // Now, we need to update the flows along this path
            for (String edgeId : bfsResult.getKey()) {
                // Get the edge from the map
                Edge edge = getEdge(edgeId);
                if(edge == null){
                    break;
                }
                // Update the flow of the edge and reduce its capacity
                edge.setFlow(edge.flow() + bottleneck);
                edge.setCapacity(edge.capacity() - bottleneck);

                // Update the reverse edge.
                // The id of the reverse edge is edgeId+1 if edgeId is even, and edgeId-1 if edgeId is odd.
                String reverseEdgeId = Integer.parseInt(edgeId) % 2 == 0 ? String.valueOf(Integer.parseInt(edgeId) + 1) : String.valueOf(Integer.parseInt(edgeId) - 1);
                Edge reverseEdge = getEdge(reverseEdgeId);
                // Increase the capacity of the reverse edge
                reverseEdge.setCapacity(reverseEdge.capacity() + bottleneck);
            }
        }

        // Return the list of augmenting paths and their respective bottlenecks
        return augmentationPaths;

    }

    /**
     * Uses Breadth first search to return a pair of augmentation path and the corresponding bottleneck flow
     *
     * @param s - from city
     * @param t - to city
     * @param graph - residual graph
     * @return - Pair of augmentation path and the corresponding bottleneck value
     * */
    public static Pair<ArrayList<String>, Integer>  bfs(Graph graph, City s, City t) {

        ArrayList<String> augmentationPath = new ArrayList<>(); // Array list of the edge ID's, they are Strings, but will be parsed as Integers later on, this is a "more lifelike data example"
        HashMap<City, Edge> backPointer = new HashMap<>();      // Backpointer data-structure to hold edges that lead to the vertex
        Queue<City> queue = new ArrayDeque<>();                 // Queue for breadth-first-search
        Set<City> visited = new HashSet<>();                    // Avoids an infinite loop

        queue.offer(s);

        // The BFS, poll the current city out of the queue until the queue is empty
        while (!queue.isEmpty()) {
            City cur = queue.poll();
            // For each edge out of the current in the Residual graph
            for (String edgeId : cur.getEdgeIds()) {
                Edge edge = getEdge(edgeId);
                // Checks that we aren't going back to the source and that the edge capacity > 0
                if (!edge.toCity().equals(s) && backPointer.get(edge.toCity()) == null && edge.capacity() != 0) {
                    backPointer.put(edge.toCity(), edge);
                    if (backPointer.get(t) != null) { // Found a path from s to t. Build it now from reverse
                        Edge pathEdge = backPointer.get(t);
                        while (pathEdge != null) {    // Adding edges to augmentation path in reverse from (t)
                            String path = getedgeId(edges, pathEdge);
                            augmentationPath.add(path);
                            pathEdge = backPointer.get(pathEdge.fromCity());
                        }
                        Collections.reverse(augmentationPath); // Since we added pathEdges to augmentationPath from sink to source, reverse the Collection
                        int bottleneck = findBottleneck(augmentationPath);
                        return new Pair<>(augmentationPath, bottleneck);
                    }
                    queue.offer(edge.toCity());
                }
            }
        }
        return new Pair<>(null, 0); // no path found
    }

    /**
     * Way to get a string representation of the pathEdge edge in the BFS method by iterating through the edges map
     *
     * @param edgeMap - The map we are looking through
     * @param target - the target edge that we want the String Id of
     * @return - edgeId of target
     */
    public static String getedgeId(Map<String, Edge> edgeMap, Edge target) {
        for (Map.Entry<String, Edge> entry : edgeMap.entrySet()) {
            if (entry.getValue().equals(target)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Finds the Bottleneck of an augmentation path - the minimum capacity of any edge on the path
     *
     * @param augmentationPath - augmentation path, a list of strings (nodes in the path)
     * @return int value of the minimum capacity of any edge in augmentation path
     */
    private static int findBottleneck(ArrayList<String> augmentationPath) {
        int bottleneck = Integer.MAX_VALUE;  // integer value to compare capacity, essentially infinity
        // Iterate over all edges in the augmentation path, use string and then create Edge object with corresponding edgeId
        for (String edgeId : augmentationPath) {
            Edge edge = getEdge(edgeId);
            if(edge != null) {
                bottleneck = Math.min(bottleneck, edge.capacity());  // bottleNeck = to the smallest value of either original bottleneck or edge.capactity()
            }
        }
        return bottleneck;
    }
   
}


