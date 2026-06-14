package hansanhha.slice;

import hansanhha.AuthController;
import hansanhha.slice.SecurityTestConfig.CustomSecurityContextFactory;
import hansanhha.slice.SecurityTestConfig.WithMockCustomUser;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(AuthController.class)
@Import(SecurityTestConfig.class)
public class SpringSecuritySliceTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private MockMvcTester mvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        mvc = MockMvcTester.create(mockMvc);
    }

    @Test
    @WithMockUser(roles = "admin")
    @DisplayName("admin 권한을 가진 mock 사용자는 보호된 리소스에 접근할 수 있다")
    void mockAdminUserShouldAccessProtectedResource() {

        assertThat(mvc.get().uri("/auth"))
                .hasStatusOk()
                .bodyText().contains("hello");
    }

    @Test
    @WithAnonymousUser
    @DisplayName("권한이 없는 사용자는 보호된 리소스에 접근할 수 없다")
    void normalUserCannotAccessProtectedResource() {

        assertThat(mvc.get().uri("/auth"))
                .hasStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @WithUserDetails(value = "test username", userDetailsServiceBeanName = "simpleUserDetailsService")
    @DisplayName("사용자 정보를 가진 사용자는 보호된 리소스에 접근할 수 있다")
    void userDetailsUserShouldAccessProtectedResource() {

        assertThat(mvc.get().uri("/auth"))
                .hasStatusOk()
                .bodyText().contains("hello");
    }

    @Test
    @WithMockCustomUser(username = "test username", role = "admin")
    @DisplayName("사용자 정보를 가진 사용자는 보호된 리소스에 접근할 수 있다")
    void customSecurityContextUserShouldAccessProtectedResource() {

        assertThat(mvc.get().uri("/auth"))
                .hasStatusOk()
                .bodyText().contains("hello");
    }

}
