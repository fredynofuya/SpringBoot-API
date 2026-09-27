package com.api.Citaya.repositories;

import com.api.Citaya.models.ConsultorioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IConsultorioRepository extends JpaRepository<ConsultorioModel, Integer> {
}