package cn.jpush.android.api;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class JPushMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private int protoType = 0;
    private int action = 0;
    private String alias;
    private Set<String> tags;
    private Map<String, Object> pros;
    private String checkTag;
    private int errorCode;
    private boolean tagCheckStateResult;
    private boolean tagCheckOperator;
    private int sequence;
    private String mobileNumber;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Map<String, Object> getPros() {
        return pros;
    }

    public void setPros(Map<String, Object> pros) {
        this.pros = pros;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getCheckTag() {
        return checkTag;
    }

    public void setCheckTag(String checkTag) {
        this.checkTag = checkTag;
    }

    public boolean getTagCheckStateResult() {
        return tagCheckStateResult;
    }

    public void setTagCheckStateResult(boolean tagCheckStateResult) {
        this.tagCheckStateResult = tagCheckStateResult;
    }

    public boolean isTagCheckOperator() {
        return tagCheckOperator;
    }

    public void setTagCheckOperator(boolean tagCheckOperator) {
        this.tagCheckOperator = tagCheckOperator;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public int getProtoType() {
        return protoType;
    }

    public void setProtoType(int protoType) {
        this.protoType = protoType;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "JPushMessage{alias='" + alias
            + "', tags=" + tags
            + ", pros=" + pros
            + ", checkTag='" + checkTag
            + "', errorCode=" + errorCode
            + ", tagCheckStateResult=" + tagCheckStateResult
            + ", isTagCheckOperator=" + tagCheckOperator
            + ", sequence=" + sequence
            + ", mobileNumber=" + mobileNumber + '}';
    }
}
