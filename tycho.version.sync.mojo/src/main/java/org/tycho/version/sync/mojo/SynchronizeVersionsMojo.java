package org.tycho.version.sync.mojo;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import de.mukis.tvs.core.models.BundleManifest;
import de.mukis.tvs.core.models.Feature;
import de.mukis.tvs.core.models.FeatureProject;
import de.mukis.tvs.core.models.PluginProject;
import de.mukis.tvs.core.models.UpdateSiteProject;
import de.mukis.tvs.core.models.Versions;

/**
 * Goal which touches a timestamp file.
 * 
 * @goal sync-all
 * 
 * @phase initialize
 */
public class SynchronizeVersionsMojo extends AbstractMojo {

	/**
	 * Project base directory
	 * 
	 * @parameter expression="${project.basedir}"
	 * @required
	 */
	private File basedir;

	/**
	 * Project version
	 * 
	 * @parameter expression="${project.version}"
	 * @required
	 */
	private String version;

	/**
	 * Packaging type
	 * 
	 * @parameter expression="${project.packaging}"
	 * @required
	 */
	private String packaging;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (packaging.equals("eclipse-plugin") || packaging.equals("eclipse-test-plugin")) {
			syncPluginProject();
		} else if (packaging.equals("eclipse-feature")) {
			syncFeatureProject();
		} else if (packaging.equals("eclipse-repository")) {
			syncRepositoryProject();
		} else {
			getLog().warn("Cannot handle packaging type " + packaging + ". Skipping");
		}
	}

	private void syncPluginProject() throws MojoFailureException {
		try {
			getLog().info("Synchronize plugin project");
			PluginProject project = new PluginProject(basedir.toPath());
			BundleManifest manifest = project.get(BundleManifest.class);
			if (version.equals(manifest.getBundlePomVersion())) {
				getLog().info(manifest.getBundleSymbolicName() + " version " + manifest.getBundleVersion() + " in sync");
				return;
			}

			String bundleVersion = Versions.asBundleVersion(version);
			getLog().info("Setting version to " + bundleVersion);
			manifest.setBundleVersion(bundleVersion);
			for (String pkg : manifest.getExportedPackages().values()) {
				boolean succ = manifest.setExportedPackageVersion(BundleManifest.getRawPackage(pkg), bundleVersion);
				if (succ)
					getLog().debug("export-package " + pkg + " to version " + bundleVersion);
				else
					getLog().warn("export-package " + pkg + " to version " + bundleVersion + " FAILED");
			}

			project.update(manifest);
			getLog().info(manifest.getBundleSymbolicName() + " version " + manifest.getBundleVersion() + " synced successull");
		} catch (Exception e) {
			throw new MojoFailureException("No plugin project found", e);
		}
	}

	private void syncFeatureProject() throws MojoFailureException {
		try {
			getLog().info("Synchronize feature project");
			getLog().warn("NOT IMPLEMENTED YET");
			FeatureProject project = new FeatureProject(basedir.toPath());
			Feature feature = project.get(Feature.class);
			getLog().info(feature.getLabel() + " version " + feature.getVersion());
		} catch (Exception e) {
			throw new MojoFailureException("No feature project found", e);
		}
	}

	private void syncRepositoryProject() throws MojoFailureException {
		try {
			getLog().info("Synchronize repository project");
			getLog().warn("NOT IMPLEMENTED YET");
			UpdateSiteProject site = new UpdateSiteProject(basedir.toPath());
			getLog().info(site.getName() + " version " + site.getVersion());
		} catch (Exception e) {
			throw new MojoFailureException("No repository project found", e);
		}
	}

}
