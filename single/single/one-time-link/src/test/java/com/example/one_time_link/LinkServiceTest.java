package com.example.one_time_link;

import com.example.one_time_link.service.LinkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LinkServiceTest {

    private LinkService linkService;

    @BeforeEach
    void setUp() {
        linkService = new LinkService();
    }

    @Test
    void testGenerateLinkWithFile() throws IOException {
        // Create a fake file to simulate a user uploading an image
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "secret-schematic.png",
                "image/png",
                "fake image byte data".getBytes()
        );

        // Generate the token
        String token = linkService.generateLink(mockFile);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testSingleUseAccessAndDestruction() throws IOException {
        // 1. Upload the mock file
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "secret-schematic.png",
                "image/png",
                "fake image byte data".getBytes()
        );
        String token = linkService.generateLink(mockFile);

        // 2. First Access: The system should return the image data
        LinkService.SecureImage retrievedImage = linkService.accessLink(token);
        assertNotNull(retrievedImage, "First access should grant the image");
        assertEquals("image/png", retrievedImage.contentType);
        assertArrayEquals("fake image byte data".getBytes(), retrievedImage.data);

        // 3. Second Access: The system should return null because it was destroyed
        LinkService.SecureImage secondAttempt = linkService.accessLink(token);
        assertNull(secondAttempt, "Second access should be denied (null) because the file was wiped from RAM");
    }
}