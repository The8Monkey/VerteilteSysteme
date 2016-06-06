package ubg4;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import jdk.nashorn.internal.runtime.JSONListAdapter;

import java.lang.reflect.Array;

/**
 * Created by nieli on 30-May-16.
 */
public class JSONConverter {

    public JsonArray toJSONArray(String[] a){
        JsonArray jsonArray = new JsonArray();
        for(String s: a){
            jsonArray.add(new JsonPrimitive(s));
        }
        return jsonArray;
    }

    public String[] toArray(JsonArray jsonArray){
        if(jsonArray==null) {
            return null;
        }
        String[] stringArray = new String[jsonArray.size()];
        for(int i=0; i<stringArray.length; i++){
            stringArray[i]= jsonArray.get(i).toString();
        }
        return stringArray;
    }



}
