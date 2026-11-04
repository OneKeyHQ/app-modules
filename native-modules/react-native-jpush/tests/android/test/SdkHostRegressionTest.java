package test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import cn.jiguang.android.IDataShare;
import cn.jiguang.api.JCoreInterface;
import cn.jiguang.api.MultiSpHelper;
import cn.jiguang.core.JCoreCrypto;
import cn.jiguang.core.JCoreStorage;
import cn.jiguang.core.JCoreTcpEngine;
import cn.jiguang.core.JCoreWireCodec;
import cn.jiguang.internal.JCoreInternalHelper;
import cn.jiguang.net.DefaultHostVerifier;
import cn.jiguang.plugins.core.JCoreModule;
import cn.jiguang.plugins.push.JPushModule;
import cn.jiguang.plugins.push.common.JConstants;
import cn.jiguang.plugins.push.common.JLogger;
import cn.jiguang.plugins.push.receiver.JPushModuleReceiver;
import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationCustomButton;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.local.ActionHelper;
import cn.jpush.android.service.DataProvider;
import cn.jpush.android.service.DataShare;
import cn.jpush.android.service.DownloadProvider;
import cn.jpush.android.service.JPushMessageReceiver;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.security.cert.X509Certificate;

public final class SdkHostRegressionTest {
    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Assertion failed: " + message);
        }
    }

    private static void testCryptoParity() throws Exception {
        // 1. Exact generateSeedKey math: idx = abs(seed) % 10; val = seed * multipliers[idx] + (seed % moduli[idx]); md5("JCKP" + val)
        long seed = 123456789L;
        // idx = 9 -> multiplier = 37, modulus = 91 -> 123456789 % 91 = 1 -> val = 123456789 * 37 + 1 = 4567901194
        String expectedSeedKey = JCoreCrypto.md5Hex("JCKP4567901194");
        String seedKey1 = JCoreCrypto.generateSeedKey(seed);
        check(seedKey1.length() == 32 && seedKey1.equals(expectedSeedKey), "generateSeedKey must match exact JCKP formula");

        // 2. AES-256-CBC with 32 ASCII bytes of generateSeedKey(seed) and first 16 ASCII bytes as IV
        byte[] plain = "OneKey-Clean-Room-JPush-Payload-2026".getBytes(StandardCharsets.UTF_8);
        byte[] aes256Cipher = JCoreCrypto.aes256CbcSeedEncrypt(plain, seed);
        check(aes256Cipher.length > 0, "AES-256-CBC seed encryption must succeed");
        byte[] aes256Plain = JCoreCrypto.aes256CbcSeedDecrypt(aes256Cipher, seed);
        check(Arrays.equals(plain, aes256Plain), "AES-256-CBC seed roundtrip must match");

        // 3. Pure-Java SM4-CBC verification against GM/T 0002-2012 standard vector & seed roundtrip
        byte[] sm4StdKey = JCoreCrypto.hexDecode("0123456789abcdeffedcba9876543210");
        byte[] sm4ZeroIv = new byte[16];
        byte[] sm4StdBlock = JCoreCrypto.hexDecode("0123456789abcdeffedcba9876543210");
        byte[] sm4StdEnc = JCoreCrypto.sm4CbcEncrypt(sm4StdBlock, sm4StdKey, sm4ZeroIv);
        String firstBlockHex = JCoreCrypto.hexEncode(Arrays.copyOf(sm4StdEnc, 16));
        check(
            "681edf34d206965e86b3e94f536e4246".equals(firstBlockHex),
            "SM4 block cipher must match GM/T 0002-2012 standard test vector, got: " + firstBlockHex
        );
        byte[] sm4StdDec = JCoreCrypto.sm4CbcDecrypt(sm4StdEnc, sm4StdKey, sm4ZeroIv);
        check(Arrays.equals(sm4StdBlock, sm4StdDec), "SM4-CBC standard vector decrypt must match");

        byte[] sm4SeedEnc = JCoreCrypto.sm4CbcSeedEncrypt(plain, seed);
        check(sm4SeedEnc.length > 0, "SM4-CBC seed encryption must succeed");
        byte[] sm4SeedDec = JCoreCrypto.sm4CbcSeedDecrypt(sm4SeedEnc, seed);
        check(Arrays.equals(plain, sm4SeedDec), "SM4-CBC seed roundtrip must match");

        // 4. HTTP Report AES-256-CBC (IV = Base64Decode("aW9wMjAzMDQwNTA2YVBrIQ==") = "iop203040506aPk!")
        byte[] httpCipher = JCoreCrypto.aes256CbcHttpEncrypt(plain, seedKey1);
        check(httpCipher.length > 0, "HTTP report AES-256-CBC encryption must succeed");
        byte[] httpDec = JCoreCrypto.aes256CbcHttpDecrypt(httpCipher, seedKey1);
        check(Arrays.equals(plain, httpDec), "HTTP report AES-256-CBC roundtrip must match");

        // 5. GZIP, RSA, MD5, SHA-1, HmacSHA256
        byte[] gz = JCoreCrypto.gzipCompress(plain);
        check(Arrays.equals(plain, JCoreCrypto.gzipDecompress(gz)), "GZIP roundtrip must match");

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair kp = kpg.generateKeyPair();
        String pubB64 = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
        String privB64 = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
        String rsaCipherB64 = JCoreCrypto.rsaEncryptBase64(seedKey1, pubB64);
        check(seedKey1.equals(JCoreCrypto.rsaDecryptBase64(rsaCipherB64, privB64)), "RSA roundtrip must match");

        check(JCoreCrypto.md5Hex("abc").equals("900150983cd24fb0d6963f7d28e17f72"), "MD5 vector must match");
        check(JCoreCrypto.sha1Hex("abc").equals("a9993e364706816aba3e25717850c26c9cd0d89d"), "SHA-1 vector must match");
    }

    private static void testUdpSis10ByteAndAsymmetricTcpWireCodec() throws Exception {
        // 1. UDP SIS 10-byte wire header verification:
        //    0..1: uint16 totalLen = 10 + encrypted.length
        //    2..3: 'U', 'G'
        //    4..7: uint32 ((0x11 << 24) | (seed & 0xffffff))
        //    8..9: uint16 preEncryptLen = compressed.length
        // Non-production demo test AppKey and sandbox package name used solely for automated native tests.
        final String demoTestAppKey = "1f2027d3cfb1b51dac664c42";
        final String demoTestPackageName = "com.devstudio.msgbench";
        String sisReqJson = JCoreWireCodec.buildSisRequestJson(
            1,
            demoTestAppKey,
            900001L,
            "46000",
            Arrays.asList("10.0.0.1:7000"),
            1
        );
        check(
            sisReqJson.contains("\"sdkver\":\"5.1.1\"")
                && sisReqJson.contains("\"fail_ips\":[\"10.0.0.1:7000\"]")
                && sisReqJson.contains("\"cert_ver\":1"),
            "SIS request JSON must match production fields"
        );
        int sisSeed = 0x123456;
        byte[] sisPacket = JCoreWireCodec.encodeUdpSisPacket(sisReqJson, sisSeed);
        ByteBuffer sisBuf = ByteBuffer.wrap(sisPacket);
        int sisTotalLen = sisBuf.getShort() & 0xFFFF;
        check(sisTotalLen == sisPacket.length, "UDP SIS bytes 0..1 must equal total packet length");
        check(sisBuf.get() == 'U' && sisBuf.get() == 'G', "UDP SIS bytes 2..3 must be 'U','G'");
        int sisKeyField = sisBuf.getInt();
        check(((sisKeyField >>> 24) & 0xFF) == 0x11, "UDP SIS byte 4 flags must be 0x11");
        check((sisKeyField & 0xFFFFFF) == sisSeed, "UDP SIS bytes 5..7 must be 24-bit seed");
        int sisPreEncryptLen = sisBuf.getShort() & 0xFFFF;
        check(sisPreEncryptLen > 0 && sisPreEncryptLen < sisTotalLen, "UDP SIS bytes 8..9 must be compressed length");
        check(sisReqJson.equals(JCoreWireCodec.decodeUdpSisPacket(sisPacket)), "UDP SIS 10-byte decode must match");

        // 2. Asymmetric TCP framing (24-byte Client -> Server header, 20-byte Server -> Client header)
        //    Verify both algorithm=1 (AES-256-CBC) and algorithm=2 (SM4-CBC)
        for (int alg : new int[] { JCoreWireCodec.ALG_AES_CBC, JCoreWireCodec.ALG_SM4_CBC }) {
            JCoreWireCodec.setDefaultAlgorithm(alg);

            // Register (cmd=0, ver=29)
            byte[] regReqBytes = JCoreWireCodec.buildRegisterPayload(
                demoTestAppKey,
                "developer-default",
                demoTestPackageName,
                "dev_001"
            );
            byte[] regClientFrame = JCoreWireCodec.encodeClientFrame(
                JCoreWireCodec.CMD_REGISTER,
                JCoreWireCodec.VER_REGISTER,
                alg,
                0,
                101L,
                7654321,
                0L,
                regReqBytes
            );
            ByteBuffer regHdr = ByteBuffer.wrap(regClientFrame);
            int regRawLen = regHdr.getShort() & 0xFFFF;
            check((regRawLen & 0x8000) != 0 && (regRawLen & 0x7FFF) == regClientFrame.length, "Client frame bit 15 and length");
            check((regHdr.get() & 0xFF) == 29 && (regHdr.get() & 0xFF) == 0 && (regHdr.get() & 0xFF) == alg, "Client Register header fields");

            JCoreWireCodec.DecodedFrame decRegReq = JCoreWireCodec.decodeClientFrame(regClientFrame, 0L);
            check(decRegReq != null && decRegReq.command == JCoreWireCodec.CMD_REGISTER && decRegReq.version == 29, "Decoded Register client frame");
            JCoreWireCodec.RegisterRequest parsedRegReq = JCoreWireCodec.parseRegisterRequest(decRegReq.body);
            check(
                parsedRegReq != null
                    && (" $$ $$" + demoTestPackageName + "$$" + demoTestAppKey).equals(parsedRegReq.key)
                    && demoTestAppKey.equals(parsedRegReq.appKey)
                    && demoTestPackageName.equals(parsedRegReq.packageName)
                    && "developer-default".equals(parsedRegReq.channel)
                    && "dev_001".equals(parsedRegReq.deviceId)
                    && parsedRegReq.regBusiness == 1,
                "Register v29 payload fields must match production spec"
            );

            byte[] regRespBytes = JCoreWireCodec.buildRegisterResponsePayload(900001L, "pw_secret", "reg_id_xyz", "srv_dev_001");
            byte[] regServerFrame = JCoreWireCodec.encodeResponseFrame(
                JCoreWireCodec.CMD_REGISTER,
                JCoreWireCodec.VER_REGISTER,
                alg,
                0,
                101L,
                decRegReq.sessionId,
                900001L,
                0,
                regRespBytes
            );
            JCoreWireCodec.DecodedFrame decRegResp = JCoreWireCodec.decodeServerFrame(regServerFrame, 900001L);
            JCoreWireCodec.RegisterResponse parsedRegResp = JCoreWireCodec.parseRegisterResponse(decRegResp);
            check(
                parsedRegResp != null
                    && parsedRegResp.statusCode == 0
                    && parsedRegResp.uid == 900001L
                    && "pw_secret".equals(parsedRegResp.password)
                    && "reg_id_xyz".equals(parsedRegResp.registrationId)
                    && "srv_dev_001".equals(parsedRegResp.deviceId),
                "RegisterResponse 20-byte server frame must match"
            );

            // Login (cmd=1, ver=27)
            byte[] loginReqBytes = JCoreWireCodec.buildLoginPayload("pw_secret", "appkey_123", "srv_dev_001", "US");
            byte[] loginClientFrame = JCoreWireCodec.encodeClientFrame(
                JCoreWireCodec.CMD_LOGIN,
                JCoreWireCodec.VER_LOGIN,
                alg,
                0,
                102L,
                0,
                900001L,
                loginReqBytes
            );
            JCoreWireCodec.DecodedFrame decLoginReq = JCoreWireCodec.decodeClientFrame(loginClientFrame, 900001L);
            JCoreWireCodec.LoginRequest parsedLoginReq = JCoreWireCodec.parseLoginRequest(decLoginReq.body);
            check(
                parsedLoginReq != null
                    && parsedLoginReq.passwordMd5.equals(JCoreCrypto.md5Hex("pw_secret"))
                    && "5.1.1|5.9.0".equals(parsedLoginReq.sdkVerInfo)
                    && "appkey_123".equals(parsedLoginReq.appKey)
                    && "srv_dev_001".equals(parsedLoginReq.deviceId)
                    && "US".equals(parsedLoginReq.countryCode)
                    && parsedLoginReq.loginFlag == 1
                    && "1".equals(parsedLoginReq.gSdkTypeInfo),
                "Login v27 payload fields must match production spec"
            );

            byte[] loginRespBytes = JCoreWireCodec.buildLoginResponsePayload(4242, 520, "", 1700000000);
            byte[] loginServerFrame = JCoreWireCodec.encodeResponseFrame(
                JCoreWireCodec.CMD_LOGIN,
                JCoreWireCodec.VER_LOGIN,
                alg,
                0,
                102L,
                4242,
                900001L,
                0,
                loginRespBytes
            );
            JCoreWireCodec.LoginResponse parsedLoginResp = JCoreWireCodec.parseLoginResponse(
                JCoreWireCodec.decodeServerFrame(loginServerFrame, 900001L)
            );
            check(
                parsedLoginResp != null
                    && parsedLoginResp.statusCode == 0
                    && parsedLoginResp.sessionId == 4242
                    && parsedLoginResp.serverVersion == 520,
                "LoginResponse 20-byte server frame must match"
            );

            // Heartbeat (cmd=2, ver=8): uint32(1) + uint8(appState) + uint16(290) + uint8(1)
            byte[] hbBytes = JCoreWireCodec.buildHeartbeatPayload(false);
            JCoreWireCodec.HeartbeatRequest hbReq = JCoreWireCodec.parseHeartbeatRequest(hbBytes);
            check(
                hbReq != null
                    && hbReq.loginFlag == 1
                    && hbReq.intervalSeconds == 290
                    && hbReq.sdkType == 1,
                "Heartbeat v8 8-byte payload must match"
            );

            // PushMessage (cmd=3) & MessageReceipt (cmd=4, ver=3)
            String pushJson = "header_line\n{\"_j_msgid\":\"777\",\"n_title\":\"Hello\",\"n_content\":\"World\"}";
            byte[] pushBytes = JCoreWireCodec.buildPushMessagePayload((byte) 0, 777L, pushJson);
            byte[] pushServerFrame = JCoreWireCodec.encodeServerFrame(
                JCoreWireCodec.CMD_PUSH_MESSAGE,
                JCoreWireCodec.VER_PUSH_MESSAGE,
                alg,
                0,
                104L,
                900001L,
                0L,
                pushBytes
            );
            JCoreWireCodec.PushEnvelope env = JCoreWireCodec.parsePushMessage(
                JCoreWireCodec.decodeServerFrame(pushServerFrame, 900001L)
            );
            check(env != null && env.messageId == 777L && pushJson.equals(env.content), "PushMessage roundtrip");

            byte[] ackBytes = JCoreWireCodec.buildMessageAckPayload((byte) 0, 777L);
            byte[] ackClientFrame = JCoreWireCodec.encodeClientFrame(
                JCoreWireCodec.CMD_MESSAGE_ACK,
                JCoreWireCodec.VER_MESSAGE_ACK,
                alg,
                0,
                104L,
                4242,
                900001L,
                ackBytes
            );
            JCoreWireCodec.DecodedFrame decAck = JCoreWireCodec.decodeClientFrame(ackClientFrame, 900001L);
            JCoreWireCodec.MessageAck ack = JCoreWireCodec.parseMessageAck(decAck);
            check(
                decAck.version == 3 && ack != null && ack.statusCode == 0 && ack.messageType == 0 && ack.messageId == 777L,
                "MessageReceipt (cmd=4, ver=3) roundtrip"
            );

            // CtrlCommand (cmd=25, ver=1) & Ack
            byte[] ctrlBytes = JCoreWireCodec.buildCtrlCommandPayload(88801L, "{\"cmd\":5}");
            JCoreWireCodec.CtrlCommand ctrlCmd = JCoreWireCodec.parseCtrlCommand(ctrlBytes);
            check(ctrlCmd != null && ctrlCmd.requestId == 88801L && "{\"cmd\":5}".equals(ctrlCmd.jsonContent), "CtrlCommand parse");
            byte[] ctrlAckBytes = JCoreWireCodec.buildCtrlAckPayload(0, 88801L);
            JCoreWireCodec.CtrlAck ctrlAck = JCoreWireCodec.parseCtrlAck(ctrlAckBytes);
            check(ctrlAck != null && ctrlAck.statusCode == 0 && ctrlAck.requestId == 88801L, "CtrlAck parse");

            // MobileNumber (cmd=26, ver=1): uint8(7) + uint8(1) + string(mobileNumber)
            byte[] mobBytes = JCoreWireCodec.buildMobileNumberPayload(15, "13800138000");
            JCoreWireCodec.MobileNumberPayload mob = JCoreWireCodec.parseMobileNumberPayload(mobBytes);
            check(
                mob != null && mob.typeFlag == 7 && mob.opFlag == 1 && "13800138000".equals(mob.mobileNumber),
                "MobileNumber (cmd=26, ver=1) roundtrip"
            );

            // ThirdToken (cmd=27, ver=2): string(thirdToken) + uint8(platform) + uint8(unbind ? 0 : 1)
            byte[] dtBytes = JCoreWireCodec.buildThirdTokenPayload("fcm_token_abc", (byte) 8, false);
            JCoreWireCodec.DeviceTokenPayload dt = JCoreWireCodec.parseDeviceTokenPayload(dtBytes);
            check(dt != null && "fcm_token_abc".equals(dt.token) && dt.platform == 8 && !dt.unbind, "ThirdToken (cmd=27, ver=2) roundtrip");

            // Tags (cmd=28, ver=1): {"platform":"a","op":"add","tags":["btc","eth"],"curr":1,...}
            byte[] tagBytes = JCoreWireCodec.buildTagOperationPayload(1, 21, Arrays.asList("btc", "eth"));
            JCoreWireCodec.OperationPayload tagOp = JCoreWireCodec.parseOperationPayload(JCoreWireCodec.CMD_TAG_OP, tagBytes);
            check(
                tagOp != null
                    && "add".equals(tagOp.op)
                    && tagOp.action == 1
                    && tagOp.tags.contains("btc")
                    && tagOp.tags.contains("eth"),
                "Tags (cmd=28, ver=1) roundtrip"
            );

            // Alias (cmd=29, ver=1): {"platform":"a","op":"set","alias":"satoshi",...}
            byte[] aliasBytes = JCoreWireCodec.buildAliasOperationPayload(2, 22, "satoshi");
            JCoreWireCodec.OperationPayload aliasOp = JCoreWireCodec.parseOperationPayload(JCoreWireCodec.CMD_ALIAS_OP, aliasBytes);
            check(
                aliasOp != null && "set".equals(aliasOp.op) && aliasOp.action == 2 && "satoshi".equals(aliasOp.alias),
                "Alias (cmd=29, ver=1) roundtrip"
            );

            // UserProperties (cmd=38, ver=1): uint8(1) + string({"op":"set","data":{"tier":"vip"}})
            Map<String, Object> props = new LinkedHashMap<String, Object>();
            props.put("tier", "vip");
            byte[] propBytes = JCoreWireCodec.buildPropertyOperationPayload(1, 23, props);
            check(propBytes[0] == 1, "UserProperties (cmd=38, ver=1) must start with uint8(1)");
            JCoreWireCodec.OperationPayload propOp = JCoreWireCodec.parseOperationPayload(JCoreWireCodec.CMD_PROPERTY_OP, propBytes);
            check(
                propOp != null && "set".equals(propOp.op) && "vip".equals(propOp.properties.get("tier")),
                "UserProperties (cmd=38, ver=1) roundtrip"
            );
        }
        JCoreWireCodec.setDefaultAlgorithm(JCoreWireCodec.ALG_AES_CBC);
    }

    private static void testTcpEngineEndToEndAndProductionResilience() throws Exception {
        final ReactApplicationContext reactContext = new ReactApplicationContext();
        final JCoreModule jcoreModule = new JCoreModule(reactContext);
        final JPushModule jpushModule = new JPushModule(reactContext);
        final JPushModuleReceiver moduleReceiver = new JPushModuleReceiver();
        ActionHelper.getInstance().clearRegisteredReceivers();
        ActionHelper.getInstance().registerReceiver(moduleReceiver);

        final AtomicReference<CmdMessage> thirdTokenCmdRef = new AtomicReference<CmdMessage>();
        final CountDownLatch thirdTokenLatch = new CountDownLatch(1);
        ActionHelper.getInstance().registerReceiver(new JPushMessageReceiver() {
            @Override
            public void onCommandResult(Context context, CmdMessage cmdMessage) {
                if (cmdMessage != null && cmdMessage.cmd == 10000) {
                    thirdTokenCmdRef.set(cmdMessage);
                    thirdTokenLatch.countDown();
                }
            }
        });

        // 1. Allocate a dead TCP port to verify Multi-IP Waterfall Failover & fail_ips recording
        ServerSocket deadSocket = new ServerSocket(0, 1, InetAddress.getByName("127.0.0.1"));
        final int deadPort = deadSocket.getLocalPort();
        deadSocket.close();
        final String deadEndpoint = "127.0.0.1:" + deadPort;

        // 2. Start a real loopback TCP ServerSocket for the live endpoint
        final ServerSocket serverSocket = new ServerSocket(0, 10, InetAddress.getByName("127.0.0.1"));
        final int tcpPort = serverSocket.getLocalPort();
        final String liveEndpoint = "127.0.0.1:" + tcpPort;

        final CountDownLatch firstSessionDoneLatch = new CountDownLatch(1);
        final AtomicReference<JCoreWireCodec.MessageAck> notifyAckRef = new AtomicReference<JCoreWireCodec.MessageAck>();
        final AtomicReference<JCoreWireCodec.MessageAck> customAckRef = new AtomicReference<JCoreWireCodec.MessageAck>();
        final AtomicReference<JCoreWireCodec.CtrlAck> ctrlAckRef = new AtomicReference<JCoreWireCodec.CtrlAck>();
        final AtomicBoolean sawPreLoginAliasOnWire = new AtomicBoolean(false);
        final AtomicBoolean sawThirdTokenOnWire = new AtomicBoolean(false);
        final AtomicReference<Throwable> serverError = new AtomicReference<Throwable>();

        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket client = null;
                try {
                    serverSocket.setSoTimeout(8000);
                    client = serverSocket.accept();
                    client.setSoTimeout(8000);
                    InputStream in = client.getInputStream();
                    OutputStream out = client.getOutputStream();

                    // A. Expect Register (cmd=0, ver=29) 24-byte client frame
                    JCoreWireCodec.DecodedFrame regFrame = JCoreWireCodec.readClientFrameFromStream(in, 0L);
                    check(
                        regFrame != null && regFrame.command == JCoreWireCodec.CMD_REGISTER && regFrame.version == 29,
                        "Server expected Register (cmd=0, ver=29)"
                    );
                    JCoreWireCodec.RegisterRequest regReq = JCoreWireCodec.parseRegisterRequest(regFrame.body);
                    check(
                        regReq != null
                            && "1f2027d3cfb1b51dac664c42".equals(regReq.appKey)
                            && "com.devstudio.msgbench".equals(regReq.packageName),
                        "Server parsed RegisterRequest appKey and sandbox packageName"
                    );

                    long assignedUid = 660001L;
                    byte[] regRespBody = JCoreWireCodec.buildRegisterResponsePayload(
                        assignedUid,
                        "loopback_pwd",
                        "1a0018970a_e2e_reg",
                        "dev_e2e_660001"
                    );
                    byte[] regRespPacket = JCoreWireCodec.encodeResponseFrame(
                        JCoreWireCodec.CMD_REGISTER,
                        JCoreWireCodec.VER_REGISTER,
                        regFrame.requestId,
                        regFrame.sessionId,
                        assignedUid,
                        0,
                        regRespBody
                    );
                    out.write(regRespPacket);
                    out.flush();

                    // B. Expect Login (cmd=1, ver=27) 24-byte client frame
                    JCoreWireCodec.DecodedFrame loginFrame = JCoreWireCodec.readClientFrameFromStream(in, assignedUid);
                    check(
                        loginFrame != null && loginFrame.command == JCoreWireCodec.CMD_LOGIN && loginFrame.version == 27,
                        "Server expected Login (cmd=1, ver=27)"
                    );
                    JCoreWireCodec.LoginRequest loginReq = JCoreWireCodec.parseLoginRequest(loginFrame.body);
                    check(
                        loginReq != null && loginReq.passwordMd5.equals(JCoreCrypto.md5Hex("loopback_pwd")),
                        "Server verified Login password MD5"
                    );

                    int assignedSid = 8801;
                    byte[] loginRespBody = JCoreWireCodec.buildLoginResponsePayload(assignedSid, 520, "", 1700000100);
                    byte[] loginRespPacket = JCoreWireCodec.encodeResponseFrame(
                        JCoreWireCodec.CMD_LOGIN,
                        JCoreWireCodec.VER_LOGIN,
                        loginFrame.requestId,
                        assignedSid,
                        assignedUid,
                        0,
                        loginRespBody
                    );
                    out.write(loginRespPacket);
                    out.flush();

                    // C. Expect Heartbeat (cmd=2, ver=8)
                    JCoreWireCodec.DecodedFrame hbFrame = JCoreWireCodec.readClientFrameFromStream(in, assignedUid);
                    check(
                        hbFrame != null && hbFrame.command == JCoreWireCodec.CMD_HEARTBEAT && hbFrame.version == 8,
                        "Server expected Heartbeat (cmd=2, ver=8)"
                    );
                    byte[] hbRespPacket = JCoreWireCodec.encodeResponseFrame(
                        JCoreWireCodec.CMD_HEARTBEAT,
                        JCoreWireCodec.VER_HEARTBEAT,
                        hbFrame.requestId,
                        assignedSid,
                        assignedUid,
                        0,
                        new byte[0]
                    );
                    out.write(hbRespPacket);
                    out.flush();

                    // D. Send Downlink Notification PushMessage (cmd=3, messageType=0, messageId=50001)
                    String notifyJson = "meta_line\n{\"_j_msgid\":\"50001\",\"n_title\":\"OneKey Security\",\"n_content\":\"Hardware wallet connected\",\"n_extras\":{\"chain\":\"btc\"}}";
                    byte[] notifyPushBody = JCoreWireCodec.buildPushMessagePayload((byte) 0, 50001L, notifyJson);
                    byte[] notifyPushPacket = JCoreWireCodec.encodeServerFrame(
                        JCoreWireCodec.CMD_PUSH_MESSAGE,
                        JCoreWireCodec.VER_PUSH_MESSAGE,
                        JCoreWireCodec.ALG_AES_CBC,
                        0,
                        501L,
                        assignedUid,
                        0L,
                        notifyPushBody
                    );
                    out.write(notifyPushPacket);
                    out.flush();

                    // E. Send Downlink Custom PushMessage (cmd=3, messageType=2, messageId=50002)
                    String customJson = "{\"_j_msgid\":\"50002\",\"title\":\"Sync\",\"message\":\"tx_confirmed\",\"extras\":{\"hash\":\"0x123\"}}";
                    byte[] customPushBody = JCoreWireCodec.buildPushMessagePayload((byte) 2, 50002L, customJson);
                    byte[] customPushPacket = JCoreWireCodec.encodeServerFrame(
                        JCoreWireCodec.CMD_PUSH_MESSAGE,
                        JCoreWireCodec.VER_PUSH_MESSAGE,
                        JCoreWireCodec.ALG_AES_CBC,
                        0,
                        502L,
                        assignedUid,
                        0L,
                        customPushBody
                    );
                    out.write(customPushPacket);
                    out.flush();

                    // F. Send Downlink CtrlCommand (cmd=25, ver=1, requestId=99001)
                    byte[] ctrlPushBody = JCoreWireCodec.buildCtrlCommandPayload(99001L, "{\"cmd\":10,\"enable\":true}");
                    byte[] ctrlPushPacket = JCoreWireCodec.encodeServerFrame(
                        JCoreWireCodec.CMD_CTRL,
                        JCoreWireCodec.VER_CTRL,
                        JCoreWireCodec.ALG_AES_CBC,
                        0,
                        503L,
                        assignedUid,
                        0L,
                        ctrlPushBody
                    );
                    out.write(ctrlPushPacket);
                    out.flush();

                    // G. Read client frames until we have seen:
                    //    1) MessageAck #1 (50001)
                    //    2) MessageAck #2 (50002)
                    //    3) CtrlAck (99001)
                    //    4) Pre-login queued Alias op (cmd=29)
                    //    5) Pre-login queued ThirdToken upload (cmd=27, ver=2)
                    while (notifyAckRef.get() == null
                        || customAckRef.get() == null
                        || ctrlAckRef.get() == null
                        || !sawPreLoginAliasOnWire.get()
                        || !sawThirdTokenOnWire.get()) {
                        JCoreWireCodec.DecodedFrame clientFrame = JCoreWireCodec.readClientFrameFromStream(in, assignedUid);
                        if (clientFrame == null) {
                            break;
                        }
                        if (clientFrame.command == JCoreWireCodec.CMD_MESSAGE_ACK) {
                            JCoreWireCodec.MessageAck ack = JCoreWireCodec.parseMessageAck(clientFrame);
                            if (ack != null && ack.messageId == 50001L) {
                                notifyAckRef.set(ack);
                            } else if (ack != null && ack.messageId == 50002L) {
                                customAckRef.set(ack);
                            }
                        } else if (clientFrame.command == JCoreWireCodec.CMD_CTRL) {
                            ctrlAckRef.set(JCoreWireCodec.parseCtrlAck(clientFrame.body));
                        } else if (clientFrame.command == JCoreWireCodec.CMD_ALIAS_OP) {
                            JCoreWireCodec.OperationPayload op = JCoreWireCodec.parseOperationPayload(
                                JCoreWireCodec.CMD_ALIAS_OP,
                                clientFrame.body
                            );
                            if (op != null && "queued_before_login".equals(op.alias)) {
                                sawPreLoginAliasOnWire.set(true);
                                byte[] aliasResp = JCoreWireCodec.buildJsonCommandPayload("{\"code\":0,\"op\":\"set\"}");
                                out.write(JCoreWireCodec.encodeServerFrame(
                                    JCoreWireCodec.CMD_ALIAS_OP,
                                    JCoreWireCodec.VER_ALIAS_OP,
                                    JCoreWireCodec.ALG_AES_CBC,
                                    0,
                                    clientFrame.requestId,
                                    assignedUid,
                                    0L,
                                    aliasResp
                                ));
                                out.flush();
                            }
                        } else if (clientFrame.command == JCoreWireCodec.CMD_THIRD_TOKEN) {
                            JCoreWireCodec.DeviceTokenPayload dt = JCoreWireCodec.parseDeviceTokenPayload(clientFrame.body);
                            if (dt != null && "fcm_prelogin_token_777".equals(dt.token)) {
                                sawThirdTokenOnWire.set(true);
                                out.write(JCoreWireCodec.encodeResponseFrame(
                                    JCoreWireCodec.CMD_THIRD_TOKEN,
                                    JCoreWireCodec.VER_THIRD_TOKEN,
                                    clientFrame.requestId,
                                    assignedSid,
                                    assignedUid,
                                    0,
                                    new byte[0]
                                ));
                                out.flush();
                            }
                        }
                    }
                    firstSessionDoneLatch.countDown();
                } catch (Throwable t) {
                    serverError.set(t);
                    firstSessionDoneLatch.countDown();
                } finally {
                    try {
                        if (client != null) client.close();
                    } catch (Throwable ignored) {}
                }
            }
        }, "test-jcore-loopback-server-1");
        serverThread.setDaemon(true);
        serverThread.start();

        // Configure candidate endpoints [deadEndpoint, liveEndpoint] to verify Multi-IP Waterfall Failover
        JCoreStorage.clearFailedIps(reactContext);
        JCoreStorage.clearRegistration(reactContext);
        check("".equals(JPushInterface.getRegistrationID(reactContext)), "Registration ID must be empty before server Register (cmd=0)");
        check(JCoreStorage.getUid(reactContext) == 0L, "UID must be 0 before server Register (cmd=0)");
        check("".equals(JCoreStorage.getPassword(reactContext)), "Password must be empty before server Register (cmd=0)");
        JCoreStorage.setAppKey(reactContext, "1f2027d3cfb1b51dac664c42");
        JCoreTcpEngine.getInstance().setCandidateEndpoints(Arrays.asList(deadEndpoint, liveEndpoint));

        // Enqueue Alias (cmd=29) and ThirdToken (cmd=27) BEFORE calling init() to verify pre-login request queue flush!
        JPushInterface.setAlias(reactContext, 91, "queued_before_login");
        Bundle fcmToken = new Bundle();
        fcmToken.putString("token", "fcm_prelogin_token_777");
        fcmToken.putByte("platform", (byte) 8);
        cn.jpush.android.api.JThirdPlatFormInterface.doAction(
            reactContext, cn.jpush.android.api.JThirdPlatFormInterface.ACTION_REGISTER_TOKEN, fcmToken
        );
        check("".equals(JCoreStorage.getThirdToken(reactContext, (byte) 8)), "FCM token must not be persisted before server acknowledgement");
        check(JCoreTcpEngine.getInstance().getPreLoginQueueSize() >= 2, "Pre-login requests must be queued before login");

        jpushModule.init();

        check(JCoreTcpEngine.getInstance().awaitHandshake(5000L), "JCoreTcpEngine handshake must complete within 5s");
        check(firstSessionDoneLatch.await(5000L, TimeUnit.MILLISECONDS), "Server session #1 must receive all expected frames");
        if (serverError.get() != null) {
            throw new AssertionError("Loopback server #1 failed: " + serverError.get().getMessage(), serverError.get());
        }

        // Verify deadEndpoint was recorded in failedIps and liveEndpoint was saved as lastGoodIp
        List<String> failedIps = JCoreStorage.getFailedIps(reactContext);
        check(failedIps.contains(deadEndpoint), "Multi-IP failover must record dead endpoint in failed_ips");
        check(liveEndpoint.equals(JCoreStorage.getLastGoodIp(reactContext)), "Multi-IP failover must save live endpoint as last_good_ip");

        // Verify UDP SIS receives fail_ips containing deadEndpoint and returns full SIS config
        final AtomicReference<String> sisReceivedReqRef = new AtomicReference<String>("");
        final DatagramSocket sisServer = new DatagramSocket(0, InetAddress.getByName("127.0.0.1"));
        final int sisPort = sisServer.getLocalPort();
        Thread sisThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] buf = new byte[2048];
                    DatagramPacket pkt = new DatagramPacket(buf, buf.length);
                    sisServer.receive(pkt);
                    byte[] received = Arrays.copyOf(pkt.getData(), pkt.getLength());
                    String reqJson = JCoreWireCodec.decodeUdpSisPacket(received);
                    sisReceivedReqRef.set(reqJson);
                    String respJson = "{\"ips\":[\"" + liveEndpoint + "\"],\"ssl_ips\":[\"" + liveEndpoint
                        + "\"],\"sis_ips\":[\"127.0.0.1:" + sisPort
                        + "\"],\"http_report\":[\"127.0.0.1:18080\"],\"https_report\":[\"127.0.0.1:18443\"],\"cert_ver\":2,\"data_report\":true}";
                    byte[] respBytes = JCoreWireCodec.encodeUdpSisPacket(respJson, 7777L);
                    DatagramPacket respPkt = new DatagramPacket(respBytes, respBytes.length, pkt.getAddress(), pkt.getPort());
                    sisServer.send(respPkt);
                } catch (Throwable ignored) {
                } finally {
                    sisServer.close();
                }
            }
        }, "test-udp-sis-server");
        sisThread.setDaemon(true);
        sisThread.start();

        String[] resolvedSis = JCoreTcpEngine.getInstance().resolveUdpSisEndpoint(reactContext, "127.0.0.1", sisPort);
        check("127.0.0.1".equals(resolvedSis[0]) && String.valueOf(tcpPort).equals(resolvedSis[1]), "UDP SIS loopback resolution");
        check(sisReceivedReqRef.get().contains(deadEndpoint), "UDP SIS request must include recorded fail_ips");
        check(JCoreStorage.getSisCertVer(reactContext) == 2 && JCoreStorage.getSisDataReport(reactContext), "SIS config must be persisted in JCoreStorage");

        // Verify MessageReceipt (cmd=4, ver=3), CtrlAck (cmd=25, ver=1), and ThirdToken callback (CmdMessage 10000)
        JCoreWireCodec.MessageAck ack1 = notifyAckRef.get();
        JCoreWireCodec.MessageAck ack2 = customAckRef.get();
        JCoreWireCodec.CtrlAck ctrlAck = ctrlAckRef.get();
        check(ack1 != null && ack1.statusCode == 0 && ack1.messageType == 0 && ack1.messageId == 50001L, "MessageReceipt #1");
        check(ack2 != null && ack2.statusCode == 0 && ack2.messageType == 2 && ack2.messageId == 50002L, "MessageReceipt #2");
        check(ctrlAck != null && ctrlAck.statusCode == 0 && ctrlAck.requestId == 99001L, "CtrlCommand Ack (cmd=25, ver=1)");
        check(thirdTokenLatch.await(3000L, TimeUnit.MILLISECONDS) && thirdTokenCmdRef.get() != null && thirdTokenCmdRef.get().errorCode == 0, "ThirdToken CmdMessage(10000) callback");
        check("fcm_prelogin_token_777".equals(JCoreStorage.getThirdToken(reactContext, (byte) 8)), "ThirdToken persisted in JCoreStorage");

        check("1a0018970a_e2e_reg".equals(JPushInterface.getRegistrationID(reactContext)), "Registration ID updated");
        check(JCoreStorage.getUid(reactContext) == 660001L, "UID updated");
        check(JCoreInterface.getSid() == 8801, "Session ID updated");

        // 3. Test Registered Device Fast-Login & Login statusCode == 108 automatic re-registration!
        final AtomicInteger secondSessionFirstCmd = new AtomicInteger(-1);
        final AtomicBoolean reRegisteredAfter108 = new AtomicBoolean(false);
        final CountDownLatch secondSessionLatch = new CountDownLatch(1);
        Thread serverThread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket client = null;
                try {
                    serverSocket.setSoTimeout(8000);
                    client = serverSocket.accept();
                    client.setSoTimeout(8000);
                    InputStream in = client.getInputStream();
                    OutputStream out = client.getOutputStream();

                    // Since device is already registered, first frame MUST be Login (cmd=1, ver=27) (Fast-Login)!
                    JCoreWireCodec.DecodedFrame firstFrame = JCoreWireCodec.readClientFrameFromStream(in, 660001L);
                    check(firstFrame != null, "Fast-login server expected frame");
                    secondSessionFirstCmd.set(firstFrame.command);
                    check(firstFrame.command == JCoreWireCodec.CMD_LOGIN, "Registered device must send Login (cmd=1) directly without Register (cmd=0)");

                    // Respond with statusCode = 108 to trigger automatic clearRegistration + Register(cmd=0) + Login(cmd=1)!
                    out.write(JCoreWireCodec.encodeResponseFrame(
                        JCoreWireCodec.CMD_LOGIN,
                        JCoreWireCodec.VER_LOGIN,
                        firstFrame.requestId,
                        0,
                        660001L,
                        108,
                        new byte[0]
                    ));
                    out.flush();

                    // Now expect Register (cmd=0, ver=29)
                    JCoreWireCodec.DecodedFrame reRegFrame = JCoreWireCodec.readClientFrameFromStream(in, 0L);
                    check(reRegFrame != null && reRegFrame.command == JCoreWireCodec.CMD_REGISTER, "After Login 108, client must immediately send Register (cmd=0)");
                    long newUid = 660002L;
                    byte[] reRegResp = JCoreWireCodec.buildRegisterResponsePayload(newUid, "new_pwd_108", "reg_after_108", "dev_e2e_660002");
                    out.write(JCoreWireCodec.encodeResponseFrame(
                        JCoreWireCodec.CMD_REGISTER,
                        JCoreWireCodec.VER_REGISTER,
                        reRegFrame.requestId,
                        reRegFrame.sessionId,
                        newUid,
                        0,
                        reRegResp
                    ));
                    out.flush();

                    // Now expect Login (cmd=1, ver=27) with newUid
                    JCoreWireCodec.DecodedFrame reLoginFrame = JCoreWireCodec.readClientFrameFromStream(in, newUid);
                    check(reLoginFrame != null && reLoginFrame.command == JCoreWireCodec.CMD_LOGIN, "After re-Register, client must send Login (cmd=1)");
                    byte[] reLoginResp = JCoreWireCodec.buildLoginResponsePayload(8802, 520, "", 1700000200);
                    out.write(JCoreWireCodec.encodeResponseFrame(
                        JCoreWireCodec.CMD_LOGIN,
                        JCoreWireCodec.VER_LOGIN,
                        reLoginFrame.requestId,
                        8802,
                        newUid,
                        0,
                        reLoginResp
                    ));
                    out.flush();

                    // Expect Heartbeat (cmd=2, ver=8)
                    JCoreWireCodec.DecodedFrame hbFrame = JCoreWireCodec.readClientFrameFromStream(in, newUid);
                    if (hbFrame != null && hbFrame.command == JCoreWireCodec.CMD_HEARTBEAT) {
                        out.write(JCoreWireCodec.encodeResponseFrame(
                            JCoreWireCodec.CMD_HEARTBEAT,
                            JCoreWireCodec.VER_HEARTBEAT,
                            hbFrame.requestId,
                            8802,
                            newUid,
                            0,
                            new byte[0]
                        ));
                        out.flush();
                    }
                    reRegisteredAfter108.set(true);
                    secondSessionLatch.countDown();
                } catch (Throwable t) {
                    serverError.set(t);
                    secondSessionLatch.countDown();
                } finally {
                    try {
                        if (client != null) client.close();
                    } catch (Throwable ignored) {}
                    try {
                        serverSocket.close();
                    } catch (Throwable ignored) {}
                }
            }
        }, "test-jcore-loopback-server-2");
        serverThread2.setDaemon(true);
        serverThread2.start();

        JCoreTcpEngine.getInstance().setCustomEndpoint("127.0.0.1", tcpPort);
        JCoreTcpEngine.getInstance().triggerReconnectNow(reactContext);
        check(JCoreTcpEngine.getInstance().awaitHandshake(5000L), "Second handshake (Fast-Login + 108 re-register) must complete");
        check(secondSessionLatch.await(5000L, TimeUnit.MILLISECONDS), "Second session server must complete");
        if (serverError.get() != null) {
            throw new AssertionError("Loopback server #2 failed: " + serverError.get().getMessage(), serverError.get());
        }
        check(secondSessionFirstCmd.get() == JCoreWireCodec.CMD_LOGIN, "Fast-login must skip Register cmd=0");
        check(reRegisteredAfter108.get() && "reg_after_108".equals(JPushInterface.getRegistrationID(reactContext)), "Login 108 must re-register and update registrationId");

        // Verify exponential backoff formula: 3s, 6s, 12s, 24s, 48s, 96s, capped at 120s
        check(JCoreTcpEngine.computeReconnectDelayMs(0) == 3000L, "Backoff attempt 0 = 3s");
        check(JCoreTcpEngine.computeReconnectDelayMs(1) == 6000L, "Backoff attempt 1 = 6s");
        check(JCoreTcpEngine.computeReconnectDelayMs(2) == 12000L, "Backoff attempt 2 = 12s");
        check(JCoreTcpEngine.computeReconnectDelayMs(6) == 120000L, "Backoff attempt 6 capped at 120s");

        // 4. Exercise React Native bridge methods on JCoreModule and JPushModule
        jcoreModule.setAuth(false);
        jcoreModule.enableAutoWakeup(false);
        WritableMap logCfg = Arguments.createMap();
        logCfg.putBoolean("enable", true);
        logCfg.putBoolean("uploadJgToServer", false);
        jcoreModule.enableSDKLocalLog(logCfg);
        final AtomicReference<String> readLogsRef = new AtomicReference<String>("");
        jcoreModule.readNewLogs(new Callback() {
            @Override
            public void invoke(Object... args) {
                if (args != null && args.length > 0 && args[0] instanceof ReadableMap) {
                    readLogsRef.set(((ReadableMap) args[0]).getString("logs"));
                }
            }
        });
        check(readLogsRef.get() != null && readLogsRef.get().contains("SDKLocalLog"), "JCoreModule.readNewLogs");

        jpushModule.setLinkMergeEnable(false);
        WritableMap tagMap = Arguments.createMap();
        tagMap.putInt(JConstants.SEQUENCE, 41);
        WritableArray tagArr = Arguments.createArray();
        tagArr.pushString("hardware_wallet");
        tagMap.putArray(JConstants.TAGS, tagArr);
        jpushModule.setTags(tagMap);

        WritableMap aliasMap = Arguments.createMap();
        aliasMap.putInt(JConstants.SEQUENCE, 42);
        aliasMap.putString(JConstants.ALIAS, "onekey_pro");
        jpushModule.setAlias(aliasMap);

        WritableMap localNotiMap = Arguments.createMap();
        localNotiMap.putString(JConstants.MESSAGE_ID, "901");
        localNotiMap.putString(JConstants.TITLE, "Local Title");
        localNotiMap.putString(JConstants.CONTENT, "Local Content");
        jpushModule.addLocalNotification(localNotiMap);

        boolean sawConnect = false;
        boolean sawRemoteNotification = false;
        boolean sawCustomMessage = false;
        boolean sawTagAlias = false;
        boolean sawLocalNotification = false;

        for (ReactApplicationContext.EmittedEvent ev : reactContext.emittedEvents) {
            if (JConstants.CONNECT_EVENT.equals(ev.name)) {
                sawConnect = true;
            } else if (JConstants.NOTIFICATION_EVENT.equals(ev.name) && ev.data instanceof ReadableMap) {
                ReadableMap m = (ReadableMap) ev.data;
                if ("50001".equals(m.getString(JConstants.MESSAGE_ID))
                    && "OneKey Security".equals(m.getString(JConstants.TITLE))) {
                    sawRemoteNotification = true;
                }
            } else if (JConstants.CUSTOM_MESSAGE_EVENT.equals(ev.name) && ev.data instanceof ReadableMap) {
                ReadableMap m = (ReadableMap) ev.data;
                if ("50002".equals(m.getString(JConstants.MESSAGE_ID))
                    && "tx_confirmed".equals(m.getString(JConstants.CONTENT))) {
                    sawCustomMessage = true;
                }
            } else if (JConstants.TAG_ALIAS_EVENT.equals(ev.name)) {
                sawTagAlias = true;
            } else if (JConstants.LOCAL_NOTIFICATION_EVENT.equals(ev.name)) {
                sawLocalNotification = true;
            }
        }

        check(sawConnect, "JPushModuleReceiver must emit ConnectEvent");
        check(sawRemoteNotification, "JPushModuleReceiver must emit NotificationEvent for downlink cmd=3 notification");
        check(sawCustomMessage, "JPushModuleReceiver must emit CustomMessageEvent for downlink cmd=3 custom message");
        check(sawTagAlias, "JPushModuleReceiver must emit TagAliasEvent");
        check(sawLocalNotification, "JPushModuleReceiver must emit LocalNotificationEvent for local notification");

        JCoreTcpEngine.getInstance().closeSocket();
    }

    private static void testFcmCallbacksAndPayloadBounds() throws Exception {
        Context context = new Context();
        JCoreStorage.setPushStopped(context, false);
        JCoreStorage.setCollectionAuth(context, true);
        String payload = "{\"_jmsgid_\":\"fcm_arrival_1\",\"n_title\":\"Synthetic title\",\"n_content\":\"Synthetic body\",\"n_extras\":{\"route\":\"test\"}}";
        String zipped = "{\"jg_c\":\"" + JCoreCrypto.base64Encode(JCoreCrypto.gzipCompress(payload.getBytes(StandardCharsets.UTF_8))) + "\"}";
        NotificationMessage parsed = cn.jpush.android.api.JThirdPlatFormInterface.parseMessage(context, zipped);
        check("fcm_arrival_1".equals(parsed.msgId) && parsed.platform == 8, "Compressed FCM envelope must retain identity and platform");
        check(parsed.notificationExtras.contains("route"), "FCM extras must survive decoding");
        StringBuilder oversized = new StringBuilder();
        for (int i = 0; i < 70000; i++) oversized.append('a');
        String oversizedZip = "{\"jg_c\":\"" + JCoreCrypto.base64Encode(JCoreCrypto.gzipCompress(oversized.toString().getBytes(StandardCharsets.UTF_8))) + "\"}";
        check(cn.jpush.android.api.JThirdPlatFormInterface.decodeMessageJson(oversizedZip) == null, "FCM inflated payload must be bounded");

        ActionHelper helper = ActionHelper.getInstance();
        helper.clearRegisteredReceivers();
        final AtomicInteger arrivals = new AtomicInteger();
        final AtomicInteger opens = new AtomicInteger();
        final AtomicInteger customs = new AtomicInteger();
        final AtomicReference<NotificationMessage> delivered = new AtomicReference<NotificationMessage>();
        helper.registerReceiver(new JPushMessageReceiver() {
            @Override public void onNotifyMessageArrived(Context c, NotificationMessage message) {
                arrivals.incrementAndGet(); delivered.set(message);
            }
            @Override public void onNotifyMessageOpened(Context c, NotificationMessage message) {
                opens.incrementAndGet();
            }
            @Override public void onMessage(Context c, CustomMessage message) {
                check("fcm_custom_1".equals(message.messageId) && message.platform == 8, "FCM custom message must retain identity and platform");
                check("Synthetic data".equals(message.message), "FCM custom body must reach the callback");
                customs.incrementAndGet();
            }
        });
        Bundle notification = new Bundle();
        notification.putString("data", zipped);
        notification.putInt("noti_id", 321);
        cn.jpush.android.api.JThirdPlatFormInterface.doAction(context, cn.jpush.android.api.JThirdPlatFormInterface.ACTION_NOTIFICATION_ARRIVED, notification);
        check(arrivals.get() == 1 && delivered.get().notificationId == 321, "Vendor-displayed FCM arrival must dispatch exactly once");
        android.content.Intent tap = new android.content.Intent(JPushInterface.ACTION_NOTIFICATION_OPENED);
        tap.putExtra("JMessageExtra", zipped);
        helper.handleNotificationIntent(context, tap);
        check(opens.get() == 1, "FCM PendingIntent extras must reach the opened callback");
        helper.handleNotificationIntent(context, new android.content.Intent("unrelated").putExtra("JMessageExtra", zipped));
        check(opens.get() == 1, "Unrelated intents must not produce opened callbacks");
        Bundle received = new Bundle();
        received.putString("JMessageExtra", zipped);
        cn.jpush.android.api.JThirdPlatFormInterface.sendActionByJCore(context, received, cn.jpush.android.api.JThirdPlatFormInterface.ACTION_PLUGIN_PALTFORM_ON_MESSAGING);
        check(arrivals.get() == 2 && "fcm_arrival_1".equals(delivered.get().msgId), "Source FCM receive entry must dispatch one arrival");
        received.putString("JMessageExtra", "{\"msg_id\":\"fcm_custom_1\",\"message\":\"Synthetic data\"}");
        cn.jpush.android.api.JThirdPlatFormInterface.sendActionByJCore(context, received, cn.jpush.android.api.JThirdPlatFormInterface.ACTION_PLUGIN_PALTFORM_ON_MESSAGING);
        check(customs.get() == 1 && arrivals.get() == 2, "FCM custom data must not become a notification");
        JCoreStorage.setPushStopped(context, true);
        cn.jpush.android.api.JThirdPlatFormInterface.doAction(context, cn.jpush.android.api.JThirdPlatFormInterface.ACTION_NOTIFICATION_ARRIVED, notification);
        check(arrivals.get() == 2, "Stopped push must suppress FCM callbacks");
        check("0".equals(cn.jpush.android.api.JThirdPlatFormInterface.parseMessage(context, payload).displayForeground), "Stopped push must suppress vendor-controlled display");
        JCoreStorage.setPushStopped(context, false);
        JCoreStorage.setCollectionAuth(context, false);
        cn.jpush.android.api.JThirdPlatFormInterface.doAction(context, cn.jpush.android.api.JThirdPlatFormInterface.ACTION_NOTIFICATION_ARRIVED, notification);
        check(arrivals.get() == 2, "Unauthorized collection must suppress FCM callbacks");
        JCoreStorage.setCollectionAuth(context, true);
        helper.clearRegisteredReceivers();


    }

    private static void testHttpReportChannelAndMultiProcessIpc() throws Exception {
        final ReactApplicationContext context = new ReactApplicationContext();
        JCoreStorage.setAppContext(context);
        JCoreStorage.setAppKey(context, "onekey_report_appkey");
        JCoreStorage.updateRegistration(context, 770001L, "report_pwd_123", "reg_report_770001");

        // 1. Start a local loopback HTTP server for /v3/report
        final ServerSocket httpServer = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"));
        final int httpPort = httpServer.getLocalPort();
        final AtomicReference<String> receivedAppKey = new AtomicReference<String>("");
        final AtomicReference<String> decryptedReportJson = new AtomicReference<String>("");
        final AtomicBoolean authSignVerified = new AtomicBoolean(false);

        Thread httpThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket client = null;
                try {
                    httpServer.setSoTimeout(5000);
                    client = httpServer.accept();
                    client.setSoTimeout(5000);
                    InputStream in = client.getInputStream();
                    OutputStream out = client.getOutputStream();

                    // Read HTTP headers byte-by-byte until \r\n\r\n
                    ByteArrayOutputStream headerBuf = new ByteArrayOutputStream();
                    int b;
                    int matchState = 0;
                    while ((b = in.read()) != -1) {
                        headerBuf.write(b);
                        if ((matchState == 0 || matchState == 2) && b == '\r') {
                            matchState++;
                        } else if ((matchState == 1 || matchState == 3) && b == '\n') {
                            matchState++;
                            if (matchState == 4) {
                                break;
                            }
                        } else {
                            matchState = (b == '\r') ? 1 : 0;
                        }
                    }
                    String headersStr = new String(headerBuf.toByteArray(), StandardCharsets.UTF_8);
                    int contentLen = 0;
                    String authHeader = "";
                    for (String line : headersStr.split("\r\n")) {
                        String lower = line.toLowerCase();
                        if (lower.startsWith("content-length:")) {
                            contentLen = Integer.parseInt(line.substring(15).trim());
                        } else if (lower.startsWith("x-app-key:")) {
                            receivedAppKey.set(line.substring(10).trim());
                        } else if (lower.startsWith("authorization:")) {
                            authHeader = line.substring(14).trim();
                        }
                    }

                    byte[] bodyBytes = new byte[contentLen];
                    int readOffset = 0;
                    while (readOffset < contentLen) {
                        int r = in.read(bodyBytes, readOffset, contentLen - readOffset);
                        if (r == -1) break;
                        readOffset += r;
                    }

                    if (authHeader.startsWith("Basic ")) {
                        String decodedAuth = new String(
                            Base64.getDecoder().decode(authHeader.substring(6).trim()),
                            StandardCharsets.UTF_8
                        );
                        String[] parts = decodedAuth.split(":");
                        if (parts.length == 3) {
                            String uidStr = parts[0];
                            String signSha1 = parts[1];
                            String aesKey = parts[2];
                            String expectedSign = JCoreCrypto.sha1Hex(
                                uidStr + JCoreCrypto.md5Hex("report_pwd_123") + JCoreCrypto.sha1Hex(bodyBytes)
                            );
                            if (expectedSign.equals(signSha1)) {
                                authSignVerified.set(true);
                            }
                            byte[] decrypted = JCoreCrypto.aes256CbcHttpDecrypt(bodyBytes, aesKey);
                            byte[] plain = JCoreCrypto.gzipDecompress(decrypted);
                            decryptedReportJson.set(new String(plain, StandardCharsets.UTF_8));
                        }
                    }

                    byte[] httpResp = (
                        "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "Content-Length: 10\r\n\r\n"
                        + "{\"code\":0}"
                    ).getBytes(StandardCharsets.UTF_8);
                    out.write(httpResp);
                    out.flush();
                } catch (Throwable ignored) {
                } finally {
                    try {
                        if (client != null) client.close();
                    } catch (Throwable ignored) {}
                    try {
                        httpServer.close();
                    } catch (Throwable ignored) {}
                }
            }
        }, "test-http-report-server");
        httpThread.setDaemon(true);
        httpThread.start();

        JCoreTcpEngine.getInstance().setCustomReportUrl("http://127.0.0.1:" + httpPort + "/v3/report");
        boolean reportOk = JCoreTcpEngine.getInstance().sendHttpReport(
            context,
            "{\"msg_id\":\"50001\",\"result\":1028,\"itime\":1700000300}"
        );
        check(reportOk, "sendHttpReport must receive HTTP 200 OK");
        check("onekey_report_appkey".equals(receivedAppKey.get()), "HTTP report must send X-App-Key header");
        check(authSignVerified.get(), "HTTP report Authorization Basic signature sha1(uid + md5(pwd) + sha1(body)) must verify");
        check(
            decryptedReportJson.get().contains("\"platform\":\"a\"")
                && decryptedReportJson.get().contains("\"msg_id\":\"50001\"")
                && decryptedReportJson.get().contains("\"core\":\"5.1.1\""),
            "HTTP report decrypted & gunzipped payload must match production envelope"
        );

        // 2. Verify :pushcore Multi-Process IPC (DataShare, DownloadProvider, DataProvider, MultiSpHelper)
        DataShare share = new DataShare();
        DataShare.init(share, ":pushcore");
        check(DataShare.getInstance(":pushcore") == share, "DataShare.getInstance(:pushcore) must return bound instance");
        check("push_aidl".equals(share.bind(share, "push_aidl")), "DataShare.bind must register aidl by type");
        IBinder binder = share.getBinderByType("push_aidl", "exec");
        check(binder == share, "DataShare.getBinderByType must return DataShare binder");

        Bundle setBundle = new Bundle();
        setBundle.putString("key", "ipc_shared_key");
        setBundle.putString("value", "ipc_shared_val_99");
        share.onAction("JPUSH", "sp_set", setBundle);

        Bundle getBundle = new Bundle();
        getBundle.putString("key", "ipc_shared_key");
        Bundle getResult = share.execute("JPUSH", "sp_get", getBundle);
        check(
            getResult != null && "ipc_shared_val_99".equals(getResult.getString("value")),
            "DataShare.onAction/execute must synchronize key-value state via JCoreInternalHelper"
        );

        Bundle regResult = share.execute("INTERNAL_API", "get_reg_id", new Bundle());
        check(
            regResult != null && "reg_report_770001".equals(regResult.getString("reg_id")),
            "DataShare.execute(get_reg_id) must return registration ID"
        );

        DownloadProvider downloadProvider = new DownloadProvider();
        downloadProvider.setContext(context);
        check(downloadProvider.onCreate(), "DownloadProvider.onCreate must succeed");

        ContentValues cv = new ContentValues();
        cv.put("key", "provider_key_1");
        cv.put("value", "provider_val_1");
        downloadProvider.insert(null, cv);

        Cursor cursor = downloadProvider.query(null, null, "provider_key_1", null, null);
        check(
            cursor != null && cursor.moveToFirst() && "provider_val_1".equals(cursor.getString(1)),
            "DownloadProvider.insert & query must synchronize JCoreStorage SP values"
        );

        DataProvider dataProvider = new DataProvider();
        dataProvider.setContext(context);
        dataProvider.onCreate();
        Bundle callOut = dataProvider.call("INTERNAL_API", "get_reg_id", new Bundle());
        check(
            callOut != null && "reg_report_770001".equals(callOut.getString("reg_id")),
            "DataProvider.call must route to JCoreInternalHelper.directHandle"
        );

        MultiSpHelper.commitString(context, "multi_sp_str", "hello_multi_sp");
        check("hello_multi_sp".equals(MultiSpHelper.getString(context, "multi_sp_str", "")), "MultiSpHelper string roundtrip");

        // 3. Verify Per-Request Timeout (6002) and ThirdToken 3x Retry on a real loopback server
        final ServerSocket retryServer = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"));
        final int retryPort = retryServer.getLocalPort();
        final AtomicInteger thirdTokenAttempts = new AtomicInteger(0);
        final AtomicInteger pushCtrlReceived = new AtomicInteger(0);
        final CountDownLatch pushCtrlLatch = new CountDownLatch(1);
        Thread retryServerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket client = null;
                try {
                    retryServer.setSoTimeout(5000);
                    client = retryServer.accept();
                    client.setSoTimeout(5000);
                    InputStream in = client.getInputStream();
                    OutputStream out = client.getOutputStream();

                    // Fast-Login (device is already registered)
                    JCoreWireCodec.DecodedFrame loginFrame = JCoreWireCodec.readClientFrameFromStream(in, 770001L);
                    if (loginFrame != null && loginFrame.command == JCoreWireCodec.CMD_LOGIN) {
                        byte[] loginResp = JCoreWireCodec.buildLoginResponsePayload(9901, 520, "", 1700000400);
                        out.write(JCoreWireCodec.encodeResponseFrame(
                            JCoreWireCodec.CMD_LOGIN,
                            JCoreWireCodec.VER_LOGIN,
                            loginFrame.requestId,
                            9901,
                            770001L,
                            0,
                            loginResp
                        ));
                        out.flush();
                    }

                    while (!client.isClosed()) {
                        JCoreWireCodec.DecodedFrame frame = JCoreWireCodec.readClientFrameFromStream(in, 770001L);
                        if (frame == null) break;
                        if (frame.command == JCoreWireCodec.CMD_HEARTBEAT) {
                            out.write(JCoreWireCodec.encodeResponseFrame(
                                JCoreWireCodec.CMD_HEARTBEAT,
                                JCoreWireCodec.VER_HEARTBEAT,
                                frame.requestId,
                                9901,
                                770001L,
                                0,
                                new byte[0]
                            ));
                            out.flush();
                        } else if (frame.command == JCoreWireCodec.CMD_THIRD_TOKEN) {
                            int attempt = thirdTokenAttempts.incrementAndGet();
                            // Ignore attempt 1 and 2 to force timeout & retry; ack on attempt 3!
                            if (attempt >= 3) {
                                out.write(JCoreWireCodec.encodeResponseFrame(
                                    JCoreWireCodec.CMD_THIRD_TOKEN,
                                    JCoreWireCodec.VER_THIRD_TOKEN,
                                    frame.requestId,
                                    9901,
                                    770001L,
                                    0,
                                    new byte[0]
                                ));
                                out.flush();
                            }
                        } else if (frame.command == JCoreWireCodec.CMD_MOBILE_NUMBER) {
                            // Intentionally do not respond so client triggers 6002 timeout
                        } else if (frame.command == JCoreWireCodec.CMD_PUSH_CTRL) {
                            pushCtrlReceived.incrementAndGet();
                            pushCtrlLatch.countDown();
                        }
                    }
                } catch (Throwable ignored) {
                } finally {
                    try {
                        if (client != null) client.close();
                    } catch (Throwable ignored) {}
                    try {
                        retryServer.close();
                    } catch (Throwable ignored) {}
                }
            }
        }, "test-retry-server");
        retryServerThread.setDaemon(true);
        retryServerThread.start();

        JCoreStorage.setServerRegistered(context, true);
        JCoreTcpEngine.getInstance().setDefaultRequestTimeoutMs(120L);
        JCoreTcpEngine.getInstance().setCustomEndpoint("127.0.0.1", retryPort);
        JCoreTcpEngine.getInstance().triggerReconnectNow(context);
        check(JCoreTcpEngine.getInstance().awaitHandshake(3000L), "Retry server fast-login handshake");

        final CountDownLatch retryCmdLatch = new CountDownLatch(1);
        final AtomicInteger retryFinalCode = new AtomicInteger(-1);
        JCoreTcpEngine.getInstance().enqueueOrSendRequest(
            context,
            JCoreWireCodec.CMD_THIRD_TOKEN,
            JCoreWireCodec.VER_THIRD_TOKEN,
            JCoreStorage.nextRid(),
            JCoreWireCodec.buildThirdTokenPayload("retry_token_xyz", (byte) 2, false),
            120L,
            3,
            new JCoreTcpEngine.RequestCallback() {
                @Override
                public void onResult(JCoreWireCodec.DecodedFrame responseFrame, int statusCode) {
                    retryFinalCode.set(statusCode);
                    retryCmdLatch.countDown();
                }
            }
        );
        check(retryCmdLatch.await(3000L, TimeUnit.MILLISECONDS), "ThirdToken retry request must complete");
        check(thirdTokenAttempts.get() == 3 && retryFinalCode.get() == 0, "ThirdToken must retry and succeed on 3rd attempt");

        // Verify timeout 6002 when server does not respond and maxRetries = 0
        final CountDownLatch timeoutLatch = new CountDownLatch(1);
        final AtomicInteger timeoutCode = new AtomicInteger(0);
        JCoreTcpEngine.getInstance().enqueueOrSendRequest(
            context,
            JCoreWireCodec.CMD_MOBILE_NUMBER,
            JCoreWireCodec.VER_MOBILE_NUMBER,
            JCoreStorage.nextRid(),
            JCoreWireCodec.buildMobileNumberPayload(55, "13900001111"),
            80L,
            0,
            new JCoreTcpEngine.RequestCallback() {
                @Override
                public void onResult(JCoreWireCodec.DecodedFrame responseFrame, int statusCode) {
                    timeoutCode.set(statusCode);
                    timeoutLatch.countDown();
                }
            }
        );
        check(timeoutLatch.await(2000L, TimeUnit.MILLISECONDS), "Unanswered request must time out");
        check(timeoutCode.get() == JPushInterface.ErrorCode.TIMEOUT, "Timed-out request must return ErrorCode.TIMEOUT (6002)");
        JPushInterface.stopPush(context);
        check(
            pushCtrlLatch.await(2000L, TimeUnit.MILLISECONDS) && pushCtrlReceived.get() == 1,
            "JPushInterface.stopPush must transmit CMD_PUSH_CTRL before closing the socket"
        );
        JCoreStorage.setPushStopped(context, false);
        JCoreTcpEngine.getInstance().setDefaultRequestTimeoutMs(5000L);
        JCoreTcpEngine.getInstance().closeSocket();
    }

    private static SSLSession createDummySession(final boolean verifiedPeers) {
        return new SSLSession() {
            @Override public byte[] getId() { return new byte[0]; }
            @Override public SSLSessionContext getSessionContext() { return null; }
            @Override public long getCreationTime() { return 0; }
            @Override public long getLastAccessedTime() { return 0; }
            @Override public void invalidate() {}
            @Override public boolean isValid() { return verifiedPeers; }
            @Override public void putValue(String s, Object o) {}
            @Override public Object getValue(String s) { return null; }
            @Override public void removeValue(String s) {}
            @Override public String[] getValueNames() { return new String[0]; }
            @Override
            public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
                if (!verifiedPeers) {
                    throw new SSLPeerUnverifiedException("Peer not verified");
                }
                return new Certificate[0];
            }
            @Override public Certificate[] getLocalCertificates() { return null; }
            @Override public X509Certificate[] getPeerCertificateChain() { return null; }
            @Override public Principal getPeerPrincipal() { return null; }
            @Override public Principal getLocalPrincipal() { return null; }
            @Override public String getCipherSuite() { return "TLS_AES_128_GCM_SHA256"; }
            @Override public String getProtocol() { return "TLSv1.3"; }
            @Override public String getPeerHost() { return "tsis.jpush.cn"; }
            @Override public int getPeerPort() { return 443; }
            @Override public int getPacketBufferSize() { return 0; }
            @Override public int getApplicationBufferSize() { return 0; }
        };
    }

    private static void testDefaultHostVerifier() {
        DefaultHostVerifier verifier = new DefaultHostVerifier("tsis.jpush.cn");
        check(!verifier.verify("tsis.jpush.cn", null), "DefaultHostVerifier must reject null SSLSession");
        check(!verifier.verify("", createDummySession(true)), "DefaultHostVerifier must reject empty hostname");
        check(!verifier.verify(null, createDummySession(true)), "DefaultHostVerifier must reject null hostname");
        check(!verifier.verify("attacker.example.com", createDummySession(true)), "DefaultHostVerifier must reject mismatched hostname");
        check(!verifier.verify("tsis.jpush.cn", createDummySession(false)), "DefaultHostVerifier must reject unverified SSLSession");

        HostnameVerifier original = HttpsURLConnection.getDefaultHostnameVerifier();
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return false;
                }
            });
            check(!verifier.verify("tsis.jpush.cn", createDummySession(true)), "DefaultHostVerifier must reject when default verifier rejects");
        } finally {
            HttpsURLConnection.setDefaultHostnameVerifier(original);
        }
    }

    private static void testAutoReconnectScheduleAndBinderSecurity() throws Exception {
        final ReactApplicationContext context = new ReactApplicationContext();
        JCoreStorage.setAppContext(context);
        JCoreStorage.setPushStopped(context, false);
        check(JCoreTcpEngine.getInstance().isAutoReconnectEnabled(), "autoReconnectEnabled must default to true");

        // 1. Verify automatic reconnect when remote server drops an established TCP connection
        final ServerSocket dropServer = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"));
        final int dropPort = dropServer.getLocalPort();
        final AtomicInteger loginSessions = new AtomicInteger(0);
        final CountDownLatch secondSessionLatch = new CountDownLatch(1);

        Thread dropServerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dropServer.setSoTimeout(5000);
                    for (int sessionIdx = 0; sessionIdx < 2; sessionIdx++) {
                        Socket client = dropServer.accept();
                        client.setSoTimeout(5000);
                        InputStream in = client.getInputStream();
                        OutputStream out = client.getOutputStream();
                        JCoreWireCodec.DecodedFrame loginFrame = JCoreWireCodec.readClientFrameFromStream(in, 770001L);
                        if (loginFrame != null && loginFrame.command == JCoreWireCodec.CMD_LOGIN) {
                            int sid = 9950 + sessionIdx;
                            byte[] loginResp = JCoreWireCodec.buildLoginResponsePayload(sid, 520, "", 1700000500);
                            out.write(JCoreWireCodec.encodeResponseFrame(
                                JCoreWireCodec.CMD_LOGIN,
                                JCoreWireCodec.VER_LOGIN,
                                loginFrame.requestId,
                                sid,
                                770001L,
                                0,
                                loginResp
                            ));
                            out.flush();
                        }
                        JCoreWireCodec.DecodedFrame hbFrame = JCoreWireCodec.readClientFrameFromStream(in, 770001L);
                        if (hbFrame != null && hbFrame.command == JCoreWireCodec.CMD_HEARTBEAT) {
                            int sid = 9950 + sessionIdx;
                            out.write(JCoreWireCodec.encodeResponseFrame(
                                JCoreWireCodec.CMD_HEARTBEAT,
                                JCoreWireCodec.VER_HEARTBEAT,
                                hbFrame.requestId,
                                sid,
                                770001L,
                                0,
                                new byte[0]
                            ));
                            out.flush();
                        }
                        int count = loginSessions.incrementAndGet();
                        if (count == 1) {
                            // Drop first session immediately from server side to trigger client auto-reconnect!
                            client.close();
                        } else {
                            secondSessionLatch.countDown();
                            Thread.sleep(50L);
                            client.close();
                        }
                    }
                } catch (Throwable ignored) {
                } finally {
                    try {
                        dropServer.close();
                    } catch (Throwable ignored) {}
                }
            }
        }, "test-drop-reconnect-server");
        dropServerThread.setDaemon(true);
        dropServerThread.start();

        JCoreStorage.setServerRegistered(context, true);
        JCoreTcpEngine.getInstance().setReconnectBaseDelayMs(25L);
        JCoreTcpEngine.getInstance().setCustomEndpoint("127.0.0.1", dropPort);
        JCoreTcpEngine.getInstance().triggerReconnectNow(context);
        check(secondSessionLatch.await(3000L, TimeUnit.MILLISECONDS), "Dropped TCP session must automatically reconnect");
        check(loginSessions.get() >= 2, "Engine must complete second handshake after dropped connection");
        JCoreTcpEngine.getInstance().setReconnectBaseDelayMs(3000L);
        JCoreTcpEngine.getInstance().closeSocket();

        // 2. Verify notification schedule (pushTime & silenceTime) and latestNotificationNumber eviction
        java.util.Set<Integer> allowedDays = new java.util.HashSet<Integer>(Arrays.asList(1, 3, 5));
        JPushInterface.setPushTime(context, allowedDays, 9, 18);
        check(JCoreStorage.isPushTimeAllowed(context, 1, 10), "Monday 10:00 must be allowed");
        check(!JCoreStorage.isPushTimeAllowed(context, 1, 20), "Monday 20:00 must be outside push time");
        check(!JCoreStorage.isPushTimeAllowed(context, 2, 10), "Tuesday 10:00 must be disallowed by day filter");
        JPushInterface.setPushTime(context, null, 0, 23);
        check(JCoreStorage.isPushTimeAllowed(context, 2, 22), "Reset pushTime must allow all times");

        JPushInterface.setSilenceTime(context, 22, 30, 7, 0);
        check(JCoreStorage.isInSilenceTime(context, 23, 15), "23:15 must be inside overnight silence window");
        check(JCoreStorage.isInSilenceTime(context, 6, 45), "06:45 must be inside overnight silence window");
        check(!JCoreStorage.isInSilenceTime(context, 12, 0), "12:00 must be outside overnight silence window");
        JPushInterface.setSilenceTime(context, 0, 0, 0, 0);
        check(!JCoreStorage.isInSilenceTime(context, 23, 15), "00:00-00:00 must disable silence time");

        ActionHelper.getInstance().clearActiveNotificationIds();
        JPushInterface.setLatestNotificationNumber(context, 2);
        for (int id = 101; id <= 103; id++) {
            cn.jpush.android.api.NotificationMessage msg = new cn.jpush.android.api.NotificationMessage();
            msg.notificationId = id;
            msg.notificationTitle = "Title " + id;
            msg.notificationContent = "Body " + id;
            ActionHelper.getInstance().postSystemNotification(context, msg);
        }
        List<Integer> activeIds = ActionHelper.getInstance().getActiveNotificationIds();
        check(
            activeIds.size() == 2 && !activeIds.contains(101) && activeIds.contains(102) && activeIds.contains(103),
            "setLatestNotificationNumber(2) must evict oldest notification 101"
        );

        // 3. Verify DataShare rejects cross-UID Binder callers
        DataShare share = new DataShare();
        DataShare.setTestCallerUids(20001, 10000);
        boolean rejectedCrossUid = false;
        try {
            share.execute("INTERNAL_API", "get_reg_id", new Bundle());
        } catch (SecurityException expected) {
            rejectedCrossUid = true;
        } finally {
            DataShare.setTestCallerUids(-1, -1);
        }
        check(rejectedCrossUid, "DataShare must throw SecurityException when callingUid != myUid");

        // 4. Verify default logging is enabled and never logs sensitive notification content or PII
        check(JLogger.isLoggerEnable(), "JLogger must be enabled by default");
        JLogger.clearRecentLogs();
        JPushModuleReceiver receiver = new JPushModuleReceiver();

        NotificationMessage secretNotify = new NotificationMessage();
        secretNotify.msgId = "msg_99001";
        secretNotify.notificationId = 8801;
        secretNotify.notificationType = 0;
        secretNotify.notificationTitle = "SECRET_WALLET_TITLE";
        secretNotify.notificationContent = "SECRET_TRANSFER_AMOUNT_100_BTC";
        secretNotify.notificationExtras = "{\"txHash\":\"0xdeadbeef_secret\"}";
        receiver.onNotifyMessageArrived(context, secretNotify);
        receiver.onNotifyMessageOpened(context, secretNotify);
        receiver.onNotifyMessageDismiss(context, secretNotify);

        CustomMessage secretCustom = new CustomMessage();
        secretCustom.messageId = "custom_99002";
        secretCustom.platform = (byte) 0;
        secretCustom.title = "SECRET_CUSTOM_TITLE";
        secretCustom.message = "SECRET_CUSTOM_BODY";
        secretCustom.extra = "{\"privateNote\":\"do_not_log\"}";
        receiver.onMessage(context, secretCustom);

        JPushMessage secretOp = new JPushMessage();
        secretOp.setSequence(77);
        secretOp.setErrorCode(0);
        secretOp.setAlias("SECRET_USER_ALIAS");
        secretOp.setMobileNumber("13899998888");
        secretOp.setTags(new java.util.HashSet<String>(Arrays.asList("SECRET_VIP_TAG")));
        receiver.onAliasOperatorResult(context, secretOp);
        receiver.onTagOperatorResult(context, secretOp);
        receiver.onMobileNumberOperatorResult(context, secretOp);
        receiver.onRegister(context, "1a0018970a900100200");

        String joinedLogs = String.join("\n", JLogger.getRecentLogs());
        check(joinedLogs.contains("notificationArrived") && joinedLogs.contains("notificationType=0"), "Must log notification type");
        check(joinedLogs.contains("msg_99001") && joinedLogs.contains("1a0018970a900100200"), "Must allow msgId and registrationId");
        check(!joinedLogs.contains("SECRET_WALLET_TITLE"), "Must never log notificationTitle");
        check(!joinedLogs.contains("SECRET_TRANSFER_AMOUNT_100_BTC"), "Must never log notificationContent");
        check(!joinedLogs.contains("0xdeadbeef_secret"), "Must never log notificationExtras");
        check(!joinedLogs.contains("SECRET_CUSTOM_TITLE") && !joinedLogs.contains("SECRET_CUSTOM_BODY") && !joinedLogs.contains("do_not_log"), "Must never log custom message content");
        check(!joinedLogs.contains("SECRET_USER_ALIAS") && !joinedLogs.contains("13899998888") && !joinedLogs.contains("SECRET_VIP_TAG"), "Must never log alias, mobileNumber, or tags");

        // 5. Verify cold-start JPushModuleReceiver.onNotifyMessageOpened queues into PENDING_NITRO_EVENTS when JPushModule.reactContext == null
        ReactApplicationContext savedReactContext = JPushModule.reactContext;
        JPushModule.reactContext = null;
        final List<String> coldStartOpenedEvents = new java.util.ArrayList<String>();
        try {
            int drainId = cn.jiguang.plugins.push.helper.JPushHelper.registerNitroListener(
                new cn.jiguang.plugins.push.helper.JPushHelper.NitroEventListener() {
                    @Override
                    public void onEvent(String eventName, String payloadJson) {
                    }
                }
            );
            cn.jiguang.plugins.push.helper.JPushHelper.unregisterNitroListener(drainId);

            NotificationMessage coldStartOpenMsg = new NotificationMessage();
            coldStartOpenMsg.msgId = "msg_cold_open_001";
            coldStartOpenMsg.notificationId = 8809;
            coldStartOpenMsg.notificationType = 0;
            coldStartOpenMsg.notificationTitle = "Cold Start Title";
            coldStartOpenMsg.notificationContent = "Cold Start Body";
            receiver.onNotifyMessageOpened(context, coldStartOpenMsg);

            NotificationMessage coldStartLocalOpenMsg = new NotificationMessage();
            coldStartLocalOpenMsg.msgId = "msg_cold_local_002";
            coldStartLocalOpenMsg.notificationId = 8810;
            coldStartLocalOpenMsg.notificationType = 1;
            coldStartLocalOpenMsg.notificationTitle = "Local Cold Title";
            coldStartLocalOpenMsg.notificationContent = "Local Cold Body";
            receiver.onNotifyMessageOpened(context, coldStartLocalOpenMsg);

            int coldNitroId = cn.jiguang.plugins.push.helper.JPushHelper.registerNitroListener(
                new cn.jiguang.plugins.push.helper.JPushHelper.NitroEventListener() {
                    @Override
                    public void onEvent(String eventName, String payloadJson) {
                        coldStartOpenedEvents.add(eventName + ":" + payloadJson);
                    }
                }
            );
            cn.jiguang.plugins.push.helper.JPushHelper.unregisterNitroListener(coldNitroId);
        } finally {
            JPushModule.reactContext = savedReactContext;
        }
        check(
            coldStartOpenedEvents.size() == 2
                && coldStartOpenedEvents.get(0).startsWith("NotificationEvent:")
                && coldStartOpenedEvents.get(0).contains("notificationOpened")
                && coldStartOpenedEvents.get(1).startsWith("LocalNotificationEvent:")
                && coldStartOpenedEvents.get(1).contains("notificationOpened"),
            "JPushModuleReceiver.onNotifyMessageOpened must queue NotificationEvent and LocalNotificationEvent into PENDING_NITRO_EVENTS when JPushModule.reactContext == null"
        );
    }

    @org.junit.Test
    public void testSdkHostRegressions() throws Exception {
        testCryptoParity();
        testUdpSis10ByteAndAsymmetricTcpWireCodec();
        testTcpEngineEndToEndAndProductionResilience();
        testHttpReportChannelAndMultiProcessIpc();
        testFcmCallbacksAndPayloadBounds();
        testDefaultHostVerifier();
        testAutoReconnectScheduleAndBinderSecurity();
    }
}
