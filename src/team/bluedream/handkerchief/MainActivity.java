package team.bluedream.handkerchief;


import android.app.Activity;
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
	//TextView status_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		createGame = (Button) findViewById(R.id.create_game);
		joinGame = (Button) findViewById(R.id.join_game);
		aboutGame = (Button) findViewById(R.id.about_game);
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

		aboutGame.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, SettingsActivity.class);  
		        startActivity(intent);
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
