package com.nelumbo.parqueadero_api.repository;

import com.nelumbo.parqueadero_api.models.Vehicle;
import com.nelumbo.parqueadero_api.models.VehicleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    boolean existsByPlacaAndFechaSalidaIsNull(String placa);

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.parqueadero.id = :parqueaderoId AND v.fechaSalida IS NULL")
    int countActiveVehiclesInParking(Long parqueaderoId);

    Optional<Vehicle> findByPlacaAndFechaSalidaIsNull(String placa);
    List<Vehicle> findByParqueaderoIdAndFechaSalidaIsNull(Integer parqueaderoId);

    List<Vehicle> findByParqueaderoId(Integer parkingId);

}



