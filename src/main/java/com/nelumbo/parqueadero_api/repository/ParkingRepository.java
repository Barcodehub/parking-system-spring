package com.nelumbo.parqueadero_api.repository;

import com.nelumbo.parqueadero_api.models.Parking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, Integer> {
    List<Parking> findBySocioId(Integer socio); // Para filtrar por socio
    boolean existsByNombreAndSocioId( @Param("nombre") String nombre,
                                      @Param("socioId") Integer socioId); // Validar nombre único por socio

    boolean existsByNombreAndSocioIdAndIdNot( @Param("nombre") String nombre,
                                      @Param("socioId") Integer socioId,
                                      @Param("parkingId") Integer parkingId);


        List<Parking> findBySocioEmail(String email);


        Optional<Parking> findByIdAndSocioEmail(Integer id, String email);
        Optional<Parking> findByIdAndSocioId(Integer id, Integer socioId);



}