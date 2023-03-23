package itstep.social_freedom.service;

import itstep.social_freedom.exceptions.storage.StorageException;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileService {
    @Value("${upload.path}")
    public String uploadDir;

    public String uploadFile(MultipartFile file, String path) throws IOException {
        InputStream stream;
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
            String nameFile = nameUUID(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
            Path copyLocation = Paths.get(uploadDir + path + File.separator + nameFile);
            Files.copy(stream, copyLocation, StandardCopyOption.REPLACE_EXISTING);
            return (path + nameFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new StorageException("Could not store file " + file.getOriginalFilename()
                    + ". Please try again!");
        }
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
