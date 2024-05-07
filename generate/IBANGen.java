import java.awt.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class IBANGen {

    final int[] WEIGHT = new int[]{1, 3, 7, 1, 3, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3};
    final int ACCOUNT_PART_MAX_LENGTH = 15;
    final int IBAN_MAX_LENGTH = 25;
    final String ISO_CODE_UA = "UA";

    /**
     * Get random account.
     */
    public String getAcc(String mfo) {
        return getIbanBasedOnParameters("", mfo);
    }

    public String getAcc300335() {
        return getIbanBasedOnParameters("", "300335");
    }

    public void copyToClipboard(String copy) {
        StringSelection stringSelection = new StringSelection(copy);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        print("===== IBAN has copied to the clipboard =====");
    }

    /**
     * Generate int
     *
     * @param length set int length
     */
    public String generateInt(int length) {
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            String bytes = Integer.toString(random.nextInt(9));
            result.append(bytes);
        }
        return result.toString();
    }

    /**
     * Generate 1 account
     *
     * @param mfo    set MFO ex. 300335, 311528
     */
    public String getIbanBasedOnParameters(String prefix, String mfo) {
        String validAccountNumber = generateValidAccNumber(prefix, mfo);
        String iban = getIBAN(mfo, validAccountNumber, ISO_CODE_UA);
        print(iban);
        return iban;
    }

    /**
     * Generate accounts list
     *
     * @param quantity
     * @param prefix
     * @param mfo
     * @return
     */
    public void generateAccNumbers(int quantity, String prefix, String mfo) {
        List<String> accountsList = generateListWithValidAccNumbers(quantity, prefix, mfo);
        printList(generateIbanList(accountsList, mfo));
    }

    /**
     * @param accountNumbers
     * @param mfo
     * @return
     */
    public List<String> generateIbanList(List<String> accountNumbers, String mfo) {
        return accountNumbers.stream()
                .map(acc -> getIBAN(mfo, acc, ISO_CODE_UA))
                .collect(Collectors.toList());
    }

    /**
     * @param quantity
     * @param prefix
     * @param mfo
     * @return
     */
    public List<String> generateListWithValidAccNumbers(int quantity, String prefix, String mfo) {
        List<String> validAccountNumbers = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            validAccountNumbers.add(generateValidAccNumber(prefix, mfo));
        }
        return validAccountNumbers;
    }

    /**
     * @param prefix
     * @param mfo
     * @return
     */
    public String generateValidAccNumber(String prefix, String mfo) {
        String accountNumber;
        do {
            accountNumber = prefix.concat(generateInt(ACCOUNT_PART_MAX_LENGTH - prefix.length()));
        } while (isKeyingValid(mfo, accountNumber));
        return accountNumber;
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
                fileOutputStream.write((getIbanBasedOnParameters("", mfo) + "\n").getBytes());
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
        char[] iso = ISOCode.toUpperCase().toCharArray();
        assert account.length() > 19;

        // Get ASCII number of letter from ISOCode (A = 10, B = 11 ...)
        String firstLetterASCIINumber = getASCIINumber(iso[0]);
        String secondLetterASCIINumber = getASCIINumber(iso[1]);

        // convert account to ASCII format
        StringBuilder stringBuilder = convertAccountNumberToASCII(account);

        // Get string from '0's
        int zeroLineShouldBe = IBAN_MAX_LENGTH - MFO.length() - account.length();
        StringBuilder zeroLine = fillInRequiredLengthWithZero(zeroLineShouldBe);

        // full account value transforming to BigInteger
        BigInteger how = new BigInteger(MFO + zeroLine + stringBuilder + firstLetterASCIINumber + secondLetterASCIINumber + "00", 10);
        BigInteger rem = new BigInteger("97", 10);
        BigInteger big = how.remainder(rem);

        int sum = 98 - big.intValue();

        if (sum <= 9) {
            String sumRather = "0" + sum; // changed from Integer.toString(sum)
            return ISOCode.toUpperCase() + sumRather + MFO + zeroLine + account.toUpperCase();
        }
        return ISOCode.toUpperCase() + sum + MFO + zeroLine + account.toUpperCase();
    }

    /**
     *
     * @param accountNumberInDigitFormat
     * @return
     */
    public StringBuilder convertAccountNumberToASCII(String accountNumberInDigitFormat) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] result = accountNumberInDigitFormat.toUpperCase().toCharArray();

        for (int j = 0; j < accountNumberInDigitFormat.length(); j++) {

            // from 0 to 9 (ASCII 48 - 57)
            if (result[j] >= 48 && result[j] <= 57) {
                stringBuilder.append(result[j]);

            // from A to Z (ASCII 65 - 90)
            } else if (result[j] >= 65 && result[j] <= 90) {

                for (int k = 65; k < 90; k++) {
                    if (result[j] == k) {
                        String val = Integer.toString((k - 65) + 10);
                        stringBuilder.append(val);
                    }
                }
            }
        }
        return stringBuilder;
    }

    /**
     *
     * @param zeroLineShouldBe
     * @return
     */
    public StringBuilder fillInRequiredLengthWithZero(int zeroLineShouldBe) {
        StringBuilder zeroLine = new StringBuilder("0");
        for (int i = 2; i <= zeroLineShouldBe; i++) {
            zeroLine.append("0");
        }
        return zeroLine;
    }

    /**
     *
     * @param letter
     * @return
     */
    public String getASCIINumber(char letter) {
        String asciiNumber = "";
        for (int k = 65; k <= 90; k++) {
            if ((int) letter == k) asciiNumber = Integer.toString((k - 65) + 10);
        }
        return asciiNumber;
    }

    /**
     *
     * @param text
     */
    public static void print(String text) {
        System.out.println(text);
    }

    /**
     *
     * @param list
     */
    public static void printList(List<String> list) {
        list.forEach(System.out::println);
    }

    /**
     * Check isKeyingValid account number before get IBAN.
     * Code from internet
     *
     * @param mfo set mfo ex. 300335, 311528
     * @param acc set account number ex. 12366
     */
    public boolean isKeyingValid(String mfo, String acc) {
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
}