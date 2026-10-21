
package com.mygdx.game.jugador;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.recogibles.PowerUp;

public class Player {
    private Rectangle bucket;
    private Texture bucketImage;
    private Sound sonidoHerido;

    private int vidas = 2;
    private int puntos = 0;
    private int carrilActual = 1;
    private int monedas = 0;
    private int multiplicador = 1;

    private boolean herido = false;
    private boolean escudoActivo = false;
    private boolean imanActivo = false;
    private boolean estrellaActiva = false;

    private boolean teclaIzquierdaAnterior = false;
    private boolean teclaDerechaAnterior = false;

    private float tiempoHerido;
    private float tiempoHeridoMax = 0.3f;
    private float tiempoEscudo;
    private float tiempoIman;
    private float tiempoEstrella;

    private static final int MONEDAS_POR_VIDA = 50;

    private static final float ANCHO_MUNDO = 1280;
    private static final float ANCHO_TARRO = 64;
    private static final float ALTO_TARRO = 64;
    private static final float ANCHO_CARRIL = ANCHO_MUNDO / 3;

    public Player(Texture tex,Sound ss) {
        bucketImage = tex;
        sonidoHerido = ss;
    }

    public int getVidas() {
        return vidas;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getMonedas() {
        return monedas;
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

    public boolean tieneEscudo() {
        return escudoActivo;
    }

    public boolean tieneIman() {
        return imanActivo;
    }

    public boolean tieneEstrella() {
        return estrellaActiva;
    }

    public int getMultiplicador() {
        return multiplicador;
    }

    public void sumarPuntos(int cantidad) {
        puntos += cantidad * multiplicador;
    }

    public void sumarMoneda() {
        monedas++;
        sumarPuntos(1);

        if (monedas % MONEDAS_POR_VIDA == 0) {
            vidas++;
            Gdx.app.log("¡Vida extra! Vidas: " + vidas, " | Monedas: " + monedas);
        }
    }

    public void activarEscudo() {
        escudoActivo = true;
        tiempoEscudo = PowerUp.getDuracion(PowerUp.Tipo.ESCUDO);
    }

    public void activarIman() {
        imanActivo = true;
        tiempoIman = PowerUp.getDuracion(PowerUp.Tipo.IMAN);
    }

    public void activarEstrella(int nuevoMultiplicador) {
        estrellaActiva = true;
        multiplicador = Math.max(1,nuevoMultiplicador);
        tiempoEstrella = PowerUp.getDuracion(PowerUp.Tipo.ESTRELLA);
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
        boolean izquierda = Gdx.input.isKeyPressed(Input.Keys.LEFT)
                || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean derecha = Gdx.input.isKeyPressed(Input.Keys.RIGHT)
                || Gdx.input.isKeyPressed(Input.Keys.D);

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
        if (!escudoActivo && !herido && vidas > 0) {
            vidas--;
            herido = true;
            tiempoHerido = tiempoHeridoMax;
            sonidoHerido.play();
        }
    }
    public boolean estaAtrayendoMonedas() {
        return imanActivo;
    }

    public void actualizarEstado() {
        float delta = Gdx.graphics.getDeltaTime();

        if (herido) {
            tiempoHerido -= delta;

            if (tiempoHerido <= 0) {
                herido = false;
            }
        }

        if (escudoActivo) {
            tiempoEscudo -= delta;

            if (tiempoEscudo <= 0) {
                escudoActivo = false;
            }
        }

        if (imanActivo) {
            tiempoIman -= delta;

            if (tiempoIman <= 0) {
                imanActivo = false;
            }
        }

        if (estrellaActiva) {
            tiempoEstrella -= delta;

            if (tiempoEstrella <= 0) {
            	tiempoEstrella = 0;
                estrellaActiva = false;
                multiplicador = 1;
            }
        }
    }
    
    public boolean atraeMonedas(float x,float y) {
        if (!imanActivo) {
            return false;
        }

        float centroX = bucket.x + bucket.width / 2;
        float centroY = bucket.y + bucket.height / 2;
        float distanciaX = centroX - x;
        float distanciaY = centroY - y;

        return distanciaX * distanciaX + distanciaY * distanciaY <= 500 * 500;
    }

    public void dibujar(SpriteBatch batch) {
        if (!herido) {
            batch.draw(bucketImage,bucket.x,bucket.y);
        } else {
            batch.draw(bucketImage,bucket.x,bucket.y + MathUtils.random(-5,5));
        }
    }
    
    public float getTiempoEscudo() {
        return Math.max(0,tiempoEscudo);
    }

    public float getTiempoIman() {
        return Math.max(0,tiempoIman);
    }

    public float getTiempoEstrella() {
        return Math.max(0,tiempoEstrella);
    }

    public void destruir() {
        bucketImage.dispose();
        sonidoHerido.dispose();
    }
}