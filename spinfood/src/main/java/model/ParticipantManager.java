package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The ParticipantManager class is responsible for managing participants, pairs, and successors.
 * It provides functionality to add participants, create pairs, and maintain a list of successors.
 */
public class ParticipantManager {


    protected static List<Participant> participants;
    protected static List<Pair> CSV_Pairs;

    protected static List<Pair> pairs;
    protected static List<Participant> pairSuccessors;
    protected static List<Group> generatedGroups;
    protected static List<Pair> starterSuccessors;

    protected static List<Pair> generatedPairs;
    protected static List<Group> generatedGroupsInStarter;
    protected static List<Group> generatedGroupsInMainDish;
    protected static List<Group> generatedGroupsInDessert;



    protected static Map<Kitchen,Integer> kitchenCountMap;
    public static Map<Kitchen,List<Pair>> kitchenMap;
    public static Location partyLocation = new Location(1,1);


    /**
     * Constructs a new ParticipantManager object.
     * Initializes the lists for participants, pairs and successors.
     */
    public ParticipantManager(){
        participants = new ArrayList<>();
        pairSuccessors = new ArrayList<>();
        pairs = new ArrayList<>();
        generatedPairs = new ArrayList<>();
        kitchenCountMap = new HashMap<>();
        generatedGroups = new ArrayList<>();
        starterSuccessors = new ArrayList<>();
        kitchenMap = new HashMap<>();
        CSV_Pairs = new ArrayList<>();
        generatedGroupsInStarter = new ArrayList<>();
        generatedGroupsInMainDish = new ArrayList<>();
        generatedGroupsInDessert = new ArrayList<>();
    }

    public static List<Group> getGeneratedGroups() {
        return generatedGroups;
    }

    public static List<Participant> getParticipants() {
        return participants;
    }

    public static List<Pair> getCSV_Pairs() {
        return CSV_Pairs;
    }

    public static List<Pair> getPairs() {
        return pairs;
    }

    public static List<Participant> getPairSuccessors() {
        return pairSuccessors;
    }

    public static List<Pair> getStarterSuccessors() {
        return starterSuccessors;
    }

    public static List<Pair> getGeneratedPairs() {
        return generatedPairs;
    }

    public static Map<Kitchen, Integer> getKitchenCountMap() {
        return kitchenCountMap;
    }

    public static Map<Kitchen, List<Pair>> getKitchenMap() {
        return kitchenMap;
    }

    public static List<Group> getGeneratedGroupsInStarter() {
        return generatedGroupsInStarter;
    }

    public static List<Group> getGeneratedGroupsInMainDish() {
        return generatedGroupsInMainDish;
    }

    public static List<Group> getGeneratedGroupsInDessert() {
        return generatedGroupsInDessert;
    }

    public Participant getParticipantById(String id) {
        for (Participant participant : participants) {
            if (participant.getId().equals(id)) {
                return participant;
            }
        }
        return null;
    }

    public Pair getPairById(String id) {
        for (Pair pair : pairs) {
            if (pair.getPerson1().getId().equals(id) || pair.getPerson2().getId().equals(id)) {
                return pair;
            }
        }
        return null;
    }

    public void removeParticipant(Participant participant) {
        participants.remove(participant);
    }

    public Participant getParticipantByName(String name) {
        for (Participant participant : participants) {
            if (participant.getName().equals(name)) {
                return participant;
            }
        }
        return null;
    }



}
