package com.mygdx.game.principal;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.entidades.ObstaculoBuilder;
import com.mygdx.game.entidades.TipoObstaculo;
import com.mygdx.game.gestion.GestionEntidadMovil;
import com.mygdx.game.gestion.GestionEscenario;
import com.mygdx.game.gestion.GestionObjetosRecogibles;
import com.mygdx.game.jugador.Player;
import com.mygdx.game.recursos.GestorRecursos;

public class SubwaySurfer extends ApplicationAdapter {
    private static final float ANCHO_MUNDO = 1280f;
    private static final float ALTO_MUNDO = 720f;
    private static final float VOLUMEN_MUSICA = 0.3f;
    // Dificultad dinamica progresiva segun el puntaje
    private static final float VELOCIDAD_BASE = 360f;
    private static final float VELOCIDAD_MAXIMA = 880f;
    private static final float INCREMENTO_POR_PUNTO = 5.2f;
    private static final float INTERVALO_MINIMO = 0.32f;
    private static final float INTERVALO_MAXIMO = 1.15f;
    private static final float DURACION_GLITCH = 0.40f;
    private static final int MAX_LINEAS_VELOCIDAD = 24;

    private float tiempoProximaEntidad = 0.1f;
    private float tiempoGlitch = 0f;
    private Texture texturaPixel;

    // Lineas de viento/velocidad dinamica
    private final float[] lineasX = new float[MAX_LINEAS_VELOCIDAD];
    private final float[] lineasY = new float[MAX_LINEAS_VELOCIDAD];
    private final float[] lineasLargo = new float[MAX_LINEAS_VELOCIDAD];
    private final float[] lineasVelocidad = new float[MAX_LINEAS_VELOCIDAD];
    private final float[] lineasAlpha = new float[MAX_LINEAS_VELOCIDAD];

    private int nivelDificultad = 1;
    private float tiempoAlertaNivel = 0f;
    private String textoAlertaNivel = "";
    private float tiempoJuego = 0f;
    private Texture texturaCorazon;
    private Texture texturaCorazonVacio;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private BitmapFont font;

