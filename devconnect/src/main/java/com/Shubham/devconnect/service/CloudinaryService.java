package com.Shubham.devconnect.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, String folder) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "devconnect/" + folder,
                            "resource_type", "auto"
                    )
            );
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed: "
                    + e.getMessage());
        }
    }

    public void deleteImage(String imageUrl) {
        try {
            // Extract public ID from URL
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId,
                    ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Image deletion failed: "
                    + e.getMessage());
        }
    }

    private String extractPublicId(String imageUrl) {
        // Extract public ID from Cloudinary URL
        String[] parts = imageUrl.split("/");
        String fileWithExtension = parts[parts.length - 1];
        String folder = parts[parts.length - 2];
        String fileName = fileWithExtension.split("\\.")[0];
        return "devconnect/" + folder + "/" + fileName;
    }
}