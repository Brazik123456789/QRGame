package com.kolomin.balansir.Repositoeirs.specifications;

import com.kolomin.balansir.Entities.Event;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class EventSpecifications {
    public static Specification<Event> event_nameLike(String event_name){
        return (Specification<Event>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), String.format("%%%s%%", event_name));
    }

    public static Specification<Event> event_cityLike(String event_city){
        return (Specification<Event>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get("city"), String.format("%%%s%%", event_city));
    }

    public static Specification<Event> event_areaLike(String event_area){
        return (Specification<Event>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("area"), event_area);
    }

    public static Specification<Event> event_dateLike(Date event_date){
        return (Specification<Event>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("date"), event_date);
    }
}