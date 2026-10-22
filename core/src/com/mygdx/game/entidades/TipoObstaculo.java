package com.mygdx.game.entidades;

/** Catalogo de obstaculos: textura y tamano de cada uno. */
public enum TipoObstaculo {
    VAGON("vagon.jpg", 100f, 180f),
    AUTO("auto.jpg", 100f, 100f),
    AUTOBUS("autobus.jpg", 100f, 100f),
    AVION("avion.jpg", 100f, 100f),
    PAJAROS("pajaros.jpg", 100f, 100f);

    private final String rutaTextura;
    private final float ancho;
    private final float alto;

    TipoObstaculo(String rutaTextura, float ancho, float alto) {
        this.rutaTextura = rutaTextura;
        this.ancho = ancho;
        this.alto = alto;
    }

    public String getRutaTextura() {
        return rutaTextura;
    }

    public float getAncho() {
        return ancho;
    }

    public float getAlto() {
        return alto;
    }
}