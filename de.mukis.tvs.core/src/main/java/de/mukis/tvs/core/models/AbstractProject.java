package de.mukis.tvs.core.models;

import java.nio.file.Path;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.0.2
 *
 */
public abstract class AbstractProject implements IProject {

	private final Path root;
	
	public AbstractProject(Path root) {
		this.root = root;
	}

	@Override
	public Path getRoot() {
		return root;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Class<T> file) throws Exception {
		if(file.isAssignableFrom(POM.class)) {
			return (T) POM.parse(root.resolve("pom.xml"));
		}
		return null;
	}

}
