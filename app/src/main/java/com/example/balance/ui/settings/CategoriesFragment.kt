package com.example.balance.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.data.category.CategoryType
import com.example.balance.data.record.RecordType
import com.example.balance.databinding.FragmentMyCategoriesBinding
import com.example.balance.presentation.CategoriesViewModel
import com.example.balance.presentation.CategoryState
import com.example.balance.presentation.getViewModel
import com.example.balance.ui.recycler_view.*
import com.example.balance.ui.recycler_view.adapter.CategoryAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class CategoriesFragment : Fragment(R.layout.fragment_my_categories) {
    private var mBinding: FragmentMyCategoriesBinding? = null
    private lateinit var mNavController: NavController
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter

    private val mViewModel by getViewModel {
        CategoriesViewModel(
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
        val binding = FragmentMyCategoriesBinding.inflate(inflater, container, false)
        mBinding = binding
        categoryRecyclerView = binding.categoriesRecyclerView
        categoryAdapter = CategoryAdapter()
        mNavController = findNavController()
        initRecyclerView()
        initButtons()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.state.observe(viewLifecycleOwner, ::render)
    }

    private fun render(state: CategoryState) {
        when (state.currentChip) {
            0 -> {
                mBinding?.commonCategories?.isChecked = true
                categoryAdapter.dataSet = state.commonCategories
            }
            1 -> {
                mBinding?.profitCategories?.isChecked = true
                categoryAdapter.dataSet = state.profitCategories
            }
            2 -> {
                mBinding?.costsCategories?.isChecked = true
                categoryAdapter.dataSet = state.costsCategories
            }
        }
        categoryAdapter.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        categoryRecyclerView.adapter = categoryAdapter
        categoryRecyclerView.layoutManager = LinearLayoutManager(context)

        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (viewHolder is ViewHolderFactory.CategoryViewHolder) {
                    val position = viewHolder.bindingAdapterPosition
                    val deletedCategory = categoryAdapter.dataSet[position]
                    categoryAdapter.removeAt(position)

                    val undoSnackBar =
                        Snackbar.make(requireView(), "Категория удалена", Snackbar.LENGTH_SHORT)

                    undoSnackBar.setBackgroundTint(ResourcesCompat.getColor(resources,R.color.grey_400,null))
                    undoSnackBar.setActionTextColor(ResourcesCompat.getColor(resources,R.color.green_400,null))
                    undoSnackBar.setTextColor(ResourcesCompat.getColor(resources,R.color.grey_800,null))
                    undoSnackBar.setAction("Восстановить") {
                        categoryAdapter.insertAt(position,deletedCategory)
                        categoryAdapter.notifyDataSetChanged()
                    }

                    val dismissCallback = object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            if (event == DISMISS_EVENT_TIMEOUT) {
                                mViewModel.removeCategory(deletedCategory.id)
                            }
                        }
                    }
                    undoSnackBar.addCallback(dismissCallback)
                    undoSnackBar.show()
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(categoryRecyclerView)
    }


    private fun showAddCategoryBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(),R.style.BottomSheetDialog)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_category_creation)

        val profitCategoryNames = mViewModel.getProfitCategoryNames()
        val costsCategoryNames = mViewModel.getCostsCategoryNames()

        val categoryCosts =
            bottomSheetDialog.findViewById<RadioButton>(R.id.radioButton_category_costs)
        val categoryProfit =
            bottomSheetDialog.findViewById<RadioButton>(R.id.radioButton_category_profit)
        val categoryName =
            bottomSheetDialog.findViewById<TextInputEditText>(R.id.text_category_name)
        val buttonSave = bottomSheetDialog.findViewById<Button>(R.id.button_save_category)
        val textInputLayout = bottomSheetDialog.findViewById<TextInputLayout>(R.id.text_input_layout)

        when (mViewModel.state.value?.currentChip) {
            1 -> categoryProfit?.isChecked = true
            else -> categoryCosts?.isChecked = true
        }

        var currentCategoryName = ""
        categoryName?.doAfterTextChanged {
            currentCategoryName = it.toString().trim()
            if ((categoryCosts?.isChecked == true && it.toString().trim() in costsCategoryNames) ||
                (categoryProfit?.isChecked == true && it.toString().trim() in profitCategoryNames)
            ) {
                textInputLayout?.helperText = "Такая категория уже существует"
                buttonSave?.isEnabled = false

            } else if (categoryName.text?.trim()?.isEmpty() == true) {
                textInputLayout?.helperText = "Пустое название категории"
                buttonSave?.isEnabled = false
            } else {
                textInputLayout?.helperText = " "
                buttonSave?.isEnabled = true
            }
        }

        buttonSave?.setOnClickListener {
            val type = when (categoryCosts?.isChecked ?: false) {
                true -> CategoryType.CATEGORY_COSTS
                false -> CategoryType.CATEGORY_PROFIT
            }
            mViewModel.onSaveNewCategory(currentCategoryName, type)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun initButtons() {
        mBinding?.costsCategories?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.saveCurrentChip(2)
            }
        }
        mBinding?.profitCategories?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.saveCurrentChip(1)
            }
        }
        mBinding?.commonCategories?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.saveCurrentChip(0)
            }
        }

        mBinding?.floatingButtonCreateNewCategory?.setOnClickListener {
            showAddCategoryBottomSheet()
        }

        mBinding?.toolbarMyCategories?.setNavigationOnClickListener {
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