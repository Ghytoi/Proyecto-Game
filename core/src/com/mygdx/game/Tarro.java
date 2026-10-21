
package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Tarro {
    private Rectangle bucket;
    private Texture bucketImage;
    private Sound sonidoHerido;

    private int vidas = 3;
    private int puntos = 0;
    private int carrilActual = 1;

    private boolean herido = false;
    private boolean teclaIzquierdaAnterior = false;
    private boolean teclaDerechaAnterior = false;

    private float tiempoHerido;
    private float tiempoHeridoMax = 0.3f;

    private static final float ANCHO_MUNDO = 1920;
    private static final float ANCHO_TARRO = 64;
    private static final float ALTO_TARRO = 64;
    private static final float ANCHO_CARRIL = ANCHO_MUNDO / 3;

    public Tarro(Texture tex, Sound ss) {
        bucketImage = tex;
        sonidoHerido = ss;
    }

    public int getVidas() {
        return vidas;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getCarrilActual() {
        return carrilActual;
    }

    public Rectangle getArea() {
        return bucket;
    }

    public boolean estaHerido() {
        return herido;
    }

    public void sumarPuntos(int pp) {
        puntos += pp;
    }

    public void crear() {
        bucket = new Rectangle();
        bucket.width = ANCHO_TARRO;
        bucket.height = ALTO_TARRO;
        bucket.y = 20;
        actualizarPosicionCarril();
    }

    private void actualizarPosicionCarril() {
        bucket.x = carrilActual * ANCHO_CARRIL
                + (ANCHO_CARRIL - bucket.width) / 2;
    }

    public void actualizarMovimiento() {
        boolean izquierda = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean derecha = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        if (izquierda && !teclaIzquierdaAnterior && carrilActual > 0) {
            carrilActual--;
            actualizarPosicionCarril();
        }

        if (derecha && !teclaDerechaAnterior && carrilActual < 2) {
            carrilActual++;
            actualizarPosicionCarril();
        }

        teclaIzquierdaAnterior = izquierda;
        teclaDerechaAnterior = derecha;
    }

    public void dañar() {
        if (!herido && vidas > 0) {
            vidas--;
            herido = true;
            tiempoHerido = tiempoHeridoMax;
            sonidoHerido.play();
        }
    }

    public void actualizarEstado() {
        if (herido) {
            tiempoHerido -= Gdx.graphics.getDeltaTime();

            if (tiempoHerido <= 0) {
                herido = false;
            }
        }
    }

    public void dibujar(SpriteBatch batch) {
        if (!herido) {
            batch.draw(bucketImage, bucket.x, bucket.y);
        } else {
            batch.draw(bucketImage, bucket.x,
                    bucket.y + MathUtils.random(-5, 5));
        }
    }

    public void destruir() {
        bucketImage.dispose();
        sonidoHerido.dispose();
    }
}