package kr.ac.kaist.kmap;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

/**
 * Created by Woo on 2015. 12. 4..
 */
public class Edges {
    private static String filename;
    private static int i;
    private Map<String, Integer> edgeSizeMap;
    private static final int split_line_number = 100000; // 162,244,034 page-links_en.nt
    private static int edge_id;

    public Edges(String filename) {
        this.filename = filename;
    }

    public void putEdges(String key) throws IOException {
        HashMap<String, Set<String>> pageLinkMap;
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        String inputLine;
        Map<String, Set<String>> page_categoryId;
        ArrayList<Map<String, Object>> edges;
        HashMap resultMap;
//        HashMap<String, String> pageIdMap;
        int line_number = 1;
        int curLineNumber = 0;
        ObjectOutputStream oos;
        ObjectMapper mapper;
        int fileNo = 1;

        resultMap = new HashMap();
//        App.putTimeSlot(resultMap);

//        page_categoryId = setPageCategoryIdMap(pageIdMap);
        page_categoryId = setPageCategoryIdMap();

        System.out.println("start reading " + App.filename_page_links);

        pageLinkMap = new DefaultHashMap<>(HashSet.class);
        edge_id = 1;
//        pageIdMap = new HashMap<>();
        i = 1;
//        Disk.makeDirectory("page_links");
        while((inputLine = reader.readLine()) != null) {
            // Ignore comment lines.
            if(inputLine.startsWith("#")) {
                System.out.println("\tskip this line: " + inputLine);
                continue;
            }

            if(line_number < split_line_number) {
//                setPageLinkMap(pageLinkMap, inputLine, pageIdMap);
                setPageLinkMap(pageLinkMap, inputLine);
            } else {
                curLineNumber += line_number;
                System.out.print("\t" + curLineNumber + ": ");
                // setEdgeSizeMap()
                edges = setEdgeSizeMap(pageLinkMap, page_categoryId);
                System.out.print("edge list, ");
                // store in disk
                resultMap.put(key, edges);
                mapper = new ObjectMapper();
                mapper.writerWithDefaultPrettyPrinter().writeValue(new File(fileNo + ".json"), resultMap);
                fileNo++;
                resultMap = new HashMap();
                System.out.print("to disk.");
                // initialize pageLinkMap
                pageLinkMap = new DefaultHashMap<>(HashSet.class);
                // initialize line_number
                line_number = 0;
                // setPageLinkMap()
                setPageLinkMap(pageLinkMap, inputLine);
            }

            line_number++;
        }
        reader.close();
//        System.out.println("\tnumber of instances including interlanguage information: " + map.size());
        System.out.println();
        System.out.println("\tfinished");

        // shuffling
//        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("1.tmp"));
//        try {
//            List<Edges> list = (List<Edges>) ois.readObject();
//            for(Edges e : list) {
//                System.out.println(e.toString());
//            }
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        ois.close();
        // reducing

//        edges = setEdgeSizeMap(pageLinkMap, page_categoryId);
//        resultMap.put(key, edges);

//        mapper = new ObjectMapper();
//        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("edges.json"), resultMap);
    }

    private static Map<String, Set<String>> setPageCategoryIdMap() throws IOException {
        Category cat;
        Nodes nod;
        Map<String, Set<String>> page_categoryId;

        cat = new Category();
        cat.setMap("instance");

        nod = new Nodes();
        nod.setNodeIdMap();

        System.out.println("start generating Map<\"page\", Set(\"category id\")>");
        page_categoryId = new DefaultHashMap<>(HashSet.class);
        for(String instance : cat.getKeySet()) {
            for(String category : cat.getValueSet(instance)) {
//                page_categoryId.get(pageIdMap.get(instance)).add(nod.getId(category));
                page_categoryId.get(instance).add(nod.getId(category));
            }
        }

        System.out.println("\tMap size: " + page_categoryId.size());
        System.out.println("\tfinished");
        return page_categoryId;
    }

