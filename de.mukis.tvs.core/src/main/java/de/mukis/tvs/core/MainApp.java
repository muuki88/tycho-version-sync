package de.mukis.tvs.core;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.jar.Manifest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.osgi.framework.BundleException;

import de.mukis.tvs.core.models.BundleManifest;
import de.mukis.tvs.core.models.Project;

public class MainApp {

	private static final String VERSION = "0.0.1";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Options options = createOptions();
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption('h'))
				printHelp(options);
			else if (cmd.hasOption('v'))
				printVersion();
			else
				run(cmd);

		} catch (ParseException | IOException | BundleException e) {
			System.err.println("[ERROR] " + e.getMessage());
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	private static Options createOptions() {
		Options options = new Options();

		options.addOption("h", "help", false, "Show available commmands");
		options.addOption("v", "version", false, "version number");

		Option project = OptionBuilder.withArgName("project").hasArg().withDescription("Defines the root project directory").create("p");

		options.addOption(project);

		return options;
	}

	private static void printHelp(Options options) {
		HelpFormatter hf = new HelpFormatter();
		hf.printHelp("tycho-version-sync", options);
	}

	private static void printVersion() {
		System.out.println("Tycho-Version-Sync " + VERSION);
	}

	// /home/muki/zztest
	private static void run(CommandLine cmd) throws IOException, BundleException {
		Scanner scanner = new Scanner(System.in);
		Path root = getProjectRoot(scanner);
		System.out.println("Using [" + root + "] as project");

		List<Project> projects = new LinkedList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
			for (Path path : stream) {
				if(Files.isDirectory(path) && !path.getFileName().toString().startsWith(".")) 
					projects.add(new Project(path));
			}
		}
		for (Project project : projects) {
			
			/* BUILD PROPERTIES
			BuildProperties properties = BuildProperties.parse(project.getBuildPropertiesPath());
			if(properties != null) {
				properties.setQualifier("myNewQualifier");
				BuildProperties.write(properties, project.getBuildPropertiesPath());
			}
			*/
			
			Manifest mf = BundleManifest.parse(project.getManifestPath());
			if(mf != null) {
				OutputStream out = Files.newOutputStream(project.getRoot().resolve("MANIFEST.TEST"));
				mf.write(out);
			}
		}
		
	}

	private static Path getProjectRoot(Scanner scanner) {
		System.out.print("Project root: ");
		String rootStr = scanner.next();
		Path root = Paths.get(rootStr);
		if (!Files.exists(root)) {
			System.out.println("Path does not exist!");
			return getProjectRoot(scanner);
		}

		return root;
	}
}
