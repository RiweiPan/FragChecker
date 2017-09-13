package org.cityu.mbos.fragchecker.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Hubery on 2017/7/27.
 */

public class AppClassifier {

    private static final Set<String> APPS = new HashSet<>();

    static {

        APPS.add("com.android.chrome");
        APPS.add("com.google.android.youtube");
        APPS.add("com.facebook");
        APPS.add("com.twitter");
        APPS.add("com.tencent.mm");
        APPS.add("com.google.android.gm");
        APPS.add("com.google.earth");
        APPS.add("com.instagram");
        APPS.add("com.atomicadd.fotos");
        //APPS.add("com.facebook");
        APPS.add("dalvik-cache");

    }

    public static boolean classify(String path){
        for(String app : APPS){
            if(path.contains(app)){
                return true;
            }
        }
        return false;
    }


}
