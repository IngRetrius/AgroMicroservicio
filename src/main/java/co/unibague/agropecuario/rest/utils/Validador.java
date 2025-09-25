package co.unibague.agropecuario.rest.utils;

import java.time.LocalDateTime;

public class Validador {

    public static boolean validarCadenaNoVacia(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    public static boolean validarNumeroPositivo(double valor) {
        return valor > 0;
    }

    public static boolean validarNumeroNoNegativo(double valor) {
        return valor >= 0;
    }

    public static boolean validarRangoHectareas(double hectareas) {
        return hectareas >= 0.1 && hectareas <= 10000;
    }

    public static boolean validarNumeroAnimales(int numeroAnimales) {
        return numeroAnimales >= 1 && numeroAnimales <= 50000;
    }

    public static boolean validarPrecioRazonable(double precio) {
        return precio >= 100 && precio <= 1000000;
    }

    public static boolean validarFechaNoFutura(LocalDateTime fecha) {
        return fecha != null && !fecha.isAfter(LocalDateTime.now());
    }

    public static String limpiarCadena(String valor) {
        if (valor == null) return "";
        return valor.trim().replaceAll("\\s+", " ");
    }
}