package ru.menshevva.demoapp.service.script;

import groovy.lang.Binding;
import groovy.lang.Script;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceConnector;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.jetbrains.annotations.NotNull;
import ru.menshevva.demoapp.exception.EAppException;

import java.net.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractScriptProcessorService<T extends Script> implements ResourceConnector {

    private final GroovyScriptEngine scriptEngine;
    private final Class<T> clazz;
    private final Map<String, AppMemoryScript> scripts = new ConcurrentHashMap<>();
    private final URLStreamHandler urlStreamHandler;

    protected AbstractScriptProcessorService(Class<T> clazz) {
        this.clazz = clazz;
        this.scriptEngine = new GroovyScriptEngine(this, this.getClass().getClassLoader());
        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.setScriptBaseClass(clazz.getName());
        scriptEngine.setConfig(configuration);

        urlStreamHandler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u) {
                throw new UnsupportedOperationException("Метод не поддерживается.");
            }
        };

    }

    @Override
    public URLConnection getResourceConnection(String name) throws ResourceException {
        var script = scripts.get(name);
        if (script == null) {
            throw new ResourceException("Скрипт не найден: " + name);
        }
        URL url;
        try {
            url = URL.of(URI.create("memory://".concat(name)), urlStreamHandler);
        } catch (MalformedURLException e) {
            throw new ResourceException("Ошибка создания URL скрипта: " + name, e);
        }
        return new MemoryURLConnection(url, script);
    }

    @NotNull
    protected abstract Binding createBinding();

    public T createScriptFromString(String content) {
        String scriptName = UUID.randomUUID().toString();
        scripts.put(scriptName, new AppMemoryScript(LocalDateTime.now(), content));
        try {
            Script script;
            try {
                script = scriptEngine.createScript(scriptName, createBinding());
            } catch (ScriptException | ResourceException e) {
                var errMsg = "Ошибка создания скрипта: " + scriptName;
                log.error(errMsg, e);
                throw new EAppException(errMsg);
            }
            if (script == null) {
                throw new EAppException("Ошибка создания скрипта: " + scriptName + ". Результат is null");
            }
            if (clazz.isInstance(script)) {
                @SuppressWarnings("unchecked")
                T castedScript = (T) script;
                return castedScript;
            } else {
                throw new EAppException("Скрипт " + scriptName + " не соответствует ожидаемому типу " + clazz.getSimpleName());
            }
        } finally {
            scripts.remove(scriptName);
        }
    }
}
