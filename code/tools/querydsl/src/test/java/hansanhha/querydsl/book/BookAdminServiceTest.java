package hansanhha.querydsl.book;

import hansanhha.querydsl.book.dto.BookResponse;
import hansanhha.querydsl.book.dto.CreateBookRequest;
import hansanhha.querydsl.book.util.BookRequestFactory;
import hansanhha.querydsl.book.vo.BookSubCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookAdminServiceTest {

    @Autowired
    private BookAdminService bookAdminService;

    @Test
    @DisplayName("1권 등록")
    void register() {
        CreateBookRequest request = new CreateBookRequest("채식주의자", "한강", BookSubCategory.KOREAN_NOVEL);

        BookResponse response = bookAdminService.register(request);

        System.out.println(response);

        assertThat(response).isNotNull();
        assertThat(response.author()).isEqualTo(request.author());
    }

    @Test
    @DisplayName("여러 권 등록")
    void registerBooks() {
        List<CreateBookRequest> requests = BookRequestFactory.getKoreanNovels();
        List<BookResponse> bookResponses = bookAdminService.registerAll(requests);

        System.out.println("============생성된 책 목록===========");
        bookResponses.forEach(System.out::println);

        assertThat(bookResponses).isNotNull();
        assertThat(bookResponses.size()).isEqualTo(requests.size());
    }

}