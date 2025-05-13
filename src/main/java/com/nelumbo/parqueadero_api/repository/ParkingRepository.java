package com.nelumbo.parqueadero_api.repository;

import com.nelumbo.parqueadero_api.models.Parking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParkingRepository extends JpaRepository<Parking, Long> {
    List<Parking> findBySocioId(Integer socio_id); // Para filtrar por socio
    boolean existsByNombreAndSocioId( @Param("nombre") String nombre,
                                      @Param("socioId") Integer socioId); // Validar nombre único por socio

    boolean existsByNombreAndSocioIdAndIdNot( @Param("nombre") String nombre,
                                      @Param("socioId") Integer socioId,
                                      @Param("parkingId") Integer parkingId);

}