package ru.menshevva.demoapp.service.clients;

import com.vaadin.flow.data.provider.Query;
import ru.menshevva.demoapp.dto.clients.ClientListData;
import ru.menshevva.demoapp.service.common.ListViewAbstractSearchService;

import java.util.stream.Stream;

public interface ClientSearchService extends ListViewAbstractSearchService<ClientListData, ClientSearchFilter> {

    Stream<ClientListData> fetch(Query<ClientListData, ClientSearchFilter> query);

    int getCount(Query<ClientListData, ClientSearchFilter> query);

}
