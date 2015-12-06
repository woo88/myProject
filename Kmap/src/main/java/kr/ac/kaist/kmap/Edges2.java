package kr.ac.kaist.kmap;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

/**
 * Created by woo on 2015-12-06.
 */
public class Edges2 {
    private static String baseDir;
    private static String inputFileName;
    private static int splitLineNumber = 100000; // splitting size
    private static ObjectMapper mapper;
    private static int fileNo;

    public static void generateEdges() throws IOException {
        Map<String, Set<String>> instanceToCategoryidSet;
        String inputLine;
        BufferedReader reader;
        int lineNumber = 1;
        int curLineNumber = 0;
        Map<String, Integer> catPairToCount; // {"1-3": 2, "7-5": 1, "2-9": 3}

        // generate Map<"instance", Set<"category id">>
        instanceToCategoryidSet = new DefaultHashMap<>(HashSet.class);
        setInstanceToCategoryidSet(instanceToCategoryidSet);

        fileNo = 1;
        catPairToCount = new HashMap<>();
        reader = new BufferedReader(new FileReader(new File(baseDir + inputFileName)));
        while((inputLine = reader.readLine()) != null) {
            // ignore comment lines.
            if(inputLine.startsWith("#")) {
                System.out.println("\tskip this line: " + inputLine);
                continue;
            }

            // splitting
            if(lineNumber > splitLineNumber) {
                curLineNumber += lineNumber;
                lineNumber = 0;
                System.out.print("\t" + curLineNumber + ": ");
                // store in disk
                mapper = new ObjectMapper();
                mapper.writerWithDefaultPrettyPrinter().writeValue(new File(fileNo + ".json"), catPairToCount);
                catPairToCount = new HashMap<>();
                fileNo++;
            }

            // mapping
            // generate a list of "fromCategoryId-toCategoryId"
            setCatPairToCount(inputLine, instanceToCategoryidSet, catPairToCount); // {"1-3": 2, "7-5": 1, "2-9": 3}

            lineNumber++;
        }

        // shuffling
        // reducing
    }

    private static void setCatPairToCount(String inputLine, Map<String, Set<String>> instanceToCategoryidSet, Map<String, Integer> catPairToCount) {

    }

    private static void setInstanceToCategoryidSet(Map<String, Set<String>> instanceToCategoryidSet) {

    }

    public static void setBaseDir(String baseDir) {
        Edges2.baseDir = baseDir;
    }

    public static void setInputFileName(String inputFileName) {
        Edges2.inputFileName = inputFileName;
    }
}
