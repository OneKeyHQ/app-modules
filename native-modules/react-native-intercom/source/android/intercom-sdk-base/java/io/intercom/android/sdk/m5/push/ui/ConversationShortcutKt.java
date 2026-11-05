package io.intercom.android.sdk.m5.push.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.content.pm.ShortcutInfoCompat.Builder;
import androidx.core.graphics.drawable.IconCompat;
import com.intercom.twig.Twig;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u001a@\u0010\u0000\u001a\u0018\u0012\f\u0012\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0002\u0012\u0006\u0012\u0004\u0018\u00010\u00030\u00012\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0000\u001a2\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0004\u001a\u00020\u00052\u000e\u0010\r\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u00022\b\u0010\u000e\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u000f\u001a\u00020\u0010H\u0000¨\u0006\u0011",
   d2 = {
         "createTemporaryShortcut",
         "Lkotlin/Pair;",
         "",
         "Landroidx/core/content/pm/ShortcutInfoCompat;",
         "context",
         "Landroid/content/Context;",
         "conversationId",
         "",
         "conversationTitle",
         "avatarBitmap",
         "Landroid/graphics/Bitmap;",
         "resetShortcuts",
         "",
         "dynamicShortcuts",
         "temporaryShortcut",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nConversationShortcut.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationShortcut.kt\nio/intercom/android/sdk/m5/push/ui/ConversationShortcutKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,69:1\n1#2:70\n*E\n"
)
public final class ConversationShortcutKt {
   @NotNull
   public static final Pair createTemporaryShortcut(@NotNull Context var0, @NotNull String var1, @NotNull String var2, @Nullable Bitmap var3) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      Intrinsics.checkNotNullParameter(var2, "conversationTitle");
      List var4 = ShortcutManagerCompat.getShortcuts(var0, 8);
      List var5 = ShortcutManagerCompat.getDynamicShortcuts(var0);
      if (var3 == null) {
         Intrinsics.checkNotNull(var4);
         Iterable var8 = var4;
         Iterator var9 = var8.iterator();

         Object var10000;
         while (true) {
            if (!var9.hasNext()) {
               var10000 = null;
               break;
            }

            Object var10 = var9.next();
            ShortcutInfoCompat var11 = (ShortcutInfoCompat)var10;
            int var12 = 0;
            if (Intrinsics.areEqual(var11.getId(), var1) && Intrinsics.areEqual(var11.getShortLabel(), var2)) {
               var10000 = var10;
               break;
            }
         }

         ShortcutInfoCompat var6 = (ShortcutInfoCompat)var10000;
         if (var6 != null) {
            ShortcutInfoCompat var18 = var6;
            int var22 = 0;
            return TuplesKt.to(null, var18);
         }

         Intrinsics.checkNotNull(var5);
         var8 = var5;
         var9 = var8.iterator();

         while (true) {
            if (!var9.hasNext()) {
               var10000 = null;
               break;
            }

            Object var23 = var9.next();
            ShortcutInfoCompat var25 = (ShortcutInfoCompat)var23;
            int var26 = 0;
            if (Intrinsics.areEqual(var25.getId(), var1) && Intrinsics.areEqual(var25.getShortLabel(), var2)) {
               var10000 = var23;
               break;
            }
         }

         var6 = (ShortcutInfoCompat)var10000;
         if (var6 != null) {
            ShortcutInfoCompat var17 = var6;
            int var21 = 0;
            return TuplesKt.to(null, var17);
         }
      }

      Builder var7 = new Builder(var0, var1).setLongLived(true).setShortLabel(var2).setIntent(new Intent("android.intent.action.VIEW")).setIsConversation();
      Builder var16 = var7;
      int var20 = 0;
      if (var3 != null) {
         IconCompat var24 = IconCompat.createWithAdaptiveBitmap(var3);
         var16.setIcon(var24);
      }

      ShortcutInfoCompat var14 = var7.build();
      ShortcutManagerCompat.pushDynamicShortcut(var0, var14);
      return TuplesKt.to(var5, var14);
   }

   public static final void resetShortcuts(@NotNull Context var0, @Nullable List var1, @Nullable ShortcutInfoCompat var2, @NotNull Twig var3) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var3, "twig");
      if (var2 != null) {
         ShortcutInfoCompat var4 = var2;
         int var5 = 0;
         ShortcutManagerCompat.removeDynamicShortcuts(var0, CollectionsKt.listOf(var4.getId()));
      }

      if (var1 != null) {
         List var9 = var1;
         int var10 = 0;

         try {
            Boolean var11 = ShortcutManagerCompat.setDynamicShortcuts(var0, var9);
         } catch (IllegalArgumentException var8) {
            var3.i(var8, "Could not set dynamic shortcuts, max number of dynamic shortcuts exceeded.");
            Unit var6 = Unit.INSTANCE;
         }
      }
   }
}
