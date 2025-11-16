package ru.menshevva.demoapp.ui.clients;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import ru.menshevva.demoapp.dto.clients.ClientListData;
import ru.menshevva.demoapp.service.clients.ClientSearchFilter;
import ru.menshevva.demoapp.service.clients.ClientSearchService;
import ru.menshevva.demoapp.ui.FrontendConsts;
import ru.menshevva.demoapp.ui.components.AbstractListView;

import static ru.menshevva.demoapp.security.ApplicationRoles.ROLE_CLIENTS;

@PermitAll
@SpringComponent
@UIScope
@Route(value = FrontendConsts.PAGE_CLIENT_LIST, layout = ClientLayout.class)
@Slf4j
public class ClientListView extends AbstractListView<ClientListData, ClientSearchFilter> {

    public ClientListView(ClientSearchService clientSearchService) {
        super(clientSearchService, ROLE_CLIENTS);
    }


    @Override
    protected void initGrid(Grid<ClientListData> dataGrid) {
        dataGrid.addColumn(ClientListData::clientId)
                .setHeader("Идентификатор")
                .setKey(ClientSearchFilter.FILTER_CLIENT_ID);
        dataGrid.addColumn(ClientListData::clientName)
                .setHeader("Наименование")
                .setKey(ClientSearchFilter.FILTER_CLIENT_NAME);
        super.initGrid(dataGrid);
    }

}
