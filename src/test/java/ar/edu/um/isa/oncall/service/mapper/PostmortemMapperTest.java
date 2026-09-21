package ar.edu.um.isa.oncall.service.mapper;

import static ar.edu.um.isa.oncall.domain.PostmortemAsserts.*;
import static ar.edu.um.isa.oncall.domain.PostmortemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostmortemMapperTest {

    private PostmortemMapper postmortemMapper;

    @BeforeEach
    void setUp() {
        postmortemMapper = new PostmortemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPostmortemSample1();
        var actual = postmortemMapper.toEntity(postmortemMapper.toDto(expected));
        assertPostmortemAllPropertiesEquals(expected, actual);
    }
}
