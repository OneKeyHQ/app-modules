package com.facebook.react.bridge;
import android.app.Activity;
import android.content.ContextWrapper;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ReactApplicationContext extends ContextWrapper {
    public static final class EmittedEvent {
        public final String name;
        public final Object data;
        public EmittedEvent(String name, Object data) {
            this.name = name;
            this.data = data;
        }
    }

    public final List<EmittedEvent> emittedEvents = new CopyOnWriteArrayList<EmittedEvent>();
    private Activity currentActivity;

    public ReactApplicationContext() {
        super(null);
    }

    public void setCurrentActivity(Activity activity) {
        this.currentActivity = activity;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    @SuppressWarnings("unchecked")
    public <T extends JavaScriptModule> T getJSModule(Class<T> jsInterface) {
        if (jsInterface == DeviceEventManagerModule.RCTDeviceEventEmitter.class) {
            return (T) new DeviceEventManagerModule.RCTDeviceEventEmitter() {
                @Override
                public void emit(String eventName, Object data) {
                    emittedEvents.add(new EmittedEvent(eventName, data));
                }
            };
        }
        return null;
    }
}
