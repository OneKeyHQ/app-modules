package io.intercom.android.sdk.m5.home.reducers;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponseKt;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.components.ErrorState$WithCTA;
import io.intercom.android.sdk.m5.components.ErrorState$WithoutCTA;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.m5.home.data.HomeCardType;
import io.intercom.android.sdk.m5.home.data.HomeCards;
import io.intercom.android.sdk.m5.home.data.HomeV2Response;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData;
import io.intercom.android.sdk.m5.home.states.HomeClientState;
import io.intercom.android.sdk.m5.home.states.HomeUiState;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Error;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Loading;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader;
import io.intercom.android.sdk.models.ActiveBot;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import io.intercom.android.sdk.models.Participant;
import io.intercom.android.sdk.models.PoweredBy;
import io.intercom.android.sdk.models.TeamPresence;
import io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData;
import io.intercom.android.sdk.models.Participant$Builder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B!\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ#\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0003H\u0000¢\u0006\u0002\b\u000fJ&\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0002R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/reducers/HomeReducer;",
         "",
         "config",
         "Lkotlin/Function0;",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "<init>",
         "(Lkotlin/jvm/functions/Function0;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;)V",
         "computeUiState",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState;",
         "homeClientState",
         "Lio/intercom/android/sdk/m5/home/states/HomeClientState;",
         "onRetryClicked",
         "",
         "computeUiState$intercom_sdk_base_release",
         "reduceHomeCards",
         "",
         "Lio/intercom/android/sdk/m5/home/data/HomeCards;",
         "homeCards",
         "newConversationCta",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nHomeReducer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeReducer.kt\nio/intercom/android/sdk/m5/home/reducers/HomeReducer\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,121:1\n827#2:122\n855#2,2:123\n1563#2:125\n1634#2,3:126\n1563#2:130\n1634#2,3:131\n1#3:129\n*S KotlinDebug\n*F\n+ 1 HomeReducer.kt\nio/intercom/android/sdk/m5/home/reducers/HomeReducer\n*L\n73#1:122\n73#1:123,2\n78#1:125\n78#1:126,3\n103#1:130\n103#1:131,3\n*E\n"
)
public final class HomeReducer {
   @NotNull
   private final Function0 config;
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   public static final int $stable;

   static { $stable = 8; }

   public HomeReducer(@NotNull Function0 var1, @NotNull IntercomDataLayer var2) {
      Intrinsics.checkNotNullParameter(var1, "config");
      Intrinsics.checkNotNullParameter(var2, "intercomDataLayer");
      super();
      this.config = var1;
      this.intercomDataLayer = var2;
   }

   // $VF: synthetic method
   public HomeReducer(Function0 var1, IntercomDataLayer var2, int var3, DefaultConstructorMarker var4) {
      if ((var3 & 1) != 0) {
         var1 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.home.reducers.HomeReducer._init_$lambda$0());
      }

      if ((var3 & 2) != 0) {
         var2 = Injector.get().getDataLayer();
      }

