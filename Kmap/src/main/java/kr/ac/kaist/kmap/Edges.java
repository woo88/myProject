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
        Map<String, String> insToCat;
        String[] inputfileArr;
        int i;
        BufferedWriter writer;

        System.out.println();
        System.out.println("---------------------------------------");
        System.out.println("Start generating edges");
        System.out.println("---------------------------------------");

        System.out.println("Start calculating overlaps");
        System.out.println("---------------------------------------");
        for (String catFile : fileList) {
            // set output file name
            output = catFile + ".overlaps";

            if(App.checkFile(output)) continue;

            // make combinations of overlaps
            input = catFile;
            output = output + ".tmp";
            Category.writeOverlapsData2(input, output);

            // count occurrences of overlap
            input = output;
            output = output + ".occ";
            WordCounter.readWordFile(input, output);

            // sort before merging the same edge
            input = output;
            output = output + ".sorted";
            if (!App.checkFile(output)) {
                App.fileSort(input, output, new File("output/tmp/"));
            }

            // reduce
            input = output;
            output = catFile + ".overlaps";
            if (!App.checkFile(output)) {
                fileReduce(input, output, 1);
            }
        }

        System.out.println();
        System.out.println("---------------------------------------");
        System.out.println("Start calculating pagelinks");
        System.out.println("---------------------------------------");
        for (String pagelinksFile : pagelinksFileList) {
            // set output file name
            strArr = pagelinksFile.split("/");
            output = "output/" + strArr[0] + "/" + strArr[2];

            if(App.checkFile(output + fileSuffix)) continue;

            // write pagelinks temp into file
            // step 0 for source
            input = baseDir + pagelinksFile;
            output = output + ".tmp";
            insToCat = Category.getInsToCat(strArr[0]);
            writePagelinksTemp(input, insToCat, output, 0);
            insToCat = null;

            // step 1 for target
            input = output;
            output = "output/" + strArr[0] + "/" + strArr[2];
            insToCat = Category.getInsToCat(strArr[0]);
            writePagelinksTemp(input, insToCat, output, 1);
            insToCat = null;
            App.fileDelete(input);

            // word count
            input = output;
            output = output + fileSuffix + ".tmp";
            WordCounter.readWordFile(input, output);
            App.fileDelete(input);

            // external sort
            input = output;
            output = output + ".sorted";
            if (!App.checkFile(output)) {
                App.fileSort(input, output, new File("output/tmp/"));
                App.fileDelete(input);
            }

            // reduce
            input = output;
            output = "output/" + strArr[0] + "/" + strArr[2] + fileSuffix;
            fileReduce(input, output, 1);
            App.fileDelete(input);
        }

        // initialize edges.kmap
        inputfileArr = new String[]{
                "output/3.9/article_categories_en.nt.overlaps",
                "output/2014/article_categories_en.nt.overlaps",
                "output/2015-04/article-categories_en.nt.overlaps"
        };
        output = "output/edges.kmap.tmp";
        if (!App.checkFile("output/edges.kmap")) {
            if (!App.checkFile(output)) {
                writer = new BufferedWriter(new FileWriter(new File(output)));

                System.out.println("Initialize edges.kmap.tmp");
                for (String inputfile : inputfileArr) {
                    initEdges(inputfile, writer);
                }
                writer.close();
                System.out.println("File is created: " + output);
                System.out.println();
            }

            // sort
            input = output;
            output = output + ".sorted";
            if (!App.checkFile(output)) {
                App.fileSort(input, output, new File("output/tmp/"));
            }

            // reduce
            input = output;
            output = output + ".reduce";
            fileReduce(input, output);
        }

        // add data for each data
