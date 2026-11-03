package cn.jiguang.dy;
import android.content.Context;
// Compile-only signatures of a class absent from both original SDK JARs. Never packaged or executed.
public class JDynamicLoadBase {
    public static native JDynamicLoadBase getInstance();
    public native void updatePluin(Context context,int type,String value);
    public native void deletePlugin(Context context,int type);
    public native void updateImmediately(Context context,int type,String value);
    public native Integer getProtocolVersion();
}
