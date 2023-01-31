package src.build;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StateSpace 
{
    private Agent agent;
    private World world;

    private List<StateSpace> children;

    private boolean visited;
    private int level;
    private int weight;

    public List<String> actionSequence;

    private static String[] locations = new String[]{"Fridge", "Stove", "Sink", "Cupboard", "Table"};
    public StateSpace(Agent agent, World world) {
        this.agent = agent.clone();
        this.world = world.clone();
        children = new ArrayList<>();
        actionSequence = new ArrayList<>();
        visited = false;
        level = 0;
        weight = 0;
    }

    /**
     * Generates all the children, or possible states reachable from this state.
     * @return List of children for a node.
     */
    public List<StateSpace> expandStateSpace() {
        List<StateSpace> ss = new ArrayList<>();

        /* GoTo actions. */
        for(String s : locations) { 
            Agent agenttmp = this.agent.clone();
            World worldtmp = this.world.clone();
            String loc = agenttmp.location;
            if(agenttmp.GoTo(s)) {
                StateSpace stateSpace = initExpandedSS(agenttmp, worldtmp);
                stateSpace.actionSequence.add("GoTo(" + loc +", "+ s + ")");
                stateSpace.weight = 10;
                ss.add(stateSpace);
            }
        }

        /* TakeItem actions. */
        if(world.storage.get(agent.location) != null) {
            for(String s : world.storage.get(agent.location)) {
                Agent agenttmp = this.agent.clone();
                World worldtmp = this.world.clone();
                if(agenttmp.TakeItem(worldtmp, s)) {
                    StateSpace stateSpace = initExpandedSS(agenttmp, worldtmp);
                    stateSpace.actionSequence.add("TakeItem(" + s +", "+ agenttmp.location + ")");
                    stateSpace.weight = 5;
                    ss.add(stateSpace);
                }
            }
        }

        /* TakeContainer actions. */
        if(world.storage.get(agent.location) != null) {
            for(String s : world.storage.get(agent.location)) {
                Agent agenttmp = this.agent.clone();
                World worldtmp = this.world.clone();
                if(agenttmp.TakeContainer(worldtmp, s)) {
                    StateSpace stateSpace = initExpandedSS(agenttmp, worldtmp);
                    stateSpace.actionSequence.add("TakeContainer(" + s +", "+ agenttmp.location + ")");
                    stateSpace.weight = 5;
                    ss.add(stateSpace);
                }
            }
        }

        /* Place items. */
        if(!agent.holds.equals("")) {
            Agent agenttmp = this.agent.clone();
            World worldtmp = this.world.clone();
            String item = agenttmp.holds;
            if(agenttmp.PlaceItem(worldtmp, item)) {
                StateSpace stateSpace = initExpandedSS(agenttmp, worldtmp);
                stateSpace.actionSequence.add("PlaceItem(" + item +", "+ agenttmp.location + ")");
                stateSpace.weight = 4;
                ss.add(stateSpace);
            }
        }

        /* Place containers. */
        if(!agent.holdsContainer.getLeft().equals("")) {
            Agent agenttmp = this.agent.clone();
            World worldtmp = this.world.clone();
            Pair item = agenttmp.holdsContainer;
            if(agenttmp.PlaceContainer(worldtmp, item.getLeft())) {
                StateSpace stateSpace = initExpandedSS(agenttmp, worldtmp);
                stateSpace.actionSequence.add("PlaceContainer(" + item.getLeft() +", "+ agenttmp.location + ")");
                stateSpace.weight = 4;
                ss.add(stateSpace);
            }
        }

        /* Fill with water. */
        if(agent.location.equals("Sink") && !agent.holdsContainer.getLeft().equals("")) {
            Agent agenttmp = this.agent.clone();
            World worldtmp = this.world.clone();
            String item = agenttmp.holdsContainer.getLeft();
            if(agenttmp.fillWithWater(worldtmp, item)) {
                StateSpace stateSpace = initExpandedSS(agenttmp, worldtmp);
                stateSpace.actionSequence.add("FillWithWater(" + item + ")");
                stateSpace.weight = 1;
                ss.add(stateSpace);
            }
        }
        return ss;
    }

    /**
     * @param agenttmp Agent object for the statespace.
     * @param worldtmp World object for the statespace.
     * @return New statespace object with parent's action sequence.
     * Avoids writing the same lines for all 'if' statements in the expandStateSpace method.
     */
    private StateSpace initExpandedSS(Agent agenttmp, World worldtmp) {
        StateSpace stateSpace = new StateSpace(agenttmp, worldtmp);
        stateSpace.setLevel(this.getLevel() + 1);
        for(String action : this.actionSequence)
            stateSpace.actionSequence.add(action);
        return stateSpace;
    }

    public World getWorld() {
        return this.world;
    }

    public Agent getAgent() {
        return this.agent;
    }

    /**
     * @return A list, sorted by weight, of the children.
     */
    public List<StateSpace> getChildren() {
        this.children.sort(Comparator.comparing(StateSpace::getWeight));
        return this.children;
    }

    public void setChildren( List<StateSpace> list) {
        this.children = list;
    }

    public boolean isVisited() {
        return this.visited;
    }

    public void setVisited(boolean b) {
        this.visited = b;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getWeight() {
        return weight;
    }

    /**
     * Makes a new copy of the specific statespace.
     */
    public StateSpace clone() {
        StateSpace s = new StateSpace(agent.clone(), world.clone());
        s.children = new ArrayList<>(getChildren());
        s.weight = weight;
        s.level = level;
        s.visited = visited;
        s.actionSequence = new ArrayList<>(actionSequence);
        return s;
    }
}
