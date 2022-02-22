package com.myprog.sportislife.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.myprog.sportislife.R
import com.myprog.sportislife.data.AppDatabase
import com.myprog.sportislife.data.Training
import com.myprog.sportislife.other.Constants.ANIMATION_NEXT_WEEK
import com.myprog.sportislife.other.Constants.ANIMATION_PAST_WEEK
import com.myprog.sportislife.other.Constants.NO_ANIMATION
import com.myprog.sportislife.other.convertLongToDateString3
import com.myprog.sportislife.other.getDistanceInDayOfWeek
import com.myprog.sportislife.ui.customview.SunburstChartKT
import com.myprog.sportislife.ui.viewmodel.TrainingStatisticsViewModel
import com.myprog.sportislife.ui.viewmodel.TrainingStatisticsViewModelFactory
import kotlin.math.floor
import kotlin.math.roundToInt


// Рефактор класса, некоторую логику перенесни в viewmodel
// CustomLineChart перенести в отдельный класс, если это возможно
// Анимацию сделать по человечески
// График без чисел после запятой

class TrainingStatisticsFragment : Fragment(R.layout.fragment_training_statistics) {

    private lateinit var trainingStatisticsViewModel: TrainingStatisticsViewModel
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var textViewDate: TextView
    private lateinit var buttonBack: Button
    private lateinit var buttonForward: Button
    private lateinit var sunburstChartKT: SunburstChartKT
    private lateinit var textViewTime: TextView
    private lateinit var textViewCalories: TextView
    private lateinit var textViewSteps: TextView
    private lateinit var lineChart: LineChart
    private lateinit var textViewCountTraining: TextView
    private lateinit var textViewCountNoTraining: TextView
    //private val format: DateFormat = SimpleDateFormat("dd/MM/yyyy")


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        val dataSource = AppDatabase.getInstance(requireActivity().application).trainingDatabaseDao
        val viewModelFactory =
            TrainingStatisticsViewModelFactory(dataSource, requireActivity().application)
        trainingStatisticsViewModel =
            ViewModelProvider(this, viewModelFactory).get(TrainingStatisticsViewModel::class.java)

        textViewDate = view.findViewById(R.id.training_statistics_date)
        buttonBack = view.findViewById(R.id.training_statistics_past_week)
        buttonForward = view.findViewById(R.id.training_statistics_next_week)
        sunburstChartKT = view.findViewById(R.id.training_statistics_sunburst_chart)
        textViewTime = view.findViewById(R.id.training_statistics_time)
        textViewCalories = view.findViewById(R.id.training_statistics_calories)
        textViewSteps = view.findViewById(R.id.training_statistics_steps)
        lineChart = view.findViewById(R.id.training_statistics_line_chart)
        textViewCountTraining = view.findViewById(R.id.training_statistics_count_training)
        textViewCountNoTraining = view.findViewById(R.id.training_statistics_count_no_training)

        constraintLayout = view.findViewById(R.id.constraintLayout)


        trainingStatisticsViewModel.getDataNow().observe(viewLifecycleOwner, Observer{
            val firstDate = convertLongToDateString3(it[0])
            val secondDate = convertLongToDateString3(it[1])
            textViewDate.text = "$firstDate - $secondDate"
            trainingStatisticsViewModel.getListIntervalTraining(it)
        })

        trainingStatisticsViewModel.getListIntervalTraining().observe(viewLifecycleOwner, Observer {
            val listTrainingNotNull = it.filterNotNull()
            updateUI(listTrainingNotNull)
        })

        buttonForward.setOnClickListener {
            animate(true, ANIMATION_NEXT_WEEK)
        }

