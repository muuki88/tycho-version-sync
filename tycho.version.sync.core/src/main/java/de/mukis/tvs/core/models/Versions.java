package de.mukis.tvs.core.models;

public class Versions {

	public static String asBundleVersion(String version) {
		return version.replaceFirst("-SNAPSHOT", ".qualifier");
	}
	
	public static String asPomVersion(String version) {
		return version.replace("-SNAPSHOT", ".qualifier");
	}
}
