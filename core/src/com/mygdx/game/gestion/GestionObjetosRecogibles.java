package com.mygdx.game.gestion;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.jugador.Player;
import com.mygdx.game.recogibles.Monedas;
import com.mygdx.game.recogibles.ObjetoRecogible;
import com.mygdx.game.recogibles.PowerUp;
import com.mygdx.game.recogibles.TransicionRecogible;
import com.mygdx.game.recursos.GestorRecursos;

public class GestionObjetosRecogibles {
    private static final float ANCHO_MUNDO = 1280;
    private static final float ALTO_MUNDO = 720;
    private static final float ANCHO_OBJETO = 64;
    private static final float INTERVALO_POWERUP = 15f;
    private static final float INTERVALO_TRANSICION = 5f;
    private static final float TIEMPO_ENTRE_OBJETOS = 0.8f;
    
    private final Array<ObjetoRecogible> objetos;
    private final Array<TransicionRecogible> transiciones;

    // Todas las texturas y sonidos son compartidos: los administra GestorRecursos.
    private final Texture texturaMoneda;
    private final Sound sonidoMoneda;
    private final Texture portalVias;
    private final Texture portalCiudad;
    private final Texture portalTunel;
    private final Texture portalAereo;
    private final Texture texturaEscudo;
    private final Texture texturaIman;
    private final Texture texturaEstrella;

    private final GestionEscenario gestionEscenario;
    private final float velocidad;

    private float tiempoGeneracion;
    private float tiempoTransicion;
    private float tiempoPowerUp;

    public GestionObjetosRecogibles(GestionEscenario gestionEscenario, float velocidad) {
        this.gestionEscenario = gestionEscenario;
        this.velocidad = velocidad;

        GestorRecursos recursos = GestorRecursos.getInstancia();
        texturaMoneda = recursos.getTextura("moneda.jpg");
        sonidoMoneda = recursos.getSonido("drop.wav");

        portalVias = recursos.getTextura("portal_vias.png");
        portalCiudad = recursos.getTextura("portal_ciudad.jpg");
        portalTunel = recursos.getTextura("portal_tunel.jpg");
        portalAereo = recursos.getTextura("portal_aereo.png");

        texturaEscudo = recursos.getTextura("escudo.jpg");
        texturaIman = recursos.getTextura("iman.png");
        texturaEstrella = recursos.getTextura("estrella.png");

        objetos = new Array<ObjetoRecogible>();
        transiciones = new Array<TransicionRecogible>();
    }

    public void crear() {
        crearMoneda();
    }

    private float calcularXCarril(int carril) {
        float anchoCarril = ANCHO_MUNDO / 3;
        return carril * anchoCarril + (anchoCarril - ANCHO_OBJETO) / 2;
    }

    private void crearMoneda() {
        int carril = MathUtils.random(0, 2);
        boolean mala = false;
        //Se está investigando como eliminar este atributo que no se utiliza "mala"

        objetos.add(new Monedas(texturaMoneda, sonidoMoneda, mala, calcularXCarril(carril), ALTO_MUNDO, velocidad));
        tiempoGeneracion = 0;
    }

    private Texture texturaDePortal(String destino) {
        if ("VIAS".equals(destino)) return portalVias;
        if ("CIUDAD".equals(destino)) return portalCiudad;
        if ("TUNEL".equals(destino)) return portalTunel;
        if ("AEREO".equals(destino)) return portalAereo;
        return null;
    }

    private void crearTransicion(String escenarioActual) {
        int carril = MathUtils.random(0, 2);

        Array<String> destinos = new Array<String>();
        destinos.add("VIAS");
        destinos.add("CIUDAD");
        destinos.add("TUNEL");
        destinos.add("AEREO");

        for (int i = destinos.size - 1; i >= 0; i--) {
            if (destinos.get(i).equals(escenarioActual)) {
                destinos.removeIndex(i);
            }
        }

        String destino = destinos.random();
        Texture texturaPortal = texturaDePortal(destino);

        if (texturaPortal == null) {
            return;
        }

        transiciones.add(new TransicionRecogible(texturaPortal, calcularXCarril(carril), ALTO_MUNDO, ANCHO_OBJETO, 100, velocidad, destino));
        tiempoTransicion = 0;
    }

    public void agregar(ObjetoRecogible objeto) {
        if (objeto != null) {
            objetos.add(objeto);
        }
    }

    public String actualizar(float delta, Player player) {
        if (player.getVidas() <= 0) {
            return null;
        }

        tiempoGeneracion += delta;
        tiempoTransicion += delta;
        tiempoPowerUp += delta;

        if (tiempoPowerUp >= INTERVALO_POWERUP) {
            crearPowerUp();
        }

        if (tiempoGeneracion >= TIEMPO_ENTRE_OBJETOS) {
            crearMoneda();
        }

        if (tiempoTransicion >= INTERVALO_TRANSICION && transiciones.size == 0) {
            crearTransicion(gestionEscenario.getNombreEscenarioActual());
        }

        for (int i = objetos.size - 1; i >= 0; i--) {
            ObjetoRecogible objeto = objetos.get(i);
            objeto.actualizar(delta, player);

            if (objeto.getArea().overlaps(player.getArea())) {
                objeto.recoger(player);
                objeto.destruir();
                objetos.removeIndex(i);
            } else if (objeto.estaFueraPantalla()) {
                objeto.destruir();
                objetos.removeIndex(i);
            }
        }

        for (int i = transiciones.size - 1; i >= 0; i--) {
            TransicionRecogible transicion = transiciones.get(i);
            transicion.actualizar(delta);

            if (transicion.getArea().overlaps(player.getArea())) {
                String destino = transicion.getDestino();
                transiciones.removeIndex(i);
                return destino;
            } else if (transicion.estaFueraPantalla()) {
                transiciones.removeIndex(i);
                tiempoTransicion = 0;
            }
        }

        return null;
    }

    public void dibujar(SpriteBatch batch) {
        for (ObjetoRecogible objeto : objetos) {
            objeto.dibujar(batch);
        }

        for (TransicionRecogible transicion : transiciones) {
            transicion.dibujar(batch);
        }
    }

    private void crearPowerUp() {
        float x = calcularXCarril(MathUtils.random(0, 2));
        int tipo = MathUtils.random(0, 2);   // 0 escudo, 1 iman, 2 estrella (1/3 cada uno)
        PowerUp powerUp;

        if (tipo == 0) {
            powerUp = new PowerUp(texturaEscudo, PowerUp.Tipo.ESCUDO, 1, x, ALTO_MUNDO, velocidad);
        } else if (tipo == 1) {
            powerUp = new PowerUp(texturaIman, PowerUp.Tipo.IMAN, 1, x, ALTO_MUNDO, velocidad);
        } else {
            int[] multiplicadores = {2, 4, 8, 16};
            int multiplicador = multiplicadores[MathUtils.random(0, multiplicadores.length - 1)];
            powerUp = new PowerUp(texturaEstrella, PowerUp.Tipo.ESTRELLA, multiplicador, x, ALTO_MUNDO, velocidad);
        }

        objetos.add(powerUp);
        tiempoPowerUp = 0;
    }

    public void reiniciarObjetos() {
        for (ObjetoRecogible objeto : objetos) {
            objeto.destruir();
        }

        objetos.clear();
        transiciones.clear();

        tiempoGeneracion = 0;
        tiempoTransicion = 0;
        tiempoPowerUp = 0;
    }
}