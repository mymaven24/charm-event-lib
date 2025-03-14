package com.swwx.charm.event.lib.util;

/**
 * Created by whl on 12/4/16.
 */
public class QueueNameUtils {

    public static String getQueueName(String eventName) {
        return eventName + "_QUEUE";
    }

    public static String getAckQueueName(String eventName) {
        return eventName + "_ACK_QUEUE";
    }
}
