
/*
 * This file is part of examples, http://choco-solver.org/
 *
 * Copyright (c) 2020, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 *
 * See LICENSE file in the project root for full license information.
 */

import org.chocosolver.examples.AbstractProblem;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Simple example which solve Zebra puzzle <br/>
 *
 * @author GK
 * @since 29/01/19
 */
public class Pasta extends AbstractProblem {

    private final String[] orders = { "4", "8", "12", "16" }; // 1. four orders of different prices
    private final int SIZE = orders.length;
    private final int PASTA = 0, SAUCE = 1, PEOPLE = 2;
    private final String[] sAttrTitle = { "Pasta", "Sauce", "People" };
    private final String[][] sAttr = { { "Rotini", "Tagliolini", "Farfalle", "Capellini" },
            { "otherSauce", "Arrabiata", "Marinara", "Puttanesca" }, { "Angie", "Damon", "Claudia", "Elisa" } };
    private IntVar[][] attr;
    private IntVar[][] attr2;
    private List<Constraint> constraints;
    private List<Constraint> constraints2;
    private List<String> clues;
    private List<String> clues2;
    protected Model model2;

    @Override
    public void buildModel() {

        model = new Model();
        model2 = new Model();

        attr = model.intVarMatrix("attr", SIZE - 1, SIZE, 1, SIZE);
        attr2 = model2.intVarMatrix("attr", SIZE - 1, SIZE, 1, SIZE);

        IntVar rotini = attr[PASTA][0];
        IntVar tagli = attr[PASTA][1];
        IntVar farfa = attr[PASTA][2];
        IntVar cape = attr[PASTA][3];
        IntVar rotini2 = attr2[PASTA][0];
        IntVar tagli2 = attr2[PASTA][1];
        IntVar farfa2 = attr2[PASTA][2];
        IntVar cape2 = attr2[PASTA][3];

        IntVar otherSauce = attr[SAUCE][0];
        IntVar arrabi = attr[SAUCE][1];
        IntVar marina = attr[SAUCE][2];
        IntVar puttan = attr[SAUCE][3];
        IntVar otherSauce2 = attr2[SAUCE][0];
        IntVar arrabi2 = attr2[SAUCE][1];
        IntVar marina2 = attr2[SAUCE][2];
        IntVar puttan2 = attr2[SAUCE][3];
        
        IntVar angie = attr[PEOPLE][0];
        IntVar damon = attr[PEOPLE][1];
        IntVar claudia = attr[PEOPLE][2];
        IntVar elisa = attr[PEOPLE][3];
        IntVar angie2 = attr2[PEOPLE][0];
        IntVar damon2 = attr2[PEOPLE][1];
        IntVar claudia2 = attr2[PEOPLE][2];
        IntVar elisa2 = attr2[PEOPLE][3];

        model.allDifferent(attr[PASTA], "AC").post();
        model.allDifferent(attr[SAUCE], "AC").post();
        model.allDifferent(attr[PEOPLE], "AC").post();
        model2.allDifferent(attr2[PASTA], "AC").post();
        model2.allDifferent(attr2[SAUCE], "AC").post();
        model2.allDifferent(attr2[PEOPLE], "AC").post();

        constraints = new ArrayList<>();
        constraints2 = new ArrayList<>();
        clues = new ArrayList<>();
        clues2 = new ArrayList<>();

        clues.add("The person who ordered capellini paid less than the person who chose arrabiata sauce");
        constraints.add(cape.lt(arrabi).decompose());
        clues2.add("The person who ordered capellini paid less than the person who chose arrabiata sauce");
        constraints2.add(cape2.lt(arrabi2).decompose());

        clues.add("The person who ordered tagliolini paid more than Angie");
        constraints.add(angie.lt(tagli).decompose());
        clues2.add("The person who ordered tagliolini paid more than Angie");
        constraints2.add(angie2.lt(tagli2).decompose());

        clues.add("The person who ordered tagliolini paid less than the person who chose marinara sauce");
        constraints.add(tagli.lt(marina).decompose());
        clues2.add("The person who ordered tagliolini paid less than the person who chose marinara sauce");
        constraints2.add(tagli2.lt(marina2).decompose());

        clues.add("Claudia did not order puttanesca");
        constraints.add(claudia.ne(puttan).decompose());
        clues2.add("Claudia did not order puttanesca");
        constraints2.add(claudia2.ne(puttan2).decompose());

        clues.add("The person who ordered rotini is either the person who paid $8 more than Damon$"
                + "or the person who paid $8 less than Damon");
        constraints.add(rotini.dist(damon).eq(2).decompose());
        clues2.add("The person who ordered rotini is either the person who paid $8 more than Damon$"
                + "or the person who paid $8 less than Damon");
        constraints2.add(rotini2.dist(damon2).eq(2).decompose());

        clues.add("The person who ordered capellini is either Damon or Claudia");
        constraints.add(cape.eq(damon).or(cape.eq(claudia)).decompose());
        clues2.add("The person who ordered capellini is either Damon or Claudia");
        constraints2.add(cape2.eq(damon2).or(cape2.eq(claudia2)).decompose());

        clues.add("The person who chose arrabiata sauce is either Angie or Elisa");
        constraints.add(arrabi.eq(angie).or(arrabi.eq(elisa)).decompose());
        clues2.add("The person who chose arrabiata sauce is either Angie or Elisa");
        constraints2.add(arrabi2.eq(angie2).or(arrabi2.eq(elisa2)).decompose());

        clues.add("The person who chose arrabiata sauce ordered farfalle");
        constraints.add(arrabi.eq(farfa).decompose());
        clues2.add("The person who chose arrabiata sauce ordered farfalle");
        constraints2.add(arrabi2.eq(farfa2).decompose());
        
        for (Constraint c : constraints) {
            model.post(c);
        }
        for (Constraint c : constraints2) {
            model2.post(c);
        }
    }

