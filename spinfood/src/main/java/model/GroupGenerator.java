package model;

import controller.Distance;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GroupGenerator extends ParticipantManager {
    PartyLocation partyLocation = new PartyLocation();
    public static Map<Pair,Location> kitchenLocationsInStarter = new HashMap<>();
   public static Map<Pair,Location> kitchenLocationsInMainDish = new HashMap<>();
    public static Map<Pair,Location> kitchenLocationsInDessert = new HashMap<>();
    Set<Pair> hashSetInStarter = new HashSet<>();
    Set<Pair> hashSetInMainDish = new HashSet<>();
    Set<Pair> hashSetInDessert = new HashSet<>();


    /**
     * Generates groups by calling specific methods for generating starter, main dish, and dessert groups.
     *
     * @param pairs  The list of pairs to generate groups from.
     * @param radius The radius within which the pairs' kitchens should be located.
     */
    public void callGroupsGenerator(List<Pair> pairs, double radius) throws FileNotFoundException {
        kitchenLocationsInStarter = new HashMap<>();
        kitchenLocationsInMainDish = new HashMap<>();
        kitchenLocationsInDessert = new HashMap<>();
        generatedGroups = new ArrayList<>();
        List<Group> groups = makeStarterGroups(pairs, radius);
        List<Pair> pairsInStarter = new ArrayList<>(hashSetInStarter);
        //List<Pair> pairsInStarter = groups.stream().flatMap(x -> x.getPairsInGroup().stream()).collect(Collectors.toCollection(ArrayList::new));
        makeMainDishGroups(pairsInStarter, radius);
        makeDessertGroups(pairsInStarter);
        System.out.println("Number of generated groups in starter is " + getGeneratedGroupsinStarter().size());
        System.out.println("Number of generated groups in Maindish is " + getGeneratedGroupsInMainDish().size());
        System.out.println("Number of generated groups in dessert -> " + getGeneratedGroupsInDessert().size());
        if( hashSetInStarter.size() == hashSetInMainDish.size()  && hashSetInStarter.size() == hashSetInDessert.size()){
            System.out.println(hashSetInStarter.size());
            System.out.println(hashSetInMainDish.size());
            System.out.println(hashSetInDessert.size());
        }else {
            starterSuccessors.stream().peek(x-> x.setHaveCooked(false)).peek(x-> x.setMetPairsInStarter(new ArrayList<>())).peek(x-> x.setMetPairsInMainDish(new ArrayList<>())).peek(x-> x.setMetPairsInDessert(new ArrayList<>()));
            List<Pair> toRemove = hashSetInStarter.stream().filter(x-> !hashSetInMainDish.contains(x) && !hashSetInDessert.contains(x)).collect(Collectors.toCollection(ArrayList::new));
            if(toRemove.size() > 0){
//                System.out.println("both");
//                toRemove.stream().forEach(x -> System.out.println(x.getMainFoodPreference()));
                Pair pair = toRemove.get(0);
                pair = Stream.of(pair)
                        .map(x -> {
                            x.setHaveCooked(false);
                            x.setMetPairsInStarter(new ArrayList<>());
                            x.setMetPairsInMainDish(new ArrayList<>());
                            x.setMetPairsInDessert(new ArrayList<>());
                            return x;
                        })
                        .findFirst()
                        .orElse(null);
                hashSetInStarter.remove(pair);
                starterSuccessors.add(pair);
            }else{
                toRemove = hashSetInStarter.stream().filter(x-> !hashSetInMainDish.contains(x) || !hashSetInDessert.contains(x)).collect(Collectors.toCollection(ArrayList::new));
                if(toRemove.size() > 0){
//                    System.out.println("single");
//                    toRemove.stream().forEach(x-> System.out.println(x.getMainFoodPreference()));
                    Pair pair = toRemove.get(1);
                    pair = Stream.of(pair)
                            .map(x -> {
                                x.setHaveCooked(false);
                                x.setMetPairsInStarter(new ArrayList<>());
                                x.setMetPairsInMainDish(new ArrayList<>());
                                x.setMetPairsInDessert(new ArrayList<>());
                                return x;
                            })
                            .findFirst()
                            .orElse(null);
                    hashSetInStarter.remove(pair);
                    starterSuccessors.add(pair);
                }
            }
            hashSetInStarter.add(starterSuccessors.get(0));
            List<Pair> newPairs = hashSetInStarter.stream().peek(x-> x.setHaveCooked(false)).peek(x-> x.setMetPairsInStarter(new ArrayList<>()))
                    .peek(x-> x.setMetPairsInMainDish(new ArrayList<>()))
                    .peek(x-> x.setMetPairsInDessert(new ArrayList<>()))
                    .collect(Collectors.toCollection(ArrayList::new));
//            pairsSortedBasedOnDistance(newPairs);
            callGroupsGenerator(newPairs,1);
    }
    }


    /**
     * Generates groups for the starter course based on the given pairs and radius.
     *
     * @param pairs  The list of pairs to generate groups from.
     * @param radius The radius within which the pairs' kitchens should be located.
     * @return The list of generated groups for the starter course.
     */
    public List<Group> makeStarterGroups(List<Pair> pairs, double radius) {
        hashSetInStarter = new HashSet<>();
        Map<Group,Double> map = new HashMap<>();

        for (int i = 0; i < pairs.size(); i++) {
            Pair pair1 = pairs.get(i);
            for (int j = i + 1; j < pairs.size(); j++) {
                Pair pair2 = pairs.get(j);
                for (int k = j + 1; k < pairs.size(); k++) {
                    Pair pair3 = pairs.get(k);
                    if (checkGroupFoodPreference(pair1, pair2, pair3) && checkNotTwoMeetNone(pair1,pair2,pair3)
                             && checkTwoKitchenWithin(pair1.getKitchen(), pair2.getKitchen(), pair3.getKitchen(), radius)
                    ) {
                        Group group = new Group(pair1, pair2, pair3,Course.first);
                        map.put(group, 1.0);
                    }
                }
            }
        }
        List<Group> list = map.entrySet().stream().filter(x -> x.getValue() > 0).map(Map.Entry::getKey).collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(list);


        List<Group> resGroup = new ArrayList<>();
        for (Group group : list) {
            if (!hashSetInStarter.contains(group.pair1) && !hashSetInStarter.contains(group.pair2) && !hashSetInStarter.contains(group.pair3)) {
                findWhichPairToCookInStarter(group, pairs);
                group.setPairsWhoMetInStarter(group);
                resGroup.add(group);
                hashSetInStarter.add(group.pair1);
                hashSetInStarter.add(group.pair2);
                hashSetInStarter.add(group.pair3);
            }
        }
        starterSuccessors.addAll(pairs.stream().filter(x -> !hashSetInStarter.contains(x)).toList());
        System.out.println("number starter of validPairs is " + hashSetInStarter.size());
        System.out.println("number of successors in starter " + starterSuccessors.size());
        generatedGroupsinStarter = resGroup;
        generatedGroups.addAll(resGroup);
        return resGroup;
    }


    /**
     * Generates groups for the main dish course based on the given pairs and radius.
     *
     * @param pairs  The list of pairs to generate groups from.
     * @param radius The radius within which the pairs' kitchens should be located.
     * @return The list of generated groups for the main dish course.
     */
    public List<Group> makeMainDishGroups(List<Pair> pairs, double radius) {
        hashSetInMainDish = new HashSet<>();
        List<Group> resGroup = new ArrayList<>();
        Set<Pair> usedPairs = new HashSet<>();
        Map<Group,Double> map = new HashMap<>();
        //Collections.shuffle(pairs);
        for (int i = 0; i < pairs.size() - 2; i++) {
            Pair pair1 = pairs.get(i);
            for (int j = i + 1; j < pairs.size() - 1; j++) {
                Pair pair2 = pairs.get(j);
                for (int k = j + 1; k < pairs.size(); k++) {
                    Pair pair3 = pairs.get(k);
                    if (checkGroupFoodPreference(pair1, pair2, pair3)
                            && checkIfOneOfPairsHaveCooked(pair1, pair2, pair3)
                            && checkNotTwoMeetNone(pair1,pair2,pair3)
//                            && !usedPairs.contains(pair1)
//                            && !usedPairs.contains(pair2)
//                            && !usedPairs.contains(pair3)
                    ) { // kitchen check to after party
                        Group group = new Group(pair1, pair2, pair3,Course.main);
                        double fitness = GroupFitnessEvaluator.evaluateFitnessForMainDish(group);
                        map.put(group,fitness);
//                        if (fitness > 0) {
//                            findWhichPairToCookInMainDish(group, pairs);
//                            group.setPairsWhoMetInMainDish(group);
//                            resGroup.add(group);
//                            usedPairs.add(pair1);
//                            usedPairs.add(pair2);
//                            usedPairs.add(pair3);
//                        }
                    }
                }
            }
        }
        List<Group> list = map.entrySet().stream().filter(x -> x.getValue() > 0).map(Map.Entry::getKey).collect(Collectors.toCollection(ArrayList::new));
        //Collections.shuffle(list);

        for (Group group : list) {
            if (!hashSetInMainDish.contains(group.pair1) && !hashSetInMainDish.contains(group.pair2) && !hashSetInMainDish.contains(group.pair3)) {
                findWhichPairToCookInMainDish(group, pairs);
                group.setPairsWhoMetInMainDish(group);
                resGroup.add(group);
                hashSetInMainDish.add(group.pair1);
                hashSetInMainDish.add(group.pair2);
                hashSetInMainDish.add(group.pair3);
            }
        }
        generatedGroups.addAll(resGroup);
        generatedGroupsInMainDish = resGroup;
        return resGroup;
    }


    /**
     * Generates groups for the dessert course based on the given pairs.
     *
     * @param pairs The list of pairs to generate groups from.
     * @return The list of generated groups for the dessert course.
     */
    public List<Group> makeDessertGroups(List<Pair> pairs) {
        //this.course = Course.dessert;
        hashSetInDessert = new HashSet<>();
        List<Group> resGroup = new ArrayList<>();
        Set<Pair> usedPairs = new HashSet<>();
        Map<Group,Double> map = new HashMap<>();
        //Collections.shuffle(pairs);
        for (int i = 0; i < pairs.size(); i++) {
            Pair pair1 = pairs.get(i);
            for (int j = i + 1; j < pairs.size(); j++) {
                Pair pair2 = pairs.get(j);
                for (int k = j + 1; k < pairs.size(); k++) {
                    Pair pair3 = pairs.get(k);
                    if (checkGroupFoodPreference(pair1, pair2, pair3)
                            && checkNotTwoMeetNone(pair1,pair2,pair3)
//                            && checkIfTwoOfPairsHaveCooked(pair1, pair2, pair3)
//                            && !usedPairs.contains(pair1)
//                            && !usedPairs.contains(pair2)
//                            && !usedPairs.contains(pair3)
                    ) { // kitchen check to after party
                        Group group = new Group(pair1, pair2, pair3,Course.dessert);
                        double fitness = GroupFitnessEvaluator.evaluateFitnessForDessert(group);
                        map.put(group,fitness);
//                        if (fitness > 0) {
//                            findWhichPairToCookInDessert(group, pairs);
//                            group.setPairsWhoMetInDessert(group);
//                            resGroup.add(group);
//                            usedPairs.add(pair1);
//                            usedPairs.add(pair2);
//                            usedPairs.add(pair3);
//                        }
                    }
                }
            }
        }
        List<Group> list = map.entrySet().stream().filter(x -> x.getValue() > 0).map(Map.Entry::getKey).collect(Collectors.toCollection(ArrayList::new));
        //Collections.shuffle(list);

        for (Group group : list) {
            if (!hashSetInDessert.contains(group.pair1) && !hashSetInDessert.contains(group.pair2) && !hashSetInDessert.contains(group.pair3)) {
                findWhichPairToCookInDessert(group, pairs);
                group.setPairsWhoMetInDessert(group);
                resGroup.add(group);
                hashSetInDessert.add(group.pair1);
                hashSetInDessert.add(group.pair2);
                hashSetInDessert.add(group.pair3);
            }
        }
        generatedGroups.addAll(resGroup);
        generatedGroupsInDessert = resGroup;
        return resGroup;
    }

    /**
     * Checks if one of the pairs in a group has cooked.
     *
     * @param pair1 The first pair.
     * @param pair2 The second pair.
     * @param pair3 The third pair.
     * @return True if one of the pairs has cooked, false otherwise.
     */


    private boolean checkIfOneOfPairsHaveCooked(Pair pair1, Pair pair2, Pair pair3) {
        if (pair1.isHaveCooked() && !pair2.isHaveCooked() && !pair3.isHaveCooked()) {
            return true;
        } else if (!pair1.isHaveCooked() && pair3.isHaveCooked() && !pair2.isHaveCooked()) {
            return true;
        } else if (pair2.isHaveCooked() && !pair3.isHaveCooked() && !pair1.isHaveCooked()) {
            return true;
        }
        return false;
    }

    /**
     * Checks if none of the pairs in a group have cooked.
     *
     * @param pair1 The first pair.
     * @param pair2 The second pair.
     * @param pair3 The third pair.
     * @return True if none of the pairs have cooked, false otherwise.
     */
    private boolean checkIfAllPairsDidntCook(Pair pair1, Pair pair2, Pair pair3) {
        return (!pair1.isHaveCooked()) && (!pair2.isHaveCooked()) && (!pair3.isHaveCooked());
    }

    /**
     * Checks if two of the pairs in a group have cooked.
     *
     * @param pair1 The first pair.
     * @param pair2 The second pair.
     * @param pair3 The third pair.
     * @return True if two of the pairs have cooked, false otherwise.
     */
    private boolean checkIfTwoOfPairsHaveCooked(Pair pair1, Pair pair2, Pair pair3) {
        return (pair1.isHaveCooked() && pair2.isHaveCooked() && !pair3.isHaveCooked() && pair1.getCourse() != pair2.getCourse())
                || (pair1.isHaveCooked() && pair3.isHaveCooked() && !pair2.isHaveCooked() && pair1.getCourse() != pair3.getCourse())
                || (pair2.isHaveCooked() && pair3.isHaveCooked() && !pair1.isHaveCooked() && pair2.getCourse() != pair3.getCourse());
    }

    /**
     * Checks if all the pairs in a group have cooked.
     *
     * @param pair1 The first pair.
     * @param pair2 The second pair.
     * @param pair3 The third pair.
     * @return True if two of the pairs have cooked, false otherwise.
     */
    private boolean checkIfAllOfPairsHaveCooked(Pair pair1, Pair pair2, Pair pair3) {
        return pair1.isHaveCooked() && pair2.isHaveCooked() && pair3.isHaveCooked();
    }


    public List<Pair> pairsSortedBasedOnDistance(List<Pair> pairs) throws FileNotFoundException {
        this.partyLocation.readCSVFilePartyLocation("src/main/resources/partylocation.csv");
        double partyLongitude = this.partyLocation.getLongitude();
        double partyLatitude = this.partyLocation.getLatitude();
        Collections.sort(pairs, new Comparator<Pair>() {
            @Override
            public int compare(Pair pair1, Pair pair2) {
                double distance1 = Distance.calculateDistance(pair1.getKitchen().getKitchen_location().getLatitude(),
                        pair1.getKitchen().getKitchen_location().getLongitude(), partyLatitude, partyLongitude);
                double distance2 = Distance.calculateDistance(pair2.getKitchen().getKitchen_location().getLatitude(),
                        pair2.getKitchen().getKitchen_location().getLongitude(), partyLatitude, partyLongitude);
                return Double.compare(distance2, distance1);  // Reverse the order of comparison
            }
        });
        return pairs;

    }


    private boolean didPairsMeet(Pair pair1, Pair pair2, Pair pair3) {
        return (!pair1.getMetPairsInStarter().contains(pair2) && !pair1.getMetPairsInStarter().contains(pair3))
                && (!pair2.getMetPairsInStarter().contains(pair1) && !pair2.getMetPairsInStarter().contains(pair3))
                && (!pair3.getMetPairsInStarter().contains(pair1) && !pair3.getMetPairsInStarter().contains(pair2));
    }

    /**
     * Checks if the food preferences of three pairs in a group match.
     *
     * @param pair1 The first pair.
     * @param pair2 The second pair.
     * @param pair3 The third pair.
     * @return True if the food preferences match, false otherwise.
     */
    public boolean checkGroupFoodPreference(Pair pair1, Pair pair2, Pair pair3) {
        FoodPreference pref1 = pair1.getMainFoodPreference();
        FoodPreference pref2 = pair2.getMainFoodPreference();
        FoodPreference pref3 = pair3.getMainFoodPreference();


        boolean hasMeat = false;
        boolean hasVeggieOrVegan = false;

        if (pref1 == FoodPreference.meat
                || pref2 == FoodPreference.meat
                || pref3 == FoodPreference.meat) {
            hasMeat = true;
        }

        if (pref1 == FoodPreference.veggie || pref1 == FoodPreference.vegan
                || pref2 == FoodPreference.veggie || pref2 == FoodPreference.vegan
                || pref3 == FoodPreference.veggie || pref3 == FoodPreference.vegan) {
            hasVeggieOrVegan = true;
        }

        return !(hasMeat && hasVeggieOrVegan);
    }
    public boolean checkNotTwoMeetNone(Pair pair1,Pair pair2,Pair pair3){
        FoodPreference pref1 = pair1.getMainFoodPreference();
        FoodPreference pref2 = pair2.getMainFoodPreference();
        FoodPreference pref3 = pair3.getMainFoodPreference();

        if(((pref1 == FoodPreference.none || pref1 == FoodPreference.meat) &&
                (pref2 == FoodPreference.meat || pref2 == FoodPreference.none)) ||
                ((pref3 == FoodPreference.none || pref3 == FoodPreference.meat) &&
                        (pref2 == FoodPreference.meat || pref2 == FoodPreference.none)) ||
                ((pref1 == FoodPreference.none || pref1 == FoodPreference.meat) &&
                        (pref3 == FoodPreference.meat || pref3 == FoodPreference.none))) {
            return false;
        }else
            return true;
    }

    /**
     * Finds which pair should cook for a given group based on their kitchen distances to the party location.
     *
     * @param group The group for which to determine the cooking pair.
     * @param pairs The list of pairs.
     */

    private void findWhichPairToCookInStarter(Group group, List<Pair> pairs){
        Pair pair1 = group.pair1;
        Pair pair2 = group.pair2;
        Pair pair3 = group.pair3;
        Location location1 = pair1.getKitchen().getKitchen_location();
        Location location2 = pair2.getKitchen().getKitchen_location();
        Location location3 = pair3.getKitchen().getKitchen_location();
        Course course = Course.first;
//        int index1 = pairs.indexOf(group.pair1);
//        int index2 = pairs.indexOf(group.pair2);
//        int index3 = pairs.indexOf(group.pair3);
       // int minIndex = Math.min(index1, Math.min(index2, index3));
        //minIndex == index1
//        minIndex == index2
        if (!pair1.isHaveCooked()) {
            group.cookingPair = pair1;
            pair1.setHaveCooked(true);
            pair1.setCourse(course);
            group.setKitchen(pair1.getKitchen());
            addPairToKitchenLocationMap(pair1,pair2,pair3,kitchenLocationsInStarter,location1);
        } else if (!pair2.isHaveCooked()) {
            group.cookingPair = pair2;
            group.pair2.setHaveCooked(true);
            group.setKitchen(pair2.getKitchen());
            pair2.setCourse(course);
            addPairToKitchenLocationMap(pair1,pair2,pair3,kitchenLocationsInStarter,location2);
        } else {
            group.cookingPair = pair3;
            pair3.setHaveCooked(true);
            group.setKitchen(pair3.getKitchen());
            pair3.setCourse(course);
            addPairToKitchenLocationMap(pair1,pair2,pair3,kitchenLocationsInStarter,location3);
        }
    }
    private void addPairToKitchenLocationMap(Pair pair1,Pair pair2,Pair pair3,Map<Pair,Location> map,Location location){
        map.put(pair1,location);
        map.put(pair2,location);
        map.put(pair3,location);
    }
    private void findWhichPairToCookInMainDish(Group group, List<Pair> pairs){
        Course course = Course.main;

        List<Pair> pairsWhoDidntCook = group.getPairsInGroup().stream().filter(x -> !x.isHaveCooked()).toList();
        Pair pair1 = pairsWhoDidntCook.get(0);
        Pair pair2 = pairsWhoDidntCook.get(1);
        Location location1 = pair1.getKitchen().getKitchen_location();
        Location location2 = pair2.getKitchen().getKitchen_location();
        int index1 = pairs.indexOf(pair1);
        int index2 = pairs.indexOf(pair2);
        int minIndex = Math.min(index1, index2);

        if (minIndex == index1) {
            pair1.setHaveCooked(true);
            pair1.setCourse(course);
            group.cookingPair = pair1;
            group.setKitchen(pair1.getKitchen());
            addPairToKitchenLocationMap(group.pair1,group.pair2,group.pair3,kitchenLocationsInMainDish,location1);
        } else {
            pair2.setHaveCooked(true);
            pair2.setCourse(course);
            group.cookingPair = pair2;
            group.setKitchen(pair2.getKitchen());
            addPairToKitchenLocationMap(group.pair1,group.pair2,group.pair3,kitchenLocationsInMainDish,location2);
        }
    }

    private void findWhichPairToCookInDessert(Group group, List<Pair> pairs){
        Pair pair1 = group.pair1;
        Pair pair2 = group.pair2;
        Pair pair3 = group.pair3;
        Location location1 = pair1.getKitchen().getKitchen_location();
        Location location2 = pair2.getKitchen().getKitchen_location();
        Location location3 = pair3.getKitchen().getKitchen_location();
        Course course = Course.dessert;
        if(!pair1.isHaveCooked()){
            pair1.setHaveCooked(true);
            pair1.setCourse(course);
            group.cookingPair = pair1;
            group.setKitchen(pair1.getKitchen());
            addPairToKitchenLocationMap(pair1,pair2,pair3,kitchenLocationsInDessert,location1);
        } else if (!pair2.isHaveCooked()) {
            pair2.setHaveCooked(true);
            pair2.setCourse(course);
            group.cookingPair = pair2;
            group.setKitchen(pair2.getKitchen());
            addPairToKitchenLocationMap(pair1,pair2,pair3,kitchenLocationsInDessert,location2);
        }else {
            pair3.setHaveCooked(true);
            pair3.setCourse(course);
            group.cookingPair = pair3;
            group.setKitchen(pair3.getKitchen());
            addPairToKitchenLocationMap(pair1,pair2,pair3,kitchenLocationsInDessert,location3);
        }
    }


    /**
     * Checks if the kitchens of two pairs and the party location are within a given radius.
     *
     * @param kithcen1 The kitchen of the first pair.
     * @param kitchen2 The kitchen of the second pair.
     * @param kitchen3 The kitchen of the third pair.
     * @param radius   The radius within which the kitchens should be located.
     * @return True if the kitchens are within the radius, false otherwise.
     */
    public boolean checkTwoKitchenWithin(Kitchen kithcen1, Kitchen kitchen2, Kitchen kitchen3, double radius) {
        double long1 = kithcen1.getKitchen_location().getLongitude();
        double lat1 = kithcen1.getKitchen_location().getLatitude();
        double lat2 = kitchen2.getKitchen_location().getLatitude();
        double long2 = kitchen2.getKitchen_location().getLongitude();
        double lat3 = kitchen3.getKitchen_location().getLatitude();
        double long3 = kitchen3.getKitchen_location().getLongitude();

        double distance1 = Distance.calculateDistance(lat1, long1, lat2, long2);
        double distance2 = Distance.calculateDistance(lat2, long2, lat3, long3);
        double distance3 = Distance.calculateDistance(lat1, long1, lat3, long3);

        return (distance1 < radius) && (distance2 < radius) && (distance3 < radius);
    }

    public static String makeIndicatorForGroupList(List<Group> groups) throws FileNotFoundException {
        String indicator = "";
        int groupSize = groups.size();
        int successorSize = getstarterSuccessors().size();
        double sexDeviation = 0 ;
        double ageDifference = 0 ;
        double preferenceDeviation = 0 ;
        double pathLength = 0;

        for (Group group : groups  ){
            group.pair1.calculatePathLength(group.pair1);
            group.pair2.calculatePathLength(group.pair2);
            group.pair3.calculatePathLength(group.pair3);
            sexDeviation += group.getSexDeviation();
            ageDifference += group.getAgeDifference();
            preferenceDeviation += group.getPreferenceDeviation();
            pathLength += group.pair1.pathLength + group.pair2.pathLength + group.pair3.pathLength;
        }

        sexDeviation = sexDeviation / groupSize;
        ageDifference = ageDifference / groupSize;
        preferenceDeviation = preferenceDeviation / groupSize;

        DecimalFormat df = new DecimalFormat("#.####");
        return indicator + groupSize + " _ "  + successorSize + " _ " + df.format(sexDeviation)+ " _ " + df.format(ageDifference) + " _ " + df.format(preferenceDeviation) + " _ " + df.format(pathLength);
    }

    public static List<Pair> getstarterSuccessors() {
        return starterSuccessors;
    }


   /** public Object makeIndicatorForGroupList(List<Group> firstGroup, List<String> selectedPreferences) {
        int indicator = 0;
        for (Group group : groups) {
            // Calculate indicator based on selected preferences
            // Example: if (selectedPreferences.contains("Food Preference")) { ... }
        }

        return indicator;
    }
    */
}
