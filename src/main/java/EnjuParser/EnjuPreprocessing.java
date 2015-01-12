package EnjuParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import Processors.DefiniteNoun;
import Processors.Filtering;
import Processors.PersonalPronouns;
import Processors.PossesivePronouns;
import Processors.RelativePronouns;
import Resolution.BioNLPSolver;
import Tree.AppConstants;
import Tree.TreeNode;
import dataStructure.Annotation;
import dataStructure.ProteinAnnotation;
import dataStructure.Sentence;
import dataStructure.Word;

public class EnjuPreprocessing {
	public int charCounter = 0;
	static EnjuParser parser = new EnjuParser();
	private DefiniteNoun dn;
	private PersonalPronouns personalPronoun;
	private RelativePronouns relativePronouns;
	private PossesivePronouns possesivePronouns;

	public EnjuPreprocessing() {
		dn = new DefiniteNoun();
		personalPronoun = new PersonalPronouns();
		relativePronouns = new RelativePronouns();
		possesivePronouns = new PossesivePronouns();
	}

	private void setWordOffset(Sentence sentence, int start, String sent) {
		for (int i = 0; i < sentence.getWords().size(); i++) {
			Word wrd = sentence.getWords().get(i);
			String word = wrd.getWord();
			while (!word.equals(sent.substring(start, start + word.length()))) {
				start++;
			}
			charCounter = start;
			wrd.setBeginWord(start);
			
			start = start + word.length();
		}
		for (int i = 0; i < sentence.getWords().size(); i++) {
			Word wrd = sentence.getWords().get(i);

			ProteinAnnotation protein = BioNLPSolver.getHashedProtein().get(
					wrd.getBeginWord());

			if (protein != null) {
				while (wrd != null && wrd.getBeginWord() >= protein.getStart()
						&& wrd.getBeginWord() < protein.getEnd()) {
					wrd.setProtein(true);
					if(wrd.getWord().length()+wrd.getBeginWord()>=protein.getEnd())
						wrd.setEndProtein(true);
					wrd = wrd.getNext();
				}

			}

		}
	}

