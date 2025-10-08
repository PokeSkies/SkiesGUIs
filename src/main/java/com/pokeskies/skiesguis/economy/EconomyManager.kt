package com.pokeskies.skiesguis.economy

import com.pokeskies.skiesguis.utils.Utils

object EconomyManager {
    // Case-insensitive map of service name to service instance
    private var services: MutableMap<String, IEconomyService> = mutableMapOf()

    fun init() {
        Utils.printInfo("Initializing internal economy services... All existing services will be cleared.")
        services.clear()

        InternalEconomyTypes.entries.forEach { type ->
            if (type.isModPresent()) {
                registerService(type.identifier, type.clazz.getDeclaredConstructor().newInstance())
            }
        }
    }

    fun registerService(id: String, service: IEconomyService): Boolean {
        val lower = id.lowercase()
        if (services.containsKey(lower)) {
            Utils.printError("A service with the id '$lower' is already registered. Skipping registration...")
            return false
        }
        services[lower] = service
        Utils.printInfo("Registered Economy Service with id '$lower'")
        return true
    }

    fun getService(name: String?): IEconomyService? {
        if (name.isNullOrEmpty()) return null
        return services[name.lowercase()]
    }

    fun getServiceOrDefault(name: String?): IEconomyService? {
        if (name.isNullOrEmpty()) return services.entries.firstOrNull()?.value
        return services[name.lowercase()] ?: services.entries.firstOrNull()?.value
    }

    fun getServices(): Map<String, IEconomyService> {
        return services.toMap()
    }
}