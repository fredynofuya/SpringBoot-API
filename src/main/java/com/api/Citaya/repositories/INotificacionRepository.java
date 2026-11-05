package com.api.Citaya.repositories;

import com.api.Citaya.models.NotificacionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface INotificacionRepository extends JpaRepository<NotificacionModel, Integer> {}