package com.mygdx.game.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.recursos.GestorRecursos;

/**
 * Patron Builder: construye un obstaculo paso a paso (tipo, carril, velocidad)
 * y se encarga de elegir la clase concreta, la textura, el tamano y la posicion X.
 *
 * Uso: new ObstaculoBuilder().tipo(TipoObstaculo.VAGON).carril(1).velocidad(250f).construir();
 */
public class ObstaculoBuilder {
    private static final float ANCHO_MUNDO = 1280f;
    private static final float ALTO_MUNDO = 720f;
    private static final int CARRILES = 3;

    private TipoObstaculo tipo;
    private int carril = 1;
    private float velocidad = 250f;

    public ObstaculoBuilder tipo(TipoObstaculo tipo) {
        this.tipo = tipo;
        return this;
    }

    public ObstaculoBuilder carril(int carril) {
        if (carril < 0 || carril >= CARRILES) {
            throw new IllegalArgumentException("El carril debe estar entre 0 y " + (CARRILES - 1));
        }
        this.carril = carril;
        return this;
    }

    public ObstaculoBuilder velocidad(float velocidad) {
        if (velocidad < 0) {
            throw new IllegalArgumentException("La velocidad no puede ser negativa");
        }
        this.velocidad = velocidad;
        return this;
    }

    public EntidadMovil construir() {
        if (tipo == null) {
            throw new IllegalStateException("Falta indicar el tipo de obstaculo");
        }

        Texture textura = GestorRecursos.getInstancia().getTextura(tipo.getRutaTextura());
        float ancho = tipo.getAncho();
        float alto = tipo.getAlto();
        float anchoCarril = ANCHO_MUNDO / CARRILES;
        float x = carril * anchoCarril + (anchoCarril - ancho) / 2f;

        switch (tipo) {
            case VAGON:
                return new VagonesDeTrenes(textura, x, ALTO_MUNDO, ancho, alto, velocidad);
            case AUTO:
                return new Auto(textura, x, ALTO_MUNDO, ancho, alto, velocidad);
            case AUTOBUS:
                return new AutoBus(textura, x, ALTO_MUNDO, ancho, alto, velocidad);
            case AVION:
                return new Avion(textura, x, ALTO_MUNDO, ancho, alto, velocidad);
            case PAJAROS:
                return new BandadaDePajaros(textura, x, ALTO_MUNDO, ancho, alto, velocidad);
            default:
                throw new IllegalStateException("Tipo de obstaculo no soportado: " + tipo);
        }
    }
}