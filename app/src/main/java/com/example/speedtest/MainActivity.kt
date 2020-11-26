package com.example.speedtest

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.speedtest.data.model.SaveFieldData
import com.example.speedtest.data.viewModel.ViewModel
import kotlin.system.measureNanoTime

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ViewModel
    private var mCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(ViewModel::class.java)

        val valueToCheck: EditText = findViewById(R.id.valueToCheck)
        val mButton: Button = findViewById(R.id.mButton)
        val mButtonRoom: Button = findViewById(R.id.mButtonRoom)
        val mButtonClear: Button = findViewById(R.id.mButtonClear)
        val mButtonClearTV: Button = findViewById(R.id.mButtonClearTV)
        val mGetButton: Button = findViewById(R.id.mGetButton)
        val mGetButtonRoom: Button = findViewById(R.id.mGetButtonRoom)
        val mTextSp: TextView = findViewById(R.id.textView)
        val mTextRoom: TextView = findViewById(R.id.textViewRoom)
        val mTextDeleteSp: TextView = findViewById(R.id.textViewDeleteSp)
        val mTextDeleteRoom: TextView = findViewById(R.id.textViewDeleteRoom)
        val textViewGetSp: TextView = findViewById(R.id.textViewGetSp)
        val textViewGetRoom: TextView = findViewById(R.id.textViewGetRoom)
        val sharedPreference = this.getSharedPreferences(
                "speedTestSharedPreferences",
                Context.MODE_PRIVATE
        )

        valueToCheck.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mCount = if(s.toString().isNullOrEmpty()){
                    0
                }else {
                    Integer.parseInt(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })


        mButton.setOnClickListener {
            val time = measureNanoTime {
                for (i in 0..mCount){
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
                for (i in 0..mCount){
                    viewModel.insert(SaveFieldData(id = i, number = i))
                }
            }

            mTextRoom.text = (time / 1000000000.0).toString()
        }

        mGetButton.setOnClickListener {
            val time = measureNanoTime {
                sharedPreference.all
            }

            textViewGetSp.text = (time / 1000000000.0).toString()
        }

        mGetButtonRoom.setOnClickListener {
            val time = measureNanoTime {
                viewModel.getAll()
            }

            textViewGetRoom.text = (time / 1000000000.0).toString()
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
            textViewGetSp.text = ""
            textViewGetRoom.text = ""
            mTextDeleteSp.text = ""
            mTextDeleteRoom.text = ""
        }

    }
}