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

public class GestionObjetosRecogibles {
    private Array<ObjetoRecogible> objetos;
    private Texture texturaBuena;
    private Texture texturaMala;
    private Sound sonido;
    private Music musica;

    private float tiempoGeneracion;
    private float tiempoEntreObjetos = 0.8f;
    private float velocidad = 350;

    private static final float ANCHO_MUNDO = 1920;
    private static final float ALTO_MUNDO = 1080;
    private static final float ANCHO_OBJETO = 64;

    public GestionObjetosRecogibles(Texture texturaBuena, Texture texturaMala, Sound sonido, Music musica) {
        this.texturaBuena = texturaBuena;
        this.texturaMala = texturaMala;
        this.sonido = sonido;
        this.musica = musica;
        objetos = new Array<ObjetoRecogible>();
    }

    public void crear() {
        crearMoneda();

        musica.setLooping(true);
        musica.play();
    }

    private void crearMoneda() {
        int carril = MathUtils.random(0, 2);
        float anchoCarril = ANCHO_MUNDO / 3;
        float x = carril * anchoCarril + (anchoCarril - ANCHO_OBJETO) / 2;
        boolean mala = MathUtils.random(0, 1) == 1;

        objetos.add(new Monedas(texturaBuena, texturaMala, sonido, mala, x, ALTO_MUNDO, velocidad));
        tiempoGeneracion = 0;
    }

    public void agregar(ObjetoRecogible objeto) {
        if (objeto != null) {
            objetos.add(objeto);
        }
    }

    public void actualizar(float delta, Player tarro) {
        if (tarro.getVidas() <= 0) {
            return;
        }

        tiempoGeneracion += delta;

        if (tiempoGeneracion >= tiempoEntreObjetos) {
            crearMoneda();
        }

        for (int i = objetos.size - 1; i >= 0; i--) {
            ObjetoRecogible objeto = objetos.get(i);
            objeto.actualizar(delta);

            if (objeto.getArea().overlaps(tarro.getArea())) {
                objeto.recoger(tarro);
                objeto.destruir();
                objetos.removeIndex(i);
            } else if (objeto.estaFueraPantalla()) {
                objeto.destruir();
                objetos.removeIndex(i);
            }
        }
    }

    public void dibujar(SpriteBatch batch) {
        for (ObjetoRecogible objeto : objetos) {
            objeto.dibujar(batch);
        }
    }

    public void destruir() {
        for (ObjetoRecogible objeto : objetos) {
            objeto.destruir();
        }

        objetos.clear();
        musica.stop();
        musica.dispose();
        sonido.dispose();
        texturaBuena.dispose();
        texturaMala.dispose();
    }
}