import pandas as pd
import numpy as np
from scipy import stats
from dp_utils import laplace_mech
    
def count_over_29_private(filename='adult_with_pii.csv'):
    """
    Differentially private query: count people over 29 years old.
    Uses epsilon = ln(2) as specified.
    
    Args:
        filename: Path to the CSV file
        
    Returns:
        Dictionary with true count, DP count, and other info
    """
    # Load dataset
    df = pd.read_csv(filename)
    
    # True count (non-private)
    true_count = len(df[df['Age'] > 29])
    
    # Parameters
    epsilon = np.log(2)  # ln(2) as required
    sensitivity = 1       # Sensitivity for counting queries is always 1
    
    # Apply Laplace mechanism
    dp_count = laplace_mech(true_count, sensitivity, epsilon)
    
    return {
        'true_count': true_count,
        'dp_count': dp_count,
        'noise': dp_count - true_count,
        'sensitivity': sensitivity,
        'epsilon': epsilon
    }

if __name__ == "__main__":
    # Run the query
    result = count_over_29_private()
    
    print("--- Task 1: Laplace Mechanism and Counting Query ---")
    print(f"True count (people > 29):  {result['true_count']}")
    print(f"DP count (ε=ln(2)):        {result['dp_count']:.2f}")
    print(f"Noise added:               {result['noise']:+.2f}")
    print(f"Sensitivity:               {result['sensitivity']}")
    print(f"Epsilon:                   {result['epsilon']:.4f}")