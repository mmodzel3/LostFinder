package com.github.mmodzel3.lostfinder.user

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mmodzel3.lostfinder.LoggedUserActivityAbstract
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerEndpointStatus
import com.github.mmodzel3.lostfinder.server.ServerResponse
import kotlinx.coroutines.launch

class UserActivity : LoggedUserActivityAbstract() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter

    private val tokenManager: TokenManager by lazy {
        TokenManager.getInstance(applicationContext)
    }

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(tokenManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_user)

        recyclerView = findViewById(R.id.activity_user_rv_user_list)
        userAdapter = UserAdapter(tokenManager)

        initRecyclerView()
        listenToUserManagementEvents()
        observeUserUpdates()
    }

    override fun onResume() {
        super.onResume()

        userViewModel.runUpdates()
    }

    override fun onPause() {
        super.onPause()

        userViewModel.stopUpdates()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userAdapter
    }

    private fun listenToUserManagementEvents() {
        userAdapter.setUserManagementListener(object : UserManagementListener {
            override fun onIncreaseRoleClick(user: User) {
                onIncreaseRole(user)
            }

            override fun onDecreaseRoleClick(user: User) {
                onDecreaseRole(user)
            }

            override fun onBlockAccountClick(user: User) {
                onBlockAccount(user)
            }

            override fun onUnblockAccountClick(user: User) {
                onUnblockAccount(user)
            }

            override fun onDeleteAccountClick(user: User) {
                onDeleteAccount(user)
            }
        })
    }

    private suspend fun sendUpdateUserAccount(requestFunction: suspend () -> ServerResponse) {
        try {
            when (requestFunction()) {
                ServerResponse.NOT_FOUND -> {
                    Toast.makeText(this, R.string.activity_user_err_not_found, Toast.LENGTH_LONG)
                        .show()
                }
                ServerResponse.INVALID_PERMISSION -> {
                    Toast.makeText(this, R.string.activity_user_err_invalid_permission, Toast.LENGTH_LONG)
                        .show()
                    goToLoginActivity()
                }
                else -> {
                    Toast.makeText(this, R.string.activity_user_msg_success, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            userViewModel.forceUpdate()
        } catch (e : UserEndpointAccessErrorException) {
            Toast.makeText(this, R.string.activity_user_err_api_access_problem, Toast.LENGTH_LONG)
                .show()
        } catch (e : InvalidTokenException) {
            Toast.makeText(this, R.string.activity_user_err_invalid_token, Toast.LENGTH_SHORT)
                .show()
            goToLoginActivity()
        }
    }

    private fun onIncreaseRole(user: User) {
        lifecycleScope.launch {
            sendUpdateUserRole(user, UserRole.MANAGER)
        }
    }

    private suspend fun sendUpdateUserRole(user: User, role: UserRole) {
        sendUpdateUserAccount { userViewModel.updateUserRole(user.email, role) }
    }

    private fun onDecreaseRole(user: User) {
        lifecycleScope.launch {
            sendUpdateUserRole(user, UserRole.USER)
        }
    }

    private fun onBlockAccount(user: User) {
        lifecycleScope.launch {
            sendUpdateBlock(user, true)
        }
    }

    private suspend fun sendUpdateBlock(user: User, isBlocked: Boolean) {
        sendUpdateUserAccount { userViewModel.updateUserBlock(user.email, isBlocked) }
    }

    private fun onUnblockAccount(user: User) {
        lifecycleScope.launch {
            sendUpdateBlock(user, false)
        }
    }

    private fun onDeleteAccount(user: User) {
        lifecycleScope.launch {
            deleteUser(user)
        }
    }

    private suspend fun deleteUser(user: User) {
        sendUpdateUserAccount { userViewModel.deleteUser(user.email) }
    }

    private fun observeUserUpdates() {
        userViewModel.allUsers.observe(this, Observer {
            userAdapter.users = it.values.toMutableList()
            userAdapter.notifyDataSetChanged()
        })

        userViewModel.status.observe(this, Observer {
            when(it) {
                ServerEndpointStatus.ERROR -> Toast.makeText(this, R.string.activity_user_err_fetching_api_access_error,
                    Toast.LENGTH_LONG).show()
                ServerEndpointStatus.INVALID_TOKEN -> {
                    Toast.makeText(this, R.string.activity_user_err_fetching_invalid_token,
                        Toast.LENGTH_SHORT).show()
                    goToLoginActivity()
                }
                else -> {}
            }
        })
    }
}