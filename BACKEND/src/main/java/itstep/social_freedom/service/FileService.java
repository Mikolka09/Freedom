package itstep.social_freedom.service;

import itstep.social_freedom.exceptions.storage.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    public String uploadFile(MultipartFile file, String path) {

        try {
            String nameFile = nameUUID(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
            Path copyLocation = Paths.get(uploadDir + path + File.separator + nameFile);
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            return (path + nameFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new StorageException("Could not store file " + file.getOriginalFilename()
                    + ". Please try again!");
        }
    }

    public String nameUUID(String name) {
        String[] arr = name.split("\\.");
        String newName = UUID.randomUUID().toString();
        name = newName + "." + arr[1];
        return name;
    }
}
