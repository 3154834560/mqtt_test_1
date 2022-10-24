package com.example.demo.application.service;

import com.example.demo.config.MqttConfiguration;
import com.example.demo.config.listener.DefaultMessageListener;
import com.example.demo.infrastructure.consts.StringPool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author 王景阳
 * @date 2022/10/23 19:48
 */
@Service
@Slf4j
public class MqttService {

    @Autowired
    MqttConfiguration configuration;

    @Autowired
    MqttConnectOptions connectOptions;

    /**
     * 创建 mqtt 客户端
     */
    public String createMqttClient(String clientId) {
        if (isDuplicate(clientId)) {
            return "用户：" + clientId + "已建立连接";
        }
        MqttClient client = null;
        try {
            String clientName = configuration.getClientName() + "-" + clientId;
            // 配置 mqtt 持久性数据存储位置
            MqttDefaultFilePersistence persistence = new MqttDefaultFilePersistence(configuration.getPersistencePath());
            client = new MqttClient(configuration.getServerUri(), clientName, persistence);
            client.connect(connectOptions);
            initClientSubscribe(client, clientName);
            StringPool.MQTT_CLIENTS.put(clientId, client);
        } catch (MqttException e) {
            log.error(String.format("MQTT: 客户端[%s]连接消息服务器[%s]失败", clientId, configuration.getServerUri()));
            e.printStackTrace();
        }
        return "用户：" + clientId + "建立连接成功";
    }

    /**
     * 删除 mqtt 客户端
     */
    public String closeMqttClient(String clientId) {
        if (!isDuplicate(clientId)) {
            return "用户：" + clientId + "当前未建立连接";
        }
        try {
            MqttClient client = getClient(clientId);
            client.disconnect();
            client.close();
            StringPool.MQTT_CLIENTS.remove(clientId);
        } catch (MqttException e) {
            log.error("删除客户端{}失败", clientId);
            e.printStackTrace();
        }
        return "用户：" + clientId + "关闭连接成功";
    }

    /**
     * 发送消息
     *
     * @param clientId 发送信息的客户端的id
     * @param topic    主题
     * @param data     消息内容
     */
    public void publish(String clientId, String topic, Object data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // 转换消息为json字符串
            byte[] bytes = mapper.writeValueAsBytes(data);
            MqttMessage mqttMessage = new MqttMessage(bytes);
            mqttMessage.setQos(configuration.getQos());
            getClient(clientId).publish(topic, mqttMessage);
        } catch (JsonProcessingException e) {
            log.error(String.format("MQTT: 主题[%s]发送消息转换json失败", topic));
        } catch (MqttException e) {
            log.error(String.format("MQTT: 主题[%s]发送消息失败", topic));
        }
    }

    /**
     * 订阅主题
     *
     * @param clientId 客户端的id
     * @param topic    主题
     */
    public void subscribe(String clientId, String topic) {
        DefaultMessageListener listener = new DefaultMessageListener(configuration.getClientName());
        subscribe(clientId, topic, listener);
    }

    /**
     * 订阅主题
     *
     * @param clientId 客户端的id
     * @param topic    主题
     * @param listener 消息监听处理器
     */
    public void subscribe(String clientId, String topic, IMqttMessageListener listener) {
        try {
            getClient(clientId).subscribe(topic, configuration.getQos(), listener);
        } catch (MqttException e) {
            log.error(String.format("MQTT: 订阅主题[%s]失败", topic));
        }
    }

    /**
     * 取消订阅主题
     *
     * @param clientId 客户端的id
     * @param topic    主题
     */
    public void unsubscribe(String clientId, String topic) {
        try {
            getClient(clientId).unsubscribe(topic);
        } catch (MqttException e) {
            log.error(String.format("MQTT: 订阅主题[%s]失败", topic));
        }
    }

    /**
     * 检查当前客户端是否已建立连接
     */
    private boolean isDuplicate(String clientId) {
        return StringPool.MQTT_CLIENTS.containsKey(clientId);
    }

    /**
     * 获取客户端
     */
    private MqttClient getClient(String clientId) {
        return Optional.ofNullable(StringPool.MQTT_CLIENTS.get(clientId)).orElseThrow(() -> new RuntimeException("用户：" + clientId + "不存在"));
    }

    /**
     * 初始化客户端的订阅
     */
    private void initClientSubscribe(MqttClient client, String clientName) {
        try {
            int topicLen = configuration.getTopic().length;
            IMqttMessageListener[] listeners = new IMqttMessageListener[topicLen];
            int[] qos = new int[topicLen];
            for (int i = 0; i < topicLen; i++) {
                listeners[i] = new DefaultMessageListener(clientName);
                qos[i] = configuration.getQos();
            }
            client.subscribe(configuration.getTopic(), qos, listeners);
        } catch (MqttException e) {
            log.error(String.format("MQTT: 订阅主题 %s 失败", Arrays.toString(configuration.getTopic())));
            e.printStackTrace();
        }
    }
}
