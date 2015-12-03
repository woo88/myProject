package kr.ac.kaist.kmap;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App {
    private static final String baseDir = "/home/woo88/dbpedia/2015-04/en/";
    private static final String filename_categories = "article-categories_en.nt";
    private static final String filename_redirects = "redirects_en.nt";
    private static final String filename_infobox = "infobox-properties_en.nt";
    private static final String filename_types = "instance-types_en.nt";
    private static final String filename_page_length = "page-length_en.nt";
    private static final String filename_interlanguage = "interlanguage-links_en.nt";
    private static final String filename_freebase = "freebase-links_en.nt";
    private static final String filename_nytimes = "nytimes_links.nt";
    private static final String filename_yago_links = "yago_links.nt";
    private static final String filename_yago_types = "yago_types.nt";

    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!!!");

        Category cat = new Category(baseDir + filename_categories);
//        System.out.println(cat.getInstanceSet("Climate_forcing"));

        Redirect red = new Redirect(baseDir + filename_redirects);
//        System.out.println(red.getRedirect("United_States"));

        Infobox ib = new Infobox(baseDir + filename_infobox);
//        System.out.println(ib.getInfobox("United_States"));

        Type type = new Type(baseDir + filename_types);
        PageLength pl = new PageLength(baseDir + filename_page_length);
        InterLanguage il = new InterLanguage(baseDir + filename_interlanguage);

        /**
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
        ArrayList<Map<String, Object>> nodes = new ArrayList<>();
        int i = 0;
        for(String category : cat.getCategories()) {
            Map<String, Integer> variables = new HashMap<>();
            int instances_point = cat.getCategorySize(category);
            int redirect_point = 0;
            int infobox_point = 0;
            int type_point = 0;
            int length_pointh = 0;
            int interlanguage_point = 0;

            variables.put("instances", instances_point);
            for(String instance : cat.getInstanceSet(category)){
                redirect_point += red.getRedirect(instance);
                infobox_point += ib.getInfobox(instance);
            }
            variables.put("redirects", redirect_point);
            variables.put("infobox", infobox_point);
            variables.put("types", type_point);
            variables.put("page-length", length_pointh);
            variables.put("interlanguage", interlanguage_point);

            int node_size = instances_point + redirect_point + infobox_point;

            //        HashMap node = new HashMap();
            Map<String, Object> node = new HashMap<>();
            i++;
            node.put("id", String.valueOf(i));
            node.put("label", category);
            node.put("value", node_size);
            node.put("variables", variables);

            nodes.add(node);
        }

        HashMap map = new HashMap();
        map.put("timeslot", "2015-04");
        map.put("nodes", nodes);
        map.put("edges", "test");

//        StringBuffer sbuf = new StringBuffer();
        ObjectMapper mapper = new ObjectMapper();

        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("output.json"), map);
    }
}
