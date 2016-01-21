package com.yinliang.Lucene_3_5.analyzer;

import com.chenlb.mmseg4j.analysis.MMSegAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class MyMMSegAnalyzerTest {
    private AnalyzerUtil analyzerUtil = null;

    @Before
    public void init() {
        this.analyzerUtil = new AnalyzerUtil();
    }



    @Test
    public void displayToken_02() {
        Analyzer[] analyzers = {new MyMMSegAnalyzer(new File("data"), new String[]{"这是", "中国"}), new MMSegAnalyzer(new File("data")), new StandardAnalyzer(Version.LUCENE_35), new StopAnalyzer(Version.LUCENE_35), new SimpleAnalyzer(Version.LUCENE_35), new WhitespaceAnalyzer(Version.LUCENE_35)};

        String txt = "这是我的家, 我来自中国安徽省六安市苍墩村, 你的家是哪里的？";
        // String txt = "how are you thank you, I hate you" ;


        int aaa = 1000;
        System.out.println(aaa);
        for (Analyzer analyzer : analyzers) {
            this.analyzerUtil.displayToken(txt, analyzer);
        }
    }


}
