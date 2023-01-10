reset;

set BUSES;
set GENERATORS;
set HOURS;

#param Ka_load {BUSES} >= 0;
param Ka_gen {GENERATORS} >= 0;
param V {BUSES, BUSES} >= 0;
param Y_ab {BUSES, BUSES} >= 0;
param Pa_load {BUSES, HOURS} >= 0;
param p_ajhMax {BUSES, GENERATORS, HOURS} >= 0;
param p_ajhMin {BUSES, GENERATORS, HOURS} >= 0;
param Q_ab {BUSES, BUSES} >= 0;

# zmienne
var P_ab {a in BUSES, b in BUSES, h in HOURS};
var Pa_gen {a in BUSES, h in HOURS} >= 0;
var theta_a {a in BUSES, h in HOURS};
var p_ajh {a in BUSES, j in GENERATORS, h in HOURS} >= 0;
var generatorOn {GENERATORS, HOURS} binary;

# zmienne pomocnicze
var sum_gen {h in HOURS} >= 0;
var sum_load {h in HOURS} >= 0;
var total_cost >= 0;
var prices {GENERATORS, HOURS} >= 0;


# FUNKCJA CELU
maximize Q:
sum {h in HOURS} ( sum {a in BUSES} (sum {j in GENERATORS} - (Ka_gen[j] * p_ajh[a,j,h])) );

# OGRANICZENIA PODSTAWOWE:
#1 dla kazdego a
subject to energy_sum {a in BUSES, h in HOURS}:
sum {b in BUSES} (P_ab[a,b,h]) - Pa_gen[a,h] + Pa_load[a,h] = 0;

#2
subject to energy_flow {a in BUSES, b in BUSES, h in HOURS}:
P_ab[a,b,h] = V[a,b] * V[b,a] * Y_ab[a,b] * (theta_a[a,h] - theta_a[b,h]) ;

#3
subject to branch_energy_distribution {a in BUSES, b in BUSES, h in HOURS}:
-Q_ab[a,b] <= P_ab[a,b,h] <= Q_ab[a,b] ;

#4
subject to generation_limit_collectively {a in BUSES, h in HOURS}:
Pa_gen[a,h] =  sum {j in GENERATORS} p_ajh[a,j,h];

#5
subject to generator_on {j in GENERATORS, h in HOURS}:
sum {a in BUSES} p_ajh[a,j,h] <= generatorOn[j,h] * 999999;

#6
subject to generation_limit_up {a in BUSES, j in GENERATORS, h in HOURS}:
p_ajh[a,j,h] <= p_ajhMax[a,j,h];
#7
subject to generation_limit_down {a in BUSES, j in GENERATORS, h in HOURS}:
p_ajhMin[a,j,h] * generatorOn[j,h] <= p_ajh[a,j,h];

# OGRANICZENIA POMOCNICZE:
#8
subject to power_generated {h in HOURS}:
sum {a in BUSES} Pa_gen[a,h] >= sum {a in BUSES} Pa_load[a,h];

#9
subject to sum_generation{h in HOURS}:
sum_gen[h] = sum {a in BUSES} Pa_gen[a,h];

#10
subject to sum_load_all {h in HOURS}:
sum_load[h] = sum {a in BUSES} Pa_load[a,h];

#11
subject to all_total_balancing_cost {h in HOURS}:
total_cost = sum {a in BUSES} (sum {j in GENERATORS} (Ka_gen[j] * p_ajh[a,j,h]));

#12
subject to prices_on_working_generators {j in GENERATORS, h in HOURS}:
prices[j,h] = Ka_gen[j] * generatorOn[j,h];