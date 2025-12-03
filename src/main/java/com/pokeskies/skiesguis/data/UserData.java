package com.pokeskies.skiesguis.data;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.HashMap;
import java.util.UUID;

public class UserData {
    @BsonProperty("_id")
    public UUID uuid;

    public HashMap<String, MetadataValue> metdadata;

    public UserData(UUID uuid) {
        this.uuid = uuid;
        this.metdadata = new HashMap<>();
    }

    public UserData(UUID uuid, HashMap<String, MetadataValue> metdadata) {
        this.uuid = uuid;
        this.metdadata = metdadata;
    }

    public UserData() {}

    @Override
    public String toString() {
        return "UserData{" +
                "uuid=" + uuid +
                ", metdadata=" + metdadata +
                '}';
    }
}
