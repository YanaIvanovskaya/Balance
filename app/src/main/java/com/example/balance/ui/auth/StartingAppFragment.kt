package com.example.balance.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.balance.BalanceApp
import com.example.balance.Event
import com.example.balance.R
import com.example.balance.data.BalanceRepository
import com.example.balance.data.UserDataStore
import com.example.balance.data.category.Category
import com.example.balance.data.category.CategoryType
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordType
import com.example.balance.presentation.getViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate

class StartingAppFragment : Fragment(R.layout.fragment_starting_app) {

    private val mViewModel by getViewModel {
        StartingAppViewModel(
            dataStore = BalanceApp.dataStore,
            balanceRepository = BalanceApp.balanceRepository
        )
    }
    private lateinit var mNavController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = findNavController()

        mViewModel.events.observe(viewLifecycleOwner) { event ->
            event.consume { isNewUser ->
                mNavController.navigate(
                    when {
                        isNewUser -> R.id.onboarding_nav_graph
                        else -> R.id.auth_nav_graph
                    }
                )
            }
        }
    }

}

class StartingAppViewModel(
    private val dataStore: UserDataStore,
    private val balanceRepository: BalanceRepository
) : ViewModel() {

    val events = MutableLiveData<Event<Boolean>>()

    private fun fillDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            var date = LocalDate.of(2021, 1, 1)
            for (i in 1..20) {
                val newDate = date.plusDays((1..3).random().toLong())
                date = newDate
                for (n in 1..(1..3).random()) {
                    val recordType = if ((0..1).random() == 1) RecordType.PROFITS else RecordType.COSTS
                    val record = Record(
                        day = newDate.dayOfMonth,
                        month = newDate.month.value,
                        year = newDate.year,
                        weekDay = newDate.dayOfWeek.value,
                        isImportant = (0..1).random() == 1,
                        sumOfMoney = (1..100).random(),
                        recordType = recordType,
                        moneyType = if ((0..1).random() == 1) MoneyType.CASH else MoneyType.CARDS,
                        categoryId = if (recordType == RecordType.PROFITS) (11..12).random() else (9..10).random(),
                        comment = ""
                    )
                    BalanceApp.recordRepository.insert(record)
                }
            }
        }
    }

    init {
        viewModelScope.launch {
//            dataStore.savePasscode("00000")
//            dataStore.clearBalance()

//            BalanceApp.recordRepository.deleteAll()
//            withContext(Dispatchers.IO) {
//                fillDatabase()
//            }
            val passcode = withContext(Dispatchers.IO) {
                dataStore.passcode.first()
            }
            val isNewUser = passcode.isNullOrEmpty()

            if (!isNewUser) {
                balanceRepository.loadBalance()
            }

            events.value = Event(isNewUser)
        }
    }

}
