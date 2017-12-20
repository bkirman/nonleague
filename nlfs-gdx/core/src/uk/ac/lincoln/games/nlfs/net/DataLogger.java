package uk.ac.lincoln.games.nlfs.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;


/**
 * Class to handle logging research data on the remote server
 * It should store unsent data packets until there is a connection
 * Created by ben on 24/10/17.
 */

public class DataLogger {
    public static String PACKET_FILE = "packets.dat";
    public static int MAX_UNSENT_PACKETS = 30;
    public static ArrayList<DataPacket> packets;//unsent data packets

    public static DataPacket current_packet;

    public void DataLogger() {
        packets = new ArrayList<DataPacket>();
        load();
        current_packet = new DataPacket();
    }

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

    /**
     * Add a packet to the list to be sent
     * @param dp
     */
    public static void add(DataPacket dp) {
        //if the unsent packets > X, we shouldn't keep adding more - it will fill up the device, we can presume they are preventing transfer.
        if(packets.size()>MAX_UNSENT_PACKETS) return;
        packets.add(dp);
        //TODO try to send here? when do we send?
    }
    /**
     * Methods to load/save unsent packets from local storage
     */
    public static void save() {
        Json json = new Json();
        String o = json.toJson(packets);
        FileHandle file = Gdx.files.local(PACKET_FILE);
        file.writeString(com.badlogic.gdx.utils.Base64Coder.encodeString(o), false);
        Gdx.app.log("SAVE","Unsent Packets Saved to "+PACKET_FILE);
    }

    public static void load() {
        Gdx.app.log("LOAD", "Loading Unsent Packets from "+Gdx.files.getLocalStoragePath());

        Json json = new Json();
        String s = "";
        FileHandle file = Gdx.files.local(PACKET_FILE);
        if (file != null && file.exists()) {
            s = file.readString();
        }
        if(s.isEmpty())
            packets = new ArrayList<DataPacket>();
        else
            packets = json.fromJson(ArrayList.class, com.badlogic.gdx.utils.Base64Coder.decodeString(s));
    }
}
