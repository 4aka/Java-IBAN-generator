import java.awt.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

public class IBANGen {
    String currentPath = System.getProperty("user.dir");

    /**
     * Get random account.
     */
    public String getAcc(String mfo) {
        return generateAccNumbers(1, "", true, mfo);
    }

    public String getAcc300335() {
        return generateAccNumbers(1, "", true, "300335");
    }

    public void copyToClipboard(String copy) {
        StringSelection stringSelection = new StringSelection(copy);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        print("===== IBAN has copied to the clipboard =====");
    }

    /**
     * Generate integer
     *
     * @param length set length of int
     */
    public String generateInt(int length) {
        StringBuilder result = new StringBuilder();
        Random r = new Random();

        for (int i = 0; i < length; i++) {
            String bytes = Integer.toString(r.nextInt(9));
            result.append(bytes);
        }
        return result.toString();
    }

    /**
     * Generate list of accounts
     *
     * @param quantity set accounts quantity
     * @param isIBAN   generate IBAN if true
     * @param mfo      set MFO ex. 300335, 311528
     */
    public String generateAccNumbers(int quantity, String prefix, boolean isIBAN, String mfo) {
        int count = 0;
        String acc = "";
        String ran;

        while (count != quantity) {
            if (!prefix.isEmpty()) { ran = prefix + generateInt(12); }
            else { ran = "194" + generateInt(12); }

            if (keying(mfo, ran)) { // check that account number is correct.
                if (isIBAN) {
                    count += 1;
                    acc = getIBAN(mfo, ran, "UA");
                    print(acc);
                } else {
                    print(ran);
                    count += 1;
                }
            }
        }
        return acc;
    }

    /**
     * Check keying account number before get IBAN.
     *
     * @param mfo set mfo ex. 300335, 311528
     * @param acc set account number ex. 12366
     */
    public boolean keying(String mfo, String acc) {

        final int[] WEIGHT = new int[]{1, 3, 7, 1, 3, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3};
        char key;
        int sum;
        char[] str = (mfo.substring(0, 5) + acc).toCharArray();
        str[9] = '0';
        sum = acc.length();
        for (int i = 0; i < str.length; i++) {
            sum += ((Character.digit(str[i], 10) * WEIGHT[i]) % 10);
        }
        key = Character.forDigit(((sum % 10) * 7) % 10, 10);

        return acc.charAt(4) == key;
    }

    /**
     * Generate list of accounts into file
     *
     * @param filePath set file path which will be used for data generation
     * @param mfo      set mfo
     * @param quantity set accounts quantity
     */
    public void generateAccountsToFile(String filePath, int quantity, String mfo) {
        File out = new File(filePath);
        print("File path is: " + filePath);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(out, true);

            for (int i = 0; i < quantity; i++) {
                fileOutputStream.write((generateAccNumbers(1, "", true, mfo) + "\n").getBytes());
            }
            fileOutputStream.close();
        } catch (IOException e) {
            print(e.getMessage());
        }
    }

    /**
     * 'UA' 'checksum' 'MFO' *0x?* 'account' 'number'
     *
     * @param MFO     set mfo
     * @param account set account number
     * @param ISOCode set ISO code ex. UA
     * @return IBAN account
     */
    public String getIBAN(String MFO, String account, String ISOCode) {
        String fl = ""; // first country code letter
        String sl = ""; // second country code letter
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder zeroLine = new StringBuilder("0");

        assert account.length() > 19;

        char[] iso = ISOCode.toUpperCase().toCharArray();
        char[] acc = account.toUpperCase().toCharArray();

        // Get integer of letter from ISOCode (A = 10, B = 11 ...)
        for (int k = 65; k <= 90; k++) {
            if ((int) iso[0] == k)
                fl = Integer.toString((k - 65) + 10);
            if ((int) iso[1] == k)
                sl = Integer.toString((k - 65) + 10);
        }

        // Find account's symbol in ASCII table
        for (int j = 0; j < account.length(); j++) {

            // from 0 to 9 (ASCII 48 - 57)
            if (acc[j] >= 48 && acc[j] <= 57) {
                stringBuilder.append(acc[j]);

            } else if (acc[j] <= 90 && acc[j] >= 65) {

                // from A to Z (ASCII 65 - 90)
                for (int k = 65; k < 90; k++) {
                    if (acc[j] == k) {
                        String val = Integer.toString((k - 65) + 10);
                        stringBuilder.append(val);
                    }
                }
            }
        }

        // Get string from '0's
        for (int i = 2; i <= (25 - MFO.length() - account.length()); i++) {
            zeroLine.append("0");
        }

        // full account value transforming to BigInteger
        BigInteger how = new BigInteger(MFO + zeroLine + stringBuilder + fl + sl + "00", 10);
        BigInteger rem = new BigInteger("97", 10);
        BigInteger big = how.remainder(rem);

        int sum = 98 - big.intValue();

        if (sum <= 9) {
            String sumRather = "0" + sum; // changed from Integer.toString(sum)
            return ISOCode.toUpperCase() + sumRather + MFO + zeroLine + account.toUpperCase();
        }
        return ISOCode.toUpperCase() + sum + MFO + zeroLine + account.toUpperCase();
    }

    public static void print(String text) {
        System.out.println(text);
    }
}