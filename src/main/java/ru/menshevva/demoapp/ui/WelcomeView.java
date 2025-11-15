package ru.menshevva.demoapp.ui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import ru.menshevva.demoapp.ui.components.RgbaColorPicker;

@PermitAll
@SpringComponent
@UIScope
//@Tag("welcome-view")
//@JsModule("./src/welcome-view.ts")
@Route(value = FrontendConsts.PAGE_WELCOME, layout = MainMenuLayout.class)
@Slf4j
public class WelcomeView extends Div {
    public WelcomeView() {
        Anchor navigateToHilla = new Anchor("counter", "Navigate to a Hilla view");
        RgbaColorPicker rgbaColorPicker = new RgbaColorPicker();
        rgbaColorPicker.addColorChangeListener(rgbaColor -> log.debug("RgbaColor: {}", rgbaColor));
        add(navigateToHilla, rgbaColorPicker);
    }

}
