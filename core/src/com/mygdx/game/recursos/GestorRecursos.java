package com.mygdx.game.recursos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Patron Singleton: unica instancia que carga, comparte y libera
 * todas las texturas, sonidos y musicas del juego.
 *
 * Debe usarse despues de que libGDX haya creado el contexto OpenGL
 * (es decir, desde create() en adelante).
 */
public final class GestorRecursos {
    private static GestorRecursos instancia;

    private final ObjectMap<String, Texture> texturas = new ObjectMap<String, Texture>();
    private final ObjectMap<String, Sound> sonidos = new ObjectMap<String, Sound>();
    private final ObjectMap<String, Music> musicas = new ObjectMap<String, Music>();

    private GestorRecursos() {
        // Constructor privado: nadie mas puede crear instancias.
    }

    public static GestorRecursos getInstancia() {
        if (instancia == null) {
            instancia = new GestorRecursos();
        }
        return instancia;
    }

    public Texture getTextura(String ruta) {
        Texture textura = texturas.get(ruta);

        if (textura == null) {
            textura = new Texture(Gdx.files.internal(ruta));
            texturas.put(ruta, textura);
        }

        return textura;
    }

    public Sound getSonido(String ruta) {
        Sound sonido = sonidos.get(ruta);

        if (sonido == null) {
            sonido = Gdx.audio.newSound(Gdx.files.internal(ruta));
            sonidos.put(ruta, sonido);
        }

        return sonido;
    }

    public Music getMusica(String ruta) {
        Music musica = musicas.get(ruta);

        if (musica == null) {
            musica = Gdx.audio.newMusic(Gdx.files.internal(ruta));
            musicas.put(ruta, musica);
        }

        return musica;
    }

    /** Libera todos los recursos y descarta la instancia. */
    public static void liberar() {
        if (instancia == null) {
            return;
        }

        for (Texture textura : instancia.texturas.values()) {
            textura.dispose();
        }
        for (Sound sonido : instancia.sonidos.values()) {
            sonido.dispose();
        }
        for (Music musica : instancia.musicas.values()) {
            musica.dispose();
        }

        instancia.texturas.clear();
        instancia.sonidos.clear();
        instancia.musicas.clear();
        instancia = null;
    }
}