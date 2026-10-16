package com.api.Citaya.repositories;

import com.api.Citaya.models.EspecialidadModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEspecialidadRepository extends JpaRepository<EspecialidadModel, Integer> {}