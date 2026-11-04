package cn.jiguang.core;

import android.content.Context;
import android.os.Bundle;
import cn.jiguang.plugins.push.common.JLogger;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.local.ActionHelper;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.json.JSONArray;
import org.json.JSONObject;

public final class JCoreTcpEngine {
    public interface RequestCallback {
        void onResult(JCoreWireCodec.DecodedFrame responseFrame, int statusCode);
    }

    private static final class QueuedRequest {
        final int command;
        final int version;
        final long rid;
        final byte[] body;
        final long timeoutMs;
        final int maxRetries;
        final RequestCallback callback;

        QueuedRequest(
            int command,
            int version,
            long rid,
            byte[] body,
            long timeoutMs,
            int maxRetries,
            RequestCallback callback
        ) {
            this.command = command;
            this.version = version;
            this.rid = rid;
            this.body = body != null ? body : new byte[0];
            this.timeoutMs = timeoutMs > 0L ? timeoutMs : 5000L;
            this.maxRetries = Math.max(0, maxRetries);
            this.callback = callback;
        }
    }

    private static final class ResponseSlot {
        final CountDownLatch latch = new CountDownLatch(1);
        volatile JCoreWireCodec.DecodedFrame response;

        void complete(JCoreWireCodec.DecodedFrame frame) {
            this.response = frame;
            this.latch.countDown();
        }
    }

    private static final JCoreTcpEngine INSTANCE = new JCoreTcpEngine();
    private static final String DEFAULT_HOST = "im64.jpush.cn";
    private static final int DEFAULT_PORT = 7000;
    private static final String SIS_URL = "https://tsis.jpush.cn/v3/srv";
    private static final String DEFAULT_REPORT_URL = "https://stats.jpush.cn/v3/report";
    private static final String SIS_RSA_PUBLIC_KEY =
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCi0v4VEUhOdfIEfFCrPC72pcDsQF/"
        + "luTmr4q34NY0EZYGKzfQuTrUAm916P52HCgF+342gjZ/Nvijts5543qYNyoLvgtu4NRcHJmuGI/"
        + "w7qifhfsivYeoEj9wYphXOyB9HUjzwn1BtAih+1RyUrcErCi249yabUOIKQygPZ9OXXQIDAQAB";
    private static final String SIS_AES_IV = "0102030405060708";

    private final ExecutorService ioExecutor = Executors.newCachedThreadPool(new ThreadFactory() {
        private int count = 0;

        @Override
        public synchronized Thread newThread(Runnable r) {
            Thread t = new Thread(r, "jcore-clean-net-" + (++count));
            t.setDaemon(true);
            return t;
        }
    });

