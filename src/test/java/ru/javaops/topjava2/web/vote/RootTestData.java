package ru.javaops.topjava2.web.vote;

import ru.javaops.topjava2.model.Vote;
import ru.javaops.topjava2.to.VoteTo;
import ru.javaops.topjava2.util.VoteUtil;
import ru.javaops.topjava2.web.AbstractTestData;
import ru.javaops.topjava2.web.MatcherFactory;

import java.time.LocalDate;
import java.util.List;

import static ru.javaops.topjava2.web.restaurant.RestaurantTestData.rest1;

public class RootTestData extends AbstractTestData {

    public static final MatcherFactory.Matcher<Vote> MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Vote.class, "restaurant", "regTime","userId");

    public static final MatcherFactory.Matcher<VoteTo> MATCHER_VOTE_TO =
            MatcherFactory.usingIgnoringFieldsComparator(VoteTo.class, "regTime");

    public static final Vote vote1 = new Vote(VOTE1_ID, LocalDate.now().minusDays(1),USER_ID,rest1);
    //public static final Vote vote2 = new Vote(VOTE1_ID + 1, LocalDate.now().minusDays(1), ADMIN_ID,rest2);
    public static final Vote vote3 = new Vote(VOTE1_ID + 2, LocalDate.now(),USER_ID,rest1);
    //public static final Vote vote4 = new Vote(VOTE1_ID + 3, LocalDate.now(),ADMIN_ID,rest2);

    public static final List<VoteTo> votesTo = VoteUtil.getTos(List.of(vote1,vote3));





}
