package ubg4;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by Christoph Stumpe on 20.06.2016.
 **/
public class Serializer implements JsonSerializer<Answer> {

    public JsonElement serialize(Answer answer, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject o = new JsonObject();
        Gson g = new Gson();
        String s = g.toJson(answer);
        o.addProperty("res",s);
        return o;
    }
}
