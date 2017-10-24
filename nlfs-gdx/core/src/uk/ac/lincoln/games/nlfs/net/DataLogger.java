package uk.ac.lincoln.games.nlfs.net;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;

/**
 * Class to handle logging research data on the remote server
 * It should store unsent data packets until there is a connection
 * Created by ben on 24/10/17.
 */

public class DataLogger {

    public static void sendData(ArrayList<DataPacket> data) {
        ArrayList<DataPacket> done = new ArrayList<DataPacket>();
        for(DataPacket d:data) {
            if(!d.isComplete()&&!d.isSent())continue;

            //TODO: send data here
            Gdx.app.log("DataLogger",d.getData());
            d.markSent();
            done.add(d);
        }
        data.removeAll(done);

    }
}
