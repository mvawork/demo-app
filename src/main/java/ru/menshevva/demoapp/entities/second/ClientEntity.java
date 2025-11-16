package ru.menshevva.demoapp.entities.second;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dat_client", schema = "clients")
@Getter
@Setter
public class ClientEntity {

    @Id
    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "client_name")
    private String clientName;
}
