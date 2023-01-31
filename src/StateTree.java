package src.build;

import java.util.ArrayList;
import java.util.List;

public class StateTree 
{
    private StateSpace root;
    private GoalState currentGoal;
    private boolean solutionFound;

    private List<String> solution;
    private StateSpace solutionSS;

    public StateTree(StateSpace root, GoalState currentGoal) {
        this.root = root;
        root.setLevel(1);
        this.currentGoal = currentGoal;
        solutionFound = false;
        solution = new ArrayList<>();
    }

    /**
     * Iterative deepening depth-first search.
     * @return List of actions to reach a solution.
     */
    public List<String> iddfs() {
        int depth = 1;
        dfsNode(this.root, depth);    
        while(!solutionFound) {
            resetVisited(this.root, depth);
            depth++;
            dfsNode(this.root, depth);
        }
        return solution;
    }

    /**
     * Recursive depth-first search.
     * @param node Current node to visit.
     * @param max_depth Max depth for the search.
     */
    public void dfsNode(StateSpace node, int max_depth) {
        if(node.getLevel() > max_depth || solutionFound)
            return;
        
        if(!node.isVisited()) {
            solutionFound = visit(node, currentGoal);
            if(solutionFound)  {
                solutionSS = node;
                solution = new ArrayList<>(node.actionSequence);
            }
        }

        node.setVisited(true);

        if(node.getChildren().size() == 0)
            node.setChildren(node.expandStateSpace());

        for(StateSpace n : node.getChildren())
            if(!n.isVisited())
                dfsNode(n, max_depth);
    }

    /**
     * Resets the "visisted" boolean for each node.
     * @param root Node to be reset.
     * @param max_depth Depth of the current search.
     */
    public void resetVisited(StateSpace root, int max_depth) {
        if(root.getLevel() > max_depth)
            return; 
        for(StateSpace s : root.getChildren()) {
            if(s.isVisited())
                s.setVisited(false);
            resetVisited(s, max_depth);
        }
    }

    /**
     * Visit of the node. Checks if the statespace meets the goal state requirements.
     * @param node Node to check.
     * @param goal Goal to compare the node with.
     * @return True if the node meets the goal state requirements.
     */
    public boolean visit(StateSpace node, GoalState goal) {
        return goal.checkForGoal(node);
    }
    
    public StateSpace getSolutionSS() {
        return solutionSS;
    }

    public StateSpace getRoot() {
        return this.root;
    }
}  
