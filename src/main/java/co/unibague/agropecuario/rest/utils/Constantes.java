package co.unibague.agropecuario.rest.utils;

public final class Constantes {

    private Constantes() {}

    public static final String[] TIPOS_CULTIVO = {
            "Café", "Arroz", "Cacao", "Mango", "Aguacate",
            "Cítricos", "Maracuyá", "Sorgo", "Algodón", "Plátano"
    };

    public static final String[] TIPOS_SUELO = {
            "Franco", "Franco arcilloso", "Franco arenoso",
            "Arcilloso", "Arenoso", "Limoso"
    };

    public static final String[] TEMPORADAS = {
            "Todo el año", "Temporada seca", "Temporada lluviosa",
            "Cosecha principal", "Cosecha mitaca", "Temporada alta", "Temporada baja"
    };

    public static final double MIN_HECTAREAS = 0.1;
    public static final double MAX_HECTAREAS = 10000.0;
    public static final double MIN_PRECIO = 100.0;
    public static final double MAX_PRECIO = 1000000.0;
}