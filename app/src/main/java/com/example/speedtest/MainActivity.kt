package com.example.speedtest

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.speedtest.data.model.ChartData
import com.example.speedtest.data.model.SaveFieldData
import com.example.speedtest.data.viewModel.ChartViewModel
import com.example.speedtest.data.viewModel.ViewModel
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.system.measureNanoTime


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ViewModel
    private var mCount = 3000
    private lateinit var db: FirebaseFirestore
    private var timeCheckSP = 0L
    private var timeCheckRoom= 0L
    private var timeGetSP = 0L
    private var timeGetRoom = 0L
    private var timeDeleteSP= 0L
    private var timeDeleteRoom = 0L

    private var checkSPArray: HashMap<String, Float> = HashMap()
    private var checkRoomArray: HashMap<String, Float> = HashMap()
    private var getSPArray: HashMap<String, Float> = HashMap()
    private var getRoomArray: HashMap<String, Float> = HashMap()
    private var deleteSPArray: HashMap<String, Float> = HashMap()
    private var deleteRoomArray: HashMap<String, Float> = HashMap()

    private lateinit var chartViewModel: ChartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chartViewModel = ViewModelProvider(this).get(ChartViewModel::class.java)
        val chart = findViewById<HorizontalBarChart>(R.id.chart_bar)
        val spinner: Spinner = findViewById(R.id.spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> setChart(checkSPArray, chart)
                    1 -> setChart(checkRoomArray, chart)
                    2 -> setChart(getSPArray, chart)
                    3 -> setChart(getRoomArray, chart)
                    4 -> setChart(deleteSPArray, chart)
                    5 -> setChart(deleteRoomArray, chart)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
        chartViewModel.listObservable.observe(this, Observer {
            if(it != null){
                for (i in it){
                    checkSPArray[i.name] = i.timeCheckSP
                    checkRoomArray[i.name] = i.timeCheckRoom
                    getSPArray[i.name] = i.timeGetSP
                    getRoomArray[i.name] = i.timeGetRoom
                    deleteSPArray[i.name] = i.timeDeleteSP
                    deleteRoomArray[i.name] = i.timeDeleteRoom
                }
                setChart(checkSPArray, chart)
            }
        })

        val mStartButton: Button = findViewById(R.id.mStartButton)
        val sharedPreference = this.getSharedPreferences(
                "speedTestSharedPreferences",
                Context.MODE_PRIVATE
        )

        db = FirebaseFirestore.getInstance()

        mStartButton.setOnClickListener {
            timeCheckSP = measureNanoTime {
                for (i in 0..mCount){
                    with(sharedPreference.edit()){
                        putInt("$i", i)
                        apply()
                    }
                }
            }

            timeCheckRoom = measureNanoTime {
                for (i in 0..mCount){
                    viewModel.insert(SaveFieldData(id = i, number = i))
                }
            }

            timeGetSP = measureNanoTime {
                sharedPreference.all
            }

            timeGetRoom = measureNanoTime {
                viewModel.getAll()
            }

            timeDeleteSP = measureNanoTime {
                sharedPreference.edit().clear().apply()
            }

            timeDeleteRoom = measureNanoTime {
                viewModel.delete()
            }

            sendToFirestore()
            getFromFirestore()
        }
    }

    private fun sendToFirestore(){
        val data = hashMapOf(
                "timeCheckSP" to (timeCheckSP / 1000000000.0).toString(),
                "timeCheckRoom" to (timeCheckRoom / 1000000000.0).toString(),
                "timeGetSP" to (timeGetSP / 1000000000.0).toString(),
                "timeGetRoom" to (timeGetRoom / 1000000000.0).toString(),
                "timeDeleteSP" to (timeDeleteSP / 1000000000.0).toString(),
                "timeDeleteRoom" to (timeDeleteRoom / 1000000000.0).toString()
        )

        db.collection("benchmark").document(android.os.Build.MODEL)
                .set(data)
                .addOnSuccessListener { Toast.makeText(this, "data send to database", Toast.LENGTH_LONG).show() }
                .addOnFailureListener { e -> Toast.makeText(this, "can not send data", Toast.LENGTH_LONG).show() }
    }

    private fun getFromFirestore(){
        db.collection("benchmark")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val cD = ChartData(document.id,
                        document.data["timeCheckSP"].toString().toFloat(),
                        document.data["timeCheckRoom"].toString().toFloat(),
                        document.data["timeGetSP"].toString().toFloat(),
                        document.data["timeGetRoom"].toString().toFloat(),
                        document.data["timeDeleteSP"].toString().toFloat(),
                        document.data["timeDeleteRoom"].toString().toFloat()
                    )
                    chartViewModel.addChartData(cD)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "can not get data", Toast.LENGTH_LONG).show()
            }
    }

    private fun setChart(array: HashMap<String, Float>, chart: HorizontalBarChart){
        val result = array.toList().sortedBy { (_, value) -> value}.reversed().toMap()
        val values = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        var num = 0
        for(i in result){
            values.add(BarEntry(num.toFloat(), i.value))
            if(i.key == android.os.Build.MODEL)
                labels.add("Moje urządzenie")
            else
                labels.add(i.key)
            num++
        }

        val barDataSet = BarDataSet(values, "phones")
        chart.xAxis.textColor = ContextCompat.getColor(this,R.color.teal_700)
        chart.xAxis.setLabelCount(labels.size, false)
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        val data = BarData(barDataSet)
        chart.data = data // set the data and list of lables into chart
        data.setValueTextColor(ContextCompat.getColor(this,R.color.purple_200))
        barDataSet.color = ContextCompat.getColor(this,R.color.purple_200)
        chart.setFitBars(true)
        chart.animateY(2000)
        chart.invalidate()
    }
}