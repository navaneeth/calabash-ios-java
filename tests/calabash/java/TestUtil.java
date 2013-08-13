package calabash.java;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static calabash.java.Utils.runCommand;

public class TestUtil {

    public static IOSApplication initializeAndStart(String appName, CalabashConfiguration calabashConfiguration) throws CalabashException, IOException {
        String path = extractApp(appName);
        calabashConfiguration.setAppBundlePath(new File(path, "/Calabash/build/Calabash.app/").getAbsolutePath());
        CalabashRunner calabashRunner = new CalabashRunner(path, calabashConfiguration);
        calabashRunner.setup();
        build(path, appName);
        return calabashRunner.start();
    }

    public static void build(String path, String appName) throws CalabashException {
        String xcodeProject = String.format("%s/%s.xcodeproj", path, appName);
        File config = new File(path, "cal.xcconfig");
        String[] buildCommand = {
                "xcodebuild",
                "-project", xcodeProject,
                "-xcconfig", config.getAbsolutePath(),
                "install",
                "-configuration", "Debug",
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
        File conf = new File("tests/resources", "cal.xcconfig");
        FileUtils.copyFileToDirectory(conf, new File(tempDir, appName));
        return String.format("%s/%s", tempDir.getAbsolutePath(), appName);
    }

    private static File createTempDir(String directoryName) throws IOException {
        File tempFile = File.createTempFile("foo", "bar");
        tempFile.delete();
        File tempDir = new File(tempFile.getParentFile(), directoryName);
        tempDir.mkdir();
        return tempDir;
    }

    public static void clearAppDir() throws IOException {
        File tempDir = createTempDir("TestIOSApps");
        FileUtils.deleteDirectory(tempDir);
    }
}
