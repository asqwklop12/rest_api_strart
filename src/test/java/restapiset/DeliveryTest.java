package restapiset;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class DeliveryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Test
    void create_Delivery() throws Exception {
        Delivery delivery = Delivery.builder()
                .id(10)
                .item("book")
                .user("klom")
                .build();
        mockMvc.perform(post("/api/delivery/")
                    .content(objectMapper.writeValueAsString(delivery))
                )
               .andDo(print())
               .andExpect(status().isCreated());
    }
}