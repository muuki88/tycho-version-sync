package de.mukis.tvs.core.models;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import de.mukis.tvs.core.ProjectException;

public class PluginProject extends AbstractProject {

	public PluginProject(Path root) {
		super(root);
	}

	@Override
	public String getName() {
		try {
			BundleManifest manifest = get(BundleManifest.class);
			return manifest.getBundleSymbolicName();
		} catch (Exception e) {
			return "Unable to read name. " + e.getMessage();
		}
	}

	@Override
	public String getVersion() {
		try {
			BundleManifest manifest = get(BundleManifest.class);
			return manifest.getBundleVersion();
		} catch (Exception e) {
			return "Unable to read name. " + e.getMessage();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Class<T> file) throws Exception {
		if (file.isAssignableFrom(BundleManifest.class)) {
			return (T) BundleManifest.parse(getRoot().resolve("META-INF").resolve("MANIFEST.MF"));
		} else if (file.isAssignableFrom(BuildProperties.class)) {
			return (T) BuildProperties.parse(getRoot().resolve("build.properties"));
		}
		return super.get(file);
	}
	
	@Override
	public void update(Object file) throws Exception {
		if(file instanceof BundleManifest) {
			try (OutputStream out = Files.newOutputStream(getRoot().resolve("META-INF").resolve("MANIFEST.MF"))) {
				((BundleManifest)file).write(out);
			}
		} else if (file instanceof BuildProperties) {
			try (OutputStream out = Files.newOutputStream(getRoot().resolve("build.properties"))) {
				((BuildProperties)file).write(out);
			}
		}
	}

	@Override
	public IProject from(Path path) throws ProjectException {
		if (!Files.exists(path))
			throw new ProjectException("Path [" + path + "] does not exist.");
		if (isProjectPath(path))
			throw new ProjectException("Path [" + path + "] is not a feature project.");
		return new PluginProject(path);
	}

	@Override
	public boolean isProjectPath(Path path) {
		if (!Files.exists(path))
			return false;

		if (!Files.exists(path.resolve("META-INF").resolve("MANIFEST.MF")))
			return false;

		if (!Files.exists(path.resolve("build.properties")))
			return false;
		return true;
	}

}
