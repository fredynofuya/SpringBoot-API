package com.api.Citaya.controllers;

import com.api.Citaya.models.ConsultorioModel;
import com.api.Citaya.repositories.IConsultorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/consultorio")
public class ConsultorioController {

    @Autowired
    private IConsultorioRepository consultorioRepository;

    @GetMapping
    public List<ConsultorioModel> get() {
        return consultorioRepository.findAll();
    }
}