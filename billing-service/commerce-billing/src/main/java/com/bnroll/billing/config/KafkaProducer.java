package com.bnroll.billing.config;


import com.bnroll.commercedomain.event.property.FacilityCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private static final String FACILITY_CREATED_TOPIC = "facility-created";
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void facilityCreationEvent(FacilityCreatedEvent event) {

        kafkaTemplate.send(
                FACILITY_CREATED_TOPIC,
                event
        );
    }


}