package social.nickrest.bukkitjs.js.pnpm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PNPMDownloader {

    public static final String baseURL = "https://github.com/pnpm/pnpm/releases/download", VERSION = "8.12.1";
    public static String os = "";

    public static File installPNPM(File dir) {
        try {
            if(os.isEmpty()) throw new RuntimeException("Could not determine OS");
            if(!dir.isDirectory()) throw new IOException("Invalid directory");

            URL url = new URL(String.format("%s/v%s/pnpm-%s", baseURL, VERSION, os));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            File output = new File(dir, String.format("pnpm-%s", os));

            InputStream inputStream = connection.getInputStream();
            Files.copy(inputStream, output.toPath(), StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();

            connection.disconnect();

            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Could not download PNPM");
    }

    public static File getPNPM(File dir) {
        System.out.println("[DEBUG] PNPMDownloader.getPNPM(File dir) called with dir: " + dir.getAbsolutePath() + " and os: " + os);

        try {
            if (!isInstalled(dir)) {
                throw new IOException("PNPM is not installed");
            }

            return new File(String.format("%s/pnpm-%s", dir.getAbsolutePath(), os));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean isInstalled(File dir) {
        return new File(dir, String.format("pnpm-%s", os)).exists();
    }

    static {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) PNPMDownloader.os = "win-x64.exe";
        else if(os.contains("mac")) PNPMDownloader.os = "macos-x64";
        else if(os.contains("linux")) PNPMDownloader.os = "linux-x64";
    }

}
