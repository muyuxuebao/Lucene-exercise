package com.yinliang.Lucene_3_5.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;
import java.io.StringReader;

public class AnalyzerUtil {

	public void displayToken(String string, Analyzer analyzer) {
		try {
			TokenStream tokenStream = analyzer.tokenStream("description", new StringReader(string));
			System.out.print(analyzer.getClass().toString().substring(analyzer.getClass().toString().lastIndexOf(".") + 1) + ": ");

			// 创建一个属性,这个属性会添加到流中,随着tokenStream增加
			CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
			while (tokenStream.incrementToken()) {
				System.out.print("[" + charTermAttribute + "] ");
			}
			System.out.println();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void dispalyAllTokenInfo(String string, Analyzer analyzer) {
		try {
			System.out.println(analyzer.getClass().toString().substring(analyzer.getClass().toString().lastIndexOf(".") + 1));
			TokenStream tokenStream = analyzer.tokenStream("description", new StringReader(string));

			// 位置增量的属性, 存储语汇单元之间的相对距离, 可以用来做同义词
			PositionIncrementAttribute positionIncrementAttribute = tokenStream.addAttribute(PositionIncrementAttribute.class);
			// 语汇单元的首位字符在整个字符串中的位置
			OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
			// 存储语汇单元(分词单元信息)
			CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
			// 使用的分词器的类型信息
			TypeAttribute typeAttribute = tokenStream.addAttribute(TypeAttribute.class);

			while (tokenStream.incrementToken()) {
				System.out.println(String.format("%d: %s[%d-%d]-->%s", positionIncrementAttribute.getPositionIncrement(), charTermAttribute, offsetAttribute.startOffset(), offsetAttribute.endOffset(), typeAttribute.type()));
			}

			System.out.println("\n\n--------------------------------------------------------------\n\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
