package ru.wheelman.github.view.databinding

import androidx.databinding.DataBindingComponent
import ru.wheelman.github.di.scopes.AppScope
import javax.inject.Inject

@AppScope
class DataBindingComponent @Inject constructor(private val bindingAdapters: BindingAdapters) :
    DataBindingComponent {

    override fun getBindingAdapters() = bindingAdapters
}