package uk.ac.lincoln.games.nlfs.ui;

import uk.ac.lincoln.games.nlfs.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class TutorialWindow extends Actor {
	 private Table table;
	 private static int _width = 500;
	 private static int _height = 800;
     private Label text;
     private boolean setup = false;

	    public TutorialWindow (String title_text, String body_text, String button_text) {
	        text = new Label(body_text,Assets.skin);
	        text.setWrap(true);
            this.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

            table = new Table();
            table.pad(20);
	        table.setBackground(Assets.skin.getDrawable("tutorial"));
	        
			table.add(new Label(title_text,Assets.skin,"pagetitle")).center().expandX();
			table.row();
	        table.add(text).fill().expand().top().padLeft(10f);
	        table.row();
	        
	        TextButton button = new TextButton(button_text, Assets.skin);
	        table.add(button).width(380).height(85).center();
			table.row();


	        table.setWidth(_width);
	        table.setHeight(_height);

	        setTouchable(Touchable.enabled);
	        setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

	        addListener(new InputListener(){
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                	remove();
                	return true;
                }
            });

	    }
	    @Override
		public void act(float delta) {
			super.act(delta);
			if(!setup) { //set up initial animation

				this.setPosition((this.getStage().getViewport().getWorldWidth() / 2) - _width / 2,1500);
				this.addAction(Actions.moveTo(this.getX(), (this.getStage().getViewport().getWorldHeight() / 2) - _height / 2, 0.2f));

				setup=true;
			}
		}

	    @Override
	    public void draw (Batch batch, float parentAlpha) {


			table.setPosition(this.getX(),this.getY());
			table.draw(batch, parentAlpha);

	    }


}
