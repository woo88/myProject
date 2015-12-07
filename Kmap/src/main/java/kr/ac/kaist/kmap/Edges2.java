package kr.ac.kaist.kmap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

/**
 * Created by woo on 2015-12-06.
 */
public class Edges2 {
    private static String baseDir;
    private static String inputFileName;
    private static int lineNumber = 1;
    private static int splitLineNumber = 500000; // splitting size // 162,244,034 page-links_en.nt
    private static ObjectMapper mapper;
    private static int fileNo;

    public static void setBaseDir(String baseDir) {
        Edges2.baseDir = baseDir;
    }

    public static void setInputFileName(String inputFileName) {
        Edges2.inputFileName = inputFileName;
    }

    public static void generateEdges() throws IOException {
        // splitting and mapping
        splitAndMap();

        // shuffling
//        shuffle(baseDir + "mapReduce/");
        shuffle2(baseDir + "mapReduce2/");

        // reducing
//        reduce(catPairToCount, tmp);
    }

    private static void splitAndMap() {
        Map<String, Set<String>> instanceToCategoryidSet;
        String inputLine;
        BufferedReader reader;
        BufferedWriter out;
//        int lineNumber = 1;
        int curLineNumber = 0;
        Map<String, Integer> catPairToCount; // {"1-3": 2, "7-5": 1, "2-9": 3}

        // generate Map<"instance", Set<"category id">>
        instanceToCategoryidSet = setInstanceToCategoryidSet();
//        System.out.println(instanceToCategoryidSet.size());

        System.out.println("start reading " + inputFileName);
        System.out.println("start splitting and mapping");
        fileNo = 1;
        catPairToCount = new HashMap<>();
        try {
            reader = new BufferedReader(new FileReader(new File(baseDir + inputFileName)));
            while((inputLine = reader.readLine()) != null) {
                // ignore comment lines.
                if(inputLine.startsWith("#")) {
                    System.out.println("\tskip this line: " + inputLine);
                    continue;
                }

                // splitting
                if(lineNumber >= splitLineNumber) {
                    curLineNumber += lineNumber;
                    lineNumber = 0;
                    System.out.print("\t" + curLineNumber + ": ");

                    // store in disk
//                    mapper = new ObjectMapper();
////                    mapper.writerWithDefaultPrettyPrinter().writeValue(new File(baseDir + "mapReduce/" + fileNo + ".json"), catPairToCount);
//                    mapper.writeValue(new File(baseDir + "mapReduce/" + fileNo + ".json"), catPairToCount);
//                    System.out.print(fileNo + ".json, ");
//                    catPairToCount = new HashMap<>();
//                    fileNo++;

                    out = new BufferedWriter(new FileWriter(new File(baseDir + "mapReduce2/"
                    + fileNo + ".txt")));
                    for(String catPair : catPairToCount.keySet()) {
                        out.write(catPair + "," + catPairToCount.get(catPair)); out.newLine();
                    }
                    out.close();
                    catPairToCount = new HashMap<>();
                    fileNo++;
                }

                // mapping
                // generate a list of "fromCategoryId-toCategoryId"
                catPairToCount = setCatPairToCount(inputLine, instanceToCategoryidSet, catPairToCount); // {"1-3": 2, "7-5": 1, "2-9": 3}

//                lineNumber++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
        System.out.println("\tfinished");
    }

    private static Map<String, Set<String>> setInstanceToCategoryidSet() {
        Map<String, Set<String>> instanceToCategoryidSet = null;
        File f;
        ObjectMapper mapper;

        f = new File(baseDir + "res/instanceToCategoryidSet.json");
        if(f.isFile()) {
            System.out.println("start reading " + baseDir + "res/instanceToCategoryidSet.json");
            mapper = new ObjectMapper();
            // read JSON from a file
            try {
                instanceToCategoryidSet = mapper.readValue(
                        f,
                        new TypeReference<Map<String, Set<String>>>() {
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return instanceToCategoryidSet;
        }

        Category cat;
        Nodes nod;

        cat = new Category();
        try {
            cat.setMap("instance");
        } catch (IOException e) {
            e.printStackTrace();
        }

        nod = new Nodes();
        try {
            nod.setNodeIdMap();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("start generating Map<\"instance\", Set<\"category id\">>");
        instanceToCategoryidSet = new DefaultHashMap<>(HashSet.class);
        for(String instance : cat.getKeySet()) {
            for(String category : cat.getValueSet(instance)) {
                instanceToCategoryidSet.get(instance).add(nod.getId(category));
            }
        }

        mapper = new ObjectMapper();
        // write JSON to a file
        try {
            mapper.writeValue(f, instanceToCategoryidSet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\tMap size: " + instanceToCategoryidSet.size());
        System.out.println("\tfinished");
        return instanceToCategoryidSet;
    }

    private static Map<String, Integer> setCatPairToCount(String inputLine, Map<String, Set<String>> instanceToCategoryidSet, Map<String, Integer> catPairToCount) {
        String[] strArr;
        String from;
        String to;
        String txt; // for checking prefix
        int idx; // index of the prefix
        String key;
        Integer count;

        strArr = inputLine.split(" ", 4);
        from = strArr[0];
        to = strArr[2];
        txt = "/File:";
        idx = to.indexOf(txt);
        if(idx > -1) { // skip if the resource is a file
            return catPairToCount;
        } else { // remove prefix
            to = removePrefix(to, "/resource/");
        }
        from = removePrefix(from, "/resource/");

        if(instanceToCategoryidSet.get(from) == null | instanceToCategoryidSet.get(to) == null) return catPairToCount;

        for(String fromCategoryId : instanceToCategoryidSet.get(from)) {
            for(String toCategoryId : instanceToCategoryidSet.get(to)) {
                // skip if FROM and TO are in the same category
                if(Objects.equals(fromCategoryId, toCategoryId)) continue;

                key = fromCategoryId + "-" + toCategoryId;

                count = catPairToCount.get(key);
                if(count == null) {
                    catPairToCount.put(key, 1);
                    lineNumber++;
                } else {
                    catPairToCount.put(key, count + 1);
                }
            }
        }
        return catPairToCount;
    }

    private static String removePrefix(String s, String prefix) {
        int idx;

        idx = s.indexOf(prefix);
        if(idx > -1) {
            s = s.substring(idx + prefix.length(), s.length()-1);
        } else {
            System.out.println("[ERRO] there is no prefix /resource/");
            System.out.println(s);
        }
        return s;
    }

    private static void shuffle(String dir) {
        File dest;
        File[] fileList;
        ObjectMapper mapper;
        Map<String, Integer> catPairToCount;
        Map<String, Integer> tmp = null;
        BufferedWriter out = null;

        dest = new File(dir);
        fileList = dest.listFiles();
        catPairToCount = new HashMap<>();
        for(File file : fileList) {
            System.out.print(file.getName() + ", ");
            mapper = new ObjectMapper();
            // read JSON from a file
            try {
                tmp = mapper.readValue(
                        file,
                        new TypeReference<Map<String, Integer>>() {
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
            catPairToCount = reduce(catPairToCount, tmp);
        }

        try {
            out = new BufferedWriter(new FileWriter(new File(baseDir + "res/page-links_en.txt")));
            for(String catPair : catPairToCount.keySet()) {
                out.write(catPair + "," + catPairToCount.get(catPair)); out.newLine();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void shuffle2(String dir) {
        File dest;
        File[] fileList;
        BufferedReader reader;
        String inputLine;
        String[] strArr;
        Map<String, Integer> catPairToCount;
        Map<String, Integer> tmp = null;
        BufferedWriter out = null;

        dest = new File(dir);
        fileList = dest.listFiles();
        catPairToCount = new HashMap<>();
        for(File file : fileList) {
            System.out.print(file.getName() + ", ");
            try {
                reader = new BufferedReader(new FileReader(file));
                while((inputLine = reader.readLine()) != null) {
                    strArr = inputLine.split(",", 2);
                    Integer totalCount = catPairToCount.get(strArr[0]);
                    if(totalCount == null) {
                        catPairToCount.put(strArr[0], Integer.parseInt(strArr[1]));
                    } else {
                        catPairToCount.put(strArr[0], totalCount + Integer.parseInt(strArr[1]));
                    }

                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
        System.out.println("finish reducing");

        try {
            out = new BufferedWriter(new FileWriter(new File(baseDir + "res/page-links_en.txt")));
            for(String catPair : catPairToCount.keySet()) {
                out.write(catPair + "," + catPairToCount.get(catPair)); out.newLine();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Map<String, Integer> reduce(Map<String, Integer> catPairToCount, Map<String, Integer> tmp) {
        for(String catPair : tmp.keySet()) {
            Integer totalCount = catPairToCount.get(catPair);
            Integer count = tmp.get(catPair);
            if(totalCount == null) {
                catPairToCount.put(catPair, count);
            } else {
                catPairToCount.put(catPair, totalCount + count);
            }
        }
        return catPairToCount;
    }
}
