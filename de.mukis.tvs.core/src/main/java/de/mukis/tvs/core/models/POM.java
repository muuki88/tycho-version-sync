package de.mukis.tvs.core.models;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class POM {

	private String groupId;
	private String artifactId;
	private String version;

	private String parentGroupId;
	private String parentArtifactId;
	private String parentVersion;

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	/**
	 * 
	 * @return version or parentVersion if version equals null
	 */
	public String getVersion() {
		return version != null ? version : parentVersion;
	}

	public String getParentGroupId() {
		return parentGroupId;
	}

	public String getParentArtifactId() {
		return parentArtifactId;
	}

	public String getParentVersion() {
		return parentVersion;
	}
	
	public static POM parse(Path path) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		if (!Files.exists(path))
			return null;

		POM pom = new POM();
		// Standard of reading a XML file
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try (InputStream in = Files.newInputStream(path)) {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(in);

			// Read parent / artifact version
			NodeList versionList = doc.getElementsByTagName("version");
			for (int i = 0; i < versionList.getLength(); i++) {
				Node node = versionList.item(i);
				String parentNode = node.getParentNode().getNodeName();
				if ("project".equals(parentNode)) {
					pom.version = node.getTextContent();
				} else if ("parent".equals(parentNode)) {
					pom.parentVersion = node.getTextContent();
				}
			}

			// Read parent / artifact ids
			NodeList artifactIds = doc.getElementsByTagName("artifactId");
			for (int i = 0; i < artifactIds.getLength(); i++) {
				Node node = artifactIds.item(i);
				String parentNode = node.getParentNode().getNodeName();
				if ("project".equals(parentNode)) {
					pom.artifactId = node.getTextContent();
				} else if ("parent".equals(parentNode)) {
					pom.parentArtifactId = node.getTextContent();
				}
			}

			// Read parent / artifactGroup ids
			NodeList groupIds = doc.getElementsByTagName("groupId");
			for (int i = 0; i < groupIds.getLength(); i++) {
				Node node = groupIds.item(i);
				String parentNode = node.getParentNode().getNodeName();
				if ("project".equals(parentNode)) {
					pom.groupId = node.getTextContent();
				} else if ("parent".equals(parentNode)) {
					pom.parentGroupId = node.getTextContent();
				}
			}
		}
		return pom;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("POM [");
		if (groupId != null) {
			builder.append("groupId=");
			builder.append(groupId);
			builder.append(", ");
		}
		if (artifactId != null) {
			builder.append("artifactId=");
			builder.append(artifactId);
			builder.append(", ");
		}
		if (version != null) {
			builder.append("version=");
			builder.append(version);
			builder.append(", ");
		}
		if (parentGroupId != null) {
			builder.append("parentGroupId=");
			builder.append(parentGroupId);
			builder.append(", ");
		}
		if (parentArtifactId != null) {
			builder.append("parentArtifactId=");
			builder.append(parentArtifactId);
			builder.append(", ");
		}
		if (parentVersion != null) {
			builder.append("parentVersion=");
			builder.append(parentVersion);
		}
		builder.append("]");
		return builder.toString();
	}
	
	
}
