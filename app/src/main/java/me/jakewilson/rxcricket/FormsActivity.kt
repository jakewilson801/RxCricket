package me.jakewilson.rxcricket

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.activity_forms.emailEditText
import kotlinx.android.synthetic.main.activity_forms.emailLayout
import kotlinx.android.synthetic.main.activity_forms.passwordEditText
import kotlinx.android.synthetic.main.activity_forms.passwordLayout
import kotlinx.android.synthetic.main.activity_forms.submitButton
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit.MILLISECONDS


class FormsActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_forms)
    setUpRxListeners()
  }

  fun setUpRxListeners() {
    val passwordObs = RxTextView.textChanges(
        passwordEditText).map { it.length > 3 && it.length < 20 }

    passwordObs.debounce(250, MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
        .skip(1).subscribe { passwordLayout.error = if (it) "" else "Password must be between 4 and less than 20 characters" }

    val emailObs = RxTextView.textChanges(emailEditText).map { it.contains("@") }

    emailObs.debounce(250, MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
        .skip(1).subscribe { emailLayout.error = if (it) "" else "You must provide a valid email address" }

    val validSignUpObs = Observable.combineLatest(passwordObs, emailObs, { t1, t2 -> t1 && t2 })

    validSignUpObs.subscribe { submitButton.isEnabled = it }

    submitButton.setOnClickListener { finish() }
  }
}
