//------------------------------------------------------------------------------
package com.matalok.looprider;

//------------------------------------------------------------------------------
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

//------------------------------------------------------------------------------
public class GsonUtils {
    //**************************************************************************
    // GsonUtils
    //**************************************************************************
    public static Gson gson = new Gson();
    public static Gson gson_pretty = new GsonBuilder().setPrettyPrinting().create();

    //--------------------------------------------------------------------------
    public static String Serialize(Object obj, boolean pretty) {
        return pretty ? gson_pretty.toJson(obj) : gson.toJson(obj);
    }

    //--------------------------------------------------------------------------
    public static Object Deserialize(String json_str, Class<?> obj_class) {
        Object obj = null;
        try {
            obj = gson.fromJson(json_str, obj_class);
        } catch(JsonSyntaxException ex) {
            Utils.Log("Failed to deserialize JSON :: class=%s", obj_class.toString());
        }
        return obj;
    }
}
