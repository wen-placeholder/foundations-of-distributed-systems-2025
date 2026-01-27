import pandas as pd
import numpy as np
from scipy import stats
from dp_utils import laplace_mech

def dp_sum_capgain(df, epsilon):
    """
    Computes the differentially private sum of the Capital Gain column.
    Uses clipping to bound the sensitivity.
    """
    # Find column name
    col_name = None
    for col in df.columns:
        if 'Capital Gain' in col:
            col_name = col
            break
            
    if col_name is None:
        raise ValueError("Column 'Capital Gain' not found!")
    
    # Clipping Parameter
    # Using 100,000: safe upper bound for adult dataset
    upper_bound = 100000  
    
    # Clip data
    clipped_data = df[col_name].clip(upper=upper_bound)
    
    # Calculate true sum
    true_sum = clipped_data.sum()
    
    # Sensitivity = clipping bound
    sensitivity = upper_bound
    
    # Add noise
    dp_sum = laplace_mech(true_sum, sensitivity, epsilon)
    
    return {
        'true_sum': true_sum,
        'dp_sum': dp_sum,
        'noise': dp_sum - true_sum,
        'sensitivity': sensitivity,
        'clipping_bound': upper_bound,
        'epsilon': epsilon
    }

def solve_task4():
    print("TASK 4: Differentially Private Sum (Capital Gain)")
        
    # Load Data
    try:
        df = pd.read_csv('adult_with_pii.csv')
        df.columns = df.columns.str.strip()  # Strip whitespace from column names
    except FileNotFoundError:
        print("Error: File not found.")
        return

    # Required parameter (epsilon = 0.04)
    epsilon = 0.04
    
    print(f"Calculating sum with epsilon = {epsilon}...")
    
    # Run the function
    result = dp_sum_capgain(df, epsilon)
    
    # Print results
    print("RESULTS:")
    print("-"*70)
    print(f"Clipping Bound used:      {result['clipping_bound']}")
    print(f"Sensitivity:              {result['sensitivity']}")
    print(f"Privacy Budget (ε):       {result['epsilon']}")
    print("\n--- Sum Values ---")
    print(f"True Sum (Clipped):       {result['true_sum']:,.0f}")
    print(f"DP Sum (Noisy):           {result['dp_sum']:,.0f}")
    print(f"Noise Added:              {result['noise']:,.0f}")
    
    # Error percentage
    error_percent = (abs(result['noise']) / result['true_sum']) * 100
    print(f"Relative Error:           {error_percent:.2f}%")

if __name__ == "__main__":
    solve_task4()