package de.mukis.tvs.core.models;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.0.2
 * 
 */
public class BuildProperties implements IWriteable {

	private String rawContent;

	private String qualifier;
	private boolean hasQualifier;

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		if (hasQualifier)
			this.qualifier = qualifier;
	}

	public boolean hasQualifier() {
		return hasQualifier;
	}

	@Override
	public void write(OutputStream out) throws IOException {
		PrintWriter w = new PrintWriter(out);
		String[] lines = rawContent.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			String[] keyValue = line.replace(" ", "").split("=");
			if (keyValue[0].equals("qualifier") && !qualifier.equals(keyValue[1]))
				w.println("qualifier = " + qualifier);
			else
				w.println(line);
		}
		w.flush();
	}

	public static BuildProperties parse(Path path) throws IOException {
		if (!Files.exists(path))
			return null;
		BuildProperties properties = new BuildProperties();
		try (LineNumberReader r = new LineNumberReader(Files.newBufferedReader(path, Charset.defaultCharset()))) {
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = r.readLine()) != null) {
				String[] keyValue = line.replace(" ", "").split("=");
				if (keyValue[0].equals("qualifier")) {
					if (keyValue.length == 1)
						continue;
					properties.qualifier = keyValue[1];
					properties.hasQualifier = true;
				}
				sb.append(line).append("\n");
			}
			properties.rawContent = sb.toString();
		}
		return properties;
	}

	@Override
	public String toString() {
		return "BuildProperties [qualifier=" + qualifier + "]";
	}

}
