package org.apache.fineract.reportapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Column;
import javax.persistence.FetchType;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "report_parameters")
public class ReportParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_request_id")
    private ReportRequest reportRequest;

    @Column(name = "parameter_key")
    private String parameterKey;

    @Column(name = "parameter_value")
    private String parameterValue;
}
