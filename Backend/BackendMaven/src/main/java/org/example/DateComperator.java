package org.example;

import java.time.LocalDate;
import java.util.Comparator;

public class DateComperator implements Comparator<LocalDate> {
    @Override
    public int compare(LocalDate o1, LocalDate o2) {
        if (o1.isEqual(o2)){
            return 0;
        } else if (o1.isBefore(o2)) {
            return -1;
        }
        return 1;

    }
}
