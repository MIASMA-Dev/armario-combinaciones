package com.armario.repository;

import com.armario.domain.Prenda;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PrendaRepository extends JpaRepository<Prenda, Long> {
    List<Prenda> findByProfileId(Long profileId);
    List<Prenda> findByProfileIdAndTipoPrenda(Long profileId, com.armario.domain.TipoPrenda tipoPrenda);
}