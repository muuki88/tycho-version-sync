package de.mukis.tvs.core.models;

import java.nio.file.Files;
import java.nio.file.Path;

import de.mukis.tvs.core.ProjectException;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.0.2
 *
 */
public abstract class AbstractProject implements IProject {

	private Path root;
	
	public AbstractProject() {	}
	
	public AbstractProject(Path root) {
		read(root);
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
	
	@Override
	public void read(Path path) throws ProjectException {
		if (!Files.exists(path))
			throw new ProjectException("Path [" + path + "] does not exist.");
		if (!isProjectPath(path))
			throw new ProjectException("Path [" + path + "] is not a "+ getClass().getSimpleName() +" project.");
		this.root = path;
	}

}
