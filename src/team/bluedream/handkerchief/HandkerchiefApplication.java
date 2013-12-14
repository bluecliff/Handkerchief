package team.bluedream.handkerchief;

import team.bluedream.wifihotspot.SocketClient;
import team.bluedream.wifihotspot.SocketServer;
import team.bluedream.wifihotspot.WifiHotManager;
import android.app.Application;

public class HandkerchiefApplication extends Application {

	public SocketServer server;

	public SocketClient client;

	public WifiHotManager wifiHotManager;

	public Player instance;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
}
