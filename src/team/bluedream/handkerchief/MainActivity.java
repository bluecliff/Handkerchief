package team.bluedream.handkerchief;


import android.app.Activity;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	public static final String TAG = "MainActivity";
	//private WifiHotManager wifiHotManager;
	//private SocketServer server;
	//private Handler serverHandler;
	
	Button createGame;
	Button joinGame;
	Button aboutGame;
	Button setupGame;
	//TextView status_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		createGame = (Button) findViewById(R.id.create_game);
		joinGame = (Button) findViewById(R.id.join_game);
		aboutGame = (Button) findViewById(R.id.about_game);
		setupGame = (Button) findViewById(R.id.setup_game);
		//status_text = (TextView) findViewById(R.id.status_text_main);

		createGame.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent intent = new
				Intent intent = new Intent(MainActivity.this,PlayerListActivity.class);
				startActivity(intent);
				//initServerHandler();
				
			}
		});

		joinGame.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,ServerListActivity.class);
				startActivity(intent);
			}
		});

		setupGame.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, SettingsActivity.class);  
		        startActivity(intent);
			}
		});
		
		aboutGame.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				final CustomDialog dailog = new CustomDialog(MainActivity.this,findViewById(android.R.id.content).getRootView());
				dailog.setTitle("关于丢手绢");
				String msg="\t\t\t“丢手绢”是传统民间少儿游戏在移动平台上的再现，是一款多人(3人以上)联机互动游戏，适合朋友聚会或其它多人场合，帮助大家在找寻乐趣的同时重温那些逝去的童年时光。\n\t\t\t游戏规则简单：进入游戏后一人建立游戏，其他人加入游戏后，由做服务器的手机决定游戏是否开始。游戏开始后，将产生一个“丢掷者”，其可以选择在35秒内的某个时刻将手绢丢在某个玩家的身后，若此玩家在被丢掷后的10秒内还没选择“回头看”发现手绢,则本轮游戏被丢掷的玩家失败，接受大家的惩罚；若“丢掷者”在35秒内未能丢出手绢或者手绢丢出后10秒内被丢掷者发现，则本轮“丢掷者”失败，下一轮游戏由本轮失败者扮演“丢掷者”。所有非“丢掷者”在游戏开始后可以随时选择“回头看”查看身后是否有手绢，但每回合只有两次回头看的机会，所以把握好时机是游戏胜利的关键，当然你也可以通过偷偷查看“丢掷者”的表情动作来猜测其是否已经丢出了手绢～或者动用你神秘的第六感吧～祝你玩的愉快！\n\t\t\tBlueDream团队倾情奉上。";
				dailog.setMessage(msg);
				dailog.setPositiveButton("OK", new View.OnClickListener() {       
				    @Override
				    public void onClick(View v) {
				        dailog.dismiss();        
				    }
				});
				dailog.show();
			}
		});
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
