package dataStructure;

import java.util.ArrayList;

public class Sentence {
	String sentence;
	int nrId;
	String type; // title subtitle;
	ArrayList<Word> words;
	// markable detection 
	ArrayList<Annotation> antecedentCandidates;
	ArrayList<Annotation> anaphoraCandidates;
	Paragraph paragraph;
	String enjuParser; // the result from EnjuParser
	String tokenized = "";
	Sentence previous; // previous sentence
	Sentence next; // next sentence
	public int  offset;
	public Sentence() {
		words = new ArrayList<Word>();
		antecedentCandidates = new ArrayList<Annotation>();
		anaphoraCandidates = new ArrayList<Annotation>();
		paragraph = new Paragraph();
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public int getNrId() {
		return nrId;
	}

	public void setNrId(int nrId) {
		this.nrId = nrId;
	}

	/*public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}*/

	public Paragraph getParagraph() {
		return paragraph;
	}

	public void setParagraph(Paragraph paragraph) {
		this.paragraph = paragraph;
	}

	public ArrayList<Word> getWords() {
		return words;
	}

	public void setWords(ArrayList<Word> words) {
		this.words = words;
	}

	public String getEnjuParser() {
		return enjuParser;
	}

	public void setEnjuParser(String enjuParser) {
		this.enjuParser = enjuParser;
	}

	public ArrayList<Annotation> getAntecedentCandidates() {
		return antecedentCandidates;
	}

	public void setAntecedentCandidates(
			ArrayList<Annotation> antecedentCandidates) {
		this.antecedentCandidates = antecedentCandidates;
	}

	public ArrayList<Annotation> getAnaphoraCandidates() {
		return anaphoraCandidates;
	}

	public void setAnaphoraCandidates(ArrayList<Annotation> anaphoraCandidates) {
		this.anaphoraCandidates = anaphoraCandidates;
	}

	public Sentence getPrevious() {
		return previous;
	}

	public void setPrevious(Sentence previous) {
		this.previous = previous;
	}

	public Sentence getNext() {
		return next;
	}

	public void setNext(Sentence next) {
		this.next = next;
	}

}
