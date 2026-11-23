package android.os;
public class Binder implements IBinder {
    private IInterface owner;
    private String descriptor;

    public Binder() {}

    public static int getCallingUid() {
        return 10000;
    }

    public void attachInterface(IInterface owner, String descriptor) {
        this.owner = owner;
        this.descriptor = descriptor;
    }

    @Override
    public IInterface queryLocalInterface(String descriptor) {
        if (this.descriptor != null && this.descriptor.equals(descriptor)) {
            return this.owner;
        }
        return null;
    }

    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        return true;
    }

    @Override
    public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        return onTransact(code, data, reply, flags);
    }
}
