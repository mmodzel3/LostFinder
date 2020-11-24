package com.github.mmodzel3.lostfinder

import android.os.Bundle
import com.github.mmodzel3.lostfinder.chat.ChatActivity
import com.github.mmodzel3.lostfinder.map.UsersLocationsMapActivity

class MainActivity : ChatActivity() {

}

/* class MainActivity: UsersLocationsMapActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        initMap()
    }

    override fun onDestroy() {
        super.onDestroy()
        deInitMap()
    }
}*/
