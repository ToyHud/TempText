package com.example.android.temptext.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.android.temptext.databinding.FragmentSetUpAlertBinding
import com.example.android.temptext.viewmodel.TempTextViewModel
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
    private val viewModel: TempTextViewModel by activityViewModels()
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

           // viewModel.insertWeatherData()
            Snackbar.make(view, "Saved to database", Snackbar.LENGTH_SHORT).show()
        }
    }
}