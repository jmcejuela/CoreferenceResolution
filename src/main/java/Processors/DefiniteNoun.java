package Processors;

import java.util.ArrayList;
import Tree.AppConstants;
import Tree.TreeNode;
import dataStructure.Annotation;
import dataStructure.Sentence;
import dataStructure.Word;

public class DefiniteNoun {
	public Annotation DNAntecedent(Annotation anaphora){
		try{
			if(anaphora.getAnnotatedWords().size()==1){
				if(anaphora.getAnnotatedWords().get(0).getWord().toLowerCase().equals("this"))
					anaphora.setNumber(1);
				else
					anaphora.setNumber(10);				
			  return null;
			}
		return Filtering.getCandidatesFrom2SentencesDN(anaphora);
		}
		catch(Exception e){
			e.printStackTrace();			
		}
		return null;		 
	}
	public boolean filterDefinite(Annotation annotation,
			ArrayList<String> allowNouns) {
		if (annotation.getProteinNumber()>0)
			return true;
		for (String str : allowNouns)
				if(annotation.getHead().getBaseForm()
						.equalsIgnoreCase(str))
					return false;

		return true;
	}	
	public boolean filterDefiniteAdjectives(Annotation annotation,
			ArrayList<String> doNotAllow) {
		for (String str : doNotAllow)
			for(Word wr:annotation.getAnnotatedWords())
				if(wr.getWord().equals(str))
						return true;
		return false;
	}	
	public void definiteCheck(Sentence sentence, TreeNode current) {
		
		Annotation annotation =Filtering.findNPret(current);
		if (annotation==null||annotation.isProteinReference()||annotation.getProteinNumber()>0){
	      return;
		}
		String word = current.getWord().getWord();
		if (word.equalsIgnoreCase("that")) {
			sentence.getAnaphoraCandidates().add(annotation);
			return;
		}
		
		if (filterDefinite(annotation, AppConstants.definiteNounAllow) && annotation.getAnnotatedWords().size()>1){
			return;
		}
		if(annotation.getAnnotatedWords().size()==1)
			return;
		String firstWord=annotation.getAnnotatedWords().get(0).getWord().toLowerCase();
		String head=annotation.getHead().getWord().toLowerCase();
		if(annotation.isBiomedicalEntity())
			return;
			
		
		if (annotation.getAnnotatedWords().size() < 1 || annotation.getAnnotatedWords().size() > 3){
			return;
		}
		if(filterDefiniteAdjectives(annotation, AppConstants.definite_not_allowed))
			return;
		if(firstWord.equals("the")&& annotation.getAnnotatedWords().size()>2 &&!head.startsWith("factor"))
			return;
		if(!firstWord.startsWith("th"))
			return;
		if ( ( head.equals("element")) && annotation.getAnnotatedWords().size() > 2 ){
			return;
		}
		
		
		if(!(head.toLowerCase().startsWith("gen") ||head.toLowerCase().startsWith("prot"))&& annotation.getAnnotatedWords().size()>1){
			Word wr=annotation.getHead();
			Word last=annotation.getAnnotatedWords().get(annotation.getAnnotatedWords().size()-1);
			if(head.startsWith("fac") && !(wr.getPrevious().getWord().toLowerCase().startsWith("tran")||annotation.getAnnotatedWords().size()<3))
				return;
			if(last.getNext()!=null){
				if(last.getNext().isBiomdicalEntity())
					return;
			}
			
		}
		if(annotation.getHead().getPrevious()!=null &&(annotation.getHead().getPrevious().getWord().endsWith("er"))){
			return;
		}	
		annotation.setSentence(sentence);
		sentence.getAnaphoraCandidates().add(annotation);
	}

}
