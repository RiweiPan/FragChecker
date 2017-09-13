package org.cityu.mbos.fragchecker.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Hubery on 2017/6/29.
 */

public class TypeClassifier {

    private static final Set<String> SQLITETYPE = new HashSet<>();
    private static final Set<String> MEDIATYPE = new HashSet<>();
    private static final Set<String> EXECUTABLETYPE = new HashSet<>();

    static {

        SQLITETYPE.add("DB");
        SQLITETYPE.add("DB-JOURNAL");
        SQLITETYPE.add("DB-WAL");

        MEDIATYPE.add("JPG");
        MEDIATYPE.add("CNT");
        MEDIATYPE.add("MP4");
        MEDIATYPE.add("MP3");
        MEDIATYPE.add("BMP");
        MEDIATYPE.add("PNG");


        EXECUTABLETYPE.add("ODEX");
        EXECUTABLETYPE.add("DEX");
        EXECUTABLETYPE.add("APK");
        EXECUTABLETYPE.add("SO");

    }

    public static FileType getFileType(String suffix){

        String upSuffix = suffix.toUpperCase();

        if(SQLITETYPE.contains(upSuffix)){
            return FileType.SQLITE;
        }else if(EXECUTABLETYPE.contains(upSuffix)){
            return FileType.EXECUTABLE;
        }else if(MEDIATYPE.contains(upSuffix)){
            return FileType.MEDIA;
        }else {
            return FileType.UNKNOWN;
        }

    }



    public enum FileType{

        SQLITE(1,"DB"), MEDIA(2,"MEDIA"), EXECUTABLE(3,"EXECUTABLE"), UNKNOWN(4,"OTHERS"), ALL(5,"ALL");

        String name;

        int id;

        FileType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public static String getNameById(int id){
            FileType ret = null;
            switch (id){
                case 1 :
                    ret = SQLITE;
                    break;
                case 2 :
                    ret = MEDIA;
                    break;
                case 3:
                    ret = EXECUTABLE;
                    break;
                default:
                    ret = UNKNOWN;
                    break;
            }
            return ret.getName();
        }

    }



}
