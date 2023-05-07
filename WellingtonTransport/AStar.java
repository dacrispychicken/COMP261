/**
 * Implements the A* search algorithm to find the shortest path
 * in a graph between a start node and a goal node.
 * It returns a Path consisting of a list of Edges that will
 * connect the start node to the goal node.
 */

import java.nio.file.Path;
import java.util.Collections;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;


public class AStar {


   // private static String timeOrDistance = "distance";    removed as was causing error when calculating shortest path by time




    /**
     * Should find and return the shortest path between a start and goal node in the directed graph.
     * shortest can be defined by time or distance
     */
    public static List<Edge> findShortestPath(Stop start, Stop goal, String timeOrDistance) {
        if (start == null || goal == null) { return null; }
        timeOrDistance = (timeOrDistance.equals("time")) ? "time" : "distance";

        Queue<PathItem> fringe = new PriorityQueue<>();
        Set<Stop> visited = new HashSet<>();
        Map<Stop, Edge> backpointers = new HashMap<>(); // store edges that lead to each visited node, used to reconstruct shortest path

        // Creating pathItem for the start node, set distance to 0 and estimate total cost, add to fringe
        PathItem startPath = new PathItem(start, null, 0.0, heuristic(start, goal, timeOrDistance));
        fringe.offer(startPath);

        // loop that goes until the queue is empty (all nodes connected to start node been visited)
        while (!fringe.isEmpty()) {

            // deque lowest estimated cost pathItem and retrieve corresponding node
            PathItem currentItem = fringe.poll();
            Stop currentNode = currentItem.getNode();

            // if the node from dequeued path item hasn't been visited, add the node to visited set
            if (!visited.contains(currentNode)) {
                visited.add(currentNode);
                backpointers.put(currentNode, currentItem.getEdge());

                // if current node is goal, return reconstructpath as a list of edges
                if (currentNode.equals(goal)) {
                    return reconstructPath(start, goal, backpointers);
                }

                //look at neighbours of current node
                for (Edge outEdge : currentNode.getForwardEdges()) {
                    Stop neighborNode = outEdge.toStop();

                    // if neighbourNode not visited, create pathItem for neighbourNode with the below values, add to fringe
                    if (!visited.contains(neighborNode)) {
                        double edgeCost = edgeCost(outEdge, timeOrDistance);
                        double neighborLength = currentItem.getLengthToNode() + edgeCost;
                        double neighborEstimate = neighborLength + heuristic(neighborNode, goal, timeOrDistance);
                        PathItem neighborPath = new PathItem(neighborNode, outEdge, neighborLength, neighborEstimate);
                        fringe.offer(neighborPath);
                    }
                }
            }
        }

        return null;
    }



    /**
     * Used to reconstruct the shortest path found by the A* algorithm in 'findShortestPath'
     */
    private static List<Edge> reconstructPath(Stop start, Stop goal, Map<Stop, Edge> backpointers) {

        List<Edge> path = new ArrayList<>();  // stores edges in the shortest path
        Stop currentNode = goal;              // set currentNode to goal, will work back from goal node to start node using backpointers

        // iterate backwards from goal node to start using backpointers, adding edges along the path to pathList
        while (!currentNode.equals(start)) {
            Edge edge = backpointers.get(currentNode);
            if (edge == null) {
                break;
            }
            path.add(edge);
            currentNode = edge.fromStop();
        }

        Collections.reverse(path);           // reverse: since path is constructed in reverse order need to reverse list to get correct order
        return path;
    }




    /** Return the heuristic estimate of the cost to get from a stop to the goal */
    public static double heuristic(Stop current, Stop goal, String timeOrDistance) {
        if (timeOrDistance=="distance"){ return current.distanceTo(goal);}
        else if (timeOrDistance=="time"){return current.distanceTo(goal) / Transport.TRAIN_SPEED_MPS;}
        else {return 0;}
    }

    /** Return the cost of traversing an edge in the graph */
    public static double edgeCost(Edge edge, String timeOrDistance){
        if (timeOrDistance=="distance"){ return edge.distance();}
        else if (timeOrDistance=="time"){return edge.time();}
        else {return 1;}
    }




}
