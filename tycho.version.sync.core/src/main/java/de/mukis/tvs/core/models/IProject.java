package de.mukis.tvs.core.models;

import java.nio.file.Path;

import de.mukis.tvs.core.ProjectException;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.0.2
 *
 */
public interface IProject {

	public String getName();
	
	public String getVersion();
	
	/**
	 * 
	 * @return root path of the project
	 */
	public Path getRoot();
	
	/**
	 * 
	 * @param file - BuildProperties | BundleManifest | Feature | POM
	 * @return parsed object or null
	 * @throws Exception
	 */
	public <T> T get(Class<T> file) throws Exception;
	
	/**
	 * Updates the file inside the project with the given
	 * parameter.
	 * 
	 * @param file - BuildProperties | BundleManifest | Feature | POM
	 * @throws Exception
	 */
	public void update(Object file) throws Exception;
	
	/**
	 * 
	 * @param path
	 * @throws ProjectException
	 */
	public void read(Path path) throws ProjectException;
	
	public boolean isProjectPath(Path path);
}
