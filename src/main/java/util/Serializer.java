package util;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Serializer {
    public static void serialize(Object obj, OutputStream outputStream) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        ( new Gson()).toJson(obj, obj.getClass(), outputStreamWriter);
        outputStreamWriter.close();
    }

    public static <T> T deserialize(Class<T> clazz, InputStream inputStream) {
        return (new Gson()).fromJson(new InputStreamReader(inputStream), clazz);
    }
}
