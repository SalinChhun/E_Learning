package com.elearning.api.service.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    Object uploadImage(MultipartFile fileData) throws Exception;
    Object uploadVideo(MultipartFile fileData) throws Exception;
}
