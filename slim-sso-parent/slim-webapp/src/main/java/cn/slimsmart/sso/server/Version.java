package cn.slimsmart.sso.server;

public final class Version {

	 private Version() {
	 }
	 
	 public static String getVersion() {
	        return Version.class.getPackage().getImplementationVersion();
	 }
}
