reset;

        set BUSES;
        set J;

        param Ka_load {BUSES} >= 0;
        param Ka_gen {BUSES} >= 0;
        param V {BUSES} >= 0;
        param Y_ab {BUSES, BUSES} >= 0;

        param Pa_load {BUSES} >= 0;
        param Pa_genMax {BUSES} >= 0;  #Pa_genMax

        param Q_ab {BUSES, BUSES} >= 0; #line limit


        var P_ab {a in BUSES, b in BUSES};
        var Pa_gen {a in BUSES} >= 0;
        var theta_a {a in BUSES};

        var p_jh {j in J};

        # zmienne pomocnicze
        var sum_gen >= 0;
        var sum_load >= 0;
        # ------------------

        # FUNKCJA CELU
        maximize Q:
        sum {a in BUSES} ((Ka_load[a] * Pa_load[a]) - (Ka_gen[a] * Pa_gen[a]));

        #---------------------------------------------------------------------------------------------
        #1 dla kazdego a
        subject to energy_sum {a in BUSES}:
        sum {b in BUSES} P_ab[a,b] - Pa_gen[a] + Pa_load[a] = 0;

        #2
        subject to energy_flow {a in BUSES, b in BUSES}:
        P_ab[a,b] = V[a] * V[b] * Y_ab[a,b] * (theta_a[a] - theta_a[b]) ;

        #3
        subject to branch_energy_distribution {a in BUSES, b in BUSES}:
        -Q_ab[a,b] <= P_ab[a,b] <= Q_ab[a,b] ;

        #4 dla kazdego a
        subject to generation_limit {a in BUSES}:
        0 <= Pa_gen[a] <= Pa_genMax[a];

        #5 dla kazdego a
        #subject to load_limit {a in BUSES}:
        #	Pa_loadMin[a] <= Pa_load[a] <= Pa_loadMax[a];

        #---------------------------------------------------------------------------------------------
        # ograniczenia dodane przez autora
        subject to theta_n0:
        theta_a['B1'] = 0;
        subject to power_generated:
        sum {a in BUSES} Pa_gen[a] >= sum {a in BUSES} Pa_load[a];

        # ograniczenia zmiennych pomocnicze
        subject to sum_generation:
        sum_gen = sum {a in BUSES} Pa_gen[a];
        subject to sum_load_all:
        sum_load = sum {a in BUSES} Pa_load[a];

        # ------------------

        # --------------------------- Data    --------------------------- #
        #data 3nodes.dat
        data 'intellij\model.dat'

        # --------------------------- Solver  --------------------------- #
        option solver cplex;
        solve;

        # --------------------------- Display --------------------------- #
        display Q;
        display theta_a;
        display P_ab;
        display Pa_gen;
        display Pa_load;
        display sum_gen;
        display sum_load;