package com.mygdx.game.jugador;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.recogibles.PowerUp;

// La textura y el sonido son compartidos: los libera GestorRecursos.
public class Player {
    private static final int VIDAS_INICIALES = 3;   // para pruebas puedes subirlo (ej. 9999)
    private static final float DURACION_INVULNERABILIDAD = 1.5f;   // segundos tras recibir dano
    private static final int MONEDAS_POR_VIDA = 100;
    private static final float ANCHO_MUNDO = 1280;
    private static final float ANCHO_JUGADOR = 96;
    private static final float ALTO_JUGADOR = 96;
    private static final float ANCHO_CARRIL = ANCHO_MUNDO / 3f;
    private static final float DURACION_FRAME_JUGADOR = 0.11f;
    private static final float DURACION_SALTO = 0.7f;

    private Rectangle bucket;
    private final Texture bucketImage;
    private final TextureRegion[] frames;
    private float tiempoAnimacion = 0;
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
    private boolean saltando = false;
    private boolean arribaPresionada = false;
    private boolean izquierdaPresionada = false;
    private boolean derechaPresionada = false;
    private float tiempoHerido = 0;
    private float tiempoEscudo = 0;
    private float tiempoIman = 0;
    private float tiempoEstrella = 0;
    private float tiempoSalto = 0;
    private float tiempoPuntaje = 0;
    private float factorVelocidad = 1.0f;
    private Texture texturaGlow;

    public Player(Texture tex, Sound ss) {
        bucketImage = tex;
        sonidoHerido = ss;

        if (tex != null && tex.getWidth() > tex.getHeight()) {
            int alto = tex.getHeight();
            int columnas = tex.getWidth() / alto;
            TextureRegion[][] tmp = TextureRegion.split(tex, alto, alto);
            frames = new TextureRegion[columnas];
            for (int i = 0; i < columnas; i++) {
                frames[i] = tmp[0][i];
            }
        } else if (tex != null) {
            frames = new TextureRegion[] { new TextureRegion(tex) };
        } else {
            frames = null;
        }
    }

    public void crear() {
        bucket = new Rectangle();
        bucket.width = ANCHO_JUGADOR;
        bucket.height = ALTO_JUGADOR;
        actualizarCarril();
        bucket.y = 20;

        if (texturaGlow == null) {
            texturaGlow = generarTexturaGlow(64);
        }
    }

    private Texture generarTexturaGlow(int tamano) {
        Pixmap pixmap = new Pixmap(tamano, tamano, Pixmap.Format.RGBA8888);
        float centro = tamano / 2f;
        float radioMax = tamano / 2f;

        for (int y = 0; y < tamano; y++) {
            for (int x = 0; x < tamano; x++) {
                float dx = x - centro;
                float dy = y - centro;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist <= radioMax) {
                    float factor = 1f - (dist / radioMax);
                    // Curva suave cubica para iluminacion radial
                    float alfa = factor * factor * factor;
                    pixmap.setColor(1f, 1f, 1f, alfa);
                    pixmap.drawPixel(x, y);
                } else {
                    pixmap.setColor(0, 0, 0, 0);
                    pixmap.drawPixel(x, y);
                }
            }
        }
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    public void destruir() {
        if (texturaGlow != null) {
            texturaGlow.dispose();
            texturaGlow = null;
        }
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
        saltando = false;
        arribaPresionada = false;
        tiempoHerido = 0;
        tiempoEscudo = 0;
        tiempoIman = 0;
        tiempoEstrella = 0;
        tiempoSalto = 0;
        tiempoPuntaje = 0;
        factorVelocidad = 1.0f;
        actualizarCarril();
    }

    public void actualizarMovimiento() {
        boolean izquierda = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean derecha = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        boolean arriba = Gdx.input.isKeyPressed(Input.Keys.UP)
                      || Gdx.input.isKeyPressed(Input.Keys.W)
                      || Gdx.input.isKeyPressed(Input.Keys.SPACE);

        if (izquierda && !izquierdaPresionada && carrilActual > 0) {
            carrilActual--;
            actualizarCarril();
        }

        if (derecha && !derechaPresionada && carrilActual < 2) {
            carrilActual++;
            actualizarCarril();
        }

        if (arriba && !arribaPresionada && !saltando) {
            saltando = true;
            tiempoSalto = 0f;
        }

        izquierdaPresionada = izquierda;
        derechaPresionada = derecha;
        arribaPresionada = arriba;
    }

