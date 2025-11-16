package ru.menshevva.demoapp.service.users;

import com.vaadin.flow.data.provider.Query;
import ru.menshevva.demoapp.dto.userrole.UserRoleListData;
import ru.menshevva.demoapp.service.common.ListViewAbstractSearchService;

import java.util.stream.Stream;

public interface UserRoleSearchService extends ListViewAbstractSearchService<UserRoleListData, UserRoleSearchFilter> {

    int getCount(Query<UserRoleListData, UserRoleSearchFilter> query);

    Stream<UserRoleListData> fetch(Query<UserRoleListData, UserRoleSearchFilter> query);

}
