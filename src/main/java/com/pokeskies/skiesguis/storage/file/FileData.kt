package com.pokeskies.skiesguis.storage.file

import com.pokeskies.skiesguis.data.UserData
import java.util.*

class FileData {
    var userdata: HashMap<UUID, UserData> = HashMap()
    override fun toString(): String {
        return "FileData(userdata=$userdata)"
    }
}
