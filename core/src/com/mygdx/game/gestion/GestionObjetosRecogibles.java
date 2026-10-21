package com.mygdx.game.gestion;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.jugador.Player;
import com.mygdx.game.recogibles.Monedas;
import com.mygdx.game.recogibles.ObjetoRecogible;
import com.mygdx.game.recogibles.TransicionRecogible;

public class GestionObjetosRecogibles {
    private Array<ObjetoRecogible> objetos;
    private Array<TransicionRecogible> transiciones;

    private Texture texturaBuena;
    private Texture texturaMala;
    private Texture texturaEscalera;
    private Texture texturaTrampolin;

    private Sound sonido;
    private Music musica;

    private float tiempoGeneracion;
    private float tiempoTransicion;
    private float tiempoEntreObjetos = 0.8f;
    private static final float INTERVALO_TRANSICION = 30f;
    private float velocidad = 350;

    private static final float ANCHO_MUNDO = 1920;
    private static final float ALTO_MUNDO = 1080;
    private static final float ANCHO_OBJETO = 64;

    public GestionObjetosRecogibles(Texture texturaBuena,Texture texturaMala,Sound sonido,Music musica) {
        this.texturaBuena = texturaBuena;
        this.texturaMala = texturaMala;
        this.sonido = sonido;
        this.musica = musica;

        texturaEscalera = new Texture("escalera.png");
        texturaTrampolin = new Texture("trampolin.jpg");

        objetos = new Array<ObjetoRecogible>();
        transiciones = new Array<TransicionRecogible>();
    }

    public void crear() {
        crearMoneda();
        musica.setLooping(true);
        musica.play();
    }

    private void crearMoneda() {
        int carril = MathUtils.random(0,2);
        float anchoCarril = ANCHO_MUNDO / 3;
        float x = carril * anchoCarril + (anchoCarril - ANCHO_OBJETO) / 2;
        boolean mala = MathUtils.random(0,1) == 1;

        objetos.add(new Monedas(texturaBuena,texturaMala,sonido,mala,x,ALTO_MUNDO,velocidad));
        tiempoGeneracion = 0;
    }

    private void crearTransicion() {
        int carril = MathUtils.random(0,2);
        float anchoCarril = ANCHO_MUNDO / 3;
        float x = carril * anchoCarril + (anchoCarril - ANCHO_OBJETO) / 2;

        boolean esEscalera = MathUtils.randomBoolean();

        if (esEscalera) {
            transiciones.add(new TransicionRecogible(texturaEscalera,x,ALTO_MUNDO,ANCHO_OBJETO,100,velocidad,"CIUDAD"));
        } else {
            transiciones.add(new TransicionRecogible(texturaTrampolin,x,ALTO_MUNDO,ANCHO_OBJETO,100,velocidad,"AEREO"));
        }

        tiempoTransicion = 0;
    }

    public void agregar(ObjetoRecogible objeto) {
        if (objeto != null) {
            objetos.add(objeto);
        }
    }

    public String actualizar(float delta,Player player) {
        if (player.getVidas() <= 0) {
            return null;
        }

        tiempoGeneracion += delta;
        tiempoTransicion += delta;

        if (tiempoGeneracion >= tiempoEntreObjetos) {
            crearMoneda();
        }

        if (tiempoTransicion >= INTERVALO_TRANSICION && transiciones.size == 0) {
            crearTransicion();
        }

        for (int i = objetos.size - 1; i >= 0; i--) {
            ObjetoRecogible objeto = objetos.get(i);
            objeto.actualizar(delta);

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

    public void destruir() {
        for (ObjetoRecogible objeto : objetos) {
            objeto.destruir();
        }

        objetos.clear();

        transiciones.clear();

        musica.stop();
        musica.dispose();
        sonido.dispose();
        texturaBuena.dispose();
        texturaMala.dispose();
        texturaEscalera.dispose();
        texturaTrampolin.dispose();
    }
}