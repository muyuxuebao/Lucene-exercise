package com.yinliang.Lucene_3_5.analyzer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by muyux on 2016/1/11.
 */
public class FrequentTest {


    private Directory initDir() {
        RAMDirectory directory = null;
        IndexWriter indexWriter = null;
        try {
            directory = new RAMDirectory();
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, new MyMMSegAnalyzer(new File("data"), new String[]{"这是", "中国"}));
            indexWriter = new IndexWriter(directory, iwc);

            Document doc = null;

            doc = new Document();
            doc.add(new Field("content", "这是我的家, 我来自中国安徽省六安市苍墩村, 你的家是哪里的？", Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("filename", "1.txt", Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field("path", "path", Field.Store.YES, Field.Index.NOT_ANALYZED));

            indexWriter.addDocument(doc);
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


    private void frequent(Directory directory) {

        try {
            IndexReader reader = IndexReader.open(directory);
            TermEnum termEnum = reader.terms();
            while (termEnum.next()) {
                if (termEnum.term().toString().startsWith("content:")) {
                    System.out.println("---------------------------------------------------------------------------");
                    System.out.println(termEnum.term());
                    System.out.println("DocFreq= " + termEnum.docFreq());

                    TermDocs termDocs = reader.termDocs(termEnum.term());
                    while (termDocs.next()) {
                        System.out.println("DocNo:   " + termDocs.doc() + "  Freq:   " + termDocs.freq());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }


    @Test
    public void test() {
        Directory directory = initDir();
        frequent(directory);
    }

}
