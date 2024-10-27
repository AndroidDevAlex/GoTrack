package com.example.utils

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object Utils {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun showGpsDialog(context: Context, fragment: Fragment) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(context.getString(R.string.activate_gps_in_the_settings_to_use_this_feature))
            .setPositiveButton(context.getString(R.string.settings)) { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                fragment.startActivity(intent)
            }
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
            setTextColor(ContextCompat.getColor(context, R.color.black))
            layoutParams = (layoutParams as LinearLayout.LayoutParams).apply {
                marginStart = 16
                marginEnd = 16
            }
        }
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
            setTextColor(ContextCompat.getColor(context, R.color.black))
            layoutParams = (layoutParams as LinearLayout.LayoutParams).apply {
                marginStart = 16
                marginEnd = 16
            }
        }
    }

    fun requestLocationAndNotificationPermission(requestPermissionLauncher: ActivityResultLauncher<Array<String>>) {
        val permissions = when {

            // For Android 14 and above
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.FOREGROUND_SERVICE_LOCATION
                )
            }
            // For Android 13
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
            // For versions below Android 13
            else -> {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
        }
        requestPermissionLauncher.launch(permissions)
    }

    fun requestNotificationPermission(context: Context, fragment: Fragment) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.areNotificationsEnabled()) {
            showToast(context, context.getString(R.string.Notification_settings_are_available))
        } else {
            try {
                val intent = Intent()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                fragment.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                showToast(context, context.getString(R.string.Notification_settings_are_not_available))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}