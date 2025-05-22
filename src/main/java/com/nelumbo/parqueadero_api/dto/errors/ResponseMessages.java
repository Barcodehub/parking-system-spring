package com.nelumbo.parqueadero_api.dto.errors;

public record ResponseMessages() {
    public static final String NO_PARKING = "No se encontraron parqueaderos";
    public static final String No_FREQ_PARKING = "No hay vehículos frecuentes o no existen parqueaderos";
    public static final String No_FREQ_IN_PARKING = "No hay vehículos frecuentes en el parqueadero especificado";
    public static final String No_VEH_FIRST_TIME = "No hay vehículos por primera vez en el parqueadero especificado";
    public static final String No_INGRESOS = "No se encontraron registros de ingresos para el parqueadero";
    public static final String No_WEEK_INGRESOS = "no hay datos de ingresos semanales para socios o no hay Socios registrados en el sistema";
    public static final String No_WEEK_ParkingINGRESOS = "No hay datos de ingresos semanales para parqueaderos o no hay parqueaderos registrados";

}