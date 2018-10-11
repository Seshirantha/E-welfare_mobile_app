// Generated code from Butter Knife. Do not modify!
package org.bytedeco.javacv.android.recognize.example.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import org.bytedeco.javacv.android.recognize.example.R;

public class RegisterActivity_ViewBinding implements Unbinder {
  private RegisterActivity target;

  private View view2131230767;

  private View view2131230764;

  @UiThread
  public RegisterActivity_ViewBinding(RegisterActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public RegisterActivity_ViewBinding(final RegisterActivity target, View source) {
    this.target = target;

    View view;
    target.etStudentNo = Utils.findRequiredViewAsType(source, R.id.etStudentNo, "field 'etStudentNo'", EditText.class);
    target.etEmail = Utils.findRequiredViewAsType(source, R.id.etEmail, "field 'etEmail'", EditText.class);
    target.etPassword = Utils.findRequiredViewAsType(source, R.id.etPassword, "field 'etPassword'", EditText.class);
    target.etConfirmPassword = Utils.findRequiredViewAsType(source, R.id.etConfirmPassword, "field 'etConfirmPassword'", EditText.class);
    target.inputLayoutStudentNo = Utils.findRequiredViewAsType(source, R.id.inputLayoutStudentNo, "field 'inputLayoutStudentNo'", TextInputLayout.class);
    target.inputLayoutEmail = Utils.findRequiredViewAsType(source, R.id.inputLayoutEmail, "field 'inputLayoutEmail'", TextInputLayout.class);
    target.inputLayoutPassword = Utils.findRequiredViewAsType(source, R.id.inputLayoutPassword, "field 'inputLayoutPassword'", TextInputLayout.class);
    target.inputLayoutConfirmPassword = Utils.findRequiredViewAsType(source, R.id.inputLayoutConfirmPassword, "field 'inputLayoutConfirmPassword'", TextInputLayout.class);
    view = Utils.findRequiredView(source, R.id.btnDoRegister, "method 'register'");
    view2131230767 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.register();
      }
    });
    view = Utils.findRequiredView(source, R.id.btnDoCancel, "method 'cancel'");
    view2131230764 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.cancel();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    RegisterActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.etStudentNo = null;
    target.etEmail = null;
    target.etPassword = null;
    target.etConfirmPassword = null;
    target.inputLayoutStudentNo = null;
    target.inputLayoutEmail = null;
    target.inputLayoutPassword = null;
    target.inputLayoutConfirmPassword = null;

    view2131230767.setOnClickListener(null);
    view2131230767 = null;
    view2131230764.setOnClickListener(null);
    view2131230764 = null;
  }
}
