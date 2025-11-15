package ru.menshevva.demoapp.ui.errors;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.HttpStatusCode;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import ru.menshevva.demoapp.ui.MainMenuLayout;

import static ru.menshevva.demoapp.ui.FrontendConsts.TITLE_ACCESS_DENIED;

@AnonymousAllowed
@Tag("access-denied-view")
@JsModule("./src/errors/access-denied-view.ts")
@ParentLayout(MainMenuLayout.class)
@PageTitle(TITLE_ACCESS_DENIED)
public class AccessDeniedView extends LitTemplate implements HasErrorParameter<AccessDeniedException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent beforeEnterEvent, ErrorParameter<AccessDeniedException> errorParameter) {
        return HttpStatusCode.NOT_FOUND.getCode();
    }
}
