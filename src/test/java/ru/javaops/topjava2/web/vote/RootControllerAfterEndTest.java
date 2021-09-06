package ru.javaops.topjava2.web.vote;


import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaops.topjava2.web.AbstractTestData.*;

@TestPropertySource(properties = {
        "limit-time.vote=00:01"
})
public class RootControllerAfterEndTest extends AbstractVoteControllerTest {

    @Test
    @WithUserDetails(value = USER2_MAIL)
    void createVoteWithLocation() throws Exception {
        perform(MockMvcRequestBuilders
                .post(REST_URL + "/vote")
                .param("restaurantId", Integer.toString(REST1_ID)))
                .andDo(print())
                .andExpect(status().isLocked())
                .andExpect(content().string(containsString("Voting end at")));
    }

    @Test
    @WithUserDetails(value = USER1_MAIL)
    void update() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + "/vote")
                .param("restaurantId", Integer.toString(REST2_ID)))
                .andDo(print())
                .andExpect(status().isLocked())
                .andExpect(content().string(containsString("Voting end at")));

    }
}
