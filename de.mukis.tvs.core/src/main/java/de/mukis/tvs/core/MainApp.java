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
import java.util.Scanner;
import java.util.jar.Manifest;

import org.osgi.framework.BundleException;

import de.mukis.tvs.core.models.BuildProperties;
import de.mukis.tvs.core.models.BundleManifest;
import de.mukis.tvs.core.models.Project;

public class MainApp {

	private static final String VERSION = "0.0.1";
	private static Path root = Paths.get(System.getProperty("user.dir"));
	private static String help;

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
				} else if (cmd.equals("qualifier")) {
					qualifier(scanner, projects);
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

	private static void qualifier(Scanner scanner, List<Project> projects) throws IOException, BundleException {
		String[] args = scanner.nextLine().trim().split(" ");
		System.out.println(Arrays.toString(args));
		if(args.length == 0) {
			System.out.println("No qualifier given");
			return;
		}
			
		String version = args[0];
		for (Project project : projects) {
			BuildProperties properties = BuildProperties.parse(project.getBuildPropertiesPath());
			if (properties != null) {
				properties.setQualifier(version);
				BuildProperties.write(properties, project.getBuildPropertiesPath());
			}

			
			//Manifest mf = BundleManifest.parse(project.getManifestPath());
			//if (mf != null) {
			//	OutputStream out = Files.newOutputStream(project.getRoot().resolve("MANIFEST.TEST"));
			//	mf.write(out);
			//}
		}

	}

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

	/*
	 * private static String[] args(Scanner scanner) { String first =
	 * scanner.next(); String[] argsLeft = scanner.nextLine().trim().split(" ");
	 * String[] args = new String[argsLeft.length + 1]; args[0] = first;
	 * System.arraycopy(argsLeft, 0, args, 1, argsLeft.length); return args; }
	 */

	// / BUILD PROPERTIES BuildProperties properties =

}
