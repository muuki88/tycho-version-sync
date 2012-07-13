package de.mukis.tvs.core;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
	private static final CommandLineParser parser = new PosixParser();
	private static final Options options = new Options();
	private static Path root;

	static {
		options.addOption("h", "help", false, "Show available commmands");
		options.addOption("v", "version", false, "version number");
		options.addOption("q", "quit", false, "Quit console");

		options.addOption("l", "list", false, "List all projects");
		
		Option project = OptionBuilder.withArgName("project").hasArg().withDescription("Defines the root project directory").create("p");

		options.addOption(project);
		
		root = Paths.get(System.getProperty("user.dir"));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try(Scanner scanner = new Scanner(System.in)) {
			CommandLine cmdLine = parser.parse(options, args);
			execute(cmdLine, scanner);

		} catch (ParseException | IOException | BundleException e) {
			System.err.println("[ERROR] " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	private static void execute(CommandLine cmdLine, Scanner scanner) throws IOException, BundleException, ParseException {
		if (cmdLine.hasOption('h'))
			printHelp(options);
		if (cmdLine.hasOption('v'))
			printVersion();
		if (cmdLine.hasOption('v') || cmdLine.hasOption('h') || cmdLine.hasOption('q'))
			System.exit(0);
		if(cmdLine.getArgList().isEmpty())
			interactive(null, scanner);
		else
			interactive(cmdLine, scanner);
	}

	private static void printHelp(Options options) {
		HelpFormatter hf = new HelpFormatter();
		hf.printHelp("tycho-version-sync", options);
	}

	private static void printVersion() {
		System.out.println("Tycho-Version-Sync " + VERSION);
	}

	private static void interactive(CommandLine cmd, Scanner scanner) throws IOException, BundleException, ParseException {
		System.out.println("Using [" + root + "] as project");
		System.out.print(">");
		CommandLine cmdLine = cmd;
		if(cmdLine == null) {
			String first = scanner.next();
			String[] argsLeft = scanner.nextLine().trim().split(" ");
			String[] args = new String[argsLeft.length + 1];
			args[0] = first;
			System.arraycopy(argsLeft, 0, args, 1, argsLeft.length);
			
			cmdLine = parser.parse(options, args);
		}
		execute(cmdLine, scanner);
	}


	/*
	 * 
	 * List<Project> projects = new LinkedList<>(); try (DirectoryStream<Path>
	 * stream = Files.newDirectoryStream(root)) { for (Path path : stream) {
	 * if(Files.isDirectory(path) &&
	 * !path.getFileName().toString().startsWith(".")) projects.add(new
	 * Project(path)); } } for (Project project : projects) {
	 * 
	 * /// BUILD PROPERTIES BuildProperties properties =
	 * BuildProperties.parse(project.getBuildPropertiesPath()); if(properties !=
	 * null) { properties.setQualifier("myNewQualifier");
	 * BuildProperties.write(properties, project.getBuildPropertiesPath()); }
	 * 
	 * 
	 * Manifest mf = BundleManifest.parse(project.getManifestPath()); if(mf !=
	 * null) { OutputStream out =
	 * Files.newOutputStream(project.getRoot().resolve("MANIFEST.TEST"));
	 * mf.write(out); } }
	 */
}
