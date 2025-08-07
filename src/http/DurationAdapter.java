package http;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter writer, Duration duration) throws IOException {
        if (duration == null) {
            writer.nullValue();
        } else {
            writer.value(duration.toMinutes());
        }
    }

    @Override
    public Duration read(JsonReader reader) throws IOException {
        try {
            int minutes = reader.nextInt();
            return Duration.ofMinutes(minutes);
        } catch (Exception e) {
            throw new IOException("Не удалось распарсить Duration", e);
        }
    }
}