    @Override
    public void configureSearch() {
    }

    @Override
    public void solve() {
    	candidateExplanations();
    }

    private void print(IntVar[][] pos) {
        System.out.printf("%-13s%-13s%-13s%-13s%-13s%n", "", orders[0], orders[1], orders[2], orders[3]);
        for (int i = 0; i < SIZE - 1; i++) {
            String[] sortedLine = new String[SIZE];
            for (int j = 0; j < SIZE; j++) {
                sortedLine[pos[i][j].getValue() - 1] = sAttr[i][j];
            }
            System.out.printf("%-13s", sAttrTitle[i]);
            for (int j = 0; j < SIZE; j++) {
                System.out.printf("%-13s", sortedLine[j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        new Pasta().execute(args);
    }
    
    /**
     * Algorithm 2: Candidate Explanations
     */
    private void candidateExplanations() {
        try {

            List<List<IntVar>> candidatesVariable = new ArrayList<>();
            List<ContradictionException> candidatesContradictions = new ArrayList<>();
    		
    		// Look for facts that need to be explained
    		model.getSolver().propagate();
    		// Corresponds to J \ I in algorithm 2
    		List<IntVar> listDifferences = findDifferences(attr2, attr);

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
    				if (!n.isInstantiated()) {
    					int[] valuesArray = getValues(notN(n));
    					vConstraint = ((IntVar) modelVars[i]).in(valuesArray).decompose();
    				}
    				else {
    					vConstraint = ((IntVar) modelVars[i]).ne(n.getValue()).decompose();
    				}
    				
    				constraintsToUnpost.add(vConstraint);
    				IntVar[][] attrStateSnapshot = deepCopy(attr2);
    				
    				model2.post(vConstraint);
    				 try {
	                    model2.getSolver().propagate();
	                } catch (ContradictionException e) {
	                    e.printStackTrace();
	                    candidatesContradictions.add(e);
	                    
	                    // Keep all implied facts
	                    candidatesVariable.add(findDifferences(attrStateSnapshot, attr2));
	                    
	                    model2.getSolver().getEnvironment().worldPop();
	                    nIndex = nIndexPushed-1;
	                    model2.unpost(vConstraint);
	                    model2.post(vConstraint.getOpposite());
	                }
    			}
    			else {
    			
    				Constraint vConstraint;
    				
    				// Create a n constraints
    				if (!n.isInstantiated()) {
    					int[] valuesArray = getValues(n);
    					vConstraint = ((IntVar) modelVars[i]).in(valuesArray).decompose();
    				}
    				else {
    					vConstraint = ((IntVar) modelVars[i]).eq(n.getValue()).decompose();
    				}
    				
    				constraintsToUnpost.add(vConstraint);
    				model2.post(vConstraint);
    				try {
    					model2.getSolver().propagate();
    				}
    				catch (ContradictionException e) {
    					e.printStackTrace();
    					model2.getSolver().getEnvironment().worldPop();
    					nIndex = nIndexPushed-1;
    					model2.unpost(vConstraint);
    					model2.post(vConstraint.getOpposite());
    				}
    			}
    			nIndex++;
    		}
    		
    		print(attr2);
    		print(attr);
    		
    	 } catch (ContradictionException e) {
             e.printStackTrace();
         }
    }
    
    private IntVar[][] deepCopy(IntVar[][] attr) {
    	
    	IntVar[][] ret = new IntVar[SIZE][SIZE];
    	Model modelCopy = new Model();
    	
    	for (int i = 0 ; i < SIZE - 1; i ++) {
    		for (int j = 0 ; j < SIZE; j++) {
    			
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
    
    private List<IntVar> findDifferences(IntVar[][] oldAttr, IntVar[][] newAttr) {
    	
    	List<IntVar> ret = new ArrayList<>();
    	
    	for (int i = 0 ; i < SIZE -1 ; i ++) {
    		for (int j = 0 ; j < SIZE ; j++) {
    			
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
    
    private IntVar notN(IntVar n) {
        String name = "NOT_" + n.getName();
        List<Integer> values = new ArrayList<>();
        for (int i = 1; i < SIZE; i++) {
            if (!n.contains(i)){
                values.add(i);
            }
        }
        int[] valuesArray = values.stream().mapToInt(v -> v.intValue()).toArray();
        IntVar notN = model.intVar(name, valuesArray);
        return notN;
    }
    
    private int[] getValues(IntVar n) {

    	int[] valuesArray = null;
    	
        for (int i = 0 ; i < SIZE ; i ++) {
    		for (int j = 0 ; j < SIZE ; j++) {
    			
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
    
    private int getVarATraiter(Variable[] modelVars, IntVar n) {
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
}