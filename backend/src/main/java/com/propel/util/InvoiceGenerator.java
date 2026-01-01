package com.propel.util;
import com.propel.entity.*;
import org.springframework.stereotype.Component;
import java.util.Base64;

@Component
public class InvoiceGenerator {
    public String generateInvoiceBase64(Contract contract, User contractor, User client) {
        String html = "<!DOCTYPE html><html><body><h1>PROPEL INVOICE</h1>" +
                      "<p>Invoice #" + contract.getId() + "</p>" +
                      "<p>From: " + contractor.getName() + "</p>" +
                      "<p>To: " + client.getName() + "</p>" +
                      "<h3>Total: $" + contract.getAmount() + "</h3></body></html>";
        return Base64.getEncoder().encodeToString(html.getBytes());
    }
}
