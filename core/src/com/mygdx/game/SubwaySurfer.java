
package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class SubwaySurfer extends ApplicationAdapter {
    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private BitmapFont font;

    private Tarro tarro;
    private Lluvia lluvia;

    private static final float ANCHO_MUNDO = 1920;
    private static final float ALTO_MUNDO = 1080;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_MUNDO, ALTO_MUNDO, camera);
        viewport.apply();

        batch = new SpriteBatch();
        font = new BitmapFont();

        tarro = new Tarro(
                new Texture("bucket.png"),
                Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"))
        );
        tarro.crear();

        lluvia = new Lluvia(
                new Texture("drop.png"),
                new Texture("dropBad.png"),
                Gdx.audio.newSound(Gdx.files.internal("drop.wav")),
                Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"))
        );
        lluvia.crear();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        if (tarro.getVidas() > 0) {
            tarro.actualizarEstado();

            if (!tarro.estaHerido()) {
                tarro.actualizarMovimiento();
            }

            lluvia.actualizarMovimiento(tarro);
        }

        batch.begin();

        lluvia.dibujar(batch);
        tarro.dibujar(batch);

        font.draw(batch, "Puntos: " + tarro.getPuntos(), 20, 1045);
        font.draw(batch, "Vidas: " + tarro.getVidas(), 1750, 1045);

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
        lluvia.destruir();
        batch.dispose();
        font.dispose();
    }
}