    private ArrayList<Map<String, Object>> setEdgeSizeMap(HashMap<String, Set<String>> pageLinkMap, Map<String, Set<String>> page_categoryId) throws IOException {
        String s; // category ID for FROM plus category ID for TO
        Set<String> fromCategorySet;
        Set<String> toCategorySet;

        edgeSizeMap= new HashMap<>();
        for(String from : pageLinkMap.keySet()) {
            fromCategorySet = page_categoryId.get(from);
            for(String to : pageLinkMap.get(from)) {
                toCategorySet = page_categoryId.get(to);
                for(String fromCategory : fromCategorySet) {
                    for(String toCategory : toCategorySet) {
                        // skip if FROM and TO are the same category
                        if(Objects.equals(fromCategory, toCategory)) continue;

                        s = fromCategory + "-" + toCategory;
                        Integer count = edgeSizeMap.get(s);
                        if(count == null) {
                            edgeSizeMap.put(s, 1);
                        } else {
                            edgeSizeMap.put(s, count + 1);
                        }
                    }
                }
            }
        }
        pageLinkMap = null;

        //

        ArrayList<Map<String, Object>> edges = new ArrayList<>();
        String[] strArr;
        Map<String, Object> edge;
        Map<String, Integer> variables;
        int share_point;
        int edgeSize;
        for(String from_to : edgeSizeMap.keySet()) {
            edge = new HashMap<>();
            variables = new HashMap<>();

            variables.put("page-links", edgeSizeMap.get(from_to));
            variables.put("share-same-instance", 0);

            strArr = from_to.split("-", 2);
            share_point = 0;
            edgeSize = edgeSizeMap.get(from_to) + share_point;

            edge.put("id", edge_id);
            edge_id++;
            edge.put("source", strArr[0]);
            edge.put("target", strArr[1]);
            edge.put("value", edgeSize);
            edge.put("variables", variables);
            edges.add(edge);
        }
        edgeSizeMap = null;
        return edges;
    }

    private Set<String> getCategoryIdSet(String instance) {
        return null;
    }

    private static void setPageLinkMap(HashMap<String, Set<String>> pageLinkMap, String s) {
        String[] strArr = s.split(" ", 4);
        String from = strArr[0];
        String to = strArr[2];
        String txt; // for checking prefix
        int idx; // index of the prefix

        txt = "/File:";
        idx = to.indexOf(txt);
        if(idx > -1) { // skip if the resource is a file
            return;
        } else { // remove prefix
            txt = "/resource/";
            idx = to.indexOf(txt);
            if(idx > -1) {
                to = to.substring(idx + txt.length(), to.length()-1);
            } else {
                System.out.println("[ERRO] there is no prefix /resource/");
                System.out.println(to);
            }
        }

        // remove prefix
        txt = "/resource/";
        idx = from.indexOf(txt);
        if(idx > -1) {
            from = from.substring(idx + txt.length(), from.length()-1);
        } else {
            System.out.println("[ERRO] there is no prefix /resource/");
            System.out.println(from);
        }

//        from = assignId(pageIdMap, from);
//        to = assignId(pageIdMap, to);

        pageLinkMap.get(from).add(to);
//        Disk.storeInDisk(from, to, "page_links");
    }

    private static String assignId(HashMap<String, String> pageIdMap, String k) {
        String v = pageIdMap.get(k);
        int tmp;
        if (v == null) {
            pageIdMap.put(k, Integer.toString(i));
            tmp = i;
            i++;
            return Integer.toString(tmp);
        } else {
            return v;
        }
    }

//    private static Set<String> getToSet(String s) {
//        return pageLinkMap.get(s);
//    }

    public static void main(String[] args) {
        HashMap<String, Set<String>> pageLinkMap;
        HashMap<String, String> pageIdMap;

        pageLinkMap = new DefaultHashMap<>(HashSet.class);
        pageIdMap = new HashMap<>();
        i = 1;
        setPageLinkMap(pageLinkMap, "<http://dbpedia.org/resource/AcademyAwards>" +
                " <http://dbpedia.org/ontology/wikiPageWikiLink>" +
                " <http://dbpedia.org/resource/Academy_Awards>" +
                " .");
        setPageLinkMap(pageLinkMap, "<http://dbpedia.org/resource/AcademicElitism>" +
                " <http://dbpedia.org/ontology/wikiPageWikiLink>" +
                " <http://dbpedia.org/resource/Ivory_tower>" +
                " .");
        setPageLinkMap(pageLinkMap, "<http://dbpedia.org/resource/Albedo>" +
                " <http://dbpedia.org/ontology/wikiPageWikiLink>" +
                " <http://dbpedia.org/resource/File:Albedo-e_hg.svg>" +
                " .");
        setPageLinkMap(pageLinkMap, "<http://dbpedia.org/resource/Albedo>" +
                " <http://dbpedia.org/ontology/wikiPageWikiLink>" +
                " <http://dbpedia.org/resource/Latin>" +
                " .");
        setPageLinkMap(pageLinkMap, "<http://dbpedia.org/resource/Albedo>" +
                " <http://dbpedia.org/ontology/wikiPageWikiLink>" +
                " <http://dbpedia.org/resource/Diffuse_reflection>" +
                " .");

        System.out.println(pageLinkMap.keySet());
        System.out.println(pageIdMap.keySet());
        for(String page : pageIdMap.keySet()) {
            System.out.println(page);
            System.out.println(pageIdMap.get(page));
        }
//        System.out.println(pageLinkMap.get(pageIdMap.get("AcademyAwards")));
//        System.out.println(pageLinkMap.get(pageIdMap.get("AcademicElitism")));
//        System.out.println(pageLinkMap.get(pageIdMap.get("Albedo")));
        System.out.println(pageLinkMap.get("AcademyAwards"));
        System.out.println(pageLinkMap.get("AcademicElitism"));
        System.out.println(pageLinkMap.get("Albedo"));
    }

