package com.nowcoder.async;

import java.util.HashMap;
import java.util.Map;

public class EventModel { // 事件model
    private EventType eventType; // 事件类型

    private int actorId; // 事件的触发者

    private int entityId; // 事件的id

    private int entityType; // 事件的类型

    private int entityOwnerId; // 事件的拥有者

    private Map<String, String> exts = new HashMap<String, String>(); // 事件的额外信息

    public EventModel(){

    }

    public EventModel(EventType type){
        this.eventType = type;
    }

    public String getExt(String name) {
        return exts.get(name);
    }

    public void setExt(String name, String value) {
        exts.put(name, value);
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public int getActorId() {
        return actorId;
    }

    public void setActorId(int actorId) {
        this.actorId = actorId;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public void setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public void setExts(Map<String, String> exts) {
        this.exts = exts;
    }
}
