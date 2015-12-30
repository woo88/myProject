package kr.ac.kaist.kmap;

import java.io.*;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Created by woo on 2015-12-30.
 */
public class WordCounter {
    public static void main(String[ ] args)
    {
        TreeMap<String, Integer> frequencyData = new TreeMap<String, Integer>( );

        readWordFile(frequencyData, "words.txt");
//        printAllCounts(frequencyData);
    }

    public static int getCount
            (String word, TreeMap<String, Integer> frequencyData)
    {
        if (frequencyData.containsKey(word))
        {  // The word has occurred before, so get its count from the map
            return frequencyData.get(word); // Auto-unboxed
        }
        else
        {  // No occurrences of this word
            return 0;
        }
    }

    public static void printAllCounts(TreeMap<String, Integer> frequencyData)
    {
        System.out.println("-----------------------------------------------");
        System.out.println("    Occurrences    Word");

        for(String word : frequencyData.keySet( ))
        {
            System.out.printf("%15d    %s\n", frequencyData.get(word), word);
        }

        System.out.println("-----------------------------------------------");
    }

    public static void writeAllCounts(TreeMap<String, Integer> frequencyData, String output)
    {
        BufferedWriter writer;

        System.out.println("Start writing occurrences of word");
        try {
            writer = new BufferedWriter(new FileWriter(new File(output)));
        } catch (IOException e) {
            System.err.println(e);
            return;
        }
        for(String word : frequencyData.keySet( ))
        {
            try {
                writer.write(word + " " + frequencyData.get(word)); writer.newLine();
            } catch (IOException e) {
                System.err.println(e);
                return;
            }
        }

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done");
        System.out.println();
    }

    public static void readWordFile(TreeMap<String, Integer> frequencyData, String fileName)
    {
        Scanner wordFile;
        String word;     // A word read from the file
        Integer count;   // The number of occurrences of the word

        int lineNumber;
        int totalLineNumber;

        try
        {
            wordFile = new Scanner(new FileReader(fileName));
        }
        catch (FileNotFoundException e)
        {
            System.err.println(e);
            return;
        }

        System.out.println("Start reading: " + fileName);
        lineNumber = 0;
        totalLineNumber = 0;

        while (wordFile.hasNext( ))
        {
            // check progress
            if (lineNumber >= 500000) {
                totalLineNumber += lineNumber;
                lineNumber = 0;
                System.out.print(totalLineNumber + ", ");
            }
            lineNumber++;

            // Read the next word and get rid of the end-of-line marker if needed:
            word = wordFile.next( );

            // Get the current count of this word, add one, and then store the new count:
            count = getCount(word, frequencyData) + 1;
            frequencyData.put(word, count);
        }

        System.out.println("Done");
        System.out.println();
    }
}
