package com.example.android.temptext.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.android.temptext.R
import com.example.android.temptext.database.WeatherApplication
import com.example.android.temptext.databinding.FragmentSetUpAlertBinding
import com.example.android.temptext.viewmodel.TempTextViewModel
import com.example.android.temptext.viewmodel.WeatherViewModelFactory
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 * Use the [SetUpAlertFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetUpAlertFragment : Fragment() {
    private var _binding: FragmentSetUpAlertBinding? = null
    private val binding get() = _binding!!
    private lateinit var saveButton: Button
    private val viewModel: TempTextViewModel by activityViewModels {
        WeatherViewModelFactory((activity!!.application as WeatherApplication).database.weatherDao()!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSetUpAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveButton = binding.savebtn
        saveButton.setOnClickListener {
            saveAlertToDatabase(view)
            Snackbar.make(view, "Saved to database", Snackbar.LENGTH_SHORT).show()
        }
    }

    fun saveAlertToDatabase(view: View) {
        if(view is RadioButton){
            //val weatherData = WeatherModel()
            //determines if radiobutton is checked
            val checked = view.isChecked
            when(view.getId()){
                (R.id.temp70) ->
                    if(checked){
                        Snackbar.make(saveButton,"Alert set for 70",Snackbar.LENGTH_SHORT).show()
                    //    viewModel.insertIntoDatabase()
                    }
                (R.id.temp80) ->
                    if(checked){
                        Snackbar.make(saveButton,"Alert set for 80",Snackbar.LENGTH_SHORT).show()
                        //viewModel.insertWeatherData(70.0)
                    }
                (R.id.temp90) ->
                    if(checked){
                        Snackbar.make(saveButton,"Alert set for 90",Snackbar.LENGTH_SHORT).show()
                        //viewModel.insertWeatherData(70.0)
                    }
                (R.id.temp100) ->
                    if(checked){
                        Snackbar.make(saveButton,"Alert set for 100",Snackbar.LENGTH_SHORT).show()
                        //viewModel.insertWeatherData(70.0)
                    }
                (R.id.temp40) ->
                    if(checked){
                        Snackbar.make(saveButton,"Alert set for 40",Snackbar.LENGTH_SHORT).show()
                        //viewModel.insertWeatherData(70.0)
                    }
                (R.id.temp30) ->
                    if(checked){
                        Snackbar.make(saveButton,"Alert set for 30",Snackbar.LENGTH_SHORT).show()
                        //viewModel.insertWeatherData(70.0)
                    }
                (R.id.temp20) ->
                    if(checked){
                        Snackbar.make(saveButton,"Alert set for 20",Snackbar.LENGTH_SHORT).show()
                        //viewModel.insertWeatherData(70.0)
                    }
                (R.id.temp10) ->
                    if(checked){
                        Snackbar.make(saveButton,"Alert set for 10",Snackbar.LENGTH_SHORT).show()
                        //viewModel.insertWeatherData(70.0)
                    }
            }
        }
        findNavController().navigate(R.id.action_setUpAlertFragment_to_mainWeatherFragment)
    }
}