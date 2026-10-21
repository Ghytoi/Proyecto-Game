package com.mygdx.game.gestion;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.entidades.EntidadMovil;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GestionEntidadMovil {
    private Array<EntidadMovil> entidades;

    public GestionEntidadMovil() {
        entidades = new Array<EntidadMovil>();
    }

    public void agregar(EntidadMovil entidad) {
        if (entidad != null) {
            entidades.add(entidad);
        }
    }

    public void actualizar(float delta) {
        for (int i = entidades.size - 1; i >= 0; i--) {
            EntidadMovil entidad = entidades.get(i);
            entidad.actualizar(delta);

            if (entidad.getArea().y + entidad.getArea().height < 0) {
                entidad.destruir();
                entidades.removeIndex(i);
            }
        }
    }

    public void dibujar(SpriteBatch batch) {
        for (EntidadMovil entidad : entidades) {
            entidad.dibujar(batch);
        }
    }

    public void destruir() {
        for (EntidadMovil entidad : entidades) {
            entidad.destruir();
        }

        entidades.clear();
    }
}