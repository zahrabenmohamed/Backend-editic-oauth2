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


@Controller
public class UserController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping({"/","index"})
    public String getIndex(Model model, @AuthenticationPrincipal OAuth2User principal, OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient authorizedClient = this.getAuthorizedClient(authentication);
        model.addAttribute("userName",authentication.getPrincipal().getAttribute("preferred_username"));
        model.addAttribute("clientName",authorizedClient.getClientRegistration().getClientId());
        model.addAttribute("scopes",authorizedClient.getAccessToken().getScopes());
        return "index";

    }

    private OAuth2AuthorizedClient getAuthorizedClient(OAuth2AuthenticationToken authentication) {
        return this.authorizedClientService.loadAuthorizedClient(
            authentication.getAuthorizedClientRegistrationId(), authentication.getName());
    }
}