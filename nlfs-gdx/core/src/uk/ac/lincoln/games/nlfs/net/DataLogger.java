package uk.ac.lincoln.games.nlfs.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.async.AsyncTask;

import java.util.ArrayList;


/**
 * Class to handle logging research data on the remote server
 * It should store unsent data packets until there is a connection
 * Created by ben on 24/10/17.
 */

public class DataLogger {
    public static String PACKET_FILE = "packets.dat";
    public static String SERVER_ADDR = "http://non-league.football/log.php";
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
            if(!d.isComplete()||d.isSent())continue;
            final String jsondata = d.getData();
            Timer.instance().scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    //make HTTP request
                    Gdx.app.log("sendData","Attempting to send request to "+SERVER_ADDR);
                    HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
                    final Net.HttpRequest httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.POST).url(SERVER_ADDR).build();
                    httpRequest.setContent(jsondata);
                    httpRequest.setHeader("Content-Type","application/json");
                    Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
                        @Override
                        public void handleHttpResponse(Net.HttpResponse httpResponse) {
                            Gdx.app.log("sendData","Response: "+httpResponse.getResultAsString());
                        }

                        @Override
                        public void failed(Throwable t) {
                            Gdx.app.log("sendData","Failed:"+t.getLocalizedMessage());
                        }

                        @Override
                        public void cancelled() {

                        }
                    });

                }
            }, 0);
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
        sendData(packets);
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
