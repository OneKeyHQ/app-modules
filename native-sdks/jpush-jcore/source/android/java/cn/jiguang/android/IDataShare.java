package cn.jiguang.android;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

public interface IDataShare extends IInterface {
    String DESCRIPTOR = "cn.jiguang.android.IDataShare";

    IBinder getBinderByType(String sdkType, String action) throws RemoteException;

    void onAction(String sdkType, String action, Bundle bundle) throws RemoteException;

    Bundle execute(String sdkType, String action, Bundle bundle) throws RemoteException;

    String bind(IDataShare dataShare, String processName) throws RemoteException;

    abstract class Stub extends Binder implements IDataShare {
        static final int TRANSACTION_getBinderByType = 1;
        static final int TRANSACTION_onAction = 2;
        static final int TRANSACTION_execute = 3;
        static final int TRANSACTION_bind = 4;

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static IDataShare asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin instanceof IDataShare) {
                return (IDataShare) iin;
            }
            return new Proxy(obj);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == INTERFACE_TRANSACTION) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            switch (code) {
                case TRANSACTION_getBinderByType: {
                    data.enforceInterface(DESCRIPTOR);
                    String sdkType = data.readString();
                    String action = data.readString();
                    IBinder result = this.getBinderByType(sdkType, action);
                    reply.writeNoException();
                    reply.writeStrongBinder(result);
                    return true;
                }
                case TRANSACTION_onAction: {
                    data.enforceInterface(DESCRIPTOR);
                    String sdkType = data.readString();
                    String action = data.readString();
                    Bundle bundle = data.readInt() != 0 ? Bundle.CREATOR.createFromParcel(data) : null;
                    this.onAction(sdkType, action, bundle);
                    return true;
                }
                case TRANSACTION_execute: {
                    data.enforceInterface(DESCRIPTOR);
                    String sdkType = data.readString();
                    String action = data.readString();
                    Bundle bundle = data.readInt() != 0 ? Bundle.CREATOR.createFromParcel(data) : null;
                    Bundle result = this.execute(sdkType, action, bundle);
                    reply.writeNoException();
                    if (result != null) {
                        reply.writeInt(1);
                        result.writeToParcel(reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                }
                case TRANSACTION_bind: {
                    data.enforceInterface(DESCRIPTOR);
                    IDataShare remote = asInterface(data.readStrongBinder());
                    String processName = data.readString();
                    String result = this.bind(remote, processName);
                    reply.writeNoException();
                    reply.writeString(result);
                    return true;
                }
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        private static class Proxy implements IDataShare {
            private final IBinder remote;

            Proxy(IBinder remote) {
                this.remote = remote;
            }

            @Override
            public IBinder asBinder() {
                return remote;
            }

            @Override
            public IBinder getBinderByType(String sdkType, String action) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeString(sdkType);
                    data.writeString(action);
                    remote.transact(TRANSACTION_getBinderByType, data, reply, 0);
                    reply.readException();
                    return reply.readStrongBinder();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void onAction(String sdkType, String action, Bundle bundle) throws RemoteException {
                Parcel data = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeString(sdkType);
                    data.writeString(action);
                    if (bundle != null) {
                        data.writeInt(1);
                        bundle.writeToParcel(data, 0);
                    } else {
                        data.writeInt(0);
                    }
                    remote.transact(TRANSACTION_onAction, data, null, IBinder.FLAG_ONEWAY);
                } finally {
                    data.recycle();
                }
            }

            @Override
            public Bundle execute(String sdkType, String action, Bundle bundle) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeString(sdkType);
                    data.writeString(action);
                    if (bundle != null) {
                        data.writeInt(1);
                        bundle.writeToParcel(data, 0);
                    } else {
                        data.writeInt(0);
                    }
                    remote.transact(TRANSACTION_execute, data, reply, 0);
                    reply.readException();
                    return reply.readInt() != 0 ? Bundle.CREATOR.createFromParcel(reply) : null;
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public String bind(IDataShare dataShare, String processName) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeStrongBinder(dataShare != null ? dataShare.asBinder() : null);
                    data.writeString(processName);
                    remote.transact(TRANSACTION_bind, data, reply, 0);
                    reply.readException();
                    return reply.readString();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }
        }
    }
}
