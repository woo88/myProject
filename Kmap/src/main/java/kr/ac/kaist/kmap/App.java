package kr.ac.kaist.kmap;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

/**
 * Hello world!
 *
 */
public class App {
    protected static final String baseDir = "/home/woo88/dbpedia/2015-04/en/";
    protected static final String filename_categories = "article-categories_en.nt";
    private static final String filename_redirects = "redirects_en.nt";
    private static final String filename_infobox = "infobox-properties_en.nt";
    private static final String filename_types = "instance-types_en.nt";
    private static final String filename_page_length = "page-length_en.nt";
    private static final String filename_interlanguage = "interlanguage-links_en.nt";
    private static final String filename_freebase = "freebase-links_en.nt";
    private static final String filename_nytimes = "nytimes_links.nt";
    private static final String filename_yago_links = "yago_links.nt";
    private static final String filename_yago_types = "yago_types.nt";
    private static final String filename_page_links = "page-links_en.nt";
    private static final String TIME_SLOT = "2015-04";

//    private static HashMap resultMap = new HashMap();

    public static void main(String[] args) throws IOException {
        Map<String, String> label_id;

//        resultMap.put("timeslot", TIME_SLOT);
        label_id = GenerateNodes();
//        resultMap.put("nodes", nodes);
//        resultMap.put("edges", "test");
        GenerateEdges(label_id);

//        StringBuffer sbuf = new StringBuffer();
//        ObjectMapper mapper = new ObjectMapper();

//        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("output2.json"), resultMap);
    }

    private static Map<String, String> GenerateNodes() throws IOException {
        HashMap resultMap = new HashMap();
        putTimeSlot(resultMap);

        ArrayList<Map<String, Object>> nodes = new ArrayList<>();
        Map<String, String> label_id;
        Category cat = new Category();
        cat.setFileName(baseDir + filename_categories);
        cat.setMap("category");

        Redirect red = new Redirect(baseDir + filename_redirects);
        Infobox ib = new Infobox(baseDir + filename_infobox);
        Type type = new Type(baseDir + filename_types);
        PageLength pl = new PageLength(baseDir + filename_page_length);
        InterLanguage il = new InterLanguage(baseDir + filename_interlanguage);

        /**
         * variables
         *
         * "instances": 0,
         * "redirects": 0,
         * "infobox": 0,
         * "types": 0,
         * "page-length": 0,
         * "interlanguage": 0,
         * "freebase": 0,
         * "nytimes": 0,
         * "yago-intances": 0,
         * "yago-types": 0
         */

        int i = 0; // node ID
        label_id = new HashMap<>();
        for(String category : cat.getKeySet()) {
            Map<String, Integer> variables = new HashMap<>();
            int instances_point = cat.getValueSetSize(category);
            int redirect_point = 0;
            int infobox_point = 0;
            int type_point;
            int length_point = 0;
            int interlanguage_point = 0;

            for(String instance : cat.getValueSet(category)){
                redirect_point += red.getRedirect(instance);
                infobox_point += ib.getInfobox(instance);
                length_point += pl.getPageLength(instance);
                interlanguage_point += il.getValue(instance);
            }
            type_point = type.getIntersection(cat.getValueSet(category)).size();

            variables.put("instances", instances_point);
            variables.put("redirects", redirect_point);
            variables.put("infobox", infobox_point);
            variables.put("types", type_point);
            variables.put("page-length", length_point);
            variables.put("interlanguage", interlanguage_point);

            int node_size = 0;
            node_size += instances_point;
            node_size += redirect_point;
            node_size += infobox_point;
            node_size += type_point;
            node_size += length_point;
            node_size += interlanguage_point;

            //        HashMap node = new HashMap();
            Map<String, Object> node = new HashMap<>();
            i++; // node ID
            node.put("id", String.valueOf(i));
            node.put("label", category);
            node.put("value", node_size);
            node.put("variables", variables);

            nodes.add(node);

            label_id.put(category, String.valueOf(i));
        }
        resultMap.put("nodes", nodes);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("nodes.json"), resultMap);
        return label_id;
    }

    protected static void putTimeSlot(HashMap resultMap) {
        resultMap.put("timeslot", TIME_SLOT);
    }

    private static void GenerateEdges(Map<String, String> label_id) throws IOException {
        Edges e = new Edges(baseDir + filename_page_links);
        e.putEdges("edges", label_id);
    }
}
