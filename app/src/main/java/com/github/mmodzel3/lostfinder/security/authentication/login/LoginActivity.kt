package com.github.mmodzel3.lostfinder.security.authentication.login.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import com.github.mmodzel3.lostfinder.MainActivity
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.authenticator.Authenticator
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginEndpointAccessErrorException
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginInvalidCredentialsException
import com.github.mmodzel3.lostfinder.security.encryption.Encryptor
import com.github.mmodzel3.lostfinder.security.encryption.EncryptorInterface
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val accountManager: AccountManager by lazy { AccountManager.get(applicationContext) }
    private val accountType
        get() = applicationContext.resources.getString(R.string.account_type)
    private val tokenType
        get() = applicationContext.resources.getString(R.string.token_type)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        initLoginButton()
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
        val activity = this

        disableLogin()
        lifecycleScope.launch {
            try {
                loginUsingAccountManager(emailAddress, password, savePassword)
            } catch (e: LoginEndpointAccessErrorException) {
                Toast.makeText(activity, R.string.err_login_access, Toast.LENGTH_LONG).show()
                enableLogin()
            } catch (e: LoginInvalidCredentialsException) {
                Toast.makeText(activity, R.string.err_login_invalid_credentials, Toast.LENGTH_LONG).show()
                enableLogin()
            }
        }
    }

    private fun loginUsingAccountManager(emailAddress: String, password: String, savePassword: Boolean) {
        removeAllAccounts()
        val account: Account = addAccount(emailAddress, password)

        accountManager.getAuthToken(account, tokenType, null, true, AccountManagerCallback {
            if (it.result.getString(AccountManager.KEY_AUTHTOKEN) != null) {
                removePasswordIfNeeded(!savePassword)
                goToMainActivity()
            } else {
                val intent: Intent? = it.result.getParcelable<Intent>(AccountManager.KEY_INTENT)
                val error: String? = intent?.getStringExtra(Authenticator.AUTHENTICATOR_INFO)

                showAuthError(error)
                enableLogin()
            }
        }, null)
    }

    private fun removeAllAccounts() {
        accountManager.getAccountsByType(accountType).forEach { removeAccount(it) }
    }

    private fun removeAccount(account: Account) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            accountManager.removeAccount(account, {}, null)
        } else {
            accountManager.removeAccountExplicitly(account)
        }
    }

    private fun addAccount(emailAddress: String, password: String) : Account {
        val encodedPassword: String = encryptPassword(password)
        val account = Account(emailAddress, accountType)
        accountManager.addAccountExplicitly(account, encodedPassword, null)

        return account
    }

    private fun encryptPassword(password: String) : String {
        val encryptor: EncryptorInterface = Encryptor.getInstance()
        return encryptor.encrypt(password, applicationContext)
    }

    private fun showAuthError(error: String?) {
        if (error == Authenticator.INVALID_CREDENTIALS) {
            Toast.makeText(this, R.string.err_login_invalid_credentials, Toast.LENGTH_LONG).show()
        } else if (error == Authenticator.LOGIN_ENDPOINT_ACCESS_ERROR) {
            Toast.makeText(this, R.string.err_login_access, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, R.string.err_login, Toast.LENGTH_LONG).show()
        }
    }

    private fun removePasswordIfNeeded(removePassword: Boolean) {
        if (removePassword) {
            val account: Account = accountManager.getAccountsByType(accountType)[0]
            accountManager.setPassword(account, null)
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
        finish()
    }
}