package http;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(dtf.format(localDateTime));
        }
    }

    @Override
    public LocalDateTime read(JsonReader reader) throws IOException {
        try {
            return LocalDateTime.parse(reader.nextString(), dtf);
        } catch (Exception e) {
            throw new JsonParseException("Не удалось распарсить дату: " + reader.nextString(), e);
        }
    }
}
