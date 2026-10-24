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
        if(player == null || player.getVidas() <= 0 || player.estaHerido() || player.estaSaltando()) return;

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

    public boolean carrilOcupadoArriba(int carril, float margenY) {
        float anchoCarril = 1280f / 3f;
        float limiteY = 720f - margenY;
        float xMin = carril * anchoCarril;
        float xMax = (carril + 1) * anchoCarril;

        for (EntidadMovil entidad : entidades) {
            Rectangle area = entidad.getArea();
            if (area.x < xMax && (area.x + area.width) > xMin && (area.y + area.height) >= limiteY) {
                return true;
            }
        }
        return false;
    }

    /**
     * Garantia de juego justo: verifica si colocar un obstaculo en el carril propuesto
     * bloquearia simultaneamente los 3 carriles en la misma franja horizontal Y,
     * dejando al jugador sin una via de escape o espacio para esquivar.
     */
    public boolean creariaBloqueoTotal(int carrilPropuesto, float yPropuesta, float altoPropuesto) {
        float margenSeguridad = 100f; // Espacio vertical minimo para que el jugador pueda maniobrar entre carriles
        float yMinPropuesta = yPropuesta - margenSeguridad;
        float yMaxPropuesta = yPropuesta + altoPropuesto + margenSeguridad;

        float anchoCarril = 1280f / 3f;

        boolean[] carrilBloqueadoEnRango = new boolean[3];
        carrilBloqueadoEnRango[carrilPropuesto] = true;

        for (EntidadMovil entidad : entidades) {
            Rectangle area = entidad.getArea();
            int carrilEntidad = (int) ((area.x + area.width / 2f) / anchoCarril);
            if (carrilEntidad >= 0 && carrilEntidad < 3 && carrilEntidad != carrilPropuesto) {
                float yMinEntidad = area.y;
                float yMaxEntidad = area.y + area.height;
                if (yMinEntidad < yMaxPropuesta && yMaxEntidad > yMinPropuesta) {
                    carrilBloqueadoEnRango[carrilEntidad] = true;
                }
            }
        }

        // Bloqueo injusto si se bloquean carriles adyacentes a la vez (0 con 1, o 1 con 2, o los 3),
        // lo que impediria al jugador maniobrar hacia un carril contiguo libre.
        return (carrilBloqueadoEnRango[0] && carrilBloqueadoEnRango[1])
            || (carrilBloqueadoEnRango[1] && carrilBloqueadoEnRango[2]);
    }
}