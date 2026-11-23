package com.onekey.jpush.fcm;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.Intent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class FcmManifestTest {
    @Test
    public void mergedManifestRoutesOnlyToGuardedServiceAndDisablesDelegation() throws Exception {
        Context context = RuntimeEnvironment.getApplication();
        PackageManager packages = context.getPackageManager();
        PackageInfo info = packages.getPackageInfo(context.getPackageName(),
            PackageManager.GET_META_DATA | PackageManager.GET_PROVIDERS | PackageManager.GET_SERVICES);
        assertTrue(info.applicationInfo.metaData.containsKey("firebase_messaging_notification_delegation_enabled"));
        assertFalse(info.applicationInfo.metaData.getBoolean("firebase_messaging_notification_delegation_enabled"));
        assertTrue(info.applicationInfo.metaData.containsKey("firebase_messaging_auto_init_enabled"));
        assertFalse(info.applicationInfo.metaData.getBoolean("firebase_messaging_auto_init_enabled"));
        boolean initializerFound = false;
        for (ProviderInfo provider : info.providers) {
            if (FcmInitProvider.class.getName().equals(provider.name)) {
                initializerFound = true;
                assertFalse(provider.exported);
            }
        }
        assertTrue("The startup delegate cleanup must be installed", initializerFound);
        boolean guardedServiceFound = false;
        for (ServiceInfo service : info.services) {
            assertNotEquals("Unguarded Firebase fallback must not be installed",
                "com.google.firebase.messaging.FirebaseMessagingService", service.name);
            if (FcmMessagingService.class.getName().equals(service.name)) {
                guardedServiceFound = true;
                assertFalse(service.exported);
                assertFalse(service.directBootAware);
            }
        }
        assertTrue("The guarded receive service must be installed", guardedServiceFound);
        android.content.pm.ResolveInfo handler = packages.resolveService(
            new Intent("com.google.firebase.MESSAGING_EVENT").setPackage(context.getPackageName()), 0);
        assertNotNull(handler);
        assertEquals(FcmMessagingService.class.getName(), handler.serviceInfo.name);
    }
}
