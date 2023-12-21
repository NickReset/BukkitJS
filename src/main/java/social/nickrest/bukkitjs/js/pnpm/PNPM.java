package social.nickrest.bukkitjs.js.pnpm;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import static social.nickrest.bukkitjs.js.pnpm.PNPMDownloader.*;

@Getter
public class PNPM {

    private final File pnpmPath, pnpmExecutable, workingDirectory;

    public PNPM(File workingDirectory, File installedDirectory) {
        this.pnpmPath = installedDirectory;
        this.workingDirectory = workingDirectory;

        assert pnpmPath != null;
        if(!pnpmPath.exists() && !pnpmPath.mkdirs()) {
            throw new RuntimeException("Could not create PNPM directory");
        }

        if(!isInstalled(installedDirectory)) {
            installPNPM(installedDirectory);
        }

        this.pnpmExecutable = getPNPM(pnpmPath);
    }

    protected Process createProcess(String... args) {
        String[] arguments = new String[args.length + 1];
        arguments[0] = pnpmExecutable.getAbsolutePath();

        System.arraycopy(args, 0, arguments, 1, args.length);

        ProcessBuilder builder = new ProcessBuilder(arguments);
        builder.directory(workingDirectory);

        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            return builder.start();
        } catch (IOException e) {
            throw new RuntimeException("Could not start PNPM process", e);
        }
    }

    public void install(String... packages) {
        for(String pkg : packages) {
            Process process = createProcess("install", pkg);

            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void uninstall(String... packages) {
        for(String pkg : packages) {
            Process process = createProcess("uninstall", pkg);

            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void install(Consumer<Void> consumer, String... packages) {
        this.install(packages);
        consumer.accept(null);
    }

    public void uninstall(Consumer<Void> consumer, String... packages) {
        this.uninstall(packages);
        consumer.accept(null);
    }

}
