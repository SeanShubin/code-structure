package com.seanshubin.code.structure.composition

class BootstrapDependencies(
    integrations: Integrations
) {
    val bootstrap: Bootstrap = Bootstrap(integrations)
}
