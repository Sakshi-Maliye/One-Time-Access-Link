package com.example.one_time_link.controller;

import com.example.one_time_link.service.LinkService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/links")
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    // NEW: Accepts a file upload from the frontend
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String generatedToken = linkService.generateLink(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(generatedToken);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }
    
    @GetMapping("/{token}")
    public ResponseEntity<Object> accessLink(@PathVariable String token) {
        // This will fetch the image AND instantly delete it from memory
        LinkService.SecureImage image = linkService.accessLink(token);
        
        // Strict Anti-Caching Headers (Crucial for the Refresh Trap!)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate"); 
        headers.add("Pragma", "no-cache"); 
        headers.add("Expires", "0"); 
        
        if (image != null) {
            // Access Granted: Send the real image bytes to the browser
            headers.add(HttpHeaders.CONTENT_TYPE, image.contentType);
            return new ResponseEntity<>(image.data, headers, HttpStatus.OK); 
        } else {
            // The Trap: Switch to HTML and show the 410 GONE error
            headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");
            
            String errorHtml = "<html><head><title>Access Denied</title></head>" +
                               "<body style='background-color: #fffafb;'>" +
                               "<div style='font-family: Arial, sans-serif; text-align: center; margin-top: 100px;'>" +
                               "<h1 style='color: #d9534f; font-size: 60px; margin-bottom: 10px;'>🛑 410 GONE</h1>" +
                               "<h2 style='color: #333;'>SECURITY ALERT: TOKEN BURNED</h2>" +
                               "<p style='color: #666; font-size: 18px; max-width: 500px; margin: 20px auto;'>This highly sensitive file has already been viewed and permanently erased from server memory.</p>" +
                               "</div></body></html>";
                               
            return new ResponseEntity<>(errorHtml, headers, HttpStatus.GONE);
        }
    }
}