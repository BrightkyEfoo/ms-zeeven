package com.cs.ge.controllers;

import com.cs.ge.entites.QRCodeEntity;
import com.cs.ge.services.qrcode.QRCodeGeneratorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.URI;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@AllArgsConstructor
@RestController
@RequestMapping(path = "qr-code", produces = APPLICATION_JSON_VALUE)
public class QRController {
    private final QRCodeGeneratorService qrCodeGeneratorService;


    @ResponseStatus(HttpStatus.OK)
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public String add(@RequestBody final QRCodeEntity qrCodeEntity) throws IOException {
        return this.qrCodeGeneratorService.generate(qrCodeEntity);
    }

    @GetMapping(value = "/{publicId}")
    public ResponseEntity<Object> get(@PathVariable final String publicId) throws IOException {
        final String url = this.qrCodeGeneratorService.content(publicId);
        final RedirectView redirectView = new RedirectView();
        redirectView.setUrl(url);
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(URI.create(url)).build();
    }

}
