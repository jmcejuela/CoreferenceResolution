package EnjuParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class EnjuParser {
	private Process enjuProcess;
	private BufferedWriter enjuInputWriter;
	private BufferedReader enjuOutputReader;
	private BufferedReader enjuErrorReader;

	public EnjuParser() {
		this.init();
	}

	public void init() {
		Runtime rt = Runtime.getRuntime();
		try {
			System.out.print("Loading Enju....");
			this.enjuProcess = rt
					.exec("resources/support/enju/enju -xml -genia");
			OutputStreamWriter outStreamWriter = new OutputStreamWriter(
					this.enjuProcess.getOutputStream());
			this.enjuInputWriter = new BufferedWriter(outStreamWriter);
			InputStreamReader inStreamReader = new InputStreamReader(
					this.enjuProcess.getInputStream());
			this.enjuOutputReader = new BufferedReader(inStreamReader);
			InputStreamReader errorStreamReader = new InputStreamReader(
					this.enjuProcess.getErrorStream());
			this.enjuErrorReader = new BufferedReader(errorStreamReader);
			String line = this.enjuErrorReader.readLine();
			while (!line.equals("Ready")) {
				line = this.enjuErrorReader.readLine();
			}
			System.out.println("Ready!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		this.enjuProcess.destroy();
	}

	public String ParserSentenceToXml(String sentence) {
		String xmlStr = "";
		try {
			this.enjuInputWriter.write(sentence);
			this.enjuInputWriter.newLine();
			this.enjuInputWriter.flush();
			xmlStr = this.enjuOutputReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return xmlStr;
	}

}