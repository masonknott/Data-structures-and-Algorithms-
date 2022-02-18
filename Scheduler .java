import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import graphs.*;

public class Scheduler extends Graph {

	 private class Edge {
	        /*
	         *  We only explicitly store one endpoint of the edge. The
	         *  identity of the other endpoint is implicit from the fact
	         *  that the edge is stored in that vertex's adjacency list.
	         *  It wouldn't be wrong to store both endpoints and, in fact,
	         *  that would reduce storage space needed for undirected
	         *  graphs, as we could insert the same Edge object into
	         *  each endpoint's adjacency list.
	         */
	        int endpoint;
	        double weight;

	        /*
	         *  Constructor to create an edge with default weight of 1.
	         */
	        Edge (int endpoint) {
	            this.endpoint = endpoint;
	            this.weight = 1;
	        }

	        /*
	         *  Constructor to create an edge with a specific weight.
	         */
	        Edge (int endpoint, double weight) {
	            this.endpoint = endpoint;
	            this.weight = weight;
	        }

	        /*
	         *  There can only be one edge between any two vertices so we
	         *  override .equals() to say that two edges are equal if they
	         *  have the same endpoint, regardless of the weight. This allows
	         *  us to, e.g., use adj[x].contains(new Edge (y)) to ask if x
	         *  has an edge to y of any weight, since List.contains(),
	         *  List.indexOf() and so on use .equals() to determine if they've
	         *  found the thing we asked for.
	         */
	        @Override
	        public boolean equals(Object obj) {
	            if (!(obj instanceof Edge))
	                return false;
	            return ((Edge)obj).endpoint == this.endpoint;
	        }

	        /*
	         *  Override Object.toString to print out edges in a meaningful
	         *  way.
	         */
	        @Override
	        public String toString () {
	            return "" + endpoint + "(" + weight + ")";
	        }
	    }

	    /*
	     *  Stores whether this is a directed graph or not.
	     */
	    private final boolean isDirected;

	    /*
	     *  The adjacency lists. adj[x] will be the list of edges coming
	     *  out of x.
	     */
	    private ArrayList<Edge>[] adj;

	    /*
	     *  Constructor to create a graph with the specified number of
	     *  vertices and directed/undirected as required. Initially, the
	     *  graph has no edges.
	     */
	    Scheduler (int numVertices, boolean isDirected) {
	        this.numVertices = numVertices;
	        this.isDirected = isDirected;

	        /*
	         *  To create an array of ArrayList<Edge>s, Java forces us to
	         *  create an array of ArrayLists and then cast to the actual
	         *  type. I'm not sure why this is. We set up all the adjacency
	         *  lists to be empty lists.
	         */
	        adj = (ArrayList<Edge>[]) new ArrayList[numVertices];
	        for (int i = 0; i < numVertices; i++)
	            adj[i] = new ArrayList<>();
	    }
	    
	/*
     *  Add an edge (x, y) to the graph. We first check if an edge
     *  already exists between those two vertices. If it does, we update
     *  its weight. If not, we add it to x's adjacency list. In the case
     *  of an undirected graph, we also add the complementary edge (y,x),
     *  in the same way.
     */
    @Override
    public void addEdge (int x, int y, double weight) {
        directedAdd (x, y, weight);
        if (!isDirected())
            directedAdd (y, x, weight);
    }

    /*
     *  Add an edge to a directed graph. If there is already an edge
     *  (x,y), we update its weight; otherwise, we add it to x's
     *  adjacency list.
     */
    private void directedAdd (int x, int y, double weight) {
        Edge e = new Edge (y, weight);

        int i = adj[x].indexOf (e);
        if (i != -1)
            adj[x].get(i).weight = weight;
        else
            adj[x].add (e);
    }

    /*
     *  Delete an edge. If the graph is directed, we just delete
     *  the specified edge; if it's undirected, we also delete the
     *  complementary edge (y,x).
     */
    @Override
    public void deleteEdge (int x, int y) {
        directedDeleteEdge (x, y);
        if (!isDirected())
            directedDeleteEdge (y, x);
    }

    /*
     *  Delete an edge from a directed graph. The call to
     *  adj[x].remove() will remove any Edge object that has y as
     *  its endpoint.
     */
    private void directedDeleteEdge (int x, int y) {
        Edge e = new Edge (y, 1);
        adj[x].remove (e);
    }

