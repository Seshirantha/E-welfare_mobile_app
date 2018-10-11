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

public class LoginActivity_ViewBinding implements Unbinder {
  private LoginActivity target;

  private View view2131230765;

  private View view2131230766;

  @UiThread
  public LoginActivity_ViewBinding(LoginActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public LoginActivity_ViewBinding(final LoginActivity target, View source) {
    this.target = target;

    View view;
    target.etLogInStudentNo = Utils.findRequiredViewAsType(source, R.id.etLogInStudentNo, "field 'etLogInStudentNo'", EditText.class);
    target.etLoginPassword = Utils.findRequiredViewAsType(source, R.id.etLogInPassword, "field 'etLoginPassword'", EditText.class);
    target.textInputLayoutLogInStudentNo = Utils.findRequiredViewAsType(source, R.id.inputLayoutLogInStudentNo, "field 'textInputLayoutLogInStudentNo'", TextInputLayout.class);
    target.textInputLayoutLogInPasswrod = Utils.findRequiredViewAsType(source, R.id.inputLayoutLogInPassword, "field 'textInputLayoutLogInPasswrod'", TextInputLayout.class);
    target.loginProgressBar = Utils.findRequiredViewAsType(source, R.id.loginProgressBar, "field 'loginProgressBar'", ProgressBar.class);
    view = Utils.findRequiredView(source, R.id.btnDoLogIn, "method 'login'");
    view2131230765 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.login();
      }
    });
    view = Utils.findRequiredView(source, R.id.btnDoLogInCancel, "method 'doLoginCancel'");
    view2131230766 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.doLoginCancel();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    LoginActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.etLogInStudentNo = null;
    target.etLoginPassword = null;
    target.textInputLayoutLogInStudentNo = null;
    target.textInputLayoutLogInPasswrod = null;
    target.loginProgressBar = null;

    view2131230765.setOnClickListener(null);
    view2131230765 = null;
    view2131230766.setOnClickListener(null);
    view2131230766 = null;
  }
}
