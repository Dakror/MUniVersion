package de.dakror.muniversion;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.swing.JOptionPane;



public class MUniVersion {
	public final static String[] PHASES = { "Pre-Alpha", "Alpha", "Beta", "Release" };
	
	static String repo, name;
	static int version, phase;
	
	public static boolean offline;
	
	public static void init(String p, String v, String r, String n) {
		repo = r;
		name = n;
		phase = parsePhase(p);
		version = parseVersion(v);
		
		if (!offline && !isUpToDate() && JOptionPane.showConfirmDialog(null, "Es ist eine Aktualisierung verfügbar.\nMöchten Sie sie jetzt herunterladen?\nStarten Sie bitte nach der Aktualisierung die Anwendung erneut.", "Aktualisierung verfügbar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null) == 0) {
			try {
				Desktop.getDesktop().browse(new URL(("http://dakror.de/download?u=" + r + "/" + n + ".jar").replace(" ", "%20")).toURI());
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void init(String properties, String repo, String name) {
		Properties p = new Properties();
		try {
			p.load(MUniVersion.class.getResourceAsStream(properties));
		} catch (IOException e) {
			e.printStackTrace();
		}
		init(p.getProperty("phase"), p.getProperty("version"), repo, name);
	}
	
	private static boolean isUpToDate() {
		if (offline) return true;
		try {
			Properties p = new Properties();
			p.load(new URL(repo + "/.properties").openStream());
			int onlinePhase = parsePhase(p.getProperty("phase"));
			int onlineVersion = parseVersion(p.getProperty("version"));
			if (onlinePhase > phase) return true;
			return onlineVersion > version;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static int parsePhase(String phase) {
		if (phase.matches("^\\d+$")) return Integer.parseInt(phase);
		
		for (int i = 0; i < PHASES.length; i++) {
			if (phase.toUpperCase().equals(PHASES[i].toUpperCase())) return i;
		}
		
		return -1;
	}
	
	private static int parseVersion(String version) {
		if (version.contains("$")) // unbuilt
		return -1;
		if (version.contains("-")) // jenkins ignores maven.build.timestamp.format
		return Integer.parseInt(version.substring(2).replace("-", ""));
		return Integer.parseInt(version);
	}
}
