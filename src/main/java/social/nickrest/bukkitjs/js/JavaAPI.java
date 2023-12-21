package social.nickrest.bukkitjs.js;

import com.caoccao.javet.annotations.V8Function;

@SuppressWarnings("unused")
public class JavaAPI {

    @V8Function
    public Class<?> type(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

}
