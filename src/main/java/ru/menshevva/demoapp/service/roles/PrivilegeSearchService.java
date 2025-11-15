package ru.menshevva.demoapp.service.roles;

import com.vaadin.flow.data.provider.Query;
import ru.menshevva.demoapp.dto.privilege.PrivilegeListData;

import java.util.stream.Stream;

/**
 * Сервис для поиска привилегий ролей.
 */
public interface PrivilegeSearchService {

    Stream<PrivilegeListData> fetch(Query<PrivilegeListData, PrivilegeSearchFilter> query);

    int getCount(Query<PrivilegeListData, PrivilegeSearchFilter> query);

}
