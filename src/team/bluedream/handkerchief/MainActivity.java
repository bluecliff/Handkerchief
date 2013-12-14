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
				dailog.setTitle("���ڶ��־�");
				String msg="�����־�Ǵ�ͳ����ٶ���Ϸ���ƶ�ƽ̨�ϵ����֣���һ�����(3������)����������Ϸ���ʺ������Ѿۻ�ʱ���һ���棬������Щ������ȥ��ͯ��ʱ�⡣\n\t��Ϸ����ܼ򵥣�������Ϸ��һ�˽�����Ϸ�������˼�����Ϸ���������������ֻ�������Ϸ�Ŀ�ʼ����Ϸ��ʼ�����Ȼ����һ����Ͷ���ߡ���ѡ����35���ڵ�ĳ��ʱ�����־��ĳ����ҵ������������ڱ�Ͷ����10���ڻ�ûѡ�񡰻�ͷ���������־�,�����ұ��غ�Ϊʧ���ߣ����ܴ�ҵĳͷ�������Ͷ���ߡ���35����δ�ܶ����־���߶�����10���ڼ������֣���Ͷ���ߡ���Ϊʧ���ߡ���һ����Ϸ�ɱ���ʧ���߰��ݡ�Ͷ���ߡ������зǡ�Ͷ���ߡ�����Ϸ��ʼ�������ʱѡ�񡰻�ͷ�����鿴����Ƿ����־��ÿ�غ�ֻ�����λ�ͷ���Ļ��ᣬ���԰��պá���ͷ������ʱ������Ϸʤ���Ĺؼ�����Ȼ��Ҳ����͵͵�鿴��Ͷ���ߡ��ı��鶯�����²����Ƿ��Ѿ��������־��ף�������죡\n\tBlueDream�Ŷ�������ϡ�";
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
