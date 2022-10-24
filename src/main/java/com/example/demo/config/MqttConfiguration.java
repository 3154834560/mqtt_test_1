package com.example.demo.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * @author 王景阳
 * @date 2022/10/22 14:13
 */
@Configuration
@Getter
@Slf4j
public class MqttConfiguration {

    @Value("${mqtt.protocol:tcp}")
    private String protocol;

    @Value("${mqtt.url:}")
    private String serverUri;

    @Value("${mqtt.host:localhost}")
    private String host;

    @Value("${mqtt.port:8083}")
    private Integer port;

    @Value("${mqtt.username:admin}")
    private String username;

    @Value("${mqtt.password:guest}")
    private String password;

    @Value("${mqtt.client.name:client}")
    private String clientName;

    @Value("${mqtt.default.subscribe.topic:}")
    private String[] topic;

    @Value("${mqtt.connect.timeout:10}")
    private Integer connectTimeout;

    @Value("${mqtt.keep.alive.interval:10}")
    private Integer keepAliveInterval;

    @Value("${mqtt.qos:2}")
    private Integer qos;

    @Value("${mqtt.default.file.persistence.path}")
    private String persistencePath;

    @Value("${mqtt.clean.session:true}")
    private boolean cleanSession;

    @Value("${mqtt.auto.reconnect:true}")
    private boolean autoReconnect;

    @PostConstruct
    public void initUri() {
        if (!StringUtils.hasText(serverUri)) {
            serverUri = protocol + "://" + host + ":" + port;
        }
        if (!StringUtils.hasText(persistencePath)) {
            persistencePath = System.getProperty("user.dir") + "/data/mqClient";
        }
    }

    /**
     * MQTT连接器选项
     */

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        // 设置连接的地址
        mqttConnectOptions.setServerURIs(new String[]{serverUri});
        // 设置连接的用户名
        mqttConnectOptions.setUserName(username);
        // 设置连接的密码
        mqttConnectOptions.setPassword(password.toCharArray());
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
        // 把配置里的 cleanSession 设为false，客户端掉线后 服务器端不会清除session，
        // 当重连后可以接收之前订阅主题的消息。当客户端上线后会接受到它离线的这段时间的消息
        mqttConnectOptions.setCleanSession(cleanSession);
        // 设置超时时间 单位为秒
        mqttConnectOptions.setConnectionTimeout(connectTimeout);
        //设置自动重新连接
        mqttConnectOptions.setAutomaticReconnect(autoReconnect);
        // 设置会话心跳时间 单位为秒 服务器会每隔10秒的时间向客户端发送心跳判断客户端是否在线
        // 但这个方法并没有重连的机制
        mqttConnectOptions.setKeepAliveInterval(keepAliveInterval);
        // 设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息。
        mqttConnectOptions.setWill("willTopic", "WILL_DATA".getBytes(), 2, false);
        return mqttConnectOptions;
    }

}