      this(var1, var2);
   }

   @NotNull
   public final HomeUiState computeUiState$intercom_sdk_base_release(@NotNull HomeClientState var1, @NotNull Function0 var2) {
      Intrinsics.checkNotNullParameter(var1, "homeClientState");
      Intrinsics.checkNotNullParameter(var2, "onRetryClicked");
      NetworkResponse var3 = var1.getOpenMessengerResponse();
      NetworkResponse var4 = var1.getHomeResponse();
      if (var3 != null && var4 != null) {
         NetworkResponse var5 = var3;
         if (var5 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError || var5 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError || var5 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError) {
            return new io.intercom.android.sdk.m5.home.states.HomeUiState$Error(
               null,
               NetworkResponseKt.isRetryable(var3) ? new io.intercom.android.sdk.m5.components.ErrorState$WithCTA(0, 0, null, 0, var2, 15, null) : new io.intercom.android.sdk.m5.components.ErrorState$WithoutCTA(0, 0, null, 7, null),
               HomeHeaderStateReducerKt.computeErrorHeader(((AppConfig)this.config.invoke()).getConfigModules())
            );
         }

         if (!(var5 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success)) {
            throw new NoWhenBranchMatchedException();
         }

         OpenMessengerResponse var27 = (OpenMessengerResponse)((io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success)var3).getBody();
         io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData var6 = var27.getNewConversationData();
         NetworkResponse var7 = var4;
         HomeUiState var10000;
         if (var7 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError || var7 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError) {
            var10000 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Error(
               null, new io.intercom.android.sdk.m5.components.ErrorState$WithoutCTA(0, 0, null, 7, null), HomeHeaderStateReducerKt.computeErrorHeader(((AppConfig)this.config.invoke()).getConfigModules())
            );
         } else if (var7 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError) {
            var10000 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Error(
               null,
               new io.intercom.android.sdk.m5.components.ErrorState$WithCTA(0, 0, null, 0, var2, 15, null),
               HomeHeaderStateReducerKt.computeErrorHeader(((AppConfig)this.config.invoke()).getConfigModules())
            );
         } else {
            if (!(var7 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success)) {
               throw new NoWhenBranchMatchedException();
            }

            PoweredBy var47 = var27.getPoweredBy();
            Iterable var8 = this.reduceHomeCards(((HomeV2Response)((io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success)var4).getBody()).getCards(), var6);
            PoweredBy var17 = var47;
            int var9 = 0;
            Iterable var10 = var8;
            ArrayList var11 = new ArrayList();
            int var12 = 0;

            for (Object var14 : var10) {
               HomeCards var15 = (HomeCards)var14;
               int var16 = 0;
               if (!(var15 instanceof io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData)
                  || !((io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData)var15).getPreventMultipleInboundConversationsEnabled()
                  || ((io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData)var15).getOpenInboundConversationsIds().isEmpty()) {
                  var11.add(var14);
               }
            }

            List var10001 = (List)var11;
            Iterable var28 = ((TeamPresence)this.intercomDataLayer.getTeamPresence().getValue()).getBuiltActiveAdmins();
            List var18 = var10001;
            PoweredBy var42 = var17;
            int var30 = 0;
            Iterable var32 = var28;
            var destination$iv$ivx = new ArrayList(CollectionsKt.collectionSizeOrDefault(var28, 10));
            int var35 = 0;

            for (Object var39 : var32) {
               Participant var40 = (Participant)var39;
               Collection var19 = destination$iv$ivx;
               int var41 = 0;
               Avatar var10002 = var40.getAvatar();
               Intrinsics.checkNotNullExpressionValue(var10002, "getAvatar(...)");
               Boolean var10003 = var40.isBot();
               Intrinsics.checkNotNullExpressionValue(var10003, "isBot(...)");
               var19.add(new AvatarWrapper(var10002, var10003));
            }

            PoweredBy var48;
            List var49;
            List var50;
            AvatarWrapper var52;
            label56: {
               List var45 = (List)destination$iv$ivx;
               var48 = var42;
               var49 = var18;
               var50 = var45;
               ActiveBot var29 = ((TeamPresence)this.intercomDataLayer.getTeamPresence().getValue()).getActiveBot();
               if (var29 != null) {
                  io.intercom.android.sdk.models.Participant$Builder var31 = var29.getParticipant();
                  if (var31 != null) {
                     Participant var33 = var31.build();
                     if (var33 != null) {
                        Participant var36 = var33;
                        List var46 = var45;
                        List var44 = var18;
                        PoweredBy var43 = var42;
                        int var38 = 0;
                        Avatar var51 = var36.getAvatar();
                        Intrinsics.checkNotNullExpressionValue(var51, "getAvatar(...)");
                        Boolean var53 = var36.isBot();
                        Intrinsics.checkNotNullExpressionValue(var53, "isBot(...)");
                        AvatarWrapper var20 = new AvatarWrapper(var51, var53);
                        var48 = var43;
                        var49 = var44;
                        var50 = var46;
                        var52 = var20;
                        break label56;
                     }
                  }
               }

               var52 = null;
            }

            io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader var21 = HomeHeaderStateReducerKt.computeContentHeader(
               ((AppConfig)this.config.invoke()).getConfigModules(),
               (TeamPresence)this.intercomDataLayer.getTeamPresence().getValue(),
               var1.isHeaderImageLoaded()
            );
            boolean var22 = ((AppConfig)this.config.invoke()).isAccessToTeammateEnabled();
            AvatarWrapper var23 = var52;
            List var24 = var50;
            List var25 = var49;
            PoweredBy var26 = var48;
            var10000 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Content(var26, var25, var24, var23, var22, var21);
         }

         return var10000;
      } else {
         return new io.intercom.android.sdk.m5.home.states.HomeUiState$Loading(null);
      }
   }

   private final List reduceHomeCards(List var1, io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData var2) {
      if (var2 == null) {
         return var1;
      }

      Iterable var3 = var1;
      int var4 = 0;
      Iterable var5 = var3;
      ArrayList var6 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var3, 10));
      int var7 = 0;

      for (Object var9 : var5) {
         HomeCards var10 = (HomeCards)var9;
         Collection var12 = var6;
         int var11 = 0;
         var12.add(
            var10 instanceof io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData
               ? new io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData(
                  "",
                  HomeCardType.NEW_CONVERSATION,
                  ((io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData)var10).getAction(),
                  ((io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData)var10).getPreventMultipleInboundConversationsEnabled(),
                  ((io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData)var10).getOpenInboundConversationsIds(),
                  ((io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData)var10).getShowFinAvatar(),
                  ((io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData)var10).getShowTeammateExpectations(),
                  var2.getHomeCard()
               )
               : var10
         );
      }

      return (List)var6;
   }

   private static final AppConfig _init_$lambda$0() {
      return (AppConfig)Injector.get().getAppConfigProvider().get();
   }

   public HomeReducer() {
      this(null, null, 3, null);
   }
}
