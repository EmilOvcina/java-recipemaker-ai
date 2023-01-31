package src.build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World 
{
    public Map<String, List<String>> storage;
    public Map<String, List<Pair>> containers; //Location, <Name, IngredientsList>

    /**
     * Constructor.
     * Initialises the storage and container Maps, and adds the keys of each location in the world.
     */
    public World() {
        storage = new HashMap<>();
        containers = new HashMap<>();
        containers.put("Cupboard", new ArrayList<>());
        containers.put("Stove", new ArrayList<>());
        containers.put("Table", new ArrayList<>());
        containers.put("Fridge", new ArrayList<>());
        containers.put("Sink", new ArrayList<>());
        storage.put("Cupboard", new ArrayList<>());
        storage.put("Fridge", new ArrayList<>());
        storage.put("Table", new ArrayList<>());
        storage.put("Stove", new ArrayList<>());
        storage.put("Sink", new ArrayList<>());
    }

    /**
     * Adds a container to the map of containers.
     * @param location Location of where the container is.
     * @param name Name of the container.
     */
    public void createContainer(String location, String name) {
        List<Pair> list = new ArrayList<>();
        if(containers.get(location).size() > 0)
            list = containers.get(location);
        list.add(new Pair(name, new ArrayList<>()));
        containers.put(location, list);
        storage.get(location).add(name);
    }
    
    /**
     * Makes a copy of the world.
     */
    public World clone() {
        World w = new World();
        w.storage = Utils.copyMap(storage);
        w.containers = Utils.copyMap(containers);
        return w;
    }

    /**
     * @param world World object to check.
     * @return True if the input world is equal to this world object.
     */
    public boolean equals(World world) {
        boolean b1 = this.storage.equals(world.storage);
        return b1;
    }
}