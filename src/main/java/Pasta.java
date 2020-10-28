
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
    private List<Constraint> constraints;
    private List<String> clues;
    protected Model model2;

    @Override
    public void buildModel() {

        model = new Model();
        model2 = new Model();

        attr = model.intVarMatrix("attr", SIZE - 1, SIZE, 1, SIZE);

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
        model2.allDifferent(attr[PASTA]).post();
        model2.allDifferent(attr[SAUCE]).post();
        model2.allDifferent(attr[PEOPLE]).post();

        constraints = new ArrayList<>();
        clues = new ArrayList<>();

        clues.add("The person who ordered capellini paid less than the person who chose arrabiata sauce");
        constraints.add(cape.lt(arrabi).decompose());

        clues.add("The person who ordered tagliolini paid more than Angie");
        constraints.add(angie.lt(tagli).decompose());

        clues.add("The person who ordered tagliolini paid less than the person who chose marinara sauce");
        constraints.add(tagli.lt(marina).decompose());

        clues.add("Claudia did not order puttanesca");
        constraints.add(claudia.ne(puttan).decompose());

        clues.add("The person who ordered rotini is either the person who paid $8 more than Damon$"
                + "or the person who paid $8 less than Damon");
        constraints.add(rotini.dist(damon).eq(2).decompose());

        clues.add("The person who ordered capellini is either Damon or Claudia");
        constraints.add(cape.eq(damon).or(cape.eq(claudia)).decompose());

        clues.add("The person who chose arrabiata sauce is either Angie or Elisa");
        constraints.add(arrabi.eq(angie).or(arrabi.eq(elisa)).decompose());

        clues.add("The person who chose arrabiata sauce ordered farfalle");
        constraints.add(arrabi.eq(farfa).decompose());
        for (Constraint c : constraints) {
            model.post(c);
            model2.post();
        }
    }

    @Override
    public void configureSearch() {
    }

    @Override
    public void solve() {
        try {
            model.getSolver().propagate();
            System.out.println(model);
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        print(attr);
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
}