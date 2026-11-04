package cn.jpush.android.service;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import cn.jiguang.android.IDataShare;
import cn.jiguang.core.JCoreStorage;
import cn.jiguang.internal.JCoreInternalHelper;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataShare extends IDataShare.Stub {
    private static final Map<String, IDataShare> aidlMap = new ConcurrentHashMap<String, IDataShare>();
    private static volatile IDataShare instance;
    private static volatile boolean binding;
    private static volatile int sTestCallingUid = -1;
    private static volatile int sTestMyUid = -1;

    public static void setTestCallerUids(int callingUid, int myUid) {
        sTestCallingUid = callingUid;
        sTestMyUid = myUid;
    }

    public static void enforceSameAppCaller() {
        if (sTestCallingUid >= 0 && sTestMyUid >= 0) {
            if (sTestCallingUid != sTestMyUid) {
                throw new SecurityException("Unauthorized Binder caller UID: " + sTestCallingUid);
            }
            return;
        }
        int callingUid;
        int myUid;
        try {
            callingUid = Binder.getCallingUid();
            myUid = Process.myUid();
        } catch (Throwable ignored) {
            return;
        }
        if (myUid > 0 && callingUid != myUid) {
            throw new SecurityException("Unauthorized Binder caller UID: " + callingUid);
        }
    }

    public static IDataShare getInstance(String processName) {
        if (processName != null && !processName.isEmpty()) {
            IDataShare bound = aidlMap.get(processName);
            if (bound != null) {
                return bound;
            }
        }
        if (instance == null) {
            synchronized (DataShare.class) {
                if (instance == null) {
                    instance = new DataShare();
                }
            }
        }
        return instance;
    }

    public static void init(IDataShare aidl, String processName) {
        if (aidl != null) {
            instance = aidl;
            if (processName != null && !processName.isEmpty()) {
                aidlMap.put(processName, aidl);
            }
        }
        binding = false;
    }

    public static boolean isBinding() {
        return binding;
    }

    public static void setBinding() {
        binding = true;
    }

    @Override
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        enforceSameAppCaller();
        return super.onTransact(code, data, reply, flags);
    }

    @Override
    public IBinder getBinderByType(String type, String action) throws RemoteException {
        enforceSameAppCaller();
        if (type != null && !type.isEmpty()) {
            IDataShare bound = aidlMap.get(type);
            if (bound != null) {
                return bound.asBinder();
            }
        }
        return this;
    }

    @Override
    public void onAction(String type, String action, Bundle bundle) throws RemoteException {
        enforceSameAppCaller();
        JCoreInternalHelper.getInstance().directHandle(JCoreStorage.getAppContext(null), type, action, bundle);
    }

    @Override
    public String bind(IDataShare aidl, String type) throws RemoteException {
        enforceSameAppCaller();
        String key = (type != null && !type.isEmpty()) ? type : "default";
        if (aidl != null) {
            aidlMap.put(key, aidl);
            if (instance == null) {
                instance = aidl;
            }
        }
        return key;
    }

    @Override
    public Bundle execute(String sdkType, String action, Bundle bundle) throws RemoteException {
        enforceSameAppCaller();
        return JCoreInternalHelper.getInstance().directHandle(
            JCoreStorage.getAppContext(null),
            sdkType,
            action,
            bundle
        );
    }
}
