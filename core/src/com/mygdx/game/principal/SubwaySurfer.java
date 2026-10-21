package com.mygdx.game.principal;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.escenarios.EscenarioViasTrenes;
import com.mygdx.game.gestion.GestionEntidadMovil;
import com.mygdx.game.gestion.GestionEscenario;
import com.mygdx.game.gestion.GestionObjetosRecogibles;
import com.mygdx.game.jugador.Player;

public class SubwaySurfer extends ApplicationAdapter {
    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private BitmapFont font;

    private Player tarro;
    private GestionEscenario gestionEscenario;
    private GestionEntidadMovil gestionEntidadMovil;
    private GestionObjetosRecogibles gestionObjetosRecogibles;

    private static final float ANCHO_MUNDO = 1920;
    private static final float ALTO_MUNDO = 1080;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_MUNDO, ALTO_MUNDO, camera);
        viewport.apply();

        batch = new SpriteBatch();
        font = new BitmapFont();

        tarro = new Player(
                new Texture("bucket.png"),
                Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"))
        );
        tarro.crear();

        gestionEscenario = new GestionEscenario(new EscenarioViasTrenes(null));
        gestionEntidadMovil = new GestionEntidadMovil();

        gestionObjetosRecogibles = new GestionObjetosRecogibles(
                new Texture("moneda.jpg"),
                new Texture("dropBad.png"),
                Gdx.audio.newSound(Gdx.files.internal("drop.wav")),
                Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"))
        );
        gestionObjetosRecogibles.crear();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        float delta = Gdx.graphics.getDeltaTime();

        if (tarro.getVidas() > 0) {
            tarro.actualizarEstado();

            if (!tarro.estaHerido()) {
                tarro.actualizarMovimiento();
            }

            gestionEscenario.actualizar(delta);
            gestionEntidadMovil.actualizar(delta);
            gestionObjetosRecogibles.actualizar(delta, tarro);
        }

        batch.begin();

        gestionEscenario.dibujar(batch, ANCHO_MUNDO, ALTO_MUNDO);
        gestionEntidadMovil.dibujar(batch);
        gestionObjetosRecogibles.dibujar(batch);
        tarro.dibujar(batch);

        font.draw(batch, "Puntos: " + tarro.getPuntos(), 20, ALTO_MUNDO - 20);
        font.draw(batch, "Vidas: " + tarro.getVidas(), ANCHO_MUNDO - 60, ALTO_MUNDO - 20);

        if (tarro.getVidas() <= 0) {
            font.draw(batch, "FIN DEL JUEGO", 850, 540);
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        tarro.destruir();
        gestionEscenario.destruir();
        gestionEntidadMovil.destruir();
        gestionObjetosRecogibles.destruir();
        batch.dispose();
        font.dispose();
    }
}