package dataStructure;

import java.util.ArrayList;

import Tree.TreeNode;

public class Annotation implements Comparable<Annotation> {
	Sentence sentence;
	Word head;
	ArrayList<Word> annotatedWords;
	Annotation coreference;
	TreeNode node;
	int number = 0;
	int proteinNumber = 0;
	boolean proteinReference;
	long hashcode;
    boolean biomedicalEntity=false;
    int numberOfBiomedicalEntity=0;
	public Annotation() {
		annotatedWords = new ArrayList<Word>();
		proteinReference = false;
	}

	public Sentence getSentence() {
		return sentence;
	}

	public void setSentence(Sentence sentence) {
		this.sentence = sentence;
	}

	public ArrayList<Word> getAnnotatedWords() {
		return annotatedWords;
	}

	public void setAnnotatedWords(ArrayList<Word> annotatedWords) {
		this.annotatedWords = annotatedWords;
	}


	public Annotation getCoreference() {
		return coreference;
	}

	public void setCoreference(Annotation coreference) {
		this.coreference = coreference;
	}

	public Word getHead() {
		return head;
	}

	public void setHead(Word head) {
		this.head = head;
	}

	public TreeNode getNode() {
		return node;
	}

	public void setNode(TreeNode node) {
		this.node = node;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isProteinReference() {
		return proteinReference;
	}

	public void setProteinReference(boolean proteinReference) {
		this.proteinReference = proteinReference;
	}

	public int getProteinNumber() {
		return proteinNumber;
	}

	public void setProteinNumber(int proteinNumber) {
		this.proteinNumber = proteinNumber;
	}

	public void incrementProteins() {
		this.proteinNumber = this.proteinNumber + 1;
	}
    
	// unique identifier of every annotation 6000 is chosen because there isn't a file that contains more than 6000 characters
	public long getHashcode() {
		long begin = (long) annotatedWords.get(0).getBeginWord();
		Word last = annotatedWords.get(annotatedWords.size() - 1);
		long end = (long) last.getBeginWord() + last.getWord().length();
		return 6000L * end + begin;
	}
	@Override
	public int compareTo(Annotation o) {
		if (this.getHashcode() < o.getHashcode())
			return 1;
		if (this.getHashcode() > o.getHashcode())
			return -1;
		return 0;

	}	
	

	public boolean isBiomedicalEntity() {
		return biomedicalEntity;
	}

	public void setBiomedicalEntity(boolean biomedicalEntity) {
		this.biomedicalEntity = biomedicalEntity;
	}

	public void setHashcode(long hashcode) {
		this.hashcode = hashcode;
	}

	public int getNumberOfBiomedicalEntity() {
		return numberOfBiomedicalEntity;
	}

	public void incremntNumberOfBiomedicalEntity() {
		this.numberOfBiomedicalEntity++;
	}

}
