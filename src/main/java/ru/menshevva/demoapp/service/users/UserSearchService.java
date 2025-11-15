package ru.menshevva.demoapp.service.users;

import com.vaadin.flow.data.provider.Query;
import ru.menshevva.demoapp.dto.UserListData;

import java.util.stream.Stream;

public interface UserSearchService {
    int getCount(Query<UserListData, UserSearchFilter> query);

    Stream<UserListData> fetch(Query<UserListData, UserSearchFilter> query);
}
