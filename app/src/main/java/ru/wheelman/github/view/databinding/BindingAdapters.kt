package ru.wheelman.github.view.databinding

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import ru.wheelman.github.di.scopes.AppScope
import ru.wheelman.github.view.utils.ImageLoader
import javax.inject.Inject

@AppScope
class BindingAdapters @Inject constructor(private val imageLoader: ImageLoader<AppCompatImageView>) {

    @BindingAdapter("imageUrl")
    fun loadImage(imageView: AppCompatImageView, url: String) {
        imageLoader.loadImage(url, imageView)
    }

}