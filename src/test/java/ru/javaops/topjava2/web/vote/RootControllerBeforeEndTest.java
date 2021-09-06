package ru.javaops.topjava2.web.vote;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaops.topjava2.model.Vote;
import ru.javaops.topjava2.web.GlobalExceptionHandler;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaops.topjava2.web.user.UserTestData.user1;
import static ru.javaops.topjava2.web.vote.RootTestData.*;

//https://www.baeldung.com/spring-tests-override-properties
@TestPropertySource(properties = {
        "limit-time.vote=23:59"
})

class RootControllerBeforeEndTest extends AbstractVoteControllerTest{


    @Test
    @WithUserDetails(value = USER2_MAIL)
    void createVoteWithLocation() throws Exception {
        ResultActions action = perform(MockMvcRequestBuilders
                .post(REST_URL + "/vote")
                .param("restaurantId",Integer.toString(REST1_ID)))
                .andDo(print())
                .andExpect(status().isCreated());

        Vote created = MATCHER.readFromJson(action);
        int newId = created.getId();
        Vote newVote = new Vote(newId, LocalDate.now(), null,null);
        newVote.setRegTime(created.getRegTime());
        MATCHER.assertMatch(created,newVote);
        MATCHER.assertMatch(voteRepository.getById(newId),newVote);

    }

    @Test
    @WithUserDetails(value = USER2_MAIL)
    void createNotFound() throws Exception {
        perform(MockMvcRequestBuilders
                .post(REST_URL + "/vote")
                .param("restaurantId",Integer.toString(NOT_FOUND)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER1_MAIL)
    void createDuplicate() throws Exception {
        perform(MockMvcRequestBuilders
                .post(REST_URL + "/vote")
                .param("restaurantId",Integer.toString(REST1_ID)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(GlobalExceptionHandler.EXCEPTION_DUPLICATE_VOTE)));
    }

    @Test
    @WithUserDetails(value = USER1_MAIL)
    void update() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + "/vote")
                .param("restaurantId",Integer.toString(REST2_ID)))
                .andExpect(status().isNoContent())
                .andDo(print());

        Vote updated = voteRepository.findByUserId(user1.getId()).get();
        Assertions.assertEquals(updated.getRestaurant().getId(),REST2_ID);
    }

    @Test
    void updateUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + "/vote")
                .param("restaurantId",Integer.toString(REST2_ID)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithUserDetails(value = USER1_MAIL)
    void updateNotFound() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + "/vote")
                .param("restaurantId",Integer.toString(NOT_FOUND)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

}
















