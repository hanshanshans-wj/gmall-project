package com.wuju.gmall.gmalllistservice;


import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServiceApplicationTests {
    @Autowired
    JestClient jestClient;

    @Test
    public void contextLoads() {
    }
    @Test
    public void testES() throws IOException {
        String query="{\n" +
                "  \"query\": {\n" +
                "    \"match\": {\n" +
                "      \"actorList.name\": \"张译\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        Search build = new Search.Builder(query).addIndex("movie_chn").addType("movie").build();
        //执行search查询
        SearchResult execute = jestClient.execute(build);
        //遍历出集合对象
        List<SearchResult.Hit<Map, Void>> hits = execute.getHits(Map.class);
        for (SearchResult.Hit<Map, Void> hit : hits) {
            Map source = hit.source;
            System.out.println(source);
        }
    }

}