    /*
     *  Returns whether the graph is directed.
     */
    @Override
    public boolean isDirected () {
        return isDirected;
    }

    /*
     *  Returns whether the graph contains the edge (x,y).
     */
    @Override
    public boolean isEdge (int x, int y) {
        return adj[x].contains (new Edge (y));
    }

    /*
     *  Returns the weight of the edge (x,y) if the edge exists, and
     *  Double.NaN if it does not.
     */
    @Override
    public double weight (int x, int y) {
        int i = adj[x].indexOf (new Edge (x));

        return i < 0 ? Double.NaN : adj[x].get(i).weight;
    }

    /*
     *  Computes the in-degree of vertex x. Since our adjacency lists
     *  are only storing out-neighbours, we must look through the other
     *  vertices' outgoing edges.
     */
    @Override
    public int inDegree (int x) {
        int count = 0;
        Edge e = new Edge (x);
        for (int i = 0; i < numVertices; i++)
            if (i != x && adj[i].contains (e))
                count++;
        return count;
    }

    /*
     *  Out-degree is easy to calculate: it's just the number of edges
     *  in the vertex's adjacency list.
     */
    @Override
    public int outDegree (int x) {
        return adj[x].size();
    }

    /*
     *  To return the array of in-neighbours, we need to search all
     *  the other vertices' adjacency lists, as with degree. To avoid
     *  searching them all twice (once to calculate degree and once to
     *  populate the array), we make an array that's guaranteed to be
     *  big enough, and then return a copy of the part that contains
     *  actual data.
     */
    @Override
    public int[] inNeighbours (int x) {
        Edge e = new Edge (x);
        int[] result = new int[numVertices];
        int j = 0;

        for (int i = 0; i < numVertices; i++)
            if (i != x && adj[i].contains(e))
                result[j++] = i;

        return Arrays.copyOf (result, j);
    }

    /*
     *  For out-neighbours, we just convert our adjacency list to an
     *  array.
     */
    @Override
    public int[] outNeighbours (int x) {
        int[] result = new int[adj[x].size()];
        int j = 0;

        for (Edge e : adj[x])
            result[j++] = e.endpoint;
        return result;
    }
    
    
    public static Graph getTestGraph() {
		Scheduler test = new Scheduler(10, DIRECTED_GRAPH);
		test.addEdge(4, 0);
		test.addEdge(4, 7);
		test.addEdge(1, 6);
		test.addEdge(1, 8);
		test.addEdge(9, 8);
		test.addEdge(9, 2);
		
		test.addEdge(0, 7);
		test.addEdge(6, 7);
		test.addEdge(6, 3);
		test.addEdge(8, 3);
		test.addEdge(8, 2);
		
		test.addEdge(3, 5);
		test.addEdge(2, 5);
		
		return test;
	}
    
    public static void printGraph(Graph g) {
    	for (int i = 0; i < g.numVertices(); i++) {
    		int[] inN = g.inNeighbours(i);
    		System.out.print(i + ": ");
    		for(int j = 0; j < inN.length; j++) {
    			System.out.print(inN[j] + " ");
    		}
    		System.out.println();
    	}
    }
    
    /*public static Graph makeGraph (int numVertices, double edgeProb) {
    	Scheduler randGraph = new Scheduler(numVertices, DIRECTED_GRAPH);
    	
    }*/
    
    public static List<Integer> getSchedule(Graph g){
    	List<Integer> scheduleList = new ArrayList<Integer>();
    	Queue<Integer> scheduleQueue = new ConcurrentLinkedQueue<>();
    	for(int i = 0; i < g.numVertices(); i++) {
    		if(g.inNeighbours(i).equals(null)) {
    			scheduleQueue.add(i);
    		}
    		while(!scheduleQueue.isEmpty()) {
        		scheduleList.add(scheduleQueue.poll());   
        		int[] outN = g.outNeighbours(i);
        		for(int j = 0; j < outN.length; j++) {
        			if(scheduleList.contains(g.inNeighbours(outN[j]))) {
        				scheduleQueue.add(outN[j]);
        			}
        		}
        	}
    	}
    	return scheduleList;
    }
    
    /*public static boolean isValidSchedule(List<Integer> schedule, Graph g) {
    	
    }*/
    
    public static void main (String[] args) {
    	Scheduler test = (Scheduler) Scheduler.getTestGraph();
    	Scheduler.printGraph(test);
    }
	
}
