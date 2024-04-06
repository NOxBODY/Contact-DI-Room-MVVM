package com.vibeoncreation.contact

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibeoncreation.contact.ui.theme.ContactTheme
import com.vibeoncreation.contact.ui.widgets.ContactScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: ContactViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                ContactScreen(
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}