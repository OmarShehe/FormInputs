package com.omarshehe.forminputjava.Utils;

import android.graphics.Color;

public enum PasswordStrength {
        Weak(0, Color.RED), Medium(1, Color.argb(255, 220, 185, 0)), Strong(2, Color.parseColor("#28A645"));
        static int REQUIRED_LENGTH = 6;
        static int MAXIMUM_LENGTH = 6;
        static boolean REQUIRE_SPECIAL_CHARACTERS = true;
        static boolean REQUIRE_DIGITS = true;
        static boolean REQUIRE_LOWER_CASE = true;
        static boolean REQUIRE_UPPER_CASE = true;
        int resId;
        int color;
        PasswordStrength(int resId, int color) {
            this.resId = resId;
            this.color = color;
        }

        public int getValue() {
            return resId;
        }
        public int getColor() {
            return color;
        }

        public static PasswordStrength calculateStrength(String password) {
            int currentScore = 0;
            boolean sawUpper = false;
            boolean sawLower = false;
            boolean sawDigit = false;
            boolean sawSpecial = false;

            for (int i = 0; i < password.length(); i++) {
                char c = password.charAt(i);

                if (!sawSpecial && !Character.isLetterOrDigit(c)) {
                    currentScore += 1;
                    sawSpecial = true;
                } else {
                    if (!sawDigit && Character.isDigit(c)) {
                        currentScore += 1;
                        sawDigit = true;
                    } else {
                        if (!sawUpper || !sawLower) {
                            if (Character.isUpperCase(c))
                                sawUpper = true;
                            else
                                sawLower = true;
                            if (sawUpper && sawLower)
                                currentScore += 1;
                        }
                    }
                }
            }

            if (password.length() > REQUIRED_LENGTH) {
                if ((REQUIRE_SPECIAL_CHARACTERS && !sawSpecial) || (REQUIRE_UPPER_CASE && !sawUpper) || (REQUIRE_LOWER_CASE && !sawLower) || (REQUIRE_DIGITS && !sawDigit)) {
                    currentScore = 1;
                } else {
                    currentScore = 2;
                }
            } else {
                currentScore = 0;
            }

            switch (currentScore) {
                case 0:
                    return Weak;
                case 1:
                    return Medium;
                case 2:
                    return Strong;
                default:
            }

            return Strong;
        }


}
