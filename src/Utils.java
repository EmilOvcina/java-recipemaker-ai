package src.build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils 
{
    /**
     * Used for deep copy of a Map object, where the values are Lists.
     * @param map Map to deep copy.
     * @return A deep copy of the Map.
     */
    public static <S,L> Map<S, List<L>> copyMap(Map<S, List<L>> map) {
        Map<S, List<L>> result = new HashMap<>();
        for(Map.Entry<S, List<L>> entry : map.entrySet()) {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return result;
    }

    /**
     * Finds a Pair object, by string value, in a list of Pair objects.
     * @param list List of Pair objects to search through.
     * @param name 'Left' value of the Pair to search for
     * @return Pair object matching the search string.
     */
    public static Pair findPairInList(List<Pair> list, String name) {
        for(int i = 0; i < list.size(); i++)
            if(list.get(i).getLeft().equals(name))
                return list.get(i);
        return new Pair();
    }

    /**
     * Turns a string into a List<String> containing that string.
     * @param s String to add to list.
     * @return List of strings containing input string.
     */
    public static List<String> toStringList(String s) {
        List<String> l = new ArrayList<>();
        l.add(s);
        return l;
    }

    /**
     * Turns an array of strings into a List<String> containing all the strings in the array.
     * @param s Array of string to turn into a List.
     * @return List of strings containing all input strings.
     */
    public static List<String> toStringList(String[] s) {
        List<String> l = new ArrayList<>();
        for(String st : s)
            l.add(st);
        
        return l;
    }
}
