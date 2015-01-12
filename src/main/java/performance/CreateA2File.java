package performance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import Resolution.BioNLPSolver;


import dataStructure.Annotation;
import dataStructure.Article;
import dataStructure.Relation;
import dataStructure.Word;

public class CreateA2File {
	BufferedWriter wr;
	static String folderName = "resources/inputData/";
	static String resultFolder = "resources/results/";
    
	public static void CreatFile(Article article, String filename,
			ArrayList<Integer> s) throws Exception {
		int start = s.size() + 1;		
		BufferedReader reader = new BufferedReader(new FileReader(folderName
				+ filename + ".txt"));
		String sentence = "";
		String read;
		while ((read = reader.readLine()) != null) {
			sentence = sentence+read+" ";
		}
		reader.close();
		File fileResult = new File(resultFolder + filename + ".a2");

		BufferedWriter wr = new BufferedWriter(new FileWriter(fileResult));
		int numeration = start + 1;
		HashMap<Annotation, Integer> hashmap = new HashMap<Annotation, Integer>();
		HashMap<Integer, Annotation> hashmapInvrese = new HashMap<Integer, Annotation>();
		HashMap<Long, Integer> hashMapCod = new HashMap<Long, Integer>();
		for (int i = 0; i < article.getRelations().size(); i++) {
			Relation rel = article.getRelations().get(i);
			if (rel.getCoreference() != null
					&& hashmap.get(rel.getCoreference()) == null) {
				if (hashMapCod.get(rel.getCoreference().getHashcode()) != null) {
					rel.setCoreference(hashmapInvrese.get(hashMapCod.get(rel
							.getCoreference().getHashcode())));
				} else {
					hashmap.put(rel.getCoreference(), numeration);
					hashmapInvrese.put(numeration, rel.getCoreference());
					hashMapCod.put(rel.getCoreference().getHashcode(),
							numeration);
					numeration++;
				}
			}
			if (rel.getAnaphora() != null
					&& hashmap.get(rel.getAnaphora()) == null) {
				if (hashMapCod.get(rel.getAnaphora().getHashcode()) != null) {
					rel.setAnaphora(hashmapInvrese.get(hashMapCod.get(rel
							.getAnaphora().getHashcode())));
				} else {
					hashmap.put(rel.getAnaphora(), numeration);
					hashmapInvrese.put(numeration, rel.getAnaphora());
					hashMapCod.put(rel.getAnaphora().getHashcode(), numeration); 
					numeration++;
				}
			}

		}
		for (int i = start + 1; i < numeration; i++) {
			Annotation anaphora = hashmapInvrese.get(i);
			int startAnanphora = anaphora.getAnnotatedWords().get(0)
					.getBeginWord();
			Word lastA = anaphora.getAnnotatedWords().get(
					anaphora.getAnnotatedWords().size() - 1);
			int endAnaphora = lastA.getBeginWord() + lastA.getWord().length();
			String ana = "T" + i + "\t" + "Exp " + startAnanphora + " "
					+ endAnaphora + "\t"
					+ sentence.substring(startAnanphora , endAnaphora );
			wr.write(ana + "\n");	
		
		}
		for (int i = 0; i < article.getRelations().size(); i++) {
			Relation relation = article.getRelations().get(i);
			int ttt = i + 1;
			String temp = "R" + ttt + "\t" + "Coref Anaphora:T"
					+ hashmap.get(relation.getAnaphora()) + " "
					+ "Antecedent:T" + hashmap.get(relation.getCoreference());
			ArrayList<Integer> prot = new ArrayList<Integer>();
			int startAntecedent = relation.getCoreference().getAnnotatedWords()
					.get(0).getBeginWord();
			Word last = relation
					.getCoreference()
					.getAnnotatedWords()
					.get(relation.getCoreference().getAnnotatedWords().size() - 1);
			int endAntecedent = last.getBeginWord() + last.getWord().length();

			for (int j = 0; j < s.size(); j++) {
				if (s.get(j) >= startAntecedent && s.get(j) <= endAntecedent)
					prot.add(j + 1);
			}
			if (prot.size() > 0) {

				String pr = "[";
				pr = pr + "T" + prot.get(0);

				for (int j = 1; j < prot.size(); j++)
					pr = pr + ", T" + prot.get(j);
				pr = pr + "]";
				temp = temp + "\t" + pr;

			}
			wr.write(temp + "\n");		
		}
		wr.close();
	}

	
}
