package ru.javaops.topjava2.web.vote;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaops.topjava2.repository.VoteRepository;
import ru.javaops.topjava2.web.AbstractControllerTest;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaops.topjava2.web.menu.MenuTestData.MATCHER_MENU_TO;
import static ru.javaops.topjava2.web.menu.MenuTestData.allMenuTosForToday;
import static ru.javaops.topjava2.web.restaurant.RestaurantTestData.*;
import static ru.javaops.topjava2.web.vote.RootTestData.*;

public abstract class AbstractVoteControllerTest  extends AbstractControllerTest {
    protected static final String REST_URL = RootController.REST_URL;
    @Autowired
    protected VoteRepository voteRepository;

    @Test
    @WithUserDetails(value = USER1_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .param("pageNumber","0")
                .param("pageSize","10"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER_MENU_TO.contentJson(allMenuTosForToday));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .param("pageNumber","0")
                .param("pageSize","10"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER1_MAIL)
    void getBadRequest() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .param("pageNumber","dfs")
                .param("pageSize","sdf"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(value = USER1_MAIL)
    void getResult() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/vote/result"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER_RESTAURANT_TO.contentJson(voteResult));
    }

    @Test
    void getResultUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/vote/result"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithUserDetails(value = USER1_MAIL)
    void getVote() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/vote/by"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RootTestData.MATCHER.contentJson(vote3));
    }

    @Test
    void getVoteUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/vote/by"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }


    @Test
    @WithUserDetails(value = USER1_MAIL)
    void getAllVotes() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/vote/user/history")
                .param("startDate", "")
                .param("endDate",""))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER_VOTE_TO.contentJson(votesTo));
    }

    @Test
    void getAllVotesUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/vote/user/history")
                .param("startDate", "")
                .param("endDate",""))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithUserDetails(value = USER1_MAIL)
    void getAllVotesBadRequest() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/vote/user/history")
                .param("startDate", "bad value")
                .param("endDate","bad value"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithUserDetails(value = USER1_MAIL)
    void getResultHistory() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/vote/result/history")
                .param("startDate", "")
                .param("endDate",""))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER_RESTAURANT_TO.contentJson(historyResult));
    }

    @Test
    void getResultHistoryUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/vote/result/history")
                .param("startDate", "")
                .param("endDate",""))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithUserDetails(value = USER1_MAIL)
    void getResultHistoryBadRequest() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/vote/result/history")
                .param("startDate", "bad value")
                .param("endDate","bad value"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
