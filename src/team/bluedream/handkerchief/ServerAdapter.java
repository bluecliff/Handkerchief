package team.bluedream.handkerchief;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ServerAdapter extends BaseAdapter {

	public List<ScanResult> mResults;

	private Context mContext;

	public ServerAdapter(List<ScanResult> results, Context mContext) {

		this.mResults = results;
		this.mContext = mContext;
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mResults.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mResults.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		System.out.println("into getView()");
		TextView nameTxt = null;
		TextView levelTxt = null;
		if (arg1 == null) {
			arg1 = View.inflate(mContext, R.layout.server_layout, null);
		}
		nameTxt = (TextView) arg1.findViewById(R.id.hotName);
		levelTxt = (TextView) arg1.findViewById(R.id.hotLevel);
		nameTxt.setText(mResults.get(arg0).SSID);
		levelTxt.setText("Level :" + mResults.get(arg0).level);
		System.out.println("out getView()");
		return arg1;
	}
	
	public void refreshData(List<ScanResult> results) {
		System.out.println("into refreshData(List<ScanResult> results) results.size =" + results.size());
		
		this.mResults.clear();
		this.mResults = results;
		this.notifyDataSetChanged();
		System.out.println("out refreshData(List<ScanResult> results)"+this.mResults.size());
	}

	public void clearData() {

		if (mResults != null && mResults.size() > 0) {
			mResults.clear();
			mResults = null;
		}
	}
}
