package com.mygdx.game.principal;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
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
    private static final float VELOCIDAD_MUNDO = 300f;   // misma velocidad para obstaculos y recogibles
    private static final float INTERVALO_MINIMO = 0.8f;
    private static final float INTERVALO_MAXIMO = 2.0f;

    private float tiempoProximaEntidad = 0.1f;

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

        // Singleton: todos los recursos se piden a GestorRecursos.
        GestorRecursos recursos = GestorRecursos.getInstancia();

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
        gestionObjetosRecogibles = new GestionObjetosRecogibles(gestionEscenario, VELOCIDAD_MUNDO);
        gestionObjetosRecogibles.crear();

        crearEntidadesEscenario();
    }

    private void crearEntidadesEscenario() {
        gestionEntidadMovil.reiniciar();
        tiempoProximaEntidad = 0f;
    }

    private void reiniciarJuego() {
        player.reiniciar();
        gestionEscenario.cambiarEscenario("VIAS");
        gestionObjetosRecogibles.reiniciarObjetos();
        crearEntidadesEscenario();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        if (player.getVidas() > 0) {
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
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            reiniciarJuego();
        }

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        batch.begin();

        gestionEscenario.dibujar(batch, ANCHO_MUNDO, ALTO_MUNDO);
        gestionEntidadMovil.dibujar(batch);
        gestionObjetosRecogibles.dibujar(batch);
        player.dibujar(batch);

        font.draw(batch, "Puntos: " + player.getPuntos(), 20f, ALTO_MUNDO - 20f);
        font.draw(batch, "Monedas: " + player.getMonedas(), 20f, ALTO_MUNDO - 40f);
        font.draw(batch, "Vidas: " + player.getVidas(), ANCHO_MUNDO - 100f, ALTO_MUNDO - 20f);

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

        // Builder: arma el obstaculo paso a paso.
        gestionEntidadMovil.agregar(
            new ObstaculoBuilder()
                .tipo(tipo)
                .carril(MathUtils.random(0, 2))
                .velocidad(VELOCIDAD_MUNDO)
                .construir()
        );
    }

    private void actualizarGeneracionEntidades(float delta) {
        tiempoProximaEntidad -= delta;

        if (tiempoProximaEntidad <= 0f) {
            generarEntidadAleatoria();
            tiempoProximaEntidad = MathUtils.random(INTERVALO_MINIMO, INTERVALO_MAXIMO);
        }
    }

    @Override
    public void dispose() {
        if (musicaFondo != null) {
            musicaFondo.stop();
        }

        if (gestionEntidadMovil != null) gestionEntidadMovil.destruir();

        // Singleton: libera de una sola vez texturas, sonidos y musica.
        GestorRecursos.liberar();

        if (font != null) font.dispose();
        if (batch != null) batch.dispose();
    }
}