	public void apply(Sentence sentence) throws Exception {
		if (sentence.getSentence().length() > 1000)
			sentence.setSentence(sentence.getSentence().substring(0, 1000));
		InputStream stream = new ByteArrayInputStream(parser
				.ParserSentenceToXml(sentence.getSentence()).getBytes(
						StandardCharsets.UTF_8));
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		dBuilder.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicId, String systemId)
					throws SAXException, IOException {
				return new InputSource(new StringReader(""));
			}
		});
		Document doc = dBuilder.parse(stream);
		TreeNode root = buildParseTree(doc, sentence);
		setWordOffset(sentence, charCounter, BioNLPSolver.theArticle);
		
		findAllNP(root, sentence);
		findAntecedents(sentence, root);
		findAnpahoras(sentence, root);
	}

	private TreeNode creatTreeNode(TreeNode parent, Node current,
			Sentence sentence, int startNumber) {
		TreeNode node = new TreeNode();
		node.setParent(parent);
		parent.getChildrens().addLast(node);
		Element element = (Element) current;
		String type = element.getNodeName();
		node.setType(type);
		String cat = element.getAttribute("cat");
		node.setCat(cat);
		node.ID=element.getAttribute("id");
		if (type.equals("tok")) {
			Word wrd = new Word();
			node.setWord(wrd);
			if (sentence.getWords().size() > 0) {
				Word last = sentence.getWords().get(
						sentence.getWords().size() - 1);
				wrd.setPrevious(last);
				last.setNext(wrd);
			}
			sentence.getWords().add(wrd);
			wrd.setPos(element.getAttribute("pos"));
			wrd.setLexentry(element.getAttribute("lexentry"));
			wrd.setBaseForm(element.getAttribute("base"));
			wrd.setWord(element.getTextContent());
			wrd.setTreeNode(node);
			wrd.setSentence(sentence);
			wrd.setPositionInSentence(Integer.parseInt(element.getAttribute(
					"id").substring(1))
					- startNumber);
		}
		if (type.equals("cons")) {
			node.setSchema(element.getAttribute("schema"));
			node.setHead(element.getAttribute("head"));
			node.setXcat(element.getAttribute("xcat"));
			node.setSem_head(element.getAttribute("sem_head"));
		}
		return node;
	}

	private void processTree(TreeNode parent, Node current, Sentence sentence,
			int startNumber) {
		TreeNode childNode = creatTreeNode(parent, current, sentence,
				startNumber);
		if (childNode.getType().equals("cons")) {
			for (int i = 0; i < current.getChildNodes().getLength(); i++)
				if (current.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE)
					processTree(childNode, current.getChildNodes().item(i),
							sentence, startNumber);
		}

	}

	private boolean isTheWordAllowed(String str, ArrayList<String> notAllowed) {
		for (String term : notAllowed) {
			if (str.startsWith(term))
				return false;
		}
		return true;
	}

	private TreeNode buildParseTree(Document doc, Sentence sentence) {
		TreeNode root;
		root = new TreeNode();
		NodeList tokList = doc.getElementsByTagName("tok");
		int startNumber=-1;
		try{
		 startNumber = Integer.parseInt(((Element) tokList.item(0))
				.getAttribute("id").substring(1));
		}
		catch(Exception e){
			
		}
		if(startNumber<0)
			return null;
	
		Element rootElement = doc.getDocumentElement();
		root.setType(rootElement.getNodeName());
		NodeList childList = rootElement.getChildNodes();
		for (int i = 0; i < rootElement.getChildNodes().getLength(); i++) {
			if (childList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) (childList.item(i));
				elem.setAttribute("cat", "S");
				if (i != 0 || !elem.getAttribute("cat").startsWith("NP"))
					processTree(root, childList.item(i), sentence, startNumber); 
			}
		}
		return root;
	}

	public void getAntecedent(TreeNode root, ArrayList<String> notAllowed,
			Annotation annot) {
		for (int i = 0; i < root.getChildrens().size(); i++) {
			if (root.getChildrens().get(i).getType().equals("tok")) {
				annot.getAnnotatedWords().add(
						root.getChildrens().get(i).getWord());
				continue;
			}
			if (isTheWordAllowed(root.getChildrens().get(i).getCat(),
					notAllowed))
				getAntecedent(root.getChildrens().get(i), notAllowed, annot);
		}
	}

	private void findAllNP(TreeNode root, Sentence sentence) {
		for (int i = 0; i < root.getChildrens().size(); i++) {
			if (root.getChildrens().get(i).getType().equalsIgnoreCase("cons")
					&& (root.getChildrens().get(i).getCat().startsWith("NP") || NXasNounPhrase(root
							.getChildrens().get(i)))) {

				Annotation annot = new Annotation();
				getAntecedent(root.getChildrens().get(i),
						AppConstants.notAllowed, annot);

				root.getChildrens().get(i).setNpAnnotation(annot);
				if (root.getChildrens().size() == 2) {
					if (root.getChildrens().get(0).getCat().equals("NX")
							&& root.getChildrens().get(1).getCat().equals("NX")) {
					}
				}
				annot.setSentence(sentence);
				annot.setNode(root.getChildrens().get(i));
				setHeadNoun(annot);

			}
			findAllNP(root.getChildrens().get(i), sentence);

		}

	}

	private boolean NXasNounPhrase(TreeNode node) {
		if (node.getCat().equals("NX")) {
			if (node.getParent() != null) {
				if (!node.getParent().getCat().equals("NP") &&  !node.getParent().getCat().equals("COOD")&&  !node.getParent().getCat().equals("S")
						&& !node.getParent().equals("NX"))
					return true;
			}
		}

		return false;
	}

	private void findAntecedents(Sentence sentence, TreeNode root) {

		for (int i = 0; i < root.getChildrens().size(); i++) {
			if (root.getChildrens().get(i).getType().equalsIgnoreCase("cons")
					&& (root.getChildrens().get(i).getCat().equals("NP") || NXasNounPhrase(root
							.getChildrens().get(i)))) {
				TreeNode temp = root.getChildrens().get(i);
				boolean coord = false;
				Annotation annot = temp.getNpAnnotation();
				Word previous = null;
				try {
					previous = annot.getAnnotatedWords().get(0).getPrevious();
				} catch (Exception e) {

				}

			
				if (/* !childOrBig && */annot != null
						&& annot.getAnnotatedWords().size() > 0) {
					annot.setSentence(sentence);
					setNumberofAnnotation(annot);
					boolean isChild = false;
					if (annot.getHead() == null)
						isChild = true;
					setProteinReference(annot);
					Word last = annot.getAnnotatedWords().get(
							annot.getAnnotatedWords().size() - 1);

					if (last.getPos().equals("PN")
							|| last.getWord().equals(","))
						annot.getAnnotatedWords().remove(
								annot.getAnnotatedWords().size() - 1);
					int sz = sentence.getAntecedentCandidates().size() - 1;

					for (int j = sz; j >= 0; j--) {
						if (sentence.getAntecedentCandidates().get(j).getHead()
								.equals(annot.getHead())) {
							isChild = true;
							break;
						}

					}
					for(int j=0;j<annot.getAnnotatedWords().size();j++)
						if(isBiomedicalEntity(annot.getAnnotatedWords().get(j))){
						    annot.getAnnotatedWords().get(j).setBiomdicalEntity(true);
						    annot.incremntNumberOfBiomedicalEntity();
							annot.setBiomedicalEntity(true);
						}
					if (!isChild && !coord)
						sentence.getAntecedentCandidates().add(annot);
					else
						annot.getNode().setNpAnnotation(null);
				}
			}
			findAntecedents(sentence, root.getChildrens().get(i));
		}

	}

	// -----anaphoras---//
	private void findAnpahoras(Sentence sentence, TreeNode root) {
		for (int i = 0; i < root.getChildrens().size(); i++) {

			TreeNode current = root.getChildrens().get(i);
			if (current.getType().equals("tok")) {
				String POS = current.getWord().getPos();
				if (POS.startsWith("PR")) {
					 personalPronoun.pronounCheck(sentence, current);
					continue;
				}
				if (POS.startsWith("DT")) {
					if (current.getWord().getWord().equalsIgnoreCase("a")
							|| current.getWord().getWord()
									.equalsIgnoreCase("an"))
						continue;

					dn.definiteCheck(sentence, current);
					continue;
				}
				if (POS.startsWith("WDT") || POS.startsWith("WP")) {
					if (current.getWord().getWord().equalsIgnoreCase("who"))
						continue;
					Annotation annot = Filtering.findNPret(current);
					annot.setSentence(sentence);
					 sentence.getAnaphoraCandidates().add(annot);
					continue;
				}
			} else
				findAnpahoras(sentence, current);
		}
	}

	private void setProteinReference(Annotation annot) {
		ArrayList<ProteinAnnotation> proteins=BioNLPSolver.getProteinAnnotaion();
		int start=annot.getAnnotatedWords().get(0).getBeginWord();
		int end= annot.getAnnotatedWords().get(annot.getAnnotatedWords().size()-1).getBeginWord()+annot.getAnnotatedWords().get(annot.getAnnotatedWords().size()-1).getWord().length();
		for(int i=0;i<proteins.size();i++){
			if(proteins.get(i).getStart()>=start && proteins.get(i).getStart()<end)
				annot.incrementProteins();
		}
	
		boolean allWord = true;
		for (int i = 0; i < annot.getAnnotatedWords().size(); i++)
			if (!annot.getAnnotatedWords().get(i).isProtein()) {
				allWord = false;
				break;
			}
		if (allWord) {
			annot.setProteinReference(true);
			return;
		}

		int headPos = 0;
		for (int i = annot.getAnnotatedWords().size() - 1; i >= 0; i--) {
			if (annot.getAnnotatedWords().get(i).equals(annot.getHead())) {
				headPos = i;
			}		
		}
		if (annot.getAnnotatedWords().size() <= 10
				&& annot.getProteinNumber() > 2) {
			annot.setProteinReference(true);
			return;
		}
		String head = annot.getAnnotatedWords().get(headPos).getBaseForm();
		if (!head.equalsIgnoreCase("protein") && !head.equalsIgnoreCase("gene")) {
			return;
		}
		for (int i = headPos - 1; i >= headPos - 6 && i >= 0; i--) {
			if (annot.getAnnotatedWords().get(i).isProtein()) {
				annot.setProteinReference(true);
				return;
			}

		}

	}

	private void setHeadNoun(Annotation annot) {
		TreeNode root = annot.getNode();
		// from root find the head word which carries the number od the head
		// word
		while (root.getCat().equals("NP") || root.getCat().equals("NX")) {
			boolean found = false;
			for (int i = 0; i < root.getChildrens().size(); i++) {
				String category = root.getChildrens().get(i).getCat();
				if (category.equals("NX") || category.equals("COOD")) {
					root = root.getChildrens().get(i); 
					found = true;
					break;
				}

			}
			if (!found && root.getChildrens().size() == 2)
				for (int i = 0; i < root.getChildrens().size(); i++)
					if (root.getChildrens().get(i).getCat().equals("NP")) {
						root = root.getChildrens().get(i);
						found = true;
						break;
					}

			if (!found)
				break;
		}
		root = root.getChildrens().get(0);
		annot.setHead(root.getWord());
	}
	boolean isBiomedicalEntity(Word wr){
		String word=wr.getWord();
		
		if(word.length()<3){
			if(word.length()==2&& word.charAt(0)<='Z' && word.charAt(0)>='A' && wr.getPrevious()!=null && !wr.getPrevious().getWord().equals("."))
				return true;
			return false;
		}
    	boolean startLower=true;
    	if(word.endsWith("oid"))
    		return true;
    	if(word.endsWith("oids"))
    		return true;
    	//begin with digit
    	if(word.charAt(0)>='0' && word.charAt(0)<='9'){
    		boolean containsLetter=false;
    		for(int i=0;i<word.length();i++){
    			if((word.charAt(i)<='z' && word.charAt(i)>='a')||(word.charAt(i)<='Z' && word.charAt(i)>='A'))
    				containsLetter=true;
    		}
    		if(!containsLetter)
    			return false;
    		else
    			return true;
    	}
    	if(word.charAt(0)<='Z' && word.charAt(0)>='A')
    		startLower=false;
    	boolean hasSmall=false;
    	if(!startLower && wr.getPrevious()!=null && !wr.getPrevious().equals("."))
  			return true;
    	for(int i=1;i<word.length();i++){
    		if(startLower){
    			if((word.charAt(i)<='Z' && word.charAt(i)>='A')||(word.charAt(i)>='0' && word.charAt(i)<='9')|| word.charAt(i)=='-')
    					return true;
    		}
    		else{
    			if((word.charAt(i)>='0' && word.charAt(i)<='9')|| word.charAt(i)=='-')
    					return true;
    			if((word.charAt(i)>='a' && word.charAt(i)<='z')|| word.charAt(i)=='-')
    				hasSmall=true;
    			if(((word.charAt(i)>='A' && word.charAt(i)<='Z')|| word.charAt(i)=='-')&&hasSmall)
    				return true;
    			
    		}
    			
    	}
    	if(!startLower && wr.getPrevious()!=null && !wr.getPrevious().equals("."))
    	  			return true;
    	
    	return false;
    	
    	
    }

	private void setNumberofAnnotation(Annotation annot) {

		if (annot != null && annot.getHead() != null
				&& annot.getHead().getPos().equals("NNS"))
			annot.setNumber(10);
		if (annot != null && annot.getHead() != null
				&& annot.getHead().getPos().equals("NN")){ 
			if(annot.getProteinNumber()>1)
				annot.setNumber(10);
			else
			annot.setNumber(1);
		}
		// System.out.println(annot+"  "+annot.getNumb()+" "+annot.getHead());
	}

	// ---find antecedents of each anaphora
	public Annotation getAntecedentCandidates(Sentence sentence,
			Annotation anaphora) {
		Annotation antecedent=null;
		if (anaphora.getAnnotatedWords().get(0).getWord() // a bug
				.equalsIgnoreCase("which")||anaphora.getAnnotatedWords().get(0).getWord().toLowerCase() // a bug
				.startsWith("whos")||anaphora.getAnnotatedWords().get(0).getWord().toLowerCase() // a bug
				.startsWith("whom")
				|| anaphora.getAnnotatedWords().get(0).getWord()
						.equalsIgnoreCase("that")
				|| anaphora.getAnnotatedWords().get(0).getWord()
						.equalsIgnoreCase("itself")
				|| anaphora.getAnnotatedWords().get(0).getWord()
						.equalsIgnoreCase("themselves")){
			antecedent = relativePronouns.processRelative(anaphora);
			
		}
		else if (anaphora.getAnnotatedWords().get(0).getWord()
				.equalsIgnoreCase("it")
				|| anaphora.getAnnotatedWords().get(0).getWord()
						.equalsIgnoreCase("they")
				|| anaphora.getAnnotatedWords().get(0).getWord()
						.equalsIgnoreCase("them")) {
			antecedent = personalPronoun.annotationLookForIt(anaphora);
		} else if (anaphora.getAnnotatedWords().get(0).getWord()
				.equalsIgnoreCase("its")
				|| anaphora.getAnnotatedWords().get(0).getWord()
						.equalsIgnoreCase("their")) {
			antecedent = possesivePronouns.apply(anaphora);
		} else {
			if(anaphora.getAnnotatedWords().size()<4){}
			antecedent = dn.DNAntecedent(anaphora);
		}
		anaphora.setCoreference(antecedent);
		return antecedent;
	}

}
