package ru.menshevva.demoapp.service.script;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class MemoryURLConnection extends URLConnection {

    private final AppMemoryScript script;

    public MemoryURLConnection(URL url, AppMemoryScript script) {
        super(url);
        this.script = script;
    }

    @Override
    public void connect() {
        throw new UnsupportedOperationException("MemoryURLConnection не поддерживает открытие соединения");
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(script.content().getBytes(StandardCharsets.UTF_8));
    }

}