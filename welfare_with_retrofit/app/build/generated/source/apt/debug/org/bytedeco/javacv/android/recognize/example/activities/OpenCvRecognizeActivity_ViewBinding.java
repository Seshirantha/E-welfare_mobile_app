// Generated code from Butter Knife. Do not modify!
package org.bytedeco.javacv.android.recognize.example.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ProgressBar;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import org.bytedeco.javacv.android.recognize.example.R;

public class OpenCvRecognizeActivity_ViewBinding implements Unbinder {
  private OpenCvRecognizeActivity target;

  @UiThread
  public OpenCvRecognizeActivity_ViewBinding(OpenCvRecognizeActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public OpenCvRecognizeActivity_ViewBinding(OpenCvRecognizeActivity target, View source) {
    this.target = target;

    target.CVProgressBar = Utils.findRequiredViewAsType(source, R.id.cvProgressBar, "field 'CVProgressBar'", ProgressBar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    OpenCvRecognizeActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.CVProgressBar = null;
  }
}
