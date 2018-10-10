// Generated code from Butter Knife. Do not modify!
package org.bytedeco.javacv.android.recognize.example.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import org.bytedeco.javacv.android.recognize.example.R;

public class VerifyActivity_ViewBinding implements Unbinder {
  private VerifyActivity target;

  private View view2131230767;

  private View view2131230768;

  @UiThread
  public VerifyActivity_ViewBinding(VerifyActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public VerifyActivity_ViewBinding(final VerifyActivity target, View source) {
    this.target = target;

    View view;
    target.verifyProgressBar = Utils.findRequiredViewAsType(source, R.id.verifyProgressBar, "field 'verifyProgressBar'", ProgressBar.class);
    target.etVerifyCode = Utils.findRequiredViewAsType(source, R.id.etVerifyCode, "field 'etVerifyCode'", EditText.class);
    target.inputLayoutVerifyCode = Utils.findRequiredViewAsType(source, R.id.inputLayoutVerifyCode, "field 'inputLayoutVerifyCode'", TextInputLayout.class);
    view = Utils.findRequiredView(source, R.id.btnDoVerify, "method 'doVerification'");
    view2131230767 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.doVerification();
      }
    });
    view = Utils.findRequiredView(source, R.id.btnDoVerifyCancel, "method 'doVerifyCancel'");
    view2131230768 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.doVerifyCancel();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    VerifyActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.verifyProgressBar = null;
    target.etVerifyCode = null;
    target.inputLayoutVerifyCode = null;

    view2131230767.setOnClickListener(null);
    view2131230767 = null;
    view2131230768.setOnClickListener(null);
    view2131230768 = null;
  }
}
