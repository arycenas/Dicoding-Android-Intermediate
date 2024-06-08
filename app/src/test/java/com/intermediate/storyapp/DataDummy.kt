package com.intermediate.storyapp

import com.intermediate.storyapp.data.pref.UserModel
import com.intermediate.storyapp.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "author + $i",
                "story $i",
                "pgotoUrl ${i}",
                "time ${i}",
                2.3443,
                2.4566
            )
            items.add(story)
        }
        return items
    }

    fun generateDummyUserResponse(): UserModel {
        val user = UserModel(
            "testing@gmail.com", "37623hdhjsfgsdgfhsgd", true
        )
        return user
    }
}