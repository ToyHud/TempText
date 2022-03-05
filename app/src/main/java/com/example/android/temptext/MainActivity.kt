package com.example.android.temptext

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.example.android.temptext.viewmodel.TempTextViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: TempTextViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}