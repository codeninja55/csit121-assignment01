//package assignment1;
import java.util.*;

/*
 *
 * @author Dinh Che
 * Student Number: 5721970
 * Email: dbac496@uowmail.edu.au
 *
 */

public class Shop {

    private ArrayList<Card> cards;
    private ArrayList<Purchase> purchases;
    private ArrayList<String> categoriesList; // store a String list of category keys
    private Map<String, Double> categories;
    private Scanner input = new Scanner(System.in);
    //private Helper Helper = new Helper(); // Helper class to print menu's to console

    /******************************************************************/
    /************************** CONSTRUCTORS **************************/
    /******************************************************************/

    // default
    public Shop() {
        this.purchases = new ArrayList<>();
        this.cards = new ArrayList<>();
    }

    // constructor to create categories list
    public Shop(ArrayList<String> categoriesList) {
        this.purchases = new ArrayList<>();
        this.cards = new ArrayList<>();
        this.categoriesList = categoriesList;

        this.categories = new HashMap<>();
        for (String item : categoriesList)
            this.categories.put(item, 0D);
    }

    /*************************************************************/
    /************************** SETTERS **************************/
    /*************************************************************/

    public void makePurchase() {

        System.out.print("\nEnter Receipt ID:  ");
        int receiptID = input.nextInt();
        input.nextLine();

        System.out.print("Enter Card ID [or Cash]:  ");
        String cardID = input.nextLine();

        // TODO Fix this
        //Map<String, Double> categories = setCategories();
        setCategories();

        /*
        * NOTE: Regarding ConcurrentModificationError when
        * iterating over ArrayList
        * There are 2 options available:
        * 1. Create a copy of cards ArrayList using
        *    ArrayList(Collection<? extends E> c)
        *    to avoid Modifying the original cards list
        * 2. Iterating over original ArrayList to find
        *    index of existing cards and making
        *    modifications to any existing card objects
        *    using its index in the ArrayList
        * */

        ArrayList<Card> cardsCopy = new ArrayList<>(cards);

        boolean newCard = true; // flag to see if new card required

        if (cardID.equalsIgnoreCase("cash")) {
            /* If it just a cash purchase, no updates required to card */
            purchases.add(new Purchase(receiptID, categories));
        } else {
            /* Loop through cards ArrayList to validate for existing cards
             * if the card does not exist, prompt user to make one. */
            for (Card card : cardsCopy) {

                if (card.id.equals(cardID)) {
                    String cardType = card.cardType;
                    Purchase newPurchase = new Purchase(receiptID, cardID, cardType, categories);
                    card.setPoints(newPurchase.calcCategoriesTotal());

                    if (!cardType.equalsIgnoreCase("AnonCard"))
                        card.setBalance(newPurchase.calcCategoriesTotal());

                    purchases.add(newPurchase);
                    newCard = false; // set flag so new card not created
                    break;
                }
            }

            if (newCard) {
                System.out.print("\nPlease create a new card for this purchase\n");

                createCard(receiptID, cardID, categories);
            }
        }
    } // end of makePurchase method

    /*private Map<Integer, String> createCategoriesMenu() {



        return categoriesMenu;
    }*/


    private void setCategories() {

        Map<Integer, String> categoriesMenu = new HashMap<>();
        int counter = 1;

        for (Map.Entry<String, Double> item : categories.entrySet()) {
            categoriesMenu.put(counter,item.getKey());
            counter++;
        }

        System.out.printf("%nPlease select Purchase Category from below to add amount:%n");
        System.out.printf("[ 0 ] %s%n", "Finished");

        for (Map.Entry<Integer, String> item : categoriesMenu.entrySet()) {
            System.out.printf("[ %d ] %s%n", item.getKey(), item.getValue());
        }

        int choice = Helper.userSelection();
        String selection = "";
        boolean sentinel = true;

        for (Map.Entry<Integer, String> item : categoriesMenu.entrySet()) {
            if (choice == item.getKey()) {
                selection = item.getValue();
            }

            sentinel = false;
        }

        if (sentinel) {
            selection = "";
        }

        while (true) {
            //String selection = Helper.categoriesSelection();

            if (selection.isEmpty()) {
                break;
            } else {
                System.out.printf("Enter Total Amount for %s Category:  ", selection);
                double categoryAmount = input.nextDouble();
                categories.put(selection, categoryAmount);
            }
        }
    } // end of createCategories method

