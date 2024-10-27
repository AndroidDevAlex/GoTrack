package com.example.settingsapplication

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import com.example.settingsApplication.R
import com.example.settingsApplication.databinding.DialogSettingsBottomSheetBinding
import com.google.android.material.chip.Chip
import com.google.android.material.color.MaterialColors

abstract class DialogSettings : Fragment() {

    fun <T : ApplicationLanguage> showMultipleChoiceSettingsDialog(
        title: String,
        subtitle: String,
        setting: T,
        onSelect: (T) -> Unit
    ) {

        val dialogBinding = DialogSettingsBottomSheetBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(dialogBinding.root)
            window?.apply {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                attributes.windowAnimations = R.style.DialogAnimation
                setGravity(Gravity.BOTTOM)
            }
        }

        dialogBinding.dialogTitle.text = title
        dialogBinding.dialogHelpingText.text = subtitle

        ApplicationLanguage.entries.forEach { language ->
            val chip = Chip(requireContext()).apply {
                text = language.getLocalizedName(requireContext())
                chipBackgroundColor = MaterialColors.getColorStateListOrNull(
                    context,
                    com.google.android.material.R.attr.colorPrimaryContainer
                )
                setTextColor(
                    MaterialColors.getColor(
                        context,
                        com.google.android.material.R.attr.colorOnPrimaryContainer,
                        Color.WHITE
                    )
                )
                isCheckable = true
                tag = language
                if (language == setting) {
                    isChecked = true
                    chipBackgroundColor = MaterialColors.getColorStateListOrNull(
                        context,
                        com.google.android.material.R.attr.colorTertiaryContainer
                    )
                    chipStrokeColor = MaterialColors.getColorStateListOrNull(
                        context,
                        com.google.android.material.R.attr.colorOnPrimaryContainer
                    )
                }
            }
            dialogBinding.dialogChipGroup.addView(chip)
        }

        dialogBinding.dialogChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val currentSelection = group.findViewById<Chip>(checkedIds[0]).tag as T
            onSelect(currentSelection)
            dialog.dismiss()
        }

        dialogBinding.dialogCloseButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}