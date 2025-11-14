package com.hamsini.backend.model;

import java.util.Date;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "media_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    private String type; // "image" or "video"
    private String fileName;
    private String originalFileName;
    private String filePath; // Path where file is stored

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;
    
    @PrePersist
    protected void onCreate() {
        uploadDate = new Date();
    }
}