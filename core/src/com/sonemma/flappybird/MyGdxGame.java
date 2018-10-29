package com.sonemma.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture birds[];
	Texture topTube;
	Texture bottomTube;
	Texture gameOver;
	//ShapeRenderer shapeRenderer;//like batch draw shape instead
    Circle birdCircle;
    Rectangle[] topTubeRect;
    Rectangle[] bottomTubeRect;
    BitmapFont font;

	int flapState=0;
	float birdY=0;
	float velocity=0;
	int gameState=0;
	float gravivity=2;
	float maxTubeOffset;
	int gap=400;
	Random randomGenerator;
	float tubeVelocity=5;
	int numberOfTubes=4;
    float tubeX[]=new float[numberOfTubes];
    float tubeOffset[]=new float[numberOfTubes];
	float tubeDistance;
    int score=0;
    int scoringTube=0;


	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		font=new BitmapFont();
		font.setColor(Color.RED);
		font.getData().setScale(10);
        birdCircle=new Circle();
        //shapeRenderer=new ShapeRenderer();
        topTubeRect=new Rectangle[numberOfTubes];
        bottomTubeRect=new Rectangle[numberOfTubes];
        gameOver=new Texture("gameover.png");

		birds=new Texture[2];
		birds[0]=new Texture("bird.png");
		birds[1]=new Texture("bird2.png");
		topTube=new Texture("toptube.png");
		bottomTube=new Texture("bottomtube.png");
		maxTubeOffset=Gdx.graphics.getHeight()/2-gap/2-100;

		tubeDistance=Gdx.graphics.getWidth()/2;
		birdY=Gdx.graphics.getHeight()/2-birds[flapState].getHeight()/2;//here

		randomGenerator =new Random();

		//heres
		/*for(int i=0;i<numberOfTubes;i++){
            tubeOffset[i]=(randomGenerator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-1800);
            tubeX[i]=Gdx.graphics.getWidth()/2-topTube.getWidth()/2+Gdx.graphics.getWidth()+i*tubeDistance;

            topTubeRect[i]=new Rectangle();
            bottomTubeRect[i]=new Rectangle();
        }*/
		startGame();
	}

	@Override
	public void render () {
        batch.begin();

        //drawing background
        batch.draw(background, 0, 0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());


        //draw bird
        batch.draw(birds[flapState], Gdx.graphics.getWidth()/2-birds[flapState].getWidth()/2, birdY);


        if(gameState==1){
            //for bird animation
            if(flapState==0){
                flapState=1;

            }else{
                flapState=0;
            }

            if(birdY>0){
                velocity+=gravivity;
                birdY-=velocity;
            }else{
                gameState=2;
            }


            //scoring check to see if tube is at the left center, if it is, it means bird already passed it
            if(tubeX[scoringTube]<Gdx.graphics.getWidth()/2-topTube.getWidth()){
                score++;
                Gdx.app.log("score", String.valueOf(score));
                if(scoringTube<numberOfTubes-1){
                    scoringTube++;
                }else{
                    scoringTube=0;
                }
            }
            for(int i=0;i<numberOfTubes;i++){
                if(tubeX[i]<0-topTube.getWidth()){
                    tubeX[i]+=numberOfTubes*tubeDistance;
                    tubeOffset[i]=(randomGenerator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-1800);
                }
                else {
                    tubeX[i]-=tubeVelocity;

                }
                //draw the tubes
                batch.draw(topTube,tubeX[i],Gdx.graphics.getHeight()/2+gap/2+tubeOffset[i]);
                batch.draw(bottomTube,tubeX[i],Gdx.graphics.getHeight()/2-gap/2-bottomTube.getHeight()+tubeOffset[i]);

                //draw our text font
                font.draw(batch,Integer.toString(score),100,200);

                topTubeRect[i]=new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2+gap/2+tubeOffset[i],topTube.getWidth(),topTube.getHeight());
                bottomTubeRect[i]=new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2-gap/2-bottomTube.getHeight()+tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());

            }
        }






        //set the circle to be in the same place as the bird, collision only works on shape not textures
        //once sets on the bird then it will move with it
        birdCircle.set(Gdx.graphics.getWidth()/2,birdY+birds[flapState].getWidth()/2,birds[flapState].getHeight()/2);

        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.setColor(Color.RED);
        //putting the bird circle into the renderer
        //shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);

        //putting rectangles shape in same place as tubes so you can see it
        for(int i=0;i<numberOfTubes;i++){
            //shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2+gap/2+tubeOffset[i],topTube.getWidth(),topTube.getHeight());
            //shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2-gap/2-bottomTube.getHeight()+tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());
            //check our collision
            if(Intersector.overlaps(birdCircle,topTubeRect[i])|| Intersector.overlaps(birdCircle,bottomTubeRect[i])){
                gameState=2;
                //batch.draw(gameOver,Gdx.graphics.getWidth()/2-gameOver.getWidth()/2,Gdx.graphics.getHeight()/2-gameOver.getHeight()/2);
            }
        }
        //check input
        inputControl();
        //shapeRenderer.end();
        batch.end();

	}


	public void inputControl(){
	    if(gameState!=2){
            if(Gdx.input.justTouched()){
                Gdx.app.log("Touched","Yap");
                gameState=1;
                velocity=-20;
            }
        }else if(gameState==2){
	        batch.draw(gameOver,Gdx.graphics.getWidth()/2-gameOver.getWidth()/2,Gdx.graphics.getHeight()/2-gameOver.getHeight()/2);
            if(Gdx.input.justTouched()){
                gameState=1;
                startGame();
            }
        }

    }

    public void startGame(){
        birdY=Gdx.graphics.getHeight()/2-birds[flapState].getHeight()/2;
        for(int i=0;i<numberOfTubes;i++){
            tubeOffset[i]=(randomGenerator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-1800);
            tubeX[i]=Gdx.graphics.getWidth()/2-topTube.getWidth()/2+Gdx.graphics.getWidth()+i*tubeDistance;

            topTubeRect[i]=new Rectangle();
            bottomTubeRect[i]=new Rectangle();

            score=0;
            scoringTube=0;
            velocity=0;
        }
    }
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
        birds[0].dispose();
        birds[1].dispose();
        topTube.dispose();
        bottomTube.dispose();

	}
}
