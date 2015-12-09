package kr.ac.kaist.kmap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by woo on 2015-12-07.
 */
public class Edges3 {
    private static String baseDir = null;
    private static String inputFileName = null;
    private static String outFileName = "category-links_en.txt";
    private static String sortedOutFileName = "sorted_category-links_en.txt";
    private static String reducedOutFileName = "result_category-links_en.txt";

    public static void setBaseDir(String baseDir) {
        Edges3.baseDir = baseDir;
    }

    public static void setInputFileName(String inputFileName) {
        Edges3.inputFileName = inputFileName;
    }

    public static void generateEdges() throws IOException {
        // setPageLinkToCategoryLink()
        setPageLinkToCategoryLink();

        // external sort
        sortCategoryLinks();

        // reducing
        countCategoryLinks();
    }

    private static void countCategoryLinks() throws IOException {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String inputLine = null;
        String prevLine = null;
        int count = 1;
        String[] strArr = null;
        int sharedInstanceNumber = 0;
        Map<String, Set<String>> categoryidToInstances = null;
        int total = 0;

        categoryidToInstances = setCategoryidToInstances();

        reader = new BufferedReader(new FileReader(new File(baseDir + "res/" + sortedOutFileName)));
        writer = new BufferedWriter(new FileWriter(new File(baseDir + "res/" + reducedOutFileName)));
        prevLine = reader.readLine();
        while((inputLine = reader.readLine()) != null) {
            if(Objects.equals(inputLine, prevLine)) {
                count++;
            } else {
                strArr = prevLine.split("-", 2);
                Set<String> fromInstances = categoryidToInstances.get(strArr[0]);
                Set<String> toInstances = categoryidToInstances.get(strArr[1]);
                fromInstances.retainAll(toInstances);
                sharedInstanceNumber = fromInstances.size();
                if(sharedInstanceNumber != 0) {
                    System.out.print(prevLine + ":" + sharedInstanceNumber + ", ");
                }
                total = count + sharedInstanceNumber;
//                writer.write(strArr[0] + "," + strArr[1] + "," + count + "," + sharedInstanceNumber + "," + total);
                writer.write(strArr[0] + " " + strArr[1] + " " + total + " " + count + " " + sharedInstanceNumber);
                writer.newLine();
                count = 1;
            }
            prevLine = inputLine;
        }
        reader.close();
        writer.close();
    }

    private static Map<String, Set<String>> setCategoryidToInstances() throws IOException {
        Map<String, Set<String>> categoryidToInstances = null;
        File f = null;
        ObjectMapper mapper = null;

        f = new File(baseDir + "res/categoryidToInstances.json");
        if(f.isFile()) {
            System.out.println("start reading " + baseDir + "res/categoryidToInstances.json");
            mapper = new ObjectMapper();
            // read JSON from a file
            try {
                categoryidToInstances = mapper.readValue(
                        f,
                        new TypeReference<Map<String, Set<String>>>() {
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return categoryidToInstances;
        }

        Category cat;
        Nodes nod;

        cat = new Category();
        try {
            cat.setMap("category");
//            cat.setMap("instance");
        } catch (IOException e) {
            e.printStackTrace();
        }

        nod = new Nodes();
        try {
            nod.setNodeIdMap();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("start generating Map<\"category id\", Set<\"instances\">>");
        categoryidToInstances = new DefaultHashMap<>(HashSet.class);
        for(String category : cat.getKeySet()) {
            String id = nod.getId(category);
            for(String instance : cat.getValueSet(category)) {
                categoryidToInstances.get(id).add(instance);
            }
        }

        mapper = new ObjectMapper();
        // write JSON to a file
        try {
            mapper.writeValue(f, categoryidToInstances);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\tMap size: " + categoryidToInstances.size());
        System.out.println("\tfinished");
        return categoryidToInstances;
    }

    private static void sortCategoryLinks() throws IOException {
        ExternalSort sort = new ExternalSort();
        boolean verbose = true;
        boolean distinct = false;
        int maxtmpfiles = 1024;
        Charset cs = Charset.defaultCharset();
        String inputfile = null, outputfile = null;
        File tempFileStore = null;
        boolean usegzip = false;
        int headersize = 0;

        inputfile = baseDir + "res/" + outFileName;
        outputfile = baseDir + "res/" + sortedOutFileName;
        tempFileStore = new File(baseDir + "res/tmp/");

        Comparator<String> comparator = sort.defaultcomparator;
        List<File> l = sort.sortInBatch(new File(inputfile), comparator,
                maxtmpfiles, cs, tempFileStore, distinct, headersize,
                usegzip);
        if (verbose)
            System.out.println("created " + l.size() + " tmp files");
        sort.mergeSortedFiles(l, new File(outputfile), comparator, cs,
                distinct, false, usegzip);
    }

    private static void setPageLinkToCategoryLink() throws IOException {
        BufferedReader reader = null;
        String inputLine = null;
        BufferedWriter writer = null;

        // generate Map<"instance", Set<"category id">>
        Map<String, Set<String>> instanceToCategoryidSet = setInstanceToCategoryidSet();

        System.out.println("start reading " + inputFileName);
        reader = new BufferedReader(new FileReader(new File(baseDir + inputFileName)));
        writer = new BufferedWriter(new FileWriter(new File(baseDir + "res/" + outFileName)));
        while((inputLine = reader.readLine()) != null) {
            // ignore comment lines.
            if(inputLine.startsWith("#")) {
                System.out.println("\tskip this line: " + inputLine);
                continue;
            }

            String[] strArr = inputLine.split(" ", 4);
            String from = strArr[0];
            String to = strArr[2];
            String txt = "/File:";
            int idx = to.indexOf(txt);
            if(idx > -1) { // skip if the resource is a file
                continue;
            } else { // remove prefix
                to = App.removePrefix(to, "/resource/");
            }
            from = App.removePrefix(from, "/resource/");

            if(instanceToCategoryidSet.get(from) == null || instanceToCategoryidSet.get(to) == null) continue;

            for(String fromCategoryId : instanceToCategoryidSet.get(from)) {
                for(String toCategoryId : instanceToCategoryidSet.get(to)) {
                    // skip if FROM and TO are in the same category
                    if(Objects.equals(fromCategoryId, toCategoryId)) continue;

                    writer.write(fromCategoryId + "-" + toCategoryId); writer.newLine();
                }
            }
        }
        System.out.println("\tfinished");
        writer.close();
        reader.close();
    }

    private static Map<String, Set<String>> setInstanceToCategoryidSet() {
        Map<String, Set<String>> instanceToCategoryidSet = null;
        Category cat = null;
        Nodes nod = null;

        if(App.isFile("instanceToCategoryidSet.json")) {
            instanceToCategoryidSet = App.readJson("instanceToCategoryidSet.json");
            return instanceToCategoryidSet;
        }

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

        App.writeJson(instanceToCategoryidSet, "instanceToCategoryidSet.json");

        System.out.println("\tMap size: " + instanceToCategoryidSet.size());
        System.out.println("\tfinished");

        return instanceToCategoryidSet;
    }
}
