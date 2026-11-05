package com.onekey.jpush.fcm;

import android.content.Intent;
import android.os.Bundle;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.core.JCoreStorage;
import cn.jiguang.plugins.push.common.JLogger;
import cn.jpush.android.api.JThirdPlatFormInterface;
import cn.jpush.android.local.ActionHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import org.json.JSONObject;

public final class FcmMessagingService extends FirebaseMessagingService {
    @Override
    public void handleIntent(Intent intent) {
        // Firebase may display background notifications before onMessageReceived is called.
        if (JCoreStorage.isPushStopped(this) || !JCoreStorage.isCollectionAuth(this)) {
            JLogger.i("FCM receive blocked by push or authorization state");
            return;
        }
        super.handleIntent(intent);
    }

    @Override
    public void onNewToken(String token) {
        if (JCoreStorage.isPushStopped(this) || !JCoreStorage.isCollectionAuth(this)) {
            return;
        }
        // Initialize the transport without fetching and submitting this token a second time.
        JCoreManager.init(getApplicationContext());
        FcmPushManager.acceptToken(getApplicationContext(), token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (JCoreStorage.isPushStopped(this) || !JCoreStorage.isCollectionAuth(this)) {
            return;
        }
        try {
            Map<String, String> data = remoteMessage.getData();
            String raw = data.get("JMessageExtra");
            JSONObject message;
            if (raw == null || raw.isEmpty()) {
                message = new JSONObject(data);
            } else {
                String decoded = JThirdPlatFormInterface.decodeMessageJson(raw);
                if (decoded == null) {
                    JLogger.w("FCM payload rejected");
                    return;
                }
                message = new JSONObject(decoded);
            }
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            if (notification != null) {
                if (!message.has("n_title") && !message.has("title")) {
                    message.put("n_title", notification.getTitle());
                }
                if (!message.has("n_content") && !message.has("alert") && !message.has("content")) {
                    message.put("n_content", notification.getBody());
                }
            }
            if (!message.has("msg_id") && !message.has("_jmsgid_") && !message.has("_j_msgid")) {
                message.put("msg_id", remoteMessage.getMessageId());
            }
            if (!message.has("n_extras") && !message.has("extras") && !data.containsKey("JMessageExtra")) {
                message.put("n_extras", new JSONObject(data));
            }
            raw = message.toString();
            Bundle payload = new Bundle();
            payload.putString("JMessageExtra", raw);
            payload.putByte("platform", (byte) 8);
            ActionHelper.getInstance().doAction(
                getApplicationContext(),
                JThirdPlatFormInterface.ACTION_PLUGIN_PALTFORM_ON_MESSAGING,
                payload
            );
            JLogger.i("FCM message forwarded to native dispatcher");
        } catch (Exception invalidMessage) {
            JLogger.w("FCM payload rejected");
        }
    }
}
