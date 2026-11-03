package cn.jpush.android.local;

import java.nio.ByteBuffer;

public class JPushResponse {
    public int cmd;
    public long rid;
    public long rquestId;
    public ByteBuffer body;
    public long uid;

    public JPushResponse(int cmd, long rid, long rquestId, ByteBuffer body) {
        this.cmd = cmd;
        this.rid = rid;
        this.rquestId = rquestId;
        this.body = body;
    }

    public JPushResponse(int cmd, long rid, long rquestId, long uid, ByteBuffer body) {
        this.cmd = cmd;
        this.rid = rid;
        this.rquestId = rquestId;
        this.uid = uid;
        this.body = body;
    }
}
