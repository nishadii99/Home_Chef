package lk.ijse.backend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;

@Service
@RequiredArgsConstructor
public class Payhere {
    @Value("${payhere.merchant.id}")
    private  String merchantId;

    @Value("${payhere.merchant.secret}")
    private  String appSecret;


    public String generateSignature(String orderId, double amount, String currency) {
        try {
            String data = merchantId + orderId + String.format("%.2f", amount) + currency + hash(appSecret).toUpperCase();
            System.out.println(data);
            return hash(data).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }
    private String hash(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
