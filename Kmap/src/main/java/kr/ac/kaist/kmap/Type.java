package kr.ac.kaist.kmap;

import java.io.*;
import java.util.*;

/**
 * Created by woo on 2015-12-03.
 */
public class Type {
    private Set<String> set = new HashSet<>();

    public Type(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        String inputLine;

        System.out.println("start reading instance-types");

        while((inputLine = reader.readLine()) != null) {
            // Ignore comment lines.
            if(inputLine.startsWith("#")) {
                continue;
            }

            String[] strArr = inputLine.split(" ", 4);
            String s = strArr[0];

            // Remove prefix.
            String txt = "/resource/";
            int idx = s.indexOf(txt);
            if(idx > -1) {
                s = s.substring(idx + txt.length(), s.length()-1);
            } else {
                System.out.println("[ERRO] there is no prefix /resource/");
                System.out.println(s);
                break;
            }

            set.add(s);
        }
        reader.close();
        System.out.println("\tnumber of instances including type information: " + set.size());
        System.out.println("\tfinish reading instance-types");
    }

    public Set<String> getSet() {
        return set;
    }

    public Set<String> getIntersection(Set<String> s) {
        s.retainAll(set);
        return s;
    }

    public static void convertInsToCat(String baseDir, ArrayList<String> typesFileList) throws IOException {
        for(String fileName : typesFileList) {
            String[] strArr = fileName.split("/");
            String output = "output/" + strArr[0] + "/" + strArr[2];

            if(App.checkFile(output)) continue;

            // get Map of instance to categories
            Map<String, ArrayList<String>> insToCat = Category.getInsToCat(strArr[0]);

            BufferedReader reader = new BufferedReader(new FileReader(new File(baseDir + fileName)));
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(output)));
            int lineNumber = 0;
            int totalLineNumber = 0;
            String inputLine = null;
            String prevIns = "";
            System.out.println("Start reading: " + fileName);
            while ((inputLine = reader.readLine()) != null) {
                // ignore comment lines.
                if(inputLine.startsWith("#")) continue;

                // tokenize
                strArr = inputLine.split(" ");
                String ins = App.removePrefix(strArr[0], "/resource/");

                if (Objects.equals(ins, prevIns)) {
                    continue;
                } else {
                    StringJoiner sj = new StringJoiner(" ");
                    for (String s : insToCat.get(ins)) {
                        sj.add(s);
                    }
                    writer.write(String.valueOf(sj)); writer.newLine();
                }
                prevIns = ins;

                // check progress
                if (lineNumber >= 500000) {
                    totalLineNumber += lineNumber;
                    lineNumber = 0;
                    System.out.print(totalLineNumber + ", ");
                }
                lineNumber++;
            }
            System.out.println("Done");
            System.out.println("File is created: " + output);
            System.out.println();
            reader.close();
            writer.close();
        }
    }
}
