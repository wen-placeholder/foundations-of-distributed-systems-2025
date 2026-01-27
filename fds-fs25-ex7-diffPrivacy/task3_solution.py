import pandas as pd
import numpy as np
from scipy import stats
from dp_utils import laplace_mech

def score(df, occupation):
    """
    Returns a score for an occupation.
    High score = common occupation (many people have it)
    Low score = uncommon occupation (few people have it)
    
    Args:
        df: DataFrame with 'Occupation' column
        occupation: Name of the occupation to score
        
    Returns:
        int: Number of people with this occupation
    """
    return len(df[df['Occupation'] == occupation])


def most_common_occupation(df, epsilon):
    """
    Returns the most common occupation using differential privacy.
    Uses Laplace mechanism with exponential mechanism approach.
    
    Args:
        df: DataFrame with 'Occupation' column
        epsilon: Privacy budget
        
    Returns:
        dict with results
    """
    # Get all unique occupations
    occupations = df['Occupation'].unique()
    
    # Calculate true scores for all occupations
    true_scores = {}
    for occ in occupations:
        true_scores[occ] = score(df, occ)
    
    # Find true most common occupation
    true_most_common = max(true_scores, key=true_scores.get)
    
    # Add Laplace noise to each score
    # Sensitivity = 1 (one person can change one occupation count by 1)
    sensitivity = 1
    noisy_scores = {}
    for occ in occupations:
        noisy_scores[occ] = laplace_mech(true_scores[occ], sensitivity, epsilon)
    
    # Select occupation with highest noisy score
    dp_most_common = max(noisy_scores, key=noisy_scores.get)
    
    return {
        'true_most_common': true_most_common,
        'true_count': true_scores[true_most_common],
        'dp_most_common': dp_most_common,
        'dp_noisy_count': noisy_scores[dp_most_common],
        'true_count_of_selected': true_scores[dp_most_common],
        'all_true_scores': true_scores,
        'all_noisy_scores': noisy_scores,
        'sensitivity': sensitivity,
        'epsilon': epsilon
    }


def task3_solution():
    """
    Task 3: Differentially private selection from sets.
    """
    print("TASK 3: Differentially Private Selection")
    
    # Load dataset
    df = pd.read_csv('adult_with_pii.csv')
    
    # Remove missing values
    df = df[df['Occupation'].notna()]
    df = df[df['Occupation'] != '?']
    
    # Parameters
    epsilon = 0.05
    
    print(f"Total records: {len(df)}")
    print(f"Privacy budget (ε): {epsilon}")
    print(f"Number of unique occupations: {df['Occupation'].nunique()}")
    
    # Run the differentially private selection
    result = most_common_occupation(df, epsilon)
    
    print("\n" + "-"*70)
    print("RESULTS:")
    print(f"True most common occupation:  {result['true_most_common']}")
    print(f"True count:                   {result['true_count']}")
    print(f"\nDP selected occupation:       {result['dp_most_common']}")
    print(f"DP noisy count:               {result['dp_noisy_count']:.2f}")
    print(f"True count of selected:       {result['true_count_of_selected']}")
    
    # Check if selection was correct
    is_correct = result['true_most_common'] == result['dp_most_common']
    print(f"\nCorrect selection: {'YES' if is_correct else 'NO'}")
    
    # Show top 5 occupations (true counts)
    print("\n" + "-"*70)
    print("TOP 5 OCCUPATIONS (True Counts):")
    sorted_occupations = sorted(result['all_true_scores'].items(), 
                                key=lambda x: x[1], reverse=True)
    for i, (occ, count) in enumerate(sorted_occupations[:5], 1):
        noisy = result['all_noisy_scores'][occ]
        print(f"{i}. {occ:25s} - True: {count:5d}, Noisy: {noisy:8.2f}")
    

if __name__ == "__main__":
    task3_solution()