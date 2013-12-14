package team.bluedream.handkerchief;

import java.util.List;

import team.bluedream.playerView.PlayerView;
import team.bluedream.wifihotspot.Global;
import team.bluedream.wifihotspot.SocketClient;
import team.bluedream.wifihotspot.SocketClient.ClientMsgListener;
import team.bluedream.wifihotspot.WifiHotManager;
import team.bluedream.wifihotspot.WifiHotManager.OpretionsType;
import team.bluedream.wifihotspot.WifiHotManager.WifiBroadCastOperations;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ServerListActivity extends Activity implements
		WifiBroadCastOperations {

	private static final String TAG = "ServerListActivity";

	private int position;
	private int receiverID;

	private Button refresh;
	private Button lookBackButton;
	private Button throwButton;
	private ListView listView;
	private TextView statusText;

	private Handler clientHandler;
	private List<ScanResult> wifiList;
	private WifiHotManager wifiHotManager;
	private SocketClient client;
	private ServerAdapter adapter;
	private String mSSID;
	private Player instance;

	private ViewGroup playerListView;
	private List<Player> playerList;

	private boolean onStarting = false;
	private View statusBar1;
	private View statusBar2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serverlist);
		refresh = (Button) findViewById(R.id.finding_server);
		throwButton = (Button) findViewById(R.id.throw_handkerchief);
		lookBackButton = (Button) findViewById(R.id.look_back);
		listView = (ListView) findViewById(R.id.server_list);
		statusText = (TextView) findViewById(R.id.status_connect);

		statusBar1 = findViewById(R.id.status_bar1);
		statusBar2 = findViewById(R.id.status_bar2);

		playerListView = (ViewGroup) findViewById(R.id.player_list_client);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String playerName = prefs.getString("playerName", Build.MODEL);
		int imgId = Integer.parseInt(prefs.getString("playerImgId", "3"));
		instance = new Player(playerName);
		instance.setId(imgId);

		throwButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (instance.throwing == true) {
					Toast.makeText(
							getApplicationContext(),
							"已经扔到了"
									+ playerList.get(receiverID)
											.getPlayerName() + "身上",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (playerList.get(position).playerName
						.equals(instance.playerName)) {
					Toast.makeText(getApplicationContext(), "不能扔自己身上~~",
							Toast.LENGTH_LONG).show();
					return;
				}
				playerList.get(position).setIdentity(
						PlayerStatus.IDENTITY_RECEIVER);
				Toast.makeText(
						getApplicationContext(),
						"扔到了" + playerList.get(position).getPlayerName() + "身上",
						Toast.LENGTH_LONG).show();
				instance.setThrowing(true);
				receiverID = position;
				client.sendMsg(new Gson().toJson(instance));
				client.sendMsg(new Gson().toJson(playerList.get(position)));
			}
		});
		lookBackButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (instance.backCount > 0) {
					instance.backCount--;
					if (instance.identity
							.equals(PlayerStatus.IDENTITY_RECEIVER)) {
						instance.setIdentity(PlayerStatus.IDENTITY_WINNER);
					} else {
						Toast.makeText(getApplicationContext(), "什么都没有~",
								Toast.LENGTH_LONG).show();
					}
					client.sendMsg(new Gson().toJson(instance));
				} else {
					Toast.makeText(getApplicationContext(), "不能再回头看了~",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		refresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				statusText.setText(R.string.finding_server);
				wifiHotManager.scanWifiHot();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ScanResult result = wifiList.get(position);
				mSSID = result.SSID;
				statusText.setText("连接中...");
				Log.i(TAG, "into  onItemClick() SSID= " + result.SSID);
				wifiHotManager.connectToHotpot(result.SSID, wifiList,
						Global.PASSWORD);
				Log.i(TAG, "out  onItemClick() SSID= " + result.SSID);
			}
		});

		initClientHandler();

		wifiHotManager = WifiHotManager.getInstance(ServerListActivity.this,
				ServerListActivity.this);
		wifiHotManager.scanWifiHot();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (adapter != null) {
			adapter.clearData();
			adapter = null;
		}

		if (client != null) {
			client.clearClient();
			client.stopAcceptMessage();
			client = null;
			wifiHotManager.deleteMoreCon(mSSID);
		}
		Log.i(TAG, "out onDestroy() ");
		System.exit(0);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.d(TAG, "into onResume");
		super.onResume();
		Log.d(TAG, "out onRsume");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Log.i(TAG, "into onBackPressed()");
			wifiHotManager.unRegisterWifiScanBroadCast();
			wifiHotManager.unRegisterWifiStateBroadCast();
			wifiHotManager.disableWifiHot();
			this.finish();
			Log.i(TAG, "out onBackPressed()");
			return true;
		}
		return true;
	}

	@Override
	public void disPlayWifiScanResult(List<ScanResult> wifiList) {
		// TODO Auto-generated method stub
		Log.i(TAG, "into disPlayWifiScanResult" + wifiList);
		wifiHotManager.checkConnectHotIsEnable("HAND", wifiList);
		this.wifiList = wifiList;
		wifiHotManager.unRegisterWifiScanBroadCast();
		refreshWifiList(wifiList);
		Log.i(TAG, "out disPlayWifiScanResult " + wifiList);
	}

	@Override
	public boolean disPlayWifiConResult(boolean result, WifiInfo wifiInfo) {
		// TODO Auto-generated method stub
		Log.i(TAG, "into disPlayWifiConResult");
		String ip = "";
		wifiHotManager.setConnectStatu(false);
		wifiHotManager.unRegisterWifiStateBroadCast();
		wifiHotManager.unRegisterWifiConnectBroadCast();
		initClient(ip);
		Log.i(TAG, "out disPlayWifiConResult");
		return false;
	}

	@Override
	public void operationByType(OpretionsType type, String SSID) {
		// TODO Auto-generated method stub
		Log.i(TAG, "into operationByType,Type = " + type);
		if (type == OpretionsType.CONNECT) {
			wifiHotManager.connectToHotpot(SSID, wifiList, Global.PASSWORD);
		} else if (type == OpretionsType.SCAN) {
			wifiHotManager.scanWifiHot();
		}
		Log.i(TAG, "out operationByType");
	}

	private void initClient(String IP) {
		client = SocketClient.newInstance("192.168.43.1", 12345,
				new ClientMsgListener() {
					Message msg = null;

					@Override
					public void handlerErorMsg(String errorMsg) {
						Log.d(TAG, "client ");
						msg = clientHandler.obtainMessage();
						msg.obj = errorMsg;
						msg.what = 0;
						clientHandler.sendMessage(msg);
					}

					@Override
					public void handlerHotMsg(String hotMsg) {
						Log.i(TAG, "client handlerHotMsg");
						msg = clientHandler.obtainMessage();
						msg.obj = hotMsg;
						msg.what = 1;
						clientHandler.sendMessage(msg);
					}

					@Override
					public void handlerConnectMsg(String connectMsg) {
						// TODO Auto-generated method stub
						Log.i(TAG, "client handlerHotMsg");
						msg = clientHandler.obtainMessage();
						msg.obj = connectMsg;
						msg.what = 2;
						clientHandler.sendMessage(msg);
					}

					@Override
					public void handlerStartMsg(String startMsg) {
						// TODO Auto-generated method stub
						msg = clientHandler.obtainMessage();
						msg.what = 3;
						msg.obj = startMsg;
						clientHandler.sendMessage(msg);
					}
				});
		client.connectServer();
	}

	private void refreshWifiList(List<ScanResult> results) {
		Log.i(TAG, "intorefreshWifiList");
		if (null == adapter) {
			Log.i(TAG, "into refreshWifiListadapter is null");
			adapter = new ServerAdapter(results, this);
			listView.setAdapter(adapter);
		} else {
			Log.i(TAG, "into refreshWifiList;adapter is not null");
			adapter.refreshData(results);
		}
		statusText.setText("服务器列表");
		Log.i(TAG, "out refreshWifiList");
	}

	private void initClientHandler() {
		clientHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.i(TAG,
						"into initClientHandler() handleMessage(Message msg)");
				if (msg.what == 0) {
					statusText.setText("服务器连接出错");
					Log.i(TAG,
							"into initClientHandler() handleMessage(Message msg) text ="
									+ msg.obj);
				} else if (msg.what == 1) {
					// 收到服务器发来的用户状态信息
					String text = (String) msg.obj;
					Log.d(TAG, text);
					playerList = new Gson().fromJson(text,
							new TypeToken<List<Player>>() {
							}.getType());
					refreshPlayerListView();
					// /////////////
				} else if (msg.what == 2) {
					statusText.setText("服务器连接成功");

					Log.i(TAG,
							"into initClientHandler() handleMessage(Message msg) text ="
									+ msg.obj);
					// 跳转到playlist页面
					// saveData();
					// Intent intent = new
					// Intent(ServerListActivity.this,ClientListActivity.class);
					// startActivity(intent);
					client.sendMsg(new Gson().toJson(instance));
					switchUI(2);
					Log.i(TAG,
							"into initClientHandler() handleMessage(Message msg) text ="
									+ msg.obj);
				} else if (msg.what == 3) {
					onStarting = true;
				}
			}
		};
	}

	private void switchUI(int key) {
		if (key == 1) {

		} else if (key == 2) {
			// refreshPlayerListView();
			this.listView.setVisibility(View.GONE);
			this.playerListView.setVisibility(View.VISIBLE);
			this.statusBar1.setVisibility(View.GONE);
			this.statusBar2.setVisibility(View.VISIBLE);
		}
	}

	private void refreshPlayerListView() {
		if (playerList == null || playerList.size() == 0) {
			return;
		}
		this.playerListView.removeAllViews();
		if (onStarting) {
			if (instance.identity.equals(PlayerStatus.IDENTITY_TRUCKER)) {
				if (!instance.throwing) {
					for (Player player : playerList) {
						Log.d(TAG, player.getPlayerName());

						PlayerView p = new PlayerView(this);

						p.setPlayerName(player.getPlayerName());
						p.setPlayerImg(player.getId());
						// p.setPlayerBackCounts(player.getBackCount());
						// p.setPlayerStatus(player.getIdentity());
						if (player.identity
								.equals(PlayerStatus.IDENTITY_WINNER)) {
							displayResult();
							return;
						}

						if (player.color.equals(PlayerStatus.COLOR_BRIGHT)) {
							// p.setBackgroundColor(R.color.antiquewhite);
							// p.setBackgroundColor(getResources().getColor(
							// R.color.blue));
							p.setBack();
							position = playerList.indexOf(player);
						}
						// p.setPlayerStatusVisable();
						this.playerListView.addView(p);
					}
				} else {
					for (Player player : playerList) {
						Log.d(TAG, player.getPlayerName());

						PlayerView p = new PlayerView(this);

						p.setPlayerName(player.getPlayerName());
						p.setPlayerImg(player.getId());
						// p.setPlayerBackCounts(player.getBackCount());
						// p.setPlayerStatus(player.getIdentity());
						if (player.identity
								.equals(PlayerStatus.IDENTITY_WINNER)) {
							displayResult();
							return;
						}
						if(player.identity.equals(PlayerStatus.IDENTITY_RECEIVER)){
							p.setBack();
							position = playerList.indexOf(player);
						}
						this.playerListView.addView(p);
					}
				}

			} else {
				for (Player player : playerList) {
					if (player.playerName.equals(instance.playerName)) {
						instance.setIdentity(player.identity);
						break;
					}
				}
				for (Player player : playerList) {
					PlayerView p = new PlayerView(this);
					p.setPlayerName(player.getPlayerName());
					p.setPlayerImg(player.getId());
					// p.setPlayerBackCounts(player.getBackCount());
					// p.setPlayerStatus(player.getIdentity());
					if (player.identity.equals(PlayerStatus.IDENTITY_WINNER)) {
						displayResult();
						return;
					}
					if (player.color.equals(PlayerStatus.COLOR_BRIGHT)) {
						// p.setBackgroundColor(R.color.antiquewhite);
						// p.setBackgroundColor(getResources().getColor(
						// R.color.blue));
						p.setBack();
					}
					this.playerListView.addView(p);
				}
			}

		} else {
			for (Player player : playerList) {
				Log.d(TAG, player.getPlayerName());

				PlayerView p = new PlayerView(this);

				p.setPlayerName(player.getPlayerName());
				p.setPlayerImg(player.getId());
				// p.setPlayerBackCounts(player.getBackCount());
				// p.setPlayerStatus(player.getIdentity());
				if (player.playerName.equals(instance.playerName)) {
					instance.setBackCount(player.backCount);
					instance.setIdentity(player.identity);
					if (instance.identity.equals(PlayerStatus.IDENTITY_TRUCKER)) {
						throwButton.setVisibility(View.VISIBLE);
						lookBackButton.setVisibility(View.GONE);
					} else {
						throwButton.setVisibility(View.GONE);
						lookBackButton.setVisibility(View.VISIBLE);
					}
				}
				this.playerListView.addView(p);
			}
		}
		this.playerListView.invalidate();
	}

	private void displayResult() {
		onStarting = false;
		String winner = "";
		String loser = "";
		for (Player player : playerList) {
			if (player.identity.equals(PlayerStatus.IDENTITY_WINNER)) {
				winner = player.getPlayerName();
			}
			if (player.identity.equals(PlayerStatus.IDENTITY_LOSER)) {
				loser = player.getPlayerName();
			}
		}

		AlertDialog.Builder builder = new Builder(ServerListActivity.this);
		builder.setTitle("Result");
		builder.setMessage("Winner：" + winner + "\nLoser:" + loser);
		builder.setPositiveButton("确认", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				initGame();
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	private void initGame() {
		// this.onStarting=false;
		this.onStarting = false;
		client.onStartListener = false;
		instance.init();
		
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		
		int imgId = Integer.parseInt(prefs.getString("playerImgId", "3"));
		instance.setId(imgId);
		client.sendMsg(new Gson().toJson(instance));
	}

}
