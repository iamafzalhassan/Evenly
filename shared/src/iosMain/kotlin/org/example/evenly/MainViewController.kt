package org.example.evenly

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

private val appGraph: AppGraph by lazy { createAppGraph() }

fun MainViewController(): UIViewController = ComposeUIViewController { App(graph = appGraph) }
