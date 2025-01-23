package com.foreach.across;


import com.atlassian.bamboo.specs.api.builders.requirement.Requirement;
import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;

public class Jobs {
    public static Requirement requiresLinux() {
        return new Requirement("os").matchValue("linux").matchType(Requirement.MatchType.EQUALS);
    }

    public static Task defaultRepositoryCheckoutTask() {
        return new VcsCheckoutTask().description("Checkout Default Repository")
                .checkoutItems(new CheckoutItem().defaultRepository())
                .cleanCheckout(true);
    }
}