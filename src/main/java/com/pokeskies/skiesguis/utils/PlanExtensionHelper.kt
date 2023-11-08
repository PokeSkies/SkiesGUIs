package com.pokeskies.skiesguis.utils

import com.djrapitops.plan.query.QueryService
import java.util.*


object PlanExtensionHelper {
    private var queryService: QueryService? = null

    init {
        try {
            queryService = QueryService.getInstance()
        } catch (e: IllegalStateException) {
            Utils.printError("The Plan mod was not found or enabled! Integrations may not work right... ${e.printStackTrace()}")
        }
    }

    fun getPlaytime(uuid: UUID): Long {
        if (queryService == null) {
            Utils.printError("Plan integrations were not enabled correctly! Please check the logs for startup errors...")
            return 0
        }

        var playtime: Long = 0
        for (serverUUID in queryService!!.commonQueries.fetchServerUUIDs()) {
            playtime += queryService!!.commonQueries.fetchPlaytime(uuid, serverUUID, 0, Long.MAX_VALUE)
        }

        return playtime / 1000
    }
}