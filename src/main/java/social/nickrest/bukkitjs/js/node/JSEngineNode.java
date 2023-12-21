package social.nickrest.bukkitjs.js.node;

import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Locker;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.JavetEngineConfig;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.caoccao.javet.node.modules.NodeModuleModule;
import com.caoccao.javet.utils.JavetOSUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import social.nickrest.bukkitjs.BukkitJS;
import social.nickrest.bukkitjs.js.JavaAPI;
import social.nickrest.bukkitjs.util.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

@Getter
public class JSEngineNode {

    private Consumer<Exception> handleError = Throwable::printStackTrace;

    private JavetEnginePool<NodeRuntime> enginePool;
    private IJavetEngine<NodeRuntime> engine;

    private NodeRuntime runtime;

    private boolean running;

    public JSEngineNode() {
        this.start();
    }

    public void eval(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            this.runtime.getExecutor(builder.toString()).executeVoid();
        } catch (IOException | JavetException e) {
            handleError.accept(e);
        }
    }

    public void put(String key, Object value) {
        try {
            this.runtime.getGlobalObject().set(key, value);
        } catch (JavetException e) {
            handleError.accept(e);
        }
    }

    public void put(String key, Class<?> clazz) {
        this.eval(String.format("var %s = Java.type('%s');", key, clazz.getName()));
    }

    public void remove(String key) {
        try {
            this.runtime.getGlobalObject().delete(key);
        } catch (JavetException e) {
            handleError.accept(e);
        }
    }

    public Object eval(String js) {
        try {
            return this.engine.getV8Runtime().getExecutor(js).executeObject();
        } catch (JavetException e) {
            handleError.accept(e);
        }

        return null;
    }

    public void handleError(Consumer<Exception> handleError) {
        this.handleError = handleError;
    }

    public void start() {
        V8Host.setLibraryReloadable(true);

        this.enginePool = new JavetEnginePool<>(
                new JavetEngineConfig()
                        .setAllowEval(true)
                        .setGlobalName("globalThis")
                        .setJSRuntimeType(JSRuntimeType.Node)
        );

        try {
            this.engine = this.enginePool.getEngine();
            this.runtime = this.engine.getV8Runtime();
            this.runtime.getNodeModule(NodeModuleModule.class).setRequireRootDirectory(new File(JavetOSUtils.WORKING_DIRECTORY, BukkitJS.get().getDataFolder() + "\\scripts\\node_modules"));
            this.engine.getV8Runtime().setConverter(new JavetProxyConverter());
            this.running = true;
        } catch (JavetException e) {
            handleError.accept(e);
        }

        this.put("Java", new JavaAPI());
    }

    public Object runFunctionFromSrc(String src, String sub, Object... args) {
        Random random = new Random();
        List<String> longVariableNameThatNoOneWillEverUse = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            longVariableNameThatNoOneWillEverUse.add(sub + "_" + StringUtil.getSaltString(random.nextInt(10) + 10));

            this.put(longVariableNameThatNoOneWillEverUse.get(i), args[i]);
        }

        String functionSourceMinified = src.replace('\n', ' ').trim();
        String toExecute = String.format("(%s)(%s);", functionSourceMinified, String.join(", ", longVariableNameThatNoOneWillEverUse));

        Object output = this.eval(toExecute);

        for(String s : longVariableNameThatNoOneWillEverUse) {
            this.remove(s);
        }

        return output;
    }

    public void terminate() {
        try {
            this.enginePool.close();

            this.running = false;
        } catch (Exception e) {
            handleError.accept(e);
        }
    }

}
