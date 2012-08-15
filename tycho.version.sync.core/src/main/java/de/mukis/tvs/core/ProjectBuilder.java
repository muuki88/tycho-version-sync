package de.mukis.tvs.core;

import java.nio.file.Path;

import de.mukis.tvs.core.models.FeatureProject;
import de.mukis.tvs.core.models.PluginProject;
import de.mukis.tvs.core.models.UpdateSiteProject;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.0.2
 *
 */
public class ProjectBuilder {

	private static final FeatureProject f = new FeatureProject();
	private static final PluginProject p = new PluginProject();
	private static final UpdateSiteProject u = new UpdateSiteProject();
	
	public static boolean isFeatureProject(Path path) {
		return f.isProjectPath(path);
	}
	
	public static boolean isPluginProject(Path path) {
		return p.isProjectPath(path);
	}
	
	public static boolean isUpdateSiteProject(Path path) {
		return u.isProjectPath(path);
	}
	
	
}
