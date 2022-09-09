package com.pigllet.hmoneylover.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pigllet.hmoneylover.R
import com.pigllet.hmoneylover.billings.activities.BuyCoinActivity
import com.pigllet.hmoneylover.billings.activities.IAPActivity
import com.pigllet.hmoneylover.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentMainBinding.inflate(inflater, container, false).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnStart.setOnClickListener {
            findNavController().navigate(R.id.action_to_homeFragment)
        }
        binding.btnBuy.setOnClickListener {
            startActivity(Intent(activity, IAPActivity::class.java))
        }
    }

}