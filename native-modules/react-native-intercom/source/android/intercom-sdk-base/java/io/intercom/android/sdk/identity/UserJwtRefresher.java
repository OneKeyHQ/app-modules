package io.intercom.android.sdk.identity;

import android.os.Handler;
import android.os.Looper;
import androidx.compose.runtime.internal.StabilityInferred;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.IntercomUserJwtProvider;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.utilities.commons.TimeProvider;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.Ref.ObjectRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0013\b\u0001\u0018\u0000 82\u00020\u0001:\u00045678B´\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012K\u0010\u0007\u001aG\u0012\u0013\u0012\u00110\t¢\u0006\f\b\n\u0012\b\b\u000b\u0012\u0004\b\b(\f\u0012\u0013\u0012\u00110\t¢\u0006\f\b\n\u0012\b\b\u000b\u0012\u0004\b\b(\r\u0012\u0013\u0012\u00110\t¢\u0006\f\b\n\u0012\b\b\u000b\u0012\u0004\b\b(\u000e\u0012\u0004\u0012\u00020\u000f0\b\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0011\u0012\u000e\b\u0002\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u0005\u0012\u000e\b\u0002\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0014\b\u0002\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0017\u0012\u0004\u0012\u00020\u000f0\u0016\u0012\b\b\u0002\u0010\u0018\u001a\u00020\u0019¢\u0006\u0004\b\u001a\u0010\u001bJ\u000e\u0010)\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\tJ\u0006\u0010*\u001a\u00020\tJ&\u0010+\u001a\u0004\u0018\u00010\t2\u0006\u0010,\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\t2\f\u0010-\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005J\u000e\u0010.\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\tJ\u001a\u0010/\u001a\u0004\u0018\u00010$2\u0006\u0010,\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\tH\u0002J\"\u00100\u001a\u00020\u000f2\u0006\u0010+\u001a\u00020$2\b\u0010\f\u001a\u0004\u0018\u00010\t2\u0006\u00101\u001a\u00020\u0006H\u0002J\u0010\u00102\u001a\u00020\u000f2\u0006\u0010+\u001a\u00020$H\u0002J\u0010\u00103\u001a\u00020\u000f2\u0006\u0010+\u001a\u00020$H\u0002J\u001c\u00104\u001a\u0004\u0018\u00010\t*\u00020$2\f\u0010-\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000RS\u0010\u0007\u001aG\u0012\u0013\u0012\u00110\t¢\u0006\f\b\n\u0012\b\b\u000b\u0012\u0004\b\b(\f\u0012\u0013\u0012\u00110\t¢\u0006\f\b\n\u0012\b\b\u000b\u0012\u0004\b\b(\r\u0012\u0013\u0012\u00110\t¢\u0006\f\b\n\u0012\b\b\u000b\u0012\u0004\b\b(\u000e\u0012\u0004\u0012\u00020\u000f0\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0017\u0012\u0004\u0012\u00020\u000f0\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\u001c\u001a\u0004\u0018\u00010\u001dX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001f\"\u0004\b \u0010!R\u000e\u0010\"\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010#\u001a\u0004\u0018\u00010$X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010%\u001a\u0004\u0018\u00010&X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010'\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000¨\u00069",
   d2 = {
         "Lio/intercom/android/sdk/identity/UserJwtRefresher;",
         "",
         "userIdentity",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "isAppInForeground",
         "Lkotlin/Function0;",
         "",
         "applyJwt",
         "Lkotlin/Function3;",
         "",
         "Lkotlin/ParameterName;",
         "name",
         "jwt",
         "fingerprint",
         "replacing",
         "",
         "timeProvider",
         "Lio/intercom/android/sdk/utilities/commons/TimeProvider;",
         "elapsedRealtime",
         "",
         "isOnMainThread",
         "runOnMainThread",
         "Lkotlin/Function1;",
         "Ljava/lang/Runnable;",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "<init>",
         "(Lio/intercom/android/sdk/identity/UserIdentity;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function3;Lio/intercom/android/sdk/utilities/commons/TimeProvider;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lcom/intercom/twig/Twig;)V",
         "provider",
         "Lio/intercom/android/sdk/IntercomUserJwtProvider;",
         "getProvider",
         "()Lio/intercom/android/sdk/IntercomUserJwtProvider;",
         "setProvider",
         "(Lio/intercom/android/sdk/IntercomUserJwtProvider;)V",
         "lock",
         "pending",
         "Lio/intercom/android/sdk/identity/UserJwtRefresher$PendingRefresh;",
         "completed",
         "Lio/intercom/android/sdk/identity/UserJwtRefresher$CompletedRefresh;",
         "coolDownUntilMillis",
         "coolDownFingerprint",
         "shouldRefreshBeforeSending",
         "currentFingerprint",
         "refresh",
         "staleJwt",
         "isCanceled",
         "onUserJwtSet",
         "start",
         "complete",
         "fromProvider",
         "expireLocked",
         "abandonLocked",
         "await",
         "Callback",
         "CompletedRefresh",
         "PendingRefresh",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nUserJwtRefresher.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UserJwtRefresher.kt\nio/intercom/android/sdk/identity/UserJwtRefresher\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,193:1\n1#2:194\n*E\n"
)
public final class UserJwtRefresher {
   @NotNull
   public static final UserJwtRefresher$Companion Companion = new UserJwtRefresher$Companion(null);
   @NotNull
   private final UserIdentity userIdentity;
   @NotNull
   private final Function0 isAppInForeground;
   @NotNull
   private final Function3 applyJwt;
   @NotNull
   private final TimeProvider timeProvider;
   @NotNull
   private final Function0 elapsedRealtime;
   @NotNull
   private final Function0 isOnMainThread;
   @NotNull
   private final Function1 runOnMainThread;
   @NotNull
   private final Twig twig;
   @Nullable
   private volatile IntercomUserJwtProvider provider;
   @NotNull
   private final Object lock;
   @Nullable
   private UserJwtRefresher$PendingRefresh pending;
   @Nullable
   private UserJwtRefresher$CompletedRefresh completed;
   private long coolDownUntilMillis;
   @NotNull
   private String coolDownFingerprint;
   public static final int $stable;

   static { $stable = 8; }
   public static final long PROVIDER_TIMEOUT_MILLIS = 10000L;
   public static final long FAILURE_COOL_DOWN_MILLIS = 30000L;
   public static final long PROACTIVE_REFRESH_WINDOW_MILLIS = 60000L;
   private static final long CANCELLATION_POLL_MILLIS = 100L;

   public UserJwtRefresher(
      @NotNull UserIdentity var1,
      @NotNull Function0 var2,
      @NotNull Function3 var3,
      @NotNull TimeProvider var4,
      @NotNull Function0 var5,
      @NotNull Function0 var6,
      @NotNull Function1 var7,
      @NotNull Twig var8
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "userIdentity");
      Intrinsics.checkNotNullParameter(var2, "isAppInForeground");
      Intrinsics.checkNotNullParameter(var3, "applyJwt");
      Intrinsics.checkNotNullParameter(var4, "timeProvider");
      Intrinsics.checkNotNullParameter(var5, "elapsedRealtime");
      Intrinsics.checkNotNullParameter(var6, "isOnMainThread");
      Intrinsics.checkNotNullParameter(var7, "runOnMainThread");
      Intrinsics.checkNotNullParameter(var8, "twig");
      this.userIdentity = var1;
      this.isAppInForeground = var2;
      this.applyJwt = var3;
      this.timeProvider = var4;
      this.elapsedRealtime = var5;
      this.isOnMainThread = var6;
      this.runOnMainThread = var7;
      this.twig = var8;
      this.lock = new Object();
      this.coolDownFingerprint = "";
   }

   // $VF: synthetic method
   public UserJwtRefresher(
      UserIdentity var1,
      Function0 var2,
      Function3 var3,
      TimeProvider var4,
      Function0 var5,
      Function0 var6,
      Function1 var7,
      Twig var8,
      int var9,
      DefaultConstructorMarker var10
   ) {
      this(var1, var2, var3, (((var9 & 8) != 0) ? (TimeProvider) (TimeProvider.SYSTEM) : var4), (((var9 & 16) != 0) ? (Function0) (UserJwtRefresher$1.INSTANCE) : var5), (((var9 & 32) != 0) ? (Function0) (((kotlin.jvm.functions.Function0)()->UserJwtRefresher._init_$lambda$0())) : var6), (((var9 & 64) != 0) ? (Function1) (((kotlin.jvm.functions.Function1)(recoveredArgument_135_0)->UserJwtRefresher._init_$lambda$1((java.lang.Runnable)recoveredArgument_135_0))) : var7), (((var9 & 128) != 0) ? (Twig) (LumberMill.getLogger()) : var8));
   }

   @Nullable
   public final IntercomUserJwtProvider getProvider() {
      return this.provider;
   }

   public final void setProvider(@Nullable IntercomUserJwtProvider var1) {
      this.provider = var1;
   }

   public final boolean shouldRefreshBeforeSending(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "jwt");
      if (this.provider == null) {
         return false;
      } else {
         Long var10000 = JwtClaims.INSTANCE.expiresAtMillis(var1);
         if (var10000 != null) {
            long var2 = var10000;
            return var2 - this.timeProvider.currentTimeMillis() <= 60000L;
         } else {
            return false;
         }
      }
   }

   @NotNull
   public final String currentFingerprint() {
      String var10000 = this.userIdentity.getFingerprint();
      Intrinsics.checkNotNullExpressionValue(var10000, "getFingerprint(...)");
      return var10000;
   }

   // $VF: Extended synchronized range to monitorexit
   @Nullable
   public final String refresh(@NotNull String var1, @NotNull String var2, @NotNull Function0 var3) {
      Intrinsics.checkNotNullParameter(var1, "staleJwt");
      Intrinsics.checkNotNullParameter(var2, "fingerprint");
      Intrinsics.checkNotNullParameter(var3, "isCanceled");
      if (var2.length() == 0) {
         return null;
      }

      String var10000;
      synchronized (this.userIdentity) {
         int var7 = 0;
         String var18 = Intrinsics.areEqual(this.userIdentity.getFingerprint(), var2) ? this.userIdentity.getJwt() : null;
         var10000 = var18;
      }

      if (var10000 == null) {
         return null;
      }

      String var4 = var10000;
      if (var4.length() > 0 && !Intrinsics.areEqual(var4, var1)) {
         return var4;
      }

      if ((Boolean)this.isOnMainThread.invoke()) {
         return null;
      }

      UserJwtRefresher$PendingRefresh var20;
      synchronized (this.lock) {
         int var8 = 0;
         if (this.pending != null) {
            UserJwtRefresher$PendingRefresh var9 = this.pending;
            UserJwtRefresher$PendingRefresh var10 = var9;
            int var11 = 0;
            UserJwtRefresher$PendingRefresh var34 = ((Number)this.elapsedRealtime.invoke()).longValue() >= var10.getDeadlineMillis() ? var9 : null;
            if (var34 != null) {
               var10 = var34;
               var11 = 0;
               this.expireLocked(var10);
            }
         }

         if (this.completed != null) {
            UserJwtRefresher$CompletedRefresh var22 = this.completed;
            UserJwtRefresher$CompletedRefresh var25 = var22;
            int var30 = 0;
            UserJwtRefresher$CompletedRefresh var35 = Intrinsics.areEqual(var25.getStaleJwt(), var1) && Intrinsics.areEqual(var25.getFingerprint(), var2)
               ? var22
               : null;
            if (var35 != null) {
               var25 = var35;
               var30 = 0;
               return var25.getJwt();
            }
         }

         if (this.pending != null) {
            UserJwtRefresher$PendingRefresh var23 = this.pending;
            UserJwtRefresher$PendingRefresh var26 = var23;
            int var31 = 0;
            UserJwtRefresher$PendingRefresh var36 = !Intrinsics.areEqual(var26.getFingerprint(), var2) ? var23 : null;
            if (var36 != null) {
               var26 = var36;
               var31 = 0;
               this.abandonLocked(var26);
            }
         }

         UserJwtRefresher$PendingRefresh var37 = this.pending;
         if (this.pending == null) {
            var37 = this.start(var1, var2);
         }

         var20 = var37;
      }

      if (var20 == null) {
         return null;
      }

      UserJwtRefresher$PendingRefresh var5 = var20;
      var10000 = this.await(var5, var3);
      if (var10000 != null) {
         String var17 = var10000;
         int var21 = 0;
         var10000 = Intrinsics.areEqual(this.userIdentity.getFingerprint(), var2) ? var17 : null;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   // $VF: Extended synchronized range to monitorexit
   public final void onUserJwtSet(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "jwt");
      UserJwtRefresher$PendingRefresh var10000;
      synchronized (this.lock) {
         int var4 = 0;
         this.coolDownUntilMillis = 0L;
         this.completed = null;
         UserJwtRefresher$PendingRefresh var7 = this.pending;
         var10000 = var7;
      }

      if (var10000 != null) {
         UserJwtRefresher$PendingRefresh var2 = var10000;
         this.complete(var2, var1, false);
      }
   }

   private final UserJwtRefresher$PendingRefresh start(String var1, String var2) {
      if (this.provider == null) {
         return null;
      }

      IntercomUserJwtProvider var3 = this.provider;
      if (!(Boolean)this.isAppInForeground.invoke()) {
         return null;
      }

      if (Intrinsics.areEqual(var2, this.coolDownFingerprint) && ((Number)this.elapsedRealtime.invoke()).longValue() < this.coolDownUntilMillis) {
         return null;
      }

      UserJwtRefresher$PendingRefresh var4 = new UserJwtRefresher$PendingRefresh(var1, var2, ((Number)this.elapsedRealtime.invoke()).longValue() + 10000L);
      this.pending = var4;
      final io.intercom.android.sdk.IntercomUserJwtProvider recoveredExpressionCapture_136_0 = var3;
      final io.intercom.android.sdk.identity.UserJwtRefresher recoveredExpressionCapture_136_1 = this;
      final io.intercom.android.sdk.identity.UserJwtRefresher$PendingRefresh recoveredExpressionCapture_136_2 = var4;
      this.runOnMainThread.invoke(((java.lang.Runnable) () -> io.intercom.android.sdk.identity.UserJwtRefresher.start$lambda$12(recoveredExpressionCapture_136_0, recoveredExpressionCapture_136_1, recoveredExpressionCapture_136_2)));
      return var4;
   }

   // $VF: Extended synchronized range to monitorexit
   private final void complete(UserJwtRefresher$PendingRefresh var1, String var2, boolean var3) {
      ObjectRef var4 = new ObjectRef();
      String var27;
      synchronized (this.lock) {
         int var7 = 0;
         if (this.pending != var1) {
            if (var1.getTimedOut() && !var1.getAnswered() && var3) {
               var1.setAnswered(true);
               ObjectRef var25 = var4;
               String var10001;
               if (var2 != null) {
                  String var17 = var2;
                  String var19 = var17;
                  ObjectRef var10 = var4;
                  int var21 = 0;
                  Boolean var29 = var19.length() > 0 && !Intrinsics.areEqual(var19, var1.getStaleJwt());
                  var25 = var10;
                  var10001 = var29 ? var17 : null;
               } else {
                  var10001 = null;
               }

               var25.element = var10001;
               var27 = (String)var4.element;
               if ((String)var4.element != null) {
                  String var22 = var27;
                  int var23 = 0;
                  this.coolDownUntilMillis = 0L;
                  this.completed = new UserJwtRefresher$CompletedRefresh(var1.getStaleJwt(), var1.getFingerprint(), var22);
               }
            }

            var27 = null;
         } else {
            this.pending = null;
            var1.setAnswered(true);
            if (var2 != null) {
               String var9 = var2;
               String var11 = var9;
               int var12 = 0;
               var27 = var11.length() > 0
                     && !Intrinsics.areEqual(var11, var1.getStaleJwt())
                     && Intrinsics.areEqual(this.userIdentity.getFingerprint(), var1.getFingerprint())
                  ? var9
                  : null;
            } else {
               var27 = null;
            }

            String var8 = var27;
            if (var8 == null) {
               this.coolDownUntilMillis = ((Number)this.elapsedRealtime.invoke()).longValue() + 30000L;
               this.coolDownFingerprint = var1.getFingerprint();
            } else {
               this.completed = new UserJwtRefresher$CompletedRefresh(var1.getStaleJwt(), var1.getFingerprint(), var8);
            }

            var1.setResult(var8);
            var27 = var8;
         }

         String var14 = var27;
         var27 = var14;
      }

      label72: {
         String var5 = var27;
         if (var5 != null) {
            String var15 = var5;
            int var20 = 0;
            var27 = var3 ? var15 : null;
            if ((var3 ? var15 : null) != null) {
               break label72;
            }
         }

         var27 = (String)var4.element;
      }

      if (var27 != null) {
         String var16 = var27;
         int var18 = 0;
         this.applyJwt.invoke(var16, var1.getFingerprint(), var1.getStaleJwt());
      }

      var1.getLatch().countDown();
   }

   private final void expireLocked(UserJwtRefresher$PendingRefresh var1) {
      this.twig.w("The user JWT provider did not respond within 10s");
      this.pending = null;
      var1.setTimedOut(true);
      this.coolDownUntilMillis = var1.getDeadlineMillis() + 30000L;
      this.coolDownFingerprint = var1.getFingerprint();
      var1.setResult(null);
      var1.getLatch().countDown();
   }

   private final void abandonLocked(UserJwtRefresher$PendingRefresh var1) {
      this.pending = null;
      var1.setResult(null);
      var1.getLatch().countDown();
   }

   // $VF: Extended synchronized range to monitorexit
   private final String await(UserJwtRefresher$PendingRefresh var1, Function0 var2) {
      try {
         while (!var1.getLatch().await(100L, TimeUnit.MILLISECONDS)) {
            if ((Boolean)var2.invoke()) {
               return null;
            }

            if (((Number)this.elapsedRealtime.invoke()).longValue() >= var1.getDeadlineMillis()) {
               synchronized (this.lock) {
                  int var5 = 0;
                  if (this.pending == var1) {
                     this.expireLocked(var1);
                  }

                  Unit var9 = Unit.INSTANCE;
               }
            }
         }
      } catch (InterruptedException var8) {
         Thread.currentThread().interrupt();
         return null;
      }

      return var1.getResult();
   }

   private static final boolean _init_$lambda$0() {
      return Intrinsics.areEqual(Looper.myLooper(), Looper.getMainLooper());
   }

   private static final Unit _init_$lambda$1(Runnable var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      new Handler(Looper.getMainLooper()).post(var0);
      return Unit.INSTANCE;
   }

   private static final void start$lambda$12(IntercomUserJwtProvider var0, UserJwtRefresher var1, UserJwtRefresher$PendingRefresh var2) {
      try {
         var0.provideUserJwt(new UserJwtRefresher$Callback(var1, var2));
      } catch (Exception var4) {
         var1.twig.e("The user JWT provider threw an exception: " + var4.getMessage());
         var1.complete(var2, null, true);
      }
   }

   @JvmStatic
   @NotNull
   public static final UserJwtRefresher create(@NotNull UserIdentity var0, @NotNull IntercomDataLayer var1) {
      return Companion.create(var0, var1);
   }

   // $VF: synthetic method
   public static final void access$complete(UserJwtRefresher var0, UserJwtRefresher$PendingRefresh var1, String var2, boolean var3) {
      var0.complete(var1, var2, var3);
   }
}
