package com.app.exptracker.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class ExcelDataModelDateCompDesc implements Comparator<ExcelDataModel> {

    @Override
    public int compare(ExcelDataModel e1, ExcelDataModel e2) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date o1PublishDate = null;
        Date o2PublishDate = null;
        try {
            o1PublishDate = formatter.parse(e1.getDate());
            o2PublishDate = formatter.parse(e2.getDate());
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return o2PublishDate.compareTo(o1PublishDate);
    }
}
