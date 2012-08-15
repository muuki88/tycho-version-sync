package de.mukis.tvs.core.models;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.0.2
 *
 */
public interface IWriteable {

	public void write(OutputStream out) throws IOException;
}
