package de.mukis.tvs.core.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

import com.google.common.base.Joiner;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.0.1
 * @see http://www.eclipsezone.com/eclipse/forums/t116172.html
 * 
 */
public class BundleManifest extends Manifest {

	private String bundleVersion;

	/** ManifestElement.getValue -> ManifestElement.toString **/
	private final Map<String, String> exportedPackages = new HashMap<>();
	private final Map<String, String> importedPackages = new HashMap<>();
	private final Map<String, String> requiredBundles = new HashMap<>();

	protected BundleManifest() {	
		super();
	}
	
	protected BundleManifest(InputStream in) throws IOException {
		super(in);
	}
	
	/**
	 * 
	 * @param version - x.y.z[.qualifier]
	 * @return - Old version
	 */
	public String changeBundleVersion(String version) {
		String oldVersion = bundleVersion;
		bundleVersion = version;
		return oldVersion;
	}
	
	/**
	 * 
	 * @param pkg - package to update, e.g. com.example
	 * @param version - x.y.z[.qualifier]
	 * @return
	 */
	public boolean changeExportedPackageVersion(String pkg, String version) {
		return changePackage(pkg, version, exportedPackages);
	}
	
	/**
	 * 
	 * @param pkg - package to update, e.g. com.example
	 * @param version - x.y.z[.qualifier] or (x.y.z,a.b.c]
	 * @return
	 */
	public boolean changeImportedPackageVersion(String pkg, String version) {
		return changePackage(pkg, version, importedPackages);
	}
	
	/**
	 * 
	 * @param pkg - package to update, e.g. com.example
	 * @param version - x.y.z[.qualifier] or (x.y.z,a.b.c]
	 * @return
	 */
	public boolean changeRequiredBundleVersion(String pkg, String version) {
		if(!requiredBundles.containsKey(pkg))
			return false;
		String value = requiredBundles.get(pkg);
		String newValue = value.replaceFirst("(bundle-version=)\"(.*?)\"", "$1\"" + version + "\"");
		requiredBundles.put(pkg, newValue);
		return true;
	}
	
	private boolean changePackage(String pkg, String version, Map<String, String> properties) {
		if(!properties.containsKey(pkg))
			return false;
		String value = properties.get(pkg);
		String newValue = value.replaceFirst("(version=)\"(.*?)\"", "$1\"" + version + "\"");
		properties.put(pkg, newValue);
		return true;
	}
	
	@Override
	public void write(OutputStream out) throws IOException {
		//update version
		getMainAttributes().putValue(Constants.BUNDLE_VERSION, bundleVersion);
		
		//update exported packages
		String exported = Joiner.on(",").join(exportedPackages.values());
		getMainAttributes().putValue(Constants.EXPORT_PACKAGE, exported);

		//update imported packages
		String imported = Joiner.on(",").join(importedPackages.values());
		getMainAttributes().putValue(Constants.IMPORT_PACKAGE, imported);
		
		//update required bundles
		String required = Joiner.on(",").join(requiredBundles.values());
		getMainAttributes().putValue(Constants.REQUIRE_BUNDLE, required);
		
		super.write(out);
	}
	
	public String getBundleVersion() {
		return bundleVersion;
	}

	public Map<String, String> getExportedPackages() {
		return exportedPackages;
	}

	public Map<String, String> getImportedPackages() {
		return importedPackages;
	}

	public Map<String, String> getRequiredBundles() {
		return requiredBundles;
	}

	public static BundleManifest parse(Path path) throws IOException, BundleException {
		if (!Files.exists(path))
			return null;
		
		try (InputStream in = Files.newInputStream(path)) {
			return parse(in);
		}
	}
	
	public static BundleManifest parse(InputStream in) throws IOException, BundleException {
		BundleManifest manifest = new BundleManifest(in);
		Attributes attributes = manifest.getMainAttributes();
		
		String version = attributes.getValue(Constants.BUNDLE_VERSION);
		manifest.bundleVersion = version;
		
		String exported = attributes.getValue(Constants.EXPORT_PACKAGE);
		ManifestElement[] expPackages = ManifestElement.parseHeader(Constants.EXPORT_PACKAGE, exported);
		for (ManifestElement e : expPackages) {
			manifest.getExportedPackages().put(e.getValue(), e.toString());
		}
		
		String imported = attributes.getValue(Constants.IMPORT_PACKAGE);
		ManifestElement[] impPackages = ManifestElement.parseHeader(Constants.IMPORT_PACKAGE, imported);
		for (ManifestElement e : impPackages) {
			manifest.getImportedPackages().put(e.getValue(), e.toString());
		}
		
		String required = attributes.getValue(Constants.REQUIRE_BUNDLE);
		ManifestElement[] reqBundles = ManifestElement.parseHeader(Constants.REQUIRE_BUNDLE, required);
		for (ManifestElement e : reqBundles) {
			manifest.getRequiredBundles().put(e.getValue(), e.toString());
		}
		
		return manifest;
	}
}
