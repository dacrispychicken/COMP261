import javafx.util.Pair;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * Page rank measures the transitive influence of the nodes (influence of neighbours and neigbours on neighbours)
 * Page rank of a page P is dependant on page rank of pages that have an outgoing link to P (nodes pointing to P)
 * Google Uses page rank for search results.
 */
public class PageRank
{
    //class members 
    private static double dampingFactor = .85;
    private static int iter = 10;
    /**
     * build the fromLinks and toLinks:
     * - For each edge in graph, add to Links from e.toCity to edge.fromCity() and vice-versa
     *
     * @param graph - the graph which the links are being computed from
     */
    public static void computeLinks(Graph graph){
        for(Edge e : graph.getOriginalEdges()){
            e.fromCity().addToLinks(e.toCity());
            e.toCity().addFromLinks(e.fromCity());
        }
        printPageRankGraphData(graph);  ////may help in debugging
    }

    public static void printPageRankGraphData(Graph graph){
        System.out.println("\nPage Rank Graph");

        for (City city : graph.getCities().values()){
            System.out.print("\nCity: "+city.toString());
            //for each city display the in edges 
            System.out.print("\nIn links to cities:");
            for(City c:city.getFromLinks()){

                System.out.print("["+c.getId()+"] ");
            }

            System.out.print("\nOut links to cities:");
            //for each city display the out edges 
            for(City c: city.getToLinks()){
                System.out.print("["+c.getId()+"] ");
            }
            System.out.println();;

        }    
        System.out.println("=================");
    }

    /**
     * Computes Page Rank
     *
     * Instead of using setter and getter methods for pageRanks,
     * uses Map that links each City to it's page rank
     *
     * @param graph - graph that pageRank is calculated from
     */
    public static void computePageRank(Graph graph){
        // Get count of nodes in the graph
        int n = graph.getCities().size();

        // Initialize page ranks for all nodes
        Map<City, Double> pageRanks = new HashMap<>();
        for (City city : graph.getCities().values()) {
            pageRanks.put(city, 1.0 / n);
        }

        // Start iterations
        for (int i = 0; i < iter; i++) {
            // Create a copy of page ranks map for storing new page ranks
            Map<City, Double> newPageRanks = new HashMap<>();

            for (City city : graph.getCities().values()) {
                double rankSum = 0;

                // Calculate the summation part of PageRank formula
                for (City linkedCity : city.getFromLinks()) {
                    rankSum += pageRanks.get(linkedCity) / linkedCity.getToLinks().size();
                }

                // Calculate the new PageRank
                double newRank = (1 - dampingFactor) / n + dampingFactor * rankSum;

                // Store the new rank to the temporary map
                newPageRanks.put(city, newRank);
            }

            // Update the PageRanks
            pageRanks = newPageRanks;
        }

        // Print out the PageRank of each city
        System.out.printf("Iteration %d:\n\n", iter);
        for (City city : graph.getCities().values()) {
            System.out.println(city.getName() + "[" + city.getId() + "]" + ": " + pageRanks.get(city));
        }

    }
}
