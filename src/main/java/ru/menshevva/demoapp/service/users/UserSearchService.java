package ru.menshevva.demoapp.service.users;

import com.vaadin.flow.data.provider.Query;
import ru.menshevva.demoapp.dto.UserListData;
import ru.menshevva.demoapp.service.common.ListViewAbstractSearchService;

import java.util.stream.Stream;

public interface UserSearchService extends ListViewAbstractSearchService<UserListData, UserSearchFilter> {
    int getCount(Query<UserListData, UserSearchFilter> query);

    Stream<UserListData> fetch(Query<UserListData, UserSearchFilter> query);
}
