package com.example.balance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment
import com.example.balance.databinding.GreetingNewUserFragmentBinding

//
//data class Record(val id: String)
//
//class FirstViewModel : ViewModel() {
//
//
//
//}

class GreetingNewUserFragment : Fragment(R.layout.greeting_new_user_fragment) {

//    var list = listOf<Record>()
//    lateinit var viewModel: FirstViewModel
    private var mBinding: GreetingNewUserFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = GreetingNewUserFragmentBinding.inflate(inflater,container,false)
        mBinding = binding

        val navController = NavHostFragment.findNavController(this)

        binding.buttonStartNext.setOnClickListener {
            navController.navigate(R.id.passcodeCreationFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}

