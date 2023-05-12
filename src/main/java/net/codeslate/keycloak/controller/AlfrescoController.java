package net.codeslate.keycloak.controller;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;


@RestController
public class AlfrescoController {

    private final RestTemplate restTemplate;

    @Autowired
    public AlfrescoController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }





    /****************PDF VIEWER********************************/
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/pdfviewer")
    public String pdfViewer(Model model) {
        // Add any necessary data to the model
        return "pdfviewer.html";
    }

    /*******************Download REST API***********************/

    @GetMapping(value = "/download-document/{nodeId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadDocument(@PathVariable String nodeId) {
        String url = "http://localhost:8080/alfresco/api/-default-/public/alfresco/versions/1/nodes/" + nodeId + "/content";
        HttpHeaders headers = new HttpHeaders();
        String auth = "admin:admin"; // replace with your actual username and password
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        System.out.println(authHeader);
        headers.set("Authorization", authHeader);
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), byte[].class);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document.pdf")
                .body(response.getBody());
    }


    /******************UPLOAD DOCUMENT REST API***************************************/

    /*Todo*/ //manage the size of the document ( maximum size and also if the document already exist there )
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFiles(@RequestParam("file") MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        String auth = "admin:admin";
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("name", file.getOriginalFilename());
        body.add("relativePath", "/folders");
        body.add("filedata", new InputStreamResource(file.getInputStream()) {
            @Override
            public long contentLength() {
                return file.getSize();
            }

            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        try {
            ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/alfresco/api/-default-/public/alfresco/versions/1/nodes/-my-/children", HttpMethod.POST, requestEntity, String.class);
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }

    /******************************SEARCH DOCUMENT API REST **************************************/
    private final String alfrescoSearchUrl = "http://localhost:8080/alfresco/api/-default-/public/search/versions/1/search";
    @PostMapping("/search")
    public ResponseEntity<String> search(@RequestBody String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("admin", "admin"); // replace with actual username and password

        HttpEntity<String> request = new HttpEntity<>(query, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(alfrescoSearchUrl, request, String.class);

        return response;
    }

}

