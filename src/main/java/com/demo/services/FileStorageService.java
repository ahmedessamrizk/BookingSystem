package com.demo.services;

import com.demo.services.impl.UploadedFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
    UploadedFile uploadFile(MultipartFile file, String path);
    List<UploadedFile> uploadFiles(List<MultipartFile> files, String path);
    void delete(String publicId);
}
