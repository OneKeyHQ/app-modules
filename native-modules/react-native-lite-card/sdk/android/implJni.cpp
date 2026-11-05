// Recovered JNI contract from ziancube/GPChannel-test.
#include <jni.h>
#include <atomic>
#include <string>
#include "GPChannelSDK.h"
#include "GPChannelSDKCore.h"
#include "json/json.h"

namespace {
std::atomic<int> errorCode{0};
void logBuild(JNIEnv* env, bool safe, JUB_RV code, bool outputPresent) {
    // Preserve caller exceptions; only diagnostics-owned lookup/call failures may be cleared.
    if (env->ExceptionCheck()) return;
    try {
        jclass logger = env->FindClass("com/margelo/nitro/nativelogger/OneKeyLog");
        if (logger) {
            jmethodID info = env->GetStaticMethodID(logger, "info", "(Ljava/lang/String;Ljava/lang/String;)V");
            if (info) {
                const std::string message = std::string("stage=command_build nativeSourceId=") +
                    reinterpret_cast<const char*>(GPChannelSDKCoreVersionString) +
                    " safe=" + (safe ? "true" : "false") +
                    " sdkReturnCode=" + std::to_string(code) +
                    " outputPresent=" + (outputPresent ? "true" : "false");
                jstring tag = env->NewStringUTF("LiteCard");
                jstring text = tag && !env->ExceptionCheck() ? env->NewStringUTF(message.c_str()) : nullptr;
                if (tag && text && !env->ExceptionCheck()) env->CallStaticVoidMethod(logger, info, tag, text);
                if (tag) env->DeleteLocalRef(tag);
                if (text) env->DeleteLocalRef(text);
            }
            env->DeleteLocalRef(logger);
        }
    } catch (...) {
        // Diagnostics must not replace the actual command result on allocation failure.
    }
    if (env->ExceptionCheck()) env->ExceptionClear();
}
class UtfChars {
public:
    UtfChars(JNIEnv* env, jstring value) : env(env), value(value),
        chars(value ? env->GetStringUTFChars(value, nullptr) : nullptr) {}
    ~UtfChars() { if (chars) env->ReleaseStringUTFChars(value, chars); }
    const char* get() const { return chars; }
private:
    JNIEnv* env;
    jstring value;
    const char* chars;
};
class Output {
public:
    char* value = nullptr;
    ~Output() { if (value) JUB_FreeMemory(value); }
};
jstring json(JNIEnv* env, const Json::Value& value) {
    return env->NewStringUTF(Json::FastWriter().write(value).c_str());
}
jstring result(JNIEnv* env, JUB_RV code, const Output& output) {
    if (code != JUBR_OK) { errorCode.store(static_cast<int>(code)); return nullptr; }
    return env->NewStringUTF(output.value);
}
jint getError(JNIEnv*, jobject) { return errorCode.load(); }
jint initialize(JNIEnv* env, jobject, jstring input) {
    try {
        UtfChars text(env, input);
        if (!text.get()) return JUBR_ARGUMENTS_BAD;
        Json::Value root;
        Json::Reader reader;
        if (!reader.parse(text.get(), root) || !root.isObject()) return JUBR_ARGUMENTS_BAD;
        const char* fields[] = {"scpID", "keyUsage", "keyType", "hostID", "cardGroupID", "crt", "sk"};
        for (const char* name : fields) if (!root[name].isString()) return JUBR_ARGUMENTS_BAD;
        if (!root["keyLength"].isUInt() || root["keyLength"].asUInt() != 16) return JUBR_ARGUMENTS_BAD;
        GPC_SCP11_SHAREDINFO info{
            const_cast<char*>(root["scpID"].asCString()),
            const_cast<char*>(root["keyUsage"].asCString()),
            const_cast<char*>(root["keyType"].asCString()),
            const_cast<char*>("10"),
            const_cast<char*>(root["hostID"].asCString()),
            const_cast<char*>(root["cardGroupID"].asCString())};
        return static_cast<jint>(JUB_GPC_Initialize(info, root["crt"].asCString(), root["sk"].asCString()));
    } catch (...) {
        errorCode.store(static_cast<int>(JUBR_ARGUMENTS_BAD));
        return JUBR_ARGUMENTS_BAD;
    }
}
jint finalize(JNIEnv*, jobject) {
    try {
        return static_cast<jint>(JUB_GPC_Finalize());
    } catch (...) {
        return JUBR_ERROR;
    }
}
jint open(JNIEnv* env, jobject, jstring input) {
    try {
        UtfChars text(env, input);
        if (!text.get()) return JUBR_ARGUMENTS_BAD;
        return static_cast<jint>(JUB_GPC_OpenSecureChannel(text.get()));
    } catch (...) {
        errorCode.store(static_cast<int>(JUBR_ARGUMENTS_BAD));
        return JUBR_ARGUMENTS_BAD;
    }
}
jstring mutual(JNIEnv* env, jobject) {
    try {
        Output output;
        auto code = JUB_GPC_BuildMutualAuthData(&output.value);
        return result(env, code, output);
    } catch (...) {
        errorCode.store(static_cast<int>(JUBR_ERROR));
        return nullptr;
    }
}
jstring build(JNIEnv* env, bool safe, jlong cla, jlong ins, jlong p1, jlong p2, jstring input) {
    try {
        UtfChars text(env, input);
        Output output;
        auto code = safe ? JUB_GPC_BuildSafeAPDU(cla, ins, p1, p2, text.get(), &output.value)
                         : JUB_GPC_BuildAPDU(cla, ins, p1, p2, text.get(), &output.value);
        logBuild(env, safe, code, output.value != nullptr);
        return result(env, code, output);
    } catch (...) {
        errorCode.store(static_cast<int>(JUBR_ERROR));
        return nullptr;
    }
}
jstring buildPlain(JNIEnv* env, jobject, jlong cla, jlong ins, jlong p1, jlong p2, jstring input) {
    return build(env, false, cla, ins, p1, p2, input);
}
jstring buildSafe(JNIEnv* env, jobject, jlong cla, jlong ins, jlong p1, jlong p2, jstring input) {
    return build(env, true, cla, ins, p1, p2, input);
}
jstring parse(JNIEnv* env, bool safe, jstring input) {
    try {
        UtfChars text(env, input);
        Output output;
        JUB_UINT16 word = 0;
        auto code = safe ? JUB_GPC_ParseSafeAPDUResponse(text.get(), &word, &output.value)
                         : JUB_GPC_ParseAPDUResponse(text.get(), &word, &output.value);
        if (code != JUBR_OK) { errorCode.store(static_cast<int>(code)); return nullptr; }
        Json::Value root;
        root["wRet"] = word;
        root["response"] = output.value;
        return json(env, root);
    } catch (...) {
        errorCode.store(static_cast<int>(JUBR_ERROR));
        return nullptr;
    }
}
jstring parsePlain(JNIEnv* env, jobject, jstring input) { return parse(env, false, input); }
jstring parseSafe(JNIEnv* env, jobject, jstring input) { return parse(env, true, input); }
jstring decodeTLV(JNIEnv* env, jobject, jstring input) {
    try {
        UtfChars text(env, input);
        Output output;
        JUB_ULONG tag = 0;
        auto code = JUB_GPC_TLVDecode(text.get(), &tag, &output.value);
        if (code != JUBR_OK) { errorCode.store(static_cast<int>(code)); return nullptr; }
        Json::Value root;
        root["tag"] = static_cast<Json::Int64>(tag);
        root["value"] = output.value;
        return json(env, root);
    } catch (...) {
        errorCode.store(static_cast<int>(JUBR_ERROR));
        return nullptr;
    }
}
jstring parseCertificate(JNIEnv* env, jobject, jstring input) {
    try {
        UtfChars text(env, input);
        Output serial, subject;
        auto code = JUB_GPC_ParseCertificate(text.get(), &serial.value, &subject.value);
        if (code != JUBR_OK) { errorCode.store(static_cast<int>(code)); return nullptr; }
        Json::Value root;
        root["sn"] = serial.value;
        root["subjectID"] = subject.value;
        return json(env, root);
    } catch (...) {
        errorCode.store(static_cast<int>(JUBR_ERROR));
        return nullptr;
    }
}
#define METHOD(name, signature, function) \
    {const_cast<char*>(name), const_cast<char*>(signature), reinterpret_cast<void*>(function)}
JNINativeMethod methods[] = {
    METHOD("nativeGetErrorCode", "()I", getError),
    METHOD("nativeGPCInitialize", "(Ljava/lang/String;)I", initialize),
    METHOD("nativeGPCFinalize", "()I", finalize),
    METHOD("nativeGPCBuildMutualAuthData", "()Ljava/lang/String;", mutual),
    METHOD("nativeGPCOpenSecureChannel", "(Ljava/lang/String;)I", open),
    METHOD("nativeGPCBuildAPDU", "(JJJJLjava/lang/String;)Ljava/lang/String;", buildPlain),
    METHOD("nativeGPCBuildSafeAPDU", "(JJJJLjava/lang/String;)Ljava/lang/String;", buildSafe),
    METHOD("nativeGPCParseSafeAPDUResponse", "(Ljava/lang/String;)Ljava/lang/String;", parseSafe),
    METHOD("nativeGPCParseAPDUResponse", "(Ljava/lang/String;)Ljava/lang/String;", parsePlain),
    METHOD("nativeGPCTLVDecode", "(Ljava/lang/String;)Ljava/lang/String;", decodeTLV),
    METHOD("nativeGPCParseCertificate", "(Ljava/lang/String;)Ljava/lang/String;", parseCertificate)
};
#undef METHOD
}
extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
    JNIEnv* env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) return JNI_ERR;
    jclass type = env->FindClass("org/haobtc/onekey/card/gpchannel/GPChannelNatives");
    if (!type) return JNI_ERR;
    auto code = env->RegisterNatives(type, methods, sizeof(methods) / sizeof(methods[0]));
    env->DeleteLocalRef(type);
    return code < 0 ? JNI_ERR : JNI_VERSION_1_6;
}
