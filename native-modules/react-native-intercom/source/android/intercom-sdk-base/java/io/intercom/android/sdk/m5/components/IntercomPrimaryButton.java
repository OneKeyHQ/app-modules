package io.intercom.android.sdk.m5.components;

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
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.platform.AbstractComposeView;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.functions.Function0;
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
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u000f\b\u0001\u0018\u00002\u00020\u0001B'\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\r\u0010!\u001a\u00020\u0014H\u0017¢\u0006\u0002\u0010\"R+\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\u000b8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u0011\u0010\u0012\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R7\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00140\u00138F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u001a\u0010\u0012\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R/\u0010\u001b\u001a\u0004\u0018\u00010\u00072\b\u0010\n\u001a\u0004\u0018\u00010\u00078F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b \u0010\u0012\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001f¨\u0006#",
   d2 = {
         "Lio/intercom/android/sdk/m5/components/IntercomPrimaryButton;",
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
         "",
         "text",
         "getText",
         "()Ljava/lang/String;",
         "setText",
         "(Ljava/lang/String;)V",
         "text$delegate",
         "Landroidx/compose/runtime/MutableState;",
         "Lkotlin/Function0;",
         "",
         "onClick",
         "getOnClick",
         "()Lkotlin/jvm/functions/Function0;",
         "setOnClick",
         "(Lkotlin/jvm/functions/Function0;)V",
         "onClick$delegate",
         "trailingIconId",
         "getTrailingIconId",
         "()Ljava/lang/Integer;",
         "setTrailingIconId",
         "(Ljava/lang/Integer;)V",
         "trailingIconId$delegate",
         "Content",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nIntercomPrimaryButton.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomPrimaryButton.kt\nio/intercom/android/sdk/m5/components/IntercomPrimaryButton\n+ 2 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,131:1\n85#2:132\n117#2,2:133\n85#2:135\n117#2,2:136\n85#2:138\n117#2,2:139\n*S KotlinDebug\n*F\n+ 1 IntercomPrimaryButton.kt\nio/intercom/android/sdk/m5/components/IntercomPrimaryButton\n*L\n99#1:132\n99#1:133,2\n100#1:135\n100#1:136,2\n101#1:138\n101#1:139,2\n*E\n"
)
public final class IntercomPrimaryButton extends AbstractComposeView {
   @NotNull
   private final MutableState text$delegate;
   @NotNull
   private final MutableState onClick$delegate;
   @NotNull
   private final MutableState trailingIconId$delegate;
   public static int $stable;

   static {}

   @JvmOverloads
   public IntercomPrimaryButton(@NotNull Context var1, @Nullable AttributeSet var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "context");
      super(var1, var2, var3);
      this.text$delegate = SnapshotStateKt.mutableStateOf$default("", null, 2, null);
      this.onClick$delegate = SnapshotStateKt.mutableStateOf$default(((kotlin.jvm.functions.Function0) () -> IntercomPrimaryButton.onClick_delegate$lambda$0()), null, 2, null);
      this.trailingIconId$delegate = SnapshotStateKt.mutableStateOf$default(null, null, 2, null);
   }

   // $VF: synthetic method
   public IntercomPrimaryButton(Context var1, AttributeSet var2, int var3, int var4, DefaultConstructorMarker var5) {
      this(var1, (((var4 & 2) != 0) ? (AttributeSet) (null) : var2), (((var4 & 4) != 0) ? (int) (0) : var3));
   }

   @NotNull
   public final String getText() {
      State var1 = this.text$delegate;
      KProperty var2 = null;
      int var3 = 0;
      return (String)var1.getValue();
   }

   public final void setText(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      MutableState var2 = this.text$delegate;
      KProperty var3 = null;
      int var4 = 0;
      var2.setValue(var1);
   }

   @NotNull
   public final Function0 getOnClick() {
      State var1 = this.onClick$delegate;
      KProperty var2 = null;
      int var3 = 0;
      return (Function0)var1.getValue();
   }

   public final void setOnClick(@NotNull Function0 var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      MutableState var2 = this.onClick$delegate;
      KProperty var3 = null;
      int var4 = 0;
      var2.setValue(var1);
   }

   @Nullable
   public final Integer getTrailingIconId() {
      State var1 = this.trailingIconId$delegate;
      KProperty var2 = null;
      int var3 = 0;
      return (Integer)var1.getValue();
   }

   public final void setTrailingIconId(@Nullable Integer var1) {
      MutableState var2 = this.trailingIconId$delegate;
      KProperty var3 = null;
      int var4 = 0;
      var2.setValue(var1);
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   @Override
   public void Content(@Nullable Composer var1, int var2) {
      var1 = var1.startRestartGroup(346924157);
      ComposerKt.sourceInformation(var1, "C(Content)104@3701L133:IntercomPrimaryButton.kt#arqjag");
      int var3 = var2;
      if ((var2 & 6) == 0) {
         var3 |= var1.changed(this) ? 4 : 2;
      }

      if ((var3 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               346924157, var3, -1, "io.intercom.android.sdk.m5.components.IntercomPrimaryButton.Content (IntercomPrimaryButton.kt:103)"
            );
         }

         String var4 = this.getText();
         Function0 var5 = this.getOnClick();
         Integer var6 = this.getTrailingIconId();
         IntercomPrimaryButtonKt.IntercomPrimaryButton(var4, null, var6, var5, var1, 0, 2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var1.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.components.IntercomPrimaryButton recoveredCapture_223_0 = this;
         final int recoveredCapture_223_1 = var2;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_223_0, recoveredArgument_223_1) -> IntercomPrimaryButton.Content$lambda$1(recoveredCapture_223_0, recoveredCapture_223_1, (androidx.compose.runtime.Composer)recoveredArgument_223_0, ((java.lang.Integer)recoveredArgument_223_1).intValue())));
      }
   }

   @JvmOverloads
   public IntercomPrimaryButton(@NotNull Context var1, @Nullable AttributeSet var2) {
      Intrinsics.checkNotNullParameter(var1, "context");
      this(var1, var2, 0, 4, null);
   }

   @JvmOverloads
   public IntercomPrimaryButton(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      this(var1, null, 0, 6, null);
   }

   private static final Unit onClick_delegate$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit Content$lambda$1(IntercomPrimaryButton var0, int var1, Composer var2, int var3) {
      var0.Content(var2, RecomposeScopeImplKt.updateChangedFlags(var1 | 1));
      return Unit.INSTANCE;
   }
}
