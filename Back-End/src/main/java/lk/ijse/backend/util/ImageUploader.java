package lk.ijse.backend.util;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class ImageUploader {

    private static final String API_KEY = "6d207e02198a847aa98d0a2a901485a5";
    private static final String UPLOAD_URL = "https://freeimage.host/api/1/upload";

    public static String uploadImage(MultipartFile data) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Wrap MultipartFile in InputStreamResource
            InputStreamResource resource = new InputStreamResource(data.getInputStream()) {
                @Override
                public String getFilename() {
                    return data.getOriginalFilename();
                }

                @Override
                public long contentLength() throws IOException {
                    return data.getSize();
                }
            };

            // Prepare multipart request
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("source", resource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Send POST request
            ResponseEntity<String> response = restTemplate.postForEntity(
                    UPLOAD_URL + "?key=" + API_KEY,
                    requestEntity,
                    String.class
            );

            // Parse display_url from JSON response
            String json = response.getBody();
            int start = json.indexOf("\"display_url\":\"") + 15;
            int end = json.indexOf("\"", start);
            return json.substring(start, end).replace("\\/", "/");

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
