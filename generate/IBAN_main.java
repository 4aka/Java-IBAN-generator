import java.util.Scanner;

public class IBAN_main {

    public static void main(String[] args) {
        IBANGen gen = new IBANGen();
        Scanner in = new Scanner(System.in);
        String mfo;
        String menu;

        // Validation https://www.google.com/url?sa=t&source=web&rct=j&opi=89978449&url=https://www.iban.com/iban-checker&ved=2ahUKEwiQjvj3uvyFAxUrKhAIHXBCAu8QFnoECAYQAQ&usg=AOvVaw3MwI2mM4cTfkDgS7ULC2iT

        con:
        do {
            printMenu();
            menu = in.nextLine();

            switch (menu) {

                case "1":

                    IBANGen.print("Generate IBAN based on 300335 MFO:");
                    String iban300335 = gen.getAcc300335();
                    gen.copyToClipboard(iban300335);
                    continue con;

                case "2":

                    IBANGen.print("Generate random IBAN based on filled in MFO:");
                    IBANGen.print("Insert MFO");
                    mfo = in.nextLine();
                    String getAcc = gen.getAcc(mfo);
                    gen.copyToClipboard(getAcc);
                    continue con;

                case "3":

                    IBANGen.print("Random accounts list generation:");
                    IBANGen.print("How much account do you need? ");
                    int length = Integer.parseInt(in.nextLine());
                    IBANGen.print("prefix: (if empty - here is used 194. accounts_black_list)");
                    String prefix = in.nextLine();
                    IBANGen.print("Generate list in IBAN format? (y\\n)");
                    IBANGen.print("Insert MFO = ");
                    mfo = in.nextLine();
                    gen.generateAccNumbers(length, prefix, mfo);
                    continue con;

                case "4":

                    IBANGen.print("Generate IBAN from scratch:");
                    IBANGen.print("Insert MFO = ");
                    String MFO = in.nextLine();

                    IBANGen.print("Insert account number = ");
                    String account = in.nextLine();

                    while (!gen.isKeyingValid(MFO, account)) {
                        IBANGen.print("Keying wrong!");
                        IBANGen.print("Insert account number = ");
                        account = in.nextLine();
                        gen.isKeyingValid(MFO, account);
                    }
                    IBANGen.print("Insert ISO country code = ");
                    String countryCode = in.nextLine();
                    String paramIban = gen.getIBAN(MFO, account, countryCode);
                    IBANGen.print(paramIban);
                    gen.copyToClipboard(paramIban);
                    continue con;

                case "5":

                    IBANGen.print("Generate IBAN's list into file");
                    IBANGen.print("Insert file path: ex. D:/fileName.txt");
                    IBANGen.print("Your current directory: " + System.getProperty("user.dir"));
                    String filePath = in.nextLine();

                    IBANGen.print("Insert quantity");
                    String quantity = in.nextLine();

                    IBANGen.print("Insert MFO");
                    mfo = in.nextLine();

                    gen.generateAccountsToFile(filePath, Integer.parseInt(quantity), mfo);
                    IBANGen.print("Done");
                    continue con;

                case "6":

                    IBANGen.print("Verify account number isKeyingValid:");
                    IBANGen.print("Insert account number");
                    String acc = in.nextLine();
                    IBANGen.print("Insert mfo");
                    mfo = in.nextLine();
                    if (gen.isKeyingValid(mfo, acc)) IBANGen.print("Keying is correct");
                    else IBANGen.print("Keying is NOT correct!");
                    continue con;

                case "q":

                    in.close();
                    break;

                default:
                    printMenu();

            }

        } while (!menu.matches("q"));
    }

    private static void printMenu() {
        IBANGen.print("");
        IBANGen.print("");
        IBANGen.print("-----------------------------------------");
        IBANGen.print("Choose: ");
        IBANGen.print("Press 1 - Generate IBAN based on 300335 MFO");
        IBANGen.print("Press 2 - Generate random IBAN based on filled in MFO:");
        IBANGen.print("Press 3 - Random accounts list generation:");
        IBANGen.print("Press 4 - Generate IBAN from scratch:");
        IBANGen.print("Press 5 - Generate IBAN's list into file");
        IBANGen.print("Press 6 - Verify account number isKeyingValid:");
        IBANGen.print("Press q - for exit.");
    }
}