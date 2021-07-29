package com.example.balance.ui.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.balance.R

class StatisticsCommonFragment : Fragment(R.layout.fragment_statistics_dashboard) {
    private var mPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (getArguments() != null) {
//            mPage = getArguments().getInt(ARG_PAGE)
//        }
    }

//    override fun onCreateView(
//        @NonNull inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val view: View = inflater.inflate(R.layout.fragment_page, container, false)
//        val textView = view as TextView
//        textView.text = "Fragment #$mPage"
//        return view
//    }
//
//    companion object {
//        const val ARG_PAGE = "ARG_PAGE"
//        fun newInstance(page: Int): PageFragment {
//            val args = Bundle()
//            args.putInt(ARG_PAGE, page)
//            val fragment = PageFragment()
//            fragment.setArguments(args)
//            return fragment
//        }
//    }
}