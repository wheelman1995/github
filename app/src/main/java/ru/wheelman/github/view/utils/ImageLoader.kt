package ru.wheelman.github.view.utils

interface ImageLoader<T> {

    fun loadImage(url: String, container: T)
}