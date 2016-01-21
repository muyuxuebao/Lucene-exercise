package com.yinliang.Lucene_3_5.analyzer;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by muyux on 2016/1/11.
 */
public class Test {
    @org.junit.Test
    public void test() throws IOException {
        File file = new File("ttt.txt");
        List<String> list = new LinkedList<String>();
        list.add("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        list.add("fsadfasfasf");


        FileUtils.writeLines(file, list);
    }
}
