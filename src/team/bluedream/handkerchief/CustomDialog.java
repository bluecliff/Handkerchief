package team.bluedream.handkerchief;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
                                                                                                                                                     
public class CustomDialog {
                                                                                                                                                
    private View mParent;
    private PopupWindow mPopupWindow;
    private LinearLayout mRootLayout; 
    private LayoutParams mLayoutParams;
                                                                                                                                                
    //PopupWindow������һ��ParentView�����Ա�������������
    public CustomDialog(Context context, View parent) {
                                                                                                                                                    
        mParent = parent;
                                                                                                                                                    
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);     
                                                                                                                                                    
        //���ز����ļ�
        mRootLayout = (LinearLayout)mInflater.inflate(R.layout.custom_dialog, null); 
                                                                                                                                                           
        mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    } 
                                                                                                                                                
    //����Dailog�ı���
    public void setTitle(String title) {
        TextView mTitle = (TextView)mRootLayout.findViewById(R.id.CustomDlgTitle);
        mTitle.setText(title);
    }
                                                                                                                                                
    //����Dailog����������
    public void setMessage(String message) {
        TextView mMessage = (TextView)mRootLayout.findViewById(R.id.CustomDlgContentText);
        mMessage.setText(message);
    }
                                                                                                                                                
    //����Dailog�ġ�ȷ������ť
    public void setPositiveButton(String text,OnClickListener listener ) {
        final Button buttonOK = (Button)mRootLayout.findViewById(R.id.CustomDlgButtonOK);
        buttonOK.setText(text);
        buttonOK.setOnClickListener(listener);
        buttonOK.setVisibility(View.VISIBLE);
    }
                                                                                                                                                
    //����Dailog�ġ�ȡ������ť
   /* public void setNegativeButton(String text,OnClickListener listener ) {
        final Button buttonCancel = (Button)mRootLayout.findViewById(R.id.CustomDlgButtonCancel);
        buttonCancel.setText(text);
        buttonCancel.setOnClickListener(listener);
        buttonCancel.setVisibility(View.VISIBLE);
    }*/
                                                                                                                                                
    //�滻Dailog�ġ����塱����
    public void setContentLayout(View layout) {
                                                                                                                                                    
        TextView mMessage = (TextView)mRootLayout.findViewById(R.id.CustomDlgContentText);
        mMessage.setVisibility(View.GONE);
                                                                                                                                                    
        LinearLayout contentLayout = (LinearLayout)mRootLayout.findViewById(R.id.CustomDlgContentView);     
        contentLayout.addView(layout);             
    }
                                                                                                                                                
    //����Dailog�ĳ���
    public void setLayoutParams(int width, int height) {
        mLayoutParams.width  = width;
        mLayoutParams.height = height;
    }
                                                                                                                                                
    //��ʾDailog
    public void show() {
                                                                                                                                                
        if(mPopupWindow == null) {
            mPopupWindow = new PopupWindow(mRootLayout, mLayoutParams.width,mLayoutParams.height);
            mPopupWindow.setFocusable(true);
        }
                                                                                                                                                    
        mPopupWindow.showAtLocation(mParent, Gravity.CENTER, Gravity.CENTER, Gravity.CENTER);
    }
                                                                                                                                                
    //ȡ��Dailog����ʾ
    public void dismiss() {
                                                                                                                                                    
        if(mPopupWindow == null) {
            return;
        }
                                                                                                                                                    
        mPopupWindow.dismiss();
    }
}