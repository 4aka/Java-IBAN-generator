import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class IBAN_main {
    static String currentPath = System.getProperty("user.dir");

    public static void main(String[] args) {
        IBANGen gen = new IBANGen();

        System.out.println("Choose: ");
        System.out.println("Press 1 - get IBAN (300335)");
        System.out.println("Press 2 - get IBAN (311528)");
        System.out.println("Press 3 - get IBAN (set MFO)");
        System.out.println("Press 4 - convert list of IBAN");
        System.out.println("Press 5 - generate random IBAN list");
        System.out.println("Press 6 - get parametrized IBAN");
        System.out.println("Press 7 - generation IBAN list to file");
        System.out.println("Press 8 - Check account number keying");
        System.out.println("Press m - menu.");
        System.out.println("Press q - for exit.");

        Scanner in = new Scanner(System.in);
        String menu;

        con:
        do {
            menu = in.nextLine();

            switch (menu) {

                case "1":

                    System.out.println("Get random IBAN account (300335):");
                    String iban300335 = gen.getAcc300335();
                    gen.copyToClipboard(iban300335);
                    continue con;

                case "2":

                    System.out.println("Get random IBAN account (311528):");
                    String iban311528 = gen.getAcc311528();
                    gen.copyToClipboard(iban311528);
                    continue con;

                case "3":

                    System.out.println("random IBAN account with set MFO generation:");
                    System.out.println("Insert MFO");
                    String mfo2 = in.nextLine();
                    String getAcc = gen.getAcc(mfo2);
                    gen.copyToClipboard(getAcc);
                    continue con;

                case "4":

                    System.out.println("IBAN list convert:");
                    System.out.println("-------------------------------------------------------");
                    System.out.println("Please notice! Here is no any account checkers!");
                    System.out.println("-------------------------------------------------------");
                    System.out.println("Put your account numbers into 'iban.txt' near this app.");
                    System.out.println("-------------------------------------------------------");
                    System.out.println("File must have a name 'iban.txt'");
                    System.out.println("-------------------------------------------------------");
                    System.out.println("Insert MFO for convert to:");
                    System.out.println("-------------------------------------------------------");
                    String mfo = in.nextLine();
                    Path path = Paths.get(currentPath + "/iban.txt");
                    gen.convert(path, mfo);
                    continue con;

                case "5":

                    System.out.println("Random accounts list generation:");
                    System.out.println("How much account needs to generate: ");
                    int length = Integer.parseInt(in.nextLine());
                    System.out.println("prefix: (if empty - there is uses 194. accounts_black_list)");
                    String prefix = in.nextLine();
                    System.out.println("Generate list in IBAN format? (y\\n)");
                    String isIban = in.nextLine();
                    System.out.println("Insert MFO = ");
                    String MFO1 = in.nextLine();
                    gen.generateAccNumbers(length, prefix, isIban.contains("y"), MFO1);
                    continue con;

                case "6":

                    System.out.println("Get parametrized IBAN:");
                    System.out.println("Insert MFO = ");
                    String MFO = in.nextLine();

                    System.out.println("Insert account number = ");
                    String account = in.nextLine();

                    while (!gen.keying(MFO, account)) {
                        System.out.println("Keying wrong!");
                        System.out.println("Insert account number = ");
                        account = in.nextLine();
                        gen.keying(MFO, account);
                    }
                    System.out.println("Insert ISO country code = ");
                    String countryCode = in.nextLine();
                    String paramIban = gen.getIBAN(MFO, account, countryCode);
                    gen.copyToClipboard(paramIban);
                    continue con;

                case "7":

                    System.out.println("generation IBAN list to file");
                    System.out.println("Insert file path: ex. D:/fileName.txt");
                    String filePath = in.nextLine();

                    System.out.println("Insert quantity");
                    String quantity = in.nextLine();

                    System.out.println("Insert MFO");
                    String mfo3 = in.nextLine();

                    gen.generateAccountsToFile(filePath, Integer.parseInt(quantity), mfo3);
                    System.out.println("Done");
                    continue con;

                case "8":

                    System.out.println("Check account number keying:");
                    System.out.println("Insert account number");
                    String acc = in.nextLine();
                    System.out.println("Insert mfo");
                    String mfo4 = in.nextLine();
                    if (gen.keying(mfo4, acc)) System.out.println("Account number is correct");
                    else System.out.println("Account number is INCORRECT!");
                    continue con;

                case "42":

                    System.out.println("    *  *  *   *  *  *   *  *  *   *  *  *   *  *  *   *  *  *   *  *  * ** ** **");
                    System.out.println("*  *  * *  *  * *  *  * *  *  * *  *  * *  *  * *  *  * *  *  * *  *  * *  *  * ");
                    System.out.println("    *  *  *   *  *  *   *  *  *   *  *  *   *  *  *   *  *  *   *  *  * ** ** **");
                    System.out.println("*  *  * *  *  * *  *  * *  *  * *  *  * *  *  * *  *  * *  *  * *  *  * *  *  * ");
                    System.out.println("----------  What's the question 'The Answer to the Ultimate Question of Life, --");
                    System.out.println("---------------------------  the Universe, and Everything'  --------------------");
                    System.out.println("    *  *  *   *  *  *   *  *  *   *  *  *   *  *  *   *  *  *   *  *  * ** ** **");
                    System.out.println("*  *  * *  *  * *  *  * *  *  * *  *  * *  *  * *  *  * *  *  * *  *  * *  *  * ");
                    System.out.println("    *  *  *   *  *  *   *  *  *   *  *  *   *  *  *   *  *  *   *  *  * ** ** **");
                    System.out.println("*  *  * *  *  * *  *  * *  *  * *  *  * *  *  * *  *  * *  *  * *  *  * *  *  * ");
                    continue con;

                case "V":

                    for (int i = 0; i < 10; i++) {
                        System.out.println("         vv        vv");
                        System.out.println("          vv      vv       So, everything is        ");
                        System.out.println("           vv    vv              FINE!  ");
                        System.out.println("            vv  vv");
                        System.out.println("             vvvv");
                        System.out.println("              vv");
                    }
                    continue con;

                case "q":

                    in.close();
                    break;

                default:
                    System.out.println("Try again");
            }

        } while (!menu.matches("q"));
    }
}