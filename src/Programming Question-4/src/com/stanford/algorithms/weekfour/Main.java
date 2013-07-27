/**
 * Programming Question - Week 4
 * 
 * The file contains the edges of a directed graph. Vertices are labeled as positive 
 * integers from 1 to 875714. Every row indicates an edge, the vertex label in first 
 * column is the tail and the vertex label in second column is the head (recall the 
 * graph is directed, and the edges are directed from the first column vertex to the 
 * second column vertex). So for example, the 11th row looks liks : "2 47646". This 
 * just means that the vertex with label 2 has an outgoing edge to the vertex with 
 * label 47646.
 * 
 * Your task is to code up the algorithm from the video lectures for computing strongly 
 * connected components (SCCs), and to run this algorithm on the given graph.
 * 
 * Output Format: You should output the sizes of the 5 largest SCCs in the given graph,
 * in decreasing order of sizes, separated by commas (avoid any spaces). So if your 
 * algorithm computes the sizes of the five largest SCCs to be 500, 400, 300, 200 and 
 * 100, then your answer should be "500,400,300,200,100". If your algorithm finds less
 * than 5 SCCs, then write 0 for the remaining terms. Thus, if your algorithm computes 
 * only 3 SCCs whose sizes are 400, 300, and 100, then your answer should be "400,300,100,0,0".
 * 
 * WARNING: This is the most challenging programming assignment of the course. Because of the 
 * size of the graph you may have to manage memory carefully. The best way to do this depends
 * on your programming language and environment, and we strongly suggest that you exchange tips
 * for doing this on the discussion forums.
 */
package com.stanford.algorithms.weekfour;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Algorithms: Design and Analysis, Part 1
 * Programming Question - Week 4
 * @author Felix Garcia Lainez
 */
public class Main 
{
    //GLOBAL VARIABLES
    private static ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
    
    private static HashMap<Integer, ArrayList<Integer[]>> adjacencyMap = new HashMap<Integer, ArrayList<Integer[]>>();
    private static HashMap<Integer, ArrayList<Integer[]>> reverseAdjacencyMap = new HashMap<Integer, ArrayList<Integer[]>>();
    
    private static HashMap<Integer, Boolean> exploredMap = new HashMap<Integer, Boolean>();
    
    private static Integer s = new Integer(0);
    private static Integer t = new Integer(0);
    
    private static HashMap<Integer, Integer> finishingMap = new HashMap<Integer, Integer>();
    private static HashMap<Integer, Integer> leaderMap = new HashMap<Integer, Integer>();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        //LOAD GRAPH
        loadGraphFromFile();

        System.out.println("Graph Loaded Successfully");
        
        //CALCULATE NUMBER OF NODES AND CREATE LABELING ARRAY
        int numberOfNodes = getNumberOfNodes(edges);
        
        System.out.println("Number of Nodes => " + numberOfNodes);
        
        ArrayList<Integer> labeling = new ArrayList<Integer>();
        
        for(int i = numberOfNodes; i >= 0; i--){
            labeling.add(new Integer(i));
        }
        
        //DO REVERSE DFSLOOP
        processDFSLoop(labeling, true);
        
        System.out.println("Reverse DFSLoop Done");
        
        
        //DO FORWARD DFSLOOP
        HashMap<Integer, Integer> inverseFinishingMap = new HashMap<Integer, Integer>();
        
        Iterator<Integer> itFinishingMap = (Iterator<Integer>)finishingMap.keySet().iterator();
        
        while(itFinishingMap.hasNext())
        {
            Integer key = itFinishingMap.next();
            Integer value = finishingMap.get(key);
            inverseFinishingMap.put(value, key);
        }
        
        ArrayList<Integer> inverseLabeling = new ArrayList<Integer>();
        
        for(int i = numberOfNodes; i >= 0; i--){
            inverseLabeling.add(inverseFinishingMap.get(i));
        }
        
        resetCurrentState();
        
        processDFSLoop(inverseLabeling, false);
        
        System.out.println("Forward DFSLoop Done");
        
        
        //COMPUTE THE RESULTS
        HashMap<Integer, ArrayList<Integer>> sccsMap = new HashMap<Integer, ArrayList<Integer>>();
        
        Iterator<Integer> itLeaderMap = (Iterator<Integer>)leaderMap.keySet().iterator();
        
        while(itLeaderMap.hasNext())
        {
            Integer key = itLeaderMap.next();
            
            if(sccsMap.get(leaderMap.get(key)) == null)
            {
                ArrayList<Integer> sccItems = new ArrayList<Integer>();
                sccItems.add(key);
                
                sccsMap.put(leaderMap.get(key), sccItems);
            }
            else{
                sccsMap.get(leaderMap.get(key)).add(key);
            }
        }
        
        
        //PREPARE AND PRINT RESULTS
        System.out.println("Printing Results...");
        System.out.println();
        
        ArrayList<Integer> sccsSizeOrderedArray = new ArrayList<Integer>();
        
        Iterator<Integer> itSCCSMap = (Iterator<Integer>)sccsMap.keySet().iterator();
        
