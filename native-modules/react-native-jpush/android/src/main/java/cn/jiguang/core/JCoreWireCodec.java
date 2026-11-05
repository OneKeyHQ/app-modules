package cn.jiguang.core;

import android.os.Build;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class JCoreWireCodec {
    public static final int CMD_REGISTER = 0;
    public static final int CMD_LOGIN = 1;
    public static final int CMD_HEARTBEAT = 2;
    public static final int CMD_PUSH_MESSAGE = 3;
    public static final int CMD_MESSAGE_ACK = 4;
    public static final int CMD_DEVICE_TOKEN = 13;
    public static final int CMD_CTRL = 25;
    public static final int CMD_MOBILE_NUMBER = 26;
    public static final int CMD_THIRD_TOKEN = 27;
    public static final int CMD_TAG_OP = 28;
    public static final int CMD_ALIAS_OP = 29;
    public static final int CMD_PROPERTY_OP = 38;
    public static final int CMD_PUSH_CTRL = 40;

    public static final int VER_REGISTER = 29;
    public static final int VER_LOGIN = 27;
    public static final int VER_HEARTBEAT = 8;
    public static final int VER_PUSH_MESSAGE = 6;
    public static final int VER_MESSAGE_ACK = 3;
    public static final int VER_DEVICE_TOKEN = 3;
    public static final int VER_CTRL = 1;
    public static final int VER_MOBILE_NUMBER = 1;
    public static final int VER_THIRD_TOKEN = 2;
    public static final int VER_TAG_OP = 1;
    public static final int VER_ALIAS_OP = 1;
    public static final int VER_PROPERTY_OP = 1;
    public static final int VER_PUSH_CTRL = 1;

    public static final int ALG_AES_CBC = 1;
    public static final int ALG_SM4_CBC = 2;

    public static final byte SIS_MAGIC_U = (byte) 'U';
    public static final byte SIS_MAGIC_M = (byte) 'M';
    public static final byte SIS_MAGIC_G = (byte) 'G';

    public static final int CLIENT_HEADER_BYTES = 24;
    public static final int SERVER_HEADER_BYTES = 20;

    private static volatile long lastRegisterSeed = 1L;
    private static volatile int defaultAlgorithm = ALG_AES_CBC;

    private JCoreWireCodec() {
    }

    public static long getLastRegisterSeed() {
        return lastRegisterSeed;
    }

    public static void setLastRegisterSeed(long seed) {
        if (seed > 0L) {
            lastRegisterSeed = seed;
        }
    }

    public static int getDefaultAlgorithm() {
        return defaultAlgorithm;
    }

    public static void setDefaultAlgorithm(int algorithm) {
        defaultAlgorithm = (algorithm == ALG_SM4_CBC) ? ALG_SM4_CBC : ALG_AES_CBC;
    }

    public static final class DecodedFrame {
        public final int version;
        public final int command;
        public final int algorithm;
        public final int flag;
        public final long requestId;
        public final int sessionId;
        public final long uid;
        public final int statusCode;
        public final byte[] body;

        public DecodedFrame(int version, int command, long requestId, long uid, int statusCode, byte[] body) {
            this(version, command, ALG_AES_CBC, 0, requestId, 0, uid, statusCode, body);
        }

        public DecodedFrame(
            int version,
            int command,
            long requestId,
            int sessionId,
            long uid,
            int statusCode,
            byte[] body
        ) {
            this(version, command, ALG_AES_CBC, 0, requestId, sessionId, uid, statusCode, body);
        }

        public DecodedFrame(
            int version,
            int command,
            int algorithm,
            int flag,
            long requestId,
            int sessionId,
            long uid,
            int statusCode,
            byte[] body
        ) {
            this.version = version;
            this.command = command;
            this.algorithm = algorithm;
            this.flag = flag;
            this.requestId = requestId;
            this.sessionId = sessionId;
            this.uid = uid;
            this.statusCode = statusCode;
            this.body = body != null ? body : new byte[0];
        }
    }

    public static final class RegisterRequest {
        public final String key;
        public final String apkVersion;
        public final String clientInfo;
        public final int platform;
        public final String extKey;
        public final int regBusiness;
        public final String accountId;
        public final String packageName;
        public final String appKey;
        public final String channel;
        public final String deviceId;

        public RegisterRequest(
            String key,
            String apkVersion,
            String clientInfo,
            int platform,
            String extKey,
            int regBusiness,
            String accountId,
            String packageName,
            String appKey,
            String channel,
            String deviceId
        ) {
            this.key = key != null ? key : "";
            this.apkVersion = apkVersion != null ? apkVersion : "";
            this.clientInfo = clientInfo != null ? clientInfo : "";
            this.platform = platform;
            this.extKey = extKey != null ? extKey : "";
            this.regBusiness = regBusiness;
            this.accountId = accountId != null ? accountId : "";
            this.packageName = packageName != null ? packageName : "";
            this.appKey = appKey != null ? appKey : "";
            this.channel = channel != null ? channel : "";
            this.deviceId = deviceId != null ? deviceId : "";
        }
    }

    public static final class RegisterResponse {
        public final int statusCode;
        public final long uid;
        public final String password;
        public final String registrationId;
        public final String deviceId;

        public RegisterResponse(int statusCode, long uid, String password, String registrationId) {
            this(statusCode, uid, password, registrationId, "");
        }

        public RegisterResponse(int statusCode, long uid, String password, String registrationId, String deviceId) {
            this.statusCode = statusCode;
            this.uid = uid;
            this.password = password != null ? password : "";
            this.registrationId = registrationId != null ? registrationId : "";
            this.deviceId = deviceId != null ? deviceId : "";
        }
    }

    public static final class LoginRequest {
        public final String passwordMd5;
        public final String sdkVerInfo;
        public final String appKey;
        public final int platform;
        public final int loginFlag;
        public final int netType;
        public final String deviceId;
        public final String countryCode;
        public final int initPushSdk;
        public final String gSdkTypeInfo;

        public LoginRequest(
            String passwordMd5,
            String sdkVerInfo,
            String appKey,
            int platform,
            int loginFlag,
            int netType,
            String deviceId,
            String countryCode,
            int initPushSdk,
            String gSdkTypeInfo
        ) {
            this.passwordMd5 = passwordMd5 != null ? passwordMd5 : "";
            this.sdkVerInfo = sdkVerInfo != null ? sdkVerInfo : "";
            this.appKey = appKey != null ? appKey : "";
            this.platform = platform;
            this.loginFlag = loginFlag;
            this.netType = netType;
            this.deviceId = deviceId != null ? deviceId : "";
            this.countryCode = countryCode != null ? countryCode : "";
            this.initPushSdk = initPushSdk;
            this.gSdkTypeInfo = gSdkTypeInfo != null ? gSdkTypeInfo : "";
        }
    }

    public static final class LoginResponse {
        public final int statusCode;
        public final int sessionId;
        public final int serverVersion;
        public final String errorDesc;
        public final int serverTimestamp;

        public LoginResponse(int statusCode, int sessionId, int serverVersion, int serverTimestamp) {
            this(statusCode, sessionId, serverVersion, "", serverTimestamp);
        }

        public LoginResponse(
            int statusCode,
            int sessionId,
            int serverVersion,
            String errorDesc,
            int serverTimestamp
        ) {
            this.statusCode = statusCode;
            this.sessionId = sessionId;
            this.serverVersion = serverVersion;
            this.errorDesc = errorDesc != null ? errorDesc : "";
            this.serverTimestamp = serverTimestamp;
        }
    }

    public static final class HeartbeatRequest {
        public final int loginFlag;
        public final int appState;
        public final int intervalSeconds;
        public final int sdkType;

        public HeartbeatRequest(int loginFlag, int appState, int intervalSeconds, int sdkType) {
            this.loginFlag = loginFlag;
            this.appState = appState;
            this.intervalSeconds = intervalSeconds;
            this.sdkType = sdkType;
        }
    }

    public static final class PushEnvelope {
        public final byte messageType;
        public final long messageId;
        public final String content;

        public PushEnvelope(byte messageType, long messageId, String content) {
            this.messageType = messageType;
            this.messageId = messageId;
            this.content = content != null ? content : "";
        }
    }

    public static final class MessageAck {
        public final int statusCode;
        public final byte messageType;
        public final long messageId;
        public final String extData;

        public MessageAck(byte messageType, long messageId) {
            this(0, messageType, messageId, "");
        }

        public MessageAck(int statusCode, byte messageType, long messageId, String extData) {
            this.statusCode = statusCode;
            this.messageType = messageType;
            this.messageId = messageId;
            this.extData = extData != null ? extData : "";
        }
    }

    public static final class CtrlCommand {
        public final long requestId;
        public final String jsonContent;

        public CtrlCommand(long requestId, String jsonContent) {
            this.requestId = requestId;
            this.jsonContent = jsonContent != null ? jsonContent : "";
        }
    }

    public static final class CtrlAck {
        public final int statusCode;
        public final long requestId;

        public CtrlAck(int statusCode, long requestId) {
            this.statusCode = statusCode;
            this.requestId = requestId;
        }
    }

    public static final class DeviceTokenPayload {
        public final String token;
        public final byte platform;
        public final boolean unbind;

        public DeviceTokenPayload(String token, byte platform) {
            this(token, platform, false);
        }

        public DeviceTokenPayload(String token, byte platform, boolean unbind) {
            this.token = token != null ? token : "";
            this.platform = platform;
            this.unbind = unbind;
        }
    }

    public static final class MobileNumberPayload {
        public final int typeFlag;
        public final int opFlag;
        public final int sequence;
        public final String mobileNumber;

        public MobileNumberPayload(int sequence, String mobileNumber) {
            this(7, 1, sequence, mobileNumber);
        }

        public MobileNumberPayload(int typeFlag, int opFlag, int sequence, String mobileNumber) {
            this.typeFlag = typeFlag;
            this.opFlag = opFlag;
            this.sequence = sequence;
            this.mobileNumber = mobileNumber != null ? mobileNumber : "";
        }
    }

    public static final class OperationPayload {
        public final int command;
        public final int action;
        public final String op;
        public final int sequence;
        public final String alias;
        public final Set<String> tags;
        public final Map<String, String> properties;
        public final String rawJson;

        public OperationPayload(
            int command,
            int action,
            String op,
            int sequence,
            String alias,
            Set<String> tags,
            Map<String, String> properties,
            String rawJson
        ) {
            this.command = command;
            this.action = action;
            this.op = op != null ? op : "";
            this.sequence = sequence;
            this.alias = alias != null ? alias : "";
            this.tags = tags != null ? tags : Collections.<String>emptySet();
            this.properties = properties != null ? properties : Collections.<String, String>emptyMap();
            this.rawJson = rawJson != null ? rawJson : "";
        }
    }

    private static boolean hasResponseStatusPrefix(int command) {
        return command == CMD_REGISTER
            || command == CMD_LOGIN
            || command == CMD_HEARTBEAT
            || command == 10
            || command == 19
            || command == CMD_MOBILE_NUMBER
            || command == CMD_THIRD_TOKEN
            || command == CMD_PROPERTY_OP;
    }

    private static byte[] encryptPayload(byte[] plainBytes, int algorithm, int flag, long seed) {
        if (plainBytes == null) {
            plainBytes = new byte[0];
        }
        byte[] source = ((flag & 1) != 0 && plainBytes.length > 0)
            ? JCoreCrypto.gzipCompress(plainBytes)
            : plainBytes;
        if (algorithm == ALG_SM4_CBC) {
            return JCoreCrypto.sm4CbcSeedEncrypt(source, seed);
        }
        return JCoreCrypto.aes256CbcSeedEncrypt(source, seed);
    }

    private static byte[] decryptPayload(byte[] cipherBytes, int algorithm, int flag, long seed) {
        if (cipherBytes == null || cipherBytes.length == 0) {
            return new byte[0];
        }
        byte[] decrypted = (algorithm == ALG_SM4_CBC)
            ? JCoreCrypto.sm4CbcSeedDecrypt(cipherBytes, seed)
            : JCoreCrypto.aes256CbcSeedDecrypt(cipherBytes, seed);
        if (decrypted.length > 0 && (flag & 1) != 0) {
            decrypted = JCoreCrypto.gzipDecompress(decrypted);
        }
        return decrypted;
    }

    /**
     * Encodes an outbound Client -> Server 24-byte header TCP packet.
     */
    public static byte[] encodeFrame(int command, int version, long requestId, int sessionId, long uid, byte[] body) {
        return encodeClientFrame(command, version, defaultAlgorithm, 0, requestId, sessionId, uid, body);
    }

    public static byte[] encodeClientFrame(
        int command,
        int version,
        int algorithm,
        int flag,
        long requestId,
        int seedOrSessionId,
        long uid,
        byte[] body
    ) {
        int effectiveAlg = (algorithm == ALG_SM4_CBC) ? ALG_SM4_CBC : ALG_AES_CBC;
        int headerSeedOrSid = seedOrSessionId;
        long effectiveUid = uid;
        long cryptoSeed;
        if (command == CMD_REGISTER) {
            int regSeed = seedOrSessionId > 0 ? seedOrSessionId : JCoreCrypto.nextRegisterSeed();
            lastRegisterSeed = regSeed & 0xFFFFFFFFL;
            headerSeedOrSid = regSeed;
            effectiveUid = 0L;
            cryptoSeed = lastRegisterSeed;
        } else {
            cryptoSeed = (uid > 0L) ? uid : lastRegisterSeed;
        }

        byte[] payload = body != null ? body : new byte[0];
        byte[] encryptedBody = encryptPayload(payload, effectiveAlg, flag, cryptoSeed);
        int totalLen = CLIENT_HEADER_BYTES + encryptedBody.length;

        ByteBuffer buffer = ByteBuffer.allocate(totalLen);
        buffer.putShort((short) ((totalLen & 0x7FFF) | 0x8000));
        buffer.put((byte) (version & 0xFF));
        buffer.put((byte) (command & 0xFF));
        buffer.put((byte) (effectiveAlg & 0xFF));
        buffer.put((byte) (flag & 0xFF));
        buffer.putInt(0); // reserved
        buffer.putShort((short) (requestId & 0xFFFF));
        buffer.putInt(headerSeedOrSid);
        buffer.putLong(effectiveUid);
        if (encryptedBody.length > 0) {
            buffer.put(encryptedBody);
        }
        return buffer.array();
    }

    /**
     * Decodes a 24-byte Client -> Server frame (used by loopback/mock servers and unit tests).
     */
    public static DecodedFrame decodeClientFrame(byte[] rawFrame, long defaultUid) {
        if (rawFrame == null || rawFrame.length < CLIENT_HEADER_BYTES) {
            return null;
        }
        ByteBuffer buf = ByteBuffer.wrap(rawFrame);
        int rawLen = buf.getShort() & 0xFFFF;
        int totalLen = rawLen & 0x7FFF;
        boolean encrypted = (rawLen & 0x8000) != 0;
        if (totalLen < CLIENT_HEADER_BYTES || totalLen > rawFrame.length) {
            totalLen = rawFrame.length;
        }
        int version = buf.get() & 0xFF;
        int command = buf.get() & 0xFF;
        int algorithm = buf.get() & 0xFF;
        int flag = buf.get() & 0xFF;
        buf.getInt(); // reserved
        long rid = buf.getShort() & 0xFFFFL;
        int seedOrSid = buf.getInt();
        long uid = buf.getLong();

        byte[] body = new byte[0];
        if (totalLen > CLIENT_HEADER_BYTES) {
            body = new byte[totalLen - CLIENT_HEADER_BYTES];
            System.arraycopy(rawFrame, CLIENT_HEADER_BYTES, body, 0, body.length);
        }
        if (encrypted && body.length > 0) {
            long seed;
            if (command == CMD_REGISTER) {
                seed = seedOrSid & 0xFFFFFFFFL;
                if (seed > 0L) {
                    lastRegisterSeed = seed;
                }
            } else if (uid > 0L) {
                seed = uid;
            } else if (defaultUid > 0L) {
                seed = defaultUid;
            } else {
                seed = lastRegisterSeed;
            }
            body = decryptPayload(body, algorithm, flag, seed);
        }
        return new DecodedFrame(version, command, algorithm, flag, rid, seedOrSid, uid, 0, body);
    }

    public static DecodedFrame readClientFrameFromStream(InputStream in, long defaultUid) throws IOException {
        byte[] fullFrame = readRawPacketBytes(in, CLIENT_HEADER_BYTES);
        if (fullFrame == null) {
            return null;
        }
        return decodeClientFrame(fullFrame, defaultUid);
    }

    /**
     * Encodes an inbound Server -> Client 20-byte header TCP packet.
     */
    public static byte[] encodeServerFrame(
        int command,
        int version,
        int algorithm,
        int flag,
        long requestId,
        long uid,
        long registerSeed,
        byte[] body
    ) {
        int effectiveAlg = (algorithm == ALG_SM4_CBC) ? ALG_SM4_CBC : ALG_AES_CBC;
        long seed;
        if (command == CMD_REGISTER) {
            seed = registerSeed > 0L ? registerSeed : lastRegisterSeed;
        } else {
            seed = uid > 0L ? uid : (registerSeed > 0L ? registerSeed : lastRegisterSeed);
        }
        byte[] payload = body != null ? body : new byte[0];
        byte[] encryptedBody = encryptPayload(payload, effectiveAlg, flag, seed);
        int totalLen = SERVER_HEADER_BYTES + encryptedBody.length;

        ByteBuffer buffer = ByteBuffer.allocate(totalLen);
        buffer.putShort((short) ((totalLen & 0x7FFF) | 0x8000));
        buffer.put((byte) (version & 0xFF));
        buffer.put((byte) (command & 0xFF));
        buffer.put((byte) (effectiveAlg & 0xFF));
        buffer.put((byte) (flag & 0xFF));
        buffer.putInt(0); // reserved
        buffer.putShort((short) (requestId & 0xFFFF));
        buffer.putLong(uid);
        if (encryptedBody.length > 0) {
            buffer.put(encryptedBody);
        }
        return buffer.array();
    }

    public static byte[] encodeResponseFrame(
        int command,
        int version,
        long requestId,
        int sessionId,
        long uid,
        int statusCode,
        byte[] body
    ) {
        return encodeResponseFrame(command, version, defaultAlgorithm, 0, requestId, sessionId, uid, statusCode, body);
    }

    public static byte[] encodeResponseFrame(
        int command,
        int version,
        int algorithm,
        int flag,
        long requestId,
        int sessionId,
        long uid,
        int statusCode,
        byte[] body
    ) {
        byte[] raw = body != null ? body : new byte[0];
        byte[] payload;
        if (hasResponseStatusPrefix(command)) {
            ByteBuffer buf = ByteBuffer.allocate(2 + raw.length);
            buf.putShort((short) (statusCode & 0xFFFF));
            if (raw.length > 0) {
                buf.put(raw);
            }
            payload = buf.array();
        } else {
            payload = raw;
        }
        long regSeed = sessionId > 0 ? (sessionId & 0xFFFFFFFFL) : lastRegisterSeed;
        return encodeServerFrame(command, version, algorithm, flag, requestId, uid, regSeed, payload);
    }

    /**
     * Decodes an inbound 20-byte Server -> Client TCP packet (or falls back to 24-byte Client frame if needed).
     */
    public static DecodedFrame decodeFrame(byte[] rawFrame, long defaultUid) {
        return decodeServerFrame(rawFrame, defaultUid);
    }

    public static DecodedFrame decodeServerFrame(byte[] rawFrame, long defaultUid) {
        if (rawFrame == null || rawFrame.length < SERVER_HEADER_BYTES) {
            return null;
        }
        ByteBuffer buf = ByteBuffer.wrap(rawFrame);
        int rawLen = buf.getShort() & 0xFFFF;
        int totalLen = rawLen & 0x7FFF;
        boolean encrypted = (rawLen & 0x8000) != 0;
        if (totalLen < SERVER_HEADER_BYTES || totalLen > rawFrame.length) {
            totalLen = rawFrame.length;
        }
        int version = buf.get() & 0xFF;
        int command = buf.get() & 0xFF;
        int algorithm = buf.get() & 0xFF;
        int flag = buf.get() & 0xFF;
        buf.getInt(); // reserved
        long rid = buf.getShort() & 0xFFFFL;
        long uid = buf.getLong();

        byte[] cipherBody = new byte[0];
        if (totalLen > SERVER_HEADER_BYTES) {
            cipherBody = new byte[totalLen - SERVER_HEADER_BYTES];
            System.arraycopy(rawFrame, SERVER_HEADER_BYTES, cipherBody, 0, cipherBody.length);
        }

        byte[] body = cipherBody;
        if (encrypted && cipherBody.length > 0) {
            long primarySeed;
            if (command == CMD_REGISTER) {
                primarySeed = lastRegisterSeed;
            } else if (uid > 0L) {
                primarySeed = uid;
            } else if (defaultUid > 0L) {
                primarySeed = defaultUid;
            } else {
                primarySeed = lastRegisterSeed;
            }
            byte[] decrypted = decryptPayload(cipherBody, algorithm, flag, primarySeed);
            if (decrypted.length == 0 && command == CMD_REGISTER && uid > 0L && uid != primarySeed) {
                decrypted = decryptPayload(cipherBody, algorithm, flag, uid);
            }
            if (decrypted.length == 0 && rawFrame.length >= CLIENT_HEADER_BYTES) {
                DecodedFrame clientDecoded = decodeClientFrame(rawFrame, defaultUid);
                if (clientDecoded != null && clientDecoded.body.length > 0) {
                    return clientDecoded;
                }
            }
            if ((flag & 1) != 0 && decrypted.length == 0) return null;
            body = decrypted;
        }

        int statusCode = 0;
        if (hasResponseStatusPrefix(command) && body.length >= 2) {
            statusCode = ((body[0] & 0xFF) << 8) | (body[1] & 0xFF);
        }
        return new DecodedFrame(version, command, algorithm, flag, rid, 0, uid, statusCode, body);
    }

    private static byte[] readRawPacketBytes(InputStream in, int minHeaderSize) throws IOException {
        if (in == null) {
            return null;
        }
        int first = in.read();
        if (first == -1) {
            return null;
        }
        int second = in.read();
        if (second == -1) {
            throw new EOFException("Truncated JCore frame length");
        }
        int rawLength = ((first & 0xFF) << 8) | (second & 0xFF);
        int frameLength = rawLength & 0x7FFF;
        if (frameLength < minHeaderSize || frameLength > 65535) {
            throw new IOException("Invalid JCore frame length: " + frameLength);
        }
        byte[] fullFrame = new byte[frameLength];
        fullFrame[0] = (byte) first;
        fullFrame[1] = (byte) second;
        int offset = 2;
        while (offset < frameLength) {
            int read = in.read(fullFrame, offset, frameLength - offset);
            if (read == -1) {
                throw new EOFException("Truncated JCore frame body");
            }
            offset += read;
        }
        return fullFrame;
    }

    public static DecodedFrame readFrameFromStream(InputStream in, long defaultUid) throws IOException {
        byte[] fullFrame = readRawPacketBytes(in, SERVER_HEADER_BYTES);
        if (fullFrame == null) {
            return null;
        }
        return decodeServerFrame(fullFrame, defaultUid);
    }

    public static List<DecodedFrame> decodeFramesFromStreamBuffer(ByteArrayOutputStream streamBuffer, long defaultUid) {
        if (streamBuffer == null) {
            return Collections.emptyList();
        }
        byte[] all = streamBuffer.toByteArray();
        List<DecodedFrame> frames = new ArrayList<DecodedFrame>();
        int cursor = 0;
        while (all.length - cursor >= SERVER_HEADER_BYTES) {
            int rawLen = ((all[cursor] & 0xFF) << 8) | (all[cursor + 1] & 0xFF);
            int frameLen = rawLen & 0x7FFF;
            if (frameLen < SERVER_HEADER_BYTES || frameLen > 65535) {
                cursor = all.length;
                break;
            }
            if (all.length - cursor < frameLen) {
                break;
            }
            byte[] slice = new byte[frameLen];
            System.arraycopy(all, cursor, slice, 0, frameLen);
            cursor += frameLen;
            DecodedFrame decoded = decodeServerFrame(slice, defaultUid);
            if (decoded != null) {
                frames.add(decoded);
            }
        }
        if (cursor > 0) {
            streamBuffer.reset();
            if (cursor < all.length) {
                streamBuffer.write(all, cursor, all.length - cursor);
            }
        }
        return frames;
    }

    public static String buildSisRequestJson(
        int netType,
        String appKey,
        long uid,
        String operator,
        Collection<String> failIps,
        int certVer
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"type\":").append(netType)
            .append(",\"appkey\":\"").append(escapeJson(appKey != null ? appKey : "")).append('"')
            .append(",\"sdkver\":\"5.1.1\"")
            .append(",\"platform\":0")
            .append(",\"uid\":").append(uid)
            .append(",\"opera\":\"").append(escapeJson(operator != null ? operator : "")).append('"')
            .append(",\"fail_ips\":[");
        if (failIps != null) {
            boolean first = true;
            for (String ip : failIps) {
                if (ip == null || ip.isEmpty()) {
                    continue;
                }
                if (!first) {
                    sb.append(',');
                }
                first = false;
                sb.append('"').append(escapeJson(ip)).append('"');
            }
        }
        sb.append("],\"ips_flag\":0,\"report_flag\":0,\"cert_ver\":").append(certVer).append('}');
        return sb.toString();
    }

    /**
     * Encodes a 10-byte header UDP SIS packet:
     * 0..1: uint16 totalLen = 10 + encrypted.length
     * 2..3: 'U', 'G'
     * 4..7: uint32 ((0x11 << 24) | (seed & 0xffffff))
     * 8..9: uint16 preEncryptLen = compressed.length
     * 10..totalLen: AES-256-CBC encrypted GZIP payload
     */
    public static byte[] encodeUdpSisPacket(String jsonPayload, long seed) {
        return encodeUdpSisPacket(SIS_MAGIC_U, jsonPayload, seed);
    }

    public static byte[] encodeUdpSisPacket(byte firstMagic, String jsonPayload, long seed) {
        byte magic0 = (firstMagic == SIS_MAGIC_M) ? SIS_MAGIC_M : SIS_MAGIC_U;
        byte[] plainBytes = (jsonPayload != null ? jsonPayload : "{}").getBytes(StandardCharsets.UTF_8);
        byte[] compressed = JCoreCrypto.gzipCompress(plainBytes);
        int preEncryptLen = compressed.length;
        int seed24 = (int) (seed & 0xFFFFFFL);
        if (seed24 == 0) {
            seed24 = JCoreCrypto.nextPositiveSeed24();
        }
        byte[] encrypted = JCoreCrypto.aes256CbcSeedEncrypt(compressed, seed24);
        int totalLen = 10 + encrypted.length;
        int keyField = (0x11 << 24) | (seed24 & 0xFFFFFF);

        ByteBuffer buffer = ByteBuffer.allocate(totalLen);
        buffer.putShort((short) (totalLen & 0xFFFF));
        buffer.put(magic0);
        buffer.put(SIS_MAGIC_G);
        buffer.putInt(keyField);
        buffer.putShort((short) (preEncryptLen & 0xFFFF));
        buffer.put(encrypted);
        return buffer.array();
    }

    public static String decodeUdpSisPacket(byte[] packet) {
        if (packet == null || packet.length < 10) {
            return "";
        }
        ByteBuffer buffer = ByteBuffer.wrap(packet);
        int totalLen = buffer.getShort() & 0xFFFF;
        byte magic0 = buffer.get();
        byte magic1 = buffer.get();
        if ((magic0 != SIS_MAGIC_U && magic0 != SIS_MAGIC_M) || magic1 != SIS_MAGIC_G) {
            return "";
        }
        int keyField = buffer.getInt();
        int flags = (keyField >>> 24) & 0xFF;
        long seed = keyField & 0xFFFFFFL;
        int declaredLen = buffer.getShort() & 0xFFFF;

        int availableCipher = Math.min(packet.length, totalLen) - 10;
        if (availableCipher <= 0) {
            return "";
        }
        byte[] encrypted = new byte[availableCipher];
        buffer.get(encrypted);
        byte[] decrypted = JCoreCrypto.aes256CbcSeedDecrypt(encrypted, seed);
        if (decrypted.length == 0) {
            return "";
        }
        if (declaredLen > 0 && declaredLen < decrypted.length) {
            byte[] sliced = new byte[declaredLen];
            System.arraycopy(decrypted, 0, sliced, 0, declaredLen);
            decrypted = sliced;
        }
        byte[] plain = (flags & 1) != 0 ? JCoreCrypto.gzipDecompress(decrypted) : decrypted;
        return new String(plain, StandardCharsets.UTF_8);
    }

    /**
     * Register (command = 0, version = 29):
     * 1. string key: " $$ $$" + packageName + "$$" + appKey
     * 2. string apkVersion: appVersion
     * 3. string clientInfo: osVersion + "$$" + model + "$$" + brand + "$$" + device + "$$" + channel + "$$5.9.0|5.1.1$$$$$$&&$$0"
     * 4. uint8(0) (Android platform = 0)
     * 5. string extKey: "0$$" + deviceId + "$$ $$ $$ $$ $$ $$ $$ $$ $$ $$ "
     * 6. uint32(1) (regBusiness = 1 for JPush)
     * 7. string accountId: ""
     */
    public static byte[] buildRegisterPayload(String appKey, String channel, String packageName, String deviceId) {
        return buildRegisterPayload(appKey, channel, packageName, deviceId, "1.0.0");
    }

    public static byte[] buildRegisterPayload(
        String appKey,
        String channel,
        String packageName,
        String deviceId,
        String apkVersion
    ) {
        String osVersion = "14";
        String model = "Android";
        String brand = "OneKey";
        String device = "generic";
        try {
            if (Build.VERSION.RELEASE != null && !Build.VERSION.RELEASE.isEmpty()) {
                osVersion = Build.VERSION.RELEASE;
            }
            if (Build.MODEL != null && !Build.MODEL.isEmpty()) {
                model = Build.MODEL;
            }
            if (Build.BRAND != null && !Build.BRAND.isEmpty()) {
                brand = Build.BRAND;
            }
            if (Build.DEVICE != null && !Build.DEVICE.isEmpty()) {
                device = Build.DEVICE;
            }
        } catch (Throwable ignored) {
        }
        String key = " $$ $$" + (packageName != null ? packageName : "") + "$$" + (appKey != null ? appKey : "");
        String ver = (apkVersion != null && !apkVersion.isEmpty()) ? apkVersion : "1.0.0";
        String clientInfo = osVersion
            + "$$" + model
            + "$$" + brand
            + "$$" + device
            + "$$" + (channel != null ? channel : "developer-default")
            + "$$5.9.0|5.1.1$$$$$$&&$$0";
        String extKey = "0$$" + (deviceId != null ? deviceId : "") + "$$ $$ $$ $$ $$ $$ $$ $$ $$ $$ ";

        ByteBuffer buf = ByteBuffer.allocate(1024);
        writeString(buf, key);
        writeString(buf, ver);
        writeString(buf, clientInfo);
        buf.put((byte) 0); // Android platform = 0
        writeString(buf, extKey);
        buf.putInt(1); // regBusiness = 1 (JPush)
        writeString(buf, ""); // accountId
        return sliceUsed(buf);
    }

    public static byte[] buildRegisterResponsePayload(long uid, String password, String registrationId) {
        return buildRegisterResponsePayload(uid, password, registrationId, "");
    }

    public static byte[] buildRegisterResponsePayload(
        long uid,
        String password,
        String registrationId,
        String deviceId
    ) {
        ByteBuffer buf = ByteBuffer.allocate(256);
        buf.putLong(uid);
        writeString(buf, password != null ? password : "");
        writeString(buf, registrationId != null ? registrationId : "");
        if (deviceId != null && !deviceId.isEmpty()) {
            writeString(buf, deviceId);
        }
        return sliceUsed(buf);
    }

    public static RegisterRequest parseRegisterRequest(byte[] body) {
        if (body == null || body.length < 4) {
            return null;
        }
        ByteBuffer buf = ByteBuffer.wrap(body);
        String key = readString(buf);
        String apkVersion = readString(buf);
        String clientInfo = readString(buf);
        int platform = buf.remaining() >= 1 ? (buf.get() & 0xFF) : 0;
        String extKey = readString(buf);
        int regBusiness = buf.remaining() >= 4 ? buf.getInt() : 1;
        String accountId = readString(buf);

        String pkg = "";
        String appKey = "";
        if (!key.isEmpty()) {
            String[] keyParts = key.split("\\$\\$", -1);
            if (keyParts.length > 2) {
                pkg = keyParts[2];
            }
            if (keyParts.length > 3) {
                appKey = keyParts[3];
            }
        }
        String channel = "";
        if (!clientInfo.isEmpty()) {
            String[] infoParts = clientInfo.split("\\$\\$", -1);
            if (infoParts.length > 4) {
                channel = infoParts[4];
            }
        }
        String deviceId = "";
        if (!extKey.isEmpty()) {
            String[] extParts = extKey.split("\\$\\$", -1);
            if (extParts.length > 1) {
                deviceId = extParts[1];
            }
        }
        return new RegisterRequest(
            key,
            apkVersion,
            clientInfo,
            platform,
            extKey,
            regBusiness,
            accountId,
            pkg,
            appKey,
            channel,
            deviceId
        );
    }

    public static RegisterResponse parseRegisterResponse(DecodedFrame frame) {
        if (frame == null) {
            return null;
        }
        return parseRegisterResponse(frame.body, frame.statusCode);
    }

    public static RegisterResponse parseRegisterResponse(byte[] body, int fallbackStatusCode) {
        if (body == null || body.length < 2) {
            return new RegisterResponse(fallbackStatusCode, 0L, "", "", "");
        }
        ByteBuffer buf = ByteBuffer.wrap(body);
        int statusCode = buf.getShort() & 0xFFFF;
        if (statusCode != 0 || buf.remaining() < 8) {
            return new RegisterResponse(statusCode, 0L, "", "", "");
        }
        long uid = buf.getLong();
        String password = readString(buf);
        String regId = readString(buf);
        String deviceId = buf.remaining() >= 2 ? readString(buf) : "";
        return new RegisterResponse(statusCode, uid, password, regId, deviceId);
    }

    /**
     * Login (command = 1, version = 27):
     * 1. uint8(97) (0x61)
     * 2. uint8(0)
     * 3. uint16(0)
     * 4. string(md5Hex(password))
     * 5. string("5.1.1|5.9.0") (sdkVerInfo)
     * 6. string(appKey)
     * 7. uint8(0) (Android platform = 0)
     * 8. uint32(1) (loginFlag = 1 for JPush)
     * 9. uint8(0) (romPlatformMask)
     * 10. uint8(1) (netType)
     * 11. string(deviceId)
     * 12. string("") (locInfo empty)
     * 13. string(countryCode)
     * 14. string("") (accountId)
     * 15. uint8(0) (userType)
     * 16. uint8(1) (initPushSdk = 1)
     * 17. string("1") (gSdkTypeInfo)
     */
    public static byte[] buildLoginPayload(String password, String appKey) {
        return buildLoginPayload(password, appKey, "", "");
    }

    public static byte[] buildLoginPayload(
        String password,
        String appKey,
        String deviceId,
        String countryCode
    ) {
        String pwdMd5 = JCoreCrypto.md5Hex(password != null ? password : "");
        ByteBuffer buf = ByteBuffer.allocate(512);
        buf.put((byte) 97);
        buf.put((byte) 0);
        buf.putShort((short) 0);
        writeString(buf, pwdMd5);
        writeString(buf, "5.1.1|5.9.0");
        writeString(buf, appKey != null ? appKey : "");
        buf.put((byte) 0); // Android platform = 0
        buf.putInt(1); // loginFlag = 1
        buf.put((byte) 0); // romPlatformMask
        buf.put((byte) 1); // netType
        writeString(buf, deviceId != null ? deviceId : "");
        writeString(buf, ""); // locInfo
        writeString(buf, countryCode != null ? countryCode : "");
        writeString(buf, ""); // accountId
        buf.put((byte) 0); // userType
        buf.put((byte) 1); // initPushSdk = 1
        writeString(buf, "1"); // gSdkTypeInfo
        return sliceUsed(buf);
    }

    public static byte[] buildLoginResponsePayload(int sessionId, int serverVersion, int serverTimestamp) {
        return buildLoginResponsePayload(sessionId, serverVersion, "", serverTimestamp);
    }

    public static byte[] buildLoginResponsePayload(
        int sessionId,
        int serverVersion,
        String errorDesc,
        int serverTimestamp
    ) {
        ByteBuffer buf = ByteBuffer.allocate(128);
        buf.putInt(sessionId);
        buf.putShort((short) (serverVersion & 0xFFFF));
        writeString(buf, errorDesc != null ? errorDesc : "");
        buf.putInt(serverTimestamp);
        buf.put((byte) 0); // idc
        return sliceUsed(buf);
    }

    public static LoginRequest parseLoginRequest(byte[] body) {
        if (body == null || body.length < 6) {
            return null;
        }
        ByteBuffer buf = ByteBuffer.wrap(body);
        buf.get(); // 97
        buf.get(); // 0
        buf.getShort(); // 0
        String pwdMd5 = readString(buf);
        String sdkVerInfo = readString(buf);
        String appKey = readString(buf);
        int platform = buf.remaining() >= 1 ? (buf.get() & 0xFF) : 0;
        int loginFlag = buf.remaining() >= 4 ? buf.getInt() : 1;
        if (buf.remaining() >= 1) {
            buf.get(); // romPlatformMask
        }
        int netType = buf.remaining() >= 1 ? (buf.get() & 0xFF) : 1;
        String deviceId = readString(buf);
        readString(buf); // locInfo
        String countryCode = readString(buf);
        readString(buf); // accountId
        if (buf.remaining() >= 1) {
            buf.get(); // userType
        }
        int initPushSdk = buf.remaining() >= 1 ? (buf.get() & 0xFF) : 1;
        String gSdkTypeInfo = readString(buf);
        return new LoginRequest(
            pwdMd5,
            sdkVerInfo,
            appKey,
            platform,
            loginFlag,
            netType,
            deviceId,
            countryCode,
            initPushSdk,
            gSdkTypeInfo
        );
    }

    public static LoginResponse parseLoginResponse(DecodedFrame frame) {
        if (frame == null) {
            return null;
        }
        return parseLoginResponse(frame.body, frame.statusCode);
    }

    public static LoginResponse parseLoginResponse(byte[] body, int fallbackStatusCode) {
        if (body == null || body.length < 2) {
            return new LoginResponse(fallbackStatusCode, 0, 0, "", 0);
        }
        ByteBuffer buf = ByteBuffer.wrap(body);
        int statusCode = buf.getShort() & 0xFFFF;
        if (statusCode != 0 || buf.remaining() < 4) {
            return new LoginResponse(statusCode, 0, 0, "", 0);
        }
        int sid = buf.getInt();
        int serverVer = buf.remaining() >= 2 ? (buf.getShort() & 0xFFFF) : 0;
        String errorDesc = readString(buf);
        int serverTime = buf.remaining() >= 4 ? buf.getInt() : 0;
        return new LoginResponse(statusCode, sid, serverVer, errorDesc, serverTime);
    }

    /**
     * Heartbeat (command = 2, version = 8):
     * uint32(1 loginFlag) + uint8(appState) + uint16(290 interval) + uint8(1 sdkType)
     */
    public static byte[] buildHeartbeatPayload(boolean pushStopped) {
        return buildHeartbeatPayload(1, pushStopped ? 3 : 1, 290, 1);
    }

    public static byte[] buildHeartbeatPayload(int loginFlag, int appState, int intervalSeconds, int sdkType) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putInt(loginFlag);
        buf.put((byte) (appState & 0xFF));
        buf.putShort((short) (intervalSeconds & 0xFFFF));
        buf.put((byte) (sdkType & 0xFF));
        return buf.array();
    }

    public static HeartbeatRequest parseHeartbeatRequest(byte[] body) {
        if (body == null || body.length < 8) {
            return null;
        }
        ByteBuffer buf = ByteBuffer.wrap(body);
        int loginFlag = buf.getInt();
        int appState = buf.get() & 0xFF;
        int interval = buf.getShort() & 0xFFFF;
        int sdkType = buf.get() & 0xFF;
        return new HeartbeatRequest(loginFlag, appState, interval, sdkType);
    }

    public static boolean parseHeartbeatPayload(byte[] body) {
        HeartbeatRequest req = parseHeartbeatRequest(body);
        return req != null && req.appState == 3;
    }

    /**
     * PushMessage (command = 3) & MessageReceipt (command = 4, version = 3)
     */
    public static byte[] buildPushMessagePayload(byte messageType, long messageId, String content) {
        byte[] contentBytes = (content != null ? content : "").getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(16 + contentBytes.length);
        buf.put(messageType);
        buf.putLong(messageId);
        writeString(buf, content);
        return sliceUsed(buf);
    }

    public static PushEnvelope parsePushMessage(DecodedFrame frame) {
        if (frame == null) {
            return null;
        }
        return parsePushMessage(frame.body);
    }

    public static PushEnvelope parsePushMessage(byte[] body) {
        if (body == null || body.length < 9) {
            return null;
        }
        ByteBuffer buf = ByteBuffer.wrap(body);
        byte msgType = buf.get();
        long msgId = buf.getLong();
        String content = readString(buf);
        return new PushEnvelope(msgType, msgId, content);
    }

    public static byte[] buildMessageAckPayload(byte messageType, long messageId) {
        return buildMessageAckPayload(0, messageType, messageId, "");
    }

    public static byte[] buildMessageAckPayload(int statusCode, byte messageType, long messageId, String extData) {
        byte[] extBytes = (extData != null ? extData : "").getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(16 + extBytes.length);
        buf.putShort((short) (statusCode & 0xFFFF));
        buf.put(messageType);
        buf.putLong(messageId);
        if (extData != null && !extData.isEmpty()) {
            writeString(buf, extData);
        }
        return sliceUsed(buf);
    }

    public static MessageAck parseMessageAck(DecodedFrame frame) {
        if (frame == null) {
            return null;
        }
        return parseMessageAck(frame.body);
    }

    public static MessageAck parseMessageAck(byte[] body) {
        if (body == null || body.length < 11) {
            return null;
        }
        ByteBuffer buf = ByteBuffer.wrap(body);
        int statusCode = buf.getShort() & 0xFFFF;
        byte msgType = buf.get();
        long msgId = buf.getLong();
        String extData = buf.remaining() >= 2 ? readString(buf) : "";
        return new MessageAck(statusCode, msgType, msgId, extData);
    }

    /**
     * Ctrl Command (command = 25, version = 1):
     * Inbound: uint64 requestId + string jsonContent
     * Outbound Ack: uint16 statusCode(0) + uint64 requestId
     */
    public static byte[] buildCtrlCommandPayload(long requestId, String jsonContent) {
        byte[] contentBytes = (jsonContent != null ? jsonContent : "").getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(12 + contentBytes.length);
        buf.putLong(requestId);
        writeString(buf, jsonContent);
        return sliceUsed(buf);
    }

    public static CtrlCommand parseCtrlCommand(byte[] body) {
        if (body == null || body.length < 10) {
            return null;
        }
        ByteBuffer buf = ByteBuffer.wrap(body);
        long reqId = buf.getLong();
        String json = readString(buf);
        return new CtrlCommand(reqId, json);
    }

    public static byte[] buildCtrlAckPayload(int statusCode, long requestId) {
        ByteBuffer buf = ByteBuffer.allocate(10);
        buf.putShort((short) (statusCode & 0xFFFF));
        buf.putLong(requestId);
        return buf.array();
    }

    public static CtrlAck parseCtrlAck(byte[] body) {
        if (body == null || body.length < 10) {
            return null;
        }
        ByteBuffer buf = ByteBuffer.wrap(body);
        int statusCode = buf.getShort() & 0xFFFF;
        long reqId = buf.getLong();
        return new CtrlAck(statusCode, reqId);
    }

    /**
     * Third-Party Vendor Token Upload (command = 27, version = 2):
     * string(thirdToken) + uint8(platform) + uint8(unbind ? 0 : 1)
     */
    public static byte[] buildThirdTokenPayload(String token, byte platform, boolean unbind) {
        byte[] tokenBytes = (token != null ? token : "").getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(8 + tokenBytes.length);
        writeString(buf, token);
        buf.put(platform);
        buf.put((byte) (unbind ? 0 : 1));
        return sliceUsed(buf);
    }

    public static byte[] buildDeviceTokenPayload(String token, byte platform) {
        return buildThirdTokenPayload(token, platform, false);
    }

    public static DeviceTokenPayload parseDeviceTokenPayload(byte[] body) {
        if (body == null || body.length < 2) {
            return null;
        }
        ByteBuffer buf = ByteBuffer.wrap(body);
        String token = readString(buf);
        byte platform = buf.remaining() >= 1 ? buf.get() : 0;
        boolean unbind = buf.remaining() >= 1 && (buf.get() == 0);
        return new DeviceTokenPayload(token, platform, unbind);
    }

    /**
     * MobileNumber (command = 26, version = 1):
     * uint8(7) + uint8(1) + string(mobileNumber)
     */
    public static byte[] buildMobileNumberPayload(int sequence, String mobileNumber) {
        byte[] mobileBytes = (mobileNumber != null ? mobileNumber : "").getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(8 + mobileBytes.length);
        buf.put((byte) 7);
        buf.put((byte) 1);
        writeString(buf, mobileNumber);
        return sliceUsed(buf);
    }

    public static MobileNumberPayload parseMobileNumberPayload(byte[] body) {
        if (body == null || body.length < 4) {
            return null;
        }
        ByteBuffer buf = ByteBuffer.wrap(body);
        int typeFlag = buf.get() & 0xFF;
        int opFlag = buf.get() & 0xFF;
        String mobileNumber = readString(buf);
        int sequence = buf.remaining() >= 4 ? buf.getInt() : 0;
        return new MobileNumberPayload(typeFlag, opFlag, sequence, mobileNumber);
    }

    /**
     * Tags (command = 28, version = 1):
     * string(json) where json is {"platform":"a","op":("add"|"set"|"del"|"clean"|"get"|"valid"),"tags":[...],"curr":1}
     */
    public static String mapTagActionToOp(int action) {
        switch (action) {
            case 1:
                return "add";
            case 2:
                return "set";
            case 3:
                return "del";
            case 4:
                return "clean";
            case 5:
                return "get";
            case 6:
                return "valid";
            default:
                return "set";
        }
    }

    public static int mapTagOpToAction(String op) {
        if ("add".equals(op)) return 1;
        if ("set".equals(op)) return 2;
        if ("del".equals(op)) return 3;
        if ("clean".equals(op)) return 4;
        if ("get".equals(op)) return 5;
        if ("valid".equals(op)) return 6;
        return 2;
    }

    public static byte[] buildTagOperationPayload(int action, int sequence, Collection<String> tags) {
        String op = mapTagActionToOp(action);
        StringBuilder sb = new StringBuilder();
        sb.append("{\"platform\":\"a\",\"op\":\"").append(op).append('"');
        if (tags != null && !tags.isEmpty()) {
            sb.append(",\"tags\":[");
            boolean first = true;
            for (String tag : tags) {
                if (tag == null) {
                    continue;
                }
                if (!first) {
                    sb.append(',');
                }
                first = false;
                sb.append('"').append(escapeJson(tag)).append('"');
            }
            sb.append(']');
        } else {
            sb.append(",\"tags\":[]");
        }
        sb.append(",\"curr\":1,\"seq\":").append(sequence).append('}');
        return buildJsonCommandPayload(sb.toString());
    }

    /**
     * Alias (command = 29, version = 1):
     * string(json) where json is {"platform":"a","op":("set"|"get"|"del"),"alias":"..."}
     */
    public static String mapAliasActionToOp(int action) {
        switch (action) {
            case 2:
                return "set";
            case 3:
                return "del";
            case 5:
                return "get";
            default:
                return "set";
        }
    }

    public static int mapAliasOpToAction(String op) {
        if ("set".equals(op)) return 2;
        if ("del".equals(op)) return 3;
        if ("get".equals(op)) return 5;
        return 2;
    }

    public static byte[] buildAliasOperationPayload(int action, int sequence, String alias) {
        String op = mapAliasActionToOp(action);
        String json = "{\"platform\":\"a\",\"op\":\"" + op
            + "\",\"alias\":\"" + escapeJson(alias != null ? alias : "")
            + "\",\"seq\":" + sequence + "}";
        return buildJsonCommandPayload(json);
    }

    /**
     * User Properties (command = 38, version = 1):
     * uint8(1) + string(json) where json is {"op":("set"|"del"|"clean"),"data":{...}}
     */
    public static String mapPropertyActionToOp(int action) {
        switch (action) {
            case 1:
                return "set";
            case 2:
                return "del";
            case 3:
                return "clean";
            default:
                return "set";
        }
    }

    public static int mapPropertyOpToAction(String op) {
        if ("set".equals(op)) return 1;
        if ("del".equals(op)) return 2;
        if ("clean".equals(op)) return 3;
        return 1;
    }

    public static byte[] buildPropertyOperationPayload(int action, int sequence, Map<String, Object> properties) {
        String op = mapPropertyActionToOp(action);
        StringBuilder sb = new StringBuilder();
        sb.append("{\"op\":\"").append(op).append("\",\"data\":{");
        if (properties != null) {
            boolean first = true;
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                if (entry.getKey() == null) {
                    continue;
                }
                if (!first) {
                    sb.append(',');
                }
                first = false;
                sb.append('"').append(escapeJson(entry.getKey())).append("\":\"")
                    .append(escapeJson(entry.getValue() != null ? String.valueOf(entry.getValue()) : "")).append('"');
            }
        }
        sb.append("},\"seq\":").append(sequence).append('}');
        byte[] jsonBytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(5 + jsonBytes.length);
        buf.put((byte) 1);
        writeString(buf, sb.toString());
        return sliceUsed(buf);
    }

    public static byte[] buildJsonCommandPayload(String json) {
        String safe = json != null ? json : "{}";
        byte[] bytes = safe.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(4 + bytes.length);
        writeString(buf, safe);
        return sliceUsed(buf);
    }

    public static String parseJsonCommandPayload(byte[] body) {
        if (body == null || body.length == 0) {
            return "";
        }
        // Check for uint8(1) prefix (User Properties cmd=38 request)
        if (body.length >= 3 && body[0] == 1) {
            int len = ((body[1] & 0xFF) << 8) | (body[2] & 0xFF);
            if (len > 0 && 3 + len <= body.length && body[3] == '{') {
                return new String(body, 3, len, StandardCharsets.UTF_8);
            }
        }
        // Check for standard 2-byte length-prefixed string
        if (body.length >= 2) {
            int len = ((body[0] & 0xFF) << 8) | (body[1] & 0xFF);
            if (len > 0 && 2 + len <= body.length && body[2] == '{') {
                return new String(body, 2, len, StandardCharsets.UTF_8);
            }
        }
        // Check for uint16 statusCode + 2-byte length-prefixed string (cmd=38 response)
        if (body.length >= 4) {
            int len = ((body[2] & 0xFF) << 8) | (body[3] & 0xFF);
            if (len > 0 && 4 + len <= body.length && body[4] == '{') {
                return new String(body, 4, len, StandardCharsets.UTF_8);
            }
        }
        // Check for uint16 statusCode + raw JSON object string
        if (body.length >= 3 && body[2] == '{') {
            return new String(body, 2, body.length - 2, StandardCharsets.UTF_8).trim();
        }
        ByteBuffer buf = ByteBuffer.wrap(body);
        String str = readString(buf);
        if (!str.isEmpty()) {
            return str;
        }
        return new String(body, StandardCharsets.UTF_8);
    }

    public static String parseJsonCommandBody(byte[] body) {
        return parseJsonCommandPayload(body);
    }

    public static OperationPayload parseOperationPayload(int command, byte[] body) {
        String rawJson = parseJsonCommandPayload(body);
        if (rawJson.isEmpty()) {
            return null;
        }
        String op = extractJsonString(rawJson, "op");
        int action = extractJsonInt(rawJson, "action", 0);
        if (action == 0) {
            if (command == CMD_TAG_OP) {
                action = mapTagOpToAction(op);
            } else if (command == CMD_ALIAS_OP) {
                action = mapAliasOpToAction(op);
            } else if (command == CMD_PROPERTY_OP) {
                action = mapPropertyOpToAction(op);
            }
        }
        int seq = extractJsonInt(rawJson, "seq", 0);
        String alias = extractJsonString(rawJson, "alias");
        Set<String> tags = extractJsonStringArray(rawJson, "tags");
        Map<String, String> pros = extractJsonStringMap(rawJson, "data");
        if (pros.isEmpty()) {
            pros = extractJsonStringMap(rawJson, "pros");
        }
        return new OperationPayload(command, action, op, seq, alias, tags, pros, rawJson);
    }

    public static byte[] buildPushControlPayload(boolean stopped) {
        return new byte[] { (byte) (stopped ? 1 : 0) };
    }

    public static boolean parsePushControlPayload(byte[] body) {
        return body != null && body.length > 0 && body[0] != 0;
    }

    public static void writeString(ByteBuffer buffer, String value) {
        byte[] bytes = (value != null ? value : "").getBytes(StandardCharsets.UTF_8);
        int len = Math.min(65535, bytes.length);
        buffer.putShort((short) len);
        if (len > 0) {
            buffer.put(bytes, 0, len);
        }
    }

    public static String readString(ByteBuffer buffer) {
        if (buffer == null || buffer.remaining() < 2) {
            return "";
        }
        int len = buffer.getShort() & 0xFFFF;
        if (len <= 0 || buffer.remaining() < len) {
            return "";
        }
        byte[] bytes = new byte[len];
        buffer.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static byte[] sliceUsed(ByteBuffer buffer) {
        byte[] out = new byte[buffer.position()];
        buffer.flip();
        buffer.get(out);
        return out;
    }

    public static String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static int extractJsonInt(String json, String key, int defaultValue) {
        if (json == null || key == null) {
            return defaultValue;
        }
        String pattern = "\"" + key + "\":";
        int idx = json.indexOf(pattern);
        if (idx < 0) {
            return defaultValue;
        }
        int start = idx + pattern.length();
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }
        int end = start;
        if (end < json.length() && (json.charAt(end) == '-' || json.charAt(end) == '+')) {
            end++;
        }
        while (end < json.length() && Character.isDigit(json.charAt(end))) {
            end++;
        }
        if (end == start) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(json.substring(start, end));
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    public static String extractJsonString(String json, String key) {
        if (json == null || key == null) {
            return "";
        }
        String pattern = "\"" + key + "\":\"";
        int idx = json.indexOf(pattern);
        if (idx < 0) {
            return "";
        }
        int start = idx + pattern.length();
        StringBuilder out = new StringBuilder();
        boolean escaped = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                out.append(c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                return out.toString();
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    public static Set<String> extractJsonStringArray(String json, String key) {
        Set<String> out = new LinkedHashSet<String>();
        if (json == null || key == null) {
            return out;
        }
        String pattern = "\"" + key + "\":[";
        int idx = json.indexOf(pattern);
        if (idx < 0) {
            return out;
        }
        int start = idx + pattern.length();
        int end = json.indexOf(']', start);
        if (end <= start) {
            return out;
        }
        String content = json.substring(start, end);
        int pos = 0;
        while (pos < content.length()) {
            int q1 = content.indexOf('"', pos);
            if (q1 < 0) {
                break;
            }
            int q2 = content.indexOf('"', q1 + 1);
            if (q2 < 0) {
                break;
            }
            out.add(content.substring(q1 + 1, q2));
            pos = q2 + 1;
        }
        return out;
    }

    public static Map<String, String> extractJsonStringMap(String json, String key) {
        Map<String, String> out = new LinkedHashMap<String, String>();
        if (json == null || key == null) {
            return out;
        }
        String pattern = "\"" + key + "\":{";
        int idx = json.indexOf(pattern);
        if (idx < 0) {
            return out;
        }
        int start = idx + pattern.length();
        int end = json.indexOf('}', start);
        if (end <= start) {
            return out;
        }
        String content = json.substring(start, end);
        int pos = 0;
        while (pos < content.length()) {
            int k1 = content.indexOf('"', pos);
            if (k1 < 0) {
                break;
            }
            int k2 = content.indexOf('"', k1 + 1);
            if (k2 < 0) {
                break;
            }
            int v1 = content.indexOf('"', k2 + 1);
            if (v1 < 0) {
                break;
            }
            int v2 = content.indexOf('"', v1 + 1);
            if (v2 < 0) {
                break;
            }
            out.put(content.substring(k1 + 1, k2), content.substring(v1 + 1, v2));
            pos = v2 + 1;
        }
        return out;
    }
}