    private Player player;
    private GestionEscenario gestionEscenario;
    private GestionEntidadMovil gestionEntidadMovil;
    private GestionObjetosRecogibles gestionObjetosRecogibles;
    private Music musicaFondo;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_MUNDO, ALTO_MUNDO, camera);
        viewport.apply();

        batch = new SpriteBatch();
        font = new BitmapFont();

        Pixmap px = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        px.setColor(Color.WHITE);
        px.fill();
        texturaPixel = new Texture(px);
        px.dispose();

        // Singleton: todos los recursos se piden a GestorRecursos.
        GestorRecursos recursos = GestorRecursos.getInstancia();

        texturaCorazon = recursos.getTextura("corazon.png");
        texturaCorazonVacio = recursos.getTextura("corazon_vacio.png");

        player = new Player(
            recursos.getTextura("player.jpg"),
            recursos.getSonido("hurt.ogg")
        );
        player.crear();

        musicaFondo = recursos.getMusica("principalTheme.mp3");
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(VOLUMEN_MUSICA);
        musicaFondo.play();

        gestionEscenario = new GestionEscenario();
        gestionEntidadMovil = new GestionEntidadMovil();
        gestionObjetosRecogibles = new GestionObjetosRecogibles(gestionEscenario, gestionEntidadMovil, VELOCIDAD_BASE);
        gestionObjetosRecogibles.crear();

        for (int i = 0; i < MAX_LINEAS_VELOCIDAD; i++) {
            lineasX[i] = MathUtils.random(10f, ANCHO_MUNDO - 10f);
            lineasY[i] = MathUtils.random(0f, ALTO_MUNDO);
            lineasLargo[i] = MathUtils.random(40f, 120f);
            lineasVelocidad[i] = MathUtils.random(1.2f, 1.8f);
            lineasAlpha[i] = MathUtils.random(0.18f, 0.45f);
        }

        crearEntidadesEscenario();
    }

    private void crearEntidadesEscenario() {
        gestionEntidadMovil.reiniciar();
        tiempoProximaEntidad = 0f;
    }

    private void reiniciarJuego() {
        player.reiniciar();
        tiempoGlitch = 0f;
        nivelDificultad = 1;
        tiempoAlertaNivel = 0f;
        gestionEscenario.cambiarEscenario("VIAS");
        gestionObjetosRecogibles.reiniciarObjetos();
        crearEntidadesEscenario();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        tiempoJuego += delta;

        if (tiempoGlitch > 0) {
            tiempoGlitch -= delta;
            float shakeX = MathUtils.random(-16f, 16f);
            float shakeY = MathUtils.random(-8f, 8f);
            camera.position.set(ANCHO_MUNDO / 2f + shakeX, ALTO_MUNDO / 2f + shakeY, 0);
        } else {
            camera.position.set(ANCHO_MUNDO / 2f, ALTO_MUNDO / 2f, 0);
        }
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        if (player.getVidas() > 0) {
            float velocidadActual = getVelocidadActual();
            gestionObjetosRecogibles.setVelocidad(velocidadActual);

            float factorVelocidad = velocidadActual / VELOCIDAD_BASE;
            player.setFactorVelocidad(factorVelocidad);

            float factorProgreso = (velocidadActual - VELOCIDAD_BASE) / (VELOCIDAD_MAXIMA - VELOCIDAD_BASE);
            factorProgreso = MathUtils.clamp(factorProgreso, 0f, 1f);

            int nuevoNivel = 1 + (int) (factorProgreso * 4.0f);
            nuevoNivel = MathUtils.clamp(nuevoNivel, 1, 5);
            if (nuevoNivel > nivelDificultad) {
                nivelDificultad = nuevoNivel;
                tiempoAlertaNivel = 2.0f;
                switch (nivelDificultad) {
                    case 2: textoAlertaNivel = "¡DIFICULTAD: NIVEL 2 (RAPIDO)!"; break;
                    case 3: textoAlertaNivel = "¡DIFICULTAD: NIVEL 3 (INTENSO)!"; break;
                    case 4: textoAlertaNivel = "¡PELIGRO: NIVEL 4 (FRENETICO)!"; break;
                    case 5: textoAlertaNivel = "¡MAXIMA VELOCIDAD: NIVEL 5 (EXTREMO)!"; break;
                }
            }
            if (tiempoAlertaNivel > 0) {
                tiempoAlertaNivel -= delta;
            }

            // Actualizar lineas de viento/velocidad
            for (int i = 0; i < MAX_LINEAS_VELOCIDAD; i++) {
                lineasY[i] -= velocidadActual * lineasVelocidad[i] * delta;
                if (lineasY[i] + lineasLargo[i] < 0) {
                    lineasY[i] = ALTO_MUNDO + MathUtils.random(10f, 100f);
                    lineasX[i] = MathUtils.random(10f, ANCHO_MUNDO - 10f);
                    lineasLargo[i] = MathUtils.random(40f, 120f);
                }
            }

            player.actualizarEstado();
            player.actualizarMovimiento();

            gestionEscenario.actualizar(delta);
            gestionEntidadMovil.actualizar(delta);
            gestionEntidadMovil.comprobarColisiones(player);
            actualizarGeneracionEntidades(delta);

            String destino = gestionObjetosRecogibles.actualizar(delta, player);

            if (destino != null) {
                gestionEscenario.cambiarEscenario(destino);
                gestionObjetosRecogibles.reiniciarObjetos();
                crearEntidadesEscenario();
                tiempoGlitch = DURACION_GLITCH;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            reiniciarJuego();
        }

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        batch.begin();

        gestionEscenario.dibujar(batch, ANCHO_MUNDO, ALTO_MUNDO);

        float velocidadActual = getVelocidadActual();
        float factorProgreso = (velocidadActual - VELOCIDAD_BASE) / (VELOCIDAD_MAXIMA - VELOCIDAD_BASE);
        factorProgreso = MathUtils.clamp(factorProgreso, 0f, 1f);

        // Lineas de viento / sensacion de velocidad extrema
        if (factorProgreso > 0.04f && texturaPixel != null) {
            int lineasActivas = (int) (6 + factorProgreso * (MAX_LINEAS_VELOCIDAD - 6));
            for (int i = 0; i < lineasActivas; i++) {
                float alpha = lineasAlpha[i] * (0.3f + 0.7f * factorProgreso);
                batch.setColor(0.9f, 0.95f, 1f, alpha);
                batch.draw(texturaPixel, lineasX[i], lineasY[i], 2.5f, lineasLargo[i]);
            }
            batch.setColor(Color.WHITE);
        }

        gestionEntidadMovil.dibujar(batch);
        gestionObjetosRecogibles.dibujar(batch);
        player.dibujar(batch);

        // Efecto Glitch al atravesar un portal interdimensional
        if (tiempoGlitch > 0 && texturaPixel != null) {
            int barras = MathUtils.random(4, 9);
            for (int i = 0; i < barras; i++) {
                float yBarra = MathUtils.random(0f, ALTO_MUNDO);
                float hBarra = MathUtils.random(8f, 36f);
                float xBarra = MathUtils.random(-40f, 40f);
                if (i % 2 == 0) {
                    batch.setColor(0f, 0.95f, 1f, MathUtils.random(0.35f, 0.60f));
                } else {
                    batch.setColor(1f, 0.1f, 0.85f, MathUtils.random(0.35f, 0.60f));
                }
                batch.draw(texturaPixel, xBarra, yBarra, ANCHO_MUNDO + 80f, hBarra);
            }
            batch.setColor(Color.WHITE);
        }

        if (player.estaSaltando()) {
            font.draw(batch, ">>> SALTO <<<", player.getArea().x - 15f, player.getArea().y + 160f);
        }

        font.draw(batch, "Puntos: " + player.getPuntos(), 20f, ALTO_MUNDO - 20f);
        font.draw(batch, "Monedas: " + player.getMonedas(), 20f, ALTO_MUNDO - 40f);
        // Vidas representadas con corazones pixel art (sustituyendo texto)
        int vidasActuales = player.getVidas();
        if (texturaCorazon != null) {
            float tamanoCorazon = 32f;
            float espaciado = 6f;
            float yCorazon = ALTO_MUNDO - 36f;

            if (vidasActuales <= 5) {
                int totalContenedores = Math.max(3, vidasActuales);
                float anchoTotal = totalContenedores * tamanoCorazon + (totalContenedores - 1) * espaciado;
                float xInicio = ANCHO_MUNDO - 20f - anchoTotal;

                for (int i = 0; i < totalContenedores; i++) {
                    float xPos = xInicio + i * (tamanoCorazon + espaciado);
                    if (i < vidasActuales) {
                        // Efecto de latido urgente cuando queda 1 sola vida
                        if (vidasActuales == 1) {
                            float pulso = 1f + 0.16f * MathUtils.sin(tiempoJuego * 9f);
                            float wP = tamanoCorazon * pulso;
                            float hP = tamanoCorazon * pulso;
                            batch.draw(texturaCorazon, xPos - (wP - tamanoCorazon) / 2f, yCorazon - (hP - tamanoCorazon) / 2f, wP, hP);
                        } else {
                            batch.draw(texturaCorazon, xPos, yCorazon, tamanoCorazon, tamanoCorazon);
                        }
                    } else if (texturaCorazonVacio != null) {
                        batch.draw(texturaCorazonVacio, xPos, yCorazon, tamanoCorazon, tamanoCorazon);
                    }
                }
            } else {
                // Modo pruebas (ej: 9999 vidas)
                float xPos = ANCHO_MUNDO - 130f;
                batch.draw(texturaCorazon, xPos, yCorazon, tamanoCorazon, tamanoCorazon);
                font.draw(batch, "x " + vidasActuales, xPos + tamanoCorazon + 8f, yCorazon + 22f);
            }
        }

        // Indicador HUD de Nivel y Velocidad Dinamica
        String etiquetaNivel;
        Color colorNivel;
        switch (nivelDificultad) {
            case 1:
                etiquetaNivel = "Nivel 1 (Normal)";
                colorNivel = Color.GREEN;
                break;
            case 2:
                etiquetaNivel = "Nivel 2 (Rapido)";
                colorNivel = Color.YELLOW;
                break;
            case 3:
                etiquetaNivel = "Nivel 3 (Intenso)";
                colorNivel = Color.ORANGE;
                break;
            case 4:
                etiquetaNivel = "Nivel 4 (Frenetico)";
                colorNivel = Color.RED;
                break;
            default:
                etiquetaNivel = "Nivel 5 (EXTREMO)";
                colorNivel = Color.MAGENTA;
                break;
        }

        font.setColor(colorNivel);
        font.draw(
            batch,
            String.format(java.util.Locale.US, "Dificultad: %s", etiquetaNivel),
            ANCHO_MUNDO - 260f,
            ALTO_MUNDO - 45f
        );
        font.draw(
            batch,
            String.format(java.util.Locale.US, "Velocidad: %.1fx (%d km/h)", velocidadActual / VELOCIDAD_BASE, (int) (velocidadActual / 5f)),
            ANCHO_MUNDO - 260f,
            ALTO_MUNDO - 65f
        );
        font.setColor(Color.WHITE);

        // Banner flotante central al subir de nivel de dificultad
        if (tiempoAlertaNivel > 0 && texturaPixel != null) {
            float alphaBanner = Math.min(1f, tiempoAlertaNivel);
            batch.setColor(0f, 0f, 0f, 0.70f * alphaBanner);
            batch.draw(texturaPixel, ANCHO_MUNDO / 2f - 240f, ALTO_MUNDO / 2f + 115f, 480f, 44f);

            batch.setColor(colorNivel.r, colorNivel.g, colorNivel.b, alphaBanner);
            batch.draw(texturaPixel, ANCHO_MUNDO / 2f - 240f, ALTO_MUNDO / 2f + 156f, 480f, 3f);
            batch.draw(texturaPixel, ANCHO_MUNDO / 2f - 240f, ALTO_MUNDO / 2f + 115f, 480f, 3f);

            font.setColor(colorNivel.r, colorNivel.g, colorNivel.b, alphaBanner);
            font.draw(batch, textoAlertaNivel, ANCHO_MUNDO / 2f - 190f, ALTO_MUNDO / 2f + 142f);
            font.setColor(Color.WHITE);
            batch.setColor(Color.WHITE);
        }

        if (player.tieneEscudo()) {
            font.draw(
                batch,
                String.format(java.util.Locale.US, "Escudo: %.1f s", player.getTiempoEscudo()),
                20f,
                ALTO_MUNDO - 60f
            );
        }

        if (player.tieneIman()) {
            font.draw(
                batch,
                String.format(java.util.Locale.US, "Iman: %.1f s", player.getTiempoIman()),
                20f,
                ALTO_MUNDO - 80f
            );
        }

        if (player.tieneEstrella()) {
            font.draw(
                batch,
                String.format(
                    java.util.Locale.US,
                    "Multiplicador x%d: %.1f s",
                    player.getMultiplicador(),
                    player.getTiempoEstrella()
                ),
                20f,
                ALTO_MUNDO - 100f
            );
        }

        if (player.getVidas() <= 0) {
            font.draw(batch, "FIN DEL JUEGO", ANCHO_MUNDO / 2f - 70f, ALTO_MUNDO / 2f);
            font.draw(batch, "Presiona ENTER para reiniciar", ANCHO_MUNDO / 2f - 100f, ALTO_MUNDO / 2f - 30f);
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    private void generarEntidadAleatoria() {
        String escenario = gestionEscenario.getNombreEscenarioActual();
        TipoObstaculo tipo;

        switch (escenario) {
            case "VIAS":
                tipo = TipoObstaculo.VAGON;
                break;

            case "CIUDAD":
            case "TUNEL":
                tipo = MathUtils.randomBoolean() ? TipoObstaculo.AUTO : TipoObstaculo.AUTOBUS;
                break;

            case "AEREO":
                tipo = MathUtils.randomBoolean() ? TipoObstaculo.AVION : TipoObstaculo.PAJAROS;
                break;

            default:
                Gdx.app.error("SubwaySurfer", "Escenario desconocido: " + escenario);
                return;
        }

        // Margen proporcional a la altura del obstaculo para trenes largos y obstaculos estandar
        float margenObstaculo = Math.max(240f, tipo.getAlto() + 60f);
        Array<Integer> carrilesLibres = new Array<Integer>();

        for (int c = 0; c < 3; c++) {
            boolean ocupadoObstaculo = gestionEntidadMovil.carrilOcupadoArriba(c, margenObstaculo);
            boolean ocupadoRecogible = gestionObjetosRecogibles.carrilOcupadoArriba(c, 180f);
            boolean bloqueoInjusto = gestionEntidadMovil.creariaBloqueoTotal(c, ALTO_MUNDO, tipo.getAlto());

            if (!ocupadoObstaculo && !ocupadoRecogible && !bloqueoInjusto) {
                carrilesLibres.add(c);
            }
        }

        if (carrilesLibres.size == 0) {
            tiempoProximaEntidad = 0.2f;
            return;
        }

        int carrilSeleccionado = carrilesLibres.random();

        // Builder: arma el obstaculo paso a paso.
        gestionEntidadMovil.agregar(
            new ObstaculoBuilder()
                .tipo(tipo)
                .carril(carrilSeleccionado)
                .velocidad(getVelocidadActual())
                .construir()
        );
    }

    private float getVelocidadActual() {
        if (player == null) return VELOCIDAD_BASE;
        return Math.min(VELOCIDAD_MAXIMA, VELOCIDAD_BASE + player.getPuntos() * INCREMENTO_POR_PUNTO);
    }

    private void actualizarGeneracionEntidades(float delta) {
        tiempoProximaEntidad -= delta;

        if (tiempoProximaEntidad <= 0f) {
            generarEntidadAleatoria();

            float velocidadActual = getVelocidadActual();
            float factorProgreso = (velocidadActual - VELOCIDAD_BASE) / (VELOCIDAD_MAXIMA - VELOCIDAD_BASE);
            factorProgreso = MathUtils.clamp(factorProgreso, 0f, 1f);

            // Reduccion dinamica progresiva del intervalo entre obstaculos
            float factorIntervalo = 1f - factorProgreso * 0.58f;
            tiempoProximaEntidad = MathUtils.random(INTERVALO_MINIMO * factorIntervalo, INTERVALO_MAXIMO * factorIntervalo);
        }
    }

    @Override
    public void dispose() {
        if (musicaFondo != null) {
            musicaFondo.stop();
        }

        if (gestionEntidadMovil != null) gestionEntidadMovil.destruir();
        if (player != null) player.destruir();

        // Singleton: libera de una sola vez texturas, sonidos y musica.
        GestorRecursos.liberar();

        if (font != null) font.dispose();
        if (batch != null) batch.dispose();
        if (texturaPixel != null) texturaPixel.dispose();
    }
}