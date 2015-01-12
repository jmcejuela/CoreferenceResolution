package Tree;

import java.util.LinkedList;

import dataStructure.Annotation;
import dataStructure.Word;

public class TreeNode {
	LinkedList<TreeNode>childrens;
	String type="";
	TreeNode parent;
	Word word;	
	public String ID;
	String cat="";
	String xcat="";
	String head="";
	String sem_head="";
	String schema="";
	Annotation npAnnotation;
	boolean visited=false;
	public TreeNode() {
	childrens= new LinkedList<TreeNode>();
	}
	public LinkedList<TreeNode> getChildrens() {
		return childrens;
	}
	public void setChildrens(LinkedList<TreeNode> childrens) {
		this.childrens = childrens;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public TreeNode getParent() {
		return parent;
	}
	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	public Word getWord() {
		return word;
	}
	public void setWord(Word word) {
		this.word = word;
	}
	
	public String getCat() {
		return cat;
	}
	public void setCat(String cat) {
		this.cat = cat;
	}
	public String getXcat() {
		return xcat;
	}
	public void setXcat(String xcat) {
		this.xcat = xcat;
	}
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public String getSem_head() {
		return sem_head;
	}
	public void setSem_head(String sem_head) {
		this.sem_head = sem_head;
	}
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	public Annotation getNpAnnotation() {
		return npAnnotation;
	}
	public void setNpAnnotation(Annotation npAnnotation) {
		this.npAnnotation = npAnnotation;
	}
	public boolean isVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	
	
	
	


}
