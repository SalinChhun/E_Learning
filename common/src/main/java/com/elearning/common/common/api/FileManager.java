package com.elearning.common.common.api;

import com.elearning.common.components.properties.FileInfoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileManager {

    public static FileInfoConfig fileInfoConfig;

    @Autowired
    public void setFileInfoConfig(FileInfoConfig fileInfoConfig) {
        this.fileInfoConfig = fileInfoConfig;
    }

    /**
     * Method storeImage : Handle the process for storing images on server
     * @param files MultipartFile
     * @return File object
     * @throws Exception
     */
    public static String storeImage(MultipartFile files) throws Exception {
        return storeFile(files);
    }

    /**
     * Method storeFile : Handle the process for storing files (images, videos, etc.) on server
     * @param files MultipartFile
     * @return File name
     * @throws Exception
     */
    public static String storeFile(MultipartFile files) throws Exception {
        final String fileName = UUID.randomUUID() + "." + files.getOriginalFilename().substring(files.getOriginalFilename().lastIndexOf(".") +  1);
        String targetPath = fileInfoConfig.getServerPath();
        File targetFile = new File(Paths.get(targetPath).toUri());

        if(!targetFile.exists())
            targetFile.mkdirs();

        File targetFileObj = new File(Paths.get(targetPath, fileName).toUri());

        if(targetFileObj.exists())
            targetFileObj.delete();

        Files.copy(files.getInputStream(), targetFileObj.toPath());

        return fileName;
    }

}
