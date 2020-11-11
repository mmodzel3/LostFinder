package com.github.mmodzel3.lostfinder.security.authentication.login.activity

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
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginAccountManagerActivityAbstract
import kotlinx.coroutines.launch

class LoginActivity : LoginAccountManagerActivityAbstract() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        initLoginButton()
        setAccountEmailAddressEditTextFromAccount()
        enableLogin()
    }

    private fun initLoginButton() {
        val loginButton: Button = findViewById(R.id.activity_login_bt_login)
        loginButton.setOnClickListener { onLoginClick() }
    }

    private fun onLoginClick() {
        val emailAddress: EditText = findViewById(R.id.activity_login_et_email_address)
        val password: EditText = findViewById(R.id.activity_login_et_password)
        val savePassword: SwitchCompat = findViewById(R.id.activity_login_sw_save_password)

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

    private fun login(emailAddress: String, password: String, savePassword: Boolean) {
        disableLogin()

        lifecycleScope.launch {
            loginUsingAccountManager(emailAddress, password,
                        createLoginAccountManagerCallback(!savePassword))
        }
    }

    private fun createLoginAccountManagerCallback(removePassword: Boolean) : AccountManagerCallback<Bundle> {
        return AccountManagerCallback {
            removePasswordIfNeeded(removePassword)

            if (it.result.getString(AccountManager.KEY_AUTHTOKEN) != null) {
                onLoginSuccess()
            } else {
                val intent: Intent? = it.result.getParcelable<Intent>(AccountManager.KEY_INTENT)
                val error: String? = intent?.getStringExtra(Authenticator.AUTHENTICATOR_INFO)

                onLoginFailure(error)
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

    private fun onLoginFailure(error: String?) {
        showLoginError(error)
        enableLogin()
    }

    private fun showLoginError(error: String?) {
        when (error) {
            Authenticator.INVALID_CREDENTIALS -> {
                Toast.makeText(this, R.string.err_login_invalid_credentials, Toast.LENGTH_LONG).show()
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