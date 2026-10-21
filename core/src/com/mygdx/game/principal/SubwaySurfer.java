package com.mygdx.game.principal;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.gestion.GestionEntidadMovil;
import com.mygdx.game.gestion.GestionEscenario;
import com.mygdx.game.gestion.GestionObjetosRecogibles;
import com.mygdx.game.jugador.Player;
import com.badlogic.gdx.audio.Music;

public class SubwaySurfer extends ApplicationAdapter {
    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private BitmapFont font;

    private Player player;
    private GestionEscenario gestionEscenario;
    private GestionEntidadMovil gestionEntidadMovil;
    private GestionObjetosRecogibles gestionObjetosRecogibles;

    private static final float ANCHO_MUNDO = 1920;
    private static final float ALTO_MUNDO = 1080;
    
    private Music musicaFondo;
    private static final float VOLUMEN_MUSICA = 0.3f;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_MUNDO, ALTO_MUNDO, camera);
        viewport.apply();

        batch = new SpriteBatch();
        font = new BitmapFont();

        player = new Player(
                new Texture("bucket.png"),
                Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"))
        );
        player.crear();
        
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("principalTheme.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(VOLUMEN_MUSICA);
        musicaFondo.play();
        
        gestionEscenario = new GestionEscenario();
        gestionEntidadMovil = new GestionEntidadMovil();

        gestionObjetosRecogibles = new GestionObjetosRecogibles(
                new Texture("moneda.jpg"),
                new Texture("tren.jpg"),
                Gdx.audio.newSound(Gdx.files.internal("drop.wav")),
                gestionEscenario
                );
        gestionObjetosRecogibles.crear();
    }

    @Override
    public void render() {

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        float delta = Gdx.graphics.getDeltaTime();

        if (player.getVidas() > 0) {
        	player.actualizarEstado();

            if (!player.estaHerido()) {
            	player.actualizarMovimiento();
            }

            gestionEscenario.actualizar(delta);
            gestionEntidadMovil.actualizar(delta);
            String destino = gestionObjetosRecogibles.actualizar(delta,player);

            if (destino != null) {
                gestionEscenario.cambiarEscenario(destino);
                gestionObjetosRecogibles.reiniciarObjetos();
                gestionEntidadMovil.reiniciarObjetos();
            }
        }

        batch.begin();

        gestionEscenario.dibujar(batch, ANCHO_MUNDO, ALTO_MUNDO);
        gestionEntidadMovil.dibujar(batch);
        gestionObjetosRecogibles.dibujar(batch);
        player.dibujar(batch);

        font.draw(batch, "Puntos: " + player.getPuntos(), 20, ALTO_MUNDO - 20);
        font.draw(batch, "Vidas: " + player.getVidas(), ANCHO_MUNDO - 60, ALTO_MUNDO - 20);

        if (player.getVidas() <= 0) {
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
    	player.destruir();
        gestionEntidadMovil.destruir();
        gestionObjetosRecogibles.destruir();
        batch.dispose();
        font.dispose();
        if (musicaFondo != null) {
            musicaFondo.stop();
            musicaFondo.dispose();
        }
    }
}