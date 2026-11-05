package io.intercom.android.sdk.fcm;

import android.app.Application;
import android.text.TextUtils;
import androidx.annotation.VisibleForTesting;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.push.IntercomPushClient;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: intercom-sdk-fcm-18.10.0.aar:classes.jar:io/intercom/android/sdk/fcm/IntercomFcmMessengerService.class */
public class IntercomFcmMessengerService extends FirebaseMessagingService {
    private static final IntercomPushClient pushClient = new IntercomPushClient();
    private static final Twig twig = LumberMill.getLogger();
    private static final Executor TOKEN_EXECUTOR = new ThreadPoolExecutor(0, 1, 30, TimeUnit.SECONDS, new LinkedBlockingQueue(), new ThreadFactory() { // from class: io.intercom.android.sdk.fcm.IntercomFcmMessengerService.1
        @Override // java.util.concurrent.ThreadFactory
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, "intercom-fcm-token");
            thread.setDaemon(true);
            return thread;
        }
    });

    /* JADX INFO: loaded from: intercom-sdk-fcm-18.10.0.aar:classes.jar:io/intercom/android/sdk/fcm/IntercomFcmMessengerService$TokenConsumer.class */
    interface TokenConsumer {
        void accept(String str);
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        twig.d("Intercom push received: " + remoteMessage.getData(), new Object[0]);
        pushClient.handlePush(getApplication(), remoteMessage.getData());
    }

    public void onNewToken(String refreshedToken) {
        if (TextUtils.isEmpty(refreshedToken)) {
            twig.e("Intercom push registration failed. Please make sure you have added a google-services.json file", new Object[0]);
        } else {
            pushClient.sendTokenToIntercom(getApplication(), refreshedToken);
        }
    }

    public static void initialize(Application application) {
        registerTokenListener(FirebaseMessaging.getInstance().getToken(), TOKEN_EXECUTOR, token -> {
            pushClient.sendTokenToIntercom(application, token);
        });
    }

    @VisibleForTesting
    static void registerTokenListener(Task<String> tokenTask, Executor executor, TokenConsumer consumer) {
        tokenTask.addOnCompleteListener(executor, task -> {
            if (!task.isSuccessful()) {
                twig.w("Fetching FCM registration token failed", new Object[]{task.getException()});
                return;
            }
            String token = (String) task.getResult();
            twig.internal("FCM registration token fetched: " + token);
            consumer.accept(token);
        });
    }
}
