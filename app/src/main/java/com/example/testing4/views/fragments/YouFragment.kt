package com.example.testing4.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testing4.R
import com.example.testing4.databinding.FragmentYouBinding

class YouFragment : Fragment() {
    private lateinit var binding : FragmentYouBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentYouBinding.inflate(inflater, container, false)
        return binding.root
    }

}