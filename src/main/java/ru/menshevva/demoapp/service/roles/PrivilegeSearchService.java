package ru.menshevva.demoapp.service.roles;

import com.vaadin.flow.data.provider.Query;
import ru.menshevva.demoapp.dto.privilege.PrivilegeListData;
import ru.menshevva.demoapp.service.common.ListViewAbstractSearchService;

import java.util.stream.Stream;

/**
 * Сервис для поиска привилегий ролей.
 */
public interface PrivilegeSearchService extends ListViewAbstractSearchService<PrivilegeListData, PrivilegeSearchFilter> {

    Stream<PrivilegeListData> fetch(Query<PrivilegeListData, PrivilegeSearchFilter> query);

    int getCount(Query<PrivilegeListData, PrivilegeSearchFilter> query);

}
