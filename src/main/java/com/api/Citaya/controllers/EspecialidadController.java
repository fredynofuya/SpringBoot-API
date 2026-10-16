package com.api.Citaya.controllers;

import com.api.Citaya.models.EspecialidadModel;
import com.api.Citaya.repositories.IEspecialidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/especialidad")
public class EspecialidadController {

    @Autowired
    private IEspecialidadRepository especialidadRepository;

    @GetMapping
    public List<EspecialidadModel> get() {
        return especialidadRepository.findAll();
    }
}