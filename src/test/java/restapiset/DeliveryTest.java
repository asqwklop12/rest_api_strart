package restapiset;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.RestDocsConfiguration;
import java.time.LocalDateTime;
import java.util.stream.IntStream;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
class DeliveryTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private DeliveryRepository deliveryRepository;

  @Test
  void create_Delivery() throws Exception {
    DeliveryDto delivery = DeliveryDto.builder()
        .item("book")
        .user("klom")
        .itemPrice(1000)
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
        .andDo(document("index",
            links(
                linkWithRel("query-events").description("query events"),
                linkWithRel("update-events").description("update events"),
                linkWithRel("self").description("self events"),
                linkWithRel("profile").description("profile")
            ),
            requestHeaders(
                headerWithName(HttpHeaders.CONTENT_TYPE).description("ContentType is application"),
                headerWithName(HttpHeaders.ACCEPT).description("Accept is application/hal+json"),
                headerWithName(HttpHeaders.CONTENT_LENGTH).description("137")),
            requestFields(
                fieldWithPath("item").description("what is your item"),
                fieldWithPath("user").description("how is made"),
                fieldWithPath("deliveryTime").description("start time"),
                fieldWithPath("deliveryEndTime").description("end time"),
                fieldWithPath("itemPrice").description("item price is")),
            responseHeaders(
                headerWithName(HttpHeaders.LOCATION).description("Location!"),
                headerWithName(HttpHeaders.CONTENT_TYPE)
                    .description("Content-Type is application/hal+json")),
            responseFields(fieldWithPath("id").description("this item id"),
                fieldWithPath("item").description("what is your item"),
                fieldWithPath("user").description("what your name"),
                fieldWithPath("deliveryTime").description("start time"),
                fieldWithPath("deliveryEndTime").description("end time"),
                fieldWithPath("status").description("current status"),
                fieldWithPath("itemPrice").description("item price is "),
                fieldWithPath("deliveryCost").description("delivery cost"),
                fieldWithPath("_links.*.*").description("link information")))
        )
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
        .andExpect(jsonPath("errors[0].objectName").exists())
        .andExpect(jsonPath("errors[0].field").exists())
        .andExpect(jsonPath("errors[0].rejectedValue").exists())
        .andExpect(jsonPath("_links.index").exists())
    ;
  }

  @Test
  void eventQuery() throws Exception {
    IntStream.range(0, 30)
        .forEach(this::generateEvent);
    mockMvc.perform(get("/api/delivery")
        .param("page", "2")
        .param("size", "10")
        .param("sort", "item,DESC"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("page").exists())
        .andDo(document("query-events",
            links(linkWithRel("first").description("첫번째 페이지"),
                linkWithRel("last").description("마지막 페이지"),
                linkWithRel("prev").description("이전 페이지"),
                linkWithRel("self").description("현재 페이지")),
            requestParameters(parameterWithName("page").description("현재 페이지"),
                parameterWithName("size").description("가져오는 데이터 수"),
                parameterWithName("sort").description("정렬되는 방법")),
            responseFields(fieldWithPath("page.size").description("페이지 크기"),
            fieldWithPath("page.totalElements").description("전체 갯수"),
            fieldWithPath("page.totalPages").description("전체 페이지"),
            fieldWithPath("page.number").description("현재 페이지"),
            fieldWithPath("_links.first.href").description("첫번째 링크"),
            fieldWithPath("_links.prev.href").description("이전 링크"),
            fieldWithPath("_links.self.href").description("현재 링크"),
            fieldWithPath("_links.last.href").description("마지막 링크"),
            fieldWithPath("_embedded.deliveryList[].id").description("인덱스"),
            fieldWithPath("_embedded.deliveryList[].item").description("상품"),
            fieldWithPath("_embedded.deliveryList[].user").description("판매인"),
            fieldWithPath("_embedded.deliveryList[].deliveryTime").description("배송일"),
            fieldWithPath("_embedded.deliveryList[].deliveryEndTime").description("도착 예상일"),
            fieldWithPath("_embedded.deliveryList[].status").description("배송 상태"),
            fieldWithPath("_embedded.deliveryList[].itemPrice").description("물품 가격"),
            fieldWithPath("_embedded.deliveryList[].deliveryCost").description("배달 비용"),
            fieldWithPath("_embedded.deliveryList[]._links.query-events.href").description("목록으로"),
            fieldWithPath("_embedded.deliveryList[]._links.update-events.href").description("업데이트하기"),
            fieldWithPath("_embedded.deliveryList[]._links.self.href").description("현재 이벤트"),
            fieldWithPath("_embedded.deliveryList[]._links.profile.href").description("현재 작업")
            )
        ));
  }

  private void generateEvent(int index) {
    Delivery delivery = Delivery.builder()
        .item("Delivery" + index)
        .user("klom")
        .build();
    deliveryRepository.save(delivery);
  }
}