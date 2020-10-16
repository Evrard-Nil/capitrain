
/*
 * This file is part of examples, http://choco-solver.org/
 *
 * Copyright (c) 2020, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 *
 * See LICENSE file in the project root for full license information.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.chocosolver.examples.AbstractProblem;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;

import com.ibm.icu.impl.Pair;

/**
 * Simple example which solve Zebra puzzle
 * <br/>
 *
 * @author GK
 * @since 29/01/19
 */
public class Zebra extends AbstractProblem {

    private final String[] sHouse = {"House 1", "House 2", "House 3", "House 4", "House 5"}; // 1. five houses
    private final int SIZE = sHouse.length;
    private final int NATIONALITY = 0, COLOR = 1, CIGARETTE = 2, PET = 3, DRINK = 4;
    private final String [] sAttrTitle = {"Nationality", "Color", "Cigarette", "Pet", "Drink"};
    private final String [][] sAttr = {
            {"Ukranian", "Norwegian", "Englishman", "Spaniard", "Japanese"},
            {"Red", "Blue", "Yellow", "Green", "Ivory"},
            {"Old Gold", "Parliament", "Kools", "Lucky Strike", "Chesterfield"},
            {"Zebra", "Dog", "Horse", "Fox", "Snails"},
            {"Coffee", "Tea", "Water", "Milk", "Orange juice"}
    };
    private IntVar[][] attr;
    private IntVar zebra;

    @Override
    public void buildModel() {

        model = new Model();

        attr = model.intVarMatrix("attr", SIZE, SIZE, 1, SIZE);

        IntVar ukr   = attr[NATIONALITY][0];
        IntVar norge = attr[NATIONALITY][1];
        IntVar eng   = attr[NATIONALITY][2];
        IntVar spain = attr[NATIONALITY][3];
        IntVar jap   = attr[NATIONALITY][4];

        IntVar red    = attr[COLOR][0];
        IntVar blue   = attr[COLOR][1];
        IntVar yellow = attr[COLOR][2];
        IntVar green  = attr[COLOR][3];
        IntVar ivory  = attr[COLOR][4];

        IntVar oldGold = attr[CIGARETTE][0];
        IntVar parly   = attr[CIGARETTE][1];
        IntVar kools   = attr[CIGARETTE][2];
        IntVar lucky   = attr[CIGARETTE][3];
        IntVar chest   = attr[CIGARETTE][4];

        zebra  = attr[PET][0];
        IntVar dog    = attr[PET][1];
        IntVar horse  = attr[PET][2];
        IntVar fox    = attr[PET][3];
        IntVar snails = attr[PET][4];

        IntVar coffee = attr[DRINK][0];
        IntVar tea    = attr[DRINK][1];
        IntVar h2o    = attr[DRINK][2];
        IntVar milk   = attr[DRINK][3];
        IntVar oj     = attr[DRINK][4];

        model.allDifferent(attr[COLOR]).post();
        model.allDifferent(attr[CIGARETTE]).post();
        model.allDifferent(attr[NATIONALITY]).post();
        model.allDifferent(attr[PET]).post();
        model.allDifferent(attr[DRINK]).post();

        eng.eq(red).post(); // 2. the Englishman lives in the red house
        spain.eq(dog).post(); // 3. the Spaniard owns a dog
        coffee.eq(green).post(); // 4. coffee is drunk in the green house
        ukr.eq(tea).post(); // 5. the Ukr drinks tea
        ivory.add(1).eq(green).post(); // 6. green house is to right of ivory house
        oldGold.eq(snails).post(); // 7. oldGold smoker owns snails
        kools.eq(yellow).post(); // 8. kools are smoked in the yellow house
        milk.eq(3).post(); // 9. milk is drunk in the middle house
        norge.eq(1).post(); // 10. Norwegian lives in first house on the left
        chest.dist(fox).eq(1).post(); // 11. chesterfield smoker lives next door to the fox owner
        kools.dist(horse).eq(1).post(); // 12. kools smoker lives next door to the horse owner
        lucky.eq(oj).post(); // 13. lucky smoker drinks orange juice
        jap.eq(parly).post(); // 14. Japanese smokes parliament
        norge.dist(blue).eq(1).post(); // 15. Norwegian lives next to the blue house
    }

