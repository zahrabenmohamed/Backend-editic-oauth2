package net.codeslate.keycloak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;


@Controller
public class UserController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping({"pdfviewer"})
    public String getIndex(Model model, @AuthenticationPrincipal OAuth2User principal, OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient authorizedClient = this.getAuthorizedClient(authentication);
        return "pdfviewer";

    }
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> displayPdf() throws IOException {
        ClassPathResource pdfFile = new ClassPathResource("static/pdfs/invoice.pdf");
        byte[] pdfBytes = Files.readAllBytes(pdfFile.getFile().toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "pdf.pdf");
        headers.setCacheControl("must-revalidate, no-store");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }


    @GetMapping("/download/pdf")
    public ResponseEntity<byte[]> downloadPdf() throws IOException {
        ClassPathResource pdfFile = new ClassPathResource("static/pdfs/zahra.pdf");
        byte[] pdfBytes = Files.readAllBytes(pdfFile.getFile().toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice.pdf");
        headers.setCacheControl("must-revalidate, no-store");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }


    private OAuth2AuthorizedClient getAuthorizedClient(OAuth2AuthenticationToken authentication) {
        return this.authorizedClientService.loadAuthorizedClient(
            authentication.getAuthorizedClientRegistrationId(), authentication.getName());
    }
}