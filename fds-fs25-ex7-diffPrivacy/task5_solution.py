import pandas as pd
import numpy as np
from scipy import stats
from dp_utils import laplace_mech

def dp_differencing_attack(df, epsilon):
    """
    Implements a differentially private version of the differencing attack.
    Uses sequential composition to protect both queries.
    
    The differencing attack consists of two queries:
    - q1: Sum of all ages
    - q2: Sum of ages excluding target person
    
    Both queries need privacy protection, so we split epsilon between them.
    """
    # Sensitivity determination
    # Each sum query has sensitivity = max age a person can contribute
    # Using 125 as a safe upper bound for human age
    max_age_bound = 125
    sensitivity = max_age_bound
    
    # Sequential Composition: Split privacy budget between two queries
    # Total privacy cost = epsilon/2 + epsilon/2 = epsilon
    epsilon_per_query = epsilon / 2
    
    # Find target person (using first person as example)
    target_index = df.index[0]
    target_true_age = df.loc[target_index, 'Age']
    
    # Query 1: Sum of all ages (with clipping)
    clipped_ages_all = df['Age'].clip(upper=max_age_bound)
    true_sum_all = clipped_ages_all.sum()
    dp_sum_all = laplace_mech(true_sum_all, sensitivity, epsilon_per_query)
    
    # Query 2: Sum excluding target person (with clipping)
    df_without_target = df.drop(target_index)
    clipped_ages_without = df_without_target['Age'].clip(upper=max_age_bound)
    true_sum_without = clipped_ages_without.sum()
    dp_sum_without = laplace_mech(true_sum_without, sensitivity, epsilon_per_query)
    
    # Attacker's computation (difference)
    attack_result = dp_sum_all - dp_sum_without
    
    return {
        'true_age': target_true_age,
        'attack_result_dp': attack_result,
        'noise': attack_result - target_true_age,
        'epsilon_total': epsilon,
        'epsilon_per_query': epsilon_per_query,
        'sensitivity': sensitivity
    }

def solve_task5():
    print("TASK 5: Differencing Attack with Differential Privacy")
        
    # Load data
    try:
        df = pd.read_csv('adult_with_pii.csv')
        df.columns = df.columns.str.strip()
    except FileNotFoundError:
        print("Error: File not found.")
        return
    
    # Privacy parameter
    epsilon = 1.0
    
    print(f"Running protected differencing attack with epsilon = {epsilon}...\n")
    
    result = dp_differencing_attack(df, epsilon)
    
    print("--- Attack Results ---")
    print(f"Target Person's True Age:      {result['true_age']} years")
    print(f"Attack Result (DP):            {result['attack_result_dp']:.2f} years")
    print(f"Noise Added (Protection):      {result['noise']:.2f} years")
    print(f"\n--- Privacy Parameters ---")
    print(f"Total Privacy Budget (ε):      {result['epsilon_total']}")
    print(f"Budget per Query (ε/2):        {result['epsilon_per_query']}")
    print(f"Sensitivity:                   {result['sensitivity']}")
    
    print("\n--- Attack Evaluation ---")
    error = abs(result['noise'])
    if error > 2:
        print(f"✓ Attack PREVENTED: Noise ({error:.2f} years) protects true age")
    else:
        print(f"Small noise ({error:.2f} years) - may reveal approximate age")
    
    # Demonstrate randomness
    print("\n--- Multiple Runs (showing noise randomness) ---")
    for i in range(3):
        result = dp_differencing_attack(df, epsilon)
        print(f"Run {i+1}: Attack result = {result['attack_result_dp']:.2f} years, "
              f"Error = {abs(result['noise']):.2f} years")

if __name__ == "__main__":
    solve_task5()