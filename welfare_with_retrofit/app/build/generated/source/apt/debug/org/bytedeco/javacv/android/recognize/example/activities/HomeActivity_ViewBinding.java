// Generated code from Butter Knife. Do not modify!
package org.bytedeco.javacv.android.recognize.example.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import org.bytedeco.javacv.android.recognize.example.R;

public class HomeActivity_ViewBinding implements Unbinder {
  private HomeActivity target;

  @UiThread
  public HomeActivity_ViewBinding(HomeActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public HomeActivity_ViewBinding(HomeActivity target, View source) {
    this.target = target;

    target.homeProgressBar = Utils.findRequiredViewAsType(source, R.id.homeProgressBar, "field 'homeProgressBar'", ProgressBar.class);
    target.tvHomeScholarship = Utils.findRequiredViewAsType(source, R.id.tvHomeSchol, "field 'tvHomeScholarship'", TextView.class);
    target.tvHomeDuration = Utils.findRequiredViewAsType(source, R.id.tvHomeDuration, "field 'tvHomeDuration'", TextView.class);
    target.tvHomeScholStatus = Utils.findRequiredViewAsType(source, R.id.tvHomeScholStatus, "field 'tvHomeScholStatus'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    HomeActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.homeProgressBar = null;
    target.tvHomeScholarship = null;
    target.tvHomeDuration = null;
    target.tvHomeScholStatus = null;
  }
}
