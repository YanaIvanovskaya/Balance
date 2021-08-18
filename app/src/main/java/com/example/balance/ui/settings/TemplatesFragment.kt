package com.example.balance.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentMyTemplatesBinding
import com.example.balance.presentation.getViewModel
import com.example.balance.presentation.settings.TemplateState
import com.example.balance.presentation.settings.TemplatesViewModel
import com.example.balance.ui.recycler_view.DividerItemDecoration
import com.example.balance.ui.recycler_view.SwipeToDeleteCallback
import com.example.balance.ui.recycler_view.adapter.TemplateAdapter
import com.example.balance.ui.recycler_view.item.TemplateItem
import com.google.android.material.snackbar.Snackbar

class TemplatesFragment : Fragment(R.layout.fragment_my_templates) {

    private var mBinding: FragmentMyTemplatesBinding? = null
    private lateinit var mNavController: NavController
    private lateinit var mTemplateRecyclerView: RecyclerView
    private lateinit var mTemplateAdapter: TemplateAdapter
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
        mNavController = findNavController()
        mTemplateRecyclerView = binding.templatesRecyclerView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTemplateAdapter = TemplateAdapter()
        mViewModel.state.observe(viewLifecycleOwner, ::render)
        initRecyclerView()
        initButtons()
    }

    private fun initRecyclerView() {
        mTemplateRecyclerView.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        ResourcesCompat.getDrawable(resources, R.drawable.item_divider, null)
            ?.let { DividerItemDecoration(it) }
            ?.let { mTemplateRecyclerView.addItemDecoration(it) }
        mTemplateRecyclerView.adapter = mTemplateAdapter

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                viewHolder.itemView.visibility = View.GONE
                val deletedTemplate = mTemplateAdapter.dataSet[position] as TemplateItem
                mTemplateAdapter.removeAt(position)

                val undoSnackBar =
                    Snackbar.make(requireView(), "Шаблон удален", Snackbar.LENGTH_LONG)
                undoSnackBar.setBackgroundTint(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.grey_400,
                        null
                    )
                )
                undoSnackBar.setActionTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.green_400,
                        null
                    )
                )
                undoSnackBar.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.grey_800,
                        null
                    )
                )
                undoSnackBar.setAction("Восстановить") {
                    viewHolder.itemView.visibility = View.VISIBLE
                    mTemplateAdapter.notifyDataSetChanged()
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
        val itemDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemDeleteHelper.attachToRecyclerView(mTemplateRecyclerView)

//        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                if (viewHolder is ViewHolderFactory.TemplateViewHolder) {
//                    val position = viewHolder.bindingAdapterPosition
//                    val editedTemplate = mTemplateAdapter.dataSet[position] as TemplateItem
//                }
//            }
//        }
//        val itemEditHelper = ItemTouchHelper(swipeEditHandler)
//        itemEditHelper.attachToRecyclerView(mTemplateRecyclerView)


    }

    private fun render(state: TemplateState) {
        when (state.currentChip) {
            0 -> {
                mBinding?.commonTemplates?.isChecked = true
                mTemplateAdapter.dataSet = state.commonTemplates
            }
            1 -> {
                mBinding?.profitTemplates?.isChecked = true
                mTemplateAdapter.dataSet = state.profitTemplates
            }
            2 -> {
                mBinding?.costsTemplates?.isChecked = true
                mTemplateAdapter.dataSet = state.costsTemplates
            }
        }
        mTemplateAdapter.notifyDataSetChanged()
    }

    private fun initButtons() {
        mBinding?.costsTemplates?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                mViewModel.saveCurrentChip(2)
        }
        mBinding?.profitTemplates?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                mViewModel.saveCurrentChip(1)
        }
        mBinding?.commonTemplates?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                mViewModel.saveCurrentChip(0)
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