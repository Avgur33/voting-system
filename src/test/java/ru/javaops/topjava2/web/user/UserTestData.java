package ru.javaops.topjava2.web.user;

import ru.javaops.topjava2.model.Role;
import ru.javaops.topjava2.model.User;
import ru.javaops.topjava2.util.JsonUtil;
import ru.javaops.topjava2.web.AbstractTestData;
import ru.javaops.topjava2.web.MatcherFactory;

import java.util.Collections;
import java.util.Date;

public class UserTestData extends AbstractTestData {
    public static final MatcherFactory.Matcher<User> MATCHER = MatcherFactory.usingIgnoringFieldsComparator(User.class, "registered", "password");

    public static final User user1 = new User(USER_ID, "User", USER1_MAIL, "password", Role.USER);
    public static final User admin = new User(ADMIN_ID, "Admin", ADMIN_MAIL, "admin", Role.ADMIN, Role.USER);
    public static final User user2 = new User(3, "User2", "user2@yandex.ru", "admin", Role.USER);

    public static User getNew() {
        return new User(null, "New", "new@gmail.com", "newPass", false, new Date(), Collections.singleton(Role.USER));
    }

    public static User getUpdated() {
        return new User(USER_ID, "UpdatedName", USER1_MAIL, "newPass", false, new Date(), Collections.singleton(Role.ADMIN));
    }

    public static String jsonWithPassword(User user, String pass) {
        return JsonUtil.writeAdditionProps(user, "password", pass);
    }
}
