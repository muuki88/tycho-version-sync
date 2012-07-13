package de.mukis.tvs.core.models;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Project {

	private final Path root;

	private final Path manifestPath;
	private final Path manifestSearchPath = Paths.get("META-INF", "MANIFEST.MF");
	
	private final Path featurePath;
	private final Path featureSearchPath = Paths.get("feature.xml");
	
	private final Path pomPath;
	private final Path pomSearchPath = Paths.get("pom.xml");
	
	private final Path buildPropertiesPath;
	private final Path buildPropertiesSearchPath = Paths.get("build.properties");

	public Project(Path root) {
		this.root = root;
		manifestPath = root.resolve(manifestSearchPath);
		featurePath = root.resolve(featureSearchPath);
		pomPath = root.resolve(pomSearchPath);
		buildPropertiesPath = root.resolve(buildPropertiesSearchPath);
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
		StringBuilder builder = new StringBuilder();
		builder.append("Project [").append(root).append("]\n");

		if (manifestPath != null && Files.exists(manifestPath)) {
			builder.append("- MANIFEST.MF = ").append(manifestSearchPath).append("\n");
		}
		if (featurePath != null && Files.exists(featurePath)) {
			builder.append("- feature.xml = ").append(featureSearchPath).append("\n");
		}
		if (pomPath != null && Files.exists(pomPath)) {
			builder.append("- pom.xml = ").append(pomSearchPath).append("\n");
		}
		if (buildPropertiesPath != null && Files.exists(buildPropertiesPath)) {
			builder.append("- build.properties = ").append(buildPropertiesSearchPath).append("\n");
		}
		return builder.toString();
	}

}
