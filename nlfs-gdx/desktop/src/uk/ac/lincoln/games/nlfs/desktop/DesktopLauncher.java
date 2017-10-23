package uk.ac.lincoln.games.nlfs.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import uk.ac.lincoln.games.nlfs.NonLeague;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 1280;
		config.width = 720;
		new LwjglApplication(new NonLeague("abcdef0123456789"), config);//this would need to be unique if ever distributed on desktop/web
	}
}
