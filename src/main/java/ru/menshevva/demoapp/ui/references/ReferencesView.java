package ru.menshevva.demoapp.ui.references;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ru.menshevva.demoapp.ui.FrontendConsts;
import ru.menshevva.demoapp.ui.MainMenuLayout;

import static ru.menshevva.demoapp.ui.FrontendConsts.TITLE_PAGE_REFERENCES;

@PageTitle(TITLE_PAGE_REFERENCES)
@Route(value = FrontendConsts.PAGE_REFERENCES, layout = MainMenuLayout.class)
public class ReferencesView extends HorizontalLayout {

}
