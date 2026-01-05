package ru.menshevva.demoapp.entities.main.metadata;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "DIC_REFERENCE_FIELD", schema = "METADATA")
@SequenceGenerator(name = "SQ_DIC_REFERENCE_FIELD", schema = "METADATA", sequenceName = "SQ_DIC_REFERENCE", allocationSize = 1)
@Getter
@Setter
public class ReferenceFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_DIC_REFERENCE_FIELD")
    @Column(name = "FIELD_ID")
    private Long fieldId;

    @Column(name = "REFERENCE_ID")
    private Long referenceId;

    @Column(name = "FIELD_NAME")
    private String fieldName;

    @Column(name = "FIELD_TITLE")
    private String fieldTitle;

    @Column(name = "FIELD_TYPE")
    private String fieldType;

    @Column(name = "FIELD_LENGTH")
    private Integer fieldLength;

    @Column(name = "FIELD_ORDER")
    private Integer fieldOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REFERENCE_ID", referencedColumnName = "REFERENCE_ID", insertable = false, updatable = false)
    private ReferenceEntity referenceEntity;

}
