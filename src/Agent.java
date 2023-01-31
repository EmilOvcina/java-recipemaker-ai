package src.build;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Agent
{
    public String location;
    public String holds;
    public Pair holdsContainer;

    public Agent() {
        location = "Stove";
        holds = "";
        holdsContainer = new Pair("", new ArrayList<>());
    }

    /**
     * Predicate for checking if the agent is at a specific location.
     */
    public Predicate<String> At = s -> this.location.equals(s);

    /**
     * @param location Location for the agent to go to.
     * @return True if agent can go to the location.
     */
    public boolean GoTo(String location) {
        if (At.test(location))
                return false;
        this.location = location;
        return true;
    }

    /**
     * @param world Instance of the world object containing all the items.
     * @param item The item to pick up.
     * @return True if the agent is able to pickup the item.
     */
    public boolean TakeItem(World world, String item) {
        if(holds.equals("") && world.storage.get(location).contains(item) && !(item.contains("Bowl") || item.contains("Pot") || item.contains("Pan"))) {
            world.storage.get(location).remove(item);
            holds = item;
            return true;
        }
        return false;
    }

    /**
     * @param world Instance of the world object containing all the containers.
     * @param item The container to pick up.
     * @return True if the agent is able to pickup the container.
     */
    public boolean TakeContainer(World world, String item) {
        if(holdsContainer.getLeft().equals("") && world.storage.get(location).contains(item) && (item.contains("Bowl") || item.contains("Pot") || item.contains("Pan"))) {
            world.storage.get(location).remove(item);

            List<Pair> list = world.containers.get(location);
            int index = -1;
            for(int i = 0; i < list.size(); i++)
                if (list.get(i).getLeft().equals(item))
                    index = i;
    
            this.holdsContainer = new Pair(item, new ArrayList<>(world.containers.get(location).get(index).getRight()));    
            world.containers.get(location).remove(index);
            return true;
        }
        return false;
    }

    /**
     * @param world Instance of the world object containing all the containers.
     * @param item The container to place.
     * @return True if the agent is able to place the container in the world.
     */
    public boolean PlaceContainer(World world, String item) {
        if(holdsContainer.getLeft().equals(item) && (item.contains("Bowl") || item.contains("Pot") || item.contains("Pan"))) {
            if(item.contains("Bowl") && location.equals("Stove"))
                return false;
            world.storage.get(location).add(item);
            world.containers.get(location).add(new Pair(item, holdsContainer.getRight()));
            holdsContainer = new Pair("", new ArrayList<>());
            return true;
        }
        return false;
    }

    /**
     * @param world Instance of the world object containing all the items.
     * @param item The item to place.
     * @return True if the agent is able to place the item in the world.
     */
    public boolean PlaceItem(World world, String item) {
        if(holds.equals(item)) {
            world.storage.get(location).add(item);
            holds = "";
            return true;
        }
        return false;
    }

    /**
     * @param world Instance of the world object containing all the items.
     * @param item The container to fill with water.
     * @return True if the agent is able to fill the item with water.
     */
    public boolean fillWithWater(World world, String item) {
        if(At.test("Sink") && holdsContainer.getLeft().equals(item) && (item.contains("Bowl") || item.contains("Pot") || item.contains("Pan"))) {
            List<String> l = new ArrayList<>(holdsContainer.getRight());
            l.add("Water");
            holdsContainer = new Pair(item, l);
            return true;
        }
        return false;
    }

    /**
     * Atomic function. Shouldn't be added in statespace.
     * @param world Instance of the world object containing all the items.
     * @param item Item to add to the container
     * @param container Container for the item to go into.
     * Add item to container.
     */
    public void addToContainer(World world, String item, Pair container) {
        container.getRight().add(item);
        holds = "";
    }

    /**
     * Atomic function. Shouldn't be added in statespace.
     * @param world Instance of the world object containing all the items.
     * @param container1 The container for the EggWhites to go into.
     * @param container2 The container for the EggYolks to go into.
     * Seperates an egg into two bowls.
     */
    public void seperateEgg(World world, String container1, String container2) {
        holds = "";
        List<Pair> list = world.containers.get(location);
        int index = -1;
        int index2 = -1;
        for(int i = 0; i < list.size(); i++) {
            if (list.get(i).getLeft().equals(container1))
                index = i;
            if (list.get(i).getLeft().equals(container2))
                index2 = i;
        }
        List<String> content1 = new ArrayList<>(list.get(index).getRight());
        List<String> content2 = new ArrayList<>(list.get(index2).getRight());
        content1.add("EggYolks");
        content2.add("EggWhites");
        Pair c1 = new Pair(container1, content1);
        Pair c2 = new Pair(container2, content2);
        list.remove(index);
        list.add(c1);
        list.add(c2);
        list.remove(index2);
    }

    /**
     * Atomic function. Shouldn't be added in statespace.
     * @param world Instance of the world object containing all the items.
     * @param container2 Container in the world.
     * Pours all the content from the container in the agents hand to a container in the world.
     */
    public void pourContent(World world, String container2) {
        List<Pair> list = world.containers.get(location);
        int index2 = -1;
        for(int i = 0; i < list.size(); i++) {
            if (list.get(i).getLeft().equals(container2))
                index2 = i;
        }
        List<String> content1 = new ArrayList<>(holdsContainer.getRight());
        content1.addAll(list.get(index2).getRight());
        Pair c2 = new Pair(container2, content1);
        list.add(c2);
        list.remove(index2);
    }

    /**
     * Atomic function. Shouldn't be added in statespace.
     * @param world Instance of the world object containing all the items.
     * @param container Container to whip.
     * Whips the content in a container.
     */
    public void whip(World world, String container) {
        List<Pair> list = world.containers.get(location);
        int index = -1;
        for(int i = 0; i < list.size(); i++) {
            if (list.get(i).getLeft().equals(container))
                index = i;
        }
        List<String> newContent = new ArrayList<>();
        for(String s : list.get(index).getRight())
            if(!s.startsWith("Whipped") && !s.contains("Water"))
                newContent.add("Whipped"+s);
        list.remove(index);
        list.add(new Pair(container, newContent));
    }

    /**
     * Atomic function. Shouldn't be added in statespace.
     * @param world Instance of the world object containing all the items.
     * @param container Container to boil.
     * Boils the content in the container.
     */
    public void boil(World world, String container) {
        List<Pair> list = world.containers.get(location);
        int index = -1;
        for(int i = 0; i < list.size(); i++) {
            if (list.get(i).getLeft().equals(container))
                index = i;
        }
        List<String> newContent = new ArrayList<>();
        for(String s : list.get(index).getRight())
            if(!s.startsWith("Boiled"))
                newContent.add("Boiled"+s);
        
        world.containers.get("Stove").remove(index);
        world.containers.get("Stove").add(new Pair("Pot", newContent));
    }

    /**
     * Atomic function. Shouldn't be added in statespace.
     * @param world Instance of the world object containing all the items.
     * Drains the water from the container which the agent is holding.
     */
    public void drain(World world) {
        List<String> newContent = new ArrayList<>();
        for(String s : holdsContainer.getRight())
            if(!s.contains("Water"))
                newContent.add(s);
        holdsContainer = new Pair(holdsContainer.getLeft(), newContent);
    }

    /**
     * Makes a new copy of this Agent object.
     */
    public Agent clone() {
        Agent cloned = new Agent();
        cloned.location = this.location;
        cloned.holds = this.holds;
        cloned.holdsContainer = this.holdsContainer.clone();
        return cloned;
    }

    /**
     * Checks if two agents are equal,
     */
    public boolean equals(Agent agent) {
        boolean b1 = agent.location.equals(this.location);
        boolean b2 = agent.holds.equals(this.holds);
        boolean b3 = agent.holdsContainer.equals(this.holdsContainer);
        return b1 && b2 && b3;
    }
}
