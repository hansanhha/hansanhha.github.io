package hansanhha.querydsl.book;

import hansanhha.querydsl.book.dto.BookResponse;
import hansanhha.querydsl.book.dto.CreateBookRequest;
import hansanhha.querydsl.book.entity.BookCategory;
import hansanhha.querydsl.book.util.BookRequestFactory;
import hansanhha.querydsl.book.vo.BookMainCategory;
import hansanhha.querydsl.book.vo.BookMiddleCategory;
import hansanhha.querydsl.book.vo.BookSubCategory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookUserServiceTest {

    @Autowired
    private BookAdminService bookAdminService;

    @Autowired
    private BookUserService bookUserService;

    @BeforeAll
    @DisplayName("테스트용 책 등록")
    void init() {
        List<CreateBookRequest> koreanNovels = BookRequestFactory.getKoreanNovels();
        List<CreateBookRequest> programmings = BookRequestFactory.getProgrammings();
        List<CreateBookRequest> requests = Stream.concat(koreanNovels.stream(), programmings.stream()).toList();

        bookAdminService.registerAll(requests);
    }

    @Test
    @DisplayName("메인 카테고리 별 책 조회")
    void findBooksByMainCategory() {
        Pageable pageable = Pageable.ofSize(20);
        BookMainCategory mainCategory = BookMainCategory.ETC;
        Slice<BookResponse> findBooks = bookUserService.getBooksByCategory(BookCategory.from(mainCategory), pageable);

        System.out.println("====== ETC 메인 카테고리에 등록된 책 목록 및 페이징 정보 ======");
        findBooks.forEach(System.out::println);
        System.out.println(findBooks.getNumber());
        System.out.println(findBooks.getSize());
        System.out.println(findBooks.getNumberOfElements());

        assertThat(findBooks.getContent()).isNotNull();
        assertThat(findBooks.getSize()).isEqualTo(pageable.getPageSize());
    }

    @Test
    @DisplayName("중간 카테고리 별 책 조회")
    void findBooksByMiddleCategory() {
        Pageable pageable = Pageable.ofSize(20).withPage(4);
        BookMiddleCategory middleCategory = BookMiddleCategory.KOREAN_LITERATURE;
        Slice<BookResponse> findBooks = bookUserService.getBooksByCategory(BookCategory.from(middleCategory), pageable);

        System.out.println("====== KOREAN_LITERATURE 중간 카테고리에 등록된 책 목록 및 페이징 정보 ======");
        findBooks.forEach(System.out::println);
        System.out.println(findBooks.getNumber());
        System.out.println(findBooks.getSize());
        System.out.println(findBooks.getNumberOfElements());

        assertThat(findBooks.getContent()).isNotNull();
        assertThat(findBooks.getSize()).isEqualTo(pageable.getPageSize());
    }

    @Test
    @DisplayName("서브 카테고리 별 책 조회")
    void findBooksBySubCategory() {
        Pageable pageable = Pageable.ofSize(20);
        BookSubCategory subCategory = BookSubCategory.KOREAN_NOVEL;
        Slice<BookResponse> findBooks = bookUserService.getBooksByCategory(BookCategory.from(subCategory), pageable);

        System.out.println("====== KOREAN_NOVEL 서브 카테고리에 등록된 책 목록 및 페이징 정보 ======");
        findBooks.forEach(System.out::println);
        System.out.println(findBooks.getNumber());
        System.out.println(findBooks.getSize());
        System.out.println(findBooks.getNumberOfElements());

        assertThat(findBooks.getContent()).isNotNull();
        assertThat(findBooks.getSize()).isEqualTo(pageable.getPageSize());
        assertThat(findBooks.getContent().stream().allMatch(book -> book.categoryCode().equals(subCategory.getCode()))).isTrue();
    }

    @Test
    @DisplayName("책 이름 조회")
    void findBooksByTitle() {
        Pageable pageable = Pageable.ofSize(20).withPage(1);
        String title = "자바";

        Page<BookResponse> findBooks = bookUserService.getBooksByTitle(title, pageable);

        System.out.printf("====== %s 제목으로 검색한 책 목록 및 페이징 정보 ======\n", title);
        findBooks.forEach(System.out::println);
        System.out.println("페이지: " + findBooks.getNumber());
        System.out.println("요청 개수: " + findBooks.getSize());
        System.out.println("찾은 개수: " + findBooks.getNumberOfElements());
        System.out.println("첫 페이지: " + findBooks.isFirst());
        System.out.println("마지막 페이지: " + findBooks.isLast());

        assertThat(findBooks.getContent()).isNotNull();
        assertThat(findBooks.getContent().size()).isNotZero();
        assertThat(findBooks.stream().allMatch(book -> book.title().contains(title))).isTrue();
    }

    @Test
    @DisplayName("작가 이름 조회")
    void findBooksByAuthor() {
        Pageable pageable = Pageable.ofSize(20).withPage(1);
        String author = "한강";

        Page<BookResponse> findBooks = bookUserService.getBooksByAuthor(author, pageable);

        System.out.printf("====== %s 작가 이름으로 검색한 책 목록 및 페이징 정보 ======\n", author);
        findBooks.forEach(System.out::println);
        System.out.println("페이지: " + findBooks.getNumber());
        System.out.println("요청 개수: " + findBooks.getSize());
        System.out.println("찾은 개수: " + findBooks.getNumberOfElements());
        System.out.println("첫 페이지: " + findBooks.isFirst());
        System.out.println("마지막 페이지: " + findBooks.isLast());

        assertThat(findBooks.getContent()).isNotNull();
        assertThat(findBooks.getContent().size()).isNotZero();
        assertThat(findBooks.stream().allMatch(book -> book.author().contains(author))).isTrue();
    }

    @Test
    @DisplayName("카테고리, 책 이름 조합 조회")
    void findBooksByAnyCondition() {
        Pageable pageable = Pageable.ofSize(20).withPage(2);

        BookMiddleCategory category = BookMiddleCategory.MIDDLE_ETC;
        String title = "자바";

        Page<BookResponse> findBooks = bookUserService.getBooksV2(BookCategory.from(category), title, null, false, pageable);

        System.out.printf("====== \"%s\" 카테고리와 \"%s\" 책 이름으로 검색한 책 목록 및 페이징 정보 ======\n", category.getDisplayName(), title);
        findBooks.forEach(System.out::println);
        System.out.println("페이지: " + findBooks.getNumber());
        System.out.println("요청 개수: " + findBooks.getSize());
        System.out.println("찾은 개수: " + findBooks.getNumberOfElements());
        System.out.println("첫 페이지: " + findBooks.isFirst());
        System.out.println("마지막 페이지: " + findBooks.isLast());

        assertThat(findBooks.getContent()).isNotNull();
        assertThat(findBooks.getContent().size()).isNotZero();
        assertThat(findBooks.stream().allMatch(book -> book.title().contains(title))).isTrue();
    }

}