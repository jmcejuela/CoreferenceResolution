package Processors;

import java.util.ArrayList;

import Tree.TreeNode;
import dataStructure.Annotation;
import dataStructure.Sentence;
import dataStructure.Word;
/*
 *  I filter the pronouns which referes to humans (authors) 
 *  I remove Pleonastic It which do not refers to any entity
 *  
 */
public class PersonalPronouns {

	// ---------it and they-----------------------------//
	boolean filterPleonasticIt(Sentence sentence, Word word) {
		int counter = word.getPositionInSentence();
		ArrayList<Word> words = sentence.getWords();
		// --------pattern 1
		if (counter < words.size() - 1
				&& words.get(++counter).getBaseForm().equalsIgnoreCase("be")) {
			counter++;
			while (counter < words.size()
					&& (words.get(counter).getPos().startsWith("JJ")
							|| words.get(counter).getPos().startsWith("RB") || words
							.get(counter).getPos().startsWith("VB")))
				counter++;
			if (counter < words.size()
					&& words.get(counter).getWord().equalsIgnoreCase("that"))
				return true;

		}
		// -------pattern 2
		counter = word.getPositionInSentence();
		if (counter < words.size() - 1
				&& words.get(++counter).getBaseForm().equalsIgnoreCase("be")) {
			counter++;
			if (counter < words.size()
					&& words.get(counter).getPos().startsWith("JJ")) {
				counter++;
				if (counter < words.size()
						&& words.get(counter).getWord().equalsIgnoreCase("for")) {
					counter++;
					if (counter < words.size()
							&& words.get(counter).getPos().startsWith("NN")) {
						counter++;
						if (counter < words.size()
								&& words.get(counter).getWord()
										.equalsIgnoreCase("to")) {
							counter++;
							if (counter < words.size()
									&& words.get(counter).getPos()
											.startsWith("V")) {
								return true;
							}

						}
					}

				}
			}
		}
		counter = word.getPositionInSentence();
		// ----------------pattern 3
		counter++;
		if (counter < words.size()) {

			String tmpWord = words.get(counter).getWord();
			if (tmpWord.equalsIgnoreCase("seems")
					|| tmpWord.equalsIgnoreCase("appears")
					|| tmpWord.equalsIgnoreCase("means")
					|| tmpWord.equalsIgnoreCase("follows")) {
				counter++;
				if (counter < words.size()
						&& words.get(counter).getWord()
								.equalsIgnoreCase("that"))
					return true;
			}
		}
		// my pattern
		if (sentence.getWords().size() > word.getPositionInSentence() + 4) {
			counter = word.getPositionInSentence();
			ArrayList<Word> allWords = sentence.getWords();
			if (allWords.size() > counter + 4) {
				if (allWords.get(counter + 1).getWord().equalsIgnoreCase("has")
						&& allWords.get(counter + 2).getWord()
								.equalsIgnoreCase("been"))
					return true;
			}
		}
		return false;
	}

	boolean filterNonRelevantPronouns(String pronoun) {
		if (pronoun.equalsIgnoreCase("I") || pronoun.equalsIgnoreCase("we")
				|| pronoun.equalsIgnoreCase("you")
				|| pronoun.equalsIgnoreCase("our")
				|| pronoun.equalsIgnoreCase("your")
				|| pronoun.equalsIgnoreCase("his")
				|| pronoun.equalsIgnoreCase("her")
				|| pronoun.equalsIgnoreCase("us")
				|| pronoun.equalsIgnoreCase("them"))
			return true;
		return false;
	}

	Annotation theAnnot;
	boolean added;

	void dfs(TreeNode root) {
		if (root.getNpAnnotation() != null && !added) {
			if (!(root.getNpAnnotation().getAnnotatedWords().get(0).getWord()
					.equalsIgnoreCase("we") || root.getNpAnnotation()
					.getAnnotatedWords().get(0).getWord().equalsIgnoreCase("I"))){
				theAnnot = root.getNpAnnotation();
			added = true;
			return;
			}

		} 
		if(!added)
			for (int i = 0; i < root.getChildrens().size(); i++)
				if (!root.getCat().startsWith("PP"))
					dfs(root.getChildrens().get(i));

	}

	public Annotation annotationLookForIt(Annotation anaphora) {
		if (anaphora.getAnnotatedWords().get(0).getWord().toLowerCase().equals("them")||anaphora.getAnnotatedWords().get(0).getWord().toLowerCase().equals("they"))
			anaphora.setNumber(10);
		else
			anaphora.setNumber(1);
		TreeNode root = anaphora.getNode();
		TreeNode firstS = root.getParent();
		Sentence sentence = anaphora.getSentence();
		Sentence previous = sentence.getPrevious();
		theAnnot = null;
		added = false;

		while (firstS != null && !(firstS.getCat().startsWith("S"))) {

			if (firstS.getCat().startsWith("COOD"))
				if (!firstS.getParent().getChildrens().get(0).equals(firstS)) {
					if (!firstS.getParent().getChildrens().get(0).getCat()
							.startsWith("P")) {
						dfs(firstS.getParent().getChildrens().get(0));
					}
					if (theAnnot != null) 
						return theAnnot;
				}
			firstS = firstS.getParent();
		}

		if (firstS == null) {

			if (previous == null) {
				return null;
			} else
				return previous.getAntecedentCandidates().get(0);
		}
		firstS = firstS.getParent();
		boolean upSentence = false;
		while (firstS.getParent() != null) {
			if (firstS.getParent().getCat().startsWith("S") || upSentence){
				upSentence=true;
				for (int i = 0; i < firstS.getParent().getChildrens().size(); i++)
					if (firstS.getParent().getChildrens().get(i).equals(firstS)) {
						break;
					} else {

						dfs(firstS.getParent().getChildrens().get(i));
						if (theAnnot != null&&numberAgree(theAnnot, anaphora)) {
							return theAnnot;
						}
					}

					}
			firstS = firstS.getParent();
		}

		if (previous == null) {
			return null;
		} else
			return previous.getAntecedentCandidates().get(0);
	}
	boolean numberAgree(Annotation candidate, Annotation anaphora){
		if(candidate.getProteinNumber()>1 && anaphora.getNumber()>1)
		return true;
		if(candidate.getNumber()==1 && anaphora.getNumber()>1)
			return false;
		if(candidate.getNumber()>1 && anaphora.getNumber()==1)
			return false;
		if(candidate.getHead().isProtein() && anaphora.getNumber()>1)
			return false;
			
		if(candidate.getHead().isProtein() && anaphora.getNumber()==1)
			
			return true;
		return true;
		
		
	}

	public Annotation pronounCheck(Sentence sentence, TreeNode current) {
		Annotation annotation = Filtering.findNPret(current);
		if (filterNonRelevantPronouns(current.getWord().getWord()))
			return null;
		if (current.getWord().getWord().equalsIgnoreCase("it")
				&& filterPleonasticIt(sentence, current.getWord()))
			return null;
		if (annotation.getAnnotatedWords().size() < 1
				&& annotation.getAnnotatedWords().size() > 15) {
			return null;
		}

		else {
			sentence.getAnaphoraCandidates().add(annotation);
			return annotation;

		}
	}
}
