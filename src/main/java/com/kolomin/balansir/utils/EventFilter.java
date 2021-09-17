package com.kolomin.balansir.utils;

import com.kolomin.balansir.Entities.Event;
import com.kolomin.balansir.Repositoeirs.specifications.EventSpecifications;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.kolomin.balansir.Services.AdminService.myFormat;

@Getter
public class EventFilter {
    private Specification<Event> spec;

    public EventFilter(Map<String, String> map) {
        this.spec = Specification.where(null);

        if (map.containsKey("name") && !map.get("name").isEmpty()) {
            String event_name = map.get("name");
            spec = spec.and(EventSpecifications.event_nameLike(event_name));
        }

        if (map.containsKey("city") && !map.get("city").isEmpty()) {
            String event_city = map.get("city");
            spec = spec.and(EventSpecifications.event_cityLike(event_city));
        }

        if (map.containsKey("area") && !map.get("area").isEmpty()) {
            String event_area = map.get("area");
            spec = spec.and(EventSpecifications.event_areaLike(event_area));
        }

        if (map.containsKey("date") && !map.get("date").isEmpty()) {
            Date event_date = new Date();
            try {
                event_date = myFormat.parse(map.get("date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            spec = spec.and(EventSpecifications.event_dateLike(event_date));
        }
    }
}
