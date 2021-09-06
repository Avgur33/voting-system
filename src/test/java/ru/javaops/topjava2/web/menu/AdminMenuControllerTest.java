package ru.javaops.topjava2.web.menu;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.topjava2.model.Menu;
import ru.javaops.topjava2.repository.MenuRepository;
import ru.javaops.topjava2.web.AbstractControllerTest;
import ru.javaops.topjava2.web.GlobalExceptionHandler;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaops.topjava2.web.menu.MenuTestData.*;

class AdminMenuControllerTest extends AbstractControllerTest {

    private static final String REST_URL = "/api/admin/restaurants/";
    @Autowired
    MenuRepository menuRepository;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    @Transactional(propagation = Propagation.NEVER)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + REST1_ID + "/menu/" + MENU1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(menuRepository.findById(MENU1_ID).isPresent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + REST1_ID + "/menu/" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteWrongRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + NOT_FOUND + "/menu/" + MENU1_ID))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER1_MAIL)
    void deleteForbidden() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + REST1_ID + "/menu/" + MENU1_ID))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + REST1_ID + "/menu/" + MENU1_ID))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void creatWithLocation() throws Exception {
        ResultActions action = perform(MockMvcRequestBuilders
                .post(REST_URL + REST3_ID + "/menu")
                .param("forDate", "")
                .param("dishes", "7", "8", "9")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        Menu newMenu = new Menu(null, LocalDate.now(), null, null);
        Menu created = MATCHER.readFromJson(action);
        int newId = created.getId();
        newMenu.setId(newId);
        MATCHER.assertMatch(created, newMenu);
        MATCHER.assertMatch(menuRepository.getById(newId), newMenu);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void creatDuplicate() throws Exception {
        perform(MockMvcRequestBuilders
                .post(REST_URL + REST1_ID + "/menu")
                .param("forDate", "")
                .param("dishes", "1", "2", "3")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(GlobalExceptionHandler.EXCEPTION_DUPLICATE_MENU)));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void creatNotEnough() throws Exception {
        perform(MockMvcRequestBuilders
                .post(REST_URL + REST1_ID + "/menu")
                .param("forDate", "")
                .param("dishes", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void creatTooMuch() throws Exception {
        perform(MockMvcRequestBuilders
                .post(REST_URL + REST1_ID + "/menu")
                .param("forDate", "")
                .param("dishes", "1", "2", "3", "4", "5", "6")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void creatEarlier() throws Exception {
        perform(MockMvcRequestBuilders
                .post(REST_URL + REST3_ID + "/menu")
                .param("forDate", LocalDate.now().minusDays(1).toString())
                .param("dishes", "7", "8", "9")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isLocked());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + REST2_ID + "/menu/" + MENU2_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER.contentJson(menu2));
    }


    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + REST2_ID + "/menu/" + NOT_FOUND))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getWrongRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND + "/menu/" + MENU2_ID))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER1_MAIL)
    void getForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + REST2_ID + "/menu/" + MENU2_ID))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getBy() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + REST1_ID + "/menu/by"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER.contentJson(menu4));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + REST2_ID + "/menu/")
                .param("startDate", "")
                .param("endDate", ""))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER.contentJson(allMenusOfRestaurant2));
    }

    @Test
    @WithUserDetails(value = USER1_MAIL)
    void getAllForBidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + REST2_ID + "/menu/")
                .param("startDate", "")
                .param("endDate", ""))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + REST2_ID + "/menu/" + MENU5_ID)
                .param("dishes", "4", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserDetails(value = USER1_MAIL)
    void updateForbidden() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + REST2_ID + "/menu/" + MENU5_ID)
                .param("dishes", "4", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateTooLate() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + REST2_ID + "/menu/" + MENU2_ID)
                .param("dishes", "4", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isLocked());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWrongRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + REST1_ID + "/menu/" + MENU5_ID)
                .param("dishes", "4", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateNotEnoughDish() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + REST2_ID + "/menu/" + MENU5_ID)
                .param("dishes", "4")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateTooMuchDish() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + REST2_ID + "/menu/" + MENU5_ID)
                .param("dishes", "4", "5", "6", "7", "8", "9")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

}