//        input = output;
//        inputfileArr = new String[]{
//                "output/3.9/article_categories_en.nt.overlaps.occ",
//                "output/3.9/page_links_en.nt.occ",
//                "output/2014/article_categories_en.nt.overlaps.occ",
//                "output/2014/page_links_en.nt.occ",
//                "output/2015-04/article-categories_en.nt.overlaps.occ",
//                "output/2015-04/page-links_en.nt.occ"
//        };
//        output = "output/edges.kmap";
//        i = 0;
//        for (String inputfile : inputfileArr) {
//            writeEdges(input, inputfile, output, (i/2)+1, i%2);
//            i++;
//        }
//
//        File file = new File("output/edges.kmap.tmp");
//        File file2 = new File("output/edges.kmap");
//        file.renameTo(file2);
    }

    private static void writeEdges(String input, String inputfile,
                                   String output, int i, int j) throws IOException {
        Scanner occFile;
        TreeMap<String, String> occData;
        int lineNumber;
        int limitNumber;

        if (App.checkFile(output)) return;

        occFile = new Scanner(new FileReader(inputfile));
        occData = new TreeMap<>();
        lineNumber = 0;
        limitNumber = 2500000;

        System.out.println("Start reading: " + inputfile);
        while (occFile.hasNext()) {
            if (lineNumber > limitNumber) {
                replaceEdgeData(input, occData, output, i, j);

                File file = new File(output);
                File file2 = new File(input);
                file2.delete();
                file.renameTo(file2);

                occData = null;
                occData = new TreeMap<>();
                lineNumber = 0;
            }

            occData.put(occFile.next(), occFile.next());
            lineNumber++;
        }
        replaceEdgeData(input, occData, output, i, j);

        File file = new File(output);
        File file2 = new File(input);
        file2.delete();
        file.renameTo(file2);

        System.out.println("Done");
        System.out.println();
    }

    private static void replaceEdgeData(String input, TreeMap<String, String> occData,
                                        String output, int i, int j) throws IOException {
        BufferedReader reader;
        BufferedWriter writer;
        String inputLine;
        String[] strArr;
        String data;
        String tmp;
        StringJoiner sj;

        reader = new BufferedReader(new FileReader(new File(input)));
        writer = new BufferedWriter(new FileWriter(new File(output)));

        while ((inputLine = reader.readLine()) != null) {
            strArr = inputLine.split(" ");
            data = strArr[i].split("/")[j];

            if (Objects.equals(data, "0")) {
                if (occData.containsKey(strArr[0])) {
                    data = occData.get(strArr[0]);
                    occData.remove(strArr[0]);

                    if (j == 0) {
                        tmp = data + "/" + strArr[i].split("/")[1];
                    } else {
                        tmp = strArr[i].split("/")[0] + "/" + data;
                    }

                    sj = new StringJoiner(" ");
                    for (int k = 0; k < strArr.length; k++) {
                        if (k == i) {
                            sj.add(tmp);
                        } else {
                            sj.add(strArr[k]);
                        }
                    }

                    writer.write(sj.toString());
                } else {
                    writer.write(inputLine);
                }
            } else {
                writer.write(inputLine);
            }
            writer.newLine();
        }
        reader.close();

        for (String edge : occData.keySet()) {
            if (j == 0) {
                tmp = occData.get(edge) + "/0";
            } else {
                tmp = "0/" + occData.get(edge);
            }

            sj = new StringJoiner(" ");
            sj.add(edge);
            for (int k = 1; k < 4; k++) {
                if (k == i) {
                    sj.add(tmp);
                } else {
                    sj.add("0/0");
                }
            }
            writer.write(sj.toString()); writer.newLine();
        }
        writer.close();
    }

    private static void initEdges(String input, BufferedWriter writer) throws IOException {
        BufferedReader reader;
        String inputLine;
        String[] strArr;
        String tmp;

        reader = new BufferedReader(new FileReader(new File(input)));

        System.out.println("Start reading: " + input);
        while ((inputLine = reader.readLine()) != null) {
            strArr = inputLine.split(" ", 2);

            writer.write(strArr[0]); writer.newLine();

            tmp = strArr[1];
        }
    }

    private static void writePagelinksTemp(String input, Map<String, String> insToCat, String output, int step) throws IOException {
        BufferedReader reader;
        BufferedWriter writer;
        String inputLine;
        String[] strArr;
        int lineNumber;
        double totalLineNumber;
        String sourceIns;
        String targetIns;
        String[] sourceArr;
        String[] targetArr;

        if (App.checkFile(output)) return;

        reader = new BufferedReader(new FileReader(new File(input)));
        writer = new BufferedWriter(new FileWriter(new File(output)));
        lineNumber = 0;
        totalLineNumber = 0;

        System.out.println("Start reading: " + input);
        while ((inputLine = reader.readLine()) != null) {
            // check progress
//            if (lineNumber >= 1000000) {
//                totalLineNumber += lineNumber;
//                lineNumber = 0;
//                System.out.print(totalLineNumber + ", ");
//            }
//            lineNumber++;

            if (step == 0) { // step 0 for source
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
                    continue;
                }
            } else if (step == 1) { // step 1 for target
                strArr = inputLine.split(" ");

                try {
                    targetArr = insToCat.get(strArr[1]).split(" ");
                    for (String targetCat : targetArr) {
                        if (Objects.equals(strArr[0], targetCat)) continue;
                        writer.write(strArr[0] + "/" + targetCat); writer.newLine();
                    }
                } catch (NullPointerException e) {
                    continue;
                }
            }
        }
        writer.close();
        reader.close();
        System.out.println("Done");
        System.out.println("File is created: " + output);
        System.out.println();
    }

    public static void fileReduce(String inputfile, String outputfile, int i) throws IOException {
        BufferedReader reader;
        BufferedWriter writer;
        String inputLine;
        String[] strArr;
        String prevWord;
        String word;
        int count;
        int totalCnt;
        int lineNumber;
        double totalLineNumber;
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

        System.out.println("Start reducing: " + inputfile);
        while ((inputLine = reader.readLine()) != null) {
            // check progress
//            if (lineNumber >= 1000000) {
//                totalLineNumber += lineNumber;
//                lineNumber = 0;
//                System.out.print(totalLineNumber + ", ");
//            }
//            lineNumber++;

            strArr = inputLine.split(" ");
            word = strArr[0];

            try {
                count = Integer.parseInt(strArr[i]);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println(e);
                System.out.println(inputLine);
//                System.exit(1);
            }

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
        System.out.println("File is created: " + outputfile);
        System.out.println();
    }

    public static void fileReduce(String inputfile, String outputfile) throws IOException {
        BufferedReader reader;
        BufferedWriter writer;
        String inputLine;
        String prevWord;
        String word;
        int lineNumber;
        double totalLineNumber;

        if (App.checkFile(outputfile)) return;

        reader = new BufferedReader(new FileReader(new File(inputfile)));
        writer = new BufferedWriter(new FileWriter(new File(outputfile)));
        prevWord = "";
        lineNumber = 0;
        totalLineNumber = 0;

        System.out.println("Start reducing: " + inputfile);
        while ((inputLine = reader.readLine()) != null) {
            // check progress
//            if (lineNumber >= 1000000) {
//                totalLineNumber += lineNumber;
//                lineNumber = 0;
//                System.out.print(totalLineNumber + ", ");
//            }
//            lineNumber++;

            word = inputLine.trim();

            if (Objects.equals(word, prevWord)) continue;

            writer.write(word); writer.newLine();
            prevWord = word;
        }
        writer.close();
        reader.close();
        System.out.println("File is created: " + outputfile);
        System.out.println();
    }
}
