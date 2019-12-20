package com.tinygame.herostory.util;

public class SystemUtil {

    public static final String OS_NAME;
    public static final boolean IS_OS_WINDOWS;

    static {
        OS_NAME = getSystemProperty("os.name");
        IS_OS_WINDOWS = getOsMatchesName("Windows");
    }

    private static boolean getOsMatchesName(String osNamePrefix) {
        return isOSNameMatch(OS_NAME, osNamePrefix);
    }

    static boolean isOSNameMatch(String osName, String osNamePrefix) {
        return osName == null ? false : osName.startsWith(osNamePrefix);
    }

    private static String getSystemProperty(String property) {
        try {
            return System.getProperty(property);
        } catch (SecurityException var2) {
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(OS_NAME);
    }
}
