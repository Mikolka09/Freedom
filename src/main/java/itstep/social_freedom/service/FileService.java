package itstep.social_freedom.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileService {

    private AmazonS3 s3client;

    @Value("${aws.s3.endpoint.url}")
    private String endpointUrl;

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
        InputStream stream;
        String nameFile = "";
        String fileUrl = "";
        String pathNew = path.replace(String.valueOf('/'), "");
        BufferedImage img = ImageIO.read(file.getInputStream());
        if (pathNew.equals("avatar")) {
            if (img.getWidth() > 400 && img.getHeight() > 400) {
                int avatarWidth = 400;
                int avatarHeight = 400;
                BufferedImage image = resizeImage(img, avatarWidth, avatarHeight);
                stream = asInputStream(image);
            } else
                stream = asInputStream(img);
        } else {
            if (img.getWidth() > 800 && img.getHeight() > 600) {
                int postWidth = 800;
                int postHeight = 600;
                BufferedImage image = resizeImage(img, postWidth, postHeight);
                stream = asInputStream(image);
            } else
                stream = asInputStream(img);
        }

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, 1);
            File fileOut = convertMultiPartToFile(stream, file);
            nameFile = nameUUID(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
            uploadFileTos3bucket(nameFile, fileOut);
        } catch (Exception e) {
            e.printStackTrace();
            throw new StorageException("Could not store file " + file.getOriginalFilename()
                    + ". Please try again!");
        }
        s3client.setObjectAcl(bucketName, nameFile, CannedAccessControlList.PublicRead);
        fileUrl = String.valueOf(s3client.getUrl(bucketName, nameFile));
        return fileUrl;
    }

    private File convertMultiPartToFile(InputStream stream, MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(stream.available());
        fos.close();
        return convFile;
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

    private InputStream asInputStream(BufferedImage buff) throws IOException {
        ByteArrayOutputStream str = new ByteArrayOutputStream();
        ImageIO.write(buff, "jpg", str);
        return new ByteArrayInputStream(str.toByteArray());
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        return Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_EXACT, targetWidth,
                targetHeight, Scalr.OP_ANTIALIAS);
    }

}
