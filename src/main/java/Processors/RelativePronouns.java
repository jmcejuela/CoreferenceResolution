package Processors;

import java.util.ArrayList;

import sun.security.krb5.internal.APOptions;

import Resolution.BioNLPSolver;
import Tree.AppConstants;
import Tree.TreeNode;

import dataStructure.Annotation;
import dataStructure.Sentence;
import dataStructure.Word;

public class RelativePronouns {

	
	public Annotation processRelative(Annotation anaphora) {
		if (anaphora.getAnnotatedWords().size() > 3
				&& anaphora.getAnnotatedWords().get(0).getWord().equals("that"))
			return null;
		int start = anaphora.getAnnotatedWords().get(0).getPositionInSentence();
		int end = start + anaphora.getAnnotatedWords().size() - 1;
		Sentence sentence = anaphora.getSentence();
		boolean found = false;
		boolean isRelative=false;
		TreeNode tree=anaphora.getNode();
		for(int i=0;i<4;i++){
			 if (tree.getXcat().length()>0){
				 isRelative=true;
			 }
			tree=tree.getParent();
		}
		if(!isRelative){
			return null;
			
		}
		ArrayList<Annotation> candidates = sentence.getAntecedentCandidates();
		int maks = 0;
		int anaphoraBegin = anaphora.getAnnotatedWords().get(0).getBeginWord();
		// System.out.println(anaphora);
		for (int i = 0; i < candidates.size(); i++) {
			if (candidates.size() == 0)
				break;
			ArrayList<Word> annotation = candidates.get(i).getAnnotatedWords();
			if (annotation.size() > 18)
				continue;
			Word last = annotation.get(annotation.size() - 1);
			int endNP = last.getBeginWord() + last.getWord().length();
			if (last.getBeginWord() + last.getWord().length() < anaphoraBegin) {
				maks = Math.max(maks, last.getBeginWord()
						+ last.getWord().length());
				found = true;
			}

		}
		int antecedentPos = 0;
		int pos = 0;
		if (found)
			for (int i = 0; i < candidates.size(); i++) {

				ArrayList<Word> annotation = candidates.get(i)
						.getAnnotatedWords();
				Word last = annotation.get(annotation.size() - 1);
				if (last.getBeginWord() + last.getWord().length() >= maks - 2) {
					if (annotation.get(0).getBeginWord() > pos
							&& annotation.size() < 19) {
						antecedentPos = i;
						pos = annotation.get(0).getBeginWord();

					}
				}
			}

		if (found) {
			/*
			 * my preprocessing
			 */
			Annotation currentAntecedent = candidates.get(antecedentPos);
			// currentAntecedent=myPrcessing(currentAntecedent);
			anaphora.setCoreference(currentAntecedent);
			return currentAntecedent;
		}
		return null;
	}

	
}
