package restapiset;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.BaseControllerTest;
import java.time.LocalDateTime;
import java.util.stream.IntStream;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


class DeliveryTest extends BaseControllerTest {


  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private DeliveryRepository deliveryRepository;
  @Autowired
  private ModelMapper modelMapper;

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
                linkWithRel("next").description("다음 페이지"),
                linkWithRel("self").description("현재 페이지"),
                linkWithRel("profile").description("현재 링크")),
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
                fieldWithPath("_links.next.href").description("다음 링크"),
                fieldWithPath("_links.profile.href").description("현재 링크로"),
                fieldWithPath("_embedded.deliveryList[].id").description("인덱스"),
                fieldWithPath("_embedded.deliveryList[].item").description("상품"),
                fieldWithPath("_embedded.deliveryList[].user").description("판매인"),
                fieldWithPath("_embedded.deliveryList[].deliveryTime").description("배송일"),
                fieldWithPath("_embedded.deliveryList[].deliveryEndTime").description("도착 예상일"),
                fieldWithPath("_embedded.deliveryList[].status").description("배송 상태"),
                fieldWithPath("_embedded.deliveryList[].itemPrice").description("물품 가격"),
                fieldWithPath("_embedded.deliveryList[].deliveryCost").description("배달 비용"),
                fieldWithPath("_embedded.deliveryList[]._links.query-events.href")
                    .description("목록으로"),
                fieldWithPath("_embedded.deliveryList[]._links.update-events.href")
                    .description("업데이트하기"),
                fieldWithPath("_embedded.deliveryList[]._links.self.href").description("현재 이벤트")
            )
        ));
  }

  @Test
  public void create_event() throws Exception {
    Delivery delivery = generateEvent(10);
    this.mockMvc.perform(get("/api/delivery/{id}", delivery.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").exists())
        .andExpect(jsonPath("_links.self").exists())
        .andExpect(jsonPath("_links.profile").exists())
        .andDo(document("get-event",
            links(linkWithRel("query-events").description("이벤트 생성"),
                linkWithRel("update-events").description("이벤트 수정"),
                linkWithRel("self").description("해당 이벤트로 이동"),
                linkWithRel("profile").description("해당 이벤트로 이동")),
            responseHeaders(headerWithName(HttpHeaders.CONTENT_TYPE).description("현재 contentType")),
            responseFields(fieldWithPath("id").description("현재 이벤트 아이디"),
                fieldWithPath("item").description("상품명"),
                fieldWithPath("user").description("판매자"),
                fieldWithPath("deliveryTime").description("출발 시간"),
                fieldWithPath("deliveryEndTime").description("도착 시간"),
                fieldWithPath("status").description("베송 상태"),
                fieldWithPath("itemPrice").description("물품 가격"),
                fieldWithPath("deliveryCost").description("배송가격"),
                fieldWithPath("_links.query-events.href").description("이벤트 링크로 가기"),
                fieldWithPath("_links.update-events.href").description("이벤트 수정 링크 이동"),
                fieldWithPath("_links.self.href").description("해당 링크로 이동하기"),
                fieldWithPath("_links.profile.href").description("이벤트 프로파일 링크이동하기"))));
  }

  @Test
  void update_event() throws Exception {
    Delivery delivery = generateEvent(100);
    DeliveryDto deliveryDto = modelMapper.map(delivery, DeliveryDto.class);
    deliveryDto.setItem("book");
    deliveryDto.setUser("klom2");
    deliveryDto.setItemPrice(1000);
    deliveryDto.setDeliveryTime(LocalDateTime.now());
    deliveryDto.setDeliveryEndTime(LocalDateTime.now().plusDays(10));
    this.mockMvc.perform(put("/api/delivery/{id}", delivery.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(deliveryDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").exists())
        .andExpect(jsonPath("user").value("klom2"))
        .andExpect(jsonPath("_links.self").exists())
        .andExpect(jsonPath("_links.profile").exists())
        .andDo(document("update-event",
            links(linkWithRel("query-events").description("이벤트 생성페이지로"),
                linkWithRel("update-events").description("이벤트 수정페이지로"),
                linkWithRel("self").description("해당 이벤트로 이동페이지로"),
                linkWithRel("profile").description("해당 이벤트로 이동페이지로")),
            requestHeaders(headerWithName(HttpHeaders.CONTENT_TYPE).description("현재 contentType"),
                headerWithName(HttpHeaders.CONTENT_LENGTH).description("content 길이")),
            requestFields(fieldWithPath("item").description("상품명"),
                fieldWithPath("user").description("판매자"),
                fieldWithPath("deliveryTime").description("판매일"),
                fieldWithPath("deliveryEndTime").description("도착일"),
                fieldWithPath("itemPrice").description("상품가격")),

            responseHeaders(
                headerWithName(HttpHeaders.CONTENT_TYPE).description("현재 사용하고 있는 contentType")),
            responseFields(fieldWithPath("id").description("this item id"),
                fieldWithPath("item").description("바꿀 상품명"),
                fieldWithPath("user").description("바꿀 유저이름"),
                fieldWithPath("deliveryTime").description("출발 시간"),
                fieldWithPath("deliveryEndTime").description("도착 시간"),
                fieldWithPath("status").description("현재 상품 상태"),
                fieldWithPath("itemPrice").description("상품 가격"),
                fieldWithPath("deliveryCost").description("배송비"),
                fieldWithPath("_links.query-events.href").description("목록페이지로"),
                fieldWithPath("_links.update-events.href").description("수정페이지로"),
                fieldWithPath("_links.self.href").description("현재페이지로"),
                fieldWithPath("_links.profile.href").description("현재 링크로 이동"))));
  }

  @Test
  void update_event_badEmpty() throws Exception {
    Delivery delivery = generateEvent(100);
    DeliveryDto deliveryDto = new DeliveryDto();
    this.mockMvc.perform(put("/api/delivery/{id}", delivery.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(deliveryDto)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_event_badWrong() throws Exception {
    Delivery delivery = generateEvent(100);
    DeliveryDto deliveryDto = modelMapper.map(delivery, DeliveryDto.class);
    deliveryDto.setDeliveryTime(LocalDateTime.now().plusDays(10));
    deliveryDto.setDeliveryEndTime(LocalDateTime.now());
    this.mockMvc.perform(put("/api/delivery/{id}", delivery.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(deliveryDto)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  public void update_event_notFound() throws Exception {
    DeliveryDto deliveryDto = new DeliveryDto();
    this.mockMvc.perform(put("/api/delivery/1223123")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(deliveryDto)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }


  @Test
  public void create_event_bad() throws Exception {
    this.mockMvc.perform(get("/api/delivery/9999999"))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  private Delivery generateEvent(int index) {
    Delivery delivery = Delivery.builder()
        .item("Delivery" + index)
        .user("klom")
        .build();
    return deliveryRepository.save(delivery);
  }
}