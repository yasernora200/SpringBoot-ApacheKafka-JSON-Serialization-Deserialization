package com.nora.consumer;

import com.nora.dto.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaMessageListener {

    Logger log = LoggerFactory.getLogger(KafkaMessageListener.class);

    @KafkaListener(topics = "students",groupId = "stud-group")
    public void consumeEvents(List<Customer> customer) {
        log.info("consumer consume the events {} ", customer.toString());
    }

//    @KafkaListener(topics = "employees",groupId = "emp-group")
//    public void consume2(Customer message) {
//        log.info("consumer2 consume the message {} ", message);
//    }
//
//    @KafkaListener(topics = "employees",groupId = "emp-group")
//    public void consume3(Customer message) {
//        log.info("consumer3 consume the message {} ", message);
//    }
//
//    @KafkaListener(topics = "employees",groupId = "emp-group")
//    public void consume4(Customer message) {
//        log.info("consumer4 consume the message {} ", message);
//    }
}
