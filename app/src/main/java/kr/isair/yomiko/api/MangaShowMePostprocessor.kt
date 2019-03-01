package kr.isair.yomiko.api

import com.facebook.common.references.CloseableReference
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory
import com.facebook.imagepipeline.request.BasePostprocessor
import com.facebook.cache.common.SimpleCacheKey
import com.facebook.cache.common.CacheKey




class MangaShowMePostprocessor(private val chapter: Int, private val token: Int) : BasePostprocessor() {

    private var seed = 0.0
    private val onePage = false

    override fun process(sourceBitmap: Bitmap, bitmapFactory: PlatformBitmapFactory): CloseableReference<Bitmap>? {
        val w = sourceBitmap.width
        val h = sourceBitmap.height
        val nw = w // if (onePage) w / 2 else w
        val nh = h

        val bitmapRef = bitmapFactory.createBitmap(w, h)

        try {
            val destBitmap = bitmapRef.get()
            val canvas = Canvas(destBitmap)

            if (0 == token) {
                // var s = onePage ? -w / 2 : 0;
                // o.drawImage(n, 0, 0, w, h, s, 0, r, i)
                canvas.drawBitmap(
                    sourceBitmap,
                    null,
                    Rect(0, 0,  nw, nh),
                    null
                )
            } else {
                var cx = 5
                var cy = 5
                if (token / 10 > 3e4) {cx = 1; cy = 6}
                else if (token / 10 > 2e4) cx = 1
                else if (token / 10 > 1e4) cx = 1

                seed = token.toDouble() / 10
                var d : ArrayList<Pair<Double, Int>> = ArrayList()
                for (m in 0..(cx * cy - 1)) {
                    d.add(Pair(random(), m))
                }
                d.sortWith(compareBy({it.first}, {it.second}))
                val dx = w / cx
                val dy = h / cy
                for ((i, p) in d.withIndex()) {
                    val x = i % cx * dx
                    val y = i / cx * dy
                    val f = p.second % cx * dx
                    val g = p.second / cx * dy
                    // s = onePage ? -r / 2 : 0;
                    // o.drawImage(n,
                    // h * u, _ * g, u, g,
                    // f * u + s, v * g, u, g)
                    canvas.drawBitmap(
                        sourceBitmap,
                        Rect(x, y, x + dx, y+ dy),
                        Rect(f, g, f + dx, g + dy),
                        null
                    )

                }
            }

            return CloseableReference.cloneOrNull(bitmapRef)
        } finally {
            CloseableReference.closeSafely(bitmapRef)
        }
    }

    override fun getPostprocessorCacheKey(): CacheKey? {
        return SimpleCacheKey(token.toString())
    }

    fun random() : Double{
        if (chapter < 554714) {
            var e = 1e4 * Math.sin(seed)
            seed+=1
            return Math.floor(1e5 * (e - Math.floor(e)))
        }
        seed++
        var t = 100 * Math.sin(10 * seed)
        var n = 1e3 * Math.cos(13 * seed)
        var a = 1e4 * Math.tan(14 * seed)
        t = Math.floor(100 * (t - Math.floor(t)))
        n = Math.floor(1e3 * (n - Math.floor(n)))
        a = Math.floor(1e4 * (a - Math.floor(a)))
        return t + n + a
    }
}