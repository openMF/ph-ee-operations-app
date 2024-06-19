package org.apache.fineract.reportrequest;

import lombok.*;

import javax.persistence.*;

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
