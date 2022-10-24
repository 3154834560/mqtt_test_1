package com.example.demo.infrastructure.consts;

import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 王景阳
 * @date 2022/10/23 21:00
 */
public class StringPool {

    public final static ConcurrentHashMap<String, MqttClient> MQTT_CLIENTS = new ConcurrentHashMap<>();

}
