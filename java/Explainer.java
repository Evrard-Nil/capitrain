import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.util.ESat;

public class Explainer {

	/**
     * Algorithm 2: Candidate Explanations
     *  - Finds a solution for the first model "model"
     *  - Lists differences between the solved model and the base model "model2"
     *  - Tries to find all constraints preventing an intvar N from having a certain value
     */
    public static void candidateExplanations(Model model1, IntVar[][] attr1, Model model2, IntVar[][] attr2, int sizeX, int sizeY) {

            List<List<IntVar>> candidatesVariable = new ArrayList<>();
            List<ContradictionException> candidatesContradictions = new ArrayList<>();
    		
    		// Look for facts that need to be explained
    		try {
    			propagateToSolution(model1,attr1, sizeX, sizeY);
    		}
    		catch (ContradictionException e) {
    			e.printStackTrace();
    		}
    		// Corresponds to J \ I in algorithm 2
    		List<IntVar> listDifferences = findDifferences(attr2, attr1, sizeX, sizeY);

    		Variable[] modelVars = model2.getVars();

    		List<Constraint> constraintsToUnpost = new ArrayList<>();
    		
    		int nIndex = 0;
    		int nIndexPushed = 0;
    		
    		while (nIndex < listDifferences.size()) {
    			
    			IntVar n = listDifferences.get(nIndex);
    			
    			System.out.println(n);
    			
    			// Find the variable corresponding to the new fact
    			int i = getVarATraiter(modelVars, n);
    			
    			if (model2.getSolver().getEnvironment().getWorldIndex() == 0) {

    				Constraint vConstraint;
    				
    				// Save current state
    				model2.getSolver().getEnvironment().worldPush();
    				nIndexPushed = nIndex;
    				
    				// Create a not_n constraints
					vConstraint = ((IntVar) modelVars[i]).in(notN(n, sizeX)).decompose();
    				
    				constraintsToUnpost.add(vConstraint);
    				IntVar[][] attrStateSnapshot = deepCopy(attr2, sizeX, sizeY);
    				
    				model2.post(vConstraint);

    				try {
    					 model2.getSolver().propagate();
	                } catch (ContradictionException e) {
	                    e.printStackTrace();
	                    candidatesContradictions.add(e);
	                    
	                    // Keep all implied facts
	                    candidatesVariable.add(findDifferences(attrStateSnapshot, attr2, sizeX, sizeY));
	                    model2.getSolver().getEnvironment().worldPop();
	                    nIndex = nIndexPushed;
	                    model2.unpost((Constraint[]) constraintsToUnpost.toArray(new Constraint[constraintsToUnpost.size()]));
	                    model2.post(vConstraint.getOpposite());
	                }
				}
    			else {
    			
    				Constraint vConstraint;
    				
    				// Create a n constraints
					int[] valuesArray = getValues(n, sizeX, sizeY);
					vConstraint = ((IntVar) modelVars[i]).in(valuesArray).decompose();
    				constraintsToUnpost.add(vConstraint);
    				model2.post(vConstraint);
    				try {
   					 	model2.getSolver().propagate();
	                } catch (ContradictionException e) {
	                    e.printStackTrace();
	                    candidatesContradictions.add(e);
	                    
	                    model2.getSolver().getEnvironment().worldPop();
	                    nIndex = nIndexPushed;
	                    model2.unpost((Constraint[]) constraintsToUnpost.toArray(new Constraint[constraintsToUnpost.size()]));
	                    model2.post(vConstraint.getOpposite());
	                }
    			}
    			nIndex++;
    		}
    }

    /**
     * Searches the matrix to find the intVar with the same name as the one passed as parameter
     * @param modelVars
     * @param n
     * @return
     */
	private static int getVarATraiter(Variable[] modelVars, IntVar n) {
		Variable variableATraiter = null;
		int i = -1;
		while (variableATraiter == null) {
			i++;
			if (n.getName().equals(modelVars[i].getName())) {
				variableATraiter = modelVars[i];
			}
		}
		return i;
	}
    
