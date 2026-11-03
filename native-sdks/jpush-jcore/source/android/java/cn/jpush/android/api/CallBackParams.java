package cn.jpush.android.api;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class CallBackParams implements Serializable {
    private static final long serialVersionUID = 1L;
    public String alias;
    public Set<String> tags;
    public Map<String, Object> pros;
    public TagAliasCallback tagAliasCallBack;
    public long sendTime;
    public int protoType = 0;
    public int action = 0;
    public int sequence;

    public CallBackParams(String alias, Set<String> tags, TagAliasCallback callback, long sendTime, int protoType, int action) {
        this.alias = alias;
        this.tags = tags;
        this.tagAliasCallBack = callback;
        this.sendTime = sendTime;
        this.protoType = protoType;
        this.action = action;
    }

    public CallBackParams(int sequence, String alias, long sendTime, int protoType, int action) {
        this.sequence = sequence;
        this.alias = alias;
        this.sendTime = sendTime;
        this.protoType = protoType;
        this.action = action;
    }

    public CallBackParams(int sequence, Set<String> tags, long sendTime, int protoType, int action) {
        this.sequence = sequence;
        this.tags = tags;
        this.sendTime = sendTime;
        this.protoType = protoType;
        this.action = action;
    }

    public CallBackParams(int sequence, Map<String, Object> pros, long sendTime, int protoType, int action) {
        this.sequence = sequence;
        this.pros = pros;
        this.sendTime = sendTime;
        this.protoType = protoType;
        this.action = action;
    }
}
