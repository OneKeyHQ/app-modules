package io.intercom.android.sdk.m5.helpcenter.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.platform.AbstractComposeView;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.ConfigurableIntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B'\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\r\u0010\u001a\u001a\u00020\u001bH\u0017¢\u0006\u0002\u0010\u001cR/\u0010\f\u001a\u0004\u0018\u00010\u000b2\b\u0010\n\u001a\u0004\u0018\u00010\u000b8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u0011\u0010\u0012\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R+\u0010\u0014\u001a\u00020\u00132\u0006\u0010\n\u001a\u00020\u00138F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u0019\u0010\u0012\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018¨\u0006\u001d",
   d2 = {
         "Lio/intercom/android/sdk/m5/helpcenter/ui/components/TeamPresenceComponent;",
         "Landroidx/compose/ui/platform/AbstractComposeView;",
         "context",
         "Landroid/content/Context;",
         "attrs",
         "Landroid/util/AttributeSet;",
         "defStyle",
         "",
         "<init>",
         "(Landroid/content/Context;Landroid/util/AttributeSet;I)V",
         "<set-?>",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;",
         "teamPresenceState",
         "getTeamPresenceState",
         "()Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;",
         "setTeamPresenceState",
         "(Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;)V",
         "teamPresenceState$delegate",
         "Landroidx/compose/runtime/MutableState;",
         "",
         "needsChatBubble",
         "getNeedsChatBubble",
         "()Z",
         "setNeedsChatBubble",
         "(Z)V",
         "needsChatBubble$delegate",
         "Content",
         "",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nTeamPresenceComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TeamPresenceComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/TeamPresenceComponent\n+ 2 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,245:1\n85#2:246\n117#2,2:247\n85#2:249\n117#2,2:250\n*S KotlinDebug\n*F\n+ 1 TeamPresenceComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/TeamPresenceComponent\n*L\n148#1:246\n148#1:247,2\n149#1:249\n149#1:250,2\n*E\n"
)
public final class TeamPresenceComponent extends AbstractComposeView {
   @NotNull
   private final MutableState teamPresenceState$delegate;
   @NotNull
   private final MutableState needsChatBubble$delegate;
   public static int $stable;

   static {}

   @JvmOverloads
   public TeamPresenceComponent(@NotNull Context var1, @Nullable AttributeSet var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "context");
      super(var1, var2, var3);
      this.teamPresenceState$delegate = SnapshotStateKt.mutableStateOf$default(null, null, 2, null);
      this.needsChatBubble$delegate = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
   }

   // $VF: synthetic method
   public TeamPresenceComponent(Context var1, AttributeSet var2, int var3, int var4, DefaultConstructorMarker var5) {
      this(var1, (((var4 & 2) != 0) ? (AttributeSet) (null) : var2), (((var4 & 4) != 0) ? (int) (0) : var3));
   }

   @Nullable
   public final ArticleViewState$TeamPresenceState getTeamPresenceState() {
      State var1 = this.teamPresenceState$delegate;
      KProperty var2 = null;
      int var3 = 0;
      return (ArticleViewState$TeamPresenceState)var1.getValue();
   }

   public final void setTeamPresenceState(@Nullable ArticleViewState$TeamPresenceState var1) {
      MutableState var2 = this.teamPresenceState$delegate;
      KProperty var3 = null;
      int var4 = 0;
      var2.setValue(var1);
   }

   public final boolean getNeedsChatBubble() {
      State var1 = this.needsChatBubble$delegate;
      KProperty var2 = null;
      int var3 = 0;
      return (Boolean)var1.getValue();
   }

   public final void setNeedsChatBubble(boolean var1) {
      MutableState var2 = this.needsChatBubble$delegate;
      Object var3 = null;
      Object var4 = var1;
      int var5 = 0;
      var2.setValue(var4);
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   @Override
   public void Content(@Nullable Composer var1, int var2) {
      var1 = var1.startRestartGroup(-1850798977);
      ComposerKt.sourceInformation(var1, "C(Content)*153@6265L199,153@6187L277:TeamPresenceComponent.kt#ngglre");
      int var3 = var2;
      if ((var2 & 6) == 0) {
         var3 |= var1.changed(this) ? 4 : 2;
      }

      if ((var3 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1850798977, var3, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.TeamPresenceComponent.Content (TeamPresenceComponent.kt:151)"
            );
         }

         ArticleViewState$TeamPresenceState var10000 = this.getTeamPresenceState();
         if (var10000 != null) {
            ArticleViewState$TeamPresenceState var4 = var10000;
            int var5 = 0;
            Object var7 = Injector.get().getAppConfigProvider().get();
            Intrinsics.checkNotNullExpressionValue(var7, "get(...)");
            ConfigurableIntercomThemeKt.ConfigurableIntercomTheme(
               (AppConfig)var7,
               ComposableLambdaKt.rememberComposableLambda(-1302062926, true, new TeamPresenceComponent$Content$1$1(this, var4), var1, 54),
               var1,
               48
            );
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var8 = var1.endRestartGroup();
      if (var8 != null) {
         final io.intercom.android.sdk.m5.helpcenter.ui.components.TeamPresenceComponent recoveredCapture_879_0 = this;
         final int recoveredCapture_879_1 = var2;
         var8.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_879_0, recoveredArgument_879_1) -> TeamPresenceComponent.Content$lambda$1(recoveredCapture_879_0, recoveredCapture_879_1, (androidx.compose.runtime.Composer)recoveredArgument_879_0, ((java.lang.Integer)recoveredArgument_879_1).intValue())));
      }
   }

   @JvmOverloads
   public TeamPresenceComponent(@NotNull Context var1, @Nullable AttributeSet var2) {
      Intrinsics.checkNotNullParameter(var1, "context");
      this(var1, var2, 0, 4, null);
   }

   @JvmOverloads
   public TeamPresenceComponent(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      this(var1, null, 0, 6, null);
   }

   private static final Unit Content$lambda$1(TeamPresenceComponent var0, int var1, Composer var2, int var3) {
      var0.Content(var2, RecomposeScopeImplKt.updateChangedFlags(var1 | 1));
      return Unit.INSTANCE;
   }
}
