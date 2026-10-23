package com.unofficial.pokebonk

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * Optionally keeps the game WebView inside a centered aspect-ratio viewport.
 * A ratio of 0 fills the available screen.
 */
class AspectRatioFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var targetAspectRatio = 0f

    fun setTargetAspectRatio(aspectRatio: Float) {
        targetAspectRatio = aspectRatio
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        if (availableWidth == 0 || availableHeight == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        val viewportWidth: Int
        val viewportHeight: Int
        if (targetAspectRatio <= 0f) {
            viewportWidth = availableWidth
            viewportHeight = availableHeight
        } else if (availableWidth.toFloat() / availableHeight > targetAspectRatio) {
            viewportHeight = availableHeight
            viewportWidth = (availableHeight * targetAspectRatio).toInt()
        } else {
            viewportWidth = availableWidth
            viewportHeight = (availableWidth / targetAspectRatio).toInt()
        }

        // Keep the outer container full-screen so letterbox space remains black.
        setMeasuredDimension(
            resolveSize(availableWidth, widthMeasureSpec),
            resolveSize(availableHeight, heightMeasureSpec)
        )

        val childWidthSpec = MeasureSpec.makeMeasureSpec(viewportWidth, MeasureSpec.EXACTLY)
        val childHeightSpec = MeasureSpec.makeMeasureSpec(viewportHeight, MeasureSpec.EXACTLY)
        for (index in 0 until childCount) {
            getChildAt(index).measure(childWidthSpec, childHeightSpec)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val child = getChildAt(0) ?: return
        val childLeft = (width - child.measuredWidth) / 2
        val childTop = (height - child.measuredHeight) / 2
        child.layout(
            childLeft,
            childTop,
            childLeft + child.measuredWidth,
            childTop + child.measuredHeight
        )
    }
}
