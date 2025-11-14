package com.hamsini.backend.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hamsini.backend.config.FileStorageProperties;
import com.hamsini.backend.model.AdminLogin;
import com.hamsini.backend.model.EnrollRequest;
import com.hamsini.backend.model.MediaItem;
import com.hamsini.backend.repository.EnrollRepository;
import com.hamsini.backend.repository.MediaRepository;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "https://www.hamsinitechsolutions.com")
public class AdminController {
    
    @Autowired
    private EnrollRepository enrollRepo;

    @Autowired
    private MediaRepository mediaRepo;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    // Admin login endpoint
    @PostMapping("/login")
    public ResponseEntity<String> adminLogin(@RequestBody AdminLogin login) {
        if ("admin@gmail.com".equals(login.getEmail()) && "admin123".equals(login.getPassword())) {
            return ResponseEntity.ok("Login Successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    // Get all enroll details (only for admin)
    @GetMapping("/enrolls")
    public ResponseEntity<List<EnrollRequest>> getAllEnrolls() {
        List<EnrollRequest> enrolls = enrollRepo.findAll();
        return ResponseEntity.ok(enrolls);
    }

    // Upload multiple files
@PostMapping("/upload-media")
public ResponseEntity<List<String>> uploadMultipleMedia(
        @RequestParam("titles") List<String> titles,
        @RequestParam("descriptions") List<String> descriptions,
        @RequestParam("types") List<String> types,
        @RequestParam("files") MultipartFile[] files) {
    
    List<String> results = new ArrayList<>();
    
    try {
        String uploadDir = fileStorageProperties.getUploadDir();
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        System.out.println("Upload directory: " + uploadPath.toAbsolutePath());
        System.out.println("Number of files received: " + files.length);

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            String title = titles.size() > i ? titles.get(i) : file.getOriginalFilename();
            String description = descriptions.size() > i ? descriptions.get(i) : "";
            
            // FIX: Always detect file type from content type, don't use "auto"
            String fileType = getFileType(file.getContentType());

            System.out.println("Processing file: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());
            System.out.println("Content type: " + file.getContentType());
            System.out.println("Detected file type: " + fileType); // Debug log

            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Save file to disk
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath);

            System.out.println("File saved to: " + filePath.toAbsolutePath());

            // Save to database
            MediaItem mediaItem = new MediaItem();
            mediaItem.setTitle(title);
            mediaItem.setDescription(description);
            mediaItem.setType(fileType); // This should be "image" or "video", not "auto"
            mediaItem.setFileName(uniqueFileName);
            mediaItem.setOriginalFileName(originalFileName);
            mediaItem.setFilePath(filePath.toString());
            mediaItem.setUploadDate(new java.util.Date());
            
            MediaItem savedItem = mediaRepo.save(mediaItem);
            System.out.println("Media item saved to database with ID: " + savedItem.getId() + " Type: " + fileType);
            
            results.add("Uploaded: " + originalFileName + " as " + fileType);
        }
        
        return ResponseEntity.ok(results);
        
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(List.of("Upload failed: " + e.getMessage()));
    }
}

// FIX: Improve file type detection
private String getFileType(String contentType) {
    if (contentType == null) {
        return "file";
    }
    if (contentType.startsWith("image/")) {
        return "image";
    } else if (contentType.startsWith("video/")) {
        return "video";
    } else if (contentType.startsWith("audio/")) {
        return "audio";
    } else {
        return "file";
    }
}

    // Get all media items for events page
    @GetMapping("/media")
    public ResponseEntity<List<MediaItem>> getAllMedia() {
        List<MediaItem> mediaItems = mediaRepo.findAllByOrderByUploadDateDesc();
        System.out.println("Retrieved " + mediaItems.size() + " media items from database");
        
        for (MediaItem item : mediaItems) {
            System.out.println("Media Item: " + item.getTitle() + " - " + item.getFileName() + " - " + item.getType());
        }
        
        return ResponseEntity.ok(mediaItems);
    }

    // Get media by type
    @GetMapping("/media/type/{type}")
    public ResponseEntity<List<MediaItem>> getMediaByType(@PathVariable String type) {
        List<MediaItem> mediaItems = mediaRepo.findByTypeOrderByUploadDateDesc(type);
        return ResponseEntity.ok(mediaItems);
    }

    // Delete media by ID
    @PostMapping("/media/delete/{id}")
    public ResponseEntity<String> deleteMedia(@PathVariable Long id) {
        try {
            MediaItem mediaItem = mediaRepo.findById(id).orElse(null);
            if (mediaItem != null) {
                // Delete file from disk
                Path filePath = Paths.get(mediaItem.getFilePath());
                Files.deleteIfExists(filePath);
                
                // Delete from database
                mediaRepo.deleteById(id);
                return ResponseEntity.ok("Media deleted successfully");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Media not found");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Delete failed: " + e.getMessage());
        }
    }

    

    // Add this method to your AdminController
@GetMapping("/debug-files")
public ResponseEntity<List<String>> debugFiles() {
    try {
        String uploadDir = fileStorageProperties.getUploadDir();
        Path uploadPath = Paths.get(uploadDir);
        
        if (!Files.exists(uploadPath)) {
            return ResponseEntity.ok(List.of("Upload directory does not exist: " + uploadPath.toAbsolutePath()));
        }
        
        List<String> fileList = new ArrayList<>();
        Files.list(uploadPath).forEach(path -> {
            fileList.add(path.getFileName().toString());
        });
        
        return ResponseEntity.ok(fileList);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(List.of("Error reading upload directory: " + e.getMessage()));
    }
}
}