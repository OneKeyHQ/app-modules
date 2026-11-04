package cn.jpush.android.service;

import android.os.Bundle;
import android.os.IBinder;
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
    public IBinder getBinderByType(String type, String action) throws RemoteException {
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
        JCoreInternalHelper.getInstance().directHandle(JCoreStorage.getAppContext(null), type, action, bundle);
    }

    @Override
    public String bind(IDataShare aidl, String type) throws RemoteException {
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
        return JCoreInternalHelper.getInstance().directHandle(
            JCoreStorage.getAppContext(null),
            sdkType,
            action,
            bundle
        );
    }
}
