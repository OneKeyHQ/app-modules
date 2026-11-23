package android.os;
public interface IBinder {
    IInterface queryLocalInterface(String descriptor);
    boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException;
}
