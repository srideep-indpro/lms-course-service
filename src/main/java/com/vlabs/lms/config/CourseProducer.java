package com.vlabs.lms.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class CourseProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseProducer.class);
    private NewTopic topic;

    private KafkaTemplate<String, CourseEvent> kafkaTemplate;

    public CourseProducer(NewTopic topic, KafkaTemplate<String, CourseEvent> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(CourseEvent event){
        LOGGER.info(String.format("Course event => %s", event.toString()));

        // create Message
        Message<CourseEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();
        kafkaTemplate.send(message);
    }
}
