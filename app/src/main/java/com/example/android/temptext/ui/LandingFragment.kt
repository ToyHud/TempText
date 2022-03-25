package com.example.android.temptext.ui

import android.content.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.android.temptext.databinding.FragmentLandingBinding
import com.example.android.temptext.viewmodel.TempTextViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [LandingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34

class LandingFragment : Fragment() {


    private var _binding: FragmentLandingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TempTextViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentLandingBinding.inflate(inflater, container, false)
        // locationSearch = binding.button

        return binding.root
    }
}