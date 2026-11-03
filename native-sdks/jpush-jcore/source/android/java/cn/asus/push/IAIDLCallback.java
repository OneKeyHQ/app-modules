package cn.asus.push;

import android.os.IInterface;

public interface IAIDLCallback extends IInterface {
   void call(DataBuffer var1) throws android.os.RemoteException;
}
