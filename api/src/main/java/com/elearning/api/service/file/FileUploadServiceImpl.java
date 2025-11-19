package com.elearning.api.service.file;

import com.elearning.common.common.api.FileManager;
import com.elearning.common.components.properties.FileInfoConfig;
import com.elearning.common.enums.StatusCode;
import com.elearning.common.exception.BusinessException;
import com.elearning.common.util.ImageUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final FileInfoConfig fileInfoConfig;

    public FileUploadServiceImpl(FileInfoConfig fileInfoConfig) {
        this.fileInfoConfig = fileInfoConfig;
    }

    @Override
    public Object uploadImage(MultipartFile fileData) throws Exception {
        if(fileData.isEmpty()) throw new BusinessException(StatusCode.IMAGE_CANNOT_BE_EMPTY);

        String imageUrl =  FileManager.storeImage(fileData);

        Map<String, String> data = new HashMap<>();
        data.put("image_url",imageUrl);

        return data;
    }

    @Override
    public Object uploadVideo(MultipartFile fileData) throws Exception {
        if(fileData.isEmpty()) throw new BusinessException(StatusCode.IMAGE_CANNOT_BE_EMPTY);

        String videoUrl = FileManager.storeFile(fileData);

        Map<String, String> data = new HashMap<>();
        data.put("video_url", ImageUtil.getImageUrl(fileInfoConfig.getBaseUrl(), videoUrl));

        return data;
    }
}
