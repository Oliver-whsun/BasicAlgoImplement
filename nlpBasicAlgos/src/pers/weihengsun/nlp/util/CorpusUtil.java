package pers.weihengsun.nlp.util;

import java.util.List;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

public class CorpusUtil {
	
	public static String doWordSeg(String inputString) {
		String outpuString = "";
		if(inputString == null || inputString.trim().isEmpty())
			return outpuString;
		//inputString = inputString.replaceAll("  ", " ");
		Result result = ToAnalysis.parse(inputString);
		List<Term> paser = result.getTerms();
		if(paser.size() == 1)
			return outpuString += paser.get(0).getName();
		for(Term term:paser) {
			if(!" ".equals(term.getName()))
				outpuString += term.getName() + " ";
		}
		return outpuString.trim();
	}
}
