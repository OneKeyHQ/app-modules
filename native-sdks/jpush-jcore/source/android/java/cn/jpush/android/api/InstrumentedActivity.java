package cn.jpush.android.api;

import android.app.Activity;

public class InstrumentedActivity extends Activity {
   @Override
   public void onStart() {
      super.onStart();
   }

   @Override
   protected void onResume() {
      super.onResume();
      JPushInterface.onResume(this);
   }

   @Override
   protected void onPause() {
      super.onPause();
      JPushInterface.onPause(this);
   }

   @Override
   public void onStop() {
      super.onStop();
   }
}
