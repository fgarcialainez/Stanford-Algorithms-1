/*
 * Programming Question - Week 6, July 2013.
 * 
 * The goal of this problem is to implement a variant of the 2-SUM algorithm 
 * (covered in the Week 6 lecture on hash table applications).
 *
 * The file contains 1 million integers, both positive and negative (there might
 * be some repetitions!).This is your array of integers, with the ith row of the
 * file specifying the ith entry of the array.
 * 
 * Your task is to compute the number of target values t in the interval [-10000,
 * 10000] (inclusive) such that there are distinct numbers x,y in the input file that 
 * satisfy x+y=t. (NOTE: ensuring distinctness requires a one-line addition to the 
 * algorithm from lecture.)
 * 
 * Write your numeric answer (an integer between 0 and 20001) in the space provided.
 * 
 * OPTIONAL CHALLENGE: If this problem is too easy for you, try implementing your own
 * hash table for it. For example, you could compare performance under the chaining 
 * and open addressing approaches to resolving collisions. 
 */
package com.stanford.algorithms.weeksix;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Algorithms: Design and Analysis, Part 1 
 * Programming Question - Week 6
 * @author Felix Garcia Lainez
 */
public class Question1 
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        ArrayList<Long> array = readNumbersArrayFromFile();
  
        //SORT THE ARRAY
        Collections.sort(array);
        
        //WE DO USING ARRAYS INSTEAD OF NATIVE JAVA HASHMAP IN ORDER TO SPEED UP THE COMPUTATION
        int counter = 0;
        
        for(int sum = -10000; sum <= 10000; sum++)
        {
            int start = 0;
            int end = array.size() - 1;
	
            boolean found = false;
	
            while(!found && start < end) 
            {
		if(array.get(start) + array.get(end) == sum)
                {
                    if(array.get(start).longValue() != array.get(end).longValue()){
                        found = true;
                    }
                }
		else if(array.get(start) + array.get(end) > sum){
                    end--;
                }
		else if(array.get(start) + array.get(end) < sum){
                    start++;
                }
            }
	
            if(found){
                counter++;
            }
        }
        
        System.out.println("*** COUNTER => " + counter + " ***");
    }
    
    
    /**
     * Reads the Long array to be used as input for the assignment
     * @return A Long array 
     */
    private static ArrayList<Long> readNumbersArrayFromFile()
    {
        ArrayList<Long> longArray = new ArrayList<Long>();
            
        FileInputStream fstream = null;
        try 
        {
            fstream = new FileInputStream("algo1-programming_prob-2sum.txt");
            
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            String line;
            while ((line = br.readLine()) != null){
                longArray.add(Long.valueOf(line));
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
        
        return longArray;
    }
}