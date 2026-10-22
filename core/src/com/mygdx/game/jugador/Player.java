package com.mygdx.game.jugador;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.recogibles.PowerUp;

// La textura y el sonido son compartidos: los libera GestorRecursos.
public class Player {
    private static final int VIDAS_INICIALES = 3;   // para pruebas puedes subirlo (ej. 9999)
    private static final float DURACION_INVULNERABILIDAD = 1.5f;   // segundos tras recibir dano
    private static final int MONEDAS_POR_VIDA = 100;
    private static final float ANCHO_MUNDO = 1280;
    private static final float ANCHO_JUGADOR = 64;
    private static final float ALTO_JUGADOR = 64;
    private static final float ANCHO_CARRIL = ANCHO_MUNDO / 3f;

    private Rectangle bucket;
    private final Texture bucketImage;
    private final Sound sonidoHerido;
    private int vidas = VIDAS_INICIALES;
    private int puntos = 0;
    private int carrilActual = 1;
    private int monedas = 0;
    private int multiplicador = 1;
    private boolean herido = false;
    private boolean escudoActivo = false;
    private boolean imanActivo = false;
    private boolean estrellaActiva = false;
    private boolean izquierdaPresionada = false;
    private boolean derechaPresionada = false;
    private float tiempoHerido = 0;
    private float tiempoEscudo = 0;
    private float tiempoIman = 0;
    private float tiempoEstrella = 0;
    private float tiempoPuntaje = 0;

    public Player(Texture tex, Sound ss) {
        bucketImage = tex;
        sonidoHerido = ss;
    }

    public void crear() {
        bucket = new Rectangle();
        bucket.width = ANCHO_JUGADOR;
        bucket.height = ALTO_JUGADOR;
        actualizarCarril();
        bucket.y = 20;
    }

    public void reiniciar() {
        vidas = VIDAS_INICIALES;
        puntos = 0;
        monedas = 0;
        multiplicador = 1;
        carrilActual = 1;
        herido = false;
        escudoActivo = false;
        imanActivo = false;
        estrellaActiva = false;
        tiempoHerido = 0;
        tiempoEscudo = 0;
        tiempoIman = 0;
        tiempoEstrella = 0;
        tiempoPuntaje = 0;
        actualizarCarril();
    }

    public void actualizarMovimiento() {
        boolean izquierda = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean derecha = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);

        if (izquierda && !izquierdaPresionada && carrilActual > 0) {
            carrilActual--;
            actualizarCarril();
        }

        if (derecha && !derechaPresionada && carrilActual < 2) {
            carrilActual++;
            actualizarCarril();
        }

        izquierdaPresionada = izquierda;
        derechaPresionada = derecha;
    }

    private void actualizarCarril() {
        if (bucket != null) {
            bucket.x = carrilActual * ANCHO_CARRIL + (ANCHO_CARRIL - ANCHO_JUGADOR) / 2f;
        }
    }

    public void actualizarEstado() {
        float delta = Gdx.graphics.getDeltaTime();

        tiempoPuntaje += delta;
        while (tiempoPuntaje >= 1f) {
            puntos += multiplicador;
            tiempoPuntaje -= 1f;
        }

        if (herido) {
            tiempoHerido += delta;
            if (tiempoHerido >= DURACION_INVULNERABILIDAD) {
                herido = false;
                tiempoHerido = 0;
            }
        }

        if (escudoActivo) {
            tiempoEscudo -= delta;
            if (tiempoEscudo <= 0) {
                escudoActivo = false;
                tiempoEscudo = 0;
            }
        }

        if (imanActivo) {
            tiempoIman -= delta;
            if (tiempoIman <= 0) {
                imanActivo = false;
                tiempoIman = 0;
            }
        }

        if (estrellaActiva) {
            tiempoEstrella -= delta;
            if (tiempoEstrella <= 0) {
                estrellaActiva = false;
                multiplicador = 1;
                tiempoEstrella = 0;
            }
        }
    }

    public float getTiempoEscudo() {
        return tiempoEscudo;
    }

    public boolean tieneIman() {
        return imanActivo;
    }

    public float getTiempoIman() {
        return tiempoIman;
    }

    public float getTiempoEstrella() {
        return tiempoEstrella;
    }

    public void sumarPuntos(int cantidad) {
        puntos += cantidad * multiplicador;
    }

    public void sumarMoneda() {
        monedas++;

        if (monedas >= MONEDAS_POR_VIDA) {
            monedas -= MONEDAS_POR_VIDA;
            vidas++;
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

    public void activarEstrella(int multiplicador) {
        estrellaActiva = true;
        this.multiplicador = multiplicador;
        tiempoEstrella = PowerUp.getDuracion(PowerUp.Tipo.ESTRELLA);
    }

    public void danar() {
        if (escudoActivo || herido || vidas <= 0) {
            return;
        }

        vidas--;
        herido = true;
        tiempoHerido = 0;

        if (sonidoHerido != null) {
            sonidoHerido.play();
        }
    }

    public boolean estaAtrayendoMonedas() {
        return imanActivo;
    }

    public boolean atraeMonedas(float x, float y) {
        if (!imanActivo || bucket == null) {
            return false;
        }

        float centroX = bucket.x + bucket.width / 2f;
        float centroY = bucket.y + bucket.height / 2f;
        float dx = x - centroX;
        float dy = y - centroY;

        return dx * dx + dy * dy <= 500f * 500f;
    }

    public void dibujar(SpriteBatch batch) {
        if (bucketImage != null && bucket != null) {
            // Parpadeo mientras es invulnerable (se oculta en intervalos de 0.1 s).
            if (herido && ((int) (tiempoHerido * 10)) % 2 == 0) {
                return;
            }
            batch.draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height);
        }
    }

    public Rectangle getArea() {
        return new Rectangle(bucket);
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

    public int getMultiplicador() {
        return multiplicador;
    }

    public int getCarrilActual() {
        return carrilActual;
    }

    public boolean estaHerido() {
        return herido;
    }

    public boolean tieneEscudo() {
        return escudoActivo;
    }

    public boolean tieneEstrella() {
        return estrellaActiva;
    }
}