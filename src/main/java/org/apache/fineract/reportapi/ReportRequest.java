package org.apache.fineract.reportapi;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Data
@Table(name = "report_request")
@Getter
@Setter
public class ReportRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_name", nullable = false)
    private String reportName;

    @Column(name = "report_type")
    private String reportType;

    @Column(name = "report_subType")
    private String reportSubType;

    @Column(name = "report_category")
    private String reportCategory;

    @Column
    private String description;

    @Column(name = "report_sql", nullable = false)
    private String reportSql;

    @OneToMany(mappedBy = "reportRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportParameter> reportParameters;

}
