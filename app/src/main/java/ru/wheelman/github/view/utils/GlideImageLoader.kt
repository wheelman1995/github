package ru.wheelman.github.view.utils

import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import ru.wheelman.github.R
import ru.wheelman.github.di.scopes.AppScope
import javax.inject.Inject

@AppScope
class GlideImageLoader @Inject constructor() : ImageLoader<AppCompatImageView> {

    override fun loadImage(url: String, container: AppCompatImageView) {
        Glide.with(container)
            .load(url)
            .circleCrop()
            .placeholder(R.drawable.avatar_placeholder)
            .into(container)
    }
}