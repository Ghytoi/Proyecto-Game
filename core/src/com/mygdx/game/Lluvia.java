
package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Lluvia {
    private Array<Rectangle> rainDropsPos;
    private Array<Integer> rainDropsType;

    private Texture dropImage;
    private Texture dropBadImage;
    private Sound dropSound;
    private Music rainMusic;

    private float lastDropTime;
    private float tiempoEntreGotas = 0.8f;
    private float velocidadGotas = 350;

    private static final float ANCHO_MUNDO = 1920;
    private static final float ALTO_MUNDO = 1080;
    private static final float ANCHO_GOTA = 64;
    private static final float ALTO_GOTA = 64;
    private static final float ANCHO_CARRIL = ANCHO_MUNDO / 3;

    public Lluvia(Texture drop, Texture dropBad, Sound sound, Music music) {
        dropImage = drop;
        dropBadImage = dropBad;
        dropSound = sound;
        rainMusic = music;
    }

    public void crear() {
        rainDropsPos = new Array<Rectangle>();
        rainDropsType = new Array<Integer>();

        crearGotaDeLluvia();
        rainMusic.setLooping(true);
        rainMusic.play();
    }

    private void crearGotaDeLluvia() {
        Rectangle gota = new Rectangle();

        gota.width = ANCHO_GOTA;
        gota.height = ALTO_GOTA;

        int carril = MathUtils.random(0, 2);

        gota.x = carril * ANCHO_CARRIL
                + (ANCHO_CARRIL - gota.width) / 2;
        gota.y = ALTO_MUNDO;

        int tipo = MathUtils.random(0, 1);

        rainDropsPos.add(gota);
        rainDropsType.add(tipo);

        lastDropTime = 0;
    }

    public void actualizarMovimiento(Tarro tarro) {
        float delta = Gdx.graphics.getDeltaTime();
        lastDropTime += delta;

        if (lastDropTime >= tiempoEntreGotas) {
            crearGotaDeLluvia();
        }

        for (int i = rainDropsPos.size - 1; i >= 0; i--) {
            Rectangle gota = rainDropsPos.get(i);
            int tipo = rainDropsType.get(i);

            gota.y -= velocidadGotas * delta;

            if (gota.overlaps(tarro.getArea())) {
                if (tipo == 0) {
                    tarro.sumarPuntos(1);
                    dropSound.play();
                } else {
                    tarro.dañar();
                }

                rainDropsPos.removeIndex(i);
                rainDropsType.removeIndex(i);
            } else if (gota.y + gota.height < 0) {
                rainDropsPos.removeIndex(i);
                rainDropsType.removeIndex(i);
            }
        }
    }

    public void dibujar(SpriteBatch batch) {
        for (int i = 0; i < rainDropsPos.size; i++) {
            Rectangle gota = rainDropsPos.get(i);

            if (rainDropsType.get(i) == 0) {
                batch.draw(dropImage, gota.x, gota.y);
            } else {
                batch.draw(dropBadImage, gota.x, gota.y);
            }
        }
    }

    public void destruir() {
        dropImage.dispose();
        dropBadImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }
}