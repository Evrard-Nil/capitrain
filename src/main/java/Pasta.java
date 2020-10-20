
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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Simple example which solve Zebra puzzle
 * <br/>
 *
 * @author GK
 * @since 29/01/19
 */
public class Pasta extends AbstractProblem {

    private final String[] orders = {"4", "8", "12", "16"}; // 1. four orders of different prices
    private final int SIZE = orders.length;
    private final int PASTA = 0, SAUCE = 1, PEOPLE = 2;
    private final String[] sAttrTitle = {"Pasta", "Sauce", "People"};
    private final String[][] sAttr = {
            {"Rotini", "Tagliolini", "Farfalle", "Capellini"},
            {"otherSauce", "Arrabiata", "Marinara", "Puttanesca"},
            {"Angie", "Damon", "Claudia", "Elisa"}
    };
    private IntVar[][] attr;

    @Override
    public void buildModel() {

        model = new Model();

        attr = model.intVarMatrix("attr", SIZE-1, SIZE, 1, SIZE);

        IntVar rotini = attr[PASTA][0];
        IntVar tagli = attr[PASTA][1];
        IntVar farfa = attr[PASTA][2];
        IntVar cape = attr[PASTA][3];

        IntVar otherSauce = attr[SAUCE][0];
        IntVar arrabi = attr[SAUCE][1];
        IntVar marina = attr[SAUCE][2];
        IntVar puttan = attr[SAUCE][3];

        IntVar angie = attr[PEOPLE][0];
        IntVar damon = attr[PEOPLE][1];
        IntVar claudia = attr[PEOPLE][2];
        IntVar elisa = attr[PEOPLE][3];

        model.allDifferent(attr[PASTA]).post();
        model.allDifferent(attr[SAUCE]).post();
        model.allDifferent(attr[PEOPLE]).post();

        cape.lt(arrabi).post();// 1. The person who ordered capellini paid less than the person who chose arrabiata sauce
        angie.lt(tagli).post();// 2. The person who ordered tagliolini paid more than Angie
        tagli.lt(marina).post();// 3. The person who ordered tagliolini paid less than the person who chose marinara sauce
        claudia.ne(puttan).post();// 4. Claudia did not order puttanesca
        rotini.dist(damon).eq(2);// 5. The person who ordered rotini is either the person who paid $8 more than Damon
        // or the person who paid $8 less than Damon
        cape.eq(damon).or(cape.eq(claudia)).post();// 6. The person who ordered capellini is either Damon or Claudia
        arrabi.eq(angie).or(arrabi.eq(elisa)).post();// 7. The person who chose arrabiata sauce is either Angie or Elisa
        arrabi.eq(farfa).post();// 8. The person who chose arrabiata sauce ordered farfalle
    }

    @Override
    public void configureSearch() {
    }

    @Override
    public void solve() {

        System.out.println(model);
        try {
            model.getSolver().propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        print(attr);
    }

    private void print(IntVar[][] pos) {
        System.out.printf("%-13s%-13s%-13s%-13s%-13s%n", "",
                orders[0], orders[1], orders[2], orders[3]);
        for (int i = 0; i < SIZE-1; i++) {
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
}