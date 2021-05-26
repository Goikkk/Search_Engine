import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

/**
 * Local search engine. Reads data from files, corrects words and index them.
 * User can specify which words he is looking for or wants to avoid.
 *
 * @author Rafal Karwowski
 */
public class Main {

    final static int numberOfDocuments = 10;

    public static void main(String[] args) {

        // Hash map with all words plus the documents there found in
        Map<String, List<String>> invertedIndex = new HashMap<>();

        // List of all documents names which were checked
        List<String> documents = new ArrayList<>();


        indexation(invertedIndex, documents);
        userQuery(invertedIndex, documents);


    }

    /**
     * Search for words in every document, puts them to the invertedIndex map together with documents list,
     * which contains list of documents in which the word was found.
     *
     * @param invertedIndex map where words and document's name are stored
     * @param documents list of documents which were checked during indexation
     */
    static void indexation(Map<String, List<String>> invertedIndex, List<String> documents)
    {

        String data;
        int nrOfWords = 0;

        // Loop to index words from every document
        for(int i = 1; i <= numberOfDocuments; i++)
        {
            try
            {
                File myObj = new File("documents/doc" + i + ".txt");
                Scanner myReader = new Scanner(myObj);

                // Saving name of the document
                documents.add(String.valueOf(i));

                // Or in this way, if you want the whole name of the documents
                // Also change: list.add(myObj.getName());
                // documents.add(myObj.getName());

                // Reads data from a file, line by line
                while(myReader.hasNextLine())
                {
                    data = myReader.nextLine();

                    // Index on which next word starts
                    int newWordStart = 0;

                    // Go through every character in currently checked line
                    for(int j = 0; j < data.length(); j++)
                    {
                        // If there is space or it's end of a line, detects it as end of a word
                        if (data.charAt(j) == ' ' || j+1 == data.length())
                        {
                            // The word which was found
                            String newWord;

                            // If the newWord consists of two words (e.g. aren't, won't)
                            // the second part of the word is stored here
                            String secondWord;

                            if(data.charAt(j) == ' ')
                            {
                                newWord = data.substring(newWordStart, j);
                            } else
                            {
                                newWord = data.substring(newWordStart, j+1);
                            }

                            // Gets the corrected word from the function correctWord()
                            String[] words = correctWord(newWord);

                            newWord = words[0];
                            secondWord = words[1];


                            // If there is a second word, execute twice
                            for(int k = 0; k < (!(secondWord.isEmpty()) ? 2 : 1); k++)
                            {
                                if(k==1)
                                {
                                    newWord = secondWord;
                                }


                                // List of the documents which already consist this word
                                List<String> reading = invertedIndex.get(newWord);
                                // List with all the documents from reading list and the new one
                                List<String> list = new ArrayList<>();

                                // If there is any document which already contains this word put it to the new list
                                if(reading != null)
                                {
                                    for(String str : reading)
                                    {
                                        if(str.compareTo(String.valueOf(i))!=0)
                                            list.add(str);
                                    }
                                }

                                // At the end add the new document
                                list.add(String.valueOf(i));

                                // Or in this way, if you want the whole name of the documents
                                // Also change: documents.add(myObj.getName());
                                // list.add(myObj.getName());

                                invertedIndex.put(newWord, list);

                            }

                            // Update the information where the next word starts
                            newWordStart = j + 1;
                            nrOfWords++;
                        }
                    }

                }
                myReader.close();
            }catch(FileNotFoundException e){
                System.out.println("An error occurred! Problem with reading a document.");
                e.printStackTrace();
            }
        }

        // Displays invertedIndex
        // /*       // If you want to skip it, uncomment this part
        for(String s : invertedIndex.keySet())
        {
            List<String> reading = invertedIndex.get(s);
            System.out.print("\n" + s);
            for(String str : reading)
            {
                System.out.print(" " + str);
            }
        }

        System.out.println("\n\nNumber of words: " + nrOfWords);

        // */

    }


