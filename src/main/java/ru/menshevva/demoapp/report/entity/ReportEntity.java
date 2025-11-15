package ru.menshevva.demoapp.report.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dat_report",schema = "reports")
@SequenceGenerator(name = "sq_dat_report", sequenceName = "sq_dat_report", schema = "reports",  allocationSize = 1)
@Getter
@Setter
public class ReportEntity {

    @Id
    @GeneratedValue(generator = "sq_dat_report",  strategy = GenerationType.SEQUENCE)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "report_group")
    private String reportGroup;

    @Column(name = "report_name")
    private String reportName;

}
