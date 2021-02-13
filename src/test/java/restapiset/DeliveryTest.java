package restapiset;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
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
    void badRequest_empty_entity() throws Exception {
        DeliveryDto delivery = DeliveryDto.builder()
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
    void badRequest() throws Exception {
        DeliveryDto delivery = DeliveryDto.builder()
            .item("book")
            .user("klom")
            .deliveryTime(LocalDateTime.now())
            .deliveryEndTime(LocalDateTime.now().plusDays(10))
            .itemPrice(0)
            .build();
        mockMvc.perform(post("/api/delivery/")
            .accept(MediaTypes.HAL_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(delivery))
        )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].field").exists())
            .andExpect(jsonPath("$.[0].objectName").exists())
            .andExpect(jsonPath("$.[0].code").exists())
            .andExpect(jsonPath("$.[0].rejectedValue").exists())
        ;
    }

}