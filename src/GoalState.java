package src.build;

public class GoalState 
{
    private Agent goalAgent;
    private World goalWorld;
    private static String[] locations = new String[]{"Fridge", "Stove", "Sink", "Cupboard", "Table"};

    public GoalState(Agent agent, World goalWorld) {
        this.goalAgent = agent;
        this.goalWorld = goalWorld;
    }

    /**
     * @param ss StateSpace to check.
     * @return True if the spatespace contains everything this goal state contains.
     */
    public boolean checkForGoal(StateSpace ss) {
        if(!ss.getAgent().equals(goalAgent) && goalAgent.location != "")
            return false;
        for(String s : locations) {
            if(!ss.getWorld().storage.get(s).containsAll(goalWorld.storage.get(s)))
                return false;
        }
        for(String s : locations) {
            if(!ss.getWorld().containers.get(s).containsAll(goalWorld.containers.get(s))) {
                return false;
            }
        }
        return true;
    }

    public Agent getGoalAgent() {
        return this.goalAgent;
    }

    public World getGoalWorld() {
        return this.goalWorld;
    }
}