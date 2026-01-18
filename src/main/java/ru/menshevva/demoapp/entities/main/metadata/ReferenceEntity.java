package ru.menshevva.demoapp.entities.main.metadata;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "DIC_REFERENCE", schema = "METADATA")
@Getter
@Setter
@SequenceGenerator(name = "SQ_DIC_REFERENCE", schema = "METADATA", sequenceName = "SQ_DIC_REFERENCE", allocationSize = 1)
public class ReferenceEntity {

    @Id
    @Column(name = "REFERENCE_ID")
    @GeneratedValue(generator = "SQ_DIC_REFERENCE", strategy = GenerationType.SEQUENCE)
    private Long referenceId;

    @Column(name = "REFERENCE_NAME")
    private String referenceName;

    @Column(name = "SCHEMA_NAME")
    private String schemaName;

    @Column(name = "TABLE_NAME")
    private String tableName;

    @Column(name = "TABLE_SQL")
    private String tableSql;

    @Column(name = "JVM_SCRIPT")
    private String jvmScript;

    @OneToMany
    @JoinColumn(name = "reference_id", referencedColumnName = "reference_id", insertable = false, updatable = false)
    private List<ReferenceFieldEntity> referenceFieldEntities;


}
