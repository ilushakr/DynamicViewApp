package com.example.appxml

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.ImageView
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.example.appxml.fragments.HomeFragment
import com.example.appxml.listeners.ThemeModeTogglerListener
import com.example.design.provider.api.BarsHeightsHolder
import com.example.design.provider.api.PaletteMode
import com.example.design.provider.api.ResourceProvider
import com.example.design.ui.xml.extensions.navigationBarHeight
import com.example.design.ui.xml.extensions.statusBarHeight
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module


class MainActivity : AppCompatActivity(), ThemeModeTogglerListener, KoinComponent {

    private val resourceProvider: ResourceProvider by inject()

    private val themeSwiperIv by lazy {
        findViewById<ImageView>(R.id.theme_swiper_iv)
    }

    private val containerFl by lazy {
        findViewById<View>(R.id.container_fcv)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge(navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT))

        loadKoinModules(
            module {
                single<ThemeModeTogglerListener> { this@MainActivity }
            }
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setLightStatusBar()

        if (savedInstanceState == null) {
            containerFl.post {
                resourceProvider.barsHeightsHolder = BarsHeightsHolder(
                    statusBarHeight = statusBarHeight(),
                    navigationBarHeight = navigationBarHeight()
                )
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container_fcv, HomeFragment(), HomeFragment.TAG)

                fragmentTransaction.commit()

            }
        }

    }

    private fun setLightStatusBar() {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = false
    }

    override fun toggleThemeMode(initiatorView: View) {
        if (themeSwiperIv.isVisible) {
            return
        }

        val w = containerFl.measuredWidth
        val h = containerFl.measuredHeight

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        containerFl.draw(canvas)

        themeSwiperIv.setImageBitmap(bitmap)
        themeSwiperIv.isVisible = true

        val finalRadius = kotlin.math.hypot(w.toFloat(), h.toFloat())

        toggleProviderThemeMode()

        val arr = IntArray(2).apply(initiatorView::getLocationOnScreen)

        val centreX = arr[0] + initiatorView.width / 2
        val centreY = arr[1] + initiatorView.height / 2

        val anim = ViewAnimationUtils.createCircularReveal(
            containerFl,
            centreX,
            centreY,
            0f,
            finalRadius
        )
        anim.duration = 500L
        anim.doOnEnd {
            themeSwiperIv.setImageDrawable(null)
            themeSwiperIv.isVisible = false
        }
        anim.start()
    }

    private fun toggleProviderThemeMode() {
        resourceProvider.currentPalette?.let {
            resourceProvider.currentPalette = it.copy(
                mode = when (it.mode) {
                    PaletteMode.Light -> PaletteMode.Dark
                    PaletteMode.Dark -> PaletteMode.Light
                }
            )
        }
    }

}