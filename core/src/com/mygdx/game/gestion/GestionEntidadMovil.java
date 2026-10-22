package com.mygdx.game.gestion;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.entidades.EntidadMovil;
import com.mygdx.game.escenarios.TipoEscenario;
import com.mygdx.game.jugador.Player;

public class GestionEntidadMovil {
    private final Array<EntidadMovil> entidades;

    public GestionEntidadMovil() {
        entidades=new Array<EntidadMovil>();
    }

    public void agregar(EntidadMovil entidad) {
        if(entidad!=null) entidades.add(entidad);
    }

    public void actualizar(float delta) {
        for(int i=entidades.size-1;i>=0;i--) {
            EntidadMovil entidad=entidades.get(i);
            entidad.actualizar(delta);

            Rectangle area=entidad.getArea();

            if(area.y+area.height<0) {
                entidad.destruir();
                entidades.removeIndex(i);
            }
        }
    }

    public void comprobarColisiones(Player player) {
        if(player == null || player.getVidas() <= 0 || player.estaHerido()) return;

        for(EntidadMovil entidad : entidades) {
            if(entidad.colisionaCon(player.getArea())) {
                player.danar();
                break;
            }
        }
    }

    public void dibujar(SpriteBatch batch) {
        for(EntidadMovil entidad:entidades) {
            entidad.dibujar(batch);
        }
    }

    public void reiniciar() {
        for(EntidadMovil entidad:entidades) {
            entidad.destruir();
        }

        entidades.clear();
    }

    public void filtrarPorEscenario(TipoEscenario escenario) {
        for(int i=entidades.size-1;i>=0;i--) {
            EntidadMovil entidad=entidades.get(i);

            if(!entidad.perteneceAEscenario(escenario)) {
                entidad.destruir();
                entidades.removeIndex(i);
            }
        }
    }

    public void destruir() {
        reiniciar();
    }
}