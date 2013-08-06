/*
 * Programming Question - Week 6, July 2013.
 * 
 * The goal of this problem is to implement the "Median Maintenance" algorithm 
 * (covered in the Week 5 lecture on heap applications). The text file contains
 * a list of the integers from 1 to 10000 in unsorted order; you should treat 
 * this as a stream of numbers, arriving one by one. Letting xi denote the ith 
 * number of the file, the kth median mk is defined as the median of the numbers
 * x1,…,xk. (So, if k is odd, then mk is ((k+1)/2)th smallest number among x1,…,xk;
 * if k is even, then mk is the (k/2)th smallest number among x1,…,xk.)
 * 
 * In the box below you should type the sum of these 10000 medians, modulo 10000
 * (i.e., only the last 4 digits). That is, you should compute 
 * (m1+m2+m3+⋯+m10000)mod10000.
 * 
 * OPTIONAL EXERCISE: Compare the performance achieved by heap-based and search-tree-based
 * implementations of the algorithm.
 */
package com.stanford.algorithms.weeksix;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Algorithms: Design and Analysis, Part 1 
 * Programming Question - Week 6
 * @author Felix Garcia Lainez
 */
public class Question2 
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        ArrayList<Integer> array = readIntegerArrayFromFile();
        
        int sum = 0;
        
        PriorityQueue<Integer> heapLow = new PriorityQueue<Integer>();
        PriorityQueue<Integer> heapHigh = new PriorityQueue<Integer>();
    
        for(Integer x_i : array)
        {
            if(heapLow.size() > 0)
            {
                if(x_i > -(heapLow.peek()))
                {
                    heapHigh.add(x_i);
                }
                else
                {
                    heapLow.add(-x_i);
                }
            }
            else
            {
                heapLow.add(-x_i);
            }

            if(heapLow.size() > heapHigh.size() + 1)
            {
                heapHigh.add(-heapLow.poll());
            }
            else if(heapHigh.size() > heapLow.size())
            {
                heapLow.add(-heapHigh.poll());
            }

            sum += -heapLow.peek();
        }
        
        System.out.println("*** RESULT => " + sum % 10000 + " ***");
    }
    
    /**
     * Reads the Integer array to be used as input for the assignment
     * @return A Long array 
     */
    private static ArrayList<Integer> readIntegerArrayFromFile()
    {
        ArrayList<Integer> integerArray = new ArrayList<Integer>();
            
        FileInputStream fstream = null;
        try 
        {
            fstream = new FileInputStream("Median.txt");
            
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            String line;
            while ((line = br.readLine()) != null){
                integerArray.add(Integer.valueOf(line));
            }
            
            br.close();
        }catch (FileNotFoundException ex) {
            Logger.getLogger(Question1.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
            Logger.getLogger(Question1.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            try {
                fstream.close();
            } catch (IOException ex) {
                Logger.getLogger(Question1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return integerArray;
    }
}