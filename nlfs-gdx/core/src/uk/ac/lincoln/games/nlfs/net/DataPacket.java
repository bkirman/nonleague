package uk.ac.lincoln.games.nlfs.net;

import com.badlogic.gdx.Gdx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.ac.lincoln.games.nlfs.logic.GameState;

/**
 * DataPacket objects contain relevant game data to be used in research
 * Created by ben on 24/10/17.
 */

public class DataPacket {
    private JSONObject json;
    private boolean complete,sent;
    public DataPacket() {
        json = new JSONObject();
        complete = false;
        sent = false;
        try {//init with time of creation, device ID and NLFS version
            json.put("time", System.currentTimeMillis());
            json.put("id", GameState.DEVICE_ID);
            json.put("v",GameState.VERSION);
            json.put("team",GameState.player_team.name);
        } catch (JSONException e) {
            Gdx.app.log("DataPacket:init",e.getMessage());
        }
    }

    public void addRituals(ArrayList<String> rituals, long time_spent) {
        JSONObject ritual_json = new JSONObject();
        JSONArray ritual_arr = new JSONArray();
        try {
            ritual_json.put("time", time_spent);
            for (String r : rituals) {
                ritual_arr.put(r);
            }
            ritual_json.put("selected:",ritual_arr);
            json.put("rituals",ritual_json);
        }catch (JSONException e) {
            Gdx.app.log("DataPacket:addRituals",e.getMessage());
        }
    }

    public void addResult(String opponent, boolean home, int player_score, int opponent_score,int new_league_position) {
        String wld ="";
        if (player_score>opponent_score) wld = "w";
        if (player_score<opponent_score) wld = "l";
        if (player_score==opponent_score) wld = "d";
        JSONObject result = new JSONObject();
        try{
            result.put("wld",wld);
            result.put("opp",opponent);
            result.put("home",home);
            result.put("p",player_score);
            result.put("o",opponent_score);
            result.put("pos",new_league_position);
            json.put("result",result);
        }catch (JSONException e) {
            Gdx.app.log("DataPacket:addResult",e.getMessage());
        }
        if(json.has("rituals")&&json.has("result")) complete = true;
    }

    public String getData() {
        return json.toString();
    }

    public boolean isComplete() {return complete;}
    public boolean isSent() {return sent;}
    public void markSent() {sent = true;}
}
