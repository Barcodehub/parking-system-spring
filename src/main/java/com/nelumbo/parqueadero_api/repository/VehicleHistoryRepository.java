package com.nelumbo.parqueadero_api.repository;

import com.nelumbo.parqueadero_api.models.VehicleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VehicleHistoryRepository extends JpaRepository<VehicleHistory, Integer> {

    // Top 10 vehículos más frecuentes (global)
    @Query("SELECT vh.placa, COUNT(vh) as count FROM VehicleHistory vh " +
            "GROUP BY vh.placa ORDER BY count DESC LIMIT 10")
    List<Object[]> findTop10MostFrequentVehicles();

    // Top 10 vehículos por parqueadero
    @Query("SELECT vh.placa, COUNT(vh) as count FROM VehicleHistory vh " +
            "WHERE vh.parqueadero.id = :parkingId " +
            "GROUP BY vh.placa ORDER BY count DESC LIMIT 10")
    List<Object[]> findTop10ByParkingId(@Param("parkingId") Long parkingId);

    // Placas por parqueadero (para verificación de primera vez)
    @Query("SELECT DISTINCT vh.placa FROM VehicleHistory vh WHERE vh.parqueadero.id = :parkingId")
    List<String> findPlacasByParking(@Param("parkingId") Long parkingId);

    // Consultas de ganancias (versión compatible con PostgreSQL)
    @Query(value = "SELECT SUM(vh.costo) FROM vehicle_history vh " +
            "WHERE vh.parqueadero_id = :parkingId " +
            "AND DATE_TRUNC('day', vh.fecha_salida) = CURRENT_DATE",
            nativeQuery = true)
    BigDecimal findTodayEarnings(@Param("parkingId") Long parkingId);

    @Query(value = "SELECT SUM(vh.costo) FROM vehicle_history vh " +
            "WHERE vh.parqueadero_id = :parkingId " +
            "AND vh.fecha_salida >= DATE_TRUNC('week', CURRENT_DATE) " +
            "AND vh.fecha_salida < DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '1 week'",
            nativeQuery = true)
    BigDecimal findWeeklyEarnings(@Param("parkingId") Long parkingId);

    @Query(value = "SELECT SUM(vh.costo) FROM vehicle_history vh " +
            "WHERE vh.parqueadero_id = :parkingId " +
            "AND DATE_TRUNC('month', vh.fecha_salida) = DATE_TRUNC('month', CURRENT_DATE)",
            nativeQuery = true)
    BigDecimal findMonthlyEarnings(@Param("parkingId") Long parkingId);

    @Query(value = "SELECT SUM(vh.costo) FROM vehicle_history vh " +
            "WHERE vh.parqueadero_id = :parkingId " +
            "AND DATE_TRUNC('year', vh.fecha_salida) = DATE_TRUNC('year', CURRENT_DATE)",
            nativeQuery = true)
    BigDecimal findYearlyEarnings(@Param("parkingId") Long parkingId);

    // Top 3 socios por ganancias semanales
    @Query("SELECT vh.socio.name, COUNT(vh), SUM(vh.costo) " +
            "FROM VehicleHistory vh " +
            "WHERE vh.fechaSalida >= :startOfWeek AND vh.fechaSalida < :endOfWeek " +
            "GROUP BY vh.socio.id, vh.socio.name " +
            "ORDER BY SUM(vh.costo) DESC LIMIT 3")
    List<Object[]> findTop3SociosByWeeklyEarnings(
            @Param("startOfWeek") LocalDateTime startOfWeek,
            @Param("endOfWeek") LocalDateTime endOfWeek);

    // Top 3 parqueaderos por ganancias semanales
    @Query("SELECT vh.parqueadero.nombre, SUM(vh.costo) " +
            "FROM VehicleHistory vh " +
            "WHERE vh.fechaSalida >= :startOfWeek AND vh.fechaSalida < :endOfWeek " +
            "GROUP BY vh.parqueadero.id, vh.parqueadero.nombre " +
            "ORDER BY SUM(vh.costo) DESC LIMIT 3")
    List<Object[]> findTop3ParkingsByWeeklyEarnings(
            @Param("startOfWeek") LocalDateTime startOfWeek,
            @Param("endOfWeek") LocalDateTime endOfWeek);


}

