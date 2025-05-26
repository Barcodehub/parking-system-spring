package com.nelumbo.parqueadero_api.constants;

// In ApiPaths.java
public final class ApiPaths {

    public static final String REGISTRO = "/api/users";
    public static final String PARKINGS = "/api/parkings";
    public static final String ALLPARKINGS = "/api/parkings/parkings/**";
    public static final String ADMINPARKINGS = "/api/parkings/**";
    public static final String SOCIOPARKINGS = "/api/parkings/socio/my-parkings/**";


    public static final String VEHICULE_ENTRY = "/api/vehicles/entry";
    public static final String VEHICULE_EXIT = "/api/vehicles/exit";

    public static final String ANALITYC = "/api/analityc";
    public static final String EARNINGPARKING = "/api/analityc/parkings/top-earnings";
    public static final String EARNINGSOCIO = "/api/analityc/socios/top-earnings";
    public static final String EMAIL = "/api/admin/emails";

    private ApiPaths() {} // Prevent instantiation
}