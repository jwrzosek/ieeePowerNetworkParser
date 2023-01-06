package com.company;

import com.ampl.AMPL;
import com.ampl.Environment;

import java.io.IOException;

public class Ampl {

    public static void main(String[] args) throws IOException {
        Environment env = new Environment("C:\\AMPLCOMMUNITY\\ampl.mswin64");
        AMPL ampl = new AMPL(env);

        // Outer try-catch-finally block, to be sure of releasing the AMPL
        // object when done
        try {
            ampl.setOption("solver", "cplex");
            // Use the provided path or the default one
            String dir = "C:\\Users\\wrzos\\Desktop\\Moje\\PW\\_MGR\\ampl\\model\\book\\kse_1st_model";
            // Load the AMPL model from file
            ampl.read("C:\\Users\\wrzos\\Desktop\\model.mod");

            ampl.readData("C:\\Users\\wrzos\\Desktop\\1.dat");

            ampl.solve();

            System.out.println(ampl.getObjective("Q").value());
        } finally {
            ampl.close();
        }
    }
}
