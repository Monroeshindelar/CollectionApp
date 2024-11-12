package com.mshindelar.collection.db.filter;

import com.mshindelar.collection.db.filter.collection.ObjectFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Filter {
    private String field;
    private FilterOperator operator;
    private Object value;
    private Object secondaryValue;
    private ObjectFormat format;
    private Class entity;

    public Object getValue() {
        return convertValue(this.value);
    }

    public Object getSecondaryValue() {
        return convertValue(this.secondaryValue);
    }

    private Object convertValue(Object value) {
        switch (this.getFormat()) {
            case DATE -> value = stringToDate((String) value);
        }

        return value;
    }

    private Timestamp stringToDate(String dateString) {
        try {
            return Timestamp.from(new SimpleDateFormat("yyyy-MM-dd")
                    .parse(dateString)
                    .toInstant());
        } catch (ParseException e) {
            return Timestamp.from(Instant.EPOCH);
        }
    }
}