    /**
     * Checks if the newWord does not contains any different character then letters or digits.
     * Divides the newWord into two parts if necessary.
     *
     * @param newWord the word which we want to check
     * @return corrected word with lower case letters and the second word if exists
     */
    static String[] correctWord(String newWord)
    {

        // If the newWord consists of two words, the second is save here
        String secondWord = "";
        // True if all unwanted characters (e.g. !) are deleted
        boolean allCharacters = false;

        newWord = newWord.toLowerCase();

        // Loop until all unwanted characters from the end of a word are not deleted
        do
        {
            if (newWord.charAt(newWord.length() - 1) == ',' || newWord.charAt(newWord.length() - 1) == '.' ||
                    newWord.charAt(newWord.length() - 1) == '?' || newWord.charAt(newWord.length() - 1) == '!' ||
                    newWord.charAt(newWord.length() - 1) == ')' || newWord.charAt(newWord.length() - 1) == ':' ||
                    newWord.charAt(newWord.length() - 1) == ';')
            {
                newWord = newWord.substring(0, newWord.length() - 1);
            } else
            {
                allCharacters = true;
            }

        }while(!allCharacters);

        // Deletes '(' from the beginning of a word
        if(newWord.charAt(0) == '(')
        {
            newWord = newWord.substring(1);
        }

        // If a word consists of two words, it splits it
        if(newWord.contains("'"))
        {
            if(newWord.contains("'t"))
            {
                if(newWord.contains("won't"))
                {
                    newWord = "will";
                    secondWord = "not";
                }else if(newWord.contains("can't"))
                {
                    newWord = "can";
                    secondWord = "not";
                }else if(newWord.contains("shan't"))
                {
                    newWord = "shall";
                    secondWord = "not";
                }else
                {
                    newWord = newWord.substring(0, newWord.length()-3);
                    secondWord = "not";
                }
            }else if(newWord.contains("i'm"))
            {
                newWord = "i";
                secondWord = "am";
            }else if(newWord.contains("'re"))
            {
                newWord = newWord.substring(0, newWord.length()-3);
                secondWord = "are";
            }else if(newWord.contains("'s"))
            {
                if(newWord.contains("he's") || newWord.contains("she's") || newWord.contains("it's"))
                {
                    // Can't check if it's "is" or "has". The same with "she'd".
                }else if(newWord.contains("let's"))
                {
                    newWord = "let";
                    secondWord = "us";
                }else
                {
                    newWord = newWord.substring(0, newWord.length()-2);
                    secondWord = "is";
                }
            }else if(newWord.contains("'ll"))
            {
                newWord = newWord.substring(0, newWord.length()-3);
                secondWord = "will";
            }
        }

        return new String[] {newWord, secondWord};

    }