	/**
	 * Makes a deep copy of the IntVar matrix param
	 * @param attr
	 * @return
	 */
    private static IntVar[][] deepCopy(IntVar[][] attr, int sizeX, int sizeY) {
    	
    	IntVar[][] ret = new IntVar[sizeX][sizeY];
    	Model modelCopy = new Model();
    	
    	for (int i = 0 ; i < sizeX ; i ++) {
    		for (int j = 0 ; j < sizeY ; j++) {
    			
    			Iterator<Integer> valuesItr = attr[i][j].iterator();
    			ArrayList<Integer> valuesArrayList = new ArrayList<>();
    			
    			while (valuesItr.hasNext()) {
    				Integer v = valuesItr.next();
    				valuesArrayList.add(v);
    			}
    			
    			int[] valuesArray = valuesArrayList.stream().mapToInt(v -> v.intValue()).toArray();
    			
    			ret[i][j] = modelCopy.intVar(attr[i][j].getName(), valuesArray);
    		}
    	}
    	return ret;
    }
    
    /***
     * Finds the differences between the intvar of the first and second attributes
     * and returns an array containing all these differences.
     * @param oldAttr
     * @param newAttr
     * @return
     */
    private static List<IntVar> findDifferences(IntVar[][] oldAttr, IntVar[][] newAttr, int sizeX, int sizeY) {

        List<IntVar> ret = new ArrayList<>();

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {

                Iterator<Integer> oldValuesItr = oldAttr[i][j].iterator();
                ArrayList<Integer> oldValuesArrayList = new ArrayList<>();

                while (oldValuesItr.hasNext()) {
                    Integer v = oldValuesItr.next();
                    oldValuesArrayList.add(v);
                }

                Iterator<Integer> newValuesItr = newAttr[i][j].iterator();
                ArrayList<Integer> newValuesArrayList = new ArrayList<>();

                while (newValuesItr.hasNext()) {
                    Integer v = newValuesItr.next();
                    newValuesArrayList.add(v);
                }

                if (!oldValuesArrayList.equals(newValuesArrayList)) {
                    ret.add(newAttr[i][j]);
                }
            }
        }
        return ret;
    }

    /**
     * Returns an array of all non-possible values of the Intvar param
     * @param n
     * @return
     */
    private static int[] notN(IntVar n, int size) {
        List<Integer> values = new ArrayList<>();
        for (int i = 1; i < size; i++) {
            if (!n.contains(i)) {
                values.add(i);
            }
        }
        int[] valuesArray = values.stream().mapToInt(v -> v.intValue()).toArray();
        return valuesArray;
    }

    /**
     * Returns an array of all possible values of the IntVar param
     * @param n
     * @return
     */
    private static int[] getValues(IntVar n, int sizeX, int sizeY) {

        int[] valuesArray = null;

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {

                Iterator<Integer> valuesItr = n.iterator();
                ArrayList<Integer> valuesArrayList = new ArrayList<>();

                while (valuesItr.hasNext()) {
                    Integer v = valuesItr.next();
                    valuesArrayList.add(v);
                }

                valuesArray = valuesArrayList.stream().mapToInt(v -> v.intValue()).toArray();

            }
        }

        return valuesArray;
    }
    
    /**
     * Method which uses propagate to find a solution for the model.
     * This method adds constraints to fix intvar with one possible values
     * and tries every values for the others until it finds a solution. 
     * @param modelToTest
     * @param attrToTest
     * @throws ContradictionException
     */
    public static void propagateToSolution(Model modelToTest, IntVar[][] attrToTest, int sizeX, int sizeY) throws ContradictionException {

    	ArrayList<Constraint> constraintsArray = new ArrayList<>();
    	
    	modelToTest.getSolver().propagate();
    	
        for (IntVar v : modelToTest.retrieveIntVars(false)) {
    			for (int possibleValue : getValues(v, sizeX, sizeY)) {
        			Constraint constraint = v.eq(possibleValue).decompose();
        			modelToTest.post(constraint);
        			constraintsArray.add(constraint);
        			try {
        				modelToTest.getEnvironment().worldPush();
        				modelToTest.getSolver().propagate();
        				if (modelToTest.getSolver().isSatisfied().equals(ESat.TRUE)) {
        					break;
        				}
        				else {
        					if (modelToTest.getSolver().isSatisfied().equals(ESat.FALSE)) {
        						modelToTest.unpost(constraint);
        						constraintsArray.remove(constraint);
            				}
        					break;
        				}
        			}
        			catch (ContradictionException e) {
        				e.printStackTrace();
        				modelToTest.getSolver().getEngine().flush();
        				modelToTest.unpost(constraint);
        				constraintsArray.remove(constraint);
        				modelToTest.getEnvironment().worldPop();
        			}
    			}
			if (modelToTest.getSolver().isSatisfied().equals(ESat.FALSE)) {
				modelToTest.getEnvironment().worldPop();
			}
        }
    }
	
}
