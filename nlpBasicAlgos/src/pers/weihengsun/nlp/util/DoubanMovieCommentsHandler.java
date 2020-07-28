package pers.weihengsun.nlp.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class DoubanMovieCommentsHandler {
	public static void main(String[] args) {
		String corpusFullPath = "./corpus/raw_corpus/douban_movie_comments.csv";
		String trainSetFullPath = "./corpus/word_embedding_train.txt";
		
		try {
			File rawCorpus = new File(corpusFullPath);
			File trainSet = new File(trainSetFullPath);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(rawCorpus),"UTF-8"));

			PrintWriter trainWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(trainSet),"UTF-8")));
			
			String aLine = null;
			String utt = null;
			int count = 0;
			System.out.println("Start to process corpus.");
			while((aLine=reader.readLine())!= null) {
				if(aLine.startsWith("\uFEFF" + "ID")) continue;
				String[] uttFragments = aLine.split(",");
				if(uttFragments.length!=10) continue; //wrong format input, skip
				utt = uttFragments[8];
				utt = utt.replaceFirst(" ", "");
				
				utt = CorpusUtil.doWordSeg(utt);
				
				trainWriter.write(utt + "\r\n");
				count ++;
			}
			reader.close();
			trainWriter.close();
			System.out.println(count + " comments written to " + trainSetFullPath);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
