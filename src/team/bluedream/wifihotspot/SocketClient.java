package team.bluedream.wifihotspot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class SocketClient {
	static Socket client;

	static BufferedReader in;

	private static SocketClient socketClient;

	private static final String TAG = "SocketClient";

	private String site;

	private int port;

	private boolean onGoinglistner = true;
	
	public boolean onStartListener = false;

	private ClientMsgListener clientListener;

	public static interface ClientMsgListener {

		public void handlerErorMsg(String errorMsg);

		public void handlerHotMsg(String hotMsg);
		
		public void handlerConnectMsg(String connectMsg);
		
		public void handlerStartMsg(String startMsg);

	}

	public static synchronized SocketClient newInstance(String site, int port,
			ClientMsgListener clientListener) {

		if (socketClient == null) {
			socketClient = new SocketClient(site, port, clientListener);
		}
		Log.i(TAG, "socketClient =" + socketClient);
		return socketClient;
	}

	public void setMsgListener(ClientMsgListener listener) {
		this.clientListener = listener;
	}

	private SocketClient(String site, int port, ClientMsgListener clientListener) {

		this.site = site;
		this.port = port;
		this.clientListener = clientListener;
	}

	public void connectServer() {
		Log.i(TAG, "into connectServer()");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					client = new Socket(site, port);
					Log.i(TAG, "Client is created! site:" + site + " port:" + port);
					acceptMsg();
					clientListener.handlerConnectMsg(Global.INT_CLIENT_SUCCESS);
				} catch (UnknownHostException e) {
					e.printStackTrace();
					clientListener.handlerErorMsg(Global.INT_CLIENT_FAIL);
					Log.d(TAG, "UnknownHostException");
				} catch (IOException e) {
					e.printStackTrace();
					clientListener.handlerErorMsg(Global.INT_CLIENT_FAIL);
					Log.d(TAG, "IOException");
				}
			}
		}).start();
		Log.i(TAG, "out connectServer()");
	}

	public String sendMsg(final String msg) {
		Log.i(TAG, "into sendMsgsendMsg(final ChatMessage msg)  msg =" + msg);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (client != null && client.isConnected()) {
						if (!client.isOutputShutdown()) {
							PrintWriter out = new PrintWriter(client.getOutputStream());
							out.println(msg);
							// out.println(JsonUtil.obj2Str(msg));
							out.flush();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					Log.d(TAG, "client snedMsg error!");
				}
			}
		}).start();
		return "";
	}

	private void closeConnection() {
		try {
			if (client != null && client.isConnected()) {
				client.close();
				client = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void acceptMsg() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (onGoinglistner) {
					if (client != null) {
						if (client.isConnected()) {
							if (!client.isInputShutdown()) {
								try {
									in = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
									String chatMsg = in.readLine();
									Log.i(TAG, "into acceptMsg()  chatMsg =" + chatMsg);
									if (chatMsg != null && !chatMsg.equals("")) {
										if(onStartListener){
											clientListener.handlerHotMsg(chatMsg);
										}
										else{
											if(chatMsg.equals("start")){
												clientListener.handlerStartMsg(chatMsg);
												onStartListener=true;
											}
											else{
												clientListener.handlerHotMsg(chatMsg);
											}
										}
										
										
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}).start();
	}

	public void clearClient() {
		closeConnection();
	}

	public void stopAcceptMessage() {
		onGoinglistner = false;
	}
}