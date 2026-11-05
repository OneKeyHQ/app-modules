package com.onekey.jpush.fcm;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Looper;
import android.os.Process;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.api.utils.JCollectionAuth;
import cn.jiguang.core.JCoreCrypto;
import cn.jiguang.core.JCoreStorage;
import cn.jiguang.core.JCoreTcpEngine;
import cn.jiguang.core.JCoreWireCodec;
import cn.jiguang.plugins.push.common.JLogger;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JThirdPlatFormInterface;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.local.ActionHelper;
import cn.jpush.android.service.JNotifyActivity;
import cn.jpush.android.service.JPushMessageReceiver;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import org.robolectric.util.ReflectionHelpers;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest = Config.NONE)
@LooperMode(LooperMode.Mode.PAUSED)
public class FcmAdapterTest {
    private Context context;
    private FcmMessagingService service;
    private NotificationManager notifications;
    private Object firebaseRpc;
    private final List<NotificationMessage> arrived = new ArrayList<>();
    private final List<NotificationMessage> opened = new ArrayList<>();
    private final List<CustomMessage> custom = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        context = RuntimeEnvironment.getApplication();
        ReflectionHelpers.setStaticField(JCoreStorage.class, "sAppContext", null);
        ReflectionHelpers.<Map<String, Object>>getStaticField(JCoreStorage.class, "MEMORY_FALLBACK").clear();
        JCoreStorage.setAppContext(context);
        JCoreStorage.setCollectionAuth(context, true);
        JCoreStorage.setPushStopped(context, false);
        ReflectionHelpers.setStaticField(JThirdPlatFormInterface.class, "fcmPlugin", null);
        ReflectionHelpers.setStaticField(JThirdPlatFormInterface.class, "fcmRegistered", false);
        ReflectionHelpers.<java.util.Queue<String>>getStaticField(FirebaseMessagingService.class, "recentlyReceivedMessageIds").clear();
        JLogger.setLoggerEnable(true);
        JLogger.clearRecentLogs();
        notifications = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifications.cancelAll();
        setProcessImportance(ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND);
        ActionHelper.getInstance().clearRegisteredReceivers();
        ActionHelper.getInstance().registerReceiver(new JPushMessageReceiver() {
            @Override public void onNotifyMessageArrived(Context c, NotificationMessage message) { arrived.add(message); }
            @Override public void onNotifyMessageOpened(Context c, NotificationMessage message) { opened.add(message); }
            @Override public void onMessage(Context c, CustomMessage message) { custom.add(message); }
        });
        service = Robolectric.buildService(FcmMessagingService.class).create().get();
        // Isolate only the SDK's transport acknowledgement; run its real receive/display code.
        firebaseRpc = mock(Class.forName("com.google.android.gms.cloudmessaging.Rpc"));
        ReflectionHelpers.setField(service, "rpc", firebaseRpc);
    }

    @After
    public void tearDown() {
        ActionHelper.getInstance().clearRegisteredReceivers();
        notifications.cancelAll();
    }

    @Test
    public void coreDiscoversSourceAdapterOnceAndRebindsTheCurrentToken() {
        FirebaseMessaging provider = mock(FirebaseMessaging.class);
        when(provider.getToken()).thenReturn(Tasks.forResult("current_secret_token"));
        GoogleApiAvailabilityLight availability = mock(GoogleApiAvailabilityLight.class);
        when(availability.isGooglePlayServicesAvailable(context)).thenReturn(0);
        ActionHelper dispatcher = mock(ActionHelper.class);
        JCoreTcpEngine engine = synchronousEngine();
        try (MockedStatic<FirebaseMessaging> firebase = mockStatic(FirebaseMessaging.class);
             MockedStatic<FirebaseApp> app = mockStatic(FirebaseApp.class);
             MockedStatic<GoogleApiAvailabilityLight> google = mockStatic(GoogleApiAvailabilityLight.class);
             MockedStatic<ActionHelper> helper = mockStatic(ActionHelper.class);
             MockedStatic<JCoreTcpEngine> tcp = mockStatic(JCoreTcpEngine.class)) {
            firebase.when(FirebaseMessaging::getInstance).thenReturn(provider);
            google.when(GoogleApiAvailabilityLight::getInstance).thenReturn(availability);
            helper.when(ActionHelper::getInstance).thenReturn(dispatcher);
            tcp.when(JCoreTcpEngine::getInstance).thenReturn(engine);
            JThirdPlatFormInterface.initializeFcm(context);
            JThirdPlatFormInterface.initializeFcm(context);
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            app.verify(() -> FirebaseApp.initializeApp(context), times(2));
            verify(provider, times(2)).getToken();
            ArgumentCaptor<Bundle> actions = ArgumentCaptor.forClass(Bundle.class);
            verify(dispatcher, times(2)).doAction(eq(context), eq("third_action"), actions.capture());
            for (Bundle action : actions.getAllValues()) {
                assertEquals("current_secret_token", action.getString("token"));
                assertEquals((byte) 8, action.getByte("platform"));
            }
            assertLogsExclude("current_secret_token");
        }
    }

    @Test
    public void missingFirebaseConfigurationDoesNotEscapeCoreInitialization() {
        GoogleApiAvailabilityLight availability = mock(GoogleApiAvailabilityLight.class);
        when(availability.isGooglePlayServicesAvailable(context)).thenReturn(0);
        JCoreTcpEngine engine = synchronousEngine();
        try (MockedStatic<FirebaseMessaging> firebase = mockStatic(FirebaseMessaging.class);
             MockedStatic<FirebaseApp> app = mockStatic(FirebaseApp.class);
             MockedStatic<GoogleApiAvailabilityLight> google = mockStatic(GoogleApiAvailabilityLight.class);
             MockedStatic<JCoreTcpEngine> tcp = mockStatic(JCoreTcpEngine.class)) {
            firebase.when(FirebaseMessaging::getInstance).thenThrow(new IllegalStateException("private_configuration_details"));
            google.when(GoogleApiAvailabilityLight::getInstance).thenReturn(availability);
            tcp.when(JCoreTcpEngine::getInstance).thenReturn(engine);
            JThirdPlatFormInterface.initializeFcm(context);
            assertTrue(JLogger.getRecentLogs().toString().contains("FCM registration unavailable"));
            assertLogsExclude("private_configuration_details");
        }
    }

    @Test
    public void authorizationGrantedAfterSkippedInitRequestsTokenWithoutReconnect() {
        JCoreStorage.setCollectionAuth(context, false);
        JCoreStorage.setConnected(context, true);
        FirebaseMessaging provider = mock(FirebaseMessaging.class);
        when(provider.getToken()).thenReturn(Tasks.forResult("consented_secret_token"));
        GoogleApiAvailabilityLight availability = mock(GoogleApiAvailabilityLight.class);
        when(availability.isGooglePlayServicesAvailable(context)).thenReturn(0);
        ActionHelper dispatcher = mock(ActionHelper.class);
        JCoreTcpEngine engine = synchronousEngine();
        try (MockedStatic<FirebaseMessaging> firebase = mockStatic(FirebaseMessaging.class);
             MockedStatic<FirebaseApp> app = mockStatic(FirebaseApp.class);
             MockedStatic<GoogleApiAvailabilityLight> google = mockStatic(GoogleApiAvailabilityLight.class);
             MockedStatic<ActionHelper> helper = mockStatic(ActionHelper.class);
             MockedStatic<JCoreTcpEngine> tcp = mockStatic(JCoreTcpEngine.class);
             MockedStatic<JCoreManager> core = mockStatic(JCoreManager.class)) {
            firebase.when(FirebaseMessaging::getInstance).thenReturn(provider);
            google.when(GoogleApiAvailabilityLight::getInstance).thenReturn(availability);
            helper.when(ActionHelper::getInstance).thenReturn(dispatcher);
            tcp.when(JCoreTcpEngine::getInstance).thenReturn(engine);
            JPushInterface.init(context);
            verifyNoInteractions(provider);
            JCollectionAuth.setAuth(context, true);
            JCollectionAuth.setAuth(context, true);
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            verify(provider).getToken();
            assertEquals("consented_secret_token", tokenAction(dispatcher).getString("token"));
            assertTrue(JCoreStorage.isConnected(context));
            core.verify(() -> JCoreManager.init(context));
            assertLogsExclude("consented_secret_token");
        }
    }

    @Test
    public void grantingAuthorizationWhileStoppedDoesNotRequestRegistration() {
        JCoreStorage.setCollectionAuth(context, false);
        JCoreStorage.setPushStopped(context, true);
        try (MockedStatic<JCoreTcpEngine> tcp = mockStatic(JCoreTcpEngine.class)) {
            JCollectionAuth.setAuth(context, true);
            assertTrue(JCollectionAuth.isAuth(context));
            tcp.verifyNoInteractions();
        }
    }

    @Test
    public void revokedAuthorizationCancelsQueuedFcmRegistration() {
        JCoreStorage.setCollectionAuth(context, false);
        JCoreTcpEngine engine = mock(JCoreTcpEngine.class);
        try (MockedStatic<JCoreTcpEngine> tcp = mockStatic(JCoreTcpEngine.class);
             MockedStatic<FirebaseMessaging> firebase = mockStatic(FirebaseMessaging.class)) {
            tcp.when(JCoreTcpEngine::getInstance).thenReturn(engine);
            JCollectionAuth.setAuth(context, true);
            ArgumentCaptor<Runnable> work = ArgumentCaptor.forClass(Runnable.class);
            verify(engine).executeAsync(work.capture());
            JCollectionAuth.setAuth(context, false);
            work.getValue().run();
            firebase.verifyNoInteractions();
            assertFalse(JCollectionAuth.isAuth(context));
        }
    }

    @Test
    public void tokenSuccessDispatchesPlatformEightWithoutPersistingBeforeAck() {
        JCoreStorage.saveThirdToken(context, (byte) 8, "previous_acknowledged_token");
        FirebaseMessaging provider = mock(FirebaseMessaging.class);
        TaskCompletionSource<String> request = new TaskCompletionSource<>();
        when(provider.getToken()).thenReturn(request.getTask());
        ActionHelper dispatcher = mock(ActionHelper.class);
        try (MockedStatic<FirebaseMessaging> firebase = mockStatic(FirebaseMessaging.class);
             MockedStatic<ActionHelper> helper = mockStatic(ActionHelper.class)) {
            firebase.when(FirebaseMessaging::getInstance).thenReturn(provider);
            helper.when(ActionHelper::getInstance).thenReturn(dispatcher);
            assertEquals("previous_acknowledged_token", new FcmPushManager().getToken(context));
            verifyNoInteractions(dispatcher);
            request.setResult("rotated_secret_token");
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            Bundle token = tokenAction(dispatcher);
            assertEquals("rotated_secret_token", token.getString("token"));
            assertEquals((byte) 8, token.getByte("platform"));
            assertEquals("previous_acknowledged_token", JCoreStorage.getThirdToken(context, (byte) 8));
            assertLogsExclude("rotated_secret_token");
        }
    }

    @Test
    public void failedTokenRequestDoesNotBindOrLogRawError() {
        FirebaseMessaging provider = mock(FirebaseMessaging.class);
        when(provider.getToken()).thenReturn(Tasks.forException(new IOException("secret_provider_error")));
        ActionHelper dispatcher = mock(ActionHelper.class);
        try (MockedStatic<FirebaseMessaging> firebase = mockStatic(FirebaseMessaging.class);
             MockedStatic<ActionHelper> helper = mockStatic(ActionHelper.class)) {
            firebase.when(FirebaseMessaging::getInstance).thenReturn(provider);
            helper.when(ActionHelper::getInstance).thenReturn(dispatcher);
            new FcmPushManager().getToken(context);
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            verifyNoInteractions(dispatcher);
            assertTrue(JLogger.getRecentLogs().toString().contains("IOException"));
            assertLogsExclude("secret_provider_error");
        }
    }

    @Test
    public void tokenCallbackRechecksStopAndAuthorizationAfterRequest() {
        for (boolean stopped : new boolean[]{true, false}) {
            JCoreStorage.setPushStopped(context, false);
            JCoreStorage.setCollectionAuth(context, true);
            FirebaseMessaging provider = mock(FirebaseMessaging.class);
            TaskCompletionSource<String> request = new TaskCompletionSource<>();
            when(provider.getToken()).thenReturn(request.getTask());
            ActionHelper dispatcher = mock(ActionHelper.class);
            try (MockedStatic<FirebaseMessaging> firebase = mockStatic(FirebaseMessaging.class);
                 MockedStatic<ActionHelper> helper = mockStatic(ActionHelper.class)) {
                firebase.when(FirebaseMessaging::getInstance).thenReturn(provider);
                helper.when(ActionHelper::getInstance).thenReturn(dispatcher);
                new FcmPushManager().getToken(context);
                JCoreStorage.setPushStopped(context, stopped);
                JCoreStorage.setCollectionAuth(context, stopped);
                request.setResult("late_secret_token");
                Shadows.shadowOf(Looper.getMainLooper()).idle();
                verifyNoInteractions(dispatcher);
            }
        }
    }

    @Test
    public void newTokenInitializesCoreAndForwardsRotation() {
        JCoreStorage.setConnected(context, true);
        FirebaseMessaging provider = mock(FirebaseMessaging.class);
        when(provider.getToken()).thenReturn(Tasks.forResult("new_secret_token"));
        GoogleApiAvailabilityLight availability = mock(GoogleApiAvailabilityLight.class);
        when(availability.isGooglePlayServicesAvailable(context)).thenReturn(0);
        JCoreTcpEngine engine = synchronousEngine();
        try (MockedStatic<JCoreManager> core = mockStatic(JCoreManager.class);
             MockedStatic<FirebaseMessaging> firebase = mockStatic(FirebaseMessaging.class);
             MockedStatic<FirebaseApp> app = mockStatic(FirebaseApp.class);
             MockedStatic<GoogleApiAvailabilityLight> google = mockStatic(GoogleApiAvailabilityLight.class);
             MockedStatic<JCoreTcpEngine> tcp = mockStatic(JCoreTcpEngine.class)) {
            firebase.when(FirebaseMessaging::getInstance).thenReturn(provider);
            google.when(GoogleApiAvailabilityLight::getInstance).thenReturn(availability);
            tcp.when(JCoreTcpEngine::getInstance).thenReturn(engine);
            service.handleIntent(new Intent("com.google.firebase.messaging.NEW_TOKEN").putExtra("token", "new_secret_token"));
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            core.verify(() -> JCoreManager.init(context));
            ArgumentCaptor<byte[]> body = ArgumentCaptor.forClass(byte[].class);
            verify(engine).enqueueOrSendRequest(eq(context), eq(JCoreWireCodec.CMD_THIRD_TOKEN),
                eq(JCoreWireCodec.VER_THIRD_TOKEN), anyLong(), body.capture(), anyLong(), eq(3),
                any(JCoreTcpEngine.RequestCallback.class));
            assertArrayEquals(JCoreWireCodec.buildThirdTokenPayload("new_secret_token", (byte) 8, false), body.getValue());
            verifyNoInteractions(provider);
            assertLogsExclude("new_secret_token");
        }
    }

    @Test
    public void stoppedOrUnauthorizedNewTokenDoesNotInitializeCore() {
        try (MockedStatic<JCoreManager> core = mockStatic(JCoreManager.class)) {
            JCoreStorage.setPushStopped(context, true);
            service.onNewToken("ignored");
            JCoreStorage.setPushStopped(context, false);
            JCoreStorage.setCollectionAuth(context, false);
            service.onNewToken("ignored");
            core.verifyNoInteractions();
        }
    }

    @Test
    public void emptyTokensAreIgnored() {
        ActionHelper dispatcher = mock(ActionHelper.class);
        try (MockedStatic<ActionHelper> helper = mockStatic(ActionHelper.class)) {
            helper.when(ActionHelper::getInstance).thenReturn(dispatcher);
            FcmPushManager.acceptToken(context, null);
            FcmPushManager.acceptToken(context, "");
            verifyNoInteractions(dispatcher);
        }
    }

    @Test
    public void googlePlayAvailabilityAndNotificationDelegation() {
        GoogleApiAvailabilityLight availability = mock(GoogleApiAvailabilityLight.class);
        when(availability.isGooglePlayServicesAvailable(context)).thenReturn(0, 9);
        try (MockedStatic<GoogleApiAvailabilityLight> google = mockStatic(GoogleApiAvailabilityLight.class);
             MockedStatic<JPushInterface> core = mockStatic(JPushInterface.class)) {
            google.when(GoogleApiAvailabilityLight::getInstance).thenReturn(availability);
            FcmPushManager manager = new FcmPushManager();
            assertTrue(manager.isSupport(context));
            assertFalse(manager.isSupport(context));
            assertFalse(manager.isSupport(null));
            manager.clearNotification(context, 123);
            manager.clearAllNotification(context);
            core.verify(() -> JPushInterface.clearNotificationById(context, 123));
            core.verify(() -> JPushInterface.clearAllNotifications(context));
        }
    }

    @Test
    public void firebaseInitializationAndApplicationMetadata() {
        FirebaseApp app = mock(FirebaseApp.class);
        FirebaseOptions options = new FirebaseOptions.Builder()
            .setApplicationId("1:123456:android:synthetic").setApiKey("synthetic_api_key").build();
        when(app.getOptions()).thenReturn(options);
        try (MockedStatic<FirebaseApp> firebase = mockStatic(FirebaseApp.class)) {
            firebase.when(FirebaseApp::getInstance).thenReturn(app);
            FcmPushManager manager = new FcmPushManager();
            manager.init(context);
            manager.register(context, new Bundle());
            firebase.verify(() -> FirebaseApp.initializeApp(context), times(2));
            assertEquals(options.getApplicationId(), manager.getAppId(context));
            assertEquals(options.getApiKey(), manager.getAppkey(context));
            assertEquals((byte) 8, manager.getRomType(context));
            assertFalse(manager.isNeedClearToken(context));
        }
    }

    @Test
    public void firebaseNotificationFieldsFillMetadataAndTapOpensOnce() {
        Bundle payload = metadata("{\"_jmsgid_\":\"fcm_notification\",\"n_extras\":{\"route\":\"synthetic_route\"}}");
        payload.putString("gcm.n.e", "1");
        payload.putString("gcm.n.title", "private_title");
        payload.putString("gcm.n.body", "private_body");
        service.onMessageReceived(new RemoteMessage(payload));
        assertEquals(1, arrived.size());
        assertEquals("private_title", arrived.get(0).notificationTitle);
        assertEquals("private_body", arrived.get(0).notificationContent);
        assertEquals(8, arrived.get(0).platform);
        List<Notification> posted = Shadows.shadowOf(notifications).getAllNotifications();
        assertEquals(1, posted.size());
        assertEquals("private_title", posted.get(0).extras.getString(Notification.EXTRA_TITLE));
        Intent tap = Shadows.shadowOf(posted.get(0).contentIntent).getSavedIntent();
        assertEquals(JNotifyActivity.class.getName(), tap.getComponent().getClassName());
        Robolectric.buildActivity(JNotifyActivity.class, tap).create();
        assertEquals(1, opened.size());
        assertEquals("fcm_notification", opened.get(0).msgId);
        assertTrue(opened.get(0).notificationExtras.contains("synthetic_route"));
        assertLogsExclude("private_title", "private_body", "synthetic_route");
    }

    @Test
    public void postedDataOnlyTapIsIgnoredAfterStopOrAuthorizationWithdrawal() {
        setProcessImportance(ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE);
        ActivityInfo host = new ActivityInfo();
        host.packageName = context.getPackageName();
        host.name = context.getPackageName() + ".MainActivity";
        host.applicationInfo = new ApplicationInfo(context.getApplicationInfo());
        ResolveInfo resolvedHost = new ResolveInfo();
        resolvedHost.activityInfo = host;
        Intent launcher = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(context.getPackageName());
        Shadows.shadowOf(context.getPackageManager()).addResolveInfoForIntent(launcher, resolvedHost);
        for (boolean stopped : new boolean[]{true, false}) {
            JCoreStorage.setPushStopped(context, false);
            JCoreStorage.setCollectionAuth(context, true);
            service.handleIntent(firebaseReceiveIntent("deferred_tap_" + stopped, false));
            List<Notification> posted = Shadows.shadowOf(notifications).getAllNotifications();
            assertEquals(1, posted.size());
            Intent tap = Shadows.shadowOf(posted.get(0).contentIntent).getSavedIntent();
            JCoreStorage.setPushStopped(context, stopped);
            JCoreStorage.setCollectionAuth(context, stopped);
            Robolectric.buildActivity(JNotifyActivity.class, tap).create();
            assertTrue(opened.isEmpty());
            assertNull(Shadows.shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity());
            notifications.cancelAll();
        }
    }

    @Test
    public void compressedMetadataRetainsItsOwnTitleAndExtras() {
        Bundle payload = metadata(compress("{\"msg_id\":\"zipped\",\"override_msg_id\":\"\",\"n_title\":\"metadata_title\",\"n_content\":\"metadata_body\",\"n_extras\":{\"route\":\"private_route\"}}"));
        payload.putString("gcm.n.e", "1");
        payload.putString("gcm.n.title", "fallback_title");
        payload.putString("gcm.n.body", "fallback_body");
        service.onMessageReceived(new RemoteMessage(payload));
        assertEquals(1, arrived.size());
        assertEquals("zipped", arrived.get(0).msgId);
        assertEquals("metadata_title", arrived.get(0).notificationTitle);
        assertTrue(arrived.get(0).notificationExtras.contains("private_route"));
    }

    @Test
    public void standardFirebaseDataUsesTransportIdAndPreservesExtras() {
        Bundle payload = new Bundle();
        payload.putString("google.message_id", "transport_id");
        payload.putString("n_title", "Synthetic notification");
        payload.putString("n_content", "Synthetic body");
        payload.putString("route", "synthetic_route");
        service.onMessageReceived(new RemoteMessage(payload));
        assertEquals(1, arrived.size());
        assertEquals("transport_id", arrived.get(0).msgId);
        assertTrue(arrived.get(0).notificationExtras.contains("synthetic_route"));
    }

    @Test
    public void customDataDoesNotDisplayANotification() {
        service.onMessageReceived(new RemoteMessage(metadata("{\"msg_id\":\"custom_id\",\"message\":\"private_custom_body\",\"content_type\":\"text\",\"extras\":{\"route\":\"private_custom_route\"}}")));
        assertEquals(1, custom.size());
        assertEquals("custom_id", custom.get(0).messageId);
        assertEquals("private_custom_body", custom.get(0).message);
        assertEquals("text", custom.get(0).contentType);
        assertTrue(arrived.isEmpty());
        assertEquals(0, Shadows.shadowOf(notifications).getAllNotifications().size());
        assertLogsExclude("private_custom_body", "private_custom_route");
    }

    @Test
    public void malformedAndOversizedPayloadsDoNotReachCallbacks() {
        service.onMessageReceived(new RemoteMessage(metadata("{broken")));
        service.onMessageReceived(new RemoteMessage(metadata("{\"jg_c\":\"not-gzip\"}")));
        StringBuilder oversized = new StringBuilder();
        for (int i = 0; i < 70000; i++) oversized.append('x');
        service.onMessageReceived(new RemoteMessage(metadata(compress(oversized.toString()))));
        assertTrue(arrived.isEmpty());
        assertTrue(custom.isEmpty());
        assertEquals(0, Shadows.shadowOf(notifications).getAllNotifications().size());
    }

    @Test
    public void stoppedAndUnauthorizedNotificationsAreIgnored() {
        String body = "{\"msg_id\":\"blocked\",\"n_content\":\"synthetic\"}";
        JCoreStorage.setPushStopped(context, true);
        service.onMessageReceived(new RemoteMessage(metadata(body)));
        JCoreStorage.setPushStopped(context, false);
        JCoreStorage.setCollectionAuth(context, false);
        service.onMessageReceived(new RemoteMessage(metadata(body)));
        JCoreStorage.setCollectionAuth(context, true);
        assertTrue(arrived.isEmpty());
        assertEquals(0, Shadows.shadowOf(notifications).getAllNotifications().size());
    }

    @Test
    public void foregroundDisplayDisabledSuppressesNotificationAndArrival() {
        service.onMessageReceived(foregroundSuppressedMessage());
        assertTrue(arrived.isEmpty());
        assertEquals(0, Shadows.shadowOf(notifications).getAllNotifications().size());
    }

    @Test
    public void backgroundDataOnlyMessageStillDisplaysAndDispatchesArrival() {
        setProcessImportance(ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE);
        RemoteMessage message = foregroundSuppressedMessage();
        assertNull(message.getNotification());
        service.onMessageReceived(message);
        assertEquals(1, arrived.size());
        assertEquals("foreground_suppressed", arrived.get(0).msgId);
        assertEquals(1, Shadows.shadowOf(notifications).getAllNotifications().size());
    }

    @Test
    public void foregroundDisplayDecisionUsesCurrentProcessStateForEachMessage() {
        service.onMessageReceived(foregroundSuppressedMessage());
        assertTrue(arrived.isEmpty());
        setProcessImportance(ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE);
        service.onMessageReceived(foregroundSuppressedMessage());
        assertEquals(1, arrived.size());
        notifications.cancelAll();
        setProcessImportance(ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND);
        service.onMessageReceived(foregroundSuppressedMessage());
        assertEquals(1, arrived.size());
        assertEquals(0, Shadows.shadowOf(notifications).getAllNotifications().size());
    }

    @Test
    public void lockedScreenDoesNotSuppressDataOnlyNotification() {
        KeyguardManager keyguard = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        Shadows.shadowOf(keyguard).setKeyguardLocked(true);
        service.onMessageReceived(foregroundSuppressedMessage());
        assertEquals(1, arrived.size());
        assertEquals(1, Shadows.shadowOf(notifications).getAllNotifications().size());
    }

    @Test
    public void stoppedBackgroundCombinedIntentCannotUseFirebaseAutomaticDisplay() {
        setProcessImportance(ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE);
        JCoreStorage.setPushStopped(context, true);
        service.handleIntent(firebaseReceiveIntent("stopped_combined", true));
        assertTrue(arrived.isEmpty());
        assertEquals(0, Shadows.shadowOf(notifications).getAllNotifications().size());
        verifyNoInteractions(firebaseRpc);
        assertLogsExclude("private_title", "private_body");
    }

    @Test
    public void unauthorizedBackgroundNotificationIntentCannotUseFirebaseAutomaticDisplay() {
        setProcessImportance(ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE);
        JCoreStorage.setCollectionAuth(context, false);
        Intent notificationOnly = firebaseReceiveIntent("unauthorized_notification", true);
        notificationOnly.removeExtra("JMessageExtra");
        service.handleIntent(notificationOnly);
        assertTrue(arrived.isEmpty());
        assertEquals(0, Shadows.shadowOf(notifications).getAllNotifications().size());
        verifyNoInteractions(firebaseRpc);
        assertLogsExclude("private_title", "private_body");
    }

    @Test
    public void authorizedBackgroundCombinedIntentKeepsFirebaseDisplayAndDeduplication() {
        setProcessImportance(ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE);
        Intent receive = firebaseReceiveIntent("allowed_combined", true);
        service.handleIntent(receive);
        service.handleIntent(new Intent(receive));
        assertEquals(1, Shadows.shadowOf(notifications).getAllNotifications().size());
        assertTrue(arrived.isEmpty());
        assertEquals(2, mockingDetails(firebaseRpc).getInvocations().size());
    }

    @Test
    public void authorizedBackgroundDataOnlyIntentUsesNativeDisplayAndFirebaseDeduplication() {
        setProcessImportance(ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE);
        Intent receive = firebaseReceiveIntent("allowed_data", false);
        service.handleIntent(receive);
        service.handleIntent(new Intent(receive));
        assertEquals(1, arrived.size());
        assertEquals("allowed_data", arrived.get(0).msgId);
        assertEquals(1, Shadows.shadowOf(notifications).getAllNotifications().size());
    }

    @Test
    public void receiveGateRechecksStateAfterStopWithdrawalAndRegrant() {
        setProcessImportance(ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE);
        JCoreStorage.setPushStopped(context, true);
        service.handleIntent(firebaseReceiveIntent("stopped", true));
        JCoreStorage.setPushStopped(context, false);
        JCoreStorage.setCollectionAuth(context, false);
        service.handleIntent(firebaseReceiveIntent("withdrawn", true));
        assertEquals(0, Shadows.shadowOf(notifications).getAllNotifications().size());
        JCoreStorage.setCollectionAuth(context, true);
        service.handleIntent(firebaseReceiveIntent("regranted", true));
        assertEquals(1, Shadows.shadowOf(notifications).getAllNotifications().size());
        assertEquals(1, mockingDetails(firebaseRpc).getInvocations().size());
    }

    @Test
    @Config(sdk = 34)
    public void startupRevokesOldPlayServicesDelegateWithoutTokenOrConsent() {
        JCoreStorage.setPushStopped(context, true);
        JCoreStorage.setCollectionAuth(context, false);
        notifications.setNotificationDelegate("com.google.android.gms");
        try (MockedStatic<FirebaseMessaging> firebase = mockStatic(FirebaseMessaging.class)) {
            Robolectric.buildContentProvider(FcmInitProvider.class).create();
            assertNull(notifications.getNotificationDelegate());
            assertFalse(JCollectionAuth.isAuth(context));
            assertTrue(JCoreStorage.isPushStopped(context));
            firebase.verifyNoInteractions();
        }
    }

    @Test
    @Config(sdk = 34)
    public void startupPreservesOtherNotificationDelegates() {
        notifications.setNotificationDelegate("other.notification.delegate");
        Robolectric.buildContentProvider(FcmInitProvider.class).create();
        assertEquals("other.notification.delegate", notifications.getNotificationDelegate());
    }

    @Test
    public void automaticFirebaseTapLaunchesHostAndKeepsCompressedMetadata() {
        ActivityInfo activity = new ActivityInfo();
        activity.packageName = context.getPackageName();
        activity.name = context.getPackageName() + ".MainActivity";
        activity.applicationInfo = new ApplicationInfo(context.getApplicationInfo());
        ResolveInfo resolve = new ResolveInfo();
        resolve.activityInfo = activity;
        Intent launcher = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(context.getPackageName());
        Shadows.shadowOf(context.getPackageManager()).addResolveInfoForIntent(launcher, resolve);
        Intent tap = new Intent(JPushInterface.ACTION_NOTIFICATION_OPENED)
            .putExtra("JMessageExtra", compress("{\"msg_id\":\"cold_tap\",\"n_extras\":{\"route\":\"synthetic_route\"}}"));
        Robolectric.buildActivity(JNotifyActivity.class, tap).create();
        assertEquals(1, opened.size());
        assertEquals("cold_tap", opened.get(0).msgId);
        Intent launched = Shadows.shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
        assertNotNull(launched);
        assertEquals(activity.name, launched.getComponent().getClassName());
        assertTrue(opened.get(0).notificationExtras.contains("synthetic_route"));
    }

    private static Bundle metadata(String json) {
        Bundle payload = new Bundle();
        payload.putString("JMessageExtra", json);
        return payload;
    }

    private static RemoteMessage foregroundSuppressedMessage() {
        return new RemoteMessage(metadata("{\"msg_id\":\"foreground_suppressed\",\"n_content\":\"synthetic\",\"n_foreground\":\"0\"}"));
    }

    private static Intent firebaseReceiveIntent(String id, boolean notification) {
        Intent receive = new Intent("com.google.android.c2dm.intent.RECEIVE")
            .putExtra("google.message_id", id)
            .putExtras(metadata("{\"msg_id\":\"" + id + "\",\"n_content\":\"private_body\",\"n_foreground\":\"0\"}"));
        if (notification) {
            receive.putExtra("gcm.n.e", "1");
            receive.putExtra("gcm.n.title", "private_title");
            receive.putExtra("gcm.n.body", "private_body");
            receive.putExtra("gcm.n.icon", "android");
        }
        return receive;
    }

    private void setProcessImportance(int importance) {
        ActivityManager.RunningAppProcessInfo process = new ActivityManager.RunningAppProcessInfo();
        process.pid = Process.myPid();
        process.importance = importance;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Shadows.shadowOf(manager).setProcesses(Collections.singletonList(process));
    }

    private static String compress(String json) {
        return "{\"jg_c\":\"" + JCoreCrypto.base64Encode(JCoreCrypto.gzipCompress(json.getBytes(StandardCharsets.UTF_8))) + "\"}";
    }

    private Bundle tokenAction(ActionHelper dispatcher) {
        ArgumentCaptor<Bundle> action = ArgumentCaptor.forClass(Bundle.class);
        verify(dispatcher).doAction(eq(context), eq("third_action"), action.capture());
        assertEquals(JThirdPlatFormInterface.ACTION_REGISTER_TOKEN, action.getValue().getString("third_key_action"));
        return action.getValue();
    }

    private static JCoreTcpEngine synchronousEngine() {
        JCoreTcpEngine engine = mock(JCoreTcpEngine.class);
        doAnswer(call -> {
            call.<Runnable>getArgument(0).run();
            return null;
        }).when(engine).executeAsync(any(Runnable.class));
        return engine;
    }

    private static void assertLogsExclude(String... values) {
        String logs = JLogger.getRecentLogs().toString();
        for (String value : values) assertFalse("Sensitive payload appeared in native logger", logs.contains(value));
    }
}
