package org.apache.fineract.operations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DelayResponseDTO {

    private long averageExportImportTime;
    private long averageZeebeExportTime;
    private int eventsCount;
}