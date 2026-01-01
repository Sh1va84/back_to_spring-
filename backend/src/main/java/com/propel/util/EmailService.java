package com.propel.util;
import org.springframework.stereotype.Service;
@Service
public class EmailService {
    public void sendEmailWithAttachment(String to, String subject, String body, String attachmentName, String base64Content) {
        System.out.println("--- MOCK EMAIL SENT to " + to + " ---");
    }
}
