import numpy as np
from scipy import stats

def laplace_mech(v, sensitivity, epsilon):
    """
    Implements the Laplace mechanism for differential privacy.
    
    Args:
        v: The true value of the query result
        sensitivity: The sensitivity of the query
        epsilon: The privacy parameter
        
    Returns:
        The noisy result after applying Laplace noise
    """
    scale = sensitivity / epsilon
    noise = stats.laplace.rvs(loc=0, scale=scale)
    return v + noise