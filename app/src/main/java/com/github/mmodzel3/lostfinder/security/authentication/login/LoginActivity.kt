package com.github.mmodzel3.lostfinder.security.authentication.login

import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import com.github.mmodzel3.lostfinder.MainActivity
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.authenticator.Authenticator
import com.github.mmodzel3.lostfinder.security.authentication.register.RegisterActivity
import kotlinx.coroutines.launch

class LoginActivity : LoginAccountManagerActivityAbstract() {
    var loginIdlingResource: LoginIdlingResourceInterface = LoginIdlingResource()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        initLoginButton()
        initRegisterButton()
        setAccountEmailAddressEditTextFromAccount()
        enableLogin()
    }

    internal fun login(emailAddress: String, password: String, savePassword: Boolean) {
        disableLogin()

        lifecycleScope.launch {
            loginUsingAccountManager(emailAddress, password, savePassword,
                createLoginAccountManagerCallback())
        }
    }

    private fun initLoginButton() {
        val loginButton: Button = findViewById(R.id.activity_login_bt_login)
        loginButton.setOnClickListener { onLoginClick() }
    }

    private fun onLoginClick() {
        val emailAddress: EditText = findViewById(R.id.activity_login_et_email_address)
        val password: EditText = findViewById(R.id.activity_login_et_password)
        val savePassword: SwitchCompat = findViewById(R.id.activity_login_sw_save_password)

        loginIdlingResource.increment()
        login(emailAddress.text.toString(), password.text.toString(), savePassword.isChecked)
    }

    private fun enableLogin() {
        val loginButton: Button = findViewById(R.id.activity_login_bt_login)
        loginButton.isEnabled = true
    }

    private fun disableLogin() {
        val loginButton: Button = findViewById(R.id.activity_login_bt_login)
        loginButton.isEnabled = false
    }

    private fun initRegisterButton() {
        val registerButton: Button = findViewById(R.id.activity_login_bt_register)
        registerButton.setOnClickListener { onRegisterClick() }
        registerButton.isEnabled = true
    }

    private fun onRegisterClick() {
        goToRegisterActivity()
    }

    private fun createLoginAccountManagerCallback() : AccountManagerCallback<Bundle> {
        return AccountManagerCallback {
            if (it.result.getString(AccountManager.KEY_AUTHTOKEN) != null) {
                loginIdlingResource.decrement()
                onLoginSuccess()
            } else {
                val intent: Intent? = it.result.getParcelable<Intent>(AccountManager.KEY_INTENT)
                val error: String? = intent?.getStringExtra(Authenticator.AUTHENTICATOR_INFO)

                onLoginFailure(error)
                loginIdlingResource.decrement()
            }
        }
    }

    private fun onLoginSuccess() {
        goToMainActivity()
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
        finish()
    }

    private fun goToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
    }

    private fun onLoginFailure(error: String?) {
        showLoginError(error)
        enableLogin()
    }

    private fun showLoginError(error: String?) {
        when (error) {
            Authenticator.INVALID_CREDENTIALS -> {
                Toast.makeText(this, R.string.err_login_invalid_credentials, Toast.LENGTH_LONG).show()
            }
            Authenticator.ACCOUNT_BLOCKED -> {
                Toast.makeText(this, R.string.err_login_account_blocked, Toast.LENGTH_LONG).show()
            }
            Authenticator.LOGIN_ENDPOINT_ACCESS_ERROR -> {
                Toast.makeText(this, R.string.err_login_access, Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, R.string.err_login, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setAccountEmailAddressEditTextFromAccount() {
        if (isAccountPresent) {
            val emailAddress: String = account.name
            val emailAddressEditText: EditText = findViewById(R.id.activity_login_et_email_address)

            emailAddressEditText.setText(emailAddress)
        }
    }
}