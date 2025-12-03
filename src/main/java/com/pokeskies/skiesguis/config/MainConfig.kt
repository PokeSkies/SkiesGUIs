package com.pokeskies.skiesguis.config

class MainConfig(
    var debug: Boolean = false,
    var storage: StorageOptions = StorageOptions(),
) {
    override fun toString(): String {
        return "MainConfig(debug=$debug, storage=$storage)"
    }
}
