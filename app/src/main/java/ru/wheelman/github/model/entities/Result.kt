package ru.wheelman.github.model.entities

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

data class Result(val errors: LiveData<String>, val livePagedList: LiveData<PagedList<User>>)