    @Override
    public void configureSearch() {
    }

    @Override
    public void solve() {
        
//    	System.out.println(model);
//    	try {
//            model.getSolver().propagate();
//            
//            HashMap<Integer, ArrayList<IntVar>> casesEgales = new HashMap<Integer, ArrayList<IntVar>>();
//
//            for(int i =  0; i < SIZE ; i++) {
//        		casesEgales.put(i, new ArrayList<IntVar>());
//            }
//            
//            for(int i =  0; i < SIZE ; i++) {
//            	for(int j =  0 ; j < SIZE ; j++) {
////            		System.out.println(i+","+j+" : " + attr[i][j]);
//            		if (attr[i][j].isInstantiated()) {
//            			casesEgales.get(attr[i][j].getValue()).add(attr[i][j]);
//            		}
//            	}
//            }      
//            
//            for(int i =  0; i < SIZE ; i++) {
//            	ArrayList<IntVar> varEgales = (ArrayList<IntVar>) casesEgales.get(i);
//            	for(int j =  0; j < varEgales.size() ; j++) {
//            		varEgales.get(j).eq(i).post();
//            	}
//            }
//            model.getSolver().propagate();
//            System.out.println(model);
//            print(attr);
//            
//        } catch (ContradictionException e) {
//            e.printStackTrace();
//        }
//
////        while (model.getSolver().solve()) {
//        int z = zebra.getValue();
//        int n = -1;
//        for (int i = 0; i < SIZE; i++) {
//            if (z == attr[NATIONALITY][i].getValue()) {
//                n = i;
//            }
//        }
//        if (n >= 0) {
//            System.out.printf("%n%-13s%s%s%s%n", "",
//                    "============> The Zebra is owned by the ", sAttr[NATIONALITY][n], " <============");
//        }
//        print(attr);
    	
    	candidateExplanations();
    }
    private void print(IntVar[][] pos) {
        System.out.printf("%-13s%-13s%-13s%-13s%-13s%-13s%n", "",
                sHouse[0], sHouse[1], sHouse[2], sHouse[3], sHouse[4]);
        for (int i = 0; i < SIZE; i++) {
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
        new Zebra().execute(args);
    }
    
    private void candidateExplanations() {
    	try {
    		
    		IntVar[][] oldAttr = deepCopy(attr);
    		model.getSolver().propagate();
    		List<IntVar> listDifferences = findDifferences(oldAttr, attr);
    		
    		for (IntVar n : listDifferences) {
    			model.getVa
    		}
    		
    		
//    		print(oldAttr);
//    		print(attr);
    		
    	 } catch (ContradictionException e) {
             e.printStackTrace();
         }
    }
    
    private IntVar[][] deepCopy(IntVar[][] attr) {
    	
    	IntVar[][] ret = new IntVar[SIZE][SIZE];
    	
    	for (int i = 0 ; i < SIZE ; i ++) {
    		for (int j = 0 ; j < SIZE ; j++) {
    			
    			Iterator<Integer> valuesItr = attr[i][j].iterator();
    			ArrayList<Integer> valuesArrayList = new ArrayList<>();
    			
    			while (valuesItr.hasNext()) {
    				Integer v = valuesItr.next();
    				valuesArrayList.add(v);
    			}
    			
    			int[] valuesArray = valuesArrayList.stream().mapToInt(v -> v.intValue()).toArray();
    			
    			ret[i][j] = model.intVar(attr[i][j].getName(), valuesArray);
    		}
    	}
    	return ret;
    }
    
    private List<Pair<IntVar, Integer>> findDifferences(IntVar[][] oldAttr, IntVar[][] newAttr) {
    	
    	List<IntVar> ret = new ArrayList<>();
    	
    	for (int i = 0 ; i < SIZE ; i ++) {
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
        for (int i = 0; i < SIZE; i++) {
            if (!n.contains(i)){
                values.add(i);
            }
        }
        int[] valuesArray = values.stream().mapToInt(v -> v.intValue()).toArray();
        IntVar notN = model.intVar(name, valuesArray);
        return notN;
    }
}