package com.example.speedtest

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.speedtest.data.model.SaveFieldData
import com.example.speedtest.data.viewModel.ViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(ViewModel::class.java)

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
        }
    }

    fun sendToFirestore(){
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
}