package io.intercom.android.sdk;

import androidx.compose.runtime.internal.StabilityInferred;
import com.intercom.twig.Twig;
import io.ably.lib.realtime.AblyRealtime;
import io.ably.lib.realtime.Channel;
import io.ably.lib.realtime.ChannelStateListener;
import io.ably.lib.realtime.ConnectionStateListener;
import io.ably.lib.realtime.Channel.MessageListener;
import io.ably.lib.rest.Auth.TokenCallback;
import io.ably.lib.rest.Auth.TokenParams;
import io.intercom.android.sdk.api.MessengerApi;
import io.intercom.android.sdk.api.MessengerApi$DefaultImpls;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.models.AblyAuthResponse;
import io.intercom.android.sdk.models.AblyConnectResponse;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Triple;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.SupervisorKt;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.SharedFlow;
import kotlinx.coroutines.flow.SharingStarted;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000x\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0001\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001a0\u0019J\u000e\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001a0\u001dH\u0002J\u0006\u0010\u001e\u001a\u00020\u001fJ(\u0010 \u001a\u001c\u0012\u0004\u0012\u00020\"\u0012\u0004\u0012\u00020\"\u0012\n\u0012\b\u0012\u0004\u0012\u00020$0#\u0018\u00010!H\u0082@¢\u0006\u0002\u0010%J\b\u0010&\u001a\u00020'H\u0002J\u0006\u0010(\u001a\u00020\u001fJ\u0006\u0010)\u001a\u00020\u0017J\u0006\u0010*\u001a\u00020\u001fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\u0018\u001a\n\u0012\u0004\u0012\u00020\u001a\u0018\u00010\u0019X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006+",
   d2 = {
         "Lio/intercom/android/sdk/AblyManager;",
         "",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "messengerApi",
         "Lio/intercom/android/sdk/api/MessengerApi;",
         "<init>",
         "(Lcom/intercom/twig/Twig;Lio/intercom/android/sdk/api/MessengerApi;)V",
         "scope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "ablyRealtime",
         "Lio/ably/lib/realtime/AblyRealtime;",
         "currentChannel",
         "Lio/ably/lib/realtime/Channel;",
         "messageListener",
         "Lio/ably/lib/realtime/Channel$MessageListener;",
         "connectionListener",
         "Lio/ably/lib/realtime/ConnectionStateListener;",
         "channelStateListener",
         "Lio/ably/lib/realtime/ChannelStateListener;",
         "registeredConnectionListener",
         "registeredChannelStateListener",
         "hasChannelBeenAttached",
         "",
         "_sharedEventFlow",
         "Lkotlinx/coroutines/flow/SharedFlow;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedAblyEvent;",
         "getSharedEventFlow",
         "createAblyEventFlow",
         "Lkotlinx/coroutines/flow/Flow;",
         "connect",
         "",
         "getAblyConfiguration",
         "Lkotlin/Triple;",
         "",
         "",
         "Lio/intercom/android/sdk/models/AblyEndpoint;",
         "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "createAuthCallback",
         "Lio/ably/lib/rest/Auth$TokenCallback;",
         "removeListeners",
         "isConnected",
         "disconnect",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class AblyManager {
   @NotNull
   private final Twig twig;
   @NotNull
   private final MessengerApi messengerApi;
   @NotNull
   private final CoroutineScope scope;
   @Nullable
   private AblyRealtime ablyRealtime;
   @Nullable
   private Channel currentChannel;
   @Nullable
   private MessageListener messageListener;
   @Nullable
   private ConnectionStateListener connectionListener;
   @Nullable
   private ChannelStateListener channelStateListener;
   @Nullable
   private ConnectionStateListener registeredConnectionListener;
   @Nullable
   private ChannelStateListener registeredChannelStateListener;
   private boolean hasChannelBeenAttached;
   @Nullable
   private SharedFlow _sharedEventFlow;
   public static final int $stable;

   static { $stable = 8; }

   public AblyManager(@NotNull Twig var1, @NotNull MessengerApi var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "twig");
      Intrinsics.checkNotNullParameter(var2, "messengerApi");
      this.twig = var1;
      this.messengerApi = var2;
      this.scope = CoroutineScopeKt.CoroutineScope(SupervisorKt.SupervisorJob$default(null, 1, null).plus(Dispatchers.getIO()));
   }

   // $VF: Extended synchronized range to monitorexit
   @NotNull
   public final SharedFlow getSharedEventFlow() {
      this.twig.d("AblyManager: getSharedEventFlow() called");
      if (this._sharedEventFlow == null) {
         synchronized (this) {
            int var2 = 0;
            if (this._sharedEventFlow == null) {
               this.twig.d("AblyManager: Creating new shared flow");
               this._sharedEventFlow = FlowKt.shareIn(this.createAblyEventFlow(), this.scope, SharingStarted.Companion.getEagerly(), 0);
            }

            Unit var5 = Unit.INSTANCE;
         }
      }

      SharedFlow var10000 = this._sharedEventFlow;
      Intrinsics.checkNotNull(this._sharedEventFlow);
      return var10000;
   }

   private final Flow createAblyEventFlow() {
      return FlowKt.callbackFlow(new AblyManager$createAblyEventFlow$1(this, null));
   }

   public final void connect() {
      boolean var1 = ((AppConfig)Injector.get().getAppConfigProvider().get()).hasFeature("ably-realtime-disabled");
      if (!var1) {
         if (this.ablyRealtime == null) {
            BuildersKt.launch$default(this.scope, null, null, new AblyManager$connect$1(this, null), 3, null);
         }
      }
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   private final Object getAblyConfiguration(Continuation var1) {
      AblyManager recoveredThis = this;
      AblyManager$getAblyConfiguration$1 var10;
      label83: {
         if (var1 instanceof AblyManager$getAblyConfiguration$1) {
            var10 = (AblyManager$getAblyConfiguration$1)var1;
            if ((var10.label & -2147483648) != 0) {
               var10.label -= Integer.MIN_VALUE;
               break label83;
            }
         }

         var10 = new AblyManager$getAblyConfiguration$1(recoveredThis, var1);
      }

      AblyConnectResponse var3;
      Object var10000;
      label86: {
         Object var9 = var10.result;
         Object var11 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
         switch (var10.label) {
            case 0:
               ResultKt.throwOnFailure(var9);

               try {
                  MessengerApi var18 = recoveredThis.messengerApi;
                  var10.L$0 = recoveredThis;
                  var10.label = 1;
                  var10000 = MessengerApi$DefaultImpls.getAblyConnectConfigSuspend$default(var18, null, var10, 1, null);
               } catch (Exception var15) {
                  recoveredThis.twig.e("AblyManager: Configuration error: " + var15.getMessage());
                  return null;
               }

               if (var10000 == var11) {
                  return var11;
               }
               break;
            case 1:
               recoveredThis = (AblyManager)var10.L$0;

               try {
                  ResultKt.throwOnFailure(var9);
                  var10000 = var9;
                  break;
               } catch (Exception var14) {
                  recoveredThis.twig.e("AblyManager: Configuration error: " + var14.getMessage());
                  return null;
               }
            case 2:
               var3 = (AblyConnectResponse)var10.L$1;
               recoveredThis = (AblyManager)var10.L$0;

               try {
                  ResultKt.throwOnFailure(var9);
                  var10000 = var9;
                  break label86;
               } catch (Exception var13) {
                  recoveredThis.twig.e("AblyManager: Configuration error: " + var13.getMessage());
                  return null;
               }
            default:
               throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
         }

         try {
            NetworkResponse var2 = (NetworkResponse)var10000;
            if (!(var2 instanceof NetworkResponse$Success)) {
               recoveredThis.twig.e("AblyManager: Failed to get connect config");
               return null;
            }

            var3 = (AblyConnectResponse)((NetworkResponse$Success)var2).getBody();
            MessengerApi var19 = recoveredThis.messengerApi;
            var10.L$0 = recoveredThis;
            var10.L$1 = var3;
            var10.label = 2;
            var10000 = MessengerApi$DefaultImpls.getAblyAuthTokenSuspend$default(var19, null, var10, 1, null);
         } catch (Exception var16) {
            recoveredThis.twig.e("AblyManager: Configuration error: " + var16.getMessage());
            return null;
         }

         if (var10000 == var11) {
            return var11;
         }
      }

      try {
         NetworkResponse var4 = (NetworkResponse)var10000;
         if (var4 instanceof NetworkResponse$Success) {
            AblyAuthResponse var5 = (AblyAuthResponse)((NetworkResponse$Success)var4).getBody();
            String var6 = var5.getToken();
            String var7 = var3.getChannel();
            List var8 = var3.getEndpoints();
            if (var6.length() != 0 && var7.length() != 0) {
               return new Triple(var6, var7, var8);
            }

            recoveredThis.twig.e("AblyManager: Missing token or channel");
            return null;
         } else {
            recoveredThis.twig.e("AblyManager: Failed to get auth token");
            return null;
         }
      } catch (Exception var12) {
         recoveredThis.twig.e("AblyManager: Configuration error: " + var12.getMessage());
         return null;
      }
   }

   private final TokenCallback createAuthCallback() {
      final io.intercom.android.sdk.AblyManager recoveredCapture_0_0 = this;
      return ((io.ably.lib.rest.Auth.TokenCallback) (recoveredArgument_0_0) -> AblyManager.createAuthCallback$lambda$1(recoveredCapture_0_0, (io.ably.lib.rest.Auth.TokenParams)recoveredArgument_0_0));
   }

   public final void removeListeners() {
      this.messageListener = null;
      this.connectionListener = null;
      this.channelStateListener = null;
   }

   public final boolean isConnected() {
      return Intrinsics.areEqual(
         this.ablyRealtime != null && this.ablyRealtime.connection != null && this.ablyRealtime.connection.state != null
            ? this.ablyRealtime.connection.state.name()
            : null,
         "connected"
      );
   }

   public final void disconnect() {
      BuildersKt.launch$default(this.scope, null, null, new AblyManager$disconnect$1(this, null), 3, null);
   }

   // Java preserves Kotlin unchecked propagation with a throws-only type parameter.
   private static final <E extends Exception> Object createAuthCallback$lambda$1(AblyManager var0, TokenParams var1) throws E {
      try {
         NetworkResponse var2 = (NetworkResponse)BuildersKt.runBlocking$default(null, new AblyManager$createAuthCallback$1$authResponse$1(var0, null), 1, null);
         if (var2 instanceof NetworkResponse$Success) {
            var0.twig.d("AblyManager: Auth callback got fresh token");
            return ((AblyAuthResponse)((NetworkResponse$Success)var2).getBody()).getToken();
         } else {
            var0.twig.e("AblyManager: Auth callback failed to get token");
            throw new Exception("Failed to get auth token from server");
         }
      } catch (Exception var4) {
         var0.twig.e("AblyManager: Auth callback error: " + var4.getMessage());
         throw (E)var4;
      }
   }

   // $VF: synthetic method
   public static final void access$setMessageListener$p(AblyManager var0, MessageListener var1) {
      var0.messageListener = var1;
   }

   // $VF: synthetic method
   public static final void access$setConnectionListener$p(AblyManager var0, ConnectionStateListener var1) {
      var0.connectionListener = var1;
   }

   // $VF: synthetic method
   public static final void access$setChannelStateListener$p(AblyManager var0, ChannelStateListener var1) {
      var0.channelStateListener = var1;
   }

   // $VF: synthetic method
   public static final AblyRealtime access$getAblyRealtime$p(AblyManager var0) {
      return var0.ablyRealtime;
   }

   // $VF: synthetic method
   public static final Twig access$getTwig$p(AblyManager var0) {
      return var0.twig;
   }

   // $VF: synthetic method
   public static final boolean access$getHasChannelBeenAttached$p(AblyManager var0) {
      return var0.hasChannelBeenAttached;
   }

   // $VF: synthetic method
   public static final void access$setHasChannelBeenAttached$p(AblyManager var0, boolean var1) {
      var0.hasChannelBeenAttached = var1;
   }

   // $VF: synthetic method
   public static final Object access$getAblyConfiguration(AblyManager var0, Continuation var1) {
      return var0.getAblyConfiguration(var1);
   }

   // $VF: synthetic method
   public static final void access$setAblyRealtime$p(AblyManager var0, AblyRealtime var1) {
      var0.ablyRealtime = var1;
   }

   // $VF: synthetic method
   public static final void access$setRegisteredConnectionListener$p(AblyManager var0, ConnectionStateListener var1) {
      var0.registeredConnectionListener = var1;
   }

   // $VF: synthetic method
   public static final void access$setCurrentChannel$p(AblyManager var0, Channel var1) {
      var0.currentChannel = var1;
   }

   // $VF: synthetic method
   public static final void access$setRegisteredChannelStateListener$p(AblyManager var0, ChannelStateListener var1) {
      var0.registeredChannelStateListener = var1;
   }

   // $VF: synthetic method
   public static final Channel access$getCurrentChannel$p(AblyManager var0) {
      return var0.currentChannel;
   }

   // $VF: synthetic method
   public static final TokenCallback access$createAuthCallback(AblyManager var0) {
      return var0.createAuthCallback();
   }

   // $VF: synthetic method
   public static final ConnectionStateListener access$getConnectionListener$p(AblyManager var0) {
      return var0.connectionListener;
   }

   // $VF: synthetic method
   public static final ChannelStateListener access$getChannelStateListener$p(AblyManager var0) {
      return var0.channelStateListener;
   }

   // $VF: synthetic method
   public static final MessageListener access$getMessageListener$p(AblyManager var0) {
      return var0.messageListener;
   }

   // $VF: synthetic method
   public static final ChannelStateListener access$getRegisteredChannelStateListener$p(AblyManager var0) {
      return var0.registeredChannelStateListener;
   }

   // $VF: synthetic method
   public static final ConnectionStateListener access$getRegisteredConnectionListener$p(AblyManager var0) {
      return var0.registeredConnectionListener;
   }

   // $VF: synthetic method
   public static final void access$set_sharedEventFlow$p(AblyManager var0, SharedFlow var1) {
      var0._sharedEventFlow = var1;
   }

   // $VF: synthetic method
   public static final MessengerApi access$getMessengerApi$p(AblyManager var0) {
      return var0.messengerApi;
   }
}
