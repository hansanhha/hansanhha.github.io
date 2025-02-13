package hansanhha.slice;

import hansanhha.GreetingController;
import hansanhha.GreetingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doReturn;


public class WebSliceTest {

    @Nested
    @WebMvcTest(value = GreetingController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
    class MockMvcTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private GreetingService greetingService;

        @Test
        @DisplayName("@WebMvcTest와 mockMvc를 통해 웹 계층 테스트를 수행한다")
        void greetingShouldReturnMessageFromService() throws Exception {

            doReturn("hello web layer test by MockMvc")
                    .when(greetingService)
                    .greet();

            mockMvc.perform(MockMvcRequestBuilders.get("/greeting"))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string(containsString("hello web layer test by MockMvc")));
        }
    }


    @Nested
    @WebMvcTest(value = GreetingController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
    class MockMvcTesterTest {

        @Autowired
        private MockMvcTester mvc;

        @MockitoBean
        private GreetingService greetingService;

        @Test
        @DisplayName("@WebMvcTest와 MockMvcTester를 통해 웹 계층 테스트를 수행한다")
        void greetingShouldReturnMessageFromService() {

            doReturn("hello web layer test by MockMvcTester")
                    .when(greetingService)
                    .greet();

            assertThat(mvc.get().uri("/greeting").accept(MediaType.APPLICATION_JSON_VALUE))
                    .hasStatusOk()
                    .hasBodyTextEqualTo("hello web layer test by MockMvcTester");
        }


    }




}
