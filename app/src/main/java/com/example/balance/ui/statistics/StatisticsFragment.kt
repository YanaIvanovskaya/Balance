package com.example.balance.ui.statistics

import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.balance.BalanceApp
import com.example.balance.Case
import com.example.balance.R
import com.example.balance.databinding.FragmentStatisticsBinding
import com.example.balance.getMonthName
import com.example.balance.presentation.StatisticsState
import com.example.balance.presentation.StatisticsViewModel
import com.example.balance.presentation.getViewModel
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.abs
import kotlin.math.ceil


class CustomXAxisRenderer(viewPortHandler: ViewPortHandler?, xAxis: XAxis?, trans: Transformer?) :
    XAxisRenderer(viewPortHandler, xAxis, trans) {
    override fun drawLabel(
        c: Canvas?,
        formattedLabel: String,
        x: Float,
        y: Float,
        anchor: MPPointF?,
        angleDegrees: Float
    ) {
        val line = formattedLabel.split("\n").toTypedArray()
        Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, 0f)
        Utils.drawXAxisValue(
            c,
            line[1],
            x,
            y + mAxisLabelPaint.textSize,
            mAxisLabelPaint,
            anchor,
            0f
        )
    }
}


class XAxisFormatter(
    private val chart: BarChart
) : ValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val currentEntry = chart.data.dataSets[0].getEntriesForXValue(value)[0]
        val monthNumber = currentEntry.data as Int
        val axisLabel = getMonthName(monthNumber, Case.SHORT).uppercase() + "\n2021"
//        if (monthNumber == 6 || monthNumber == 12) {"${axisLabel} "}
        return axisLabel
    }

}

class YAxisFormatter : ValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return when (abs(value)) {
            in 0f..999f -> {
                if (value.mod(10f) == 0.0f) {
                    "${value.toInt()}"
                } else ""
            }
            else -> {
                if (value.mod(1000f) == 0.0f) {
                    "${(value / 1000).toInt()}K"
                } else if (value.mod(100f) == 0.0f) {
                    "${value / 1000}K"
                } else ""
            }
        }
    }

}

class BarValueFormatter : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return when (abs(value)) {
            in 0f..999f ->
                "${value.toInt()}"
            else -> {
                if (value.mod(1000f) == 0.0f) {
                    "${(value / 1000).toInt()}K"
                } else if (value.mod(100f) == 0.0f) {
                    "${value / 1000}K"
                } else "else"
            }
        }
    }

}

class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private var mBinding: FragmentStatisticsBinding? = null
    private lateinit var mNavController: NavController
    private lateinit var mGeneralStatisticsFragment: GeneralStatisticsFragment
    private lateinit var mProfitsStatisticsFragment: ProfitsStatisticsFragment
    private lateinit var mCostsStatisticsFragment: CostsStatisticsFragment
    private val mViewModel by getViewModel {
        StatisticsViewModel(
            recordRepository = BalanceApp.recordRepository,
            categoryRepository = BalanceApp.categoryRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        mBinding = binding
        mNavController = findNavController()
        mGeneralStatisticsFragment = GeneralStatisticsFragment()
        mProfitsStatisticsFragment = ProfitsStatisticsFragment()
        mCostsStatisticsFragment = CostsStatisticsFragment()
        mViewModel.init()
        binding.chipGeneral.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                childFragmentManager.beginTransaction()
                    .replace(binding.frame.id, mGeneralStatisticsFragment)
                    .show(mGeneralStatisticsFragment)
                    .commit()
            }
        }

        binding.chipCosts.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                childFragmentManager.beginTransaction()
                    .replace(binding.frame.id, mCostsStatisticsFragment)
                    .show(mCostsStatisticsFragment)
                    .commit()
                mCostsStatisticsFragment.setData(mViewModel.getCostsStatItems())
            }
        }

        binding.chipProfit.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                childFragmentManager.beginTransaction()
                    .replace(binding.frame.id, mProfitsStatisticsFragment)
                    .show(mProfitsStatisticsFragment)
                    .commit()
                mProfitsStatisticsFragment.setData(mViewModel.getProfitStatItems())
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.state.observe(viewLifecycleOwner, ::render)
        mBinding?.frame?.let {
            childFragmentManager.beginTransaction()
                .add(it.id, mGeneralStatisticsFragment)
                .show(mGeneralStatisticsFragment)
                .commit()
        }

    }

    private fun render(state: StatisticsState) {
        when (state.hasNoRecords) {
            true -> {
                mBinding?.frameNoStatistics?.visibility = View.VISIBLE
                mBinding?.chipGroup?.visibility = View.GONE
                mBinding?.frame?.visibility = View.GONE
            }
            false -> {
                mBinding?.frameNoStatistics?.visibility = View.GONE
                mBinding?.chipGroup?.visibility = View.VISIBLE
                mBinding?.frame?.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }


}

class CustomBarChartRender(
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
        var rx = rx
        var ry = ry
        val top = rect.top
        val left = rect.left
        val right = rect.right
        val bottom = rect.bottom
        val path = Path()
        if (rx < 0) rx = 0f
        if (ry < 0) ry = 0f
        val width = right - left
        val height = bottom - top
        if (rx > width / 2) rx = width / 2
        if (ry > height / 2) ry = height / 2
        val widthMinusCorners = width - 2 * rx
        val heightMinusCorners = height - 2 * ry
        path.moveTo(right, top + ry)

        path.rQuadTo(0f, -ry, -rx, -ry)

        path.rLineTo(-widthMinusCorners, 0f)
        path.rQuadTo(-rx, 0f, -rx, ry)

        path.rLineTo(0f, heightMinusCorners)
        path.rQuadTo(0f, ry, rx, ry)

        path.rLineTo(widthMinusCorners, 0f)
        path.rQuadTo(rx, 0f, rx, -ry)

        path.rLineTo(0f, -heightMinusCorners)
        path.close()
        return path
    }
}



