package Processors;

import java.util.ArrayList;
import java.util.Collections;

import Tree.TreeNode;
import dataStructure.Annotation;
import dataStructure.Sentence;
import dataStructure.Word;

public class Filtering {

	public static Annotation findNPret(TreeNode node) {
		TreeNode tempNode = node;
		while (tempNode != null && tempNode.getNpAnnotation() == null) {

			tempNode = tempNode.getParent();
			if (tempNode.getParent() == null) {
				tempNode = tempNode.getParent();
				break;
			}

		}
		if (tempNode == null)
			return null;
		return tempNode.getNpAnnotation();
	}

	static boolean containsAnd(Annotation candidate) {
		for (int i = 0; i < candidate.getAnnotatedWords().size(); i++)
			if (candidate.getAnnotatedWords().get(i).getWord().toLowerCase()
					.equals("and"))
				return true;
		return false;

	}

	private static ArrayList<Annotation> numberAgreeProteins(
			ArrayList<Annotation> candidates, Annotation annot) {

		for (int i = 0; i < candidates.size(); i++) {

			if (annot.getNumber() == 1 && containsAnd(candidates.get(i))) {
				candidates.remove(i--);
				continue;
			}
			if (candidates.get(i).getNumber() != annot.getNumber()
					&& !(candidates.get(i).getProteinNumber() > 1 && annot
							.getNumber() == 10)
					&& !(candidates.get(i).getHead().getWord().toLowerCase()
							.startsWith("famil"))) {
				candidates.remove(i--);
			}

		}
		return candidates;
	}

	public static ArrayList<Annotation> numberAgreeOthers(
			ArrayList<Annotation> candidates, Annotation annot) {

		for (int i = 0; i < candidates.size(); i++) {
			if (candidates.get(i).getNumber() != annot.getNumber()
					&& !((candidates.get(i).getProteinNumber() > 1 && annot
							.getNumber() == 10) || (candidates.get(i)
							.getNumberOfBiomedicalEntity() > 1 && annot
							.getNumber() == 10)))
				candidates.remove(i--);

		}

		return candidates;
	}

	public static Annotation getCandidatesFrom2SentencesDN(Annotation anaphora)
			throws Exception {
		Sentence sentence = anaphora.getSentence();

		ArrayList<Annotation> annot = new ArrayList<Annotation>();
		ArrayList<Word> anaphoraWords = anaphora.getAnnotatedWords();
		for (int i = 0; i < sentence.getAntecedentCandidates().size(); ++i) {
			Annotation tempAnnotation = sentence.getAntecedentCandidates().get(
					i);
			ArrayList<Word> tempWords = tempAnnotation.getAnnotatedWords();
			if (anaphoraWords.get(0).getPositionInSentence() > tempWords.get(
					tempWords.size() - 1).getPositionInSentence()) {

				annot.add(tempAnnotation);

			}
		}
		boolean considerTwoSentnces = false;
		if (annot.size() < 3) {
			considerTwoSentnces = true;
		}

		Sentence previous = sentence.getPrevious();
		if (previous != null)
			for (int j = previous.getAntecedentCandidates().size() - 1; j >= 0; --j)
				annot.add(previous.getAntecedentCandidates().get(j));
		if (considerTwoSentnces && true) {
			if (previous != null && previous.getPrevious() != null) {
				Sentence prePrevious = previous.getPrevious();
				for (int j = prePrevious.getAntecedentCandidates().size() - 1; j >= 0; --j)
					annot.add(prePrevious.getAntecedentCandidates().get(j));
			}
		}
		Collections.sort(annot);
		if (anaphora.getHead().getWord().startsWith("gen")
				|| anaphora.getHead().getWord().startsWith("prot"))

			annot = numberAgreeProteins(annot, anaphora);
		else
			annot = numberAgreeOthers(annot, anaphora);
		if (anaphora.getNumber() > 1) {
			return filterPlural(annot, anaphora);
			//
		} else {
			return filterSing(annot, anaphora);
		}
	}

