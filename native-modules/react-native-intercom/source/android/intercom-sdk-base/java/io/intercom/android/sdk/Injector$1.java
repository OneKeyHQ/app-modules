package io.intercom.android.sdk;

import io.intercom.android.sdk.identity.AppConfig;

class Injector$1 implements Provider {
   // $VF: synthetic field
   final Injector this$0;

   Injector$1(Injector var1) {
      this.this$0 = var1;
   }

   public AppConfig get() {
      return (AppConfig)Injector.access$000(this.this$0).getConfig().getValue();
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
