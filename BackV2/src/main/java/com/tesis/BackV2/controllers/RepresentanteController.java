package com.tesis.BackV2.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/representante/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class RepresentanteController {
}