    private final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(
        new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "jcore-clean-heartbeat");
                t.setDaemon(true);
                return t;
            }
        }
    );

    private final ConcurrentHashMap<Long, ResponseSlot> pendingRequests = new ConcurrentHashMap<Long, ResponseSlot>();
    private final Queue<QueuedRequest> preLoginQueue = new ConcurrentLinkedQueue<QueuedRequest>();
    private final List<String> candidateEndpoints = new CopyOnWriteArrayList<String>();
    private final List<JCoreWireCodec.CtrlCommand> receivedCtrlCommands =
        new CopyOnWriteArrayList<JCoreWireCodec.CtrlCommand>();
    private final Object socketLock = new Object();

    private volatile boolean started = false;
    private volatile boolean loggedIn = false;
    private volatile boolean autoReconnectEnabled = true;
    private volatile long reconnectBaseDelayMs = 3000L;
    private volatile long heartbeatIntervalMs = 290000L;
    private volatile ScheduledFuture<?> activeHeartbeatFuture;
    private volatile int reconnectAttempt = 0;
    private volatile long connectionGeneration = 0L;
    private volatile int sessionId = 1;
    private volatile String customHost = "";
    private volatile int customPort = 0;
    private volatile String customReportUrl = "";
    private volatile String customSisUrl = "";
    private volatile String defaultSisUdpHost = "s.jpush.cn";
    private volatile int defaultSisUdpPort = 19000;
    private volatile long defaultRequestTimeoutMs = 5000L;
    private volatile boolean requireServerAckForOperations = false;
    private volatile boolean testConnectionEnabled = false;
    private volatile Socket activeSocket;
    private volatile OutputStream activeOutput;
    private volatile CountDownLatch handshakeLatch = new CountDownLatch(1);

    private JCoreTcpEngine() {
    }

    public static JCoreTcpEngine getInstance() {
        return INSTANCE;
    }

    public void setTestConnectionEnabled(boolean enabled) {
        this.testConnectionEnabled = enabled;
    }

    public void setAutoReconnectEnabled(boolean enabled) {
        this.autoReconnectEnabled = enabled;
    }

    public boolean isAutoReconnectEnabled() {
        return autoReconnectEnabled;
    }

    public void setReconnectBaseDelayMs(long baseDelayMs) {
        if (baseDelayMs > 0L) {
            this.reconnectBaseDelayMs = baseDelayMs;
        }
    }

    public long getReconnectBaseDelayMs() {
        return reconnectBaseDelayMs;
    }

    public void setHeartbeatIntervalMs(long intervalMs) {
        if (intervalMs > 0L) {
            this.heartbeatIntervalMs = intervalMs;
        }
    }

    public long getHeartbeatIntervalMs() {
        return heartbeatIntervalMs;
    }

    private void cancelHeartbeatTimer() {
        ScheduledFuture<?> future = this.activeHeartbeatFuture;
        this.activeHeartbeatFuture = null;
        if (future != null) {
            future.cancel(false);
        }
    }

    private void startHeartbeatTimer(final Context context, final Socket targetSocket) {
        cancelHeartbeatTimer();
        long interval = this.heartbeatIntervalMs > 0L ? this.heartbeatIntervalMs : 290000L;
        this.activeHeartbeatFuture = heartbeatExecutor.scheduleWithFixedDelay(
            new Runnable() {
                @Override
                public void run() {
                    if (!loggedIn || activeSocket != targetSocket || targetSocket == null || targetSocket.isClosed()) {
                        cancelHeartbeatTimer();
                        return;
                    }
                    try {
                        long currentUid = JCoreStorage.getUid(context);
                        byte[] hbPayload = JCoreWireCodec.buildHeartbeatPayload(JCoreStorage.isPushStopped(context));
                        sendFrameRaw(
                            JCoreWireCodec.CMD_HEARTBEAT,
                            JCoreWireCodec.VER_HEARTBEAT,
                            JCoreStorage.nextRid(),
                            sessionId,
                            currentUid,
                            hbPayload
                        );
                    } catch (Throwable ignored) {
                    }
                }
            },
            interval,
            interval,
            TimeUnit.MILLISECONDS
        );
    }

    public int getReconnectAttempt() {
        return reconnectAttempt;
    }

    public static long computeReconnectDelayMs(int attempt) {
        return computeReconnectDelayMs(attempt, 3000L);
    }

    public static long computeReconnectDelayMs(int attempt, long baseDelayMs) {
        int cappedShift = Math.max(0, Math.min(attempt, 6));
        long effectiveBase = baseDelayMs > 0L ? baseDelayMs : 3000L;
        long delay = effectiveBase * (1L << cappedShift);
        return Math.min(delay, 120000L);
    }

    public void setDefaultRequestTimeoutMs(long timeoutMs) {
        if (timeoutMs > 0L) {
            this.defaultRequestTimeoutMs = timeoutMs;
        }
    }

    public long getDefaultRequestTimeoutMs() {
        return defaultRequestTimeoutMs;
    }

    public void setRequireServerAckForOperations(boolean require) {
        this.requireServerAckForOperations = require;
    }

    public boolean isRequireServerAckForOperations() {
        return requireServerAckForOperations;
    }

    public void setCustomReportUrl(String url) {
        this.customReportUrl = url != null ? url.trim() : "";
    }

    public String getCustomReportUrl() {
        return customReportUrl;
    }

    public void setCustomSisUrl(String url) {
        this.customSisUrl = url != null ? url.trim() : "";
    }

    public String getCustomSisUrl() {
        return customSisUrl;
    }

    public void setDefaultSisUdpEndpoint(String host, int port) {
        this.defaultSisUdpHost = (host != null && !host.trim().isEmpty()) ? host.trim() : "s.jpush.cn";
        this.defaultSisUdpPort = port > 0 ? port : 19000;
    }

    public List<JCoreWireCodec.CtrlCommand> getReceivedCtrlCommands() {
        return new ArrayList<JCoreWireCodec.CtrlCommand>(receivedCtrlCommands);
    }

    public void clearReceivedCtrlCommands() {
        receivedCtrlCommands.clear();
    }

    public synchronized void setCandidateEndpoints(List<String> ipPorts) {
        this.candidateEndpoints.clear();
        if (ipPorts != null) {
            for (String item : ipPorts) {
                if (item != null && !item.trim().isEmpty()) {
                    this.candidateEndpoints.add(item.trim());
                }
            }
        }
        if (!this.candidateEndpoints.isEmpty()) {
            this.testConnectionEnabled = true;
            String first = this.candidateEndpoints.get(0);
            String[] hp = splitHostPort(first);
            if (hp != null) {
                this.customHost = hp[0];
                this.customPort = Integer.parseInt(hp[1]);
            }
        }
        closeSocket();
        this.started = false;
        this.loggedIn = false;
        this.handshakeLatch = new CountDownLatch(1);
    }

    public synchronized void setCustomEndpoint(String host, int port) {
        this.customHost = host != null ? host.trim() : "";
        this.customPort = port;
        this.candidateEndpoints.clear();
        if (!this.customHost.isEmpty() && port > 0) {
            this.testConnectionEnabled = true;
            this.candidateEndpoints.add(this.customHost + ":" + port);
        }
        closeSocket();
        this.started = false;
        this.loggedIn = false;
        this.handshakeLatch = new CountDownLatch(1);
    }

    public String getCustomHost() {
        return customHost;
    }

    public int getCustomPort() {
        return customPort;
    }

    public int getSessionId() {
        return sessionId;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public int getPreLoginQueueSize() {
        return preLoginQueue.size();
    }

    public boolean awaitHandshake(long timeoutMs) {
        try {
            return handshakeLatch.await(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public synchronized void closeSocket() {
        this.connectionGeneration++;
        cancelHeartbeatTimer();
        synchronized (socketLock) {
            if (activeSocket != null) {
                try {
                    activeSocket.close();
                } catch (Throwable ignored) {
                }
                activeSocket = null;
                activeOutput = null;
            }
        }
        for (ResponseSlot slot : pendingRequests.values()) {
            slot.complete(null);
        }
        pendingRequests.clear();
        loggedIn = false;
    }

    public synchronized void stop(Context context) {
        closeSocket();
        this.started = false;
        JCoreStorage.setConnected(context, false);
    }

    public void start(final Context context) {
        JCoreStorage.setAppContext(context);
        final long startGen;
        final CountDownLatch latchForStart;
        synchronized (this) {
            if (started) {
                return;
            }
            started = true;
            handshakeLatch = new CountDownLatch(1);
            startGen = this.connectionGeneration;
            latchForStart = this.handshakeLatch;
        }
        if (JCoreStorage.isPushStopped(JCoreStorage.getAppContext(context))) {
            latchForStart.countDown();
            return;
        }
        ioExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (!started || startGen != connectionGeneration) {
                    latchForStart.countDown();
                    return;
                }
                Context app = JCoreStorage.getAppContext(context);
                if (JCoreStorage.isPushStopped(app)) {
                    latchForStart.countDown();
                    return;
                }
                connectAndHandshake(app);
            }
        });
    }

    public void triggerReconnectNow(final Context context) {
        final long reconnectGen;
        final CountDownLatch latchForReconnect;
        synchronized (this) {
            closeSocket();
            this.started = true;
            this.handshakeLatch = new CountDownLatch(1);
            reconnectGen = this.connectionGeneration;
            latchForReconnect = this.handshakeLatch;
        }
        ioExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (!started || reconnectGen != connectionGeneration) {
                    latchForReconnect.countDown();
                    return;
                }
                connectAndHandshake(JCoreStorage.getAppContext(context));
            }
        });
    }

    public void onNetworkConnected(Context context) {
        this.reconnectAttempt = 0;
        if (!this.loggedIn) {
            triggerReconnectNow(context);
        }
    }

    public void executeAsync(Runnable task) {
        if (task != null) {
            ioExecutor.execute(task);
        }
    }

    private boolean isPlainSocketEndpoint(String host, int port) {
        if (testConnectionEnabled) {
            return true;
        }
        if (port != 443) {
            // Jiguang TCP long-connection ports (7000-7009 / 8000-8009) speak 24B/20B AES/SM4 frames directly over TCP.
            return true;
        }
        if (host == null) {
            return false;
        }
        return "127.0.0.1".equals(host) || "localhost".equalsIgnoreCase(host) || "::1".equals(host);
    }

    private static String[] splitHostPort(String ipPort) {
        if (ipPort == null) {
            return null;
        }
        String trimmed = ipPort.trim();
        int colon = trimmed.lastIndexOf(':');
        if (colon <= 0 || colon + 1 >= trimmed.length()) {
            return null;
        }
        String host = trimmed.substring(0, colon).trim();
        String portStr = trimmed.substring(colon + 1).trim();
        try {
            int port = Integer.parseInt(portStr);
            if (port > 0 && port <= 65535) {
                return new String[] { host, String.valueOf(port) };
            }
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    private List<String> buildOrderedCandidateList(Context context) {
        Set<String> ordered = new LinkedHashSet<String>();
        for (String ep : candidateEndpoints) {
            if (ep != null && !ep.isEmpty()) {
                ordered.add(ep);
            }
        }
        if (customHost != null && !customHost.isEmpty() && customPort > 0) {
            ordered.add(customHost + ":" + customPort);
        }
        String lastGood = JCoreStorage.getLastGoodIp(context);
        if (lastGood != null && !lastGood.isEmpty() && ordered.isEmpty()) {
            ordered.add(lastGood);
        }
        for (String s : JCoreStorage.getSisIps(context)) {
            ordered.add(s);
        }
        for (String s : JCoreStorage.getSisSslIps(context)) {
            ordered.add(s);
        }
        List<String> failedIps = JCoreStorage.getFailedIps(context);
        if (failedIps == null || failedIps.isEmpty()) {
            return new ArrayList<String>(ordered);
        }
        List<String> healthy = new ArrayList<String>();
        List<String> failed = new ArrayList<String>();
        for (String ep : ordered) {
            if (failedIps.contains(ep)) {
                failed.add(ep);
            } else {
                healthy.add(ep);
            }
        }
        healthy.addAll(failed);
        return healthy;
    }

    private boolean hasHealthyCandidate(Context context, List<String> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return false;
        }
        List<String> failedIps = JCoreStorage.getFailedIps(context);
        if (failedIps == null || failedIps.isEmpty()) {
            return true;
        }
        for (String ep : candidates) {
            if (ep != null && !ep.isEmpty() && !failedIps.contains(ep)) {
                return true;
            }
        }
        return false;
    }

    public void connectAndHandshake(final Context context) {
        try {
            List<String> candidates = buildOrderedCandidateList(context);
            if (customPort <= 0 && candidateEndpoints.isEmpty()) {
                if (!hasHealthyCandidate(context, candidates)) {
                    String[] sisResolved = resolveSisEndpoint(context);
                    candidates = buildOrderedCandidateList(context);
                    if (sisResolved != null && sisResolved.length == 2) {
                        String resolvedEp = (sisResolved[0] + ":" + sisResolved[1]).trim();
                        if (!resolvedEp.isEmpty() && splitHostPort(resolvedEp) != null && !candidates.contains(resolvedEp)) {
                            candidates.add(0, resolvedEp);
                        }
                    }
                }
                if (!hasHealthyCandidate(context, candidates)) {
                    String defaultEndpoint = DEFAULT_HOST + ":" + DEFAULT_PORT;
                    if (!candidates.contains(defaultEndpoint)) {
                        candidates.add(defaultEndpoint);
                    }
                }
            }
            if (candidates.isEmpty()) {
                JCoreStorage.setConnected(context, false);
                return;
            }

            boolean connectedSuccess = false;
            for (String endpoint : candidates) {
                String[] hp = splitHostPort(endpoint);
                if (hp == null) {
                    continue;
                }
                String host = hp[0];
                int port = Integer.parseInt(hp[1]);
                if (tryConnectAndHandshakeEndpoint(context, host, port)) {
                    JCoreStorage.setLastGoodIp(context, host + ":" + port);
                    connectedSuccess = true;
                    break;
                } else {
                    JCoreStorage.recordFailedIp(context, host + ":" + port);
                }
            }

            if (!connectedSuccess) {
                JCoreStorage.setConnected(context, false);
                if (this.started && this.autoReconnectEnabled && !JCoreStorage.isPushStopped(context)) {
                    final long delayMs = computeReconnectDelayMs(reconnectAttempt++, reconnectBaseDelayMs);
                    final long scheduledGen = this.connectionGeneration;
                    heartbeatExecutor.schedule(new Runnable() {
                        @Override
                        public void run() {
                            if (!started || scheduledGen != connectionGeneration || loggedIn || JCoreStorage.isPushStopped(context)) {
                                return;
                            }
                            ioExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    if (!started || scheduledGen != connectionGeneration || loggedIn || JCoreStorage.isPushStopped(context)) {
                                        return;
                                    }
                                    connectAndHandshake(context);
                                }
                            });
                        }
                    }, delayMs, TimeUnit.MILLISECONDS);
                } else {
                    this.reconnectAttempt++;
                }
            }
        } finally {
            handshakeLatch.countDown();
        }
    }

    private boolean tryConnectAndHandshakeEndpoint(final Context context, String host, int port) {
        Socket socket = null;
        try {
            if (isPlainSocketEndpoint(host, port)) {
                socket = new Socket();
                socket.connect(new InetSocketAddress(host, port), 3000);
                socket.setSoTimeout(10000);
            } else {
                socket = createVerifiedTlsSocket(host, port);
            }

            final Socket connectedSocket = socket;
            final InputStream in = connectedSocket.getInputStream();
            final OutputStream out = connectedSocket.getOutputStream();
            synchronized (socketLock) {
                this.activeSocket = connectedSocket;
                this.activeOutput = out;
            }

            Thread readerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    readLoop(context, connectedSocket, in);
                }
            }, "jcore-tcp-reader");
            readerThread.setDaemon(true);
            readerThread.start();

            String appKey = JCoreStorage.getAppKey(context);
            if (appKey.isEmpty()) {
                appKey = "onekey_default_appkey";
            }
            String channel = JCoreStorage.getChannel(context);
            String pkgName = "so.onekey.app.wallet";
            if (context != null) {
                try {
                    String p = context.getPackageName();
                    if (p != null && !p.isEmpty()) {
                        pkgName = p;
                    }
                } catch (Throwable ignored) {
                }
            }
            String deviceId = JCoreStorage.getDeviceId(context);
            String countryCode = JCoreStorage.getCountryCode(context);

            // Registered Device Fast-Login check: skip Register (cmd=0) if already server-registered
            boolean fastLogin = JCoreStorage.isServerRegistered(context)
                && JCoreStorage.getUid(context) > 0L
                && !JCoreStorage.getPassword(context).isEmpty()
                && !JCoreStorage.getRegistrationId(context).isEmpty();

            if (!fastLogin) {
                if (!performRegisterHandshake(context, appKey, channel, pkgName, deviceId)) {
                    closeSocket();
                    return false;
                }
            }

            long uid = JCoreStorage.getUid(context);
            String password = JCoreStorage.getPassword(context);
            byte[] loginBody = JCoreWireCodec.buildLoginPayload(password, appKey, deviceId, countryCode);
            JCoreWireCodec.DecodedFrame loginFrame = sendRequestOnSocket(
                JCoreWireCodec.CMD_LOGIN,
                JCoreWireCodec.VER_LOGIN,
                0,
                uid,
                loginBody,
                defaultRequestTimeoutMs
            );
            if (loginFrame == null) {
                closeSocket();
                return false;
            }
            JCoreWireCodec.LoginResponse loginResp = JCoreWireCodec.parseLoginResponse(loginFrame);
            if (loginResp == null) {
                closeSocket();
                return false;
            }

            // Handle Login statusCode == 108 (clear registration and immediately re-register + login)
            if (loginResp.statusCode == 108) {
                JCoreStorage.clearRegistration(context);
                if (!performRegisterHandshake(context, appKey, channel, pkgName, deviceId)) {
                    closeSocket();
                    return false;
                }
                uid = JCoreStorage.getUid(context);
                password = JCoreStorage.getPassword(context);
                loginBody = JCoreWireCodec.buildLoginPayload(password, appKey, deviceId, countryCode);
                loginFrame = sendRequestOnSocket(
                    JCoreWireCodec.CMD_LOGIN,
                    JCoreWireCodec.VER_LOGIN,
                    0,
                    uid,
                    loginBody,
                    defaultRequestTimeoutMs
                );
                if (loginFrame == null) {
                    closeSocket();
                    return false;
                }
                loginResp = JCoreWireCodec.parseLoginResponse(loginFrame);
            } else if (loginResp.statusCode == 1012) {
                JCoreStorage.resetSisCache(context);
                closeSocket();
                return false;
            }

            if (loginResp == null || loginResp.statusCode != 0) {
                closeSocket();
                return false;
            }

            if (loginResp.sessionId != 0) {
                this.sessionId = loginResp.sessionId;
            } else if (loginFrame.sessionId != 0) {
                this.sessionId = loginFrame.sessionId;
            }

            this.loggedIn = true;
            this.reconnectAttempt = 0;
            int idleReadTimeoutMs = (int) Math.min(
                (long) Integer.MAX_VALUE,
                Math.max(320000L, this.heartbeatIntervalMs + 30000L)
            );
            try {
                if (!connectedSocket.isClosed()) {
                    connectedSocket.setSoTimeout(idleReadTimeoutMs);
                }
            } catch (Throwable ignored) {
            }
            JLogger.i(
                "TCP login success uid="
                    + uid
                    + " sid="
                    + this.sessionId
                    + " registrationId="
                    + JCoreStorage.getRegistrationId(context)
            );
            JCoreStorage.setConnected(context, true);
            ActionHelper.getInstance().dispatchConnectedEvent(context, true);

            byte[] hbBody = JCoreWireCodec.buildHeartbeatPayload(JCoreStorage.isPushStopped(context));
            sendRequestOnSocket(
                JCoreWireCodec.CMD_HEARTBEAT,
                JCoreWireCodec.VER_HEARTBEAT,
                this.sessionId,
                uid,
                hbBody,
                defaultRequestTimeoutMs
            );

            startHeartbeatTimer(context, connectedSocket);
            flushPreLoginQueue(context);
            return true;
        } catch (Throwable ignored) {
            closeSocket();
            return false;
        }
    }

    private boolean performRegisterHandshake(
        Context context,
        String appKey,
        String channel,
        String pkgName,
        String deviceId
    ) {
        int registerSeed = JCoreCrypto.nextRegisterSeed();
        JCoreWireCodec.setLastRegisterSeed(registerSeed);
        byte[] regBody = JCoreWireCodec.buildRegisterPayload(appKey, channel, pkgName, deviceId);
        JCoreWireCodec.DecodedFrame regFrame = sendRequestOnSocket(
            JCoreWireCodec.CMD_REGISTER,
            JCoreWireCodec.VER_REGISTER,
            registerSeed,
            0L,
            regBody,
            defaultRequestTimeoutMs
        );
        if (regFrame == null) {
            return false;
        }
        JCoreWireCodec.RegisterResponse regResp = JCoreWireCodec.parseRegisterResponse(regFrame);
        if (regResp == null || regResp.statusCode != 0 || regResp.uid <= 0L) {
            return false;
        }
        JCoreStorage.updateRegistration(
            context,
            regResp.uid,
            regResp.password,
            regResp.registrationId,
            regResp.deviceId
        );
        JCoreStorage.setServerRegistered(context, true);
        JLogger.i("TCP register success uid=" + regResp.uid + " registrationId=" + regResp.registrationId);
        ActionHelper.getInstance().dispatchRegisterEvent(context, regResp.registrationId);
        return true;
    }

    private void flushPreLoginQueue(final Context context) {
        QueuedRequest item;
        while ((item = preLoginQueue.poll()) != null) {
            final QueuedRequest req = item;
            ioExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    executeQueuedRequestWithRetry(context, req);
                }
            });
        }
    }

    public void enqueueOrSendRequest(
        final Context context,
        int command,
        int version,
        long rid,
        byte[] body,
        long timeoutMs,
        int maxRetries,
        RequestCallback callback
    ) {
        final long effectiveRid = rid > 0L ? rid : JCoreStorage.nextRid();
        final QueuedRequest req = new QueuedRequest(
            command,
            version,
            effectiveRid,
            body,
            timeoutMs > 0L ? timeoutMs : defaultRequestTimeoutMs,
            maxRetries,
            callback
        );
        if (!this.loggedIn || this.activeOutput == null) {
            preLoginQueue.offer(req);
            return;
        }
        ioExecutor.execute(new Runnable() {
            @Override
            public void run() {
                executeQueuedRequestWithRetry(context, req);
            }
        });
    }

    private void executeQueuedRequestWithRetry(Context context, QueuedRequest req) {
        long uid = JCoreStorage.getUid(context);
        int attempts = 0;
        while (attempts <= req.maxRetries) {
            attempts++;
            long ridToUse = (attempts == 1) ? req.rid : JCoreStorage.nextRid();
            JCoreWireCodec.DecodedFrame resp = sendRequestWithRidOnSocket(
                req.command,
                req.version,
                ridToUse,
                this.sessionId,
                uid,
                req.body,
                req.timeoutMs
            );
            if (resp != null) {
                int code = resp.statusCode;
                if ((req.command == JCoreWireCodec.CMD_TAG_OP
                    || req.command == JCoreWireCodec.CMD_ALIAS_OP
                    || req.command == JCoreWireCodec.CMD_PROPERTY_OP)
                    && resp.body != null && resp.body.length > 0) {
                    String json = JCoreWireCodec.parseJsonCommandPayload(resp.body);
                    code = JCoreWireCodec.extractJsonInt(json, "code", code);
                }
                if (req.callback != null) {
                    req.callback.onResult(resp, code);
                }
                return;
            }
        }
        if (req.callback != null) {
            req.callback.onResult(null, JPushInterface.ErrorCode.TIMEOUT);
        }
    }

    private void readLoop(final Context context, Socket socket, InputStream in) {
        try {
            while (!socket.isClosed()) {
                long defaultUid = JCoreStorage.getUid(context);
                JCoreWireCodec.DecodedFrame frame = JCoreWireCodec.readFrameFromStream(in, defaultUid);
                if (frame == null) {
                    break;
                }
                handleIncomingFrame(context, frame);
            }
        } catch (Throwable ignored) {
        } finally {
            boolean wasCurrentSocket = false;
            boolean wasLoggedIn = false;
            boolean droppedUnexpectedly = false;
            synchronized (socketLock) {
                if (this.activeSocket == socket) {
                    wasCurrentSocket = true;
                    wasLoggedIn = this.loggedIn;
                    this.loggedIn = false;
                    droppedUnexpectedly = !socket.isClosed();
                    try {
                        socket.close();
                    } catch (Throwable ignored) {
                    }
                    this.activeSocket = null;
                    this.activeOutput = null;
                }
            }
            if (wasCurrentSocket) {
                cancelHeartbeatTimer();
                if (wasLoggedIn) {
                    JCoreStorage.setConnected(context, false);
                }
                for (ResponseSlot slot : pendingRequests.values()) {
                    slot.complete(null);
                }
                pendingRequests.clear();
                if (wasLoggedIn && autoReconnectEnabled && droppedUnexpectedly && !JCoreStorage.isPushStopped(context)) {
                    final long delayMs = computeReconnectDelayMs(reconnectAttempt++, reconnectBaseDelayMs);
                    final long scheduledGen = this.connectionGeneration;
                    heartbeatExecutor.schedule(new Runnable() {
                        @Override
                        public void run() {
                            if (!started || scheduledGen != connectionGeneration || loggedIn || JCoreStorage.isPushStopped(context)) {
                                return;
                            }
                            ioExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    if (!started || scheduledGen != connectionGeneration || loggedIn || JCoreStorage.isPushStopped(context)) {
                                        return;
                                    }
                                    connectAndHandshake(context);
                                }
                            });
                        }
                    }, delayMs, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    private void handleIncomingFrame(Context context, JCoreWireCodec.DecodedFrame frame) {
        if (frame.command == JCoreWireCodec.CMD_PUSH_MESSAGE) {
            JCoreWireCodec.PushEnvelope envelope = JCoreWireCodec.parsePushMessage(frame);
            if (envelope != null) {
                JLogger.i(
                    "TCP push message received msgType="
                        + envelope.messageType
                        + " msgId="
                        + envelope.messageId
                );
                long ackRid = frame.requestId != 0L ? frame.requestId : JCoreStorage.nextRid();
                long uid = frame.uid != 0L ? frame.uid : JCoreStorage.getUid(context);
                byte[] ackBody = JCoreWireCodec.buildMessageAckPayload(envelope.messageType, envelope.messageId);
                sendFrameRaw(
                    JCoreWireCodec.CMD_MESSAGE_ACK,
                    JCoreWireCodec.VER_MESSAGE_ACK,
                    ackRid,
                    this.sessionId,
                    uid,
                    ackBody
                );
                ActionHelper.getInstance().handleDownlinkPushMessage(
                    context,
                    envelope.messageType,
                    envelope.messageId,
                    envelope.content
                );
            }
            return;
        }

        if (frame.command == JCoreWireCodec.CMD_CTRL) {
            JCoreWireCodec.CtrlCommand ctrl = JCoreWireCodec.parseCtrlCommand(frame.body);
            if (ctrl != null) {
                receivedCtrlCommands.add(ctrl);
                long ackRid = frame.requestId != 0L ? frame.requestId : JCoreStorage.nextRid();
                long uid = frame.uid != 0L ? frame.uid : JCoreStorage.getUid(context);
                byte[] ackBody = JCoreWireCodec.buildCtrlAckPayload(0, ctrl.requestId);
                sendFrameRaw(
                    JCoreWireCodec.CMD_CTRL,
                    JCoreWireCodec.VER_CTRL,
                    ackRid,
                    this.sessionId,
                    uid,
                    ackBody
                );
            }
            return;
        }

        ResponseSlot slot = pendingRequests.remove(frame.requestId);
        if (slot != null) {
            slot.complete(frame);
        }
    }

    public boolean sendFrameRaw(int command, int version, long requestId, int sid, long uid, byte[] body) {
        byte[] packet = JCoreWireCodec.encodeFrame(command, version, requestId, sid, uid, body);
        if (packet.length == 0) {
            return false;
        }
        synchronized (socketLock) {
            if (activeOutput == null) {
                return false;
            }
            try {
                activeOutput.write(packet);
                activeOutput.flush();
                return true;
            } catch (Throwable ignored) {
                return false;
            }
        }
    }

    public JCoreWireCodec.DecodedFrame sendRequest(int command, int version, byte[] body, long timeoutMs) {
        long uid = JCoreStorage.getUid(null);
        return sendRequestOnSocket(command, version, this.sessionId, uid, body, timeoutMs);
    }

    private JCoreWireCodec.DecodedFrame sendRequestOnSocket(
        int command,
        int version,
        int sid,
        long uid,
        byte[] body,
        long timeoutMs
    ) {
        long rid = JCoreStorage.nextRid();
        return sendRequestWithRidOnSocket(command, version, rid, sid, uid, body, timeoutMs);
    }

    private JCoreWireCodec.DecodedFrame sendRequestWithRidOnSocket(
        int command,
        int version,
        long rid,
        int sid,
        long uid,
        byte[] body,
        long timeoutMs
    ) {
        ResponseSlot slot = new ResponseSlot();
        pendingRequests.put(rid, slot);
        if (!sendFrameRaw(command, version, rid, sid, uid, body)) {
            pendingRequests.remove(rid);
            return null;
        }
        try {
            slot.latch.await(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            pendingRequests.remove(rid);
        }
        return slot.response;
    }

    public String[] resolveUdpSisEndpoint(Context context, String sisHost, int sisPort) {
        DatagramSocket udpSocket = null;
        try {
            String requestJson = JCoreWireCodec.buildSisRequestJson(
                1,
                JCoreStorage.getAppKey(context),
                JCoreStorage.getUid(context),
                "",
                JCoreStorage.getFailedIps(context),
                JCoreStorage.getSisCertVer(context)
            );
            int seed24 = JCoreCrypto.nextPositiveSeed24();
            byte[] packetBytes = JCoreWireCodec.encodeUdpSisPacket(requestJson, seed24);

            udpSocket = new DatagramSocket();
            udpSocket.setSoTimeout(3000);
            InetAddress address = InetAddress.getByName(sisHost);
            DatagramPacket reqPacket = new DatagramPacket(packetBytes, packetBytes.length, address, sisPort);
            udpSocket.send(reqPacket);

            byte[] recvBuf = new byte[4096];
            DatagramPacket respPacket = new DatagramPacket(recvBuf, recvBuf.length);
            udpSocket.receive(respPacket);

            byte[] respBytes = new byte[respPacket.getLength()];
            System.arraycopy(recvBuf, 0, respBytes, 0, respBytes.length);
            String decodedJson = JCoreWireCodec.decodeUdpSisPacket(respBytes);
            if (!decodedJson.isEmpty()) {
                JCoreStorage.saveSisConfig(context, decodedJson);
            }
            Set<String> ips = JCoreWireCodec.extractJsonStringArray(decodedJson, "ips");
            if (ips.isEmpty()) {
                ips = JCoreWireCodec.extractJsonStringArray(decodedJson, "ssl_ips");
            }
            if (!ips.isEmpty()) {
                String first = ips.iterator().next();
                String[] hp = splitHostPort(first);
                if (hp != null) {
                    return hp;
                }
            }
        } catch (Throwable ignored) {
        } finally {
            if (udpSocket != null) {
                udpSocket.close();
            }
        }
        return new String[] { DEFAULT_HOST, String.valueOf(DEFAULT_PORT) };
    }

    public String[] resolveSisEndpoint(Context context) {
        if (customHost != null && !customHost.isEmpty() && customPort > 0) {
            return new String[] { customHost, String.valueOf(customPort) };
        }
        if (!"udp-only".equalsIgnoreCase(customSisUrl)) {
            OutputStream out = null;
            InputStream in = null;
            try {
                String aesKey = JCoreCrypto.generateHttpSeedKey();
                JSONObject body = new JSONObject();
                body.put("type", 1);
                body.put("opera", "");
                body.put("uid", JCoreStorage.getUid(context));
                body.put("appkey", JCoreStorage.getAppKey(context));
                body.put("sdkver", "5.1.1");
                body.put("platform", 0);

                byte[] compressed = JCoreCrypto.gzipCompress(body.toString().getBytes(StandardCharsets.UTF_8));
                byte[] encrypted = JCoreCrypto.aesCbcEncrypt(compressed, aesKey, SIS_AES_IV, 32);
                String auth = JCoreCrypto.rsaEncryptBase64(aesKey, SIS_RSA_PUBLIC_KEY);

                String targetSisUrl = (customSisUrl != null && !customSisUrl.isEmpty()) ? customSisUrl : SIS_URL;
                URL url = new URL(targetSisUrl);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier());
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", auth);

                out = conn.getOutputStream();
                out.write(encrypted);
                out.flush();

                if (conn.getResponseCode() == 200) {
                    in = conn.getInputStream();
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] chunk = new byte[2048];
                    int read;
                    while ((read = in.read(chunk)) != -1) {
                        buffer.write(chunk, 0, read);
                    }
                    byte[] decrypted = JCoreCrypto.aesCbcDecrypt(buffer.toByteArray(), aesKey, SIS_AES_IV, 32);
                    byte[] plain = JCoreCrypto.gzipDecompress(decrypted);
                    String plainStr = new String(plain, StandardCharsets.UTF_8);
                    JCoreStorage.saveSisConfig(context, plainStr);
                    Set<String> ips = JCoreWireCodec.extractJsonStringArray(plainStr, "ips");
                    if (ips.isEmpty()) {
                        ips = JCoreWireCodec.extractJsonStringArray(plainStr, "ssl_ips");
                    }
                    if (!ips.isEmpty()) {
                        String first = ips.iterator().next();
                        String[] hp = splitHostPort(first);
                        if (hp != null) {
                            return hp;
                        }
                    }
                    JSONObject json = new JSONObject(plainStr);
                    JSONArray sslIps = json.optJSONArray("ssl_ips");
                    if (sslIps != null && sslIps.length() > 0) {
                        String first = sslIps.optString(0, "");
                        String[] hp = splitHostPort(first);
                        if (hp != null) {
                            return hp;
                        }
                    }
                }
            } catch (Throwable ignored) {
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (Throwable ignored) {
                }
            }
        }
        return resolveUdpSisEndpoint(context, defaultSisUdpHost, defaultSisUdpPort);
    }

    /**
     * HTTP Report Channel (/v3/report):
     * Builds envelope {"platform":"a","uid":uid,"app_key":appKey,"channel":channel,"app_version":"1.0.0",
     * "sdk_ver":{"push":"5.9.0","core":"5.1.1"},"content":[reportItem]}, GZIP-compresses, encrypts with
     * aes256CbcHttpEncrypt (IV = "iop203040506aPk!"), sets Authorization: Basic base64(uid:sha1(uid + md5(password) + sha1(body)):aesKey)
     * and X-App-Key: appKey.
     */
    public boolean sendHttpReport(Context context, String reportItemJson) {
        OutputStream out = null;
        InputStream in = null;
        try {
            long uid = JCoreStorage.getUid(context);
            String appKey = JCoreStorage.getAppKey(context);
            String channel = JCoreStorage.getChannel(context);
            String password = JCoreStorage.getPassword(context);

            String item = (reportItemJson != null && !reportItemJson.isEmpty()) ? reportItemJson : "{}";
            String envelopeJson = "{\"platform\":\"a\",\"uid\":" + uid
                + ",\"app_key\":\"" + JCoreWireCodec.escapeJson(appKey) + "\""
                + ",\"channel\":\"" + JCoreWireCodec.escapeJson(channel) + "\""
                + ",\"app_version\":\"1.0.0\""
                + ",\"sdk_ver\":{\"push\":\"5.9.0\",\"core\":\"5.1.1\"}"
                + ",\"content\":[" + item + "]}";

            byte[] compressed = JCoreCrypto.gzipCompress(envelopeJson.getBytes(StandardCharsets.UTF_8));
            String aesKey = JCoreCrypto.generateHttpSeedKey();
            byte[] encryptedBody = JCoreCrypto.aes256CbcHttpEncrypt(compressed, aesKey);
            if (encryptedBody.length == 0) {
                return false;
            }

            String bodySha1 = JCoreCrypto.sha1Hex(encryptedBody);
            String pwdMd5 = JCoreCrypto.md5Hex(password);
            String signSha1 = JCoreCrypto.sha1Hex(uid + pwdMd5 + bodySha1);
            String authRaw = uid + ":" + signSha1 + ":" + aesKey;
            String authHeader = "Basic " + JCoreCrypto.base64Encode(authRaw.getBytes(StandardCharsets.UTF_8));

            String targetUrl = customReportUrl;
            if (targetUrl == null || targetUrl.isEmpty()) {
                List<String> httpsReports = JCoreStorage.getSisHttpsReportIps(context);
                if (!httpsReports.isEmpty()) {
                    String hostPort = httpsReports.get(0);
                    targetUrl = hostPort.startsWith("http") ? hostPort : ("https://" + hostPort + "/v3/report");
                } else {
                    List<String> httpReports = JCoreStorage.getSisHttpReportIps(context);
                    if (!httpReports.isEmpty()) {
                        String hostPort = httpReports.get(0);
                        targetUrl = hostPort.startsWith("http") ? hostPort : ("http://" + hostPort + "/v3/report");
                    } else {
                        targetUrl = DEFAULT_REPORT_URL;
                    }
                }
            }

            URL url = new URL(targetUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn instanceof HttpsURLConnection) {
                ((HttpsURLConnection) conn).setHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier());
            }
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("X-App-Key", appKey);
            conn.setRequestProperty("Authorization", authHeader);

            out = conn.getOutputStream();
            out.write(encryptedBody);
            out.flush();

            int code = conn.getResponseCode();
            if (code >= 200 && code < 300) {
                in = conn.getInputStream();
                while (in.read() != -1) {
                    // Drain response
                }
                return true;
            }
        } catch (Throwable ignored) {
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Throwable ignored) {
            }
        }
        return false;
    }

    public boolean verifyTlsSession(SSLSocket socket, String host) {
        if (socket == null || host == null) {
            return false;
        }
        SSLSession session = socket.getSession();
        return session != null && HttpsURLConnection.getDefaultHostnameVerifier().verify(host, session);
    }

    public SSLSocket createVerifiedTlsSocket(String host, int port) throws Exception {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
        socket.setSoTimeout(8000);
        socket.startHandshake();
        if (!verifyTlsSession(socket, host)) {
            socket.close();
            throw new SecurityException("TLS hostname verification failed for " + host);
        }
        return socket;
    }

    private void broadcastConnectAndRegister(Context context) {
        String regId = JCoreStorage.getRegistrationId(context);
        ActionHelper.getInstance().dispatchRegisterEvent(context, regId);
        ActionHelper.getInstance().dispatchConnectedEvent(context, true);
    }

    public Bundle handleEvent(Context context, String sdkType, int cmd, String action, Bundle bundle, Object... extras) {
        Context app = JCoreStorage.getAppContext(context);
        switch (cmd) {
            case 4: // getRegistrationID
                return stringResult(JCoreStorage.getRegistrationId(app));
            case 6: // getChannel
                return stringResult(JCoreStorage.getChannel(app));
            case 7: // getAppKey
                return stringResult(JCoreStorage.getAppKey(app));
            case 8: // getDeviceId
                return stringResult(JCoreStorage.getDeviceId(app));
            case 11:
            case 12:
                if (extras != null) {
                    for (Object extra : extras) {
                        if (extra instanceof Runnable) {
                            executeAsync((Runnable) extra);
                        }
                    }
                }
                return null;
            case 17:
            case 50:
                if (extras != null && extras.length > 0 && extras[0] instanceof byte[]) {
                    byte[] rawData = (byte[]) extras[0];
                    sendFrameRaw(
                        JCoreWireCodec.CMD_PUSH_CTRL,
                        1,
                        JCoreStorage.nextRid(),
                        this.sessionId,
                        JCoreStorage.getUid(app),
                        rawData
                    );
                }
                return null;
            case 19: // getReportTime
                Bundle timeBundle = new Bundle();
                timeBundle.putLong("result", System.currentTimeMillis() / 1000L);
                return timeBundle;
            case 20: // getUid
                Bundle uidBundle = new Bundle();
                uidBundle.putLong("result", JCoreStorage.getUid(app));
                return uidBundle;
            case 21: // isValidRegistered
                Bundle regBundle = new Bundle();
                regBundle.putBoolean("result", !JCoreStorage.getRegistrationId(app).isEmpty());
                return regBundle;
            case 67: // getConnectionState
                Bundle connBundle = new Bundle();
                connBundle.putBoolean("result", JCoreStorage.isConnected(app));
                return connBundle;
            case 96: // setAuth
                if (extras != null && extras.length > 0 && extras[0] instanceof Boolean) {
                    JCoreStorage.setCollectionAuth(app, (Boolean) extras[0]);
                }
                return null;
            default:
                return null;
        }
    }

    private static Bundle stringResult(String value) {
        Bundle b = new Bundle();
        b.putString("result", value != null ? value : "");
        return b;
    }
}
