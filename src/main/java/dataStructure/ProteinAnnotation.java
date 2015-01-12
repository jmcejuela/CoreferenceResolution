package dataStructure;

public class ProteinAnnotation {
	int start;
	int end;
	String text;

	// constructor that accepts a line from .a1 documents and creates a
	// Protein Annotation
	public ProteinAnnotation(String line) {
		String a[] = line.split("\t");
		this.text = a[2];
		int start = Integer.parseInt(a[1].split(" ")[1]);
		int end = Integer.parseInt(a[1].split(" ")[2]);
		this.start = start;
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public String getText() {
		return text;
	}

}