    private void createCard(int ReceiptID, String cardID, Map<String, Double> categories) {

        String name, email;
        Card newCard;

        String cardChoice = Helper.cardSelection();
        input.nextLine(); // consume newline character leftover from nextInt()

        Purchase newPurchase = new Purchase(ReceiptID, cardID, cardChoice, categories);
        double totalAmount = newPurchase.calcCategoriesTotal();

        if (cardChoice.isEmpty()) {
            System.out.println("\nExiting from creating card...");
        } else if (cardChoice.equalsIgnoreCase("AnonCard")) {
            System.out.println("\nCreating an Anon Card");

            newCard = new AnonCard(cardID);

            newCard.setPoints(totalAmount);
            cards.add(newCard);

        } else {

            if (cardChoice.equalsIgnoreCase("BasicCard")) {
                System.out.println("\nCreating a Basic Card");
            } else {
                System.out.println("\nCreating a Premium Card");
                System.out.println("Please note there is a $25.0 fee to sign up.");
                System.out.println("This will be added to your purchase.");
            }

            System.out.print("\nEnter Customer Name:  ");
            name = input.nextLine();

            System.out.print("\nEnter Customer Email:  ");
            email = input.nextLine();

            if (cardChoice.equalsIgnoreCase("BasicCard"))
                newCard = new BasicCard(cardID, name, email, totalAmount);
            else
                newCard = new PremiumCard(cardID, name, email, totalAmount);

            newCard.setPoints(totalAmount);

            cards.add(newCard);
        }

        purchases.add(newPurchase);
    } // end of createCard method

    /*************************************************************/
    /************************** GETTERS **************************/
    /*************************************************************/
    public ArrayList<Card> getCards() { return cards; }

    public ArrayList<Purchase> getPurchases() { return purchases; }

    /*************************************************************/
    /************************** HELPERS **************************/
    /*************************************************************/

    public void showCards() {
        System.out.printf("%n%n%-12s %-10s %-10s %-15s %-20s %-20s%n",
                "Card Type","Card ID","Points","Balance", "Name", "Email");

        for (Card card : cards)
            System.out.println(card.toString());

        System.out.println();
    }

    public void showPurchases() {
        for (Purchase purchase : purchases)
            System.out.println(purchase.toString());
    }

    public void showTotalPurchases() {
        System.out.printf("%n%n%-20s %s","Category","Total");

        Map<String, Double> categoryTotal = new HashMap<>();
        categoryTotal.put("Systems", 0D);
        categoryTotal.put("Laptops", 0D);
        categoryTotal.put("Peripherals", 0D);
        categoryTotal.put("Multimedia", 0D);
        categoryTotal.put("Accessories", 0D);

        // TODO Change this to loop through the categories
        for (Purchase purchase : purchases) {
            Map<String, Double> map = purchase.getCatMap();

            double systemsVal = categoryTotal.get("Systems") + map.get("Systems");
            categoryTotal.replace("Systems", systemsVal);

            double laptopsVal = categoryTotal.get("Laptops") + map.get("Laptops");
            categoryTotal.replace("Laptops", laptopsVal);

            double peripheralsVal = categoryTotal.get("Peripherals") + map.get("Peripherals");
            categoryTotal.replace("Peripherals", peripheralsVal);

            double multimediaVal = categoryTotal.get("Multimedia") + map.get("Multimedia");
            categoryTotal.replace("Multimedia", multimediaVal);

            double accessoriesVal = categoryTotal.get("Accessories") + map.get("Accessories");
            categoryTotal.replace("Accessories", accessoriesVal);
        }

        for (Map.Entry<String, Double> item : categoryTotal.entrySet()) {
            System.out.printf("%n%-20s $%.2f", (item.getKey() + ":"), item.getValue());
        }
        System.out.println("\n\n");
    }

    /* TODO 2
    * The user can enter via the console an arbitrary number of thresholds
    * (instead of the three required in standard deliverable number 4),
    * then these thresholds will be used when reporting the number of customers
    * in each of these point 'bands'.
    * */

    public void showPoints() {
        // Total points by all customers
        double totalPoints = 0;

        // prompt user if they would like to make a new threshold <-- put this in Helper class
        // otherwise default to the ones already created below

        // Deafult points thresholds by customer
        int low = 0;
        int medium = 0;
        int high = 0;

        for (Card card : cards) {
            totalPoints += card.getPoints();

            if (card.points < 500D) {
                low++;
            } else if (card.points > 500D && card.points < 2000D) {
                medium++;
            } else {
                high++;
            }
        }

        System.out.printf("%n%nTotal Points for All Customers: %.2f%n%n", totalPoints);

        System.out.println("Customers by Groupings");
        System.out.printf("Low (less than 500): %d%nMedium (500 to 2000): %d%n" +
                "High (more than 2000): %d%n%n", low, medium, high);
    }
}
