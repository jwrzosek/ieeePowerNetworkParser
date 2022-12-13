reset;

set BUSES;
set GENS;
set HOURS;

param Ka_load {BUSES} >= 0;
param Ka_gen {BUSES} >= 0;
param V {BUSES} >= 0;
param Y_ab {BUSES, BUSES} >= 0;
param Pa_load {BUSES, HOURS} >= 0;
param p_jhMax {BUSES, GENS, HOURS} >= 0;  #Pa_genMax
param Q_ab {BUSES, BUSES} >= 0; #line limit


var P_ab {a in BUSES, b in BUSES, h in HOURS};
var Pa_gen {a in BUSES, h in HOURS} >= 0;
var theta_a {a in BUSES, h in HOURS};
var p_jh {a in BUSES, j in GENS, h in HOURS} >= 0;

# zmienne pomocnicze
var sum_gen {h in HOURS} >= 0;
var sum_load {h in HOURS} >= 0;

# FUNKCJA CELU
maximize Q:
    sum {h in HOURS} ( sum {a in BUSES} - (Ka_gen[a] * Pa_gen[a,h]) );

#1 dla kazdego a
subject to energy_sum {a in BUSES, h in HOURS}:
    sum {b in BUSES} (P_ab[a,b,h]) - Pa_gen[a,h] + Pa_load[a,h] = 0;

#2
subject to energy_flow {a in BUSES, b in BUSES, h in HOURS}:
    P_ab[a,b,h] = V[a] * V[b] * Y_ab[a,b] * (theta_a[a,h] - theta_a[b,h]) ;

#3
subject to branch_energy_distribution {a in BUSES, b in BUSES, h in HOURS}:
    -Q_ab[a,b] <= P_ab[a,b,h] <= Q_ab[a,b] ;

#### dla kazdego a
subject to generation_limit22 {a in BUSES, h in HOURS}:
    Pa_gen[a,h] =  sum {j in GENS} p_jh[a,j,h];

#4 dla kazdego a
subject to generation_limit {a in BUSES, j in GENS, h in HOURS}:
    0 <= p_jh[a,j,h] <= p_jhMax[a,j,h];

# ograniczenia dodane przez autora
subject to power_generated {h in HOURS}:
    sum {a in BUSES} Pa_gen[a,h] >= sum {a in BUSES} Pa_load[a,h];

# ograniczenia zmiennych pomocnicze
subject to sum_generation{h in HOURS}:
    sum_gen[h] = sum {a in BUSES} Pa_gen[a,h];
subject to sum_load_all {h in HOURS}:
    sum_load[h] = sum {a in BUSES} Pa_load[a,h];