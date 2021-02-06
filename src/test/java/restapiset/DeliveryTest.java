package restapiset;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class DeliveryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeliveryRepository deliveryRepository;

    @Test
    void create_Delivery() throws Exception {
        Delivery delivery = Delivery.builder()
                .item("book")
                .user("klom")
                .build();
        delivery.setId(10);
        Mockito.when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);
        mockMvc.perform(post("/api/delivery/")
                    .accept(MediaTypes.HAL_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(delivery))
                )
               .andDo(print())
               .andExpect(status().isCreated());
    }
}