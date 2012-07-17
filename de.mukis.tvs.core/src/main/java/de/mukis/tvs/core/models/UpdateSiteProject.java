package de.mukis.tvs.core.models;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.0.2
 * 
 */
public class UpdateSiteProject extends AbstractProject {

	public UpdateSiteProject() {
	}
	
	public UpdateSiteProject(Path root) {
		super(root);
	}

	@Override
	public String getName() {
		return getRoot().getFileName().toString();
	}

	@Override
	public String getVersion() {
		return "N/A";
	}

	@Override
	public void update(Object file) throws Exception {

	}

	@Override
	public boolean isProjectPath(Path path) {
		if (!Files.exists(path))
			return false;

		boolean returns = false;
		returns = returns || Files.exists(path.resolve("site.xml"));
		returns = returns || Files.exists(path.resolve("category.xml"));
		return returns;
	}

}
