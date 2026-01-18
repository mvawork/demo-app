package ru.menshevva.demoapp.service.script;

import groovy.lang.Binding;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ReferenceDataProcessorService extends AbstractScriptProcessorService<DataProcessorScript> {



    public ReferenceDataProcessorService() {
        super(DataProcessorScript.class);
    }

    public Long nextVal(String sequencyName) {
    }

    @Override
    protected @NonNull Binding createBinding() {
        Binding binding = new Binding();
        return binding;
    }
}
