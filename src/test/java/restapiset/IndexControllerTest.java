package restapiset;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import common.BaseControllerTest;
import org.junit.jupiter.api.Test;


public class IndexControllerTest extends BaseControllerTest {


  @Test
  void index() throws Exception {
    mockMvc.perform(get("/delivery"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("_links.delivery").exists());
  }

}
