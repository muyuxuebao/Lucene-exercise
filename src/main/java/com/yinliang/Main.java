package com.yinliang;

import com.yinliang.Lucene_3_5.analyzer.MyMMSegAnalyzer;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.*;

/**
 * Created by muyux on 2016/1/11.
 */
public class Main {
    static String FILE_SEPARATOR = System.getProperty("file.separator");

    private static String[] getStopWords(String path) {
        try {
            List<String> list = FileUtils.readLines(new File(path), "utf-8");
            System.out.println("==============================================================");
            System.out.println("getStopWords " + list.get(159));
            System.out.println("==============================================================");
            return list.toArray(new String[list.size()]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

    private static void index_h(String prefix, File file, IndexWriter indexWriter) throws IOException {
        Document doc = null;

        if (file.isDirectory()) {
            File files[] = file.listFiles();
            for (File file1 : files) {
                index_h(prefix + FILE_SEPARATOR + file.getName(), file1, indexWriter);
            }
        } else {
            String content = FileUtils.readFileToString(file, "utf-8");

            System.out.println("==============================================================");
            System.out.println("index_h " + content);
            System.out.println("==============================================================");

            String filename = prefix + FILE_SEPARATOR + file.getName();
            String path = file.getAbsolutePath();

            doc = new Document();
            doc.add(new Field("content", content, Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("relative_path", filename, Field.Store.YES, Field.Index.NOT_ANALYZED));
            indexWriter.addDocument(doc);
        }
    }

    private static Directory index(Analyzer analyzer, String processingPath) {
        RAMDirectory directory = null;
        IndexWriter indexWriter = null;
        try {
            directory = new RAMDirectory();
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, analyzer);
            indexWriter = new IndexWriter(directory, iwc);
            File file = new File(processingPath);
            index_h("", file, indexWriter);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (indexWriter != null) {
                try {
                    indexWriter.close();
                } catch (CorruptIndexException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }

        return directory;
    }

    private static Map<String, List<String>> generate_result(Directory directory) {
        Map<String, List<String>> result_map = new HashMap<String, List<String>>();

        try {
            IndexReader reader = IndexReader.open(directory);
            TermEnum termEnum = reader.terms();
            while (termEnum.next()) {
                String termEnumString = termEnum.term().toString();
                if (termEnumString.startsWith("content:")) {
                    String term = termEnumString.substring(termEnumString.lastIndexOf(":") + 1);
                    TermDocs termDocs = reader.termDocs(termEnum.term());
                    while (termDocs.next()) {
                        Document doc = reader.document(termDocs.doc());
                        String relative_path = doc.get("relative_path");

                        if (result_map.containsKey(relative_path)) {
                            result_map.get(relative_path).add(term + termDocs.freq());
                        } else {
                            result_map.put(relative_path, new ArrayList<String>());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

        return result_map;
    }


    public static void main(String[] args) {


        String formatstr = "ws [--in][--out][--dd][--sw][-h]";
        Options opt = new Options();
        // opt.addOption(OptionBuilder.withArgName("in").hasArg().withDescription("search for buildfile towards the root of the filesystem and use it").create("O"));
        opt.addOption(OptionBuilder.withLongOpt("in").withDescription("file path of those files need to be processed").withValueSeparator('=').hasArg().create());
        opt.addOption(OptionBuilder.withLongOpt("out").withDescription("file path to store result").withValueSeparator('=').hasArg().create());
        opt.addOption(OptionBuilder.withLongOpt("dd").withDescription("file path of dictionary").withValueSeparator('=').hasArg().create());
        opt.addOption(OptionBuilder.withLongOpt("sw").withDescription("file path of stop words").withValueSeparator('=').hasArg().create());
        opt.addOption("h", "help", false, "print help for the command.");

        if (args.length == 0) {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp(formatstr, "", opt, "");
            return;
        } else {
            parse_args(args, formatstr, opt);
        }
    }

    private static void parse_args(String[] args, String formatstr, Options opt) {
        HelpFormatter formatter = new HelpFormatter();
        CommandLineParser parser = new PosixParser();
        CommandLine cl = null;

        try {
            cl = parser.parse(opt, args);
        } catch (ParseException e) {
            formatter.printHelp(formatstr, opt); // 如果发生异常，则打印出帮助信息
        }

        if (cl.hasOption("in") && cl.hasOption("out") && cl.hasOption("dd") && cl.hasOption("sw")) {
            String stopWordsPath = cl.getOptionValue("sw");
            String inPath = cl.getOptionValue("in");
            String outPath = cl.getOptionValue("out");
            String dicPath = cl.getOptionValue("dd");
            processOperation(stopWordsPath, inPath, outPath, dicPath);
        } else {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp(formatstr, "", opt, "");
            return;
        }
    }


    private static void processOperation(String stopWordsPath, String inPath, String outPath, String dicPath) {
        Analyzer analyzer = new MyMMSegAnalyzer(new File(dicPath), getStopWords(stopWordsPath));
        Directory directory = index(analyzer, inPath);
        Map<String, List<String>> result_map = generate_result(directory);
        output_result(outPath, result_map);
    }

    private static void output_result(String outPath, Map<String, List<String>> result_map) {
        for (String s : result_map.keySet()) {
            try {
                FileUtils.writeLines(new File(outPath + s), result_map.get(s));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
