package com.bandweaver.maxsec.util;

import com.bandweaver.maxsec.entity.MeasObjectVal;
import com.bandweaver.maxsec.entity.Video;

import java.util.ArrayList;
import java.util.List;

public class TypeConvertUtil {

    public static List<MeasObjectVal> convertLObject2LMeasObjectVal(List<Object> objects) {
        List<MeasObjectVal> list = new ArrayList<>();

        for (Object o : objects) {
            list.add((MeasObjectVal)o);
        }

        return list;
    }

    public static List<Video> convertLObject2LVideo(List<Object> objects) {
        List<Video> list = new ArrayList<>();

        for (Object o : objects) {
            list.add((Video)o);
        }

        return list;
    }
}
