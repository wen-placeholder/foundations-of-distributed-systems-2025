--- Task 1: Laplace Mechanism and Counting Query ---
True count (people > 29):  22851
DP count (ε=ln(2)):        22850.76
Noise added:               -0.24
Sensitivity:               1
Epsilon:                   0.6931

--- Task 1: Laplace Mechanism and Counting Query ---
True count (people > 29):  22851
DP count (ε=ln(2)):        22848.64
Noise added:               -2.36
Sensitivity:               1
Epsilon:                   0.6931

--- Task 1: Laplace Mechanism and Counting Query ---
True count (people > 29):  22851
DP count (ε=ln(2)):        22851.38
Noise added:               +0.38
Sensitivity:               1
Epsilon:                   0.6931

(a) What is the sensitivity of this query, and why?

Answer: The sensitivity is 1.

Explanation:
Sensitivity (Δf) means: "What is the biggest change in the answer 
if we add or remove one person from the dataset?"

For our query "count people over 29":
- Add one person over 29 → count goes up by 1
- Add one person age 29 or less → count stays the same (no change)
- Remove one person over 29 → count goes down by 1
- Remove one person age 29 or less → count stays the same (no change)

The biggest possible change is 1.

Therefore, sensitivity = 1.

Note: All counting queries always have sensitivity = 1.




--- Task 2: Contingency Tables ---
Generating DP table for Relationship vs Race (epsilon=0.3)...

DP Table Results (First 5 rows):
Race            Amer-Indian-Eskimo  Asian-Pac-Islander       Black      Other         White
Relationship
Husband                  82.143916          417.478446  678.085601  74.793158  11941.704287
Not-in-family            79.418247          213.339206  806.660717  71.579798   7129.301016
Other-relative           16.490183           73.602195  160.952082  29.345536    689.756560
Own-child                47.673171          175.026014  552.996231  37.197529   4250.840487
Unmarried                55.962434           98.134741  778.124000  40.952950   2488.464827
PS E:\Basel University Master Program\Fall Semester 2025\45402-01 - Foundation Of Distributed Systems\Exercises\exercise 7> python  task2_solution.py
--- Task 2: Contingency Tables ---
Generating DP table for Relationship vs Race (epsilon=0.3)...

DP Table Results (First 5 rows):
Race            Amer-Indian-Eskimo  Asian-Pac-Islander       Black      Other         White
Relationship
Husband                  93.053241          411.760675  673.029044  72.947974  11942.066418
Not-in-family            82.252328          216.348336  809.193590  61.287937   7126.744093
Other-relative           16.916714           93.359750  158.047245  27.738427    693.817513
Own-child                46.738696          178.079931  551.161217  43.476654   4263.347656
Unmarried                57.779345           91.249533  768.461083  42.229118   2496.364607
PS E:\Basel University Master Program\Fall Semester 2025\45402-01 - Foundation Of Distributed Systems\Exercises\exercise 7> python  task2_solution.py
--- Task 2: Contingency Tables ---
Generating DP table for Relationship vs Race (epsilon=0.3)...

DP Table Results (First 5 rows):
Race            Amer-Indian-Eskimo  Asian-Pac-Islander       Black      Other         White
Relationship
Husband                  93.968063          407.892252  670.738483  76.180329  11946.212021
Not-in-family            80.318187          213.232174  810.482189  69.037915   7138.836121
Other-relative           12.027041           80.683745  164.619424  23.520154    684.540811
Own-child                49.834076          171.784662  554.418002  38.631226   4251.610542
Unmarried                59.076109           87.851005  764.253737  35.799928   2488.603623

(a) Does parallel composition apply?Answer: Yes.Why?The contingency table splits people into groups. Each group is one cell. One person goes into only ONE cell. The cells do not share people.Example:Person A is "Husband" + "White" → goes to cell (Husband, White)Person B is "Wife" + "Black" → goes to cell (Wife, Black)They are in different cells.When we add or remove one person, only ONE cell changes. The other cells stay the same.This means:We can use the full $\epsilon$ for each cell.Total privacy cost = $\epsilon$ (NOT $\epsilon$ × number of cells).This is called "parallel composition."(b) Does the number of variables matter?For Privacy Cost: NOThe total privacy cost is always $\epsilon$. This does not change.2 variables → privacy cost = $\epsilon$3 variables → privacy cost = $\epsilon$4 variables → privacy cost = $\epsilon$Why? One person still changes only one cell, regardless of the table size. So the total cost stays $\epsilon$.For Accuracy: YESMore variables = worse accuracy.Why?More variables means more cells (e.g., 2 variables = 25 cells; 3 variables = 125 cells).More cells mean fewer people in each cell.We add the same amount of noise to every cell (because $\epsilon$ is the same).Example:Large Cell: 1000 people + noise of 3 → error is small (0.3%).Small Cell: 50 people + noise of 3 → error is large (6%).Summary:Privacy cost: Stays the same ($\epsilon$).Accuracy: Gets worse with more variables.