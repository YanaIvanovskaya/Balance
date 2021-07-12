package com.example.balance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.balance.databinding.FragmentRecordCreationBinding

class RecordCreationFragment : Fragment(R.layout.fragment_record_creation) {

    private var mBinding: FragmentRecordCreationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRecordCreationBinding.inflate(inflater, container, false)
        mBinding = binding

        // TODO: 12.07.2021 Можно вынести в отдельные функции, чтобы было читаемее
        binding.buttonCreateAndSaveNewRecord.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )

        return binding.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}