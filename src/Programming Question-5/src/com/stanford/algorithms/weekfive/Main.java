/*
 * Programming Question - Week 5, July 2013.
 * 
 * In this programming problem you'll code up Dijkstra's shortest-path algorithm. 
 * Download the text file here. (Right click and save link as). 
 * 
 * The file contains an adjacency list representation of an undirected weighted 
 * graph with 200 vertices labeled 1 to 200. Each row consists of the node tuples 
 * that are adjacent to that particular vertex along with the length of that edge. 
 * For example, the 6th row has 6 as the first entry indicating that this row corresponds
 * to the vertex labeled 6. The next entry of this row "141,8200" indicates that there
 * is an edge between vertex 6 and vertex 141 that has length 8200. The rest of the pairs
 * of this row indicate the other vertices adjacent to vertex 6 and the lengths of the 
 * corresponding edges.
 * 
 * Your task is to run Dijkstra's shortest-path algorithm on this graph, using 1 (the
 * first vertex) as the source vertex, and to compute the shortest-path distances between
 * 1 and every other vertex of the graph. If there is no path between a vertex v and vertex
 * 1, we'll define the shortest-path distance between 1 and v to be 1000000. 
 * 
 * You should report the shortest-path distances to the following ten vertices, in order:
 * 7,37,59,82,99,115,133,165,188,197. You should encode the distances as a comma-separated
 * string of integers. So if you find that all ten of these vertices except 115 are at 
 * distance 1000 away from vertex 1 and 115 is 2000 distance away, then your answer should
 * be 1000,1000,1000,1000,1000,2000,1000,1000,1000,1000. Remember the order of reporting 
 * DOES MATTER, and the string should be in the same order in which the above ten vertices
 * are given. Please type your answer in the space provided.
 * 
 * IMPLEMENTATION NOTES: This graph is small enough that the straightforward O(mn) time 
 * implementation of Dijkstra's algorithm should work fine. OPTIONAL: For those of you seeking
 * an additional challenge, try implementing the heap-based version. Note this requires a heap
 * that supports deletions, and you'll probably need to maintain some kind of mapping between
 * vertices and their positions in the heap.
 */
