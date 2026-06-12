package com.example.one_time_link.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LinkService {
    
    // A secure wrapper to hold the image bytes and its file type (e.g., image/png)
    public static class SecureImage {
        public final byte[] data;
        public final String contentType;
        
        public SecureImage(byte[] data, String contentType) {
            this.data = data;
            this.contentType = contentType;
        }
    }

    // Upgraded: The map now holds the actual file data in memory!
    private final ConcurrentHashMap<String, SecureImage> tokenStore = new ConcurrentHashMap<>();

    // Generates the token and saves the uploaded file
    public String generateLink(MultipartFile file) throws IOException {
        String token = UUID.randomUUID().toString();
        
        // Save the raw bytes of the image into the thread-safe map
        tokenStore.put(token, new SecureImage(file.getBytes(), file.getContentType()));
        return token;
    }

    // Fetches the image and DESTROYS it in one atomic step
    public SecureImage accessLink(String token) {
        // The .remove() method gets the data and deletes it simultaneously.
        // This guarantees perfect single-use security.
        return tokenStore.remove(token); 
    }
}