    public static void generateEdges(ArrayList<String> fileList, String baseDir,
                                     ArrayList<String> pagelinksFileList, String edgesFile) throws IOException {
        String fileSuffix = ".occ";
        String input;
        String output;
        String[] strArr;
        TreeMap<String, String> edgeData;
        Map<String, String> insToCat;

        System.out.println("---------------------------");

//        for (String catFile : fileList) {
//            // set output file name
//            output = catFile + ".overlaps" + fileSuffix;
//
//            if(App.checkFile(output)) continue;
//
//            Category.writeOverlapsData(catFile, output);
//        }

        for (String pagelinksFile : pagelinksFileList) {
            // set output file name
            strArr = pagelinksFile.split("/");
            output = "output/" + strArr[0] + "/" + strArr[2];

            if(App.checkFile(output + fileSuffix)) continue;

            // write pagelinks temp into file
            try {
                // step 0 for source
                insToCat = Category.getInsToCat(strArr[0], ".aa");
                writePagelinksTemp(baseDir + pagelinksFile, insToCat, output + ".tmp", 0);
                insToCat = null;

                // step 1 for source
                insToCat = Category.getInsToCat(strArr[0], ".ab");
                writePagelinksTemp(output + ".tmp0", insToCat, output + ".tmp", 1);
                insToCat = null;

                // step 2 for target
                insToCat = Category.getInsToCat(strArr[0], ".aa");
                writePagelinksTemp(output + ".tmp1", insToCat, output + ".tmp", 2);
                insToCat = null;

                // step 3 for target
                insToCat = Category.getInsToCat(strArr[0], ".ab");
                writePagelinksTemp(output + ".tmp2", insToCat, output, 3);
                insToCat = null;
            } catch (IOException e) {
                System.err.println(e);
                return;
            }

            // word count
            input = output;
            output = output + fileSuffix + ".tmp";
            WordCounter.readWordFile(input, output);

            // external sort
            input = output;
            output = output + ".sorted";
            if (!App.checkFile(output)) {
                App.fileSort(input, output, new File("output/tmp/"));
            }

            // reduce
            input = output;
            output = "output/" + strArr[0] + "/" + strArr[2] + fileSuffix;
            fileReduce(input, output);
        }

//        edgeData = new TreeMap<>();
        // add data for each data
    }

