package ru.menshevva.demoapp.service.roles;

import com.vaadin.flow.data.provider.Query;
import ru.menshevva.demoapp.dto.role.RoleListData;

import java.util.stream.Stream;

public interface RoleSearchService {

    Stream<RoleListData> fetch(Query<RoleListData, RoleSearchFilter> query);

    int getCount(Query<RoleListData, RoleSearchFilter> query);
}
