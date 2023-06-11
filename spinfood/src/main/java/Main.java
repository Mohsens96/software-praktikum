import com.github.cliftonlabs.json_simple.JsonObject;
import model.*;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static model.CSVFileReader.*;

public class Main {
    public static boolean checkDuplicatesinAllSteps(List<Group> starter,List<Group> main,List<Group> dessert){
        List<Pair> pairsWhoCookInStarter = starter.stream()
                .flatMap(x -> x.getPairsInGroup().stream())
                .filter(Pair::isHaveCooked).toList();
        Set<Pair> hashsetForStarter = new HashSet<>(pairsWhoCookInStarter);
        //System.out.println("check if we dont have duplicates in list of pairs who cook in starter");
        Boolean starterChecker = pairsWhoCookInStarter.size() == hashsetForStarter.size();
        System.out.println(" number of pairs who cook in starter -> " + pairsWhoCookInStarter.size());

        List<Pair> pairsWhoCookInMain = main.stream()
                .flatMap(x -> x.getPairsInGroup().stream())
                .filter(Pair::isHaveCooked).toList();
        Set<Pair> hashsetForMain = new HashSet<>(pairsWhoCookInMain);
        //System.out.println("check if we dont have duplicates in list of pairs who cook in Main");
        Boolean mainChecker = pairsWhoCookInMain.size() == hashsetForMain.size();
        System.out.println(" number of pairs who cook in main -> " + pairsWhoCookInMain.size());

        List<Pair> pairsWhoCookInDessert = dessert.stream()
                .flatMap(x -> x.getPairsInGroup().stream())
                .filter(Pair::isHaveCooked).toList();
        Set<Pair> hashsetForDessert = new HashSet<>(pairsWhoCookInDessert);
        //System.out.println("check if we dont have duplicates in list of pairs who cook in Dessert");
        Boolean dessertChecker = pairsWhoCookInDessert.size() == hashsetForDessert.size();
        System.out.println(" number of pairs who cook in dessert -> " + pairsWhoCookInDessert.size());
        return starterChecker && mainChecker && dessertChecker;

    }
    public static void main(String[] args) throws FileNotFoundException {
       CSVFileReader CSVFileReader = new CSVFileReader();
        PairGenerator pairGenerator = new PairGenerator();
        GroupGenerator groupGenerator = new GroupGenerator();
        PartyLocation partyLocation = new PartyLocation();
        List<Pair> csvPairs = getPairs();
        try {
            CSVFileReader.readCSVFile("teilnehmerliste.csv");
            partyLocation.readCSVFilePartyLocation("partylocation.csv");
            System.out.println(partyLocation.toString());
            //System.out.println(CSVFileReader.toStringParticipants());
            //System.out.println(CSVFileReader.toStringPairs());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("**************************************************");

        int participantsLength = getParticipants().size();
        System.out.println("Length of participants list is: " + participantsLength);

        System.out.println("************************************************");

        int pairLength = getPairs().size();
        System.out.println("Length of pair list is: " + pairLength);

        System.out.println("************************************************");
        int count = participantsLength + pairLength ;
        System.out.println("the count of all members : " + count);

        System.out.println("###############################################");
        System.out.println(getParticipants().size());
//        pairGenerator.calculateWGCount();
//        for (Map.Entry<Kitchen, List<Pair>> entry : ParticipantManager.kitchenMap.entrySet()) {
//            Kitchen kitchen = entry.getKey();
//            List<Pair> pairList = entry.getValue();
//            int pairCount = pairList.size();
//            System.out.println("Kitchen used by " + pairCount + " pair(s)");
//        }

        System.out.println("###############################################");
        List<Pair> initialPair = pairGenerator.generateInitialPopulation(getParticipants());
        System.out.println("number of generated Pairs is " + getGeneratedPairs().size());
        System.out.println("number of successor is " + model.CSVFileReader.getSuccessor().size());
        System.out.println("###############################################");
        System.out.println("number of initial pairs from initial population generator is " + initialPair.size() );
        System.out.println("number of pairs from CSV is " + csvPairs.size());
        System.out.println("###############################################");


        List<Pair> concatenatedlist = pairGenerator.makeAllPairsTogether(initialPair,csvPairs);
        System.out.println("pair kenZahl is -> " + pairGenerator.makeIndicatorForPairsList(concatenatedlist));

        groupGenerator.pairsSortedBasedOnDistance(concatenatedlist);
        System.out.println("number of all Pairs (1ka3) is " + concatenatedlist.size());
        List<Group> starterGroups = groupGenerator.makeStarterGroups(concatenatedlist,1);
        List<Pair> pairsInStarter = starterGroups.stream().flatMap(x -> x.getPairsInGroup().stream()).collect(Collectors.toCollection(ArrayList::new));
        System.out.println("number of pairs in starter is " + pairsInStarter.size());
        System.out.println(pairsInStarter.stream().filter(x-> x.getMetPairsInStarter().size() == 2).count());
        List<Group> mainDishGroup = groupGenerator.makeMainDishGroups(pairsInStarter, 4);
        List<Group> desertGroups = groupGenerator.makeDessertGroups(pairsInStarter);
        System.out.println("Number of generated groups in starter is " + starterGroups.size());
        System.out.println("Number of generated groups in Maindish is " + mainDishGroup.size());
        System.out.println("Number of generated groups in dessert -> " + desertGroups.size());
        //System.out.println("check if we dont have duplicates in pairs who cook in all Steps -> " + checkDuplicatesinAllSteps(starterGroups,mainDishGroup,desertGroups));
        System.out.println("number of pairs in starterMap is " + GroupGenerator.kitchenLocationsInStarter.keySet().size());
        System.out.println("number of pairs in MainDishMap is " + GroupGenerator.kitchenLocationsInMainDish.keySet().size());
        System.out.println("number of pairs in DessertMap is " + GroupGenerator.kitchenLocationsInDessert.keySet().size());

        Pair pair1 = new Pair(true);
        Pair pair2 = new Pair(false);
        Pair pair3 = new Pair(false);
        System.out.println(GroupFitnessEvaluator.checkIfOneOfPairsHaveCooked(pair1,pair2,pair3));

//        for (Pair pair:pairsInStarter
//             ) {
//            pair.calculatePathLength(pair);
//            System.out.println(pair.pathLength);
//        }
        HashMap<Integer,String> map = new HashMap<>();
        map.put(1,"A");
        map.put(1,"B");
        System.out.println(map.entrySet());

    }
}
