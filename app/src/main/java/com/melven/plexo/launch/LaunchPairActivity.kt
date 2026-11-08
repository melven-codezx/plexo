package com.melven.plexo.launch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

/**
 * Activity invisivel que serve de trampolim para atalhos, widget e quick settings tile.
 * Fecha-se imediatamente apos largar o par de apps.
 */
class LaunchPairActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        finish()
    }

    private fun handleIntent(intent: Intent?) {
        intent ?: return
        val pkgA = intent.getStringExtra(EXTRA_PKG_A) ?: return
        val pkgB = intent.getStringExtra(EXTRA_PKG_B) ?: return
        val mode = runCatching {
            LaunchMode.valueOf(intent.getStringExtra(EXTRA_MODE) ?: LaunchMode.SPLIT.name)
        }.getOrDefault(LaunchMode.SPLIT)

        val request = SplitLauncher.Request(
            pkgA = pkgA,
            actA = intent.getStringExtra(EXTRA_ACT_A),
            pkgB = pkgB,
            actB = intent.getStringExtra(EXTRA_ACT_B),
            mode = mode
        )
        val result = SplitLauncher.launch(this, request)
        if (!result.ok) Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val EXTRA_PKG_A = "extra_pkg_a"
        const val EXTRA_ACT_A = "extra_act_a"
        const val EXTRA_PKG_B = "extra_pkg_b"
        const val EXTRA_ACT_B = "extra_act_b"
        const val EXTRA_MODE = "extra_mode"
    }
}
