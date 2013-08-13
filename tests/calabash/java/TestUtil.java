package calabash.java;

import java.io.File;
import java.io.IOException;

import static calabash.java.Utils.runCommand;

public class TestUtil {

    public static IOSApplication initializeAndStart(String appName, CalabashConfiguration calabashConfiguration) throws CalabashException, IOException {
        String path = extractApp(appName);
        calabashConfiguration.setAppBundlePath(path + "/Calabash/build/Calabash.app/");
        CalabashRunner calabashRunner = new CalabashRunner(path, calabashConfiguration);
        calabashRunner.setup();
        build(path, appName);
        return calabashRunner.start();
    }

    public static void build(String path, String appName) throws CalabashException {
        String xcodeProject = String.format("%s/%s.xcodeproj", path, appName);
        String[] buildCommand = {
                "xcodebuild",
                "-project", xcodeProject,
                "-xcconfig", "Calabash/cal.xcconfig",
                "install", "-configuration",
                "Debug",
                "-sdk", "iphonesimulator",
                "DSTROOT=Calabash/build",
                "WRAPPER_NAME=Calabash.app"
        };
        runCommand(buildCommand, "Failed to build");
    }

    public static String extractApp(String appName) throws IOException, CalabashException {
        File tempDir = createTempDir("TestIOSApps");
        File iosApp = new File("tests/resources", appName + ".zip");
        Utils.unzip(iosApp, tempDir);
        return String.format("%s/%s", tempDir.getAbsolutePath(), appName);
    }

    private static File createTempDir(String testIOSApps) throws IOException {
        File tempFile = File.createTempFile("foo", "bar");
        tempFile.delete();
        File tempDir = new File(tempFile.getParentFile(), testIOSApps);
        tempDir.mkdir();
        return tempDir;
    }
}
