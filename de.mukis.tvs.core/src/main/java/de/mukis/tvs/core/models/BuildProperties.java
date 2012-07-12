package de.mukis.tvs.core.models;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.0.1
 * 
 */
public class BuildProperties {

	private String qualifier;

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public static BuildProperties parse(Path path) throws IOException {
		if (!Files.exists(path))
			return null;
		BuildProperties properties = new BuildProperties();
		try (LineNumberReader r = new LineNumberReader(Files.newBufferedReader(path, Charset.defaultCharset()))) {
			String line = null;
			while ((line = r.readLine()) != null) {
				String[] keyValue = line.replace(" ", "").split("=");
				if (keyValue[0].equals("qualifier"))
					properties.qualifier = keyValue[1];
			}
		}
		return properties;
	}

	public static void write(BuildProperties properties, Path path) throws IOException {
		Path tmp = path.getParent().resolve("~" + path.getFileName());
		Files.createFile(tmp);
		try (LineNumberReader r = new LineNumberReader(Files.newBufferedReader(path, Charset.defaultCharset()));
				PrintWriter w = new PrintWriter(Files.newBufferedWriter(tmp, Charset.defaultCharset()))) {
			
			//Copy everything to temporary file and reflect changes made to qualifier
			String line = null;
			while ((line = r.readLine()) != null) {
				String[] keyValue = line.replace(" ", "").split("=");
				if (keyValue[0].equals("qualifier") && !properties.qualifier.equals(keyValue[1]))
					w.println("qualifier = " + properties.qualifier);
				else
					w.println(line);
			}
			w.flush();
			//Replaced original with temporary file
			Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING);
		}

	}
	
	@Override
	public String toString() {
		return "BuildProperties [qualifier=" + qualifier + "]";
	}
}
