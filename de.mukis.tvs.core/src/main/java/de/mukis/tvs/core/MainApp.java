package de.mukis.tvs.core;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.jar.Manifest;

import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

import de.mukis.tvs.core.models.BuildProperties;
import de.mukis.tvs.core.models.BundleManifest;
import de.mukis.tvs.core.models.Project;

public class MainApp {

	private static final String VERSION = "0.0.1";
	private static Path root = Paths.get(System.getProperty("user.dir"));
	private static String help;
	private static final String MATCH_ALL_BUNDLES = "[a-z0-9.]*"; // match everything

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
			List<Project> projects = readProjects();
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
				} else if (cmd.equals("set")) {
					cmd = scanner.next();
					if (cmd.equals("qualifier")) {
						setQualifier(args(scanner), projects);
					} else if (cmd.equals("bundle-version")) {
						setBundleVersion(args(scanner), projects);
					}
				} else if(cmd.equals("sync")) {
					cmd = scanner.next();
					if(cmd.equals("exported-packages")) {
						syncExportedPackageVersion(args(scanner), projects);
					}
				} else {
					System.out.println(">Unkown command " + cmd);
					help();
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

	private static void list(List<Project> projects) {
		for (Project project : projects) {
			System.out.println(project.toString());
		}
	}

	private static void setQualifier(String[] args, List<Project> projects) throws IOException, BundleException {
		if (args.length == 0) {
			System.out.println("No qualifier given");
			return;
		}

		String qualifier = args[0];
		System.out.println("Seting qualifier to [" + qualifier + "] ...");
		for (Project project : projects) {
			BuildProperties properties = BuildProperties.parse(project.getBuildPropertiesPath());
			if (properties != null && properties.hasQualifier()) {
				System.out.print("[" + project.getName() + "] Change qualifier " + properties.getQualifier() + " to " + qualifier);
				properties.setQualifier(qualifier);
				BuildProperties.write(properties, project.getBuildPropertiesPath());
				System.out.println(" [successfull]");
			}
		}
	}

	/**
	 * 
	 * @param args
	 * @param projects
	 * @throws IOException
	 * @throws BundleException
	 */
	private static void setBundleVersion(String[] args, List<Project> projects) throws IOException, BundleException {
		if (args.length == 0) {
			System.out.println("No version given");
			return;
		}
		String version = args[0];

		String regex = MATCH_ALL_BUNDLES;
		if (args.length >= 2) {
			regex = args[1];
			for (int i = 2; i < args.length; i++) {
				regex += "|" + args[i];
			}
		}

		for (Project project : projects) {
			BundleManifest mf = BundleManifest.parse(project.getManifestPath());
			if (mf == null || !mf.getBundleSymbolicName().matches(regex))
				continue;
			System.out.println("[" + project.getName() + "][" + mf.getBundleSymbolicName() + "] from " + mf.getBundleVersion() + " to "
					+ version);
			mf.setBundleVersion(version);
			try (OutputStream out = Files.newOutputStream(project.getManifestPath())) {
				mf.write(out);
			}
		}
	}
	
	/**
	 * 
	 * @param args
	 * @param projects
	 * @throws BundleException 
	 * @throws IOException 
	 */
	private static void syncExportedPackageVersion(String[] args, List<Project> projects) throws IOException, BundleException {
		String regex = MATCH_ALL_BUNDLES;
		if (args.length != 0) {
			regex = args[0];
			for (int i = 1; i < args.length; i++) {
				regex += "|" + args[i];
			}
		}
		
		for (Project project : projects) {
			BundleManifest mf = BundleManifest.parse(project.getManifestPath());
			if (mf == null || !mf.getBundleSymbolicName().matches(regex))
				continue;
			String version = mf.getBundleVersion().replaceAll(".qualifier", "");
			for (String pkg : mf.getExportedPackages().keySet()) {
				mf.setExportedPackageVersion(pkg, version);
			}
			System.out.println("[" + project.getName() + "][" + mf.getBundleSymbolicName() + "] synced to " + version);
			try (OutputStream out = Files.newOutputStream(project.getManifestPath())) {
				mf.write(out);
			}
		}
	}
	
	/* =================================================== */
	/* ================ Utility Functions ================ */
	/* =================================================== */

	private static List<Project> readProjects() throws IOException {
		ArrayList<Project> projects = new ArrayList<>();

		try (DirectoryStream<Path> projectPaths = Files.newDirectoryStream(root);) {
			for (Path path : projectPaths) {
				if ((Files.isDirectory(path) && !path.getFileName().toString().startsWith("."))) {
					System.out.println("[Found] " + path.getFileName());
					projects.add(new Project(path));
				}
			}
		}
		return projects;
	}

	private static String[] args(Scanner scanner) {
		String line = scanner.nextLine().trim();
		if (line.isEmpty())
			return new String[0];
		return line.split(" ");
	}
	// / BUILD PROPERTIES BuildProperties properties =

}
