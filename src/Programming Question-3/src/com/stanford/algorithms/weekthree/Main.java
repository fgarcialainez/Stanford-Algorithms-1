/**
 * Programming Question - Week 3, July 2013.
 * 
 * The file contains the adjacency list representation of a simple undirected 
 * graph. There are 200 vertices labeled 1 to 200. The first column in the file
 * represents the vertex label, and the particular row (other entries except the 
 * first column) tells all the vertices that the vertex is adjacent to. So for 
 * example, the 6th row looks like : "6 155 56 52 120......". This just means
 * that the vertex with label 6 is adjacent to (i.e., shares an edge with) the 
 * vertices with labels 155,56,52,120,......,etc
 * 
 * Your task is to code up and run the randomized contraction algorithm for the
 * min cut problem and use it on the above graph to compute the min cut. 
 * (HINT: Note that you'll have to figure out an implementation of edge contractions.
 * Initially, you might want to do this naively, creating a new graph from the old
 * every time there's an edge contraction. But you should also think about more 
 * efficient implementations.) (WARNING: As per the video lectures, please make 
 * sure to run the algorithm many times with different random seeds, and remember
 * the smallest cut that you ever find.) Write your numeric answer in the space 
 * provided. So e.g., if your answer is 5, just type 5 in the space provided.
 */
package com.stanford.algorithms.weekthree;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Algorithms: Design and Analysis, Part 1
 * Programming Question - Week 3
 * @author Felix Garcia Lainez
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        //Read the graph from the text file
        HashMap<Integer, ArrayList<Integer>> originalGraph = readGraphFromFile();
        
        int minimumCut = 0;
        
        for(int i = 0; i < 1000; i++)
        {
            //Copy the original graph in each iteration
            HashMap<Integer, ArrayList<Integer>> copyGraph = copyGraph(originalGraph);
            
            int result = processKargerMinimumCutAlgorithm(copyGraph);
            
            if(minimumCut == 0){
                minimumCut = result;
            }
            else{
                if(result < minimumCut){
                    minimumCut = result;
                }
            }
            
            System.out.println("Partial Result => " + result);
        }
        
        System.out.println("*** Minimum Cut => " + minimumCut + " ***");
    }
    
    /**
     * Process the Karger Minimum Cut Algorithm for a given Graph
     * @param graph The graph to be processed
     * @return The Minimum Cut
     */
    private static int processKargerMinimumCutAlgorithm(HashMap<Integer, ArrayList<Integer>> graph)
    {
        //Iterate until there are only two nodes
        while(graph.size() > 2){
            processKargerAlgorithmStep(graph);
        }
            
        //Return the Minimum Cut (the number of edges of both nodes is the same)
        return graph.get((Integer)graph.keySet().toArray()[0]).size();
    }
    
    /**
     * Process a single step in Karger Minimum Cut Algorithm
     * @param graph The graph to be processed
     */
    private static void processKargerAlgorithmStep(HashMap<Integer, ArrayList<Integer>> graph)
    {
        //Choose randome items
        List<Integer> randomItems = chooseRandomItems(graph);
        
        Integer firstItem = randomItems.get(0);
        Integer secondItem = randomItems.get(1);
        
        ArrayList<Integer> firstItemList = graph.get(firstItem);
        ArrayList<Integer> secondItemList = graph.get(secondItem);
        
        //Add second list items to first list
        firstItemList.addAll(secondItemList);
        
        //Remove second list items
        graph.remove(randomItems.get(1));
        
        //Replace second item appeareances by first item
        Iterator it = graph.keySet().iterator();
        
        while(it.hasNext())
        {
            Integer currentKey = (Integer)it.next();
            
            ArrayList<Integer> currentItemList = graph.get(currentKey);
            
            for(Integer i : currentItemList)
            {
                if(i.intValue() == secondItem.intValue()){
                    currentItemList.set(currentItemList.indexOf(i), firstItem);
                }
            }
        }
        
        //Remove loops
        ArrayList<Integer> itemsToRemove = new ArrayList<Integer>();
        
        for(Integer i : firstItemList)
        {
            if(i.intValue() == firstItem.intValue()){
                itemsToRemove.add(i);
            }
        }
        
        firstItemList.removeAll(itemsToRemove);
    }
    
    /**
     * Select a randome node and a random edge of the list of this node
     * @param graph
     * @return 
     */
    private static List<Integer> chooseRandomItems(HashMap<Integer, ArrayList<Integer>> graph)
    {
        ArrayList<Integer> randomItems = new ArrayList<Integer>();
        
        int nodeIndex = (int)(Math.random() * graph.keySet().size());
        Integer randomNode = (Integer)(graph.keySet().toArray()[nodeIndex]);
        
        int edgeIndex = (int)(Math.random() * graph.get(randomNode).size());
        Integer randomEdge = graph.get(randomNode).get(edgeIndex);
        
        randomItems.add(randomNode);
        randomItems.add(randomEdge);
        
        return randomItems;
    }
    
    /**
     * Return a copy of the graph passed as parameter
     * @param graph The graph to be copied
     * @return The copy of the graph
     */
    private static HashMap<Integer, ArrayList<Integer>> copyGraph(HashMap<Integer, ArrayList<Integer>> graph)
    {
        HashMap<Integer, ArrayList<Integer>> graphCopy = new HashMap<Integer, ArrayList<Integer>>();
        
        Iterator it = graph.keySet().iterator();
        
        while(it.hasNext())
        {
            Integer currentKey = (Integer)it.next();
            ArrayList<Integer> currentItemList = graph.get(currentKey);
            
            graphCopy.put(currentKey, new ArrayList<Integer>(currentItemList));
        }
        
        return graphCopy;
    }
    
    /**
     * Reads the Graph used as input for the assignment
     * @return A hash that contains each vertice and the list 
     * of vertices linked to it
     */
    private static HashMap<Integer, ArrayList<Integer>> readGraphFromFile()
    {
        HashMap<Integer, ArrayList<Integer>> graph = new HashMap<Integer, ArrayList<Integer>>();
            
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream("kargerMinCut.txt");
            
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            String line;
            while ((line = br.readLine()) != null)
            {
                // process the line
                StringTokenizer tokens = new StringTokenizer(line);
                ArrayList<Integer> edges = new ArrayList<Integer>();
                
                // first item is the token
                Integer node = new Integer(tokens.nextToken());
                
                while(tokens.hasMoreTokens()){
                    edges.add(new Integer(tokens.nextToken()));
                }
                
                graph.put(node, edges);
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
        
        return graph;
    }
}
