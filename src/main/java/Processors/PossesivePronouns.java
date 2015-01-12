package Processors;

import Tree.TreeNode;
import dataStructure.Annotation;
import dataStructure.Word;

public class PossesivePronouns {
    boolean relation=false;
	@SuppressWarnings("unused")
	public Annotation apply(Annotation anaphora) {
		TreeNode tree= anaphora.getAnnotatedWords().get(0).getTreeNode();
		theAnnot=null;
		added=false;
		if (anaphora.getAnnotatedWords().get(0).getWord().equals("Its")
				|| anaphora.getAnnotatedWords().get(0).getWord().equals("Their")){

			return anaphora.getSentence().getPrevious()
					.getAntecedentCandidates().get(0);
		}
		if (anaphora.getAnnotatedWords().get(0).getWord()
				.equalsIgnoreCase("its"))
			anaphora.setNumber(1);
		else
			anaphora.setNumber(10);

		relation=false;
		TreeNode root=findTheRoot(tree);
	
		Word its=anaphora.getAnnotatedWords().get(0);
		if(root==null){
				RelativePronouns rel=  new RelativePronouns();
				return rel.processRelative(anaphora);
		}
		while(theAnnot==null && root!=null){
			TreeNode tempRoot=root;
			if(relation){
				int number=anaphora.getNumber();
				if(root.getNpAnnotation()!=null && (root.getNpAnnotation().getNumber()==number||(root.getNpAnnotation().getProteinNumber()>1  && number==10))){
					theAnnot=root.getNpAnnotation();
					break;
				}
			}
			else{
				findNearestSentence(root, tempRoot);
				tempRoot.setVisited(true);				
				dfs(tempRoot, anaphora.getNumber());
			}			
			relation=false;
			root=root.getParent();
			root=findTheRoot(root);			
		}
		if(theAnnot==null){
			RelativePronouns rel=  new RelativePronouns();
			return rel.processRelative(anaphora);
		}
		return theAnnot;
	}
	public TreeNode findTheRoot(TreeNode tree) {
		TreeNode tempNode = tree;
		boolean found = true;
  
		while (found && tempNode != null) {
			String cat = tempNode.getCat();
			String xcat = tempNode.getXcat();
			if (cat.equals("COOD")) {
				TreeNode parentNode = tempNode.getParent();
				if (parentNode.getCat().equals("NP") || parentNode.getCat().equals("NX")
						|| parentNode.getCat().equals("S"))
					if (!parentNode.getChildrens().get(0).equals(tempNode)){
						return parentNode.getChildrens().get(0);
					}
				if (parentNode.getCat().equals("VP")) {
					if (!isConnected(tempNode.getChildrens().get(0)))
						return parentNode.getChildrens().get(0);
					
				}
			}
			if (tempNode.getParent()!=null && tempNode.getParent().getCat().equals("S") && tempNode.getParent().getParent()==null && !xcat.equals("REL")){
				if(!tempNode.getParent().getChildrens().get(0).equals(tempNode)){
				return tempNode.getParent().getChildrens().get(0);
				}
			}
			if (tempNode.getParent()!=null && tempNode.getParent().getCat().equals("S") && !tempNode.getParent().getXcat().equals("REL")){
				if(!tempNode.getParent().getChildrens().get(0).equals(tempNode))
					return tempNode.getParent().getChildrens().get(0);
				
			}
			tempNode=tempNode.getParent();
		}
		return null;

	}
	 private void findNearestSentence(TreeNode node, TreeNode S){
		if(node.getCat().equals("S") && !node.getXcat().equals("REL") && !node.isVisited())
			S=node;
		for(int i=0;i<node.getChildrens().size();i++)
			findNearestSentence(node.getChildrens().get(i), S);		
	}
	
	 Annotation theAnnot;
	 boolean added;
	 
	void dfs(TreeNode root, int number) {
		if (root.getNpAnnotation() != null && !added &&root.getNpAnnotation().getAnnotatedWords().size()>0) {
			 int NPnumber=root.getNpAnnotation().getNumber();
			if (!(root.getNpAnnotation().getAnnotatedWords().get(0).getWord()
					.equalsIgnoreCase("we") || root.getNpAnnotation()
					.getAnnotatedWords().get(0).getWord().equalsIgnoreCase("I")) &&  (NPnumber==number ||(root.getNpAnnotation().getProteinNumber()>1  && number==10))){
				theAnnot = root.getNpAnnotation();
			added = true;
			return;
			}
		} 
		if(!added)
			for (int i = 0; i < root.getChildrens().size(); i++){
					dfs(root.getChildrens().get(i),number);
			}
	}
	public boolean isConnected(TreeNode node) {
        TreeNode tree =node;
		while (tree.getWord() == null)
			tree = tree.getChildrens().get(0);
		Word wr = tree.getWord();
		for (int i = 0; i < 3 &&  wr!=null; i++) {
			if (wr.getWord().equals("and")|| wr.getWord().equals("or")) 
				return true;
			wr = wr.getNext();
		}
		return false;
	}
}
