package net.codeslate.keycloak.controller;

import org.springframework.beans.factory.annotation.Autowired;
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


@Controller
public class UserController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping({"index"})
    public String getIndex(Model model, @AuthenticationPrincipal OAuth2User principal, OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient authorizedClient = this.getAuthorizedClient(authentication);
        model.addAttribute("userName",authentication.getPrincipal().getAttribute("preferred_username"));
        model.addAttribute("clientName",authorizedClient.getClientRegistration().getClientId());
        return "";

    }

    @GetMapping("/pdfviewer")
    public String showPdfViewer(@RequestParam("documentUrl") String documentUrl, Model model) {
        model.addAttribute("documentUrl", documentUrl);
        return "index";
    }

    @GetMapping("/download")
    public void downloadDocument(@RequestParam("documentUrl") String documentUrl, HttpServletResponse response) {
        // Logic to download the document
        // You can use libraries like Apache HttpClient to fetch the document from the provided URL
        // Then, set the response headers and write the document content to the response output stream
        // For brevity, this example assumes you have already fetched the document and have it as a byte array

        byte[] documentContent = fetchDocumentContent(documentUrl);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=test.pdf");
        response.setContentLength(documentContent.length);

        try {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(documentContent);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] fetchDocumentContent(String documentUrl) {
        // Implement the logic to fetch the document content from the provided URL
        // You can use libraries like Apache HttpClient to make the HTTP request and obtain the document content

        // For demonstration purposes, this example simply returns a placeholder byte array
        return "This is a placeholder document content".getBytes();
    }

    private OAuth2AuthorizedClient getAuthorizedClient(OAuth2AuthenticationToken authentication) {
        return this.authorizedClientService.loadAuthorizedClient(
            authentication.getAuthorizedClientRegistrationId(), authentication.getName());
    }
}