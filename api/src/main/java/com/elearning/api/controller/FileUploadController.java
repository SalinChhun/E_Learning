package com.elearning.api.controller;

import com.elearning.api.service.file.FileUploadService;
import com.elearning.common.common.RestApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/wba/v1/files")
@RequiredArgsConstructor
public class FileUploadController extends RestApiResponse {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@Valid @NotNull @RequestPart(name= "file_data") MultipartFile fileData) throws Exception{
        return ok(fileUploadService.uploadImage(fileData));
    }
}
