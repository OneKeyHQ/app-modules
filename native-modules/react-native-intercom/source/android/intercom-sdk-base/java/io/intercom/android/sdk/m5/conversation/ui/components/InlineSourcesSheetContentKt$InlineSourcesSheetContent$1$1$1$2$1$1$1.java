package io.intercom.android.sdk.m5.conversation.ui.components;

import android.content.Context;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.models.Source;
import io.intercom.android.sdk.utilities.LinkOpener;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class InlineSourcesSheetContentKt$InlineSourcesSheetContent$1$1$1$2$1$1$1 implements Function0 {
   // $VF: synthetic field
   final boolean $isArticle;
   // $VF: synthetic field
   final Source $source;
   // $VF: synthetic field
   final Context $context;

   InlineSourcesSheetContentKt$InlineSourcesSheetContent$1$1$1$2$1$1$1(boolean var1, Source var2, Context var3) {
      this.$isArticle = var1;
      this.$source = var2;
      this.$context = var3;
   }

   public final void recoveredVoidInvoke$979ff292a6da9613() {
      boolean var1 = this.$isArticle;
      LinkOpener.handleUrl(this.$source.getUrl(), this.$context, Injector.get().getApi(), var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke() {
      this.recoveredVoidInvoke$979ff292a6da9613();
      return Unit.INSTANCE;
   }
}
