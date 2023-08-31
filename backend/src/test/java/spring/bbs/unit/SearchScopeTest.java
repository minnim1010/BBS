package spring.bbs.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import spring.bbs.post.service.SearchScope;

import static org.assertj.core.api.Assertions.assertThat;

class SearchScopeTest {

    @DisplayName("검색 범위가 주어질 때, SearchScope에 값이 존재하는지 확인한다.")
    @CsvSource(value = {"전체:true", "제목:true", "작성자:true", "제목+내용:false"}, delimiter = ':')
    @ParameterizedTest
    void test(String searchScope, boolean expected) {
        //given

        //when
        boolean result = SearchScope.contains(searchScope);

        //then
        assertThat(result).isEqualTo(expected);
    }

}
