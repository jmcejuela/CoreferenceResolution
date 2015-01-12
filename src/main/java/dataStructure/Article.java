package dataStructure;

import java.util.ArrayList;

import EnjuParser.EnjuPreprocessing;

public class Article {
	String title;
	ArrayList<Section> sections;
	ArrayList<Relation> relations;
	ArrayList<Sentence> sentences;
    ArrayList<ProteinAnnotation> proteinAnnotation;
	public Article() {
		sections = new ArrayList<Section>();
		relations = new ArrayList<Relation>();
		proteinAnnotation= new ArrayList<ProteinAnnotation>();
		sentences= new ArrayList<Sentence>();
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<Section> getSections() {
		return sections;
	}

	public void setSections(ArrayList<Section> sections) {
		this.sections = sections;
	}

	public ArrayList<Relation> getRelations() {
		return relations;
	}

	public void setRelations(ArrayList<Relation> relations) {
		this.relations = relations;
	}
	public void addSentencesToArticle() {
		for (int i = 0; i < sections.size(); i++) {
			Section sec = sections.get(i);
			for (int j = 0; j < sec.getParagrphs().size(); j++) {
				Paragraph par = sec.getParagrphs().get(j);
				for (int k = 0; k < par.getSentences().size(); k++){
					sentences.add(par.getSentences().get(k));
				}
			}
		}
	}

	public ArrayList<Sentence> getSentences() {
		return sentences;
	}
	public ArrayList<Relation> resolve(EnjuPreprocessing processor) {
		for (int i = 0; i < sentences.size(); i++) {
			Sentence sentence = sentences.get(i);
			for (int j = 0; j < sentences.get(i).getAnaphoraCandidates().size(); j++) {
				Annotation anaphora = sentence.getAnaphoraCandidates().get(j);
				Annotation antecedent = processor.getAntecedentCandidates(
						sentence, anaphora);
				if (anaphora != null && antecedent != null && anaphora.getAnnotatedWords().size()>0 &&
						antecedent.getAnnotatedWords().size()>0){
					if(!anaphora.getAnnotatedWords().get(0).equals(antecedent.getAnnotatedWords().get(0)) || 
							anaphora.getAnnotatedWords().get(0).getBeginWord()!=antecedent.getAnnotatedWords().get(0).getBeginWord())
					relations.add(new Relation(anaphora, antecedent));
				}
			}
		}
		for (int i = 0; i < relations.size(); i++) {
			Relation relation = relations.get(i);
			while (relation.getCoreference().getCoreference() != null
					&& linkCoreferences(relation.getCoreference())
					&& relation.getCoreference() != null
					&& !relation.getCoreference().equals(
							relation.getCoreference().getCoreference())) {
				relation.setCoreference(relation.getCoreference().getCoreference());
			}
			Annotation annot=relation.getAnaphora();
			if(annot.getAnnotatedWords().get(0).getWord().equalsIgnoreCase("its")||annot.getAnnotatedWords().get(0).getWord().equalsIgnoreCase("their")
					||annot.getAnnotatedWords().get(0).getWord().equalsIgnoreCase("whose")||annot.getAnnotatedWords().get(0).getWord().equalsIgnoreCase("whom")||annot.getAnnotatedWords().get(0).getWord().equalsIgnoreCase("those")){
				while(annot.getAnnotatedWords().size()>1){
					annot.getAnnotatedWords().remove(1);
				}
			}
			
			annot=relation.getCoreference();
			while(annot.getAnnotatedWords().get(annot.getAnnotatedWords().size()-1).getPos().equals(",") || annot.getAnnotatedWords().get(annot.getAnnotatedWords().size()-1).getPos().equals("."))
				annot.getAnnotatedWords().remove(annot.getAnnotatedWords().size()-1);
			while(annot.getAnnotatedWords().get(0).getPos().equals("RB")||annot.getAnnotatedWords().get(0).getPos().equals(","))
			annot.getAnnotatedWords().remove(0);
			Word current=annot.getAnnotatedWords().get(0);
			if(current.isProtein){
				while(current.getPrevious()!=null && current.getPrevious().isProtein())
				{
					current=current.getPrevious();
					annot.getAnnotatedWords().add(0, current);
				}
			}
			 current=annot.getAnnotatedWords().get(annot.getAnnotatedWords().size()-1);
				if(current.isProtein){
					while(current.getNext()!=null && current.getNext().isProtein())
					{
						current=current.getNext();
						annot.getAnnotatedWords().add(current);
					}
				}
		}
		// removing same relation if the parser added two times
		for(int i=1;i<relations.size();i++){
			if(relations.get(i-1).getAnaphora().equals(relations.get(i).getAnaphora())){ 
			
				relations.remove(--i);
			}
		}		
		return relations;
	}

	/*
	 * a function that checks if the coreference of an anpahora is a pronoun to
	 * look for its coreference
	 */
	private boolean linkCoreferences(Annotation annot) {
		String a = annot.getAnnotatedWords().get(0).getWord();
		if (a.equalsIgnoreCase("it") || a.equalsIgnoreCase("itself")
				|| a.equalsIgnoreCase("they") || a.equalsIgnoreCase("which")
				|| a.equalsIgnoreCase("that"))
			return true;
		return false;
	}
	
}
