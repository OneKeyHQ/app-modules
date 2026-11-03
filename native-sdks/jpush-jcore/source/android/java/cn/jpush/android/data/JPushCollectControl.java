package cn.jpush.android.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class JPushCollectControl implements Serializable {
    private static final long serialVersionUID = -3135447319267654288L;
    private static final int IMEI = 1003;
    private static final int MAC = 1005;
    private static final int SSID = 1006;
    private static final int BSSID = 1007;
    private static final int CELL = 1008;
    public static final int IMSI = 1010;
    public static final int WIFI = 1012;
    public static final int APP_RUNNING = 1013;

    private final ArrayList<Integer> mEnableDatas = new ArrayList<Integer>();
    private final ArrayList<Integer> mDisableDatas = new ArrayList<Integer>();

    public JPushCollectControl(Builder builder) {
        if (builder != null) {
            mDisableDatas.addAll(builder.mDisableDatas);
            mEnableDatas.addAll(builder.mEnableDatas);
        }
    }

    public ArrayList<Integer> getEnableDatas() {
        return mEnableDatas;
    }

    public ArrayList<Integer> getDisableDatas() {
        return mDisableDatas;
    }

    public static class Builder {
        private final Set<Integer> mEnableDatas = new HashSet<Integer>();
        private final Set<Integer> mDisableDatas = new HashSet<Integer>();

        private void set(boolean enable, int code) {
            if (enable) {
                mDisableDatas.remove(code);
                mEnableDatas.add(code);
            } else {
                mEnableDatas.remove(code);
                mDisableDatas.add(code);
            }
        }

        public Builder imei(boolean enable) {
            set(enable, IMEI);
            return this;
        }

        public Builder mac(boolean enable) {
            set(enable, MAC);
            return this;
        }

        public Builder ssid(boolean enable) {
            set(enable, SSID);
            if (!enable) {
                wifi(false);
            }
            return this;
        }

        public Builder bssid(boolean enable) {
            set(enable, BSSID);
            if (!enable) {
                wifi(false);
            }
            return this;
        }

        public Builder cell(boolean enable) {
            set(enable, CELL);
            return this;
        }

        public Builder imsi(boolean enable) {
            set(enable, IMSI);
            return this;
        }

        public Builder wifi(boolean enable) {
            set(enable, WIFI);
            return this;
        }

        public JPushCollectControl build() {
            return new JPushCollectControl(this);
        }
    }
}