        buttonBack.setOnClickListener {
            animate(true, ANIMATION_PAST_WEEK)
        }

    }

    private fun updateUI(allTraining: List<Training>) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val maxValueTime = sharedPreferences.getString("preference_max_value_time", "120") as String
        val maxValueCalories = sharedPreferences.getString("preference_max_value_calories", "3000") as String
        val maxValueSteps = sharedPreferences.getString("preference_max_value_steps", "400") as String

        sunburstChartKT.setListTraining(allTraining)
        sunburstChartKT.setMaxValues(maxValueTime.toDouble(), maxValueCalories.toDouble(), maxValueSteps.toDouble())
        sunburstChartKT.start()

        var allTime = 0
        var allCalories = 0.0
        var allSteps = 0
        for (training in allTraining) {
            allTime += (training.totalTime / 60000).toInt()
            allCalories += training.calories
            allSteps += training.countStep
        }
        textViewTime.text = String.format(resources.getString(R.string.training_statistics_time),allTime)

        if(allCalories > 0.0) {
            val calories = String.format(resources.getString(R.string.training_statistics_calories), allCalories.toString().substring(0,4))
            textViewCalories.text  = calories
        } else {
            val calories = String.format(resources.getString(R.string.training_statistics_calories), allCalories.toString())
            textViewCalories.text = calories
        }

        textViewSteps.text = String.format(resources.getString(R.string.training_statistics_steps),allSteps)

        val distanceInDayOfWeek = getDistanceInDayOfWeek(allTraining)
        initLineChart(distanceInDayOfWeek)

        textViewCountTraining.text = allTraining.size.toString()
        var countNoTraining = 0
        for(day in distanceInDayOfWeek) {
            if(day == 0.0)
                countNoTraining++
        }
        textViewCountNoTraining.text = countNoTraining.toString()
    }


    private fun animate(animateOut: Boolean, flagInt: Int) {
        val centerX: Int = sunburstChartKT.pivotX.toInt()
        val centerY: Int = sunburstChartKT.pivotY.toInt()
        val radius: Int =
            Math.max(sunburstChartKT.width, sunburstChartKT.height)
        val animator: Animator

        if (animateOut) {
            animator = ViewAnimationUtils.createCircularReveal(
                sunburstChartKT, centerX, centerY, radius.toFloat(), 0f
            )
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if(flagInt == ANIMATION_NEXT_WEEK)
                        trainingStatisticsViewModel.nextWeek()
                    if(flagInt == ANIMATION_PAST_WEEK)
                        trainingStatisticsViewModel.pastWeek()
                    animate(false, NO_ANIMATION)
                }
            })
        } else {
            animator = ViewAnimationUtils.createCircularReveal(
                sunburstChartKT, centerX, centerY, 0f, radius.toFloat()
            )
        }

        val centerX1: Int = lineChart.pivotX.toInt()
        val centerY1: Int = lineChart.pivotY.toInt()
        val radius1: Int =
            Math.max(lineChart.width, lineChart.height)
        val animator1: Animator
        if (animateOut) {
            animator1 = ViewAnimationUtils.createCircularReveal(
                lineChart, centerX1, centerY1, radius1.toFloat(), 0f
            )
        } else {
            animator1 = ViewAnimationUtils.createCircularReveal(
                lineChart, centerX1, centerY1, 0f, radius1.toFloat()
            )
        }
        animator1.duration = 300
        animator1.start()


        animator.duration = 300
        animator.start()
    }

    private fun initLineChart(distanceInDayOfWeek: List<Double>) {
        val listValues: MutableList<Entry> = mutableListOf()
        for((count, distance) in distanceInDayOfWeek.withIndex()) {
            listValues.add(Entry(count.toFloat(), floor(distance * 1000).toFloat()))
        }

        val lineDataSet = LineDataSet(listValues, "dataSet1")
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(lineDataSet)
        val data = LineData(dataSets)
        val xAxis = lineChart.xAxis
        val leftAxis = lineChart.axisLeft
        val rightAxis = lineChart.axisRight

        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 14f
        val xAxisValues: List<String> = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)

        leftAxis.isEnabled = false
        rightAxis.isEnabled = false

        val legend: Legend = lineChart.legend
        legend.isEnabled = false

        lineChart.description.isEnabled = false
        lineChart.data = data
        lineChart.extraBottomOffset = 5f
        lineChart.setTouchEnabled(false)
        lineDataSet.valueTextSize = 14f
        lineDataSet.valueTextColor = Color.BLACK
        lineDataSet.lineWidth = 3f
        lineDataSet.color = resources.getColor(R.color.purple_500,null)
        lineDataSet.setCircleColor(resources.getColor(R.color.purple_500,null))
        lineDataSet.circleRadius = 8f

        lineChart.data.isHighlightEnabled = false
        lineChart.invalidate()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_training_statistics, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_training_statistics_menu -> {
                val action = TrainingStatisticsFragmentDirections
                    .actionTrainingStatisticsFragmentToPreferencesFragment()
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}