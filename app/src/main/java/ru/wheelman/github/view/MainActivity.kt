package ru.wheelman.github.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import ru.wheelman.github.R
import ru.wheelman.github.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(
            Fabric.Builder(this)
                .debuggable(true)
                .kits(Crashlytics())
                .build()
        )
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(findNavController(R.id.nav_host_fragment))
    }
}
