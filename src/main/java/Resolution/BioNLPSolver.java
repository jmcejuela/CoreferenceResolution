package Resolution;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import performance.CreateA2File;
import EnjuParser.EnjuPreprocessing;
import SentenceSplitter.GeniaSS;
import SentenceSplitter.SentenceSpliterImpl;
import Tree.AppConstants;
import dataStructure.Article;
import dataStructure.Paragraph;
import dataStructure.Section;
import dataStructure.Sentence;
import dataStructure.ProteinAnnotation;

public class BioNLPSolver {
	public Article article; // 10101249 10090942 10050877
	public static String fileName = "PMC-1134658-11-Methods";
	public static String filePath = "resources/inputData/";
	public static String theArticle = "";
	public static String resultFolder = "resources/results/";
	public static BufferedWriter bw;
	public static ArrayList<ProteinAnnotation> proteinAnnotation;


	public void readFromFile(String path) throws Exception {
		article = new Article();
		theArticle = "";
		BufferedReader br = new BufferedReader(new FileReader(path));
		Section section = new Section();
		String a;
		Paragraph par = new Paragraph();
		while ((a = br.readLine()) != null) {
			theArticle = theArticle + a + " ";
		}
		SentenceSpliterImpl sentenceSp = new GeniaSS();
		ArrayList<Sentence> sentences = sentenceSp.apply(theArticle);
		for (int j = 0; j < sentences.size(); j++) {
			sentences.get(j).setParagraph(par);
			par.getSentences().add(sentences.get(j));

		}

		section.getParagrphs().add(par);
		br.close();
		article.getSections().add(section);

	}

	public static EnjuPreprocessing processor = new EnjuPreprocessing();
	public static ArrayList<String> filt;

	public void processArticle() throws Exception {
		article.addSentencesToArticle();
		ArrayList<Sentence> sentences = article.getSentences();
		for (Sentence sent : sentences) {
			processor.apply(sent);		
		}
		article.resolve(processor);

		for (int i = 0; i < article.getRelations().size(); i++) {
		}

		ArrayList<Integer> list = getProteins(filePath + fileName + ".a1");
		CreateA2File.CreatFile(article, fileName, list);

	}

	public ArrayList<Integer> getProteins(String pathname) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(pathname));
		ArrayList<Integer> prot = new ArrayList<Integer>();
		String read;
		while ((read = reader.readLine()) != null) {
			String temp[] = read.split(" ");
			prot.add(Integer.parseInt(temp[1]));
		}
		reader.close();
		return prot;

	}

	public static int charCounter = 0;
	static public HashMap<Integer, ProteinAnnotation> protAnnot;

	public static void main(String[] args) throws Exception {
		File file = new File("resources/inputData");
		File[] a = file.listFiles();
		Arrays.sort(a);
		AppConstants.init();
		BioNLPSolver pp = new BioNLPSolver();
		for (int i = 0; i < a.length; i++) {
			if (a[i].getAbsolutePath().endsWith(".txt")) {
				processor.charCounter = 0;
				try {

					BioNLPSolver.fileName = a[i].getName().substring(0,
							a[i].getName().length() - 4);
					proteinAnnotation = getAnnotationProtein();
					pp.readFromFile(a[i].getAbsolutePath());
					pp.processArticle();
					charCounter = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	static ArrayList<dataStructure.ProteinAnnotation> proteins;

	public static ArrayList<dataStructure.ProteinAnnotation> getAnnotationProtein()
			throws Exception {
		ArrayList<ProteinAnnotation> protAnnotation = new ArrayList<ProteinAnnotation>();
		HashMap<Integer, ProteinAnnotation> hashProtein = new HashMap<Integer, ProteinAnnotation>();
		BufferedReader reader = new BufferedReader(new FileReader(filePath
				+ fileName + ".a1"));
		ArrayList<Integer> prot = new ArrayList<Integer>();
		String read;
		while ((read = reader.readLine()) != null) {
			String temp[] = read.split(" ");
			prot.add(Integer.parseInt(temp[1]));
			ProteinAnnotation protein = new ProteinAnnotation(read);
			protAnnotation.add(protein);
			hashProtein.put(protein.getStart(), protein);
		}
		protAnnot = hashProtein;
		reader.close();
		proteins = protAnnotation;
		return protAnnotation;
	}

	public static ArrayList<ProteinAnnotation> getProteinAnnotaion() {
		return proteins;
	}

	public static HashMap<Integer, ProteinAnnotation> getHashedProtein() {
		return protAnnot;
	}

}
