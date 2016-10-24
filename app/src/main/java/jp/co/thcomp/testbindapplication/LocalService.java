package jp.co.thcomp.testbindapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class LocalService extends Service {
    private static final String TAG = LocalService.class.getSimpleName();
    private int mValue = 0;

    private ILocalService.Stub mStub = new ILocalService.Stub() {
        @Override
        public int addValue(int value) throws RemoteException {
            Log.i(TAG, "Before: mValue = " + mValue);
            mValue += value;
            Log.i(TAG, "After: mValue = " + mValue);
            return mValue;
        }
    };

    public LocalService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, LocalService.class.getSimpleName() + ".onBind");
        return mStub.asBinder();
    }
}
