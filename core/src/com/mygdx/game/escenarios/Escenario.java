package com.mygdx.game.escenarios;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.recursos.GestorRecursos;

/**
 * Patron Template Method: render() es el metodo plantilla (final) que fija el
 * orden de los pasos. Las subclases solo personalizan los pasos "gancho"
 * (prepararRender / finalizarRender) y el nombre.
 */
public abstract class Escenario {
    private static final int CARRILES = 3;

    private final Texture fondo;

    protected Escenario(String rutaFondo) {
        fondo = GestorRecursos.getInstancia().getTextura(rutaFondo);
    }

    // Metodo plantilla: el algoritmo es siempre el mismo.
    public final void render(SpriteBatch batch, float ancho, float alto) {
        prepararRender(batch);
        dibujarFondo(batch, ancho, alto);
        finalizarRender(batch);
    }

    // Paso comun a todos los escenarios.
    private void dibujarFondo(SpriteBatch batch, float ancho, float alto) {
        float anchoCarril = ancho / CARRILES;

        for (int carril = 0; carril < CARRILES; carril++) {
            batch.draw(fondo, carril * anchoCarril, 0, anchoCarril, alto);
        }
    }

    // Ganchos: por defecto no hacen nada.
    protected void prepararRender(SpriteBatch batch) {
    }

    protected void finalizarRender(SpriteBatch batch) {
    }

    public abstract String getNombre();
}