    private void actualizarCarril() {
        if (bucket != null) {
            bucket.x = carrilActual * ANCHO_CARRIL + (ANCHO_CARRIL - ANCHO_JUGADOR) / 2f;
        }
    }

    public void actualizarEstado() {
        float delta = Gdx.graphics.getDeltaTime();
        tiempoAnimacion += delta * factorVelocidad;

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

        if (saltando) {
            tiempoSalto += delta;
            if (tiempoSalto >= DURACION_SALTO) {
                saltando = false;
                tiempoSalto = 0;
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

            float offsetSaltoY = 0f;
            float escalaSalto = 1f;

            if (saltando) {
                float progreso = tiempoSalto / DURACION_SALTO;
                float curva = MathUtils.sin(progreso * MathUtils.PI); // de 0 a 1 y vuelve a 0
                offsetSaltoY = curva * 55f; // Elevacion en el aire
                escalaSalto = 1f + curva * 0.22f; // Efecto de escala pseudo-3D hacia la camara
            }

            float ancho = bucket.width * escalaSalto;
            float alto = bucket.height * escalaSalto;
            float xDibujo = bucket.x - (ancho - bucket.width) / 2f;
            float yDibujo = bucket.y + offsetSaltoY;

            // 1. EFECTO GLOW ALREDEDOR DEL PERSONAJE (Mezcla aditiva para iluminar el fondo)
            if (texturaGlow != null) {
                batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
                float pulso = 0.82f + 0.18f * MathUtils.sin(tiempoAnimacion * 4.5f);

                // El color del glow acompana los powerups o brilla en un cian estelar vibrante
                if (estrellaActiva) {
                    batch.setColor(1f, 0.9f, 0.2f, 0.8f * pulso); // Aura dorada
                } else if (escudoActivo) {
                    batch.setColor(0.25f, 0.65f, 1f, 0.85f * pulso); // Aura protectora azul
                } else if (imanActivo) {
                    batch.setColor(1f, 0.28f, 0.28f, 0.8f * pulso); // Aura roja electromagnetica
                } else {
                    batch.setColor(0.35f, 0.85f, 1f, 0.55f * pulso); // Aura cian luminosa base
                }

                float margenGlow = 26f * escalaSalto;
                batch.draw(texturaGlow, xDibujo - margenGlow, yDibujo - margenGlow, ancho + margenGlow * 2f, alto + margenGlow * 2f);

                // Restaurar mezcla estandar
                batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            }

            // 2. EFECTO DE PARPADEO MIENTRAS ESTA SALTANDO (Strobe a 16 Hz)
            if (saltando) {
                boolean faseParpadeo = ((int) (tiempoSalto * 16)) % 2 == 0;
                batch.setColor(1f, 1f, 1f, faseParpadeo ? 0.35f : 1.0f);
            } else {
                batch.setColor(Color.WHITE);
            }

            // 3. DIBUJAR SPRITE DEL PERSONAJE
            if (frames != null && frames.length > 0) {
                int frameActual = (int) (tiempoAnimacion / DURACION_FRAME_JUGADOR) % frames.length;
                batch.draw(frames[frameActual], xDibujo, yDibujo, ancho, alto);
            } else {
                batch.draw(bucketImage, xDibujo, yDibujo, ancho, alto);
            }

            // Restaurar color blanco estandar para no afectar otros elementos
            batch.setColor(Color.WHITE);
        }
    }

    public boolean estaSaltando() {
        return saltando;
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

    public void setFactorVelocidad(float factor) {
        this.factorVelocidad = Math.max(1.0f, factor);
    }

    public float getFactorVelocidad() {
        return factorVelocidad;
    }
}