    private static void writePagelinksTemp(String input, Map<String, String> insToCat, String output, int step) {
        BufferedReader reader;
        BufferedWriter writer;
        String inputLine;
        String[] strArr;
        int lineNumber;
        int totalLineNumber;
        String sourceIns;
        String targetIns;
        String[] sourceArr;
        String[] targetArr;

        if (step == 3) {
            if (App.checkFile(output)) return;
        } else {
            if (App.checkFile(output + step)) return;
        }

        try {
            reader = new BufferedReader(new FileReader(new File(input)));
        } catch (FileNotFoundException e) {
            System.err.println(e);
            return;
        }

        System.out.println("Start reading: " + input);
        lineNumber = 0;
        totalLineNumber = 0;
        try {
            if (step == 3) {
                writer = new BufferedWriter(new FileWriter(new File(output)));
            } else {
                writer = new BufferedWriter(new FileWriter(new File(output + step)));
            }
            while ((inputLine = reader.readLine()) != null) {
                // check progress
                if (lineNumber >= 500000) {
                    totalLineNumber += lineNumber;
                    lineNumber = 0;
                    System.out.print(totalLineNumber + ", ");
                }
                lineNumber++;

                if (step == 0) { // step 0
                    // Ignore comment lines.
                    if(inputLine.startsWith("#")) continue;

                    strArr = inputLine.split(" ");
                    sourceIns = App.removePrefix(strArr[0], "/resource/");
                    targetIns = App.removePrefix(strArr[2], "/resource/");

                    // ignore the line if instance is File or Category
                    if (targetIns.startsWith("File:")) continue;
                    if (targetIns.startsWith("Category:")) continue;

                    try {
                        sourceArr = insToCat.get(sourceIns).split(" ");
                        for (String sourceCat : sourceArr) {
                            writer.write(sourceCat + " " + targetIns); writer.newLine();
                        }
                    } catch (NullPointerException e) {
                        writer.write("/" + sourceIns + " " + targetIns); writer.newLine();
                    }
                } else if (step == 1) { // step 1
                    strArr = inputLine.split(" ");

                    if (!strArr[0].startsWith("/")) {
                        writer.write(inputLine); writer.newLine();
                        continue;
                    }

                    try {
                        sourceArr = insToCat.get(strArr[0].replace("/", "")).split(" ");
                        for (String sourceCat : sourceArr) {
                            writer.write(sourceCat + " " + strArr[1]); writer.newLine();
                        }
                    } catch (NullPointerException e) {
                        continue;
                    }
                } else if (step == 2) { // step 2
                    strArr = inputLine.split(" ");

                    try {
                        targetArr = insToCat.get(strArr[1]).split(" ");
                        for (String targetCat : targetArr) {
                            writer.write(strArr[0] + " " + targetCat); writer.newLine();
                        }
                    } catch (NullPointerException e) {
                        writer.write(strArr[0] + " /" + strArr[1]); writer.newLine();
                    }
                } else if (step == 3) {
                    strArr = inputLine.split(" ");

                    if (strArr[1].startsWith("/")) {
                        try {
                            targetArr = insToCat.get(strArr[1].replace("/", "")).split(" ");
                            for (String targetCat : targetArr) {
                                writer.write(strArr[0] + "/" + targetCat); writer.newLine();
                            }
                        } catch (NullPointerException e) {
                            continue;
                        }
                    } else {
                        writer.write(strArr[0] + "/" + strArr[1]); writer.newLine();
                    }
                }

//                for (String sourceCat : sourceArr) {
//                    for (String targetCat : targetArr) {
//                        // skip if FROM and TO are in the same category
//                        if(Objects.equals(sourceCat, targetCat)) continue;
//
//                        writer.write(sourceCat + "/" + targetCat); writer.newLine();
//                    }
//                }
            }
            writer.close();
            reader.close();
        } catch (IOException e) {
            System.err.println(e);
            return;
        }
        System.out.println("Done");
        System.out.println("File is created: " + output + step);
        System.out.println();
    }

    public static void fileReduce(String inputfile, String outputfile) throws IOException {
        BufferedReader reader;
        BufferedWriter writer;
        String inputLine;
        String[] strArr;
        String prevWord;
        String word;
        int count;
        int totalCnt;
        int lineNumber;
        int totalLineNumber;
        boolean notFirstLine;

        if (App.checkFile(outputfile)) return;

        reader = new BufferedReader(new FileReader(new File(inputfile)));
        writer = new BufferedWriter(new FileWriter(new File(outputfile)));
        prevWord = "";
        count = 0;
        totalCnt = 0;
        lineNumber = 0;
        totalLineNumber = 0;
        notFirstLine = false;

        System.out.println("Start reading: " + inputfile);
        while ((inputLine = reader.readLine()) != null) {
            // check progress
            if (lineNumber >= 500000) {
                totalLineNumber += lineNumber;
                lineNumber = 0;
                System.out.print(totalLineNumber + ", ");
            }
            lineNumber++;

            strArr = inputLine.split(" ");
            word = strArr[0];
            count = Integer.parseInt(strArr[1]);

            if (Objects.equals(word, prevWord)) {
                totalCnt += count;
            } else {
                if (notFirstLine) {
                    writer.write(prevWord + " " + totalCnt); writer.newLine();
                }
                totalCnt = count;
            }
            notFirstLine = true;
            prevWord = word;
        }
        writer.write(prevWord + " " + totalCnt); writer.newLine();
        writer.close();
        reader.close();
        System.out.println("Done");
        System.out.println();
    }
}
