package de.mukis.tvs.core.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.eclipse.osgi.util.ManifestElement;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

import com.google.common.base.Joiner;

public class BundleManifestTest {

	private BundleManifest mf;

	@Before
	public void setup() throws IOException, BundleException, URISyntaxException {
		mf = BundleManifest.parse(Paths.get(getClass().getResource("MANIFEST.MF").toURI()));
	}

	@Test
	public void changeBundleVersion() throws IOException, BundleException {
		String oldVersion = mf.changeBundleVersion("1.0.1");
		assertEquals("0.0.1", oldVersion);

		BundleManifest manifest = simulatedWriteRead();
		assertEquals("1.0.1", manifest.getBundleVersion());
	}

	@Test
	public void changeExportedPackageVersion() throws IOException, BundleException {
		String pkg = "de.mukis.tvs.core";
		String version = "1.0.1";
		assertTrue(mf.changeExportedPackageVersion(pkg, version));
		
		BundleManifest manifest = simulatedWriteRead();
		
		String value = manifest.getExportedPackages().get(pkg);
		ManifestElement[] elements = ManifestElement.parseHeader(Constants.EXPORT_PACKAGE, value);
		for (ManifestElement e : elements) {
			if(e.getValue().equals(pkg)) {
				assertEquals(version, e.getAttribute(Constants.VERSION_ATTRIBUTE));
			}
		}
		
		assertEquals("0.0.1", manifest.getBundleVersion());
	}

	@Test
	public void changeImportedPackageVersion() throws IOException, BundleException {
		String pkg = "org.osgi.framework";
		String version = "2.0.0";
		assertTrue(mf.changeImportedPackageVersion(pkg, version));
		BundleManifest manifest = simulatedWriteRead();
		
		String value = manifest.getImportedPackages().get(pkg);
		ManifestElement[] elements = ManifestElement.parseHeader(Constants.IMPORT_PACKAGE, value);
		for (ManifestElement e : elements) {
			if(e.getValue().equals(pkg)) {
				assertEquals(version, e.getAttribute(Constants.VERSION_ATTRIBUTE));
			}
		}
		
		assertEquals("0.0.1", manifest.getBundleVersion());
	}

	@Test
	public void changeRequiredBundleVersion() throws IOException, BundleException {
		String pkg = "nz.ac.waikato.cs.weka";
		String version = "4.0.0";
		assertTrue(mf.changeRequiredBundleVersion(pkg, version));
		BundleManifest manifest = simulatedWriteRead();
		
		String value = manifest.getRequiredBundles().get(pkg);
		ManifestElement[] elements = ManifestElement.parseHeader(Constants.REQUIRE_BUNDLE, value);
		for (ManifestElement e : elements) {
			if(e.getValue().equals(pkg)) {
				assertEquals(version, e.getAttribute(Constants.BUNDLE_VERSION_ATTRIBUTE));
			}
		}
		
		assertEquals("0.0.1", manifest.getBundleVersion());
	}

	@Test
	public void testRegexReplacementRequiredBundle() {
		String version = "1.0.0";
		String value = "nz.ac.waikato.cs.weka;bundle-version=\"3.7.5\";visibility:=reexport";
		String replaced = value.replaceFirst("(bundle-version=)\"(.*?)\"", "$1\"" + version + "\"");
		assertEquals("nz.ac.waikato.cs.weka;bundle-version=\"1.0.0\";visibility:=reexport", replaced);
	}

	@Test
	public void testRegexReplacementExportedBundle() {
		String version = "1.0.0";
		String value = "nz.ac.waikato.cs.weka;version=\"3.7.5\";visibility:=reexport";
		String replaced = value.replaceFirst("(version=)\"(.*?)\"", "$1\"" + version + "\"");
		assertEquals("nz.ac.waikato.cs.weka;version=\"1.0.0\";visibility:=reexport", replaced);
	}

	@Test
	public void testJoinPackageStrings() {
		String join = Joiner.on(",").join(mf.getExportedPackages().values());
		assertEquals("de.mukis.tvs.core;version=\"0.0.1\",de.mukis.tvs.core.models;version=\"0.0.1\"", join);
	}
	
	private BundleManifest simulatedWriteRead() throws IOException, BundleException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		mf.write(out);
		return BundleManifest.parse(new ByteArrayInputStream(out.toByteArray()));
	}

}
