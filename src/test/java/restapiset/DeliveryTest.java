package restapiset;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.RestDocsConfiguration;
import java.time.LocalDateTime;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
class DeliveryTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;


  @Test
  void create_Delivery() throws Exception {
    DeliveryDto delivery = DeliveryDto.builder()
        .item("book")
        .user("klom")
        .deliveryTime(LocalDateTime.now())
        .deliveryEndTime(LocalDateTime.now().plusDays(10))
        .build();
    mockMvc.perform(post("/api/delivery/")
        .accept(MediaTypes.HAL_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(delivery))
    )
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("id").value(Matchers.not(10)))
        .andExpect(jsonPath("status").value(DeliveryStatus.READY.name()))
        .andExpect(jsonPath("_links.query-events").exists())
        .andExpect(jsonPath("_links.update-events").exists())
        .andExpect(jsonPath("_links.self").exists())
        .andDo(document("create-events"))
    ;
  }

  @Test
  void create_Delivery_wrong() throws Exception {
    Delivery delivery = Delivery.builder()
        .id(10)
        .item("book")
        .user("klom")
        .deliveryTime(LocalDateTime.now())
        .deliveryEndTime(LocalDateTime.now().plusDays(10))
        .status(DeliveryStatus.MOVE)
        .build();
    mockMvc.perform(post("/api/delivery/")
        .accept(MediaTypes.HAL_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(delivery))
    )
        .andDo(print())
        .andExpect(status().isBadRequest())

    ;
  }

  @Test
  void badRequest_null() throws Exception {
    DeliveryDto delivery = DeliveryDto.builder()
        .build();

    mockMvc.perform(post("/api/delivery/")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(delivery))
    )
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void badRequest_wrong_input() throws Exception {
    DeliveryDto delivery = DeliveryDto.builder()
        .item("book")
        .user("klom")
        .deliveryEndTime(LocalDateTime.now())
        .deliveryTime(LocalDateTime.now().plusDays(10))
        .itemPrice(0)
        .build();

    mockMvc.perform(post("/api/delivery/")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(delivery))
    )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.[0].objectName").exists())
        .andExpect(jsonPath("$.[0].field").exists())
        .andExpect(jsonPath("$.[0].rejectedValue").exists())
      ;
  }


}