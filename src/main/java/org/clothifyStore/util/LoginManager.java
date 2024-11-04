package org.clothifyStore.util;

import lombok.Getter;
import lombok.Setter;

public class LoginManager {
    @Getter
    @Setter
    private static String loggedAdminId;

    public static void setLoggedAdminId(String id) {
    }
}