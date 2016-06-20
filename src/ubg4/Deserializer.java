package ubg4;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

/**
 * Created by Christoph Stumpe on 20.06.2016.
 */
public class Deserializer {
    public String deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
        JsonObject jobject = (JsonObject) json;
        return jobject.get("req").getAsString();
    }
}
