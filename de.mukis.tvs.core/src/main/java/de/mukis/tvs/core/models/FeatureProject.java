package de.mukis.tvs.core.models;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import de.mukis.tvs.core.ProjectException;

public class FeatureProject extends AbstractProject {

	protected FeatureProject(Path root) {
		super(root);
	}

	@Override
	public FeatureProject from(Path path) throws ProjectException {
		if (!Files.exists(path))
			throw new ProjectException("Path [" + path + "] does not exist.");
		if (isProjectPath(path))
			throw new ProjectException("Path [" + path + "] is not a feature project.");

		return new FeatureProject(path);
	}

	@Override
	public boolean isProjectPath(Path path) {
		if (!Files.exists(path))
			return false;

		if (!Files.exists(path.resolve("feature.xml")))
			return false;

		if (!Files.exists(path.resolve("build.properties")))
			return false;

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Class<T> file) throws Exception {
		if (file.isAssignableFrom(Feature.class)) {
			return (T) Feature.parse(getRoot().resolve("feature.xml"));
		} else if (file.isAssignableFrom(BuildProperties.class)) {
			return (T) BuildProperties.parse(getRoot().resolve("build.properties"));
		}
		return super.get(file);
	}
	
	@Override
	public void update(Object file) throws Exception {
		if(file instanceof Feature) {
			try (OutputStream out = Files.newOutputStream(getRoot().resolve("feature.xml"))) {
				((Feature)file).write(out);
			}
		} else if (file instanceof BuildProperties) {
			try (OutputStream out = Files.newOutputStream(getRoot().resolve("build.properties"))) {
				((BuildProperties)file).write(out);
			}
		}
	}

	@Override
	public String getVersion() {
		try {
			Feature feature = get(Feature.class);
			return feature.getVersion();
		} catch (Exception e) {
			return "unable to read version: " + e.getMessage();
		}
	}

	@Override
	public String getName() {
		try {
			Feature feature = get(Feature.class);
			return feature.getLabel();
		} catch (Exception e) {
			return "unable to read name: " + e.getMessage();
		}
	}


}
