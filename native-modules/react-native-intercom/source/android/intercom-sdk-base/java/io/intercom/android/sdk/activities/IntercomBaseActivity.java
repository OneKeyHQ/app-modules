package io.intercom.android.sdk.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.utilities.ContextLocaliser;
import java.util.Locale;

public abstract class IntercomBaseActivity extends AppCompatActivity implements IntercomActivity {
   @Override
   protected void attachBaseContext(Context var1) {
      super.attachBaseContext(this.localizedContext(var1));
   }

   private Context localizedContext(Context var1) {
      AppConfig var2 = (AppConfig)Injector.get().getAppConfigProvider().get();
      Configuration var3 = new Configuration(var1.getResources().getConfiguration());
      Locale var4 = ContextLocaliser.convertToLocale(var2.getLocale());
      var3.setLocale(var4);
      return var1.createConfigurationContext(var3);
   }

   @Override
   protected void onCreate(@Nullable Bundle var1) {
      super.onCreate(var1);
      Injector.get().getActivityFinisher().register(this);
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      Injector.get().getActivityFinisher().unregister(this);
   }
}
