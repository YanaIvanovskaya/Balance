package com.example.balance.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.data.record.RecordType
import com.example.balance.databinding.FragmentMyTemplatesBinding
import com.example.balance.presentation.TemplatesViewModel
import com.example.balance.presentation.getViewModel
import com.example.balance.ui.recycler_view.SwipeToDeleteCallback
import com.example.balance.ui.recycler_view.TemplateAdapter
import com.google.android.material.snackbar.Snackbar


class TemplatesFragment : Fragment(R.layout.fragment_my_templates) {
    private var mBinding: FragmentMyTemplatesBinding? = null
    private lateinit var mNavController: NavController
    private lateinit var templateRecyclerView: RecyclerView
    private lateinit var templateAdapter: TemplateAdapter

    private val mViewModel by getViewModel {
        TemplatesViewModel(
            recordRepository = BalanceApp.recordRepository,
            templateRepository = BalanceApp.templateRepository,
            categoryRepository = BalanceApp.categoryRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMyTemplatesBinding.inflate(inflater, container, false)
        mBinding = binding
        templateRecyclerView = binding.templatesRecyclerView
        templateAdapter = TemplateAdapter()
        mNavController = findNavController()
        initRecyclerView()
        initButtons()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.allTemplates.observe(viewLifecycleOwner) { list ->
            templateAdapter.dataSet = list.toMutableList()
            templateAdapter.notifyDataSetChanged()
        }
    }

    private fun initRecyclerView() {
        templateRecyclerView.adapter = templateAdapter
        templateRecyclerView.layoutManager = LinearLayoutManager(context)

        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                templateAdapter.removeAt(viewHolder.bindingAdapterPosition)
                val snackBar = Snackbar.make(requireView(), "Шаблон удален", Snackbar.LENGTH_SHORT)

                snackBar.setAction("Восстановить") {
                    templateAdapter.notifyDataSetChanged()
                }
                snackBar.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(templateRecyclerView)

    }


    private fun initButtons() {
        mBinding?.toolbarMyTemplates?.setNavigationOnClickListener {
            mNavController.popBackStack()
        }

        mBinding?.costsTemplates?.setOnCheckedChangeListener { buttonView, isChecked ->

        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    mNavController.popBackStack()
                }
            }
        )
    }

}