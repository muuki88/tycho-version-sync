package de.mukis.tvs.core.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.0.2
 * 
 */
public class Feature implements IWriteable {

	private String id;
	private String label;
	private String version;

	private Document featureXml;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		featureXml.getElementsByTagName("feature").item(0).getAttributes().getNamedItem("id").setNodeValue(id);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
		featureXml.getElementsByTagName("feature").item(0).getAttributes().getNamedItem("label").setNodeValue(label);
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
		featureXml.getElementsByTagName("feature").item(0).getAttributes().getNamedItem("version").setNodeValue(version);
	}

	public void write(OutputStream out) throws IOException {
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(featureXml);
			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);
		} catch (TransformerException e) {
			new IOException("Error while transforming xml", e);
		}

	}

	public static Feature parse(Path path) throws IOException, ParserConfigurationException, SAXException {
		if (!Files.exists(path))
			return null;
		Feature feature = new Feature();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try (InputStream in = Files.newInputStream(path)) {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(in);
			feature.featureXml = doc;

			NodeList featureTags = doc.getElementsByTagName("feature");
			for (int i = 0; i < featureTags.getLength(); i++) {
				Node item = featureTags.item(i);
				NamedNodeMap attributes = item.getAttributes();
				Node id = attributes.getNamedItem("id");
				Node label = attributes.getNamedItem("label");
				Node version = attributes.getNamedItem("version");
				feature.id = id.getTextContent();
				feature.label = label.getTextContent();
				feature.version = version.getTextContent();
			}
		}

		return feature;
	}
}
