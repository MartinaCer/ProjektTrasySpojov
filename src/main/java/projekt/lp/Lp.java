package projekt.lp;

import lpsolve.*;

public class Lp {

    public void run() {
        try {
            // Create a problem with 4 variables and 0 constraints

            LpSolve solver = LpSolve.readXLI("xli_CPLEX", "model.txt", null, "", LpSolve.NORMAL);
            if (solver != null) {
                int a = 0;
                solver.solve();
            } else {
                int b = 2;
            }

        }
        catch (LpSolveException e) {
            e.printStackTrace();
        }
    }
}
