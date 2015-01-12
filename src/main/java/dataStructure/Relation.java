package dataStructure;

public class Relation {
	Annotation anaphora;
	Annotation coreference;
 
	public Relation(Annotation first, Annotation second) {
		this.anaphora = first;
		this.coreference = second;
	}

	public Annotation getAnaphora() {
		return anaphora;
	}

	public void setAnaphora(Annotation first) {
		this.anaphora = first;
	}

	public Annotation getCoreference() {
		return coreference;
	}

	public void setCoreference(Annotation second) {
		this.coreference = second;
	}

	

}
