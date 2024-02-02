package org.apache.fineract.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.fineract.service.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.stream.Collectors;


@RestController
@SecurityRequirement(name = "auth")
@RequestMapping("/api/v1")
@Tag(name = "Users API")
public class UtilityApi {

    @Autowired
    UtilityService utilityService;

    @PostMapping("/util/x-signature")
    public ResponseEntity<String> getXSignature(
            @RequestHeader("X-CorrelationID") String correlationId,
            @RequestHeader("Platform-TenantId") String tenantId,
            @RequestHeader("privateKey") String privateKey,
            @RequestParam(required = false) MultipartFile data,
            @RequestBody(required = false) String rawData
    )throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException, IOException {

        String tobeHashed = "";
        
        if(data != null && !data.isEmpty()) {
            String fileContent;
            try {
                fileContent = readInputStreamToString(data.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>("Failed to read file content", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            tobeHashed = correlationId+":"+tenantId+":"+fileContent;            
        }
        else if(rawData != null && !rawData.isEmpty()){
            tobeHashed = correlationId+":"+tenantId+":"+rawData;
        }
        else {
            return new ResponseEntity<>("No file or raw data provided", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(utilityService.getSignature(tobeHashed, privateKey), HttpStatus.OK);
    }

    private String readInputStreamToString(InputStream inputStream) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            return bufferedReader.lines().collect(Collectors.joining("\n"));
        }
    }
}
