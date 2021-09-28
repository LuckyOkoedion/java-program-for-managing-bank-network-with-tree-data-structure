package com.okoedion_lucky;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class Question5 {

    String configFilePath = FileSystems.getDefault().getPath("").toAbsolutePath().toString() + "/src/com/okoedion_lucky/teradata-world-setup.txt";
    String firstRequestFilePath = FileSystems.getDefault().getPath("").toAbsolutePath().toString() + "/src/com/okoedion_lucky/client-request-1.txt";
    String secondRequestFilePath = FileSystems.getDefault().getPath("").toAbsolutePath().toString() + "/src/com/okoedion_lucky/client-request-2.txt";
    String thirdRequestFilePath = FileSystems.getDefault().getPath("").toAbsolutePath().toString() + "/src/com/okoedion_lucky/client-request-3.txt";
    String outputFile;
    int[][] confData;
    String[][] requestData;
    BankNode[] topology;
    Request[] clientRequests;

    public static void main(String[] args) throws IOException {

// TODO  USER NOTE 1:   - CHANGE THE "currentInput" VARIABLE BELOW TO ANY "*..RequestFilePath" VARIABLES ABOVE. CURRENT VALUE is "firstRequestFilePath"
//  USER NOTE 2:   - OUTPUT FILE WILL BE IN THE PROJECT ROOT DIRECTORY, check project folder with file explorer if you cant see it with the IDE

//        Create instance
        Question5 question5 = new Question5();
//       Set current input file path
        String currentInput = question5.firstRequestFilePath;

        //Read the config file,
        long configFileLength = question5.getNumberOfLines(question5.configFilePath);
        String[] rawConfInput = question5.readFromFile(question5.configFilePath,configFileLength);
        String[] theConfInput = Arrays.copyOfRange(rawConfInput, 1, rawConfInput.length);

//        construct config data into two dimensional array

        question5.confData = new int[theConfInput.length][3];


        for(int i = 0; i < theConfInput.length; i++) {
            String rawLine = theConfInput[i];
            String[] rawLineArray = rawLine.split(",");
            int[] line = new int[3];
            for(int j=0; j<3; j++) {
                line[j] = Integer.parseInt(rawLineArray[j]);
            }
            question5.confData[i] = line;
        }

//        Create Bank Nodes as BankNode
//        And build the tree (topology) as BankNode[]

        question5.topology = new BankNode[theConfInput.length];

        for(int i=0; i<question5.confData.length; i++){
            int[] column = question5.confData[i];
            int parent = column[0];
            int id = column[1];
            double prob = ((double) column[2]) / 100;
            BankNode theNode = new BankNode(parent, id, prob);
            question5.topology[i] = theNode;
        }



        //Read the input file,
        long inputFileLength = question5.getNumberOfLines(currentInput);
        String[] theInput = question5.readFromFile(currentInput,inputFileLength);

//        construct input data into two dimensional array

        question5.requestData = new String[theInput.length][3];


        for(int i = 0; i < theInput.length; i++) {
            String rawLine = theInput[i];
            String[] rawLineArray = rawLine.split(",");
            String[] line = new String[3];
            for(int j=0; j<3; j++) {
                line[j] = rawLineArray[j];
            }
            question5.requestData[i] = line;
        }

//        Create Request
//        And construct the clientRequests data structure as Request[]

        question5.clientRequests = new Request[theInput.length];

        for(int i=0; i<question5.requestData.length; i++){
            String[] column = question5.requestData[i];
            int firstBank = Integer.parseInt(column[0]);
            int secondBank = Integer.parseInt(column[1]);
            double thresholdProbability = Double.parseDouble(column[2]);
            Request theRequest = new Request(firstBank, secondBank, thresholdProbability);
            question5.clientRequests[i] = theRequest;
        }

        if(currentInput == question5.firstRequestFilePath) {
            question5.outputFile = "client-response-1.txt";
        }

        if(currentInput == question5.secondRequestFilePath) {
            question5.outputFile = "client-response-2.txt";
        }

        if(currentInput == question5.thirdRequestFilePath) {
            question5.outputFile = "client-response-3.txt";
        }

//        Create output file

        PrintWriter writer = new PrintWriter(question5.outputFile, "UTF-8");

       //  check for valid clientRequest, and write 'YES' or 'NO' to file

        for(int i = 0; i < question5.clientRequests.length; i++) {
            Request currentRequest = question5.clientRequests[i];
            boolean canTransmit = question5.canTransmit(currentRequest, question5.topology);
            if(canTransmit) {
                writer.println("YES");
            } else {
                writer.println("NO");
            }
        }

        writer.close();


    }



//    Function to compare the threshold probability for transmission and the product of probability of both banks

    public boolean canTransmit
            (Request request, BankNode[] theTree)
    {
        boolean result = false;

//        Search for index in theTree where firstBank and secondBank are together(a pair)
//        . If available, compare it's probability with thresholdProb.
//        If it is >= thresholdProb return true else return false.

        int firstSearchIndex = searchForPair(request.firstBank, request.secondBank, theTree);

        if (firstSearchIndex > 0) {
            double theProb = theTree[firstSearchIndex].probability;
            if (theProb >= request.thresholdProbability) {
                result = true;
            } else {
                result = false;
            }
        }
        else {


            if (request.firstBank == 1 || request.secondBank == 1) {

//sum prob of itself to its parent, and that of its parent to 1 and return
//true if >= thresholdProb;

                if(request.firstBank == 1 && request.secondBank > 1) {
//                    Search for secondBank in tree
                    int ownSearchRes = searchForOne(request.secondBank, theTree);
                    double ownProb = theTree[ownSearchRes].probability;
                    int parentSearchRes = searchForOne(theTree[ownSearchRes].parent, theTree);
                    double parentProb = theTree[parentSearchRes].probability;
                    double sumProb = ownProb * parentProb;
                    if(sumProb >= request.thresholdProbability) {
                        result = true;
                    } else {
                        result = false;
                    }
                }



                if(request.secondBank == 1 && request.firstBank > 1) {
//                    Search for firstBank in tree
                    int ownSearchRes = searchForOne(request.firstBank, theTree);
                    double ownProb = theTree[ownSearchRes].probability;
                    int parentSearchRes = searchForOne(theTree[ownSearchRes].parent, theTree);
                    double parentProb = theTree[parentSearchRes].probability;
                    double sumProb = ownProb * parentProb;
                    if(sumProb >= request.thresholdProbability) {
                        result = true;
                    } else {
                        result = false;
                    }
                }


            } else {

//   if neither of the two banks id is 1 ... goes through a longer path
//    return false
result = false;

            }

        }

return result;

    }

//    Function to search for pair
    public  int searchForPair(int firstBank, int secondBank, BankNode[] theTree) {
        int theIndex = 0;

       for(int i = 0; i< theTree.length; i++) {
            int currentNode = theTree[i].id;
            int currentParent = theTree[i].parent;
            if ((currentNode == firstBank && currentParent == secondBank)
                    || (currentNode == secondBank && currentParent == firstBank)) {
                theIndex = i;
                break;
            }
        }

       return theIndex;
    }

    //    Function to search for one
    public  int searchForOne(int theBank, BankNode[] theTree) {
        int theIndex = 0;

        for(int i = 0; i< theTree.length; i++) {
            int currentNode = theTree[i].id;

            if (theBank == currentNode) {
                theIndex = i;
                break;
            }
        }

        return theIndex;
    }

//    Function to read file

    public String[] readFromFile(String filePath, long numberOfItems) {
        int theNo = (int) numberOfItems;
        String[] finalData = new String[theNo];
        int nextIndex = 0;
        String line;
//        read the file

        try {
            FileReader in = new FileReader(filePath);
            BufferedReader br = new BufferedReader(in);
            while ((line = br.readLine()) != null) {
//        construct lines to array of strings
                finalData[nextIndex] = line;
                nextIndex++;
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //        return array
        return finalData;
    }


    public long getNumberOfLines(String filePath) throws IOException {
        Stream<String> theStream = Files.lines( Paths.get(filePath));
        long numberOfLines = theStream.count();
        return numberOfLines;

    }




}
