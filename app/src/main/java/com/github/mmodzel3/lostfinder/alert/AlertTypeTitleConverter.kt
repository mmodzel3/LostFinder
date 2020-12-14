package com.github.mmodzel3.lostfinder.alert

import android.content.Context
import com.github.mmodzel3.lostfinder.R

object AlertTypeTitleConverter {
    private val alertTypeList: List<AlertType> = listOf(AlertType.HELP, AlertType.ANIMAL, AlertType.FOUND_SOMETHING,
                                        AlertType.FOUND_LOST, AlertType.FOUND_WITNESS, AlertType.LOST,
                                        AlertType.SEARCH, AlertType.GATHER)

    fun convertAlertTypeToTitle(context: Context, alertType: AlertType): String {
        val titleArray: Array<out String> = context.resources.getStringArray(R.array.activity_alert_add_predefined_owner)
        val titleIdx: Int = alertTypeList.indexOf(alertType)

        return titleArray[titleIdx]
    }

    fun convertTitleToAlertType(context: Context, alertTitle: String): AlertType {
        val titleArray: Array<out String> = context.resources.getStringArray(R.array.activity_alert_add_predefined_owner)
        val alertTypeIdx = titleArray.indexOf(alertTitle)

        return alertTypeList[alertTypeIdx]
    }

    fun getAlertTypeFromTitleId(alertTitleId: Int) : AlertType {
        return alertTypeList[alertTitleId]
    }
}