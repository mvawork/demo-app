package ru.menshevva.demoapp.service.roles;

import com.vaadin.flow.data.provider.Query;
import ru.menshevva.demoapp.dto.role.RoleListData;
import ru.menshevva.demoapp.service.common.ListViewAbstractSearchService;

import java.util.stream.Stream;

public interface RoleSearchService extends ListViewAbstractSearchService<RoleListData, RoleSearchFilter> {

    Stream<RoleListData> fetch(Query<RoleListData, RoleSearchFilter> query);

    int getCount(Query<RoleListData, RoleSearchFilter> query);
}
