package ru.menshevva.demoapp.ui.errors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.HttpStatusCode;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;
import ru.menshevva.demoapp.ui.MainMenuLayout;

import static ru.menshevva.demoapp.ui.FrontendConsts.TITLE_APP_ERROR;

@AnonymousAllowed
@Tag("server-error-view")
@JsModule("./src/errors/server-error-view.ts")
@ParentLayout(MainMenuLayout.class)
@PageTitle(TITLE_APP_ERROR)
@Slf4j
public class ServerErrorView extends Component implements HasErrorParameter<Exception> {
    private static final String INTERNAL_APP_ERROR = "Ошибка приложения";
    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<Exception> parameter) {
        if (event.isErrorEvent()) {
            log.error(INTERNAL_APP_ERROR, parameter.getException());
        }
        return HttpStatusCode.INTERNAL_SERVER_ERROR.getCode();
    }
}
