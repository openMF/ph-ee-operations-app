package org.apache.fineract.operations;

import junit.framework.TestCase;
import org.junit.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FileTransportTest {

    @Test
    public void test() {
        List<FileTransport> fileTransportList = List.of(
                new FileTransport(1L, 1L, new Date(), new Date(), new Date(), new Date(), FileTransport.TransportStatus.COMPLETED, toString(), "BIC1", FileTransport.TransportType.IG2, FileTransport.TransportDirection.IN, Collections.emptyList()),
                new FileTransport(2L, 1L, new Date(), new Date(), new Date(), new Date(), FileTransport.TransportStatus.COMPLETED, toString(), "BIC1", FileTransport.TransportType.IG2, FileTransport.TransportDirection.IN, Collections.emptyList())
        );
        ModelMapper modelMapper = new ModelMapper();
        List<FileTransportDto> fileTransportDtoList = fileTransportList.stream()
                .map(t -> modelMapper.map(t, FileTransportDto.class))
                .toList();
        TestCase.assertEquals(fileTransportList.get(0).getWorkflowInstanceKey(), fileTransportDtoList.get(0).getWorkflowInstanceKey());
        TestCase.assertEquals(fileTransportList.get(1).getWorkflowInstanceKey(), fileTransportDtoList.get(1).getWorkflowInstanceKey());

        Pageable pageable = PageRequest.of(1, 10, Sort.by("transactionDate").descending());
        Page<FileTransportDto> page =  new PageImpl<>(fileTransportDtoList, pageable, fileTransportDtoList.size());

        TestCase.assertEquals(fileTransportList.get(0).getWorkflowInstanceKey(), page.get().toList().get(0).getWorkflowInstanceKey());
    }
}