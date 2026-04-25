package com.aulaclick.repository;

import com.aulaclick.entity.ImagenGaleria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImagenGaleriaRepository extends JpaRepository<ImagenGaleria, Long> {

    Optional<ImagenGaleria> findByUrl(String url);

    boolean existsByUrl(String url);
}
