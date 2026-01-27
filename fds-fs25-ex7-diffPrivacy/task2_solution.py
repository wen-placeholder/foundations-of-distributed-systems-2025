import pandas as pd
import numpy as np
from scipy import stats

# ---------------------------------------------------------
# Task 2: Contingency Tables
# ---------------------------------------------------------

def dp_contingency_table(df, col1, col2, epsilon):
    """
    Generates a differentially private contingency table.
    Uses parallel composition since cells are disjoint.
    
    Args:
        df: DataFrame
        col1: First column name
        col2: Second column name
        epsilon: Total privacy budget (applied to EACH cell via parallel composition)
    """
    # True contingency table
    true_table = pd.crosstab(df[col1], df[col2])
    
    # Sensitivity = 1 (each person affects only one cell)
    sensitivity = 1.0
    
    # Parallel composition: use full epsilon for each cell
    # because cells are disjoint (each person in exactly one cell)
    scale = sensitivity / epsilon
    
    # Add Laplace noise to each cell
    # We use size=true_table.shape to generate a matrix of noise matching the table
    noise = stats.laplace.rvs(loc=0, scale=scale, size=true_table.shape)
    
    dp_table = true_table + noise
    
    return dp_table

def solve_task2():
    print("--- Task 2: Contingency Tables ---")
    
    filename = 'adult_with_pii.csv' # Dosya adını kontrol et
    
    try:
        df = pd.read_csv(filename)
        # Sütun isimlerindeki boşlukları temizle (önlem olarak)
        df.columns = df.columns.str.strip()
    except FileNotFoundError:
        print(f"Error: Could not find {filename}")
        return

    # Task Requirements
    # Relationship ve Race sütunları için epsilon=0.3
    col1 = 'Relationship'
    col2 = 'Race'
    epsilon = 0.3
    
    print(f"Generating DP table for {col1} vs {col2} (epsilon={epsilon})...")
    
    # Fonksiyonu çağır
    noisy_table = dp_contingency_table(df, col1, col2, epsilon)
    
    # Sonuçları göster
    print("\nDP Table Results (First 5 rows):")
    print(noisy_table.head())

if __name__ == "__main__":
    solve_task2()