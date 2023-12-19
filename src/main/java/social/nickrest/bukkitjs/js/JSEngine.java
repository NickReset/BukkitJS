package social.nickrest.bukkitjs.js;

import lombok.Getter;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import social.nickrest.bukkitjs.BukkitJS;

import java.io.File;
import java.io.IOException;

@Getter
public class JSEngine {

    private Engine parentEngine;

    private Context context;
    private Value bindings;

    private boolean running;

    public JSEngine() {
        this.start();
    }

    public void put(String key, Object value) {
        this.getContext().getBindings("js").putMember(key, value);
    }

    public void put(String key, Class<?> clazz) {
        this.eval(String.format("var %s = Java.type('%s');", key, clazz.getName()));
    }

    public void eval(String script) {
        this.context.eval("js", script);
    }

    public void eval(File file) {
        try {
            this.context.eval(Source.newBuilder("js", file).build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        this.parentEngine = Engine.newBuilder().option("engine.WarnInterpreterOnly", "false").build();

        String[] args = {
                "js.nashorn-compat", "true",
                "js.commonjs-require", "true",
                "js.ecmascript-version", "2022",
                "js.commonjs-require-cwd", new File(BukkitJS.get().getDataFolder(), "./scripts").getAbsolutePath()
        };

        Context.Builder builder = Context.newBuilder("js")
                .engine(this.parentEngine)
                .allowAllAccess(true)
                .allowExperimentalOptions(true);

        for(int i = 0; i < args.length; i += 2) {
            builder.option(args[i], args[i + 1]);
        }

        this.context = builder.build();
        this.bindings = this.context.getBindings("js");
        this.running = true;
    }

    public void terminate() {
        this.context.close();
        this.running = false;
    }

}

