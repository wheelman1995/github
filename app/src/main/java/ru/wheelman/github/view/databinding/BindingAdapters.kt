package ru.wheelman.github.view.databinding

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.InverseBindingMethod
import androidx.databinding.InverseBindingMethods
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import ru.wheelman.github.di.scopes.AppScope
import ru.wheelman.github.view.utils.ImageLoader
import javax.inject.Inject

@AppScope
@InverseBindingMethods(
    InverseBindingMethod(
        type = SwipeRefreshLayout::class,
        attribute = "refreshing",
        event = "refreshingAttrChanged",
        method = "isRefreshing"
    )
)
class BindingAdapters @Inject constructor(private val imageLoader: ImageLoader<AppCompatImageView>) {

    @BindingAdapter("imageUrl")
    fun loadImage(imageView: AppCompatImageView, url: String?) {
        url?.let {
            imageLoader.loadImage(url, imageView)
        }
    }

    @BindingAdapter("refreshing")
    fun setRefreshing(srl: SwipeRefreshLayout, oldValue: Boolean, newValue: Boolean) {
        if (oldValue != newValue) srl.isRefreshing = newValue
    }

    @BindingAdapter("onRefreshListener", "refreshingAttrChanged", requireAll = false)
    fun setOnRefreshListener(
        srl: SwipeRefreshLayout,
        orl: OnRefreshListener?,
        ibl: InverseBindingListener?
    ) {
        if (orl == ibl && orl == null) return
        srl.setOnRefreshListener {
            orl?.onRefresh()
            ibl?.onChange()
        }
    }
}