import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.balance.BalanceApp
import com.example.balance.presentation.PasscodeEntryViewModel
import com.example.balance.ui.onboarding.PasscodeEntryFragment

class ViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        when(modelClass) {
            PasscodeEntryFragment::class -> PasscodeEntryViewModel(
                dataStore = BalanceApp.dataStore
            )
            else -> PasscodeEntryViewModel(
                dataStore = BalanceApp.dataStore
            )
        } as T

}