package com.xunce.gpstest;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xunce.gpstest.utils.StringDecodeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class MainActivity extends AppCompatActivity  implements QRCodeView.Delegate{
    private ZXingView mQRCodeView;
    private String IMEI;
    private Button button;
    private TextView textView;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQRCodeView = (ZXingView)findViewById(R.id.zxingview);
        checkPermission();
        mQRCodeView.startCamera();
        mQRCodeView.startSpot();
        mQRCodeView.setResultHandler(this);
        button = (Button)findViewById(R.id.button);
        textView = (TextView)findViewById(R.id.text);
        editText = (EditText)findViewById(R.id.edit_text);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().length()==15){
                    IMEI = editText.getText().toString();
                }else {
                    Toast.makeText(MainActivity.this,"IMEI号错误",Toast.LENGTH_SHORT).show();
                }
                checkGPS();
            }
        });

    }
    private void checkPermission() {
        int permisson = PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permisson != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
    private  void  checkGPS(){
        HttpRequest.httpGetWithUrl("http://api.xiaoan110.com:8083/v1/imeiData/" + IMEI, null, new HttpCallback() {
            @Override
            public void httpCallBack(final String result) {
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"扫码成功,正在查询",Toast.LENGTH_SHORT).show();
                        textView.setText(dealWithResult(result));
                    }
                });
            }
        });
    }
    @Override
    public void onScanQRCodeSuccess(String result) {
        String curIMEI = StringDecodeUtil.getIMEIFromMango(result);
        if (curIMEI == null){
            curIMEI = StringDecodeUtil.getIMEIFromSim808(result);
        }
        if(curIMEI==null || curIMEI.isEmpty()){
            vibrate();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("IMEI号错误");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mQRCodeView.startCamera();
                    mQRCodeView.startSpot();
                }
            }).create().show();
        }else {
            if(!curIMEI.equals(IMEI)) {
                editText.setText(curIMEI);
                IMEI = curIMEI;
                checkGPS();
            }
        }


        mQRCodeView.startCamera();
        mQRCodeView.startSpot();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }
    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e("e", "打开相机出错");
    }

    private String dealWithResult(String string){
        try{
            JSONObject jsonObject = new JSONObject(string);
            if(jsonObject.has("code")){
                Toast.makeText(this,"查询错误",Toast.LENGTH_SHORT).show();
                return null;
            }else {
                String result = "";
                if (jsonObject.has("imei")){
                    result =result+"IMEI:"+jsonObject.get("imei")+"\n";
                }
                if(jsonObject.has("latitude")){
                    result = result + "lat:"+jsonObject.getDouble("latitude")+"\n";
                }
                if(jsonObject.has("longitude")){
                    result = result + "lon:"+jsonObject.getDouble("longitude");
                }
                return result;
            }
        }catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(this,"查询错误",Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
