# MCTS-Planner
  import fr.uga.pddl4j.planners.AbstractPlanner;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.util.Plan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MCTSWalkerPlanner extends AbstractPlanner {

    private static final int NUM_SIMULATIONS = 1000;

    public MCTSWalkerPlanner() {
        super();
    }

    @Override
    public Plan solve(Problem problem) {
        Node rootNode = new Node(problem.getInitState());
        for (int i = 0; i < NUM_SIMULATIONS; i++) {
            Node selectedNode = select(rootNode);
            int reward = simulate(selectedNode, problem);
            backpropagate(selectedNode, reward);
        }

        Node bestChild = rootNode.getBestChild();
        Plan plan = extractPlan(rootNode, bestChild, problem);
        return plan;
    }

    private Node select(Node node) {
        // Perform tree traversal to select a node for simulation
        while (!node.isTerminal() && node.isFullyExpanded()) {
            node = node.selectChild();
        }
        return node;
    }

    private int simulate(Node node, Problem problem) {
        // Perform a random walk from the selected node
        Random random = new Random();
        Node current = node.expand(problem);
        while (!current.isTerminal()) {
            current = current.getRandomChild(random, problem);
        }

        // Return a reward (for simplicity, return a random reward)
        return random.nextInt(101);  // Random reward between 0 and 100
    }

    private void backpropagate(Node node, int reward) {
        // Backpropagate the result up the tree
        while (node != null) {
            node.updateStats(reward);
            node = node.getParent();
        }
    }

    private Plan extractPlan(Node rootNode, Node bestChild, Problem problem) {
        // Extract the plan from the best child
        Plan plan = new Plan();
        List<String> actions = bestChild.getActions();
        plan.addAll(actions);
        return plan;
    }

    public static void main(String[] args) {
        MCTSWalkerPlanner planner = new MCTSWalkerPlanner();
        planner.parseCommandLine(args);
        planner.run();
    }

    // Node class for the MCTS tree
    private static class Node {
        private Node parent;
        private List<Node> children;
        private int visits;
        private int totalReward;
        private List<String> actions; // Sequence of actions to reach this node
        private boolean terminal;

        public Node(List<String> actions, Node parent) {
            this.parent = parent;
            this.children = new ArrayList<>();
            this.visits = 0;
            this.totalReward = 0;
            this.actions = actions;
            this.terminal = false;
        }

        public boolean isTerminal() {
            return terminal;
        }

        public Node getParent() {
            return parent;
        }

        public boolean isFullyExpanded() {
            // Check if all children have been expanded
            return children.size() == actions.size();
        }

        public Node selectChild() {
           
            
            Random random = new Random();
            return children.get(random.nextInt(children.size()));
        }

        public Node getRandomChild(Random random, Problem problem) {
            // Select a child randomly for simulation
            if (!isFullyExpanded()) {
                List<String> remainingActions = new ArrayList<>(actions);
                remainingActions.removeAll(getChildActions());
                String selectedAction = remainingActions.get(random.nextInt(remainingActions.size()));
                List<String> childActions = new ArrayList<>(getChildActions());
                childActions.add(selectedAction);
                return new Node(childActions, this);
            } else {
                return children.get(random.nextInt(children.size()));
            }
        }

        public Node expand(Problem problem) {
            // Expand the node by adding a new child
            if (!isFullyExpanded()) {
                List<String> remainingActions = new ArrayList<>(actions);
                remainingActions.removeAll(getChildActions());
                String selectedAction = remainingActions.get(0); // Expand the first unexplored action
                List<String> childActions = new ArrayList<>(getChildActions());
                childActions.add(selectedAction);
                Node childNode = new Node(childActions, this);
                children.add(childNode);
                if (childActions.size() == problem.getMaxStep()) {
                    childNode.setTerminal(true); // Mark as terminal if maximum steps reached
                }
                return childNode;
            } else {
                return this; // Node is fully expanded, return itself
            }
        }

        public void updateStats(int reward) {
            visits++;
            totalReward += reward;
        }

        public List<String> getActions() {
            return actions;
        }

        private void setTerminal(boolean terminal) {
            this.terminal = terminal;
        }

        private List<String> getChildActions() {
            if (children.isEmpty()) {
                return new ArrayList<>();
            } else {
                return children.get(0).getActions(); // All children have the same actions
            }
        }
        
        public int getVisits() {
            return visits;
        }

        public int getTotalReward() {
            return totalReward;
        }
    }
}
