#include <string.h>
#include "keys.h"

#ifdef __ANDROID__

#include <alloca.h>
#include <jni.h>
#include "../android/src/main/cpp/validation.h"

#endif

#if __has_include ("./keys.secret")
#   include "./keys.secret"
#else
#   define HAS_KEYS 0
#endif


char *getInitParams() {
	#if (HAS_KEYS == 1)
	    return getDecryptedKey((char *)liteInitGPCParams, sizeof(liteInitGPCParams));
	#else
	    return "{\"scpID\":\"1107\",\"keyUsage\":\"3C\",\"keyType\":\"88\",\"keyLength\":16,\"hostID\":\"80\",\"crt\":\"20\",\"sk\":\"B6\",\"cardGroupID\":\"01020304\"}";
	#endif
}

#ifdef __ANDROID__

JNIEXPORT jstring JNICALL
Java_so_onekey_app_wallet_lite_keys_KeysNativeProvider_getLiteSecureChannelInitParams(JNIEnv *env,
                                                                                 jobject this,
                                                                                 jobject context) {
    char *result = getInitParams();
    jstring value = (*env)->NewStringUTF(env, result);
#if (HAS_KEYS == 1)
    if (checkSecurityPermission(env, context, (char **) authorizedAppSha1, 3)) {
        free(result);
        return value;
    }
    LOGD("create process failure");
#endif
    free(result);
    return value;
}

#endif




