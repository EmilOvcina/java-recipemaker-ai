package src.build;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static String[] locations = new String[]{"Fridge", "Stove", "Sink", "Cupboard", "Table"};
    public static Agent agent;
    public static World world;

    public static void main(String[] args) {
        /* Initial state setup. */
        agent = new Agent();
        world = new World();
        world.createContainer("Cupboard", "Pot");
        world.createContainer("Cupboard", "Pot2");
        world.createContainer("Cupboard", "Bowl1");
        world.createContainer("Cupboard", "Bowl2");
        world.storage.get("Cupboard").add("ElectricMixer");
        world.storage.get("Fridge").add("Egg");
        world.storage.get("Fridge").add("Milk");
        world.storage.get("Cupboard").add("Pasta");
        world.storage.get("Fridge").add("Sausages");
        world.storage.get("Fridge").add("Ketchup");
        StateSpace init = new StateSpace(agent, world);

        /* Running the solve on all actions given by the input.txt file. */
        List<Action> actions = parseInput(readInput(args[0]));
        long startTime = System.nanoTime();
        System.out.println(" ");
        System.out.println(solve(actions, init));
        long endTime = System.nanoTime();
        System.out.println("\n");
        System.out.println("Time: " + ((endTime - startTime) / 1000000) + "ms");
    }

    /**
     * @param actions List of all actions.
     * @param first Initial state.
     * @return A solution to the list of actions.
     */
    private static List<String> solve(List<Action> actions, StateSpace first) {
        StateSpace initial = first;
        for(Action a : actions) {
            GoalState gs = createGoalState(a);
            StateTree st = new StateTree(initial, gs);
            st.iddfs();
            world = st.getSolutionSS().getWorld().clone();
            agent = st.getSolutionSS().getAgent().clone();
            StateSpace tmp = st.getSolutionSS().clone();
            tmp.setChildren(new ArrayList<>());

            /* Atomic functions: */
            if(a.getName().equals("sep_egg")) {
                tmp.actionSequence.add("SeperateEgg(" + a.getIngredients() + ", "+ a.getContainer() + ")");
                tmp.getAgent().seperateEgg(world, a.getIngredients(), a.getContainer());
            } else if (a.getName().equals("pour")) {
                tmp.actionSequence.add("PourContent(" + a.getIngredients() + ", " + a.getContainer() + ")");
                tmp.getAgent().pourContent(world, a.getContainer());
            } else if (a.getName().equals("whip")) {
                tmp.actionSequence.add("Whip(" + a.getContainer() + ")");
                tmp.getAgent().whip(world, a.getContainer());
            } else if (a.getName().equals("boil")) {
                tmp.actionSequence.add("Boil(" + a.getContainer() + ")");
                tmp.getAgent().boil(world, a.getContainer());
            } else if (a.getName().equals("drain")) {
                tmp.actionSequence.add("Drain(" + a.getContainer() + ")");
                tmp.getAgent().drain(world);
            } else if (a.getName().equals("add")) {
                tmp.actionSequence.add("Add("+ a.getIngredients() + ", " + a.getContainer() + ")");
                tmp.getAgent().addToContainer(world, a.getIngredients(), Utils.findPairInList(tmp.getWorld().containers.get(tmp.getAgent().location), a.getContainer()));
            }
            initial = tmp;
        }
        return initial.actionSequence;
    }

    /**
     * @param a Action parsed by the input.
     * @return The respective goal state corresponding of the result from an action.
     */
    private static GoalState createGoalState(Action a) {
        World goalWorld = new World();
        Agent goalAgent = new Agent();
        goalAgent.holds = "";
        goalAgent.holdsContainer = new Pair("", new ArrayList<>());

        /* Finds the items in the action's container. */
        List<String> ingTmp = new ArrayList<>();
        String loc = "";
        for(String s : locations)
            for(Pair p : world.containers.get(s))
                if(p.getLeft().equals(a.getContainer())) {
                    loc = s;
                    break;
                }
        if(!loc.equals("Stove") && !loc.equals("Table"))
            loc = "Table";

        /* These inputs doesn't have an ingredients. */
        if(!a.getName().equals("pour") && !a.getName().equals("sep_egg") && !a.getName().equals("drain"))
            if(!a.getIngredients().equals(""))
                ingTmp.add(a.getIngredients());
        if(Utils.findPairInList(world.containers.get(loc), a.getContainer()).getRight().size() > 0) {
            ingTmp.addAll(Utils.findPairInList(world.containers.get(loc), a.getContainer()).getRight());
        }

        /* This creates the goal state after what the action name is. */
        switch(a.getName()) {
            case "pour":
                String locat = "";
                String toC = a.getIngredients();
                List<String> secondCont = new ArrayList<>();
                for(String s : locations)
                    for(Pair p : world.containers.get(s)) {
                        if(p.getLeft().equals(toC))
                            secondCont = new ArrayList<>(p.getRight());
                        else if (p.getLeft().equals(a.getContainer()))
                            locat = s;
                    }
                if(agent.holdsContainer.getLeft().equals(toC)) {
                    secondCont = new ArrayList<>(agent.holdsContainer.getRight());
                }
                goalAgent.location = locat;
                goalAgent.holdsContainer = new Pair(toC, secondCont);
                break;
            case "boil":
                List<String> boilList = new ArrayList<>(ingTmp);
                if(!boilList.contains("Water"))
                    boilList.add("Water");
                goalWorld.containers.get("Stove").add(new Pair(a.getContainer(), boilList));
                goalAgent.location = "Stove";
                break;
            case "add":
                goalWorld.storage.get(loc).add(a.getContainer());
                goalAgent.holds = a.getIngredients();
                goalAgent.location = loc;
                break;
            case "whip":
                goalWorld.storage.get("Table").add(a.getContainer());
                goalAgent.location = "Table";
                goalAgent.holds = "ElectricMixer";
                break;
            case "drain":
                goalAgent.location = "Sink";
                goalWorld.storage.get("Sink").add(a.getContainer());
                break;
            case "sep_egg":
                goalAgent.location = "Table";
                goalAgent.holds = "Egg";
                break;
            case "takeC":
                goalWorld.storage.get(a.getContainer()).add(a.getIngredients());
                goalAgent.location = "Table";
                break;
            default:
                break;
        }
        return new GoalState(goalAgent, goalWorld);
    }

    /**
     * @param input String of inputs parsed from the file.
     * @return List of actions split up into ingredients, containers and actions.
     */
    private static List<Action> parseInput(List<String> input) {
        List<Action> actions = new ArrayList<>();
        for(String s : input) {

            String[] args = s.split(" ");
            String actionName = (args[0]).toLowerCase();
            String container = args[args.length-1];

            /* These inputs doesn't have an ingredient list */
            if(actionName.equals("sep_egg")) {
                String fromContainer = args[args.length-2];
                actions.add(new Action("takeC", "Table", container));
                actions.add(new Action("takeC", "Table", fromContainer));
                actions.add(new Action(actionName, container, fromContainer));
                continue;
            }
            if(actionName.equals("whip")) {
                actions.add(new Action("whip", container, ""));
                continue;
            }
            if(actionName.equals("pour")) {
                String fromContainer = args[args.length-2]; //second last item in string
                actions.add(new Action(actionName, container, fromContainer));
                continue;
            }
            if(actionName.equals("drain")) {
                actions.add(new Action("drain", container, ""));
                continue;
            }
            /* These inputs does have a ingredient list*/
            if(actionName.equals("add")) {
                for (int i = 1; i < args.length-1; i++) {
                    actions.add(new Action(actionName, container, args[i]));
                }
            } else if (actionName.equals("boil")) {
                /* Split up. First adding all the ingredients to the pot, and then boiling the pot with water. */
                for (int i = 1; i < args.length-1; i++) {
                    actions.add(new Action("add", container, args[i]));
                }
                actions.add(new Action(actionName, container, ""));
            } else {
                actions.add(new Action(actionName, container, ""));
            }
        }
        return actions;
    }

    /**
     * Returns a list of input strings. 
     * Filters out comments (lines starting with '//'), and empty lines.
     */
    private static List<String> readInput(String fileName) {
        List<String> output = new ArrayList<>();
        try {
            File f = new File(fileName);
            Scanner scanner = new Scanner(f);
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(!line.startsWith("//") && line.length() > 1)
                    output.add(line);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return output;
    }
}