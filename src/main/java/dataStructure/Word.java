package dataStructure;

import Tree.TreeNode;

public class Word {
	String word;
	int positionInSentence;
	int beginWord; // offset
	String pos;
	String baseForm;
	String chunk;
	String ne; // name entity
	String category;// noun adjective
	String lexentry; // added features
	TreeNode treeNode;
	Sentence sentence;
	boolean isProtein = false;
	boolean endProtein=false;
	Word next;
	Word previous;
	boolean biomdicalEntity=false;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getPositionInSentence() {
		return positionInSentence;
	}

	public void setPositionInSentence(int positionInSentence) {
		this.positionInSentence = positionInSentence;
	}

	public int getBeginWord() {
		return beginWord;
	}

	public void setBeginWord(int beginWord) {
		this.beginWord = beginWord;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getBaseForm() {
		return baseForm;
	}

	public void setBaseForm(String baseForm) {
		this.baseForm = baseForm;
	}

	public String getChunk() {
		return chunk;
	}

	public void setChunk(String chunk) {
		this.chunk = chunk;
	}

	public String getNe() {
		return ne;
	}

	public void setNe(String ne) {
		this.ne = ne;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLexentry() {
		return lexentry;
	}

	public void setLexentry(String lexentry) {
		this.lexentry = lexentry;
	}

	public TreeNode getTreeNode() {
		return treeNode;
	}

	public void setTreeNode(TreeNode treeNode) {
		this.treeNode = treeNode;
	}

	public Sentence getSentence() {
		return sentence;
	}

	public void setSentence(Sentence sentence) {
		this.sentence = sentence;
	}

	public boolean isProtein() {
		return isProtein;
	}

	public void setProtein(boolean isProtein) {
		this.isProtein = isProtein;
	}

	@Override
	public String toString() {
		return this.word;
	}

	public Word getNext() {
		return next;
	}

	public void setNext(Word next) {
		this.next = next;
	}

	public Word getPrevious() {
		return previous;
	}

	public void setPrevious(Word previous) {
		this.previous = previous;
	}

	public boolean isEndProtein() {
		return endProtein;
	}

	public void setEndProtein(boolean endProtein) {
		this.endProtein = endProtein;
	}

	public boolean isBiomdicalEntity() {
		return biomdicalEntity;
	}

	public void setBiomdicalEntity(boolean biomdicalEntity) {
		this.biomdicalEntity = biomdicalEntity;
	}
	
	
}
