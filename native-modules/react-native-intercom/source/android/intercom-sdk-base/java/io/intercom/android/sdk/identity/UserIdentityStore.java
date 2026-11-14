package io.intercom.android.sdk.identity;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.core.Preferences.Key;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.utilities.CryptoHelper;
import io.intercom.android.sdk.utilities.MultiProcessDataStoreDelegateKt;
import io.intercom.android.sdk.utilities.PreloadedValue;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.properties.ReadOnlyProperty;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0001\u0018\u0000 \u001a2\u00020\u0001:\u0002\u0019\u001aB'\b\u0002\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\u0006\u0010\u0010\u001a\u00020\u000fJ\u000e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\r\u001a\u00020\u000fJ\u0006\u0010\u0013\u001a\u00020\u0012J\u001e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00042\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00150\u0018H\u0002R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001b",
   d2 = {
         "Lio/intercom/android/sdk/identity/UserIdentityStore;",
         "",
         "dataStore",
         "Landroidx/datastore/core/DataStore;",
         "Landroidx/datastore/preferences/core/Preferences;",
         "scope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "ioDispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "<init>",
         "(Landroidx/datastore/core/DataStore;Lkotlinx/coroutines/CoroutineScope;Lkotlinx/coroutines/CoroutineDispatcher;)V",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "fields",
         "Lio/intercom/android/sdk/utilities/PreloadedValue;",
         "Lio/intercom/android/sdk/identity/UserIdentityStore$UserFields;",
         "loadUserFields",
         "save",
         "",
         "clear",
         "decryptField",
         "",
         "prefs",
         "key",
         "Landroidx/datastore/preferences/core/Preferences$Key;",
         "UserFields",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class UserIdentityStore {
   @NotNull
   public static final UserIdentityStore$Companion Companion = new UserIdentityStore$Companion(null);
   @NotNull
   private final DataStore dataStore;
   @NotNull
   private final CoroutineScope scope;
   @NotNull
   private final Twig twig;
   @NotNull
   private final PreloadedValue fields;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final String OLD_PREFS_NAME = "INTERCOM_SDK_USER_PREFS";
   @NotNull
   private static final String OLD_KEY_PREFIX = "intercomsdk-session-";
   @NotNull
   private static final Key KEY_ANONYMOUS_ID = PreferencesKeys.stringKey("anonymous_id");
   @NotNull
   private static final Key KEY_INTERCOM_ID = PreferencesKeys.stringKey("intercom_id");
   @NotNull
   private static final Key KEY_USER_ID = PreferencesKeys.stringKey("user_id");
   @NotNull
   private static final Key KEY_EMAIL = PreferencesKeys.stringKey("email");
   @NotNull
   private static final Key KEY_HMAC = PreferencesKeys.stringKey("hmac");
   @NotNull
   private static final Key KEY_JWT = PreferencesKeys.stringKey("jwt");
   @NotNull
   private static final Key KEY_MESSENGER_ENVIRONMENT = PreferencesKeys.stringKey("messenger_environment");
   @NotNull
   private static final Map MIGRATION_KEY_MAP;
   @NotNull
   private static final ReadOnlyProperty userDataStore$delegate = MultiProcessDataStoreDelegateKt.multiProcessPreferencesDataStore$default(
      "intercom_user_prefs_datastore", null, ((kotlin.jvm.functions.Function1) (recoveredArgument_131_0) -> UserIdentityStore.userDataStore_delegate$lambda$1((android.content.Context)recoveredArgument_131_0)), 2, null
   );

   private UserIdentityStore(DataStore var1, CoroutineScope var2, CoroutineDispatcher var3) {
      this.dataStore = var1;
      this.scope = var2;
      Twig var10001 = LumberMill.getLogger();
      Intrinsics.checkNotNullExpressionValue(var10001, "getLogger(...)");
      this.twig = var10001;
      this.fields = new PreloadedValue(this.scope, var3, ((kotlin.jvm.functions.Function0) () -> UserIdentityStore.fields$lambda$0()), new UserIdentityStore$fields$2(this, null));
   }

   @NotNull
   public final UserIdentityStore$UserFields loadUserFields() {
      return (UserIdentityStore$UserFields)this.fields.get$intercom_sdk_base_release();
   }

   public final void save(@NotNull UserIdentityStore$UserFields var1) {
      Intrinsics.checkNotNullParameter(var1, "fields");
      this.fields.set$intercom_sdk_base_release(var1);
      BuildersKt.launch$default(this.scope, null, null, new UserIdentityStore$save$1(this, var1, null), 3, null);
   }

   public final void clear() {
      this.fields.set$intercom_sdk_base_release(new UserIdentityStore$UserFields(null, null, null, null, null, null, null, 127, null));
      BuildersKt.launch$default(this.scope, null, null, new UserIdentityStore$clear$1(this, null), 3, null);
   }

   private final String decryptField(Preferences var1, Key var2) {
      String var10000 = (String)var1.get(var2);
      if (var10000 == null) {
         return "";
      }

      String var3 = var10000;
      return CryptoHelper.INSTANCE.decrypt$intercom_sdk_base_release(var3);
   }

   private static final UserIdentityStore$UserFields fields$lambda$0() {
      return new UserIdentityStore$UserFields(null, null, null, null, null, null, null, 127, null);
   }

   private static final List userDataStore_delegate$lambda$1(Context var0) {
      Intrinsics.checkNotNullParameter(var0, "context");
      return CollectionsKt.listOf(Companion.createSharedPrefsMigration$intercom_sdk_base_release(var0));
   }

   @JvmStatic
   @NotNull
   public static final UserIdentityStore create(@NotNull Context var0, @NotNull CoroutineScope var1) {
      return Companion.create(var0, var1);
   }

   @JvmStatic
   @VisibleForTesting
   @NotNull
   public static final UserIdentityStore createForTesting$intercom_sdk_base_release(
      @NotNull DataStore var0, @NotNull CoroutineScope var1, @NotNull CoroutineDispatcher var2
   ) {
      return Companion.createForTesting$intercom_sdk_base_release(var0, var1, var2);
   }

   // $VF: synthetic method
   public static final DataStore access$getDataStore$p(UserIdentityStore var0) {
      return var0.dataStore;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_ANONYMOUS_ID$cp() {
      return KEY_ANONYMOUS_ID;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_INTERCOM_ID$cp() {
      return KEY_INTERCOM_ID;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_USER_ID$cp() {
      return KEY_USER_ID;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_EMAIL$cp() {
      return KEY_EMAIL;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_HMAC$cp() {
      return KEY_HMAC;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_JWT$cp() {
      return KEY_JWT;
   }

   // $VF: synthetic method
   public static final Key access$getKEY_MESSENGER_ENVIRONMENT$cp() {
      return KEY_MESSENGER_ENVIRONMENT;
   }

   // $VF: synthetic method
   public UserIdentityStore(DataStore var1, CoroutineScope var2, CoroutineDispatcher var3, DefaultConstructorMarker var4) {
      this(var1, var2, var3);
   }

   // $VF: synthetic method
   public static final ReadOnlyProperty access$getUserDataStore$delegate$cp() {
      return userDataStore$delegate;
   }

   // $VF: synthetic method
   public static final Map access$getMIGRATION_KEY_MAP$cp() {
      return MIGRATION_KEY_MAP;
   }

   // $VF: synthetic method
   public static final String access$decryptField(UserIdentityStore var0, Preferences var1, Key var2) {
      return var0.decryptField(var1, var2);
   }

   // $VF: synthetic method
   public static final Twig access$getTwig$p(UserIdentityStore var0) {
      return var0.twig;
   }

   static {
      Pair[] var0 = new Pair[]{
         TuplesKt.to("INTERCOM_SDK_ANONYMOUS_ID", KEY_ANONYMOUS_ID),
         TuplesKt.to("INTERCOM_SDK_INTERCOM_ID", KEY_INTERCOM_ID),
         TuplesKt.to("INTERCOM_SDK_USER_ID", KEY_USER_ID),
         TuplesKt.to("INTERCOM_SDK_EMAIL_ID", KEY_EMAIL),
         TuplesKt.to("SecureMode_HMAC", KEY_HMAC),
         TuplesKt.to("INTERCOM_SDK_JWT", KEY_JWT)
      };
      MIGRATION_KEY_MAP = MapsKt.mapOf(var0);
   }
}