    /**
     * Asks user for the input. Which words does he want to find and avoid.
     * Then checks which documents contains, or not, them.
     *
     * @param invertedIndex map of indexed words
     * @param documents list of documents which were checked during indexation
     */
    public static void userQuery(Map<String, List<String>> invertedIndex, List<String> documents)
    {

        Scanner input = new Scanner(System.in);
        Stack<String> stackSearch = new Stack<>();
        Stack<String> stackAvoid = new Stack<>();

        // Stores input
        String word;

        // Number of words which should be searched and avoided
        float searchingWords = 0;

        System.out.println("\nThe input is taken until you put \"0\".");

        // Input for words which are searched
        do {

            System.out.print("Pass the word you are looking for: ");
            word = input.nextLine();

            if(word.equals("0"))
                break;

            stackSearch.add(word);
            searchingWords++;

        }while (true);

        System.out.print("\nDo you want to avoid any words? (0/1) ");
        word = input.nextLine();

        // Input for the words which are avoided
        while (!word.equals("0"))
        {

            System.out.print("Pass the word you want to avoid: ");
            word = input.nextLine();

            if(word.equals("0"))
                break;

            stackAvoid.add(word);
            searchingWords++;

        }


        if(stackSearch.empty() && stackAvoid.empty())
        {
            System.out.println("Empty input!");
            return;
        }


        // Map of documents, with list of words they have
        Map<String, List<String>> contains = checkIfDocumentContains(invertedIndex, stackSearch);

        // Map of documents, with list of words they do not avoid from stackAvoid
        Map<String, List<String>> avoids = checkIfDocumentContains(invertedIndex, stackAvoid);


        // List of summaries, stored to sort them later
        List<String[]> documentsSummary = new ArrayList<>();

        // True if there is at least one document, which contains at least one word or does not avoid any word
        boolean foundDocument = false;


        // Checks if there is at least one document, which contains at least one searching word
        if(!contains.isEmpty())
        {
            foundDocument = true;

            for(String s : documents)
            {
                // Getting words which current document contains
                List<String> reading = contains.get(s);

                // Getting words which current document does not avoid
                List<String> readingAvoid = avoids.get(s);

                // Summary of current document:
                // 0 - name of the document
                // 1 - words which it contains
                // 2 - words which it avoids
                // 3 - accuracy
                String[] currentDocument = new String[4];
                currentDocument[0] = s;

                // Counts the number of words which current document contains and avoids to calculate accuracy
                float count = 0;

                // Words which are not in a document, have strike through to highlight it for user
                String doesNotContain = "";
                boolean doesItNotContain = false;
                currentDocument[1] = "";


                // Goes through every searched word and checks if current document contains it or not.
                // If it does, saves it to summary, otherwise writes it to doesNotContain and
                // makes strike through later.
                for(String str : stackSearch)
                {
                    if(reading != null)
                    {
                        if(reading.contains(str))
                        {
                            currentDocument[1] += " " + str;
                            count++;
                        } else
                        {
                            doesNotContain += " " + str;
                            doesItNotContain = true;
                        }

                    } else
                    {
                        doesNotContain += " " + str;
                        doesItNotContain = true;
                    }

                }

                // Adds words, which document does not contain, to summary with a strike through
                if(doesItNotContain)
                {
                    currentDocument[1] += strikeThrough(doesNotContain + " ");
                }


                // Words which are not avoided, have strike through to highlight it for user
                String doesNotAvoid = "";
                boolean doesItNotAvoid = false;
                currentDocument[2] = "";


                // Goes through every avoided word and checks if current document avoids it or not.
                // If it does, saves it to summary, otherwise writes it to doesNotAvoid and
                // makes strike through later.
                for(String str : stackAvoid)
                {
                    if(readingAvoid != null)
                    {
                        if(!readingAvoid.contains(str))
                        {
                            currentDocument[2] += " " + str;
                            count++;
                        } else
                        {
                            doesNotAvoid += " " + str;
                            doesItNotAvoid = true;
                        }

                    } else
                    {
                        currentDocument[2] += " " + str;
                        count++;
                    }

                }

                // Adds words, which document does not avoids, to summary with a strike through
                if(doesItNotAvoid)
                {
                    currentDocument[2] += strikeThrough(doesNotAvoid + " ");
                }


                // Calculating the accuracy
                currentDocument[3] = String.valueOf(Math.round((count / searchingWords) * 100.0));

                // Saving summary of current document to list of documents
                documentsSummary.add(currentDocument);

            }

        }
        // Checks if there is at least one document, which does not avoid at least one word
        else if(!avoids.isEmpty())
        {
            foundDocument = true;


            for(String s : documents)
            {
                // Getting words which current document does not avoid
                List<String> readingAvoid = avoids.get(s);

                // Summary of current document:
                // 0 - name of the document
                // 1 - words which it contains (empty)
                // 2 - words which it avoids
                // 3 - accuracy
                String[] currentDocument = new String[4];
                currentDocument[0] = s;

                // Counts the number of words which current document avoids to calculate accuracy
                float count = 0;

                // Words which are in a document, but should be avoided,
                // have strike through to highlight it for user
                String doesNotAvoid = "";
                boolean doesItNotAvoid = false;
                currentDocument[2] = "";

                // Goes through every avoided word and checks if current document avoids it or not.
                // If it does, saves it to summary, otherwise writes it to doesNotAvoid and
                // makes strike through later.
                for(String str : stackAvoid)
                {
                    if(readingAvoid != null)
                    {
                        if(!readingAvoid.contains(str))
                        {
                            currentDocument[2] += " " + str;
                            count++;
                        } else
                        {
                            doesNotAvoid += " " + str;
                            doesItNotAvoid = true;
                        }

                    } else
                    {
                        currentDocument[2] += " " + str;
                        count++;
                    }

                }

                // Adds words, which document does not avoids, to summary with a strike through
                if(doesItNotAvoid)
                {
                    currentDocument[2] += strikeThrough(doesNotAvoid + " ");
                }

                // Calculating the accuracy
                currentDocument[3] = String.valueOf(Math.round((count / searchingWords) * 100.0));

                // Saving summary of current document to list of documents
                documentsSummary.add(currentDocument);

            }

        }
        // When stack of searched and avoided words is empty
        else
        {

            if(!stackSearch.empty())
            {
                System.out.println("None of documents contains any of searching words: ");
                for(String str : stackSearch)
                {
                    System.out.print(str + "  ");
                }
                System.out.println();
            }

            if(!stackAvoid.empty())
            {
                System.out.println("All of the documents avoid all of those words: ");
                for(String str : stackAvoid)
                {
                    System.out.print(str + "  ");
                }
            }

        }


        if(foundDocument)
        {
            System.out.println("\nResult: ");

            // Sort and display the summary of searching process
            sort(documentsSummary);
        }

    }


