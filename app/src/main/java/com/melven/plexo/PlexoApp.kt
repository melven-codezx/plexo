package com.melven.plexo

import android.app.Application
import com.melven.plexo.data.AppCatalog
import com.melven.plexo.data.PairStore

/**
 * Ponto de entrada da aplicacao. Dependencias leves, sem frameworks de injecao.
 */
class PlexoApp : Application() {

    lateinit var pairStore: PairStore
        private set

    lateinit var catalog: AppCatalog
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        pairStore = PairStore(this)
        catalog = AppCatalog(this)
    }

    companion object {
        lateinit var instance: PlexoApp
            private set
    }
}
