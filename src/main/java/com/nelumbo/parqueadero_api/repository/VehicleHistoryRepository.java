package com.nelumbo.parqueadero_api.repository;

import com.nelumbo.parqueadero_api.models.VehicleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleHistoryRepository extends JpaRepository<VehicleHistory, Long> {}
