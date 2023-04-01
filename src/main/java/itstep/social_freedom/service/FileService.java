package itstep.social_freedom.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import itstep.social_freedom.exceptions.storage.StorageException;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileService {

    private static AmazonS3 s3client;

    @Value("${aws.s3.region.name}")
    private String region;
    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    @Value("${aws.access.key.id}")
    private String accessKey;
    @Value("${aws.access.key.secret}")
    private String secretKey;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region).build();
    }


    public String uploadFile(MultipartFile file, String path) throws IOException {
        BufferedImage image;
        String nameFile = "";
        String fileUrl = "";
        String pathNew = path.replace(String.valueOf('/'), "");
        BufferedImage img = ImageIO.read(file.getInputStream());
        if (pathNew.equals("avatar")) {
            if (img.getWidth() > 400 && img.getHeight() > 400) {
                int avatarWidth = 400;
                int avatarHeight = 400;
                image = resizeImage(img, avatarWidth, avatarHeight);
            } else
                image = img;
        } else {
            if (img.getWidth() > 800 && img.getHeight() > 600) {
                int postWidth = 800;
                int postHeight = 600;
                image = resizeImage(img, postWidth, postHeight);
            } else
                image = img;
        }

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, 1);
            File fileOut = convertMultiPartToFile(image, file);
            nameFile = nameUUID(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
            uploadFileTos3bucket(nameFile, fileOut);
            fileOut.delete();
        } catch (Exception e) {
            e.printStackTrace();
            throw new StorageException("Could not store file " + file.getOriginalFilename()
                    + ". Please try again!");
        }
        s3client.setObjectAcl(bucketName, nameFile, CannedAccessControlList.PublicRead);
        fileUrl = String.valueOf(s3client.getUrl(bucketName, nameFile));
        return fileUrl;
    }

    private File convertMultiPartToFile(BufferedImage image, MultipartFile file) throws IOException {
        String nameImage = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()))
                .split("\\.")[1];
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        ImageIO.write(image, nameImage, convFile);
        return convFile;
    }

    public static InputStream downloadFile(String key){
        String buck = "elasticbeanstalk-eu-west-3-668774714230";
        try {
            S3Object s3object = s3client.getObject(new GetObjectRequest(buck, key));

            return s3object.getObjectContent();
        } catch (AmazonServiceException serviceException) {
            System.out.println("AmazonServiceException Message:    " + serviceException.getMessage());
            throw serviceException;
        } catch (AmazonClientException clientException) {
            System.out.println("AmazonClientException Message: " + clientException.getMessage());
            throw clientException;
        }
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    private String nameUUID(String name) {
        String[] arr = name.split("\\.");
        String newName = UUID.randomUUID().toString();
        name = newName + "." + arr[1];
        return name;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        return Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_EXACT, targetWidth,
                targetHeight, Scalr.OP_ANTIALIAS);
    }

}
