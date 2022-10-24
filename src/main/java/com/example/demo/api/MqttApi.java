package com.example.demo.api;


import com.example.demo.application.service.MqttService;
import com.example.demo.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @author 王景阳
 * @date 2022/10/22 14:15
 */
@RestController
@RequestMapping("/mqtt")
public class MqttApi {

    @Autowired
    MqttService service;

    @GetMapping("/create/client/{id}")
    public String create(@PathVariable("id") String id) {
        return service.createMqttClient(id);
    }

    @GetMapping("/close/client/{id}")
    public String close(@PathVariable("id") String id) {
        return service.closeMqttClient(id);
    }

    @GetMapping("/publish/{clientId}/{topic}/{message}")
    public boolean publish(@PathVariable("clientId") String clientId, @PathVariable("topic") String topic, @PathVariable("message") String message) {
        service.publish(clientId, topic, new User("sss", message, LocalDateTime.now()));
        return true;
    }

    @GetMapping("/subscribe/{clientId}/{topic}")
    public boolean subscribe(@PathVariable("clientId") String clientId, @PathVariable("topic") String topic) {
        service.subscribe(clientId, topic);
        return true;
    }

    @GetMapping("/unsubscribe/{clientId}/{topic}")
    public boolean unsubscribe(@PathVariable("clientId") String clientId, @PathVariable("topic") String topic) {
        service.unsubscribe(clientId, topic);
        return true;
    }
}
