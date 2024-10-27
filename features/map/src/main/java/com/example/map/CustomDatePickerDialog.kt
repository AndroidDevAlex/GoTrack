package com.example.map

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class CustomDatePickerDialog : DialogFragment(), DatePickerDialog.OnDateSetListener {

    companion object {
        const val SELECTED_DAY_KEY = "selected_day_key"

        fun newInstance(selectedDate: Long): CustomDatePickerDialog {
            val args = Bundle()
            args.putLong(SELECTED_DAY_KEY, selectedDate)
            val fragment = CustomDatePickerDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()

        arguments?.getLong(SELECTED_DAY_KEY)?.let { selectedDate ->
            calendar.timeInMillis = selectedDate
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val selectedDate = calendar.timeInMillis

        parentFragmentManager.setFragmentResult(
            SELECTED_DAY_KEY,
            bundleOf(SELECTED_DAY_KEY to selectedDate)
        )
    }
}