package com.example.balance.ui.statistics

import android.graphics.*
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.ceil

class RoundedBarChartRenderer(
    chart: BarDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) :
    BarChartRenderer(chart, animator, viewPortHandler) {

    private val mBarShadowRectBuffer = RectF()
    private var mRadius = 0

    fun setRadius(mRadius: Int) {
        this.mRadius = mRadius
    }

    override fun drawHighlighted(c: Canvas?, indices: Array<out Highlight>?) {
        val barData = mChart.barData

        for (high in indices!!) {
            val set = barData.getDataSetByIndex(high.dataSetIndex)
            if (set == null || !set.isHighlightEnabled) continue
            val e = set.getEntryForXValue(high.x, high.y)
            if (!isInBoundsX(e, set)) continue
            val trans = mChart.getTransformer(set.axisDependency)
            mHighlightPaint.color = set.highLightColor
            mHighlightPaint.alpha = set.highLightAlpha
            val isStack = high.stackIndex >= 0 && e.isStacked
            val y1: Float
            val y2: Float
            if (isStack) {
                if (mChart.isHighlightFullBarEnabled) {
                    y1 = e.positiveSum
                    y2 = -e.negativeSum
                } else {
                    val range = e.ranges[high.stackIndex]
                    y1 = range.from
                    y2 = range.to
                }
            } else {
                y1 = e.y
                y2 = 0f
            }
            prepareBarHighlight(e.x, y1, y2, barData.barWidth / 2f, trans)
            setHighlightDrawPos(high, mBarRect)

            if (mRadius > 0)
                c?.drawRoundRect(mBarRect, mRadius.toFloat(), mRadius.toFloat(), mHighlightPaint)
            else
                c?.drawRect(mBarRect, mHighlightPaint)
        }
    }

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)
        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)
        mShadowPaint.color = dataSet.barShadowColor
        val drawBorder = dataSet.barBorderWidth > 0f
        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY
        if (mChart.isDrawBarShadowEnabled) {
            mShadowPaint.color = dataSet.barShadowColor
            val barData = mChart.barData
            val barWidth = barData.barWidth
            val barWidthHalf = barWidth / 2.0f
            var x: Float
            var i = 0
            val count = ceil(
                (dataSet.entryCount
                    .toFloat() * phaseX).toDouble()
            ).coerceAtMost(dataSet.entryCount.toDouble())
            while (i < count) {
                val e = dataSet.getEntryForIndex(i)
                x = e.x
                mBarShadowRectBuffer.left = x - barWidthHalf
                mBarShadowRectBuffer.right = x + barWidthHalf
                trans.rectValueToPixel(mBarShadowRectBuffer)
                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                    i++
                    continue
                }
                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left)) break
                mBarShadowRectBuffer.top = mViewPortHandler.contentTop()
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom()
                c.drawRoundRect(mBarRect, mRadius.toFloat(), mRadius.toFloat(), mShadowPaint)
                i++
            }
        }

        // initialize the buffer
        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))

        buffer.setBarWidth(mChart.barData.barWidth)

        buffer.feed(dataSet)
        trans.pointValuesToPixel(buffer.buffer)
        val isSingleColor = dataSet.colors.size == 1
        if (isSingleColor) {
            mRenderPaint.color = dataSet.color
        }
        var j = 0
        while (j < buffer.size()) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                j += 4
                continue
            }
            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break
            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.color = dataSet.getColor(j / 4)
            }
            if (dataSet.gradientColor != null) {
                val gradientColor = dataSet.gradientColor
                mRenderPaint.shader = LinearGradient(
                    buffer.buffer[j],
                    buffer.buffer[j + 3],
                    buffer.buffer[j],
                    buffer.buffer[j + 1],
                    gradientColor.startColor,
                    gradientColor.endColor,
                    Shader.TileMode.MIRROR
                )
            }
            if (dataSet.gradientColors != null) {
                mRenderPaint.shader = LinearGradient(
                    buffer.buffer[j],
                    buffer.buffer[j + 3],
                    buffer.buffer[j],
                    buffer.buffer[j + 1],
                    dataSet.getGradientColor(j / 4).startColor,
                    dataSet.getGradientColor(j / 4).endColor,
                    Shader.TileMode.MIRROR
                )
            }
            val path2: Path = roundRect(
                RectF(
                    buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3]
                ), mRadius.toFloat(), mRadius.toFloat()
            )
            c.drawPath(path2, mRenderPaint)
            if (drawBorder) {
                val path: Path = roundRect(
                    RectF(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3]
                    ),
                    mRadius.toFloat(),
                    mRadius.toFloat()
                )
                c.drawPath(path, mBarBorderPaint)
            }
            j += 4
        }
    }

    private fun roundRect(
        rect: RectF,
        rx: Float,
        ry: Float
    ): Path {
        var rX = rx
        var rY = ry
        val top = rect.top
        val left = rect.left
        val right = rect.right
        val bottom = rect.bottom
        val path = Path()
        if (rX < 0) rX = 0f
        if (rY < 0) rY = 0f
        val width = right - left
        val height = bottom - top
        if (rX > width / 2) rX = width / 2
        if (rY > height / 2) rY = height / 2
        val widthMinusCorners = width - 2 * rX
        val heightMinusCorners = height - 2 * rY
        path.moveTo(right, top + rY)

        path.rQuadTo(0f, -rY, -rX, -rY)

        path.rLineTo(-widthMinusCorners, 0f)
        path.rQuadTo(-rX, 0f, -rX, rY)

        path.rLineTo(0f, heightMinusCorners)
        path.rQuadTo(0f, rY, rX, rY)

        path.rLineTo(widthMinusCorners, 0f)
        path.rQuadTo(rX, 0f, rX, -rY)

        path.rLineTo(0f, -heightMinusCorners)
        path.close()
        return path
    }

}