package io.intercom.android.sdk.m5;

import android.content.Context;
import android.content.Intent;
import io.intercom.android.sdk.helpcenter.articles.ArticleMetadata;
import io.intercom.android.sdk.m5.navigation.IntercomRootActivityArgs$ConversationScreenArgs;
import io.intercom.android.sdk.m5.navigation.IntercomRootActivityArgsKt;
import io.intercom.android.sdk.utilities.extensions.ContextExtensionsKt;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000:\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\u001a\u0017\u0010\u0000\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0000¢\u0006\u0002\u0010\u0004\u001a\u001a\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0002\u001a\u00020\u00032\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0000\u001aU\u0010\t\u001a\u00020\u00062\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\b\u0010\u0007\u001a\u0004\u0018\u00010\b2\f\b\u0002\u0010\u000f\u001a\u0006\u0012\u0002\b\u00030\u00102\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u0001¢\u0006\u0002\u0010\u0012\u001aM\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\b2\b\b\u0002\u0010\u000b\u001a\u00020\f2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000e2\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u0000¢\u0006\u0002\u0010\u0015¨\u0006\u0016",
   d2 = {
         "composerIntentFlagsFor",
         "",
         "context",
         "Landroid/content/Context;",
         "(Landroid/content/Context;)Ljava/lang/Integer;",
         "getConversationIntent",
         "Landroid/content/Intent;",
         "conversationId",
         "",
         "getComposerIntent",
         "initialMessage",
         "isLaunchedProgrammatically",
         "",
         "articleMetadata",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;",
         "activityName",
         "Ljava/lang/Class;",
         "intentFlags",
         "(Landroid/content/Context;Ljava/lang/String;ZLio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;Ljava/lang/String;Ljava/lang/Class;Ljava/lang/Integer;)Landroid/content/Intent;",
         "openComposer",
         "",
         "(Landroid/content/Context;Ljava/lang/String;ZLio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;Ljava/lang/String;Ljava/lang/Integer;)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nConversationScreenOpener.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationScreenOpener.kt\nio/intercom/android/sdk/m5/ConversationScreenOpenerKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,63:1\n1#2:64\n*E\n"
)
public final class ConversationScreenOpenerKt {
   @Nullable
   public static final Integer composerIntentFlagsFor(@NotNull Context var0) {
      Intrinsics.checkNotNullParameter(var0, "context");
      return ContextExtensionsKt.findActivity(var0) != null ? null : 268435456;
   }

   @NotNull
   public static final Intent getConversationIntent(@NotNull Context var0, @Nullable String var1) {
      Intrinsics.checkNotNullParameter(var0, "context");
      return IntercomRootActivityArgsKt.getIntentForArgs$default(
         var0, new IntercomRootActivityArgs$ConversationScreenArgs(var1, "", false, null, null, null, 48, null), null, null, 12, null
      );
   }

   @JvmOverloads
   @NotNull
   public static final Intent getComposerIntent(
      @NotNull Context var0,
      @NotNull String var1,
      boolean var2,
      @Nullable ArticleMetadata var3,
      @Nullable String var4,
      @NotNull Class var5,
      @Nullable Integer var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "initialMessage");
      Intrinsics.checkNotNullParameter(var5, "activityName");
      return IntercomRootActivityArgsKt.getIntentForArgs(
         var0,
         new IntercomRootActivityArgs$ConversationScreenArgs(
            var4, var1, var2, var3 != null ? var3.getId() : null, var3 != null ? var3.getTitle() : null, null, 32, null
         ),
         var5,
         var6
      );
   }

   // $VF: synthetic method
   public static Intent getComposerIntent$default(
      Context var0, String var1, boolean var2, ArticleMetadata var3, String var4, Class var5, Integer var6, int var7, Object var8
   ) {
      if ((var7 & 2) != 0) {
         var1 = "";
      }

      if ((var7 & 32) != 0) {
         var5 = IntercomRootActivity.class;
      }

      if ((var7 & 64) != 0) {
         var6 = 268435456;
      }

      return getComposerIntent(var0, var1, var2, var3, var4, var5, var6);
   }

   public static final void openComposer(
      @NotNull Context var0, @NotNull String var1, boolean var2, @Nullable ArticleMetadata var3, @Nullable String var4, @Nullable Integer var5
   ) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "initialMessage");
      Intent var6 = getComposerIntent$default(var0, var1, var2, var3, var4, null, var5, 32, null);
      int var7 = 0;
      int var8 = 0;
      var0.startActivity(var6);
   }

   // $VF: synthetic method
   public static void openComposer$default(Context var0, String var1, boolean var2, ArticleMetadata var3, String var4, Integer var5, int var6, Object var7) {
      if ((var6 & 2) != 0) {
         var1 = "";
      }

      if ((var6 & 4) != 0) {
         var2 = false;
      }

      if ((var6 & 8) != 0) {
         var3 = null;
      }

      if ((var6 & 16) != 0) {
         var4 = null;
      }

      if ((var6 & 32) != 0) {
         var5 = 268435456;
      }

      openComposer(var0, var1, var2, var3, var4, var5);
   }

   @JvmOverloads
   @NotNull
   public static final Intent getComposerIntent(
      @NotNull Context var0, @NotNull String var1, boolean var2, @Nullable ArticleMetadata var3, @Nullable String var4, @NotNull Class var5
   ) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "initialMessage");
      Intrinsics.checkNotNullParameter(var5, "activityName");
      return getComposerIntent$default(var0, var1, var2, var3, var4, var5, null, 64, null);
   }

   @JvmOverloads
   @NotNull
   public static final Intent getComposerIntent(
      @NotNull Context var0, @NotNull String var1, boolean var2, @Nullable ArticleMetadata var3, @Nullable String var4
   ) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "initialMessage");
      return getComposerIntent$default(var0, var1, var2, var3, var4, null, null, 96, null);
   }

   @JvmOverloads
   @NotNull
   public static final Intent getComposerIntent(@NotNull Context var0, boolean var1, @Nullable ArticleMetadata var2, @Nullable String var3) {
      Intrinsics.checkNotNullParameter(var0, "context");
      return getComposerIntent$default(var0, null, var1, var2, var3, null, null, 98, null);
   }
}
