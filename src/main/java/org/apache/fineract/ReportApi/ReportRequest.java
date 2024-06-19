package org.apache.fineract.ReportApi;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Data
@Table(name = "REPORTREQUEST")
@Getter
@Setter
public class ReportRequest {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_name")
    private String reportName;

    @Column(name = "report_type")
    private String reportType;

    @Column(name = "report_subType")
    private String reportSubType;

    @Column(name = "report_category")
    private String reportCategory;

    @Column
    private String description;

    @Column(name = "report_sql")
    private String reportSql;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "reportRequest")
    private List<ReportParameter> reportParameters;
}
