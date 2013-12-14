package team.bluedream.handkerchief;

import java.util.ArrayList;
import java.util.List;

import team.bluedream.playerView.PlayerView;
import team.bluedream.wifihotspot.SocketServer;
import team.bluedream.wifihotspot.SocketServer.ServerMsgListener;
import team.bluedream.wifihotspot.WifiHotManager;
import team.bluedream.wifihotspot.WifiHotManager.OpretionsType;
import team.bluedream.wifihotspot.WifiHotManager.WifiBroadCastOperations;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class PlayerListActivity extends Activity implements
		WifiBroadCastOperations {

	private static final String TAG = "PlayerListActivity";

	private TimeThread timeThread;

	private List<Player> playerList;
	private Button refreshButton;
	private Button startGameButton;
	private TextView status_text;
	private MediaPlayer mPlayer;

	private boolean isFirst = true;
	private boolean onMusic;
	private ViewGroup playerListView;

	private Player playerInstance;

	private SocketServer server;
	private WifiHotManager wifihotManager;
	private Handler serverHandler;
	private Handler timeHandler;

	private Button throwHandkerchief;
	private Button lookBack;
	private int position;
	private int key;
	private int receiverID;
	private int timePast = 0;
	private boolean onStarting = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i(TAG, "into onCreate()");
		setContentView(R.layout.activity_playerlist);

		startGameButton = (Button) findViewById(R.id.start_game);
		refreshButton = (Button) findViewById(R.id.refresh);
		throwHandkerchief = (Button) findViewById(R.id.throw_handkerchief);
		lookBack = (Button) findViewById(R.id.look_back);

		playerListView = (ViewGroup) findViewById(R.id.player_list);
		status_text = (TextView) findViewById(R.id.status_text_playerlist);
		mPlayer = MediaPlayer.create(this, R.raw.sound);

		// 刷新player列表
		refreshButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				sendToClients();
				refreshPlayerList();
			}
		});

		// 扔手绢
		throwHandkerchief.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Player player= playerList.get(position);
				// player.setIdentity(PlayerStatus.IDENTITY_RECEIVER);
				// playerList.set(position, playerInstance);
				if (playerInstance.throwing == true) {
					Toast.makeText(
							getApplicationContext(),
							"已经扔到了"
									+ playerList.get(receiverID)
											.getPlayerName() + "身上",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (playerList.get(position).playerName
						.equals(playerInstance.playerName)) {
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
				playerInstance.setThrowing(true);
				receiverID = position;
			}
		});

		// 回头看
		lookBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (playerInstance.backCount > 0) {
					playerInstance.backCount--;
					if (playerInstance.getIdentity().equals(
							PlayerStatus.IDENTITY_RECEIVER)) {
						playerInstance
								.setIdentity(PlayerStatus.IDENTITY_WINNER);
						for (Player player : playerList) {
							if (player.playerName
									.equals(playerInstance.playerName)) {
								player.setBackCount(playerInstance.backCount);
								player.setIdentity(playerInstance.identity);
								break;
							}
						}
						for (Player player : playerList) {
							if (player.identity
									.equals(PlayerStatus.IDENTITY_TRUCKER)) {
								player.setIdentity(PlayerStatus.IDENTITY_LOSER);
							}
						}
						timeThread.over = true;
						sendToClients();
						displayResult();
						return;
					} else {
						Toast.makeText(getApplicationContext(), "什么都没有~",
								Toast.LENGTH_LONG).show();
					}
					for (Player player : playerList) {
						if (player.playerName.equals(playerInstance.playerName)) {
							player.setBackCount(playerInstance.backCount);
							player.setIdentity(playerInstance.identity);
							break;
						}
					}
				} else {
					Toast.makeText(getApplicationContext(), "不能再回头看了~",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		startGameButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 开始游戏
				if (server != null) {
					if (isFirst) {
						key = 0; // (int) Math.random() * playerList.size();
					}
					// key = 1;
					Log.d(TAG, playerList.size() + " " + key);
					playerList.get(key).setIdentity(
							PlayerStatus.IDENTITY_TRUCKER);
					if (playerList.get(key).playerName
							.equals(playerInstance.playerName)) {
						playerInstance
								.setIdentity(PlayerStatus.IDENTITY_TRUCKER);
					}
					switchUI();
					sendToClients();
					server.sendMsgToAllCLients("start");
					onStarting = true;
					timeThread = new TimeThread();
					new Thread(timeThread).start();
					if (onMusic) {
						System.out.println("in to start");
						mPlayer.start();
					}
				}
			}
		});

		initServerHandler();
		initTimeHandler();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String playerName = prefs.getString("playerName", Build.MODEL);
		onMusic = prefs.getBoolean("onMusic", true);
		wifihotManager = WifiHotManager.getInstance(PlayerListActivity.this,
				PlayerListActivity.this);
		wifihotManager.startAWifiHot("HAND" + playerName);
		initServer();
		// WifiHotManager.scanWifiHot();

		int imgId = Integer.parseInt(prefs.getString("playerImgId", "3"));

		// System.out.println("playerImgId:"+imgId);
		System.out.println("onMusic:"+onMusic);

		playerInstance = new Player(playerName);
		playerInstance.setId(imgId);
		playerList = new ArrayList<Player>();
		playerList.add(playerInstance);
		// refreshPlayerList();
	}

	private void switchUI() {
		if (playerInstance.identity.equals(PlayerStatus.IDENTITY_TRUCKER)) {
			this.refreshButton.setVisibility(View.GONE);
			this.startGameButton.setVisibility(View.GONE);
			this.lookBack.setVisibility(View.GONE);
			this.throwHandkerchief.setVisibility(View.VISIBLE);
		} else {
			this.refreshButton.setVisibility(View.GONE);
			this.startGameButton.setVisibility(View.GONE);
			this.lookBack.setVisibility(View.VISIBLE);
			this.throwHandkerchief.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Log.i(TAG, "into onBackPressed()");
			wifihotManager.unRegisterWifiScanBroadCast();
			wifihotManager.unRegisterWifiStateBroadCast();
			wifihotManager.disableWifiHot();
			this.finish();
			Log.i(TAG, "out onBackPressed()");
			return true;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "into onDestroy() ");
		if (server != null) {
			server.clearServer();
			server.stopListner();
			server = null;
			wifihotManager.disableWifiHot();
		}
		System.exit(0);
		Log.i(TAG, "out onDestroy() ");
		super.onDestroy();
	}

	// 创建服务器并初始化后开始监听
	private void initServer() {
		server = SocketServer.newInstance(12345, new ServerMsgListener() {
			Message msg = null;

			@Override
			public void handlerHotMsg(String hotMsg) {
				// connected = true;
				Log.i(TAG, "server accept msg");
				// msg = clientHandler.obtainMessage();
				msg = serverHandler.obtainMessage();
				msg.obj = hotMsg;
				msg.what = 1;
				serverHandler.sendMessage(msg);
			}

			@Override
			public void handlerErorMsg(String errorMsg) {
				// connected = false;
				Log.d(TAG, "server connected failed");
				// msg = clientHandler.obtainMessage();
				msg = serverHandler.obtainMessage();
				msg.obj = errorMsg;
				msg.what = 0;
				serverHandler.sendMessage(msg);
			}

			@Override
			public void handleConnectMsg(String connectMsg) {
				// TODO Auto-generated method stub
				Log.d(TAG, "server connected success");
				msg = serverHandler.obtainMessage();
				msg.obj = connectMsg;
				msg.what = 2;
				serverHandler.sendMessage(msg);
			}
		});
		server.beginListen();
	}

	// 监听服务器
	private void initServerHandler() {
		serverHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				if (msg.what == 0) {
					status_text.setText("服务器创建出错");
					Log.i(TAG,
							"into initServerHandler() handleMessage(Message msg) text = "
									+ msg.obj);
				} else if (msg.what == 1) {

					if (onStarting) {
						// 收到玩家发来的状态更新消息
						String text = (String) msg.obj;
						Gson gson = new Gson();
						Player player = gson.fromJson(text, Player.class);
						for (Player player_ : playerList) {
							if (player.playerName.equals(player_.playerName)) {
								// sendToClients();
								// return;
								player_.setBackCount(player.backCount);
								player_.setIdentity(player.identity);
							}
						}
						if (player.playerName.equals(playerInstance.playerName)) {
							playerInstance.setIdentity(player.identity);
						}
						if (player.identity
								.equals(PlayerStatus.IDENTITY_WINNER)) {
							playerList.get(key).setIdentity(
									PlayerStatus.IDENTITY_LOSER);
							timeThread.over = true;
							sendToClients();
							displayResult();
						}

					} else {
						// 收到客户端发来的加入游戏的用户的信息
						String text = (String) msg.obj;
						Gson gson = new Gson();
						Player player = gson.fromJson(text, Player.class);
						for (Player player_ : playerList) {
							if (player.playerName.equals(player_.playerName)) {
								sendToClients();
								return;
							}
						}
						playerList.add(player);
						refreshPlayerList();
						sendToClients();
					}

					Log.i(TAG,
							"into initServerHandler() handleMessage(Message msg) text = "
									+ msg.obj);
				} else if (msg.what == 2) {
					status_text.setText("服务器创建成功");
					Log.i(TAG,
							"into initServerHandler() handleMessage(Message msg) text = "
									+ msg.obj);
				}
			}
		};
	}

	private void displayResult() {
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
		if (onMusic) {
			mPlayer.pause();
			// mPlayer.prepareAsync();
			// mPlayer.reset();

		}
		AlertDialog.Builder builder = new Builder(PlayerListActivity.this);
		builder.setTitle("Result");
		builder.setMessage("Winner: " + winner + "\nLoser is: " + loser+",大家一起惩罚ta吧~~");
		builder.setPositiveButton("再来一局", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// 重新开始一局游戏
				initGame();
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	private void initGame() {
		refreshButton.setVisibility(View.VISIBLE);
		startGameButton.setVisibility(View.VISIBLE);
		throwHandkerchief.setVisibility(View.GONE);
		lookBack.setVisibility(View.GONE);
		mPlayer.seekTo(0);
		onStarting = false;
		isFirst = false;
		timePast = 0;
		for (Player player : playerList) {
			if (player.identity.equals(PlayerStatus.IDENTITY_LOSER)) {
				key = playerList.indexOf(player);
				player.init();
				player.setIdentity(PlayerStatus.IDENTITY_TRUCKER);
			} else {
				player.init();
			}
			if (playerInstance.playerName.equals(player.playerName)) {
				playerInstance.init();
				playerInstance.setIdentity(player.identity);
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(this);

				int imgId = Integer.parseInt(prefs
						.getString("playerImgId", "3"));
				playerInstance.setId(imgId);
			}
		}
		sendToClients();
		refreshPlayerList();
	}

	public void refreshPlayerList() {
		if (playerList == null || playerList.size() == 0) {
			return;
		}
		this.playerListView.removeAllViews();
		if (playerInstance.identity.equals(PlayerStatus.IDENTITY_TRUCKER)) {
			if (!playerInstance.throwing) {
				for (Player player : playerList) {
					Log.d(TAG, player.getPlayerName());
					PlayerView p = new PlayerView(this);
					// PlayerView p =
					// (PlayerView)getLayoutInflater().inflate(R.id.PlayerView,
					// null);
					p.setPlayerName(player.getPlayerName());
					p.setPlayerImg(player.getId());
					// p.setPlayerBackCounts(player.getBackCount());
					// p.setPlayerStatus(player.getIdentity());
					// p.setPlayerStatusVisable();
					if (player.color.equals(PlayerStatus.COLOR_BRIGHT)) {
						// p.setBackgroundColor(R.color.antiquewhite);
						// p.setBackgroundColor(getResources().getColor(R.color.blue));
						// p.setBackgroundResource(R.drawable.handkerchief);
						p.setBack();
					}
					this.playerListView.addView(p);
				}
			} else {
				for (Player player : playerList) {
					Log.d(TAG, player.getPlayerName());
					PlayerView p = new PlayerView(this);
					// PlayerView p =
					// (PlayerView)getLayoutInflater().inflate(R.id.PlayerView,
					// null);
					p.setPlayerName(player.getPlayerName());
					p.setPlayerImg(player.getId());
					// p.setPlayerBackCounts(player.getBackCount());
					// p.setPlayerStatus(player.getIdentity());
					// p.setPlayerStatusVisable();
					if (player.identity.equals(PlayerStatus.IDENTITY_RECEIVER)) {
						p.setBack();
					}
					this.playerListView.addView(p);
				}
			}

		} else {
			for (Player player : playerList) {
				Log.d(TAG, player.getPlayerName());
				PlayerView p = new PlayerView(this);
				// PlayerView p =
				// (PlayerView)getLayoutInflater().inflate(R.id.PlayerView,
				// null);
				p.setPlayerName(player.getPlayerName());
				p.setPlayerImg(player.getId());
				// p.setPlayerBackCounts(player.getBackCount());
				// p.setPlayerStatus(player.getIdentity());
				if (player.color.equals(PlayerStatus.COLOR_BRIGHT)) {
					// p.setBackgroundColor(R.color.antiquewhite);
					p.setBack();
				}
				this.playerListView.addView(p);
				// this.playerListView.addView(p);
			}
			playerListView.invalidate();
		}
	}

	// 游戏主循环
	private void initTimeHandler() {
		timeHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if (msg.what == 1 && playerList != null) {
					for (Player player : playerList) {
						player.setColor(PlayerStatus.COLOR_GRAY);
					}
					int id = msg.getData().getInt("time");
					position = id % playerList.size();
					playerList.get(id % playerList.size()).setColor(
							PlayerStatus.COLOR_BRIGHT);
					sendToClients();
					refreshPlayerList();

					if (playerList.get(key).throwing) {
						timePast++;
						if (timePast > Gloable.timeOfPast) {
							for (Player player : playerList) {
								if (player.identity
										.equals(PlayerStatus.IDENTITY_RECEIVER)) {
									player.setIdentity(PlayerStatus.IDENTITY_LOSER);
								} else if (player.identity
										.equals(PlayerStatus.IDENTITY_TRUCKER)) {
									player.setIdentity(PlayerStatus.IDENTITY_WINNER);
								}
							}
							sendToClients();
							displayResult();
						}
					}
				}
				if (msg.what == 2 && playerList != null) {
					for (Player player : playerList) {
						/*
						 * if(player.identity.equals(PlayerStatus.IDENTITY_RECEIVER
						 * )){ player.setIdentity(PlayerStatus.IDENTITY_LOSER);
						 * } else
						 * if(player.identity.equals(PlayerStatus.IDENTITY_TRUCKER
						 * )){ player.setIdentity(PlayerStatus.IDENTITY_WINNER);
						 * }
						 */
						if (player.identity
								.equals(PlayerStatus.IDENTITY_TRUCKER)) {
							player.setIdentity(PlayerStatus.IDENTITY_LOSER);
						}
					}
					sendToClients();
					displayResult();
				}
				super.handleMessage(msg);
			}

		};
	}

	// 发送更新后的列表到各个用户
	private void sendToClients() {
		// Gson gson = new Gson();
		String text = new Gson().toJson(playerList);
		if (server != null) {
			server.sendMsgToAllCLients(text);
		}
	}

	@Override
	public void disPlayWifiScanResult(List<ScanResult> wifiList) {

		Log.i(TAG, "into disPlayWifiScanResult");
		// wifiHotM.checkConnectHotIsEnable("WIFI", wifiList);
		// this.wifiList = wifiList;
		// wifiHotM.unRegisterWifiScanBroadCast();
		// refreshWifiList(wifiList);
		Log.i(TAG, "out disPlayWifiScanResult " + wifiList);

	}

	@Override
	public boolean disPlayWifiConResult(boolean result, WifiInfo wifiInfo) {

		Log.i(TAG, "into disPlayWifiConResult");
		// String ip = "";
		// wifiHotM.setConnectStatu(false);
		// wifiHotM.unRegisterWifiStateBroadCast();
		// wifiHotM.unRegisterWifiConnectBroadCast();
		// initClient(ip);
		// Log.i(TAG, "out disPlayWifiConResult");
		return false;
	}

	@Override
	public void operationByType(OpretionsType type, String SSID) {
		Log.i(TAG, "into operationByType,Type = " + type);

		// if (type == OpretionsType.CONNECT) {
		// wifiHotM.connectToHotpot(SSID, wifiList, Global.PASSWORD);
		// } else if (type == OpretionsType.SCAN) {
		// wifiHotM.scanWifiHot();
		// }

		Log.i(TAG, "out operationByType");

	}

	public class TimeThread implements Runnable {

		private int time = 0;
		public boolean over = false;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (time < Gloable.timeOfTotal && !over) {
				try {
					Thread.sleep(1500);// 线程暂停10秒，单位毫秒
					Message message = new Message();
					message.what = 1;
					Bundle bundle = new Bundle();
					bundle.putInt("time", time);
					message.setData(bundle);
					timeHandler.sendMessage(message);
					time++;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (!over) {
				try {
					Thread.sleep(1000);// 线程暂停1秒
					Message message = new Message();
					message.what = 2;
					timeHandler.sendMessage(message);
					time++;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.over = true;
			}
		}

	}
}