package com.stanford.algorithms.weekfive;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Algorithms: Design and Analysis, Part 1 
 * Programming Question - Week 5
 * @author Felix Garcia Lainez
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        ArrayList<Vertex> vertexArray = readGraphFromFile();
        
        for(int i = 1; i < vertexArray.size(); i++)
        {
            Vertex current = vertexArray.get(i);
            HashMap<Integer, Vertex> vertextHashMap = createVertexHashMapFromArray(vertexArray);
            
            int distance = computeDijkstraAlgorithm(current, vertextHashMap);
            
            System.out.println("Distance to Vertex " + current.getId() + " => " + distance);
        }
    }
    
    /**
     * Process the Dijkstra Algorithm 
     * @param target The vertex to be found
     * @param vertextHashMap The hashmap of vertex
     * @return  The distance from first vertex to target
     */
    private static int computeDijkstraAlgorithm(Vertex target, HashMap<Integer, Vertex> vertextHashMap)
    {
        Vertex currentVertex = vertextHashMap.get(1);
        currentVertex.setExplored(true);
        currentVertex.setDistance(0);
        
        while(vertextHashMap.size() > 0)
        {
            for(Edge e : currentVertex.getEdgesArray())
            {
                int distance = currentVertex.getDistance() + e.getDistance();
                
                Vertex v = vertextHashMap.get(e.getDestinationVertexId());
                
                if(v != null)
                {
                    if(distance < v.getDistance()){
                        v.setDistance(distance);
                    }
                }
            }
            
            
            //SELECT MIN DISTANCE VERTEX
            int minDistance = 0;
            Vertex minDistanceVertex = null;
            
            Iterator<Integer> it = (Iterator<Integer>)vertextHashMap.keySet().iterator();
            
            while(it.hasNext())
            {
                Vertex v = vertextHashMap.get(it.next());
                
                if(!v.isExplored() && (v.getDistance() < minDistance || minDistance == 0))
                {
                    minDistance = v.getDistance();
                    minDistanceVertex = v;
                }
            }
            
            if(minDistanceVertex != null)
            {
                currentVertex = minDistanceVertex;
                currentVertex.setExplored(true);
                vertextHashMap.remove(currentVertex.getId());
            
                //WE HAVE FOUND THE TARGET
                if(currentVertex.getId() == target.getId())
                {
                    currentVertex = minDistanceVertex;
                    break;
                }
            }
            else
            {
                //THE TARGET HAS NOT BEEN FOUND
                currentVertex.setDistance(100000);
                break;
            }
        }
                
        return currentVertex.getDistance();
    }
    
    /**
     * Create a HashMap of Vertex from a given vertex list. 
     * Restarts the state of each Vertex object added to the map
     * @param vertexArray The vertex list
     * @return The HashMap created
     */
    private static HashMap<Integer, Vertex> createVertexHashMapFromArray(ArrayList<Vertex> vertexArray)
    {
        HashMap<Integer, Vertex> vertextHashMap = new HashMap<Integer, Vertex>();
        
        for(Vertex v : vertexArray)
        {
            v.setExplored(false);
            v.setDistance(100000);
            vertextHashMap.put(Integer.valueOf(v.getId()), v);
        }
            
        return vertextHashMap;
    }
    
    /**
     * Reads the Graph used as input for the assignment
     * @return A hash that contains each vertice and the list 
     * of vertices linked to it
     */
    private static ArrayList<Vertex> readGraphFromFile()
    {
        ArrayList<Vertex> vertexArray = new ArrayList<Vertex>();
            
        FileInputStream fstream = null;
        try 
        {
            fstream = new FileInputStream("dijkstraData.txt");
            
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            String line;
            while ((line = br.readLine()) != null)
            {
                // process the line
                StringTokenizer tokens = new StringTokenizer(line);
             
                Vertex vertex = new Vertex(Integer.valueOf(tokens.nextToken()), 100000);
                
                while(tokens.hasMoreTokens())
                {
                    String edgeStr = tokens.nextToken();
                
                    StringTokenizer edgeTokenizer = new StringTokenizer(edgeStr, ",");
                
                    // first item is the token
                    int node = new Integer(edgeTokenizer.nextToken()).intValue();
                    int distance = new Integer(edgeTokenizer.nextToken()).intValue();
                    
                    Edge edge = new Edge(node, distance);           
                    vertex.getEdgesArray().add(edge);
                }
                
                vertexArray.add(vertex);
            }
            
            br.close();
        }catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            try {
                fstream.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return vertexArray;
    }
}

    
/**
 * This class represents an Edge between a source vertex and a destination edge
 */
class Edge
{
    int destinationVertexId;
    int distance;
        
    public Edge(int destinationVertexId, int distance){
        super();
        this.destinationVertexId = destinationVertexId;
        this.distance = distance;
    }

    public int getDestinationVertexId() {
        return destinationVertexId;
    }
    public void setDestinationVertexId(int destinationVertexId) {
        this.destinationVertexId = destinationVertexId;
    }
    public int getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
}
    
/**
 * This class represents a Vertex in the Graph
 */
 class Vertex
 {
    int distance; 
    boolean explored;
  
    int id;
    ArrayList<Edge> edges;
    
    public Vertex(int id, int distance){
        super();
        this.id = id;
        this.distance = distance;
         
        edges = new ArrayList<Edge>();
    }
    
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public ArrayList<Edge> getEdgesArray(){
        return edges;
    }
    public void setEdgesArray(ArrayList<Edge> edges){
        this.edges = edges;
    }
    public int getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public boolean isExplored() {
        return explored;
    }
    public void setExplored(boolean explored) {
        this.explored = explored;
    }
}
