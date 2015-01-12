package SentenceSplitter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import dataStructure.Sentence;

public class GeniaSS implements SentenceSpliterImpl {

	@Override
	public ArrayList<Sentence> apply(String paragraph) {
		ArrayList<Sentence> sentences = new ArrayList<Sentence>();
		File input = new File("resources/support/geniass/input.txt");
		File output = new File("resources/support/geniass/output.txt");
		try {
			PrintWriter fw = new PrintWriter(input, "UTF-8");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(paragraph);
			bw.close();
			String root = System.getProperty("user.dir");
			File inputFolder = new File(root + "/resources/support/geniass/");
			ProcessBuilder pb = new ProcessBuilder(root
					+ "/resources/support/geniass/geniass", "input.txt",
					"output.txt");
			pb.directory(inputFolder);
			Process process = pb.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line;
			while ((line = r.readLine()) != null) {
			}
			FileReader fr = new FileReader(output);
			BufferedReader br = new BufferedReader(fr);
			String sentenceLine;
			int counter = 1;
			while ((sentenceLine = br.readLine()) != null) {
				Sentence tmpSentence = new Sentence();
				tmpSentence.setSentence(sentenceLine);
				tmpSentence.setNrId(counter);
				Sentence previous;
				if (counter > 1) {
					previous = sentences.get(sentences.size() - 1);
					tmpSentence.setPrevious(previous);
					previous.setNext(tmpSentence);
				}
				counter++;
				sentences.add(tmpSentence);
			}

		} catch (IOException e) {
		}
		return sentences;
	}

}
