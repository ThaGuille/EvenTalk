package com.example.tfg_application.ui.chat

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts

class MyOpenDocument : ActivityResultContracts.OpenDocument() {

    override fun createIntent(context: Context, input: Array<out String>): Intent {
        val intent = super.createIntent(context, input)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        return intent;
    }
}