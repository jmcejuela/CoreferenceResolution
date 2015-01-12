package dataStructure;

import java.util.ArrayList;

public class Section {
	String id;
	Sentence title;
	int sectionNr;
	ArrayList<Paragraph> paragraphs;
	
	public Section(){
		paragraphs= new ArrayList<Paragraph>();
	}

	/*public int getSectionNr() {
		return sectionNr;
	}

	public void setSectionNr(int sectionNr) {
		this.sectionNr = sectionNr;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Sentence getTitle() {
		return title;
	}

	public void setTitle(Sentence title) {
		this.title = title;
	}
	public void setParagrphs(ArrayList<Paragraph> paragrphs) {
		this.paragraphs = paragrphs;
	}
 */

	public ArrayList<Paragraph> getParagrphs() {
		return paragraphs;
	}

	
}
