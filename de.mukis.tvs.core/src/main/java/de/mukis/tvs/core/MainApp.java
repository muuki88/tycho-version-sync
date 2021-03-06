package de.mukis.tvs.core;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import de.mukis.tvs.core.models.BuildProperties;
import de.mukis.tvs.core.models.BundleManifest;
import de.mukis.tvs.core.models.Feature;
import de.mukis.tvs.core.models.FeatureProject;
import de.mukis.tvs.core.models.IProject;
import de.mukis.tvs.core.models.POM;
import de.mukis.tvs.core.models.PluginProject;
import de.mukis.tvs.core.models.UpdateSiteProject;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.0.2
 * 
 */
public class MainApp {

	private static final String VERSION = "0.0.1";
	private static Path root = Paths.get(System.getProperty("user.dir"));
	private static String help;
	private static final String MATCH_ALL_BUNDLES = "[a-z0-9.]*"; // match
																	// everything

	static {
		StringBuilder sb = new StringBuilder();
		sb.append("[Usage] \n");
		sb.append(" exit \t Exits the program \n");
		sb.append(" quit \t Exits the program \n");
		sb.append(" list \t List all projects \n");
		sb.append(" version \t Shows programm version \n");
		help = sb.toString();
	}

	/**
	 * @param arguments
	 */
	public static void main(String[] arguments) {
		if (arguments.length == 1) {
			Path newRoot = Paths.get(arguments[0]);
			if (Files.exists(newRoot)) {
				root = newRoot;
			}
		}
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Using [" + root + "] as project. Reading projects...");
			List<IProject> projects = readProjects();
			while (true) {
				System.out.print(">");
				String cmd = scanner.next();
				if (cmd.equals("quit") || cmd.equals("exit")) {
					System.exit(0);
				} else if (cmd.equals("version")) {
					version();
				} else if (cmd.equals("help")) {
					help();
				} else if (cmd.equals("list")) {
					list(projects);
				} else if (cmd.equals("show")) {
					cmd = scanner.next();
					if (cmd.equals("pom"))
						showPom(projects);
					else if (cmd.equals("features"))
						showFeature(projects);
				} else if (cmd.equals("set")) {
					cmd = scanner.next();
					if (cmd.equals("qualifier")) {
						setQualifier(args(scanner), projects);
					} else if (cmd.equals("bundle-version")) {
						setBundleVersion(args(scanner), projects);
					} else if (cmd.equals("import-package")) {
						setImportedPackageVersion(args(scanner), projects);
					} else if (cmd.equals("require-bundle")) {
						setRequiredBundleVersion(args(scanner), projects);
					} else if (cmd.equals("feature")) {
						setFeatureVersion(args(scanner), projects);
					}
				} else if (cmd.equals("sync")) {
					cmd = scanner.next();
					if (cmd.equals("exported-packages")) {
						syncExportedPackageVersion(args(scanner), projects);
					} else if (cmd.equals("manifest")) {
						syncManifest(args(scanner), projects);
					} else if(cmd.equals("feature")) {
						syncFeatureVersion(args(scanner), projects);
					} else if(cmd.equals("all")) {
						String[] args = args(scanner);
						System.out.println("------------- MANIFEST.MF");
						syncManifest(args, projects);
						System.out.println("------------- exported-packages");
						syncExportedPackageVersion(args, projects);
						System.out.println("------------- feature.xml");
						syncFeatureVersion(args, projects);
					}
				} else {
					System.out.println("Unkown command " + cmd);
					help();
					System.out.print(">");
				}

			}

		} catch (Exception e) {
			System.err.println("[ERROR] " + e.getMessage());
			e.printStackTrace();
		}
	}

	/* =================================================== */
	/* ==================== Functions ==================== */
	/* =================================================== */

	private static void help() {
		System.out.println(help);
	}

	private static void version() {
		System.out.println("Tycho-Version-Sync " + VERSION);
	}

	/* ============================================= */
	/* ============== Show and List =============== */
	/* ============================================= */

	private static void list(List<IProject> projects) {
		for (IProject project : projects) {
			System.out.println(project.toString());
		}
	}

	private static void showPom(List<IProject> projects) throws Exception {
		for (IProject project : projects) {
			POM pom = project.get(POM.class);
			System.out.println(pom);
		}
	}

	private static void showFeature(List<IProject> projects) throws Exception {
		for (IProject project : projects) {
			Feature feature = project.get(Feature.class);
			if (feature != null) {
				feature.setVersion("0.0.1");
				feature.write(System.out);
			}
		}
	}

	/* ============================================= */
	/* ============== Set functions =============== */
	/* ============================================= */

	private static void setQualifier(String[] args, List<IProject> projects) throws Exception {
		if (args.length == 0) {
			System.out.println("No qualifier given");
			return;
		}

		String qualifier = args[0];
		System.out.println("Seting qualifier to [" + qualifier + "] ...");
		for (IProject project : projects) {
			BuildProperties properties = project.get(BuildProperties.class);
			if (properties != null && properties.hasQualifier()) {
				System.out.print("[" + project.getName() + "] Change qualifier " + properties.getQualifier() + " to " + qualifier);
				properties.setQualifier(qualifier);
				project.update(properties);
				System.out.println(" \u2713");
			}
		}
	}

	/**
	 * 
	 * @param args
	 * @param projects
	 * @throws Exception 
	 */
	private static void setBundleVersion(String[] args, List<IProject> projects) throws Exception {
		if (args.length == 0) {
			System.out.println("No version given");
			return;
		}
		
		String version = version(args);
		String regex = regex(args);

		System.out.println("Setting bundle-version...");
		for (IProject project : projects) {
			BundleManifest manifest = project.get(BundleManifest.class);
			if (manifest == null || !manifest.getBundleSymbolicName().matches(regex))
				continue;
			manifest.setBundleVersion(version);
			project.update(manifest);
			System.out.println("[" + project.getName() + "] from " + manifest.getBundleVersion() + " to "
					+ version + " \u2713");
		}
	}
	
	public static void setImportedPackageVersion(String[] args, List<IProject> projects) throws Exception {
		if (args.length < 2) {
			System.out.println("Need version and package");
			return;
		}
		
		String version = version(args);
		String regex = regex(args);
		
		System.out.println("Setting import-package version...");
		for (IProject project : projects) {
			BundleManifest manifest = project.get(BundleManifest.class);
			if(manifest == null)
				continue;
			
			int count = 0;
			Map<String, String> packages = manifest.getImportedPackages();
			for(String pkg : packages.keySet()) {
				if (!pkg.matches(regex))
					continue;
				manifest.setImportedPackageVersion(pkg, version);
				count++;
			}
			project.update(manifest);
			System.out.println("[" + project.getName() + "] updated " + count + " packages \u2713");
		}
	}
	
	public static void setRequiredBundleVersion(String[] args, List<IProject> projects) throws Exception {
		if (args.length < 2) {
			System.out.println("Need version and bundle");
			return;
		}
		
		String version = version(args);
		String regex = regex(args);
		
		System.out.println("Setting required-bundle version...");
		for (IProject project : projects) {
			BundleManifest manifest = project.get(BundleManifest.class);
			if(manifest == null)
				continue;
			
			int count = 0;
			Map<String, String> bundles = manifest.getRequiredBundles();
			for(String pkg : bundles.keySet()) {
				if (!pkg.matches(regex))
					continue;
				manifest.setRequiredBundleVersion(pkg, version);
				count++;
			}
			project.update(manifest);
			System.out.println("[" + project.getName() + "] updated " + count + " bundles \u2713");
		}
	}
	

	private static void setFeatureVersion(String[] args, List<IProject> projects) throws Exception {
		if (args.length == 0) {
			System.out.println("Need version");
			return;
		}
		
		String version = version(args);
		String regex = regex(args);
		
		System.out.println("Setting feature version...");
		for (IProject project : projects) {
			Feature feature = project.get(Feature.class);
			if(feature == null || !feature.getId().matches(regex))
				continue;
			
			String oldversion = feature.getVersion();
			feature.setVersion(version);
			project.update(feature);
			System.out.println("[" + project.getName() + "] updated " + feature.getId() + " from " + oldversion + " to " + version + " \u2713");
		}
	}
	
	
	/* ============================================= */
	/* ============== Sync functions =============== */
	/* ============================================= */

	/**
	 * 
	 * @param args
	 * @param projects
	 * @throws Exception 
	 */
	private static void syncExportedPackageVersion(String[] args, List<IProject> projects) throws Exception {
		String regex = MATCH_ALL_BUNDLES;
		if (args.length != 0) {
			regex = args[0];
			for (int i = 1; i < args.length; i++) {
				regex += "|" + args[i];
			}
		}

		System.out.println("Syncing exported packages with bundle-version...");
		for (IProject project : projects) {
			BundleManifest manifest = project.get(BundleManifest.class);
			if (manifest == null || !manifest.getBundleSymbolicName().matches(regex))
				continue;
			String version = manifest.getBundleVersion().replaceAll(".qualifier", "");
			for (String pkg : manifest.getExportedPackages().keySet()) {
				manifest.setExportedPackageVersion(pkg, version);
			}
			project.update(manifest);
			System.out.println("[" + project.getName() + "][" + manifest.getBundleSymbolicName() + "] synced to " + version + " \u2713");
		}
	}

	private static void syncManifest(String[] args, List<IProject> projects) throws Exception {

		System.out.println("Syncing MANIFEST.MF bundle-version with pom.xml version ...");
		for (IProject project : projects) {
			POM pom = project.get(POM.class);
			BundleManifest manifest = project.get(BundleManifest.class);
			if (pom == null || manifest == null) 
				continue;
			String version = qualifierVersion(pom);
			System.out.print("Syncing " + manifest.getBundleSymbolicName() + " version=" + manifest.getBundleVersion());
			
			manifest.setBundleVersion(version);
			project.update(manifest);
			System.out.println(" to version=" + version + " \u2713");
		}
	}
	
	private static void syncFeatureVersion(String[] args, List<IProject> projects) throws Exception {
		System.out.println("Syncing feature.xml version with pom.xml version ...");
		for (IProject project : projects) {
			POM pom = project.get(POM.class);
			Feature feature = project.get(Feature.class);
			if (pom == null || feature == null)
				continue;
			
			String version = qualifierVersion(pom);
			System.out.print("Syncing " + feature.getId() + " version=" + feature.getVersion());
			
			feature.setVersion(version);
			project.update(feature);
			System.out.println(" to version=" + version + " \u2713");
		}
	}

	/* =================================================== */
	/* ================ Utility Functions ================ */
	/* =================================================== */

	private static List<IProject> readProjects() throws IOException {
		ArrayList<IProject> projects = new ArrayList<>();

		try (DirectoryStream<Path> projectPaths = Files.newDirectoryStream(root);) {
			for (Path path : projectPaths) {
				if ((Files.isDirectory(path) && !path.getFileName().toString().startsWith("."))) {
					if (ProjectBuilder.isPluginProject(path)) {
						System.out.println("[Plugin] " + path.getFileName());
						projects.add(new PluginProject(path));
					} else if (ProjectBuilder.isFeatureProject(path)) {
						System.out.println("[Feature] " + path.getFileName());
						projects.add(new FeatureProject(path));
					} else if (ProjectBuilder.isUpdateSiteProject(path)) {
						System.out.println("[Updatesite] " + path.getFileName());
						projects.add(new UpdateSiteProject(path));
					}
				}
			}
		}
		return projects;
	}
	
	private static String regex(String[] args) {
		String[] bundles = Arrays.copyOf(args, args.length -1);
		if(bundles.length == 0)
			return MATCH_ALL_BUNDLES;
		String returns = bundles[0];
		for (int i = 1; i < bundles.length; i++) {
			returns += "|" + bundles[i];
		}
		return returns;
	}
	
	private static String version(String[] args) {
		int versionIndex = args.length == 0 ? 0 : args.length -1;
		return args[versionIndex];
	}

	private static String[] args(Scanner scanner) {
		String line = scanner.nextLine().trim();
		if (line.isEmpty())
			return new String[0];
		return line.split(" ");
	}
	
	private static String qualifierVersion(POM pom) {
		return pom.getVersion().replaceFirst("-SNAPSHOT", ".qualifier");
	}

}