        while(itSCCSMap.hasNext())
        {
            Integer key = itSCCSMap.next();
            
            if(key != null){
                sccsSizeOrderedArray.add(sccsMap.get(key).size());
            }
        }
        
        Collections.sort(sccsSizeOrderedArray, Collections.reverseOrder());
        
        System.out.println("Result => ");
        
        for(int i = 0; i < sccsSizeOrderedArray.size(); i++){
            System.out.println(sccsSizeOrderedArray.get(i));
        }
    }
    
    /**
     * Process the main DFD Loop
     * @param labeling Labelling array
     * @param reversed If the DFS is reversed or not
     */
    private static void processDFSLoop(ArrayList<Integer> labeling, boolean reversed)
    {
        for(Integer i : labeling)
        {
            if(exploredMap.get(i) == null)
            {
                s = i;
                processDFS(i, reversed);
            }
        }
    }
    
    /**
     * Process DFS of a single item in the main DFS loop
     * @param start The starting item
     * @param reversed If the DFS is reversed or not
     */
    private static void processDFS(Integer start, boolean reversed)
    {
        HashMap<Integer, ArrayList<Integer[]>> adjacency;
        
        if(reversed){
            adjacency = reverseAdjacencyMap;
        }
        else{
            adjacency = adjacencyMap;
        }
        
        //ITERATE OVER THE EDGES
        ArrayList<Integer[]> stack = new ArrayList<Integer[]>();
        
        Integer[] items = {start, 1};
        stack.add(items);
        
        while(stack.size() > 0)
        {
            Integer[] currentItem = stack.remove(stack.size() - 1);
            Integer current = currentItem[0];
            Integer phase = currentItem[1];
            
            if(phase.intValue() == 1)
            {
                exploredMap.put(current, Boolean.valueOf(true));
                leaderMap.put(current, s);
                boolean edgeFound = false;
                
                if(adjacency.get(current) != null)
                {
                    for(Integer[] edge : adjacency.get(current))    
                    {
                        if(exploredMap.get(edge[1]) == null)
                        {
                            Integer[] newItem = {current, 1};
                            Integer[] newItemTwo = {edge[1], 1};
                        
                            stack.add(newItem);
                            stack.add(newItemTwo);
                        
                            edgeFound = true;
                            break;
                        }
                    }
                }
                
                if(!edgeFound)
                {
                    Integer[] newItem = {current, 2};
                    stack.add(newItem);
                }
            }
            else if(phase.intValue() == 2)
            {
                t += 1;
                finishingMap.put(current, t);
            }
        }
    }
    
    /**
     * Reset the current state
     */
    private static void resetCurrentState()
    {
        s = new Integer(0);
        t = new Integer(0);
        
        finishingMap.clear();
        leaderMap.clear();
        exploredMap.clear();
    }
    
    /**
     * Calculate the number of nodes from the edges array
     * @param edges The edges array
     * @return The number of nodes
     */
    private static int getNumberOfNodes(ArrayList<Integer[]> edges)
    {
        int maxValue = 0;
        
        for(Integer[] edge : edges)
        {
            if(edge[0].intValue() > maxValue){
                maxValue = edge[0].intValue();
            }
            
            if(edge[1].intValue() > maxValue){
                maxValue = edge[1].intValue();
            }
        }
        
        return maxValue;
    }
    
    /**
     * Reads the Graph adjacency and reverse adjacency 
     * hashes used as input for the assignment
     */
    private static void loadGraphFromFile()
    {
        FileInputStream fstream = null;
        try 
        {
            fstream = new FileInputStream("SCC.txt");
            
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            //FIRST READ THE EDGES
            String line;
            while ((line = br.readLine()) != null)
            {
                // process the line
                StringTokenizer tokens = new StringTokenizer(line);
                
                Integer[] fields = {Integer.parseInt(tokens.nextToken()), Integer.parseInt(tokens.nextToken())};
               
                edges.add(fields);
            }
            
            //CREATE ADJACENCIES AND REVERSE ADJACENCIES
            for(Integer[] edge : edges)
            {
                //ADJACENCIES
                ArrayList<Integer[]> adjArray = new ArrayList<Integer[]>();
                
                if(adjacencyMap.get(edge[0]) != null){
                    adjArray.addAll(adjacencyMap.get(edge[0]));
                }
                
                adjArray.add(edge);
                
                //REVERSE ADJACENCIES
                ArrayList<Integer[]> reverseAdjArray = new ArrayList<Integer[]>();
                
                if(reverseAdjacencyMap.get(edge[1]) != null){
                    reverseAdjArray.addAll(reverseAdjacencyMap.get(edge[1]));
                }
                
                Integer[] fields = {edge[1], edge[0]};                
                reverseAdjArray.add(fields);
                
                //SET NEW VALUES IN ADJACENCY AND REVERSE ADJACENCY MAPS
                adjacencyMap.put(edge[0], adjArray);
                reverseAdjacencyMap.put(edge[1], reverseAdjArray);
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
    }
}
