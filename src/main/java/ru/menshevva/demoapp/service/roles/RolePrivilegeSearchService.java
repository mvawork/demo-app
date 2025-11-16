package ru.menshevva.demoapp.service.roles;

import com.vaadin.flow.data.provider.Query;
import ru.menshevva.demoapp.dto.roleprivilege.RolePrivilegeListData;
import ru.menshevva.demoapp.service.common.ListViewAbstractSearchService;

import java.util.stream.Stream;

/**
 * Сервис для поиска привилегий ролей.
 */
public interface RolePrivilegeSearchService extends ListViewAbstractSearchService<RolePrivilegeListData, RolePrivilegeSearchFilter> {

    Stream<RolePrivilegeListData> fetch(Query<RolePrivilegeListData, RolePrivilegeSearchFilter> query);

    int getCount(Query<RolePrivilegeListData, RolePrivilegeSearchFilter> query);

}