	private static Annotation filterPlural(ArrayList<Annotation> candidates,
			Annotation anaphora) throws Exception {
		boolean isProtein = false;

		if (anaphora.getHead().getWord().toLowerCase().startsWith("protein")
				|| anaphora.getHead().getWord().toLowerCase()
						.startsWith("gene")) {
			Sentence prev = anaphora.getSentence().getPrevious();
			if (prev != null && prev.getPrevious() != null) {
				Sentence previous = prev.getPrevious();
				for (int j = previous.getAntecedentCandidates().size() - 1; j >= 0; --j)
					candidates.add(previous.getAntecedentCandidates().get(j));

			}
			isProtein = true;
		}
		Collections.sort(candidates);
		for (int i = 0; i < candidates.size() && isProtein; i++) {
			if (rule0Plural(candidates.get(i), anaphora)
					|| (ruleSameHeadWord(candidates.get(i), anaphora) && candidates
							.get(i).getAnnotatedWords().size() > 1)
					|| rule1Plural(candidates.get(i), anaphora)) {
				return candidates.get(i);
			}
		}
		for (int i = 0; i < candidates.size() && isProtein; i++) {
			if (rule0Plural(candidates.get(i), anaphora)
					|| (ruleSameHeadWord(candidates.get(i), anaphora) && candidates
							.get(i).getAnnotatedWords().size() > 1)
					|| rule1Plural(candidates.get(i), anaphora)
					|| rule4Plural(candidates.get(i), anaphora)) {
				return candidates.get(i);
			}
		}

		for (int i = 0; i < candidates.size(); i++) {
			if (rule0Plural(candidates.get(i), anaphora)
					|| rule1Plural(candidates.get(i), anaphora)
					|| (ruleSameHeadWord(candidates.get(i), anaphora) && candidates
							.get(i).getAnnotatedWords().size() > 1)
					|| rule3Plural(candidates.get(i), anaphora)) {
				return candidates.get(i);
			}
		}
		return null;

	}

	// Plural Rules
	static boolean rule0Plural(Annotation candidate, Annotation anaphora) {
		if (anaphora.getHead().equals("prot")
				|| anaphora.getHead().getWord().startsWith("gen")) {
			if (candidate.getProteinNumber() > 1) {
				if ((anaphora.getHead().getWord().startsWith("prot") && candidate
						.getHead().getWord().startsWith("gene"))
						|| (anaphora.getHead().getWord().startsWith("gen") && candidate
								.getHead().getWord().startsWith("prot")))
					return false;
				return true;
			}
		} else {
			if (anaphora.getNumber() > 1
					&& candidate.getNumberOfBiomedicalEntity() > 1)
				return true;

		}
		return false;
	}

	static boolean rule4Plural(Annotation candidate, Annotation anaphora) {
		if (!(anaphora.getHead().equals("prot") || anaphora.getHead().getWord()
				.startsWith("gen"))) {
			if (candidate.getProteinNumber() > 1)
				return true;
		}
		return false;
	}

	static boolean rule1Plural(Annotation candidate, Annotation anaphora) {
		if (candidate.getHead().getWord().toLowerCase().startsWith("fam"))
			return true;
		return false;
	}

	static boolean ruleSameHeadWord(Annotation candidate, Annotation anaphora) {
		if (candidate.getHead().getWord().toLowerCase()
				.equals(anaphora.getHead().getWord().toLowerCase()))
			return true;
		return false;
	}

	static boolean rule3Plural(Annotation candidate, Annotation anaphora) {
		if (candidate.getProteinNumber() > 1
				&& candidate.getHead().isEndProtein())
			return true;
		return false;
	}

	static boolean rule0Singular(Annotation candidate, Annotation anaphora) {
		if (anaphora.getHead().getWord().startsWith("prot")
				|| anaphora.getHead().getWord().startsWith("gen")) {
			if (candidate.getAnnotatedWords().size() <= 2
					&& candidate.getProteinNumber() > 0)
				return true;
		} else {
			if (candidate.getProteinNumber() > 0
					|| candidate.getNumberOfBiomedicalEntity() > 0)
				return true;

		}
		return false;
	}

	static private Annotation filterSing(ArrayList<Annotation> candidates,
			Annotation anaphora) throws Exception {
		boolean isProtein = false;
		if (anaphora.getHead().getWord().toLowerCase().startsWith("protein")
				|| anaphora.getHead().getWord().toLowerCase()
						.startsWith("gene"))
			isProtein = true;
		ArrayList<Annotation> two = new ArrayList<Annotation>();
		for (int i = 0; i < candidates.size(); i++)
			two.add(candidates.get(i));
		Sentence sen = anaphora.getSentence();
		if (sen.getPrevious() != null
				&& sen.getPrevious().getPrevious() != null) {
			Sentence th = sen.getPrevious().getPrevious();
			for (int i = 0; i < th.getAntecedentCandidates().size(); i++)
				two.add(th.getAntecedentCandidates().get(i));
		}
		for (int i = 0; i < candidates.size() && !isProtein; i++)
			if (ruleSameHeadWord(two.get(i), anaphora)
					&& two.get(i).getAnnotatedWords().size() > 1)
				return two.get(i);
		for (int i = 0; i < candidates.size(); i++)
			if (rule0Singular(candidates.get(i), anaphora)
					|| (candidates.get(i).isBiomedicalEntity() && !isProtein)
					|| candidates.get(i).getHead().isProtein())
				return candidates.get(i);
		return null;
	}

}
