package jp.co.thcomp.testbindapplication;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import jp.co.thcomp.testbindserviceapplication.IRemoteService;

public class MainActivity extends AppCompatActivity {

    private EditText mEdtValue;
    private ILocalService mLocalService;
    private IRemoteService mRemoteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEdtValue = (EditText)findViewById(R.id.edtParamIntValue);
        findViewById(R.id.btnStartBindServiceInSameAPK).setOnClickListener(mClickListener);
        findViewById(R.id.btnStartBindServiceInOtherAPK).setOnClickListener(mClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mLocalService != null){
            unbindService(mServiceConnectionForSameAPK);
            mLocalService = null;
        }
        if(mRemoteService != null){
            unbindService(mServiceConnectionForOtherAPK);
            mRemoteService = null;
        }
    }

    private void startBindServiceInSameAPK(){
        if(mLocalService == null){
            Intent intent = new Intent();
            intent.setClass(this, LocalService.class);
            if(!bindService(intent, mServiceConnectionForSameAPK, Service.BIND_AUTO_CREATE)){
                Toast.makeText(getApplicationContext(), "Fail to bind service in Same APK", Toast.LENGTH_SHORT).show();
            }
        }else{
            addValue(mLocalService);
        }
    }

    private void startBindServiceInOtherAPK(){
        if(mRemoteService == null){
            Intent intent = new Intent();
            intent.setClassName("jp.co.thcomp.testbindserviceapplication", "jp.co.thcomp.testbindserviceapplication.RemoteService");
            if(!bindService(intent, mServiceConnectionForOtherAPK, Service.BIND_AUTO_CREATE)){
                Toast.makeText(getApplicationContext(), "Fail to bind service in Other APK", Toast.LENGTH_SHORT).show();
            }
        }else{
            addValue(mRemoteService);
        }
    }

    private void addValue(IInterface interfaceInstance){
        int value = 0;
        try {
            try {
                value = Integer.valueOf(mEdtValue.getText().toString());
            }catch (NumberFormatException e){
            }

            int nextValue = 0;
            if(interfaceInstance instanceof ILocalService){
                nextValue = ((ILocalService)interfaceInstance).addValue(value);
            }else if(interfaceInstance instanceof IRemoteService){
                nextValue = ((IRemoteService)interfaceInstance).addValue(value);
            }

            Toast.makeText(getApplicationContext(), "After value is " + nextValue, Toast.LENGTH_LONG).show();
        }catch(RemoteException e){
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            switch (id){
                case R.id.btnStartBindServiceInSameAPK:
                    startBindServiceInSameAPK();
                    break;
                case R.id.btnStartBindServiceInOtherAPK:
                    startBindServiceInOtherAPK();
                    break;
            }
        }
    };

    private ServiceConnection mServiceConnectionForSameAPK = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mLocalService = ILocalService.Stub.asInterface(service);
            addValue(mLocalService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 処理なし
        }
    };

    private ServiceConnection mServiceConnectionForOtherAPK = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemoteService = IRemoteService.Stub.asInterface(service);
            addValue(mRemoteService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 処理なし
        }
    };
}
