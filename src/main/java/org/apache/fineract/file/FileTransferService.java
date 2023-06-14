package org.apache.fineract.file;

import java.io.File;
import org.springframework.stereotype.Service;

@Service
public interface FileTransferService {

    String uploadFile(File file, String bucketName);

    byte[] downloadFile(String fileName, String bucketName);

    void deleteFile(String fileName, String bucketName);

}
