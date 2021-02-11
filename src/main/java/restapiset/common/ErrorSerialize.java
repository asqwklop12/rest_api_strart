package restapiset.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

@JsonComponent
public class ErrorSerialize extends JsonSerializer<Errors> {

  @Override
  public void serialize(Errors errors, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {
      jsonGenerator.writeStartArray();
      errors.getFieldErrors().forEach(e -> {
        try {
          jsonGenerator.writeStartObject();
          jsonGenerator.writeStringField("field",e.getField());
          jsonGenerator.writeStringField("objectName",e.getObjectName());
          jsonGenerator.writeStringField("codes",e.getCode());
          Object rejectedValue = e.getRejectedValue();
          if(rejectedValue != null) {
            jsonGenerator.writeStringField("rejectedValue",rejectedValue.toString());
          }
          jsonGenerator.writeEndObject();
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }
      });

      errors.getGlobalErrors().forEach(e -> {
        try {
          jsonGenerator.writeStartObject();
          jsonGenerator.writeStringField("objectName",e.getObjectName());
          jsonGenerator.writeStringField("codes",e.getCode());
          jsonGenerator.writeEndObject();
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }
      });
      jsonGenerator.writeEndArray();
  }
}
