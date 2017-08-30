package uk.ac.lincoln.games.nlfs;

import uk.ac.lincoln.games.nlfs.NonLeague.Background;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class BaseScreen implements Screen {
	
	protected final NonLeague game;
    protected final Stage stage;
    protected final Table table;
    protected final Table root_table;

    public BaseScreen(NonLeague game)
    {
        this.game = game;
        stage = new Stage(game.viewport);
        Gdx.input.setInputProcessor(stage);
		table = new Table();
		stage.addActor(game.new Background());
		
		root_table = new Table();
		stage.addActor(root_table);
		root_table.setFillParent(true);
		root_table.pad(20);
		root_table.add(table);
		
		table.pad(10);
		table.setBackground(Assets.skin.getDrawable("transparent"));//.skin.getDrawable("transparent"));
		//table.setDebug(false);
		table.setSkin(Assets.skin);
		Gdx.input.setInputProcessor(stage);
    }
    
    public abstract void update();//This function called when screen is about to be shown
    
    protected void reset(){}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 0f, 0f, 0f, 1f );
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT );

        // update and draw the stage actors
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
    	stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {        
    	//Assets.manager.finishLoading();
    }

    @Override
    public void hide() {
        
    }

    @Override
    public void pause() {
 
    }

    @Override
    public void resume() {
    	
    	
    }

    @Override
    public void dispose() { //never called directly remember
        stage.dispose();
    }
}
