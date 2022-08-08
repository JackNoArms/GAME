package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;


public class Drop extends ApplicationAdapter {

    private Texture dropImage;
    private Texture bucketImage;
    private Sound dropSound;
    private Music rainMusic;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Rectangle bucket;
    private Array<Rectangle> raindrops;
    private long lastDropTime;

    public void create(){

        //Garante que sempre irá mostrar o jogo em 800x480
        camera = new OrthographicCamera();
        camera.setToOrtho(false,800,480);
        batch = new SpriteBatch();

        //Vai carregar as imagens
        dropImage = new Texture(Gdx.files.internal("droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        //Som de efeito e o som da musica
        //Usar sound quando for menos de 10 sec, usar musica para o outros casos.
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        //Tocar a musica por trás em loop
        rainMusic.setLooping(true);
        rainMusic.play();

        //Criando o balde
        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64/2;
        bucket.y = 20;
        bucket.height = 64;
        bucket.width = 64;

        //Criar as gotas
        raindrops = new Array<Rectangle>();
        spawnRaindrop();
    }

    public void render(){
        //Dando o clear na cor azul e dando update
        ScreenUtils.clear(0,0,0.2f,1);

        //Chama a camera
        camera.update();

        //Chama o SpriteBatch e redenriza
        batch.setProjectionMatrix(camera.combined);

        //Começa a criar um novo balde
        batch.begin();
        batch.draw(bucketImage,bucket.x,bucket.y);
        //Desenha todas as gotas
        for(Rectangle raindrop: raindrops) {
            batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        batch.end();

        //usando o mouse para mover
        if(Gdx.input.isTouched()){
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(),0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;
        }
        //usar o teclado para mover(usando o tempo)
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

        //garantir que o balde não saia da tela
        if(bucket.x < 0) bucket.x = 0;
        if(bucket.x > 800 - 64) bucket.x = 800 - 64;

        //Checa se cria nova gota
        if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

        //Se a gota de chuva tiver abaixo da borda da tela ela é removida
        //Gotas se movem 200 pixeis por segundo
        //Se o rentagulo da gota estiver no dentro do xy do balde, toca o som
        for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if(raindrop.y + 64 < 0) iter.remove();
            if(raindrop.overlaps(bucket)) {
                dropSound.play();
                iter.remove();
            }

        }
    }

    private void spawnRaindrop(){
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0,800-64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    //limpando as coisas que não serão mais usadas
    @Override
    public void dispose() {
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
        batch.dispose();
    }

}
