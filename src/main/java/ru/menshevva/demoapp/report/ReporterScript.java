package ru.menshevva.demoapp.report;

import groovy.lang.Script;
import groovy.sql.Sql;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.Map;

@Slf4j
@Getter
@Setter
public abstract class ReporterScript extends Script {

    private Map<String, ?> params;
    private Sql sql;

    public void initSql(Connection connection) {
        this.sql = new Sql(connection);
    }

    public void closeSql() {
        if (sql != null) {
            sql.close();
            sql = null;
        }
    }

    public void debug(String message) {
        log.debug(message);
    }

    public void error(String message) {
        log.error(message);
    }
    public void trace(String message) {
        log.trace(message);
    }

    public void info(String message) {
        log.info(message);
    }

    public void warn(String message) {
        log.warn(message);
    }

    public abstract Path execute(Map<String, ?> params);
}
