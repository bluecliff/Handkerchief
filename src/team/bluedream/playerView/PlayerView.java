package team.bluedream.playerView;

import team.bluedream.handkerchief.R;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlayerView extends LinearLayout {

	private TextView playerName;
	private TextView playerImg;
	private LinearLayout box;
	public static final int[] images = new int[]{
			R.drawable.img0,
			R.drawable.img1,
			R.drawable.img2,
			R.drawable.img3,
			R.drawable.img4,
			R.drawable.img5,
			R.drawable.img6,
			R.drawable.img7,
			R.drawable.img8,
			R.drawable.img9,
	};

	// private TextView playerStatus;

	public PlayerView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		((Activity) getContext()).getLayoutInflater().inflate(R.layout.player,
				this);
		playerName = (TextView) findViewById(R.id.player_name);
		playerImg = (TextView)findViewById(R.id.player_img);
		box = (LinearLayout)findViewById(R.id.box);
		// playerStatus=(TextView)findViewById(R.id.player_status);
		// playerBackCounts=(TextView)findViewById(R.id.player_back_count);
	}

	public PlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.player, this);
		playerName = (TextView) findViewById(R.id.player_name);
		// playerStatus=(TextView)findViewById(R.id.player_status);
		playerImg = (TextView) findViewById(R.id.player_img);
	}

	public void setPlayerName(String playerName) {
		this.playerName.setText(playerName);
	}
	
	public void setBack(){
		this.box.setBackgroundResource(R.drawable.handkerchief);
	}
	
	public void setPlayerImg(int id){
		this.playerImg.setBackgroundResource(images[id]);//(this.getResources().getDrawable(images[id]));
	}
/*	public void setPlayerBackCounts(int playerBackCounts) {
		this.playerBackCounts.setText("" + playerBackCounts);
	}*/

	// public void setPlayerStatus(String playerStatus) {
	// this.playerStatus.setText(playerStatus);
	// }

	/*
	 * public void setPlayerStatusVisable(){
	 * this.playerStatus.setVisibility(View.VISIBLE); }
	 */
}
