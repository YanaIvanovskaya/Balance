package com.example.balance.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentMyTemplatesBinding
import com.example.balance.presentation.TemplateState
import com.example.balance.presentation.TemplatesViewModel
import com.example.balance.presentation.getViewModel
import com.example.balance.ui.recycler_view.SwipeToDeleteCallback
import com.example.balance.ui.recycler_view.adapter.TemplateAdapter
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
        mViewModel.state.observe(viewLifecycleOwner, ::render)
    }

    private fun initRecyclerView() {
        templateRecyclerView.adapter = templateAdapter
        templateRecyclerView.layoutManager = LinearLayoutManager(context)

        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                viewHolder.itemView.visibility = View.GONE
                templateAdapter.notifyDataSetChanged()

                val deletedTemplate = templateAdapter.dataSet[position]
                val undoSnackBar =
                    Snackbar.make(requireView(), "Шаблон удален", Snackbar.LENGTH_LONG)

                undoSnackBar.setAction("Восстановить") {
                    viewHolder.itemView.visibility = View.VISIBLE
                    templateAdapter.notifyDataSetChanged()
                }

                val dismissCallback = object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        if (event == DISMISS_EVENT_TIMEOUT) {
                            mViewModel.removeTemplate(deletedTemplate.id)
                        }

                    }
                }
                undoSnackBar.addCallback(dismissCallback)
                undoSnackBar.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(templateRecyclerView)
    }

    private fun render(state: TemplateState) {
        when (state.currentChip) {
            0 -> {
                mBinding?.commonTemplates?.isChecked = true
                templateAdapter.dataSet = state.commonTemplates
            }
            1 -> {
                mBinding?.profitTemplates?.isChecked = true
                templateAdapter.dataSet = state.profitTemplates
            }
            2 -> {
                mBinding?.costsTemplates?.isChecked = true
                templateAdapter.dataSet = state.costsTemplates
            }
        }
        templateAdapter.notifyDataSetChanged()
    }

    private fun initButtons() {
        mBinding?.costsTemplates?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.saveCurrentChip(2)
            }
        }
        mBinding?.profitTemplates?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.saveCurrentChip(1)
            }
        }
        mBinding?.commonTemplates?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.saveCurrentChip(0)
            }
        }

        mBinding?.toolbarMyTemplates?.setNavigationOnClickListener {
            mNavController.popBackStack()
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