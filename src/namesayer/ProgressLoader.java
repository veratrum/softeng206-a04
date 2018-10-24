package namesayer;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ProgressLoader {

	private Progress progress;

	private File userdata;
	private File progressData;

	public ProgressLoader(Progress progress) {
		this.progress = progress;
	}

	public void loadProgress() {
		userdata = new File("userdata");
		if (!userdata.exists()) {
			userdata.mkdir();
		}

		progressData = new File(userdata, "progressdata.xml");

		// note that if no progress file exists, we will generate a blank one at the end of this method.
		if (progressData.exists()) {
			/* modified from http://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
			this block parses progressdata.xml */
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(progressData);
				document.getDocumentElement().normalize();

				NodeList ratingNodes = document.getElementsByTagName("rating");

				for (int i = 0; i < ratingNodes.getLength(); i++) {
					Element ratingElement = (Element) ratingNodes.item(i);

					String rating = ratingElement.getTextContent();

					progress.addRatingWithoutSaving(rating);
				}
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		saveProgress();
	}

	public void saveProgress() {
		// modified from http://www.tutorialspoint.com/java_xml/java_dom_create_document.htm
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();

			Element rootElement = document.createElement("progress");
			document.appendChild(rootElement);

			for (String rating: progress.getAllRatings()) {
				Element ratingElement = document.createElement("rating");

				ratingElement.appendChild(document.createTextNode(rating));

				rootElement.appendChild(ratingElement);
			}

			// generate xml and write it to disk
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(progressData);
			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
}
