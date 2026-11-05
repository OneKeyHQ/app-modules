package io.intercom.android.sdk.carousel;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.List;

public class CarouselScreenPagerAdapter extends FragmentPagerAdapter {
   private List fragments;

   public CarouselScreenPagerAdapter(FragmentManager var1, List var2) {
      super(var1);
      this.fragments = var2;
   }

   @Override
   public Fragment getItem(int var1) {
      return (Fragment)this.fragments.get(var1);
   }

   @Override
   public int getCount() {
      return this.fragments.size();
   }
}
