package com.seanshubin.code.structure.pipeline

import com.seanshubin.code.structure.model.Observations

interface Observer {
    fun makeObservations(): Observations
}
