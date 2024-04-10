package org.apache.fineract.file;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.mysql.cj.log.Log;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Service
@Qualifier("awsStorage")
public class AwsFileTransferImpl implements FileTransferService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String accessSecret;

    @Value("${cloud.aws.region.static}")
    private String region;
    @Autowired
    private AmazonS3 s3Client;
    @Value("${cloud.aws.s3BaseUrl}")
    private String endpoint;
    @Value("${cloud.aws.minio-public-host}")
    private String minioPublicHost;
    private static final String MINIO = "minio";
    @Override
    public String uploadFile(File file, String bucketName) {

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, accessSecret);

        s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withPathStyleAccessEnabled(true).withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .build();
        String fileName = System.currentTimeMillis() + "_" + file.getName();
        logger.info("uploading file");
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
        String url = s3Client.getUrl(bucketName, fileName).toString();
        if(url.contains(MINIO)){
            url = url.replaceFirst("^.*(?=/"+bucketName+")",minioPublicHost);
        }
        logger.debug("File access URL",url);
        file.delete();
        return url;
    }

    @Override
    public byte[] downloadFile(String fileName, String bucketName) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, accessSecret);
        s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withPathStyleAccessEnabled(true).withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .build();
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteFile(String fileName, String bucketName) {
        s3Client.deleteObject(bucketName, fileName);
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            logger.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }
}