    /**
     * Creates map of documents, which contains words from given stack.
     * @param invertedIndex map of indexed words
     * @param stackSearch stack of words which are checked in the map
     * @return map of documents with list of words each of them contains from the stack
     */
    static Map<String, List<String>> checkIfDocumentContains(Map<String, List<String>> invertedIndex, Stack<String> stackSearch)
    {
        Map<String, List<String>> contains = new HashMap<>();

        for(String s : stackSearch)
        {
            if(invertedIndex.containsKey(s))
            {
                // List of documents which contains s word
                List<String> reading = invertedIndex.get(s);

                // For every document which contains s word checks if it already contains any other word
                // and adds it to the list
                for(String r : reading)
                {
                    List<String> readingFromConsist = new ArrayList<>();

                    if(contains.containsKey(r))
                    {
                        readingFromConsist = contains.get(r);
                    }

                    readingFromConsist.add(s);

                    contains.put(r, readingFromConsist);
                }
            }
        }

        return contains;

    }


    /**
     * Sorts documents which consist searched words by the accuracy.
     * @param documentsSummary list of documents to sort
     */
    static void sort(List<String[]> documentsSummary)
    {

        String[][] toSort = new String[documentsSummary.size()][4];
        int i = 0;

        // Copying from list to an array
        for(String[] d : documentsSummary)
        {
            toSort[i] = d;
            i++;
        }

        // Sorting from the highest accuracy to the lowest
        Arrays.sort(toSort, (a, b) -> Integer.compare(Integer.parseInt(b[3]),Integer.parseInt(a[3])));

        displaySearch(toSort);

    }

    /**
     * Displays the results of the search.
     * @param documentsSummary documents which contains the searched words
     */
    static void displaySearch(String[][] documentsSummary)
    {
        for (String[] strings : documentsSummary) {
            if (Integer.parseInt(strings[3]) > 0) {
                System.out.print("Document \"" + strings[0] + "\"");
                if (strings[1] != null)
                    System.out.print("\nContains:" + strings[1]);

                if (!strings[2].equals(""))
                    System.out.print("\nAvoids:" + strings[2]);

                System.out.println("\nAccuracy: " + strings[3] + "%\n");
            }

        }
    }


    /**
     * Changes given string to text with strikethrough
     * @param s text which should be changed
     * @return text with strikethrough
     */
    static String strikeThrough(String s)
    {
        String ready="" + s.charAt(0) + s.charAt(1);

        for(int i = 2; i<s.length(); i++)
        {
            ready += "\u0336" + s.charAt(i);
        }

        return ready;
    }

}
