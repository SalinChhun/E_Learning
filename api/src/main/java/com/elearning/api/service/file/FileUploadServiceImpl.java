package com.elearning.api.service.file;

import com.elearning.common.common.api.FileManager;
import com.elearning.common.enums.StatusCode;
import com.elearning.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Override
    public Object uploadImage(MultipartFile fileData) throws Exception {
        if(fileData.isEmpty()) throw new BusinessException(StatusCode.IMAGE_CANNOT_BE_EMPTY);

        String imageUrl =  FileManager.storeImage(fileData);

        Map<String, String> data = new HashMap<>();
        data.put("image_url",imageUrl);

        return data;
    }
}
