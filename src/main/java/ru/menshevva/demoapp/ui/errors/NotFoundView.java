package ru.menshevva.demoapp.ui.errors;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.HttpStatusCode;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import ru.menshevva.demoapp.ui.MainMenuLayout;

import static ru.menshevva.demoapp.ui.FrontendConsts.TITLE_PAGE_NOT_FOUND;

@AnonymousAllowed
@Tag("not-found-view")
@JsModule("./src/errors/not-found-view.ts")
@ParentLayout(MainMenuLayout.class)
@PageTitle(TITLE_PAGE_NOT_FOUND)
public class NotFoundView extends LitTemplate implements HasErrorParameter<NotFoundException>  {
    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        return HttpStatusCode.NOT_FOUND.getCode();
    }
}
