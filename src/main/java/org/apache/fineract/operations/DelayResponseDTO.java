package org.apache.fineract.operations;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DelayResponseDTO {

    private long averageExportImportTime;
    private long averageZeebeExportTime;
    private int eventsCount;

}
