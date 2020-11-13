package com.example.speedtest

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.speedtest.data.model.SaveFieldData
import com.example.speedtest.data.viewModel.ViewModel
import kotlin.system.measureNanoTime

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ViewModel
    private val count = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(ViewModel::class.java)

        val mButton: Button = findViewById(R.id.mButton)
        val mButtonRoom: Button = findViewById(R.id.mButtonRoom)
        val mButtonClear: Button = findViewById(R.id.mButtonClear)
        val mButtonClearTV: Button = findViewById(R.id.mButtonClearTV)
        val mTextSp: TextView = findViewById(R.id.textView)
        val mTextRoom: TextView = findViewById(R.id.textViewRoom)
        val mTextDeleteSp: TextView = findViewById(R.id.textViewDeleteSp)
        val mTextDeleteRoom: TextView = findViewById(R.id.textViewDeleteRoom)
        val sharedPreference = this.getSharedPreferences(
            "speedTestSharedPreferences",
            Context.MODE_PRIVATE
        )


        mButton.setOnClickListener {
            val time = measureNanoTime {
                for (i in 0..count){
                    with(sharedPreference.edit()){
                        putInt("$i", i)
                        apply()
                    }
                }
            }

            mTextSp.text = (time / 1000000000.0).toString()
        }

        mButtonRoom.setOnClickListener {
            val time = measureNanoTime {
                for (i in 0..count){
                    viewModel.insert(SaveFieldData(id = i, number = i))
                }
            }

            mTextRoom.text = (time / 1000000000.0).toString()
        }

        mButtonClear.setOnClickListener {
            val timeDeleteSp = measureNanoTime {
                viewModel.delete()
            }
            val timeDeleteRoom = measureNanoTime {
                sharedPreference.edit().clear().apply()
            }
            mTextDeleteSp.text = (timeDeleteSp / 1000000000.0).toString()
            mTextDeleteRoom.text = (timeDeleteRoom / 1000000000.0).toString()
        }

        mButtonClearTV.setOnClickListener {
            mTextSp.text = ""
            mTextRoom.text = ""
            mTextDeleteSp.text = ""
            mTextDeleteRoom.text = ""
        }

    }
}