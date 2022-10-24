package com.example.demo.config.listener;

import com.example.demo.domain.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;

/**
 * @author 王景阳
 * @date 2022/10/23 19:25
 */
@Slf4j
public class DefaultMessageListener implements IMqttMessageListener {

    private final String clientName;

    public DefaultMessageListener(String clientName) {
        this.clientName = clientName;
    }

    /**
     * 处理消息
     *
     * @param topic       主题
     * @param mqttMessage 消息
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info(String.format("MQTT[" + clientName + "] 消息[" + mqttMessage.getId() + "]: 订阅主题[%s]发来消息[%s]", topic, mapper.readValue(mqttMessage.getPayload(), User.class)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}