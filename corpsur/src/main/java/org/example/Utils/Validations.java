package org.example.Utils;

public class Validations {
    public static boolean isValidEcuadorianDNI(String dni) {
        if (dni == null || !dni.matches("\\d{10}")) return false;
        int provinceCode = Integer.parseInt(dni.substring(0, 2));
        if (provinceCode < 1 || provinceCode > 24) return false;

        int thirdDigit = Integer.parseInt(dni.substring(2, 3));
        if (thirdDigit >= 6) return false;

        int[] coef = {2,1,2,1,2,1,2,1,2};
        int sum = 0;
        for (int i = 0; i < coef.length; i++) {
            int digit = Character.getNumericValue(dni.charAt(i)) * coef[i];
            sum += digit > 9 ? digit - 9 : digit;
        }

        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit == Character.getNumericValue(dni.charAt(9));
    }

    public static boolean isValidDNI(String dni) {
        return dni != null && dni.matches("\\d{10}");
    }

    public static boolean isValidProfession(String profession) {
        return profession != null && profession.matches("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]{3,50}$");
    }



}
