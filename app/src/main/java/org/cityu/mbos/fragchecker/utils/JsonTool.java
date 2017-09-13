package org.cityu.mbos.fragchecker.utils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @className JsonTool
 * @description Json简单解析工具，不能遍历数组的嵌套，可能有部分bug
 * @author 潘日维
 * @version V1.0
 * @date 2016年11月10日
 */
public class JsonTool {

    /**
     * 处理取出数据类型为<b>非数组</b>类型的各种Json格式的数据如<br/>
     * {"userbean":<br/>{"Uid":"100196",<br/>"Showname":"你好",<br/>"Avtar":null,<br/>"State":1}<br/>}<br/>
     * 使用样例: getNodeValue(json, "userbean","Uid") 结果为100196
     * @param json json格式的文件
     * @param keys 可变参数，直接处理json格式路径
     * @return String 从json格式字符串中取出的值
     */
    public static String getJsonValue(String json, String... keys){
        Object ret = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            for(int i = 0; i < keys.length - 1; i++){
                jsonObject = jsonObject.getJSONObject(keys[i]);
            }
            ret = jsonObject.get(keys[keys.length-1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    /**
     * 处理取出类型为<b>数组<b/>类型的Json格式字符串如<br/>
     * {"userbean":<br/>{"Uid":"100196",<br/>"Showname":"你好",<br/>"Avtar":null,<br/>"State":[1,2,3,4]}<br/>}<br/>
     * getNodeArray(c,"userbean","State")[1] 的结果为 2
     * @param json json格式的文件
     * @param keys 可变参数，直接处理json格式路径
     * @return String[] 返回json数组值的列表
     */
    public static String[] getJsonArray(String json, String... keys){
        String[] strs = null;
        String j1 = getJsonValue(json,keys);
        try {
            JSONArray jsonArray = new JSONArray(j1);
            strs = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                strs[i] = jsonArray.get(i).toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return strs;
    }

    /**
     * 将一个Pojo结构类，转换为Json型的字符串，使用如:<br/>
     * classToJson(new PojoPerson("Jack","age"))
     * @param obj 传入的需要转换为Json的实例对象
     * @return String 转换为Json格式的字符串
     */
    public static String classToJson(Object obj){
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /**
     * 将一个Json格式的字符串，初始化一个同样数据结构的实例对象，使用如:<br/>
     * jsonToClass(json, PojoPerson.class)
     * @param json Json格式字符串
     * @param Pojo 结构相同的class对象
     * @return 根据json的数据初始化的实例对象
     */
    public static <T> T jsonToClass(String json, Class<T> Pojo){
        Gson gson = new Gson();
        return gson.fromJson(json, Pojo);
    }



}
