package dataStructure;

import java.util.ArrayList;

public class Paragraph {
	ArrayList<Sentence>sentences;
	String id;  
	int nrId;
	Section section;
	public Paragraph(){
		sentences= new ArrayList<Sentence>();
	}
	public ArrayList<Sentence> getSentences() {
		return sentences;
	}
/*	public void setSentences(ArrayList<Sentence> sentences) {
		this.sentences = sentences;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getNrId() {
		return nrId;
	}
	public void setNrId(int nrId) {
		this.nrId = nrId;
	}
	public Section getSection() {
		return section;
	}
	public void setSection(Section section) {
		this.section = section;
	}*/

}
