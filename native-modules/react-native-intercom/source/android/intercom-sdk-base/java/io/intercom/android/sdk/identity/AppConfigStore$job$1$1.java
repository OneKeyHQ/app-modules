package io.intercom.android.sdk.identity;

import androidx.datastore.preferences.core.Preferences;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class AppConfigStore$job$1$1 implements FlowCollector {
   // $VF: synthetic field
   final AppConfigStore this$0;

   AppConfigStore$job$1$1(AppConfigStore var1) {
      this.this$0 = var1;
   }

   public final Object emit(Preferences var1, Continuation var2) {
      AppConfigStore.access$setCachedPrefs$p(this.this$0, var1);
      AppConfigStore.access$getFirstLoad$p(this.this$0).complete(Unit.INSTANCE);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((Preferences)var1, var2);
   }
}
