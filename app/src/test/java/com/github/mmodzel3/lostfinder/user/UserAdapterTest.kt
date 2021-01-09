package com.github.mmodzel3.lostfinder.user

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManagerStub
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*

class UserAdapterTest : UserRepositoryTestAbstract() {
    private lateinit var parentView: View
    private lateinit var userAdapter: UserAdapter

    @Before
    override fun setUp() {
        super.setUp()

        mockView()

        userAdapter = UserAdapter(TokenManagerStub.getInstance())

        userAdapter.users = users
    }

    @Test
    fun whenOnBindViewHolderOnAlertThenGotUpdatedData() {
        val holder = UserViewHolder(parentView)
        userAdapter.onBindViewHolder(holder, users.size-1)

        val user: User = users[users.size-1]

        assertThat(holder.userName).isEqualTo(user.username)
        assertThat(holder.lastLogin.toString()).isEqualTo(user.lastLoginDate.toString())
    }

    private fun mockView() {
        val context: Context = Mockito.mock(Context::class.java)
        val resources: Resources = Mockito.mock(Resources::class.java)
        parentView = Mockito.mock(View::class.java)

        `when`(parentView.context).thenReturn(context)
        `when`(context.resources).thenReturn(resources)
        `when`(context.getString(Mockito.anyInt())).thenReturn(UserRole.MANAGER.toString())
        `when`(parentView.findViewById<View>(Mockito.anyInt())).thenAnswer {
            if (it.arguments[0] != R.id.activity_user_info_bt_increase_permissions_role &&
                it.arguments[0] != R.id.activity_user_info_bt_decrease_permissions_role &&
                it.arguments[0] != R.id.activity_user_info_bt_block_account &&
                it.arguments[0] != R.id.activity_user_info_bt_unblock_account &&
                it.arguments[0] != R.id.activity_user_info_bt_delete_account) {
                return@thenAnswer mockTextView()
            } else {
                return@thenAnswer Mockito.mock(ImageButton::class.java)
            }
        }
    }

    private fun mockTextView(): TextView {
        val textView: TextView = Mockito.mock(TextView::class.java)
        var text = ""

        `when`(textView.text).then {
            return@then text
        }

        `when`(textView.setText(Mockito.anyString())).then {
            text = it.arguments[0] as String
            return@then Unit
        }

        return textView
    }
}