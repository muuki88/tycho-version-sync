package de.mukis.tvs.core.models;

import java.nio.file.Path;

public class Project {

	private final Path root;

	private final Path manifestPath;
	private final Path featurePath;
	private final Path pomPath;
	private final Path buildPropertiesPath;

	public Project(Path root) {
		this.root = root;
		manifestPath = root.resolve("META-INF").resolve("MANIFEST.MF");
		featurePath = root.resolve("feature.xml");
		pomPath = root.resolve("pom.xml");
		buildPropertiesPath = root.resolve("build.properties");
	}

	public Path getRoot() {
		return root;
	}
	
	

	public Path getManifestPath() {
		return manifestPath;
	}

	public Path getFeaturePath() {
		return featurePath;
	}

	public Path getPomPath() {
		return pomPath;
	}

	public Path getBuildPropertiesPath() {
		return buildPropertiesPath;
	}

	@Override
	public String toString() {
		return "Project [root=" + root + ", manifest=" + manifestPath + ", feature=" + featurePath + ", pom=" + pomPath + ", buildproperties="
				+ buildPropertiesPath + "]";
	}

}
