package ru.menshevva.demoapp.service.clients;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientSearchFilter {

    public static final String FILTER_CLIENT_ID = "client_id";
    public static final String FILTER_CLIENT_NAME = "client_name";

    private Long clientId;
    private String clientName;

}
