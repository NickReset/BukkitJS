package social.nickrest.bukkitjs.util;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class StringUtil {

    public String getSaltString(int length) {
        String saltChars = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < (length - 1)) {
            int index = (int) (rnd.nextFloat() * saltChars.length());
            salt.append(saltChars.charAt(index));
        }
        return salt.toString();

    }
}
