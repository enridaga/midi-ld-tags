package enridaga.midild.spots;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.open.kmi.discou.spotlight.SpotlightAnnotation;
import uk.ac.open.kmi.discou.spotlight.SpotlightClient;
import uk.ac.open.kmi.discou.spotlight.SpotlightResponse;

public class Spotter {
	private static final Logger L = LoggerFactory.getLogger(Spotter.class);

	public static void main(String[] args) throws IOException {
		String service = args[0];
		String confidence = args[1];
		String filenames = args[2];
		String outputFilename = args[3];
		perform(service, confidence, filenames, outputFilename);
	}

	private static void perform(String service, String confidenceS, String filenames, String outputFilename) throws IOException {
		double confidence = Double.parseDouble(confidenceS);
		String property = "http://purl.org/dc/terms/subject";
		SpotlightClient client = new SpotlightClient(service);
		String text;
		Reader in = new FileReader(filenames);
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
		File output = new File(outputFilename);
		output.createNewFile();
		PrintStream W = new PrintStream(new FileOutputStream(output, true));
		for (CSVRecord record : records) {
			String filename = record.get(0);
			String id = record.get(1);
			L.info("{}", id);
			text = filename.replaceAll("[^a-zA-Z0-9]", " ");
			SpotlightResponse r = client.perform(text, confidence, 0);
			List<SpotlightAnnotation> anns = SpotlightClient.toList(r.getXml());
			for (SpotlightAnnotation a : anns) {
				L.info("{} <{}>", a.getUri(), a.getTypes());
				W.append("<");
				W.append(id);
				W.append(">");
				W.append(" ");
				W.append("<");
				W.append(property);
				W.append(">");
				W.append(" ");
				W.append("<");
				W.append(a.getUri());
				W.append(">");
				W.append(" .");
				W.println();
				for (String s : a.getTypes()) {
					if (s.trim().equals(""))
						continue;
					String type = s;
					if (type.startsWith("DBpedia:")) {
						type = new StringBuilder().append("http://dbpedia.org/resource/").append(type.substring(8)).toString();
					} else if (type.startsWith("Http:")) {
						type = type.replace('H', 'h');
					} else if (type.startsWith("Schema:")) {
						type = new StringBuilder().append("http://schema.org/").append(type.substring(7)).toString();
					}
					W.append("<");
					W.append(a.getUri());
					W.append(">");
					W.append(" ");
					W.append("a");
					W.append(" ");
					W.append("<");
					W.append(type);
					W.append(">");
					W.append(" .");
					W.println();
				}
			}
			W.flush();
		}
		W.close();
	}
}
