package com.stanford.algorithms.weekone;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Algorithms: Design and Analysis, Part 1 
 * Programming Quesion - Week 1
 * @author Felix Garcia Lainez
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        ArrayList<Integer> inputArray = readNumbersArrayFromFile();
        
        long inversions = sortAndCountInversions(inputArray);
        
        System.out.println("Inversions --> " + inversions);
    }
    
    /**
     * Sort and count the number of inversions in a given array
     * @param array The array of integers. This in/out parameter is passed as reference, in 
     * such way that the content is ordered in the execution of this method
     * @return The number of inversions
     */
    private static long sortAndCountInversions(ArrayList<Integer> array)
    {
        long inversions = 0;
        
        if(array != null && array.size() > 1)
        {
            int halfIndex = array.size() / 2;
            ArrayList<Integer> firstHalf = new ArrayList<Integer>(array.subList(0, halfIndex));
            ArrayList<Integer> secondHalf = new ArrayList<Integer>(array.subList(halfIndex, array.size()));
            
            long x = sortAndCountInversions(firstHalf);
            long y = sortAndCountInversions(secondHalf);
            long z = mergeAndCountSplitInversions(firstHalf, secondHalf, array);
            
            inversions = x + y + z;
        }
            
        return inversions;
    }
    
    /**
     * Return the number of split inversions. Also merges the input arrays, returning the content in the out parameter 'array'
     * @param firstHalf The first half of the integer array
     * @param secondHalf The second half of the integer array
     * @param array Output parameter in which is stored the merged array
     * @return The number of split inversions
     */
    private static long mergeAndCountSplitInversions(List<Integer> firstHalf, List<Integer> secondHalf, ArrayList<Integer> array)
    {
        long nInversions = 0;
        ArrayList<Integer> orderedArray = new ArrayList<Integer>();
        
        int i = 0;
        int j = 0;
        int n = firstHalf.size() + secondHalf.size();
        
        for(int k = 0; k < n; k++)
        {
            if(i < firstHalf.size() && j < secondHalf.size())
            {
                if(firstHalf.get(i).intValue() < secondHalf.get(j).intValue())
                {
                    orderedArray.add(firstHalf.get(i));
                    i++;
                }
                else
                {
                    orderedArray.add(secondHalf.get(j));
                    j++;
                      
                    nInversions += firstHalf.size() - i;
                }
            }
            else
            {
                if(i < firstHalf.size())
                {
                    orderedArray.add(firstHalf.get(i));
                    i++;
                }
                else if(j < secondHalf.size())
                {
                    orderedArray.add(secondHalf.get(j));
                    j++;
                }
            }
        }
        
        //RETURN BY REFERENCE THE ORDERED ARRAY
        array.clear();
        array.addAll(orderedArray);
            
        return nInversions;
    }
    
    /**
     * Reads the list of numbers used as input for the assignment
     * @return An array of integer
     */
    private static ArrayList<Integer> readNumbersArrayFromFile()
    {
        ArrayList<Integer> numbersArray = new ArrayList<Integer>();
            
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream("IntegerArray.txt");
            
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            String line;
            while ((line = br.readLine()) != null){
                // process the line.
                numbersArray.add(new Integer(line));
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
        
        return numbersArray;
    }
}
