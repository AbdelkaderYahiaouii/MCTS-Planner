import fr.uga.pddl4j.planners.AbstractPlanner;
import fr.uga.pddl4j.planners.hsp.HSPPlanner;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.util.Plan;

public class PlannerComparison {

    public static void main(String[] args) {
        
        String domainFilePath = "C:\\Users\\YHI\\Desktop\\MCTS planner\\domain.pddl";

        
        String[] problemFilePaths = {
                "C:\\Users\\YHI\\Desktop\\MCTS planner\\blocksworld\\problem.pddl",
                "C:\\Users\\YHI\\Desktop\\MCTS planner\\depot\\problem.pddl",
                "C:\\Users\\YHI\\Desktop\\MCTS planner\\gripper\\problem.pddl",
                "C:\\Users\\YHI\\Desktop\\MCTS planner\\logistics\\problem.pddl"
        };

        // Run MCTS planner and HSP planner on each problem
        for (String problemFilePath : problemFilePaths) {
            Problem problem = new Problem(domainFilePath, problemFilePath);

            //  MCTS planner
            MCTSWalkerPlanner mctsPlanner = new MCTSWalkerPlanner();
            Plan mctsPlan = mctsPlanner.solve(problem);
            System.out.println("MCTS Planner Result for " + problemFilePath + ":");
            System.out.println(mctsPlan);

            //  HSP planner
            HSPPlanner hspPlanner = new HSPPlanner();
            Plan hspPlan = hspPlanner.solve(problem);
            System.out.println("HSP Planner Result for " + problemFilePath + ":");
            System.out.println(hspPlan);

            // Compare the plans 
            if (mctsPlan.equals(hspPlan)) {
                System.out.println("Plans are equivalent.");
            } else {
                System.out.println("Plans are different.");
            }

            System.out.println("MCTS Planner Visits: " + mctsPlanner.getRootNode().getVisits());
            System.out.println("MCTS Planner Total Reward: " + mctsPlanner.getRootNode().getTotalReward());
        }
    }
}
