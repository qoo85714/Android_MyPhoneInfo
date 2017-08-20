package tw.jason.app.helloworld.myphoneinfo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TelephonyManager tmgr;
    private ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_PHONE_NUMBERS,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);

        }else {
            init();
        }

    }
    private void  init(){
        img = (ImageView)findViewById(R.id.img);
        tmgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        String  deviceid  =tmgr.getDeviceId();
        Log.i("brad",deviceid);
        String n1 = tmgr.getLine1Number();
        String n2 = tmgr.getSimSerialNumber();
        String n3 = tmgr.getSubscriberId();
        Log.i("brad","n1=" +n1);
        Log.i("brad","n2=" +n2);
        Log.i("brad","n3=" +n3);
        MyCallListener mcl = new MyCallListener();
        tmgr.listen(mcl, PhoneStateListener.LISTEN_CALL_STATE);
        //tmgr.listen(mcl, PhoneStateListener.LISTEN_SERVICE_STATE);

        ContentResolver cr =  getContentResolver();
        Cursor c= cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                        ,ContactsContract.CommonDataKinds.Phone.NUMBER},null,null,null);
        while (c.moveToNext()){
            String name =c.getString(0);// c.getString(c.getColumnIndex(""));
            String tel = c.getString(1);//c.getString(c.getColumnIndex(""));
            Log.i("brad",name+":"+tel);
        }
        Cursor c2 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        c2.moveToNext();
        String file = c2.getString(c2.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
        //Log.i("brad",file);
        Bitmap bmp = BitmapFactory.decodeFile(file);
        img.setImageBitmap(bmp);

        Cursor c3 = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        while (c3.moveToNext()){
            int number = c3.getColumnIndex(CallLog.Calls.NUMBER);
            int type = c3.getColumnIndex(CallLog.Calls.TYPE);
            int date = c3.getColumnIndex(CallLog.Calls.DATE);
            String strNumber = c3.getString(number);
            int intType = c3.getInt(type);
            String strDate = c3.getString(date);

            Date date1 = new Date(Long.parseLong(strDate));
            int hh = date1.getHours();
            int mm = date1.getMinutes();
            int ss = date1.getSeconds();
            String hms = hh+":"+mm+":"+ss;

            switch(intType){
                case CallLog.Calls.INCOMING_TYPE:
                    Log.i("brad", strNumber + " : " + hms);
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    Log.i("brad", strNumber + " : " + hms);
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    Log.i("brad", strNumber + " : " + hms);
                    break;
                case CallLog.Calls.REJECTED_TYPE:
                    Log.i("brad", strNumber + " : " + hms);
                    break;
                default:
                    Log.i("brad", strNumber + " : " + hms);
                    break;
            }
        }

}
    private class MyCallListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            super.onCallStateChanged(state, incomingNumber);

            switch (state){
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i("brad","idle");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i("brad","ring:"+incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i("brad","offhook"+incomingNumber);
                    break;
            }
        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);
            Log.i("brad","service:"+serviceState);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }
}
