package com.elearning.api.controller;

import com.elearning.api.service.file.FileUploadService;
import com.elearning.common.common.RestApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "File Upload", description = "APIs for uploading images and videos")
@SecurityRequirement(name = "Bearer Authentication")
public class FileUploadController extends RestApiResponse {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload-image")
    @Operation(
            summary = "Upload image",
            description = "Uploads an image file and returns the image URL"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or file is empty"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> uploadImage(@Valid @NotNull @RequestPart(name= "file_data") MultipartFile fileData) throws Exception{
        return ok(fileUploadService.uploadImage(fileData));
    }

    @PostMapping("/upload-video")
    @Operation(
            summary = "Upload video",
            description = "Uploads a video file and returns the video URL"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or file is empty"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> uploadVideo(@Valid @NotNull @RequestPart(name = "file_data") MultipartFile fileData) throws Exception {
        return ok(fileUploadService.uploadVideo(fileData